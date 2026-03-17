"""
希音千寻-C1 助手使用示例
演示如何使用基于LlamaIndex的声学超材料设计助手
现在使用大模型进行意图检测，而非关键词匹配
"""

from XYQX_C1_Agent import XYQXC1Agent, OPENAI_API_KEY


def demo_conversation():
    """演示对话示例"""
    
    # 请在这里设置您的API Key
    API_KEY = OPENAI_API_KEY  # 从环境变量获取
    
    if API_KEY is None or API_KEY == "your-openai-api-key-here":
        print("⚠️  请先在.env.sample文件中设置您的API密钥")
        print("请在.env.sample文件中设置OPENAI_API_KEY变量")
        return
    
    try:
        # 初始化助手
        print("正在初始化希音千寻-C1助手...")
        assistant = XYQXC1Agent(api_key=API_KEY)
        
        # 预定义的测试对话 - 测试大模型的意图理解能力
        test_conversations = [
            # 基本问候
            "你好，你是谁？",
            
            # 直接的设计请求（不同表达方式）
            "请你帮我设计一个拓扑带隙为（500,3000）的声谷霍尔绝缘体",
            "能否为我制作拓扑带隙(842,400)的AVHI结构？",
            "我需要一个声谷霍尔绝缘体，拓扑带隙范围是706到4000",
            
            # 间接的设计表达
            "我想要一个能在500到3000频率范围工作的声学拓扑绝缘体",
            "有没有办法做出拓扑带隙在1000-2000之间的AVHI？",
            
            # 缺少参数的设计请求
            "我想设计一个AVHI",
            "能帮我做个声谷霍尔绝缘体吗？",
            "制作一个声学超材料结构",
            
            # 一般性问题
            "什么是拓扑绝缘体？",
            "声学超材料有什么应用？",
            "2+2等于多少？",
            
            # 不同的告别方式
            "谢谢你的帮助",
            "我要离开了",
            "对话就到这里吧",
            "再见"
        ]
        
        print("\n🎯 开始演示对话（测试大模型意图检测）：")
        print("="*80)
        
        for i, message in enumerate(test_conversations, 1):
            print(f"\n【测试 {i}】")
            print(f"👤 用户: {message}")
            print("🤖 希音千寻-C1: ", end="")
            
            response, should_exit = assistant.chat(message)
            print(response)
            
            if should_exit:
                print("\n对话结束。")
                break
                
            print("-" * 60)
        
        print("\n✅ 演示完成！")
        
    except Exception as e:
        print(f"❌ 演示过程中出现错误: {e}")


def interactive_mode():
    """交互模式"""
    
    API_KEY = OPENAI_API_KEY  # 从环境变量获取
    
    if API_KEY is None or API_KEY == "your-openai-api-key-here":
        print("⚠️  请先在.env.sample文件中设置您的API密钥")
        return
    
    try:
        # 初始化助手
        assistant = XYQXC1Agent(api_key=API_KEY)
        
        print("\n🎉 希音千寻-C1 助手已启动！")
        print("💬 现在使用大模型进行意图检测，请自然地表达您的需求")
        print("💬 说'再见'退出，'demo'运行演示，'reset'重置对话：\n")
        
        while True:
            user_input = input("👤 用户: ").strip()
            
            if not user_input:
                continue
            
            if user_input.lower() in ['demo', '演示']:
                demo_conversation()
                continue
            
            if user_input.lower() in ['reset', '重置']:
                assistant.reset_conversation()
                continue
            
            print("🤖 希音千寻-C1: ", end="")
            response, should_exit = assistant.chat(user_input)
            print(response)
            
            if should_exit:
                print("\n对话结束，感谢使用！")
                break
            
            print("\n" + "="*60 + "\n")
    
    except Exception as e:
        print(f"❌ 启动助手时出现错误: {e}")


