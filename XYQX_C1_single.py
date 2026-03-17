"""
希音千寻-C1 智能声学超材料设计助手
基于LlamaIndex框架构建，具有function calling功能
支持多用户并发访问，每个用户独立会话
"""

import os
import re
import json
import uuid
import time
from typing import Tuple, Optional, Any, List, Dict

# 设置matplotlib后端 - 必须在导入matplotlib之前设置
import matplotlib
matplotlib.use('Agg')  # 使用非交互式后端

from llama_index.core.tools import FunctionTool
from llama_index.core.llms import LLM
from llama_index.llms.openai import OpenAI
from llama_index.core.settings import Settings
from llama_index.llms.dashscope import DashScope
# Use local OpenAI-compatible client (used by design-llm.py)
from openai import OpenAI
from llama_index.embeddings.dashscope import DashScopeEmbedding
from dotenv import load_dotenv
from flask import Flask, render_template, request, jsonify, send_file, session
from flask_cors import CORS
import threading
import webbrowser
import time
import base64
from pathlib import Path

# 导入本地的chu.py模块
from chu import design_TI, load_data

# 加载环境变量
load_dotenv('.env.sample')
# 获取环境变量
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")


class XYQXC1Agent:
    """希音千寻-C1 声学超材料智能设计助手"""
    
    def __init__(self, api_key: str, model: str = "qwen-max", user_id: str = None):
        """
        初始化助手
        
        Args:
            api_key: API密钥
            model: 使用的模型名称
            user_id: 用户唯一标识
        """
        
        self.user_id = user_id or str(uuid.uuid4())
        self.created_time = time.time()
        self.last_activity = time.time()
        
        # 初始化LLM: 默认使用本地 OpenAI-compatible 服务（与 design-llm.py 保持一致）
        # 提供一个轻量 wrapper，保留 `.complete(prompt)` 接口，便于后续替换和调用
        class LocalOpenAIWrapper:
            def __init__(self, api_key, base_url: str = "http://127.0.0.1:8000/v1", model_path: str = None):
                self.client = OpenAI(api_key=api_key, base_url=base_url)
                # model_path 可以是本地模型路径或模型 id
                self.model_path = model_path or r"F:\\WangYinchu\\LLM\\model\\chuchulove\\XYQX-C1-2025"

            def _extract_content(self, result) -> str:
                content = ""
                choice = None
                try:
                    choice = result.choices[0]
                except Exception:
                    choice = None

                if choice is not None:
                    if hasattr(choice, "message"):
                        msg = choice.message
                        if isinstance(msg, dict):
                            content = msg.get("content", "")
                        else:
                            content = getattr(msg, "content", "") or ""
                    else:
                        if isinstance(choice, dict):
                            content = choice.get("text", "") or choice.get("message", {}).get("content", "")
                        else:
                            content = getattr(choice, "text", None) or str(choice)

                if content is None:
                    content = ""
                return content

            def complete(self, prompt: str):
                messages = [{"role": "user", "content": prompt}]
                try:
                    result = self.client.chat.completions.create(
                        messages=messages,
                        model=self.model_path
                    )
                except Exception as e:
                    # Return a stringified error so caller can handle it gracefully
                    return f"[LLM Error] {e}"

                return self._extract_content(result)

        # instantiate wrapper
        self.llm = LocalOpenAIWrapper(api_key=api_key)
        
        # 加载数据
        try:
            self.csv_points, self.str_data_dict, self.float_data_dict = load_data()
            print(f"✅ User {self.user_id[:8]} data loaded successfully")
        except Exception as e:
            print(f"❌ User {self.user_id[:8]} data loading failed: {e}")
            raise
        
        # 初始化对话历史
        self.messages = [
            {
                "role": "assistant",
                "content": """You are Xi Yin Qian Xun-C1, an intelligent acoustic metamaterial design assistant developed by the Operations Research and Optimization Team at Huazhong University of Science and Technology.
                You currently specialize in the design of Acoustic Valley Hall Insulators (AVHI). Your training data comes from a reinforcement learning-based Acoustic Valley Hall Insulator design method.

                You need to engage in continuous multi-round conversations with users until they clearly express their intention to end the conversation.

Conversation Rules:
1. Please respond to questions in a gentle and polite manner;
2. Maintain conversation continuity in each round, remembering previous conversation content;
3. Analyze user intent, especially regarding the design of Acoustic Valley Hall Insulators (AVHI)
4. When users want to design AVHI and provide topological bandgap coordinates (x,y), you need to use the special tag [DESIGN:x,y] at the beginning of your reply
5. If users want to design but haven't provided topological bandgap parameters, prompt them to provide the correct bandgap range
6. If users haven't requested AVHI design, answer their questions normally.
7. If users express their intention to end the conversation (such as saying "goodbye", "bye", "end conversation", "I'm leaving", etc.), use the special tag [EXIT] at the beginning of your reply

Special Tag Instructions:
- [DESIGN:x,y] - Use when users request AVHI design and provide specific topological bandgap parameters x and y
- [EXIT] - Use when users express farewell or end conversation intent
- No tag - Normal conversation reply

Examples:
User: "Please design AVHI with topological bandgap of (500,3000)"
Assistant: "[DESIGN:500,3000] Got it, I'll design this Acoustic Valley Hall Insulator with topological bandgap of (500,3000)."

User: "Goodbye"
Assistant: "[EXIT] Goodbye, wish you all the best!"

Please respond in the same language as the user's query (Chinese if they ask in Chinese, English if they ask in English).

Start the conversation now."""
            }
        ]
        
        print(f"🎉 Xi Yin Qian Xun-C1 intelligent assistant for user {self.user_id[:8]} initialized successfully!")
    
    def update_activity(self):
        """Update last activity time"""
        self.last_activity = time.time()
    
    def _extract_bandgap_params(self, user_input: str) -> Optional[Tuple[float, float]]:
        """
        Extract topological bandgap parameters from user input

        Args:
            user_input: User input text

        Returns:
            (x, y) tuple or None
        """
        # Match various possible formats: (x,y), (x,y), x,y etc.
        patterns = [
            r'[（(]\s*(\d+(?:\.\d+)?)\s*[,，]\s*(\d+(?:\.\d+)?)\s*[）)]',  # (x,y) or (x,y)
            r'(\d+(?:\.\d+)?)\s*[,，]\s*(\d+(?:\.\d+)?)',  # x,y or x,y
            r'(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)',  # x y (space separated)
        ]
        
        for pattern in patterns:
            match = re.search(pattern, user_input)
            if match:
                try:
                    x = float(match.group(1))
                    y = float(match.group(2))
                    return (x, y)
                except ValueError:
                    continue
        
        return None
    
    def _design_avhi(self, x: float, y: float) -> str:
        """
        Tool function for designing Acoustic Valley Hall Insulator

        Args:
            x: x parameter of topological bandgap
            y: y parameter of topological bandgap

        Returns:
            Description of design results
        """
        try:
            print(f"🔬 User {self.user_id[:8]} starts designing Acoustic Valley Hall Insulator, topological bandgap parameters: ({x}, {y})")

            # Use global lock to ensure thread safety for Comsol operations
            with comsol_lock:
                # 调用design_TI函数，传递用户ID
                coor_scatter, num_lattice, num_band, user_save_dir = design_TI(
                    x, y, self.csv_points, self.str_data_dict, self.float_data_dict, self.user_id
                )
            
            if coor_scatter is not None:
                result = (
                    f"Design completed. Target topological bandgap ({x}, {y}), actual designed topological bandgap {num_band}, lattice constant {num_lattice:.6f}(mm)."
                    f"3D structure diagram, bandgap figure, first bandgap eigenmode diagram, second bandgap eigenmode diagram (.png), 3D structure file (.stl), data file (.txt), Comsol simulation file (.mph) are shown below, please check or download?"
                )
                return result
            else:
                return f"Cannot find structure corresponding to topological bandgap ({x}, {y}). Try different parameters."

        except Exception as e:
            return f"Design error: {str(e)}"
    
    def _parse_llm_response(self, response_text: str) -> Tuple[str, Optional[str], Optional[Tuple[float, float]]]:
        """
        Parse LLM response, extract special tags and parameters

        Args:
            response_text: Raw LLM response

        Returns:
            (cleaned response text, action type, design parameters)
        """
        # Check for exit tag
        if response_text.startswith('[EXIT]'):
            clean_text = response_text[6:].strip()
            return clean_text, 'exit', None
        
        # Check for design tag
        design_match = re.match(r'\[DESIGN:(\d+(?:\.\d+)?),(\d+(?:\.\d+)?)\](.*)', response_text, re.DOTALL)
        if design_match:
            x = float(design_match.group(1))
            y = float(design_match.group(2))
            clean_text = design_match.group(3).strip()
            return clean_text, 'design', (x, y)

        # No special tags, normal reply
        return response_text.strip(), 'normal', None
    
    def chat(self, message: str) -> Tuple[str, bool, bool]:
        """
        Chat with user

        Args:
            message: User message

        Returns:
            (assistant reply, whether to end conversation, whether design function was called)
        """
        try:
            # Update activity time
            self.update_activity()

            # Add user message to history
            self.messages.append({
                "role": "user",
                "content": message
            })

            # Call LLM to get response
            llm_response = self.llm.complete(
                prompt=self._format_messages_for_llm()
            )
            
            response_text = str(llm_response).strip()
            
            # Parse special tags in LLM response
            clean_response, action_type, design_params = self._parse_llm_response(response_text)

            # Execute different operations based on action type
            if action_type == 'exit':
                # User wants to exit
                final_response = clean_response if clean_response else "Goodbye, wish you all the best!"
                self.messages.append({
                    "role": "assistant",
                    "content": final_response
                })
                return final_response, True, False

            elif action_type == 'design' and design_params:
                # User wants to design AVHI
                x, y = design_params
                design_result = self._design_avhi(x, y)

                # Combine LLM response and design result
                if clean_response:
                    final_response = f"{clean_response}\n\n{design_result}"
                else:
                    final_response = design_result

                self.messages.append({
                    "role": "assistant",
                    "content": final_response
                })

                return final_response, False, True  # Return True indicating design function was called

            else:
                # Normal conversation
                final_response = clean_response
                self.messages.append({
                    "role": "assistant",
                    "content": final_response
                })

                return final_response, False, False
            
        except Exception as e:
            error_msg = f"An error occurred: {str(e)}. Is there anything else I can help you with?"
            self.messages.append({
                "role": "assistant",
                "content": error_msg
            })
            return error_msg, False, False
    
    def _format_messages_for_llm(self) -> str:
        """
        Format messages into prompt that LLM can understand

        Returns:
            Formatted prompt string
        """
        formatted_prompt = ""
        for msg in self.messages:
            role = msg["role"]
            content = msg["content"]
            if role == "user":
                formatted_prompt += f"User: {content}\n"
            elif role == "assistant":
                formatted_prompt += f"Assistant: {content}\n"

        # Add current reply prompt
        formatted_prompt += "Assistant: "

        return formatted_prompt
    
    def reset_conversation(self):
        """Reset conversation history"""
        self.messages = [
            {
                "role": "assistant",
                "content": """You are Xi Yin Qian Xun-C1, an intelligent acoustic metamaterial design assistant developed by the Operations Research and Optimization Team at Huazhong University of Science and Technology.
                You currently specialize in the design of Acoustic Valley Hall Insulators (AVHI). Your training data comes from a reinforcement learning-based Acoustic Valley Hall Insulator design method.

                You need to engage in continuous multi-round conversations with users until they clearly express their intention to end the conversation.

Conversation Rules:
1. Please respond to questions in a gentle and polite manner;
2. Maintain conversation continuity in each round, remembering previous conversation content;
3. Analyze user intent, especially regarding the design of Acoustic Valley Hall Insulators (AVHI)
4. When users want to design AVHI and provide topological bandgap coordinates (x,y), you need to use the special tag [DESIGN:x,y] at the beginning of your reply
5. If users want to design but haven't provided topological bandgap parameters, prompt them to provide the correct bandgap range
6. If users haven't requested AVHI design, answer their questions normally.
7. If users express their intention to end the conversation (such as saying "goodbye", "bye", "end conversation", "I'm leaving", etc.), use the special tag [EXIT] at the beginning of your reply

Special Tag Instructions:
- [DESIGN:x,y] - Use when users request AVHI design and provide specific topological bandgap parameters x and y
- [EXIT] - Use when users express farewell or end conversation intent
- No tag - Normal conversation reply

Examples:
User: "Please design AVHI with topological bandgap of (500,3000)"
Assistant: "[DESIGN:500,3000] Got it, I'll design this Acoustic Valley Hall Insulator with topological bandgap of (500,3000)."

User: "Goodbye"
Assistant: "[EXIT] Goodbye, wish you all the best!"

Please respond in the same language as the user's query (Chinese if they ask in Chinese, English if they ask in English).

Start the conversation now."""
            }
        ]
        self.update_activity()
        print(f"🔄 User {self.user_id[:8]} conversation history has been reset")
    
    def get_conversation_history(self) -> List[Dict[str, str]]:
        """Get conversation history"""
        return self.messages.copy()


