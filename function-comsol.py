# 设置matplotlib后端 - 必须在导入matplotlib之前设置
import mph
from matplotlib.patches import Polygon
import matplotlib.pyplot as plt
import json
import numpy as np
import pandas as pd
import os
import matplotlib
# STL文件生成库
try:
    from stl import mesh
except ImportError:
    print("警告: 未安装numpy-stl库，请运行 'pip install numpy-stl' 来启用STL文件生成功能")
    mesh = None
matplotlib.use('Agg')  # 使用非交互式后端，支持多线程


def comsol_solve(coor_scatter, num_lattice):
    """
    将结构数据导入到comsol中并返回仿真结果

    参数:
    coor_scatter (numpy.ndarray): 结构数据
    num_lattice (int): 晶格常数
    """
    # 1. 启动Comsol客户端并加载模型
    print("正在启动Comsol客户端...")
    client = mph.start()
    print("正在加载模型 com.mph...")
    model = client.load('com.mph')
    print("模型加载成功！\n")

    # 第一种改变几何节点参数的方法
    # 获取几何节点
    geometries = model / 'geometries'
    geometry = geometries / 'Geometry'
    scatterer = geometry / 'scatterer'
    # value = scatterer.property("table")
    # print(value)
    # 将结构数据导入到comsol中
    model.parameter('a', f"{num_lattice}[mm]")
    coor_list = coor_scatter.astype(str).tolist()
    scatterer.property("table", coor_list)
    value = scatterer.property("table")
    model.build(geometry)

    # #尝试第二种改变几何节点参数的方法
    # java_model = model.java
    # geom = java_model.geom('geom1')
    # #wp = geom.feature('wp1')
    # #geom2d = wp.geom()
    # pol = geom.feature('pol2')
    # table_matrix = pol.getDoubleMatrix('table')
    # print(table_matrix)
    # rows = len(table_matrix)
    # cols = len(table_matrix[0])
    # np_array = np.array([[table_matrix[i][j] for j in range(cols)] for i in range(rows)])
    # print(f"  NumPy 数组:\n{np_array}")
    # pol.set('table', coor_list)

    # 运行研究
    print("正在运行研究 研究 1...")
    model.mesh()
    model.solve('研究 1')
    print("研究 1 运行完成")

    # 导出仿真结果
    model.exports()
    model.export('first-band-image', 'Save_TI/first-band-image.png')
    model.export('second-band-image', 'Save_TI/second-band-image')
    model.export('band-gap-figure', 'Save_TI/band-gap-figure')
    print("仿真结果导出完成")

    # 将mph文件另存为comsol_result.mph
    model.save('Save_TI/comsol_result.mph')
    print("mph文件另存为comsol_result.mph完成")

    # 获得仿真结果的拓扑带隙
    model.datasets()
    model.evaluate('freq', "k", '研究 1//参数化解 1', 'first', 1)
    band = model.evaluate('freq', "k", '研究 1//参数化解 1')
    bandgap = band.tolist()
    real_band = [element.real for element in bandgap][:2]
    real_band_float = [round(int(x), 0) for x in real_band]
    print(f"设计结果拓扑带隙: {real_band_float}")

    # client.remove(model)

    # #打开另一个comsol模型并得到stl文件
    # print("正在启动Comsol客户端2...")
    # client = mph.start()
    # print("正在加载模型 stl.mph...")
    # model = client.load('stl.mph')
    # print("模型加载成功！\n")
    # client.names()
    # mph.tree(model)

    # #将结构数据导入新模型
    # java_model = model.java
    # geom = java_model.geom('geom1')
    # wp = geom.feature('wp1')
    # geom2d = wp.geom()
    # pol = geom2d.feature('pol1')
    # table_matrix = pol.getDoubleMatrix('table')
    # rows = len(table_matrix)
    # cols = len(table_matrix[0])
    # np_array = np.array([[table_matrix[i][j] for j in range(cols)] for i in range(rows)])
    # print(f"  NumPy 数组:\n{np_array}")
    # pol.set('table', coor_list)
    # #model.build(geometry)

    # #导出结构文件和stl文件
    # model.exports()
    # model.export('图像 1', 'Save_TI/structure.png')

    return real_band_float

