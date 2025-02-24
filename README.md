# 💧 WaterDrinkTracker [中文]

[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg)](https://kotlinlang.org/)

[![Compose Version](https://img.shields.io/badge/Jetpack%20Compose-1.10.0-brightgreen.svg)](https://developer.android.com/jetpack/compose)

[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

一款基于Kotlin和Jetpack Compose开发的Android饮水追踪应用，帮助你培养健康饮水习惯🥤

截图预览 
<div align="center">
  <img src="app%2Fimg%2F%E4%B8%BB%E7%95%8C%E9%9D%A2.png" width="200" alt="主界面"/>
  <img src="app%2Fimg%2F%E6%97%A5%E5%8E%86%E7%95%8C%E9%9D%A2.png" width="200" alt="日历界面"/>
  <img src="app%2Fimg%2F%E8%AE%BE%E7%BD%AE%E7%95%8C%E9%9D%A2.png" width="200" alt="设置界面"/>
  <img src="app%2Fimg%2F%E5%B0%8F%E7%BB%84%E4%BB%B6.png" width="200" alt="小组件"/>
</div>
说明：
## ✨ 主要功能
### 核心功能
- 📅 日历视图：直观查看每日饮水记录

- 🎯 目标设置：自定义每日饮水目标

- 🎨 主题定制：支持动态切换应用主题颜色

- 📊 微件记录：支持使用微件记录，便于使用

### 数据管理
- 📤 数据导出：支持本地JSON文件导出

- 📥 数据导入：从本地文件恢复记录

- ☁️ 云备份：WebDAV协议实现云端备份/恢复

- ⏰ 自动备份：每日自动本地备份机制

### 交互体验
- 🚰 快捷记录：一键添加/减少饮水量

- 📌 历史追溯：点击日历查看任意日期记录

- 📱 响应式布局：适配不同屏幕尺寸

## 🛠️ 技术栈

- **语言**: 100% Kotlin
- **UI框架**: Jetpack Compose + Material3
- **本地存储**: SharedPreferences
- **数据备份**: 
  - WebDAV集成：sardine-android
- **其他库**:
  - Gson：数据序列化
  - glance：创建小组件

## 📥 安装使用
1.克隆仓库：

```bash
git clone https://github.com/xmoon2022/WaterDrinkTracker.git
```
2.使用Android Studio打开项目

3.连接设备或启动模拟器

4.点击运行按钮 ▶️

## 🤝 贡献指南
欢迎任何形式的贡献！可以多提一些issues

## 📄 许可证
本项目采用 MIT License

## 🙏 致谢
感谢Jetpack Compose官方文档

灵感来源于个人健康管理需求

## 喝水一小步，健康一大步！ 🌟