class UserSessionManager:
    """User Session Manager"""

    def __init__(self, api_key: str, session_timeout: int = 3600):
        """
        Initialize session manager

        Args:
            api_key: API key
            session_timeout: Session timeout time (seconds), default 1 hour
        """
        self.api_key = api_key
        self.session_timeout = session_timeout
        self.user_assistants: Dict[str, XYQXC1Agent] = {}
        self._lock = threading.Lock()
        
        # Start cleanup thread
        self._start_cleanup_thread()

    def get_assistant(self, user_id: str) -> XYQXC1Agent:
        """
        Get or create user's assistant instance

        Args:
            user_id: User unique identifier

        Returns:
            User's corresponding assistant instance
        """
        with self._lock:
            if user_id not in self.user_assistants:
                # Create new assistant instance
                assistant = XYQXC1Agent(api_key=self.api_key, user_id=user_id)
                self.user_assistants[user_id] = assistant
                print(f"📝 Created new assistant instance for user {user_id[:8]}")
            else:
                # Update activity time of existing assistant
                self.user_assistants[user_id].update_activity()
            
            return self.user_assistants[user_id]
    
    def remove_assistant(self, user_id: str):
        """
        Remove user's assistant instance

        Args:
            user_id: User unique identifier
        """
        with self._lock:
            if user_id in self.user_assistants:
                del self.user_assistants[user_id]
                print(f"🗑️ Removed assistant instance for user {user_id[:8]}")
    
    def cleanup_expired_sessions(self):
        """Clean up expired sessions"""
        current_time = time.time()
        expired_users = []
        
        with self._lock:
            for user_id, assistant in self.user_assistants.items():
                if current_time - assistant.last_activity > self.session_timeout:
                    expired_users.append(user_id)
        
        # 清理过期用户
        for user_id in expired_users:
            self.remove_assistant(user_id)
    
    def _start_cleanup_thread(self):
        """Start cleanup thread"""
        def cleanup_worker():
            while True:
                time.sleep(300)  # Check every 5 minutes
                self.cleanup_expired_sessions()

        cleanup_thread = threading.Thread(target=cleanup_worker)
        cleanup_thread.daemon = True
        cleanup_thread.start()
        print("🧹 Session cleanup thread started")
    
    def get_stats(self) -> Dict[str, Any]:
        """Get session statistics"""
        with self._lock:
            return {
                'active_users': len(self.user_assistants),
                'users': [
                    {
                        'user_id': user_id[:8] + '...',
                        'created_time': assistant.created_time,
                        'last_activity': assistant.last_activity,
                        'message_count': len(assistant.messages) - 1  # Subtract initial message
                    }
                    for user_id, assistant in self.user_assistants.items()
                ]
            }


