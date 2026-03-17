
import mph

# 1. 启动Comsol客户端并加载模型
print("正在启动Comsol客户端...")
client = mph.start()

print("正在加载模型 com.mph...")
model = client.load('com.mph')

print("模型加载成功！\n")

# 2. 查看模型树结构（可选，帮助理解模型结构）
print("=" * 60)
print("模型完整树结构：")
print("=" * 60)
mph.tree(model)
print("\n")

# 3. 访问几何中的scatterer节点
print("=" * 60)
print("查看scatterer的详细信息：")
print("=" * 60)

# 方法1：使用Python风格的路径访问
try:
    # 获取几何节点
    geometries = model / 'geometries'
    geometry = geometries / 'Geometry'
    scatterer = geometry / 'scatterer'
    
    print("\n【方法1：使用Node对象访问】")
    print(f"Scatterer节点路径: {scatterer}")
    print(f"Scatterer类型: {scatterer.type()}")
    
    # 获取scatterer的所有属性
    print("\n--- Scatterer的属性 ---")
    # 尝试获取常见的几何属性
    common_properties = [
        'selresult', 'selshow', 'contributeto', 
        'pos', 'base', 'axis', 'rot', 'scale',
        'size', 'layer', 'workplane'
    ]
    scatterer.properties()
    value = scatterer.property("table")

    print(value)

    new_table_data = [
    ["0", "0"],      # 第一行：x, y, z 坐标
    ["10", "00"],      # 第二行
    ["0", "10"],      # 第三行
    ]
   
    scatterer.property("table", new_table_data)
    value = scatterer.property("table")

    #将晶格常数重新设置
    model.parameters()
    model.parameter('a', '100[mm]')
    model.parameter('a')
    # 构建几何体
    
    print(value)
    model.build(geometry)

    model.exports()
    model.export('structure', 'static field1.png')


except Exception as e:
    print(f"方法1出错: {e}")



print("\n" + "=" * 60)
print("数据查看完成！")
print("=" * 60)