def draw_TI(coor_scatter, num_lattice, real_band_float):
    """
    绘制TI材料的几何结构图（三维视图）

    参数:
    coor_scatter (numpy.ndarray): 材料散射体的坐标数据
    num_lattice (float): 晶格参数，用于绘制外围六边形

    返回:
    None (保存图片到文件)
    """
    # 确保Save_TI目录存在
    import os
    from mpl_toolkits.mplot3d.art3d import Poly3DCollection
    os.makedirs("Save_TI", exist_ok=True)

    # 设置全局字体为Times New Roman
    plt.rcParams['font.family'] = 'serif'
    plt.rcParams['font.serif'] = ['Times New Roman']
    plt.rcParams['font.size'] = 14
    plt.rcParams['axes.labelsize'] = 16
    plt.rcParams['axes.titlesize'] = 18
    plt.rcParams['xtick.labelsize'] = 14
    plt.rcParams['ytick.labelsize'] = 14
    plt.rcParams['legend.fontsize'] = 14

    # 创建单个子图布局，调整尺寸使图片更大更清晰
    fig = plt.figure(figsize=(12, 10))

    try:
        # 计算六边形的顶点坐标
        a = num_lattice
        sqrt3 = np.sqrt(3)

        # 六边形的6个顶点坐标（按您提供的公式）
        hexagon_points = [
            (a/(2*sqrt3), a/2),           # 点1
            (-a/(2*sqrt3), a/2),          # 点2
            (-a/sqrt3, 0),                # 点3
            (-a/(2*sqrt3), -a/2),         # 点4
            (a/(2*sqrt3), -a/2),          # 点5
            (a/sqrt3, 0)                  # 点6
        ]

        # 将二维多边形数据转换为三维实体（高度10）
        height = 2.5
        if len(coor_scatter) > 0:
            # 创建三维散射体：底部面和顶部面以及侧面
            scatter_3d_bottom = np.column_stack((coor_scatter, np.zeros(len(coor_scatter))))
            scatter_3d_top = np.column_stack((coor_scatter, np.full(len(coor_scatter), height)))

            # 创建三维实体的面（底部、顶部和侧面）
            faces = []

            # 底部面
            faces.append(scatter_3d_bottom)

            # 顶部面
            faces.append(scatter_3d_top)

            # 侧面（连接底部和顶部的对应边）
            for i in range(len(coor_scatter)):
                next_i = (i + 1) % len(coor_scatter)
                face = np.array([
                    scatter_3d_bottom[i],
                    scatter_3d_bottom[next_i],
                    scatter_3d_top[next_i],
                    scatter_3d_top[i]
                ])
                faces.append(face)

        # =============== 三维实体侧视图 ===============
        ax1 = fig.add_subplot(111, projection='3d')

        # 绘制三维散射体 - 使用美化的颜色
        if len(coor_scatter) > 0:
            # 使用橙红色系，更加舒适的配色
            poly3d = Poly3DCollection(faces,
                                    facecolors='red',  # 橙红色
                                    alpha=0.8,            # 稍微增加透明度
                                    edgecolors='darkred',  # 粉红色边缘
                                    linewidths=0.8)       # 略细的边缘线
            ax1.add_collection3d(poly3d)

        # 设置侧视图视角 - 降低仰角让结构看起来更大
        ax1.view_init(elev=35, azim=45)  # 仰角35度，方位角45度

        # 去除坐标轴标签、标题和网格
        ax1.set_xlabel('')
        ax1.set_ylabel('')
        ax1.set_zlabel('')
        ax1.set_title('')
        ax1.grid(False)

        # 隐藏坐标轴
        ax1.set_axis_off()

        # 设置坐标轴范围（减少margin让结构更大）
        all_x = [p[0] for p in hexagon_points] + [p[0] for p in coor_scatter]
        all_y = [p[1] for p in hexagon_points] + [p[1] for p in coor_scatter]
        margin = max(abs(max(all_x) - min(all_x)), abs(max(all_y) - min(all_y))) * 0.05

        ax1.set_xlim(min(all_x) - margin, max(all_x) + margin)
        ax1.set_ylim(min(all_y) - margin, max(all_y) + margin)
        ax1.set_zlim(0, height + 1)

        # 设置整体标题（已注释掉）
        # fig.suptitle(f'AVHI Scatterer Structure (Lattice={num_lattice:.3f}, Topological Band={num_band}, Height={height})',
        #             fontsize=22, fontweight='bold', y=0.98, family='serif')
        plt.savefig("Save_TI/TI_structure.png", dpi=300, bbox_inches='tight')
        plt.savefig("Save_TI/TI_structure.pdf", bbox_inches='tight')  # 同时保存PDF格式

        print("图片已保存为 'TI_structure.png' 和 'TI_structure.pdf'")

        # 保存数据到txt文件
        save_data_to_txt(real_band_float, num_lattice, coor_scatter)

        # 保存为OBJ文件用于3D打印
        #save_to_obj(num_band, num_lattice, coor_scatter, height)

        # 保存为STL文件用于3D打印
        save_to_stl(real_band_float, num_lattice, coor_scatter, height)

    finally:
        # 确保关闭图形，释放内存
        plt.close(fig)