# Global variables
session_manager = None
# Global lock for Comsol operations to ensure thread safety
comsol_lock = threading.Lock()

app = Flask(__name__, static_folder='web_ui', template_folder='web_ui')
CORS(app)

# Configure Flask session
app.config['SECRET_KEY'] = os.urandom(24)  # Randomly generate key
app.config['PERMANENT_SESSION_LIFETIME'] = 3600  # 1 hour


def get_or_create_user_id():
    """Get or create user ID"""
    if 'user_id' not in session:
        session['user_id'] = str(uuid.uuid4())
        session.permanent = True
        print(f"🆕 Created new user session: {session['user_id'][:8]}")
    return session['user_id']


def check_generated_files(user_id=None):
    """Check generated files and return file information

    Parameters:
    user_id (str): User ID, if provided, read from user folder, otherwise from default folder
    """
    files_info = {
        'images': {
            'structure': None,      # TI_structure.png
            'band_gap': None,       # band-gap-figure.png
            'first_band': None,     # first-band-image.png
            'second_band': None     # second-band-image.png
        },
        'text': None,
        'stl': None,
        'comsol': None,             # comsol_result.mph
        'has_files': False
    }

    # 根据用户ID确定保存目录
    if user_id:
        user_dir = Path(f'Save_TI/user_{user_id}')
        if user_dir.exists():
            # 找到最新的设计文件夹
            design_dirs = [d for d in user_dir.iterdir() if d.is_dir() and d.name.startswith('design_')]
            if design_dirs:
                # 按修改时间排序，获取最新的
                design_dirs.sort(key=lambda x: x.stat().st_mtime, reverse=True)
                save_ti_dir = design_dirs[0]  # 使用最新的设计文件夹
                print(f"为用户 {user_id} 找到最新设计文件夹: {save_ti_dir}")
            else:
                save_ti_dir = Path('Save_TI')  # 如果没有设计文件夹，使用默认目录
        else:
            save_ti_dir = Path('Save_TI')  # 如果用户目录不存在，使用默认目录
    else:
        save_ti_dir = Path('Save_TI')  # 没有user_id，使用默认目录

    # 定义四个图像文件的配置
    image_configs = {
        'structure': 'TI_structure.png',
        'band_gap': 'band-gap-figure.png',
        'first_band': 'first-band-image.png',
        'second_band': 'second-band-image.png'
    }

    # 检查四个图片文件
    for key, filename in image_configs.items():
        png_file = save_ti_dir / filename
        if png_file.exists():
            try:
                # 将图片转换为base64
                with open(png_file, 'rb') as f:
                    image_data = base64.b64encode(f.read()).decode('utf-8')
                files_info['images'][key] = {
                    'data': f"data:image/png;base64,{image_data}",
                    'filename': filename,
                    'size': png_file.stat().st_size
                }
                files_info['has_files'] = True
            except Exception as e:
                print(f"读取图片文件 {filename} 失败: {e}")

    # 检查文本文件
    txt_file = save_ti_dir / 'TI_data.txt'
    if txt_file.exists():
        try:
            # 读取文本内容
            with open(txt_file, 'r', encoding='utf-8') as f:
                text_content = f.read()
            files_info['text'] = {
                'content': text_content,
                'filename': 'TI_data.txt',
                'size': txt_file.stat().st_size
            }
            files_info['has_files'] = True
        except Exception as e:
            print(f"读取文本文件失败: {e}")

    # 检查STL文件
    stl_file = save_ti_dir / 'TI_structure.stl'
    if stl_file.exists():
        try:
            files_info['stl'] = {
                'filename': 'TI_structure.stl',
                'size': stl_file.stat().st_size
            }
            files_info['has_files'] = True
        except Exception as e:
            print(f"读取STL文件失败: {e}")

    # 检查Comsol文件
    comsol_file = save_ti_dir / 'comsol_result.mph'
    if comsol_file.exists():
        try:
            files_info['comsol'] = {
                'filename': 'comsol_result.mph',
                'size': comsol_file.stat().st_size
            }
            files_info['has_files'] = True
        except Exception as e:
            print(f"读取Comsol文件失败: {e}")

    return files_info