def test_intent_detection():
    """测试意图检测功能"""
    
    print("🧪 测试大模型意图检测功能...")
    
    API_KEY = OPENAI_API_KEY
    if API_KEY is None:
        print("⚠️  请先设置API密钥")
        return
    
    try:
        assistant = XYQXC1Agent(api_key=API_KEY)
        
        # 测试不同类型的意图表达
        test_cases = [
            {
                "category": "明确设计请求",
                "inputs": [
                    "请设计拓扑带隙为（500,3000）的AVHI",
                    "制作一个频率范围在800到2500的声谷霍尔绝缘体",
                    "我要一个拓扑带隙1000,4000的TI结构"
                ]
            },
            {
                "category": "模糊设计请求",
                "inputs": [
                    "我想要设计一个声学超材料",
                    "能帮我做个AVHI吗？",
                    "制作声谷霍尔绝缘体"
                ]
            },
            {
                "category": "一般问题",
                "inputs": [
                    "什么是声学超材料？",
                    "拓扑绝缘体的原理是什么？",
                    "今天天气怎么样？"
                ]
            },
            {
                "category": "告别意图",
                "inputs": [
                    "再见",
                    "我要走了",
                    "对话结束",
                    "谢谢，不用了"
                ]
            }
        ]
        
        print("\n测试结果：")
        print("="*60)
        
        for category_info in test_cases:
            category = category_info["category"]
            inputs = category_info["inputs"]
            
            print(f"\n【{category}】")
            print("-" * 30)
            
            for i, test_input in enumerate(inputs, 1):
                print(f"\n{i}. 测试输入: {test_input}")
                
                # 重置对话以确保每次测试的独立性
                assistant.reset_conversation()
                
                response, should_exit = assistant.chat(test_input)
                print(f"   助手回复: {response[:100]}{'...' if len(response) > 100 else ''}")
                print(f"   是否退出: {should_exit}")
                
                if should_exit:
                    print("   ✅ 成功检测到退出意图")
                elif "[DESIGN:" in str(response) or "设计完成了" in response:
                    print("   ✅ 成功检测到设计意图并执行")
                elif category == "模糊设计请求" and any(word in response for word in ["参数", "频率", "带隙"]):
                    print("   ✅ 成功检测到设计意图但提示需要参数")
                elif category == "一般问题":
                    print("   ✅ 正常回答一般问题")
        
        print("\n✅ 意图检测测试完成！")
        
    except Exception as e:
        print(f"❌ 测试过程中出现错误: {e}")


def test_parameter_extraction():
    """测试参数提取功能"""
    
    print("🧪 测试参数提取功能...")
    
    # 创建一个临时助手实例用于测试
    class TempAgent:
        def _extract_bandgap_params(self, user_input):
            import re
            patterns = [
                r'[（(]\s*(\d+(?:\.\d+)?)\s*[,，]\s*(\d+(?:\.\d+)?)\s*[）)]',
                r'(\d+(?:\.\d+)?)\s*[,，]\s*(\d+(?:\.\d+)?)',
                r'(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)',
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
    
    temp_agent = TempAgent()
    
    test_cases = [
        "请你帮我设计一个拓扑带隙为（500,3000）的声谷霍尔绝缘体",
        "设计拓扑带隙为（842,400）的AVHI",
        "设计一个声谷霍尔绝缘体，拓扑带隙为（706,4000）",
        "拓扑带隙为 600,2500",
        "拓扑带隙(1000 5000)",
        "参数是 500.5,3000.7",
        "设计一个AVHI没有参数",
        "拓扑带隙为abc,def"
    ]
    
    print("\n测试结果：")
    for i, test_case in enumerate(test_cases, 1):
        result = temp_agent._extract_bandgap_params(test_case)
        print(f"{i}. 输入: {test_case}")
        print(f"   提取结果: {result}")
        print()


def show_conversation_history():
    """显示对话历史示例"""
    
    API_KEY = OPENAI_API_KEY
    
    if API_KEY is None:
        print("⚠️  请先设置API密钥")
        return
    
    try:
        assistant = XYQXC1Agent(api_key=API_KEY)
        
        # 进行几轮对话
        messages = [
            "你好",
            "我需要设计一个拓扑带隙为（500,3000）的AVHI",
            "谢谢你的帮助"
        ]
        
        print("📝 对话历史演示：")
        print("="*50)
        
        for msg in messages:
            response, should_exit = assistant.chat(msg)
            print(f"用户: {msg}")
            print(f"助手: {response}")
            print("-" * 30)
            
            if should_exit:
                break
        
        # 显示完整对话历史
        print("\n📚 完整对话历史：")
        history = assistant.get_conversation_history()
        for i, msg in enumerate(history, 1):
            role = msg['role']
            content = msg['content']
            print(f"{i}. [{role}]: {content[:100]}{'...' if len(content) > 100 else ''}")
        
    except Exception as e:
        print(f"❌ 显示对话历史时出现错误: {e}")


if __name__ == "__main__":
    print("希音千寻-C1 助手使用示例")
    print("="*50)
    print("现在使用大模型进行智能意图检测！")
    print("="*50)
    print("1. demo - 运行预设对话演示")
    print("2. interactive - 进入交互模式")
    print("3. intent - 测试意图检测功能")
    print("4. param - 测试参数提取功能")
    print("5. history - 显示对话历史示例")
    print("="*50)
    
    choice = input("请选择模式 (1/2/3/4/5): ").strip()
    
    if choice == "1" or choice.lower() == "demo":
        demo_conversation()
    elif choice == "2" or choice.lower() == "interactive":
        interactive_mode()
    elif choice == "3" or choice.lower() == "intent":
        test_intent_detection()
    elif choice == "4" or choice.lower() == "param":
        test_parameter_extraction()
    elif choice == "5" or choice.lower() == "history":
        show_conversation_history()
    else:
        print("无效选择，启动交互模式...")
        interactive_mode() 