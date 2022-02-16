# BlockGenerator 城市场地快速量型转化程序
## 1. 项目背景
本项目是东南大学建筑运算与应用研究所研究所一年级设计课本人的课程设计代码。
## 2. 简介
- 本程序可以帮助建筑师与规划师在做城市设计与城市规划阶段，通过每个地块的高度、密度、容积率等基本参数来快速生成不同功能种类的城市体块模型。
- 场地通过.dxf文件读入。
-  生成结果可以导出为.obj格式用各种主流建模软件预览并深化编辑。
- 通过调整各图元的配色，对于一些应用场景可以跳过使用Photoshop等软件的处理，达到直接出图的效果。

## 3. 依赖
- Processing
- He_mesh
- PeasyCamera
- Kabeja
- DXFImporter
- IGeo

## 4. 现支持街区功能模块
- 商业（Commercial）
  ![商业](https://github.com/AlessioWang/BlockGeneration/blob/e01a9f6cbad2107d236fb8e1cec1eb04db0f974e/src/Pics/commercial.jpg?raw=true)
- 综合体（Commercial）
  ![综合体](https://github.com/AlessioWang/BlockGeneration/blob/e01a9f6cbad2107d236fb8e1cec1eb04db0f974e/src/Pics/high.jpg?raw=true)
- 住宅（Residence）
  ![住宅](https://github.com/AlessioWang/BlockGeneration/blob/e01a9f6cbad2107d236fb8e1cec1eb04db0f974e/src/Pics/residence.jpg?raw=true)
- 产业园（ST_Zone）
  ![产业园](https://github.com/AlessioWang/BlockGeneration/blob/e01a9f6cbad2107d236fb8e1cec1eb04db0f974e/src/Pics/ST_Zone.jpg?raw=true)
- 绿地（Green）
- 硬地广场（Ground）

## 5. 成果展示
![城市轴侧](https://github.com/AlessioWang/BlockGeneration/blob/e01a9f6cbad2107d236fb8e1cec1eb04db0f974e/src/Pics/%E5%9F%8E%E5%B8%82%E8%BD%B4%E4%BE%A7.jpg?raw=true)
![城市平面](https://github.com/AlessioWang/BlockGeneration/blob/e01a9f6cbad2107d236fb8e1cec1eb04db0f974e/src/Pics/%E5%9F%8E%E5%B8%82%E5%B9%B3%E9%9D%A2.jpg?raw=true)
![街区轴侧.jpg](https://github.com/AlessioWang/BlockGeneration/blob/e01a9f6cbad2107d236fb8e1cec1eb04db0f974e/src/Pics/%E8%A1%97%E5%8C%BA%E8%BD%B4%E4%BE%A7.jpg?raw=true)

## 6. TODO
- 更多街区类型
- GUI
- 城市控制性指标参数直接转化到建筑形体