@app.route('/')
def index():
    """Home page route"""
    user_id = get_or_create_user_id()
    return render_template('index.html')

@app.route('/api/design', methods=['POST'])
def design_avhi():
    """API interface for processing design requests"""
    try:
        # Get user ID and corresponding assistant instance
        user_id = get_or_create_user_id()
        assistant = session_manager.get_assistant(user_id)

        # Get request data
        data = request.get_json()
        bandgap1 = data.get('bandgap1')
        bandgap2 = data.get('bandgap2')

        # Validate input parameters
        if not bandgap1 or not bandgap2:
            return jsonify({
                'success': False,
                'message': 'Please enter complete bandgap parameters!'
            })

        try:
            bandgap1 = float(bandgap1)
            bandgap2 = float(bandgap2)
        except ValueError:
            return jsonify({
                'success': False,
                'message': 'Please enter valid numbers!'
            })

        # Check that first bandgap must be less than second bandgap
        if bandgap1 >= bandgap2:
            return jsonify({
                'success': False,
                'message': 'Hello, the value of the first bandgap must be less than the second bandgap'
            })
        
        # Construct message to send to LLM
        user_message = f"Please help me design an Acoustic Valley Hall Insulator with topological bandgap range: ({bandgap1},{bandgap2})"

        # Call assistant for conversation and measure elapsed time
        start_time = time.time()
        response, should_exit, design_called = assistant.chat(user_message)
        end_time = time.time()
        elapsed = end_time - start_time

        # Check if files were generated
        files_data = check_generated_files(user_id)
        
        # Include response time (seconds and milliseconds)
        return jsonify({
            'success': True,
            'message': f"{response}\n\n(Response time: {elapsed:.3f} s)",
            'response_time': elapsed,
            'response_time_ms': int(elapsed * 1000),
            'user_input': f"Design parameters: First bandgap {bandgap1} Hz, Second bandgap {bandgap2} Hz",
            'files': files_data
        })
        
    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error occurred while processing request: {str(e)}'
        })

