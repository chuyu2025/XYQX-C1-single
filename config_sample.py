# 希音千寻-C1 智能助手配置文件示例
# 请复制此文件为 config.py 并填入您的实际配置

# DashScope API密钥 (阿里云通义千问)
# 获取地址: https://dashscope.console.aliyun.com/
API_KEY = "your_dashscope_api_key_here"

# 模型配置
MODEL_NAME = "qwen-max"
BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1"
TEMPERATURE = 0.7

# 注意：
# 1. 请将 your_dashscope_api_key_here 替换为您的实际API密钥
# 2. 如果使用OpenAI API，请相应修改模型配置
# 3. 请不要将包含真实API密钥的 config.py 文件提交到版本控制系统 