def save_data_to_txt(real_band_float, num_lattice, coor_scatter):
    """
    将TI材料数据保存到txt文件
    
    参数:
    num_band: 拓扑带隙值
    num_lattice: 晶格常数
    coor_scatter: 散射体坐标数据
    """
    try:
        with open("Save_TI/TI_data.txt", 'w', encoding='utf-8') as f:
            f.write(f"Topological Band Gap：{real_band_float} (Hz)\n")
            f.write(f"Lattice Constant：{num_lattice} (mm)\n")
            f.write(f"AVHI Scatterer Coordinates:{coor_scatter.tolist()}\n")
        
        print("数据已保存为 'Save_TI/TI_data.txt'")
        
    except Exception as e:
        print(f"保存数据文件时出错: {e}")
def save_to_stl(real_band_float, num_lattice, coor_scatter, height):
    """
    将TI材料的三维结构保存为STL文件，用于3D打印

    参数:
    num_band: 拓扑带隙值
    num_lattice: 晶格常数
    coor_scatter: 散射体坐标数据 (2D numpy array)
    height: 三维实体的高度
    """
    if mesh is None:
        print("跳过STL文件生成：未安装numpy-stl库")
        return

    try:
        stl_filename = "Save_TI/TI_structure.stl"

        # 计算顶点数量
        num_vertices = len(coor_scatter)
        if num_vertices < 3:
            print("警告：散射体顶点数量不足，无法生成有效的STL文件")
            return

        # 创建三维顶点坐标 - 只创建顶部和底部表面点
        # 底部面顶点 (z = 0) - 薄层底部
        scatter_3d_bottom = np.column_stack((coor_scatter, np.zeros(num_vertices)))
        # 顶部面顶点 (z = height) - 薄层顶部
        scatter_3d_top = np.column_stack((coor_scatter, np.full(num_vertices, height)))

        # 计算三角形数量 - 只创建侧面，形成开放的框架结构
        # 侧面：每个侧面都是四边形，分解为2个三角形
        side_triangles = num_vertices * 2
        total_triangles = side_triangles

        # 创建mesh对象
        scatterer_mesh = mesh.Mesh(np.zeros(total_triangles, dtype=mesh.Mesh.dtype))

        triangle_idx = 0

        # 1. 只生成侧面 (每个侧面分解为2个三角形，形成开放框架)
        for i in range(num_vertices):
            next_i = (i + 1) % num_vertices

            # 每个侧面由4个顶点组成：bottom_i, bottom_next, top_next, top_i
            v1 = scatter_3d_bottom[i]      # 底部当前顶点
            v2 = scatter_3d_bottom[next_i] # 底部下一个顶点
            v3 = scatter_3d_top[next_i]    # 顶部下一个顶点
            v4 = scatter_3d_top[i]         # 顶部当前顶点

            # 第一个三角形：v1, v2, v3 (逆时针，法线向外)
            scatterer_mesh.vectors[triangle_idx][0] = v1
            scatterer_mesh.vectors[triangle_idx][1] = v2
            scatterer_mesh.vectors[triangle_idx][2] = v3
            triangle_idx += 1

            # 第二个三角形：v1, v3, v4 (逆时针，法线向外)
            scatterer_mesh.vectors[triangle_idx][0] = v1
            scatterer_mesh.vectors[triangle_idx][1] = v3
            scatterer_mesh.vectors[triangle_idx][2] = v4
            triangle_idx += 1

        # 保存STL文件
        scatterer_mesh.save(stl_filename)

        print(f"STL文件已保存为 '{stl_filename}'")
        print(f"  - 三角形数量: {total_triangles} (侧面{side_triangles}个)")
        print(f"  - 顶点数量: {num_vertices * 2} (底部{num_vertices}个 + 顶部{num_vertices}个)")
        print(f"  - 开放框架结构：高度{height}，只有侧面")
        print("  - 可直接用于3D打印软件")

    except Exception as e:
        print(f"保存STL文件时出错: {e}")