@app.route('/api/chat', methods=['POST'])
def chat():
    """General chat interface"""
    try:
        # Get user ID and corresponding assistant instance
        user_id = get_or_create_user_id()
        assistant = session_manager.get_assistant(user_id)

        data = request.get_json()
        message = data.get('message', '').strip()

        if not message:
            return jsonify({
                'success': False,
                'message': 'Please enter message content'
            })
        
        # Time the assistant response for display
        start_time = time.time()
        response, should_exit, design_called = assistant.chat(message)
        end_time = time.time()
        elapsed = end_time - start_time

        # Only check files when design function was called
        files_data = None
        if design_called:
            files_data = check_generated_files(user_id)
        
        return jsonify({
            'success': True,
            'message': f"{response}\n\n(Response time: {elapsed:.3f} s)",
            'response_time': elapsed,
            'response_time_ms': int(elapsed * 1000),
            'should_exit': should_exit,
            'files': files_data
        })
        
    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error occurred while processing chat request: {str(e)}'
        })

@app.route('/api/reset', methods=['POST'])
def reset_conversation():
    """API interface for resetting conversation"""
    try:
        # Get user ID and corresponding assistant instance
        user_id = get_or_create_user_id()
        assistant = session_manager.get_assistant(user_id)

        # Call assistant's reset conversation method
        assistant.reset_conversation()

        return jsonify({
            'success': True,
            'message': 'Conversation has been successfully reset'
        })

    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error occurred while resetting conversation: {str(e)}'
        })

