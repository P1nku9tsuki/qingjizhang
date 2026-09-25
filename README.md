<div align="center">

![Banner](https://capsule-render.vercel.app/api?type=waving&color=0:7C6BA8,100:5E7BA4&height=240&section=header&text=%E8%BD%BB%E8%AE%B0%E8%B4%A6&fontSize=80&fontColor=ffffff&fontAlignY=38&desc=%E7%AE%80%E6%B4%81%E4%BC%98%E9%9B%85%E7%9A%84%20Android%20%E8%AE%B0%E8%B4%A6%E5%BA%94%E7%94%A8&descAlignY=60&descSize=16)

<br/>

[![Android](https://img.shields.io/badge/Android-13%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue?style=flat-square)](LICENSE)
[![Release](https://img.shields.io/github/v/release/P1nku9tsuki/qingjizhang?style=flat-square&color=success&label=Release)](https://github.com/P1nku9tsuki/qingjizhang/releases)

<br/>

**所有数据本地储存安全可靠**

*无需登录 · 完全免费 · 没有广告*

</div>

---

## 📖 目录

- [简介](#-简介)
- [功能](#-功能)
- [下载](#-下载)
- [技术栈](#-技术栈)
- [环境要求](#-环境要求)
- [构建](#-构建)
- [数据与隐私](#-数据与隐私)
- [更新日志](#-更新日志)
- [贡献](#-贡献)
- [开源协议](#-开源协议)

---

## 💡 简介

**轻记账**是一款轻便好用的 Android 记账应用。

它不追求功能堆砌，只专注于做好一件事——**快速记一笔、看得清花销、管得住预算、攒得下目标**。

> 记账不该是负担，而是一种与自己相处的方式。

---

## ✨ 功能

<table>
<tr>
<td width="50%" valign="top">

**📝 记录**

- 自定义数字键盘，触感细腻
- 内置计算器，轻松计算
- 备注自动识别分类，越用越准
- 最近常用模板，一键复用
- 同步账单（日 / 周 / 月）

</td>
<td width="50%" valign="top">

**📊 洞察**

- 环形图掌握消费构成
- 月 / 年双视角统计
- 趋势折线 + 分类排行
- 热力图看习惯
- 一键生成可分享的账单图

</td>
</tr>
<tr>
<td width="50%" valign="top">

**💰 规划**

- 月度预算 + 分类预算
- 临界与超支主动提醒
- 储蓄目标，进度可视化
- 连续记账里程碑

</td>
<td width="50%" valign="top">

**🎨 定制**

- 动态取色（Monet）
- 分类颜色自由定义
- 深浅色模式
- 桌面快捷入口
- 应用内检查更新

</td>
</tr>
</table>

---

## 📦 下载

前往 [**Releases**](https://github.com/P1nku9tsuki/qingjizhang/releases) 页面，下载最新的 `app-release.apk`。

> 安装前请在系统设置中允许「安装未知来源应用」

---

## 🛠 技术栈

| 类别 | 技术 |
|:---|:---|
| 语言 | Kotlin |
| UI | Jetpack Compose · MIUIX-UI · Haze |
| 数据 | Room · DataStore |
| 异步 | Coroutines · Flow |
| 后台 | WorkManager |

---

## 📱 环境要求

- **系统**：Android 12（API 31）及以上
- **架构**：arm64-v8a / armeabi-v7a
- **存储**：约 20 MB 可用空间

---

## 🚀 构建

克隆仓库：

    git clone https://github.com/Plnku9tsuki/qingjizhang.git
    cd qingjizhang

构建 Debug 版本：

    ./gradlew assembleDebug

构建 Release 版本：

    ./gradlew assembleRelease

> 上面的代码块用 4 空格缩进表示，复制后即为可执行命令。

---

## 🔒 数据与隐私

- 所有账单数据**仅存储于设备本地**，不会上传至任何服务器
- 唯一的外部网络请求为「检查更新」，可在设置中关闭
- 无广告、无统计、无第三方数据采集 SDK

---

## 📝 更新日志

### v1.1.0 · 2026-09-25

**兼容性与优化**

- 📱 最低支持版本降至 Android 12（API 31）
- ⚡ 精简代码
- 🔧 性能优化

### v1.0.0 · 2026-09-21

**首个正式版本**

- 📝 快速记账：自定义数字键盘 + 内置计算器
- 🤖 智能分类：备注自动识别，支持本地学习
- 📊 统计分析：环形图、趋势折线、分类排行、热力图
- 💰 预算管理：月度预算 + 分类预算，超支预警
- 🎯 储蓄目标：设定目标，追踪进度
- 🎨 主题定制：动态取色、分类颜色、深浅色
- 🖼️ 分享账单：一键生成分享图
- 📤 数据备份：JSON 导入导出 + CSV
- 🔔 桌面快捷方式，快速记一笔
- ⚙️ 应用内检查更新

<details>
<summary>查看完整历史</summary>

（后续版本将在此处追加）

</details>

---

## 🤝 贡献

这是一个个人项目，但欢迎任何形式的参与：

- 🐛 **发现 Bug？** 提交 [Issue](https://github.com/P1nku9tsuki/qingjizhang/issues/new)
- 💡 **有想法？** 提交 [Feature Request](https://github.com/P1nku9tsuki/qingjizhang/issues/new)
- 🔧 **想改代码？** Fork 后提交 Pull Request
- ⭐ **只是喜欢？** 给个 Star 就足够了

**提交 Issue 前请先搜索是否已有相同问题。**

---

## 📄 开源协议

本项目基于 [**GPL-3.0**](LICENSE) 协议开源。

您可以自由使用、修改和分发本项目，但**衍生作品必须同样以开源方式发布**。

---

## 🙏 致谢

感谢所有开源项目的贡献者，完整名单见应用内「设置 → 开源致谢」。

特别感谢：[MIUIX-UI](https://github.com/compose-miuix-ui/miuix) · [Haze](https://github.com/chrisbanes/haze) · [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

<div align="center">

![Footer](https://capsule-render.vercel.app/api?type=waving&color=0:7C6BA8,100:5E7BA4&height=180&section=footer&text=%E5%A6%82%E6%9E%9C%E5%96%9C%E6%AC%A2%EF%BC%8C%E7%82%B9%E4%B8%AA%20Star%20%E5%90%A7&fontSize=20&fontColor=ffffff&fontAlignY=72)

</div>