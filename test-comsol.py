import mph
import numpy as np
import os

# 确保Save_TI文件夹存在
if not os.path.exists('Save_TI'):
    os.makedirs('Save_TI')

client = mph.start()
model = client.create()
model = client.load('com.mph')
#查看当前加载的模型
client.names()
print(f"已加载模型: {model}")
print(client.names())
#client.clear()
for (name, value) in model.parameters().items():
    description = model.description(name)
    print(f'{description:20} {name} = {value}')
model.studies()
print(model.studies())

#修改参数
#model.parameter('a',"59[mm]")

model.datasets()
print(model.datasets())

model.exports()
print(model.exports())

model.evaluate( 'freq', "K",'研究 1//参数化解 1','first',1)

model.geometries()

print(model.geometries())

print(mph.tree(model))

print(model.properties("Geometry"))


# 运行研究1 "研究 1"
print("正在运行研究 研究 1...")
#model.solve('研究 1')
print("研究 研究 1 运行完成")

# 专门针对您的模型导出声压图像
print("正在导出声压图像...")


# 使用Java API导出
try:
    # 获取结果节点
    results = model.java.result()
    
    # 创建图像导出
    export = results.export().create("img1", "Image")
    export.set("plotgroup", "pg1")  # 指定绘图组名称
    export.set("filename", "Save_TI/acpr_pressure_java.png")
    export.set("width", "1600")
    export.set("height", "1200")
    export.set("resolution", "300")
    export.run()
    
    print("声压图像已保存为: Save_TI/acpr_pressure_java.png")
    
except Exception as e:
    print(f"方法2失败: {e}")