@app.route('/api/stats', methods=['GET'])
def get_stats():
    """API interface for getting system statistics"""
    try:
        stats = session_manager.get_stats()
        return jsonify({
            'success': True,
            'stats': stats
        })
    except Exception as e:
        return jsonify({
            'success': False,
            'message': f'Error occurred while getting statistics: {str(e)}'
        })

@app.route('/api/download/<filename>')
def download_file(filename):
    """File download interface"""
    try:
        # Get user ID
        user_id = get_or_create_user_id()

        # Determine file path based on user ID
        if user_id:
            user_dir = Path(f'Save_TI/user_{user_id}')
            if user_dir.exists():
                # Find the latest design folder
                design_dirs = [d for d in user_dir.iterdir() if d.is_dir() and d.name.startswith('design_')]
                if design_dirs:
                    # Sort by modification time, get the latest
                    design_dirs.sort(key=lambda x: x.stat().st_mtime, reverse=True)
                    save_ti_dir = design_dirs[0]  # Use the latest design folder
                else:
                    save_ti_dir = Path('Save_TI')  # If no design folder, use default directory
            else:
                save_ti_dir = Path('Save_TI')  # If user directory doesn't exist, use default directory
        else:
            save_ti_dir = Path('Save_TI')  # No user_id, use default directory

        file_path = save_ti_dir / filename
        
        if not file_path.exists():
            return jsonify({'error': 'File does not exist'}), 404

        # Security check, only allow download of specified files
        allowed_files = [
            'TI_structure.png', 'band-gap-figure.png', 'first-band-image.png', 'second-band-image.png',
            'TI_data.txt', 'TI_structure.pdf', 'TI_structure.obj', 'TI_structure.stl', 'comsol_result.mph'
        ]
        if filename not in allowed_files:
            return jsonify({'error': 'Download of this file is not allowed'}), 403
        
        return send_file(
            file_path,
            as_attachment=True,
            download_name=filename
        )
        
    except Exception as e:
        return jsonify({'error': f'Error occurred while downloading file: {str(e)}'}), 500

