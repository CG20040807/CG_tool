<div align="center">

# 测井数据处理系统

一个基于 Java 的课程项目整理版，用来完成测井数据读取、储层参数计算、结果查询、排序统计和油层筛选。

这份仓库不是把原始作业代码原封不动丢上来，而是把它重新收拾成了一个更适合展示、复用和继续扩展的 GitHub 项目。

<p>
  <img src="https://img.shields.io/badge/Language-Java%2017-2f80ed" />
  <img src="https://img.shields.io/badge/Project-Course%20Project-27ae60" />
  <img src="https://img.shields.io/badge/Status-Ready%20for%20GitHub-f2994a" />
</p>

</div>

## 项目简介

这个项目的核心工作很直接，就是把原始测井数据读进来，算出孔隙度、泥质含量和含油饱和度，然后围绕这些结果做一套常见的课程功能：

- 数据提取与检查
- 测井数据处理与结果导出
- 按深度点序号查询
- 统计最大值、最小值、平均值
- 按含油饱和度排序
- 按储层等级分类
- 查找好油层

如果把它说得再简单一点，这个程序做的就是三件事：

1. 把原始数据从文件里读出来。
2. 用给定公式算出关键储层参数。
3. 把结果做成能查、能排、能统计的分析输出。

## 功能

| 模块 | 作用 | 对应结果 |
| --- | --- | --- |
| 数据读取 | 读取测井文件头、参数文件和深度点数据 | 原始测井记录 |
| 参数计算 | 计算 `POR`、`VSH`、`So` | 储层解释结果 |
| 数据检查 | 输出原始数据和检验数据 | `数据提取与检查.txt` |
| 结果导出 | 生成标准化结果表 | `大数据22304班_Results_37.txt` |
| 查询统计 | 按索引、参数和分类进行分析 | 控制台结果 |
| 油层筛选 | 找出满足条件的好油层 | 控制台结果 |
| 结果可视化 | 生成趋势图并弹出窗口查看 | `well-logging-visualization.png` |

## 流程

```mermaid
flowchart LR
    A[读取原始测井文件] --> B[提取深度点数据]
    B --> C[计算 POR / VSH / So]
    C --> D[结果查询]
    C --> E[统计与排序]
    C --> F[储层分类]
    C --> G[好油层筛选]
    C --> H[结果文件导出]
```

## 核心指标

项目里最关键的三个指标是：

- `POR`：孔隙度，用来表示岩石内部可供流体占据的空间大小。
- `VSH`：泥质含量，用来反映储层中泥质成分的多少。
- `So`：含油饱和度，用来判断储层的含油情况。

对应的思路也比较朴素：

- 先根据声波时差计算孔隙度。
- 再根据自然伽马计算泥质含量。
- 最后结合孔隙度和电阻率估算含油饱和度。

## 仓库结构

```text
well-logging-project
├─ data
│  ├─ parameters.txt
│  └─ well_logging_data.txt
├─ output
│  └─ .gitkeep
├─ src
│  └─ main
│     └─ java
│        └─ cj1
│           ├─ ChartExporter.java
│           ├─ DataHandler.java
│           ├─ IDataProcessor.java
│           ├─ ISystemFunctions.java
│           ├─ MetricType.java
│           ├─ Renwu4.java
│           ├─ SystemFunctions.java
│           ├─ VisualizationFrame.java
│           ├─ VisualizationPanel.java
│           └─ WellRecord.java
├─ .gitignore
└─ README.md
```

## 代码结构

- `DataHandler`
  负责读文件、解析数据、计算基础参数。
- `WellRecord`
  负责保存单条深度点数据，让每个字段都有清楚的名字
- `SystemFunctions`
  负责菜单功能、输出结果、排序分类和统计分析。
- `VisualizationPanel`、`VisualizationFrame`、`ChartExporter`
  负责把结果画成图，并导出成 PNG。


## 开始

### 1. 准备数据

把你的原始文件放进 `data` 目录，文件名保持下面这样：

- `data/parameters.txt`
- `data/well_logging_data.txt`

### 2. 编译

在项目根目录执行：

```powershell
javac -encoding UTF-8 -d out src\main\java\cj1\*.java
```

### 3. 运行

```powershell
java -cp out cj1.Renwu4
```

如果你想自己指定数据目录和输出目录，也可以这样运行：

```powershell
java -cp out cj1.Renwu4 data output
```

## 运行后可以完成什么

程序启动后会给出菜单，支持下面这些操作：

- 查看提取出的原始测井数据和检验数据
- 生成测井处理结果文件
- 按深度点序号查看某一条记录
- 统计孔隙度、泥质含量、含油饱和度的最大值、最小值和平均值
- 按含油饱和度从高到低排序
- 统计不同等级储层的数量
- 查找满足条件的好油层
- 打开结果可视化窗口，并导出 PNG

## 输出文件

程序运行后会在 `output` 目录生成：

- `数据提取与检查.txt`
- `大数据22304班_Results_37.txt`
- `well-logging-visualization.png`


