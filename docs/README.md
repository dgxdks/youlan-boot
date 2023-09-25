<h1 align="center">youlan-boot</h1>
<h3 align="center">适合二次开发的轻量级业务开发框架</h3>

<p align="center">
    <a href="https://github.com/dgxdks/youlan-boot">
      <img src="https://img.shields.io/github/stars/dgxdks/youlan-boot.svg?style=social&label=Stars" alt="">
    </a>    
    <a href="https://gitee.com/dgxdks/youlan-boot">
      <img src="https://gitee.com/dgxdks/youlan-boot/badge/star.svg?theme=blue" alt="">
    </a>
    <a>
        <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="">
    </a>
    <a>
        <img src="https://img.shields.io/badge/Spring_Boot-2.7-blue.svg" alt="">
    </a>
    <a href="https://gitee.com/dgxdks/youlan-boot">
        <img src="https://img.shields.io/badge/JDK-8-green.svg" alt="">
    </a>
    <a>
        <img src="https://img.shields.io/badge/JDK-11-green.svg" alt="">
    </a>
</p>

# 项目前言

日常开发中，为了避免重复造轮子，有时候会选择一些开源后台权限管理系统作为基础框架进行二开，例如[RuoYi](https://gitee.com/y_project/RuoYi)、[RuoYi-Vue-Plus](https://gitee.com/dromara/RuoYi-Vue-Plus)
等等，本人有幸也是用过RuoYi系的的框架接过一些中小外包项目：小程序类、H5类、ERP类、接口对接类等等。由于这些框架本身不太包含常用的业务模块，比如
要二开一个小程序项目，跟小程序相关的代码是需要自己写的，这种活干错了促使我创建了基于不同业务场景的项目模版方便自己复用，做技术也有些年头了，现在
想把自己关于这块的项目经历归纳至一处当做自己的“技术笔记”，如果在这个过程中还能帮助到别人更是吾之幸事！

- 文档地址：https://doc.youlan.online
- Gitee地址：https://gitee.com/dgxdks/youlan-boot
- GitHub地址：https://github.com/dgxdks/youlan-boot
- 使用和学习优先拉取主线分支master，代码会保持 GitHub 和 Gitee 同步
- 感兴趣的小伙伴点个⭐️Star⭐️鼓励一下吧！

# 项目简介

平台本身定位就是中小型项目二次开发，所以除了后台权限管理功能模块还会不断抽象出更多的业务模块，所以它是新手入门学习的辅助工具，是中小型公司快速响应业务
的效率工具，更是勤劳的程序猿们接活利器。本着“取其精华去其糟粕”的原则，架构设计时参考过RuoYi系的项目，前端一开始也是使用RuoYi的代码，但是细看应该
会发现有较多不同，在陆续的迭代中本人也对复用的代码和数据库表进行了细致的重构，对这些代码里个人认为是漏洞或BUG的地方在这个项目的实现中也多了份关注，
总之尽可能让架构做到“高内聚低耦合”，更符合二次开发的场景！

# 项目特点

- 取消经常在各大开源框架中能看到的“framework/core”模块，秉持按需引用原则，避免新开一个应用一引用就引所有，然后一排除就要排一堆的尴尬
- 设计组织机构树表统一管理平台下属，仅将平台中的部门当做组织，新增组织机构树表，仅将部门做为组织机构中的一种机构类型，方便二开是扩展新的
- 后端细粒度拆分公共模块：内置公共模块 [common](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-common)
  、三方公共模块 [plugin](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-plugin)
  ，这些都是针对某一领域的独立“工具集合”，被copy出去放在陌生项目里照样能打
- 前端轻度抽象封装 [framework](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-web/src/framework) 模块，内置基础
  components、directive、icons、mixin、tools，放在陌生的ElementUI系项目里略加修改同样能打
- 前端CRUD页面复用预先定义的公共UI组件并
  默认混入[crud.js](https://gitee.com/dgxdks/youlan-boot/blob/master/youlan-web/src/framework/mixin/crud.js)，实现同样的功能
  可能会减少一半的代码量
- 代码生成时支持生成数据库Entity衍生的DTO（页面编辑）、VO（页面显示/Excel导出）、PageDTO（页面查询），在数据库表字段非常多时非常有用，避免都混在一个数据库Entity中让类面目全非
- 其他特点

# 功能模块

<table>
  <tr>
    <th>模块</th>
    <th>功能</th>
    <th>描述</th>
  </tr>
  <tr>
    <td rowspan="4">系统模块</td>
  </tr>
  <tr>
    <td>用户管理</td>
    <td>123123</td>
  </tr>
  <tr>
    <td>用户管理</td>
    <td>123123</td>
  </tr>
  <tr>
    <td>用户管理</td>
    <td>123123</td>
  </tr>

</table>
# 技术选型

|   |   |   |   |   |   |   |   |   |
|---|---|---|---|---|---|---|---|---|
|   |   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   |   |
|   |   |   |   |   |   |   |   |   |

