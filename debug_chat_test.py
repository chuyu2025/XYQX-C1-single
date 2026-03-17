import os, traceback
from XYQX_C1_Agent_yasaxi import UserSessionManager

API_KEY = os.getenv('OPENAI_API_KEY') or '0'
try:
    sm = UserSessionManager(api_key=API_KEY)
    user_id = 'test_user_123'
    assistant = sm.get_assistant(user_id)
    resp, should_exit, design_called = assistant.chat('Hello, how are you?')
    print('RESP:', resp)
    print('SHOULD_EXIT:', should_exit, 'DESIGN_CALLED:', design_called)
except Exception as e:
    traceback.print_exc()
    print('ERROR:', e)
