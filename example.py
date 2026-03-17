import os
from dotenv import load_dotenv
# 加载环境变量
load_dotenv('.env.sample')
# 获取环境变量
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

# api_call_example.py (最终修正版)
from openai import OpenAI

# 1. 修正 base_url
client = OpenAI(api_key=OPENAI_API_KEY , base_url="https://llmxapi.com/v1")

messages = [{"role": "user", "content": "你好， 你的模型型号具体是哪个"}]

result = client.chat.completions.create(
                        messages=messages,
                        model="gpt-4.1"
                    )




print(result.choices[0].message)