def open_browser():
    """Delay opening browser"""
    time.sleep(1.5)
    webbrowser.open('http://localhost:5000')

def run_flask_app():
    """Run Flask application"""
    # Use single-threaded mode to avoid Comsol multi-threading access conflicts
    app.run(debug=False, host='0.0.0.0', port=5000, use_reloader=False, threaded=False)

def main():
    """Main function - Start Web application"""
    global session_manager

    # API Key needs to be set here
    API_KEY = OPENAI_API_KEY  # Please replace with your actual API key

    if API_KEY is None or API_KEY == "your-openai-api-key-here":
        print("⚠️  Please set your API key in the .env.sample file first")
        return

    try:
        print("🚀 Initializing Xi Yin Qian Xun-C1 multi-user intelligent assistant...")

        # Initialize session manager
        session_manager = UserSessionManager(api_key=API_KEY)

        print("🎉 Xi Yin Qian Xun-C1 multi-user assistant initialized successfully!")
        print("🌐 Starting web server...")
        print("📡 Server address: http://localhost:5000")
        print("🔗 Browser will open automatically...")
        print("👥 System now supports multi-user concurrent access, each user has independent session")
        
        # Open browser with delay in new thread
        browser_thread = threading.Thread(target=open_browser)
        browser_thread.daemon = True
        browser_thread.start()

        # Start Flask application
        run_flask_app()

    except Exception as e:
        print(f"❌ Error occurred while starting assistant: {e}")

def console_mode():
    """Console mode - Original command line interaction"""
    # API Key needs to be set here
    API_KEY = OPENAI_API_KEY  # Please replace with your actual API key

    if API_KEY is None or API_KEY == "your-openai-api-key-here":
        print("⚠️  Please set your API key in the .env.sample file first")
        return

    try:
        # Initialize assistant (console mode uses single instance)
        assistant = XYQXC1Agent(api_key=API_KEY)

        print("\n🎉 Xi Yin Qian Xun-C1 assistant has started!")
        print("💬 Please enter your questions or requirements (say 'goodbye' to exit):\n")
        
        while True:
            user_input = input("👤 User: ").strip()

            if not user_input:
                continue

            if user_input.lower() in ['reset', 'reset']:
                assistant.reset_conversation()
                continue

            print("🤖 Xi Yin Qian Xun-C1: ", end="")
            response, should_exit, design_called = assistant.chat(user_input)
            print(response)
            
            if should_exit:
                break
            
            print("\n" + "="*60 + "\n")
    
    except Exception as e:
        print(f"❌ Error occurred while starting assistant: {e}")


if __name__ == "__main__":
    import sys

    # Check command line arguments to determine running mode
    if len(sys.argv) > 1 and sys.argv[1] == 'console':
        console_mode()  # Console mode
    else:
        main()  # Web mode (default)