def find_TI_data(x, y, csv_points, str_data_dict, float_data_dict):
    """
    根据输入坐标找到最近的TI材料数据

    参数:
    x (float): 输入坐标的x值
    y (float): 输入坐标的y值
    csv_points (numpy.ndarray): 从CSV文件读取的坐标点数据
    str_data_dict (dict): 字符串格式的JSON数据
    float_data_dict (dict): 浮点数格式的JSON数据

    返回:
    dict: 包含索引、坐标、距离和材料数据的完整信息
    """

    # 确保输入坐标是浮点数
    input_point = np.array([float(x), float(y)])

    # 计算曼哈顿距离
    manhattan_distances = []
    for point in csv_points:
        # 曼哈顿距离 = |x1-x2| + |y1-y2|
        distance = abs(input_point[0] - point[0]) + \
            abs(input_point[1] - point[1])
        manhattan_distances.append(distance)

    # 找到最小距离的索引
    nearest_index = np.argmin(manhattan_distances)

    # 获取最近点坐标
    nearest_point = csv_points[nearest_index]

    # 计算最小距离
    min_distance = manhattan_distances[nearest_index]

    # 获取所有键名并按索引位置提取数据
    str_keys = list(str_data_dict.keys())
    float_keys = list(float_data_dict.keys())

    # 检查索引是否在有效范围内
    if nearest_index >= len(str_keys) or nearest_index >= len(float_keys):
        print(
            f"警告：索引 {nearest_index} 超出数据范围（共有 {min(len(str_keys), len(float_keys))} 条数据）")
        return None

    # 提取对应位置的数据
    str_data = str_data_dict[str_keys[nearest_index]]
    float_data = float_data_dict[float_keys[nearest_index]]
    # 组织返回结果
    result = {
        'input_coordinates': (x, y),
        'nearest_index': nearest_index,
        'nearest_point': (int(nearest_point[0]), int(nearest_point[1])),
        'manhattan_distance': min_distance,
        'str_data': str_data,
        'float_data': float_data
    }

    return result


def design_TI(test_x, test_y, csv_points, str_data_dict, float_data_dict):
    """
    设计TI材料的完整函数，集成了查找TI数据和绘制TI结构图的功能

    参数:
    test_x (float): 输入坐标的x值
    test_y (float): 输入坐标的y值
    csv_points (numpy.ndarray): 从CSV文件读取的坐标点数据
    str_data_dict (dict): 字符串格式的JSON数据
    float_data_dict (dict): 浮点数格式的JSON数据

    返回:
    tuple: (coor_scatter, num_lattice, num_band)
    """
    # 查找TI材料数据
    result = find_TI_data(test_x, test_y, csv_points,
                          str_data_dict, float_data_dict)

    if result is None:
        print("未能找到对应的TI材料数据")
        return None, None, None

    # 从结果中提取所需数据
    coor_scatter = np.array(result["float_data"]["Coordinate"])
    num_lattice = result["float_data"]["Lattice"]
    num_band = result["str_data"]["Band"]

    # 将结构数据导入到函数comsol_sovel中
    real_band_float = comsol_solve(coor_scatter, num_lattice)

    # 计算目标带隙和实际带隙的误差
    target_band = [test_x, test_y]
    error = abs(target_band[0] - real_band_float[0]) + \
        abs(target_band[1] - real_band_float[1])
    print(f"目标带隙和实际带隙的误差: {error}")

    # 打印找到的数据信息
    print(f"找到TI材料数据:")
    print(f"  输入坐标: {result['input_coordinates']}")
    print(f"  最近点坐标: {result['nearest_point']}")
    print(f"  曼哈顿距离: {result['manhattan_distance']}")
    print(f"  晶格常数: {num_lattice}")
    print(f"  散射体坐标数量: {len(coor_scatter)}")
    print(f"  拓扑带隙: {num_band}")
    print(f"  实际带隙: {real_band_float}")
    print(f"  误差: {error}")

    # 绘制TI三维视图、生成txt数据和stl结构文件
    draw_TI(coor_scatter, num_lattice, real_band_float)

    # 返回关键参数
    return coor_scatter, num_lattice, real_band_float


def load_data():
    """
    加载所有需要的数据文件

    返回:
    tuple: (csv_points, str_data_dict, float_data_dict)
    """
    try:
        # 读取CSV文件
        csv_data = pd.read_csv('band_data/filter_band_30.csv', header=None)
        csv_points = csv_data.values

        # 读取字符串格式的JSON文件
        with open('TI_data/filter_str_data_com.json', 'r', encoding='utf-8') as f:
            str_data_dict = json.load(f)

        # 读取浮点数格式的JSON文件
        with open('TI_data/filter_float_data_com.json', 'r', encoding='utf-8') as f:
            float_data_dict = json.load(f)

        return csv_points, str_data_dict, float_data_dict

    except FileNotFoundError as e:
        raise FileNotFoundError(f"未找到数据文件: {e}")
    except json.JSONDecodeError as e:
        raise Exception(f"JSON文件解析错误: {e}")
    except Exception as e:
        raise Exception(f"加载数据时出错: {e}")


# 测试函数
if __name__ == "__main__":
    print("=== 加载数据 ===")
    csv_points, str_data_dict, float_data_dict = load_data()
    print(f"成功加载数据:")
    print(f"  CSV数据点: {len(csv_points)} 个")
    print(f"  字符串JSON对象: {len(str_data_dict)} 个")
    print(f"  浮点JSON对象: {len(float_data_dict)} 个")

    # 测试新的design_TI函数
    test_x, test_y = 727, 1922
    print(f"\n=== 使用design_TI函数测试坐标 ({test_x}, {test_y}) ===")
    coor_scatter, num_lattice, num_band = design_TI(
        test_x, test_y, csv_points, str_data_dict, float_data_dict)

    if coor_scatter is not None:
        print(f"\n=== design_TI函数执行完成 ===")
        print(f"返回值:")
        print(f"  coor_scatter形状: {coor_scatter.shape}")
        print(f"  num_lattice: {num_lattice}")
        print(f"  num_band: {num_band}")

    else:
        print("design_TI函数执行失败")
