<img src="https://dgxdks.gitee.io/youlan-boot-doc/assets/img/banner.jpg" height="200px" width="100%" alt="">

<h2 align="center" style="font-weight: bold;">
基于SpringBoot的轻量级业务开发框架
</h2>

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
        <img src="https://img.shields.io/badge/OpenJDK-11-green.svg" alt="">
    </a>
</p>

<p align="center">
    <a href="https://dgxdks.gitee.io/youlan-boot-doc">项目文档 | </a>
    <a href="https://gitee.com/dgxdks/youlan-boot">Gitee地址 | </a>
    <a href="https://github.com/dgxdks/youlan-boot">GitHub地址</a>
</p>


> 日常开发中，为了避免重复造轮子，有时候会选择一些开源后台权限管理系统作为基础框架进行二开，例如[RuoYi](https://gitee.com/y_project/RuoYi)、[RuoYi-Vue-Plus](https://gitee.com/dromara/RuoYi-Vue-Plus)
> 等等，本人有幸也用过RuoYi系的框架接过一些中小型外包项目：小程序类、H5类、ERP类、接口对接类等等。由于这些框架本身不太包含常用的业务模块，比如要二开一个小程序项目，跟小程序相关的代码是需要自己写的，这种活干多了促使我又创建了基于不同业务场景的项目模版方便团队复用。做技术也有些年头了，现在想把自己关于这块的经验归纳至一处当做自己的“技术笔记”，如果在这个过程中还能帮助到别人更是吾之幸事！

- 使用和学习优先拉取主线分支master，代码保持GitHub和Gitee同步，**日常使用优先访问Gitee**
- 感兴趣的小伙伴点个⭐️Star⭐️鼓励一下吧！

## 项目简介

项目本身定位就是服务于中小型项目二次开发，所以除了后台权限管理功能模块还会不断抽象出更多的业务模块，希望它是新手入门学习的辅助工具，是中小型公司快速成长的效率工具，更是勤劳的程序猿们的接活利器。本着“取其精华去其糟粕”的原则，架构设计时参考过RuoYi系的项目，前端一开始也是使用RuoYi的代码，所以有RuoYi系的既视感，但是细看应该会发现有较多不同，在陆续的迭代中本人也对复用的代码和数据库表进行了细致的重构，对这些代码里个人认为是漏洞或BUG的地方，在这个项目的实现中也多了份注意，总之尽可能让项目做到“高内聚低耦合”，就算不使用这个项目整体做开发，也能从这个项目里拷点代码走运用到日常开发工作中，更好的服务于大家！

## 项目特点

### 项目架构灵活

秉持按需引用原则，拒绝集中化管理公共代码、SpringBoot配置、SpringBoot切面等，避免“引用就得引所有排除得排一堆”的尴尬，
留给开发人员更大的自由度

> 为了能搞好的说明差异，以RuoYi-Vue-Plus4.x项目里的 [framework](https://gitee.com/dromara/RuoYi-Vue-Plus/tree/4.X/ruoyi-framework/src/main/java/com/ruoyi/framework)
> 模块为例，这里面包含了很多配置了@Configuration的类以及被公共引用的代码和注解，现在我需要二开一个小程序项目，创建了一个独立的app模块，这个模块需要独立向移动端提供接口服务，这时候可能就会遇到这样一个问题：预先定义在framework里面的有些配置和注解我想用，但是我app模块又有自己独立的数据库配置、权限框架配置等，就会很容易和framework内部已有的配置产生冲突。

### 组织机构可扩展

组织机构支持自定义扩展，平台中的用户体系不局限于部门这一种组织机构类型下

> 组织机构是后台权限管理系统中的骨架，直接影响了用户的归属和一些数据的归属问题，部门就是一种组织机构，用户归属在不同的部门下，这是很常见的场景。
> 但是如果用户只能归属在部门下会有一定局限性，例如电商和ERP项目中的商户、门店、供应商等，这些也都是组织机构，也会存在用户登录平台进行操作，并且还要对这些组织机构进行菜单和数据的权限控制，之前自己在使用RuoYi系做二开的时候会在部门表里硬加一个机构类型然后区分不同的机构类型好让扩展的机构都存在于一套权限管理体系一下，前后端代码也得再适配一下，在当前项目里对这一问题进行了优化，设计了一张组织机构表，部门表只是组织机构表的扩展

### 公共模块可插拔

后端公共模块细粒度拆分为：内置公共模块 [common](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-common)
、三方公共模块 [plugin](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-plugin)，每个模块都是独立的一套“工具集合”，拷贝出去放在陌生项目里照样能打

> 为了按需引用，在解放了“framework”模块后，这些公共的内容都散落回真实属于自己的位置。在本项目中，例如[@OperationLog](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-system/src/main/java/com/youlan/system/anno)
> 这个注解，开发者引用system模块才有机会使用注解对应的功能，不引用自然也就不需要这个功能。再比如[@ExcelDictProperty](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-system/src/main/java/com/youlan/system/excel/anno)
> 这个注解，本质是对[excel](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-common/youlan-common-excel)
> 模块的扩展，为了让excel导出时支持数据字典类型转换，同样的道理，引用了system模块，excel导出支持数据字典才有意义，如果放在“framework”这样的模块里，会被自动带入过多不相干的代码和自动注入的SpringBean

### 前端开发高效

前端轻度抽象封装 [framework](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-web/src/framework)
模块，内置基础components、directive、icons、mixin、tools，同样是一套“工具集合”，放在陌生的ElementUI系项目里略加修改照样能打

### 前端代码简洁

前端CRUD页面使用[framework](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-web/src/framework)
预先定义的公共UI组件并默认混入[crud.js](https://gitee.com/dgxdks/youlan-boot/blob/master/youlan-web/src/framework/mixin/crud.js)
，实现同样的功能代码会更简洁，代码生成模版已同步支持

### 代码生成贴心

- 支持生成数据库Entity衍生的DTO、VO、PageDTO，在数据库表字段非常多时非常有用，避免都混在一个数据库Entity中让类面目全非
- 基于[db](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-common/youlan-common-db)
  模块中的[@Query](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-common/youlan-common-db/src/main/java/com/youlan/common/db/anno)
  注解，代码生成时自动追加此注解，针对简单查询可通过[DBHelper](https://gitee.com/dgxdks/youlan-boot/blob/master/youlan-common/youlan-common-db/src/main/java/com/youlan/common/db/helper/DBHelper.java)
  一行代码生成QueryWrapper和IPage
- 基于OpenApi 3规范，自动生成配套的Java接口文档注解

## 项目结构

~~~
youlan-boot
├─ db                                   // 数据库文件目录
├─ youlan-admin                         // 后台管理【后端】
│  └─ AdminApplication.java             // 服务启动类
│  └─ resources                         // 资源文件
│      └─ crypto                        // 加密公私钥目录
│      └─ i18n                          // 国际化资源包
│      └─ application.yml               // 公共YAML配置文件
│      └─ application-dev.yml           // 开发环境YAML配置文件
│      └─ application-prod.yml          // 生产环境YAML配置文件
│      └─ banner.txt                    // 服务启动标志
│      └─ ip2region.xdb                 // IP离线定位数据包
│      └─ logback-spring.xml            // 日志配置文件
├─ youlan-common                        // 框架级别公共模块
│  └─ youlan-common-captcha             // 验证码模块
│  └─ youlan-common-core                // 核心模块
│  └─ youlan-common-crypto              // 加密模块
│  └─ youlan-common-db                  // 数据库模块
│  └─ youlan-common-excel               // Excel模块
│  └─ youlan-common-redis               // Redis模块
│  └─ youlan-common-storage             // 文件存储模块
│  └─ youlan-common-validator           // 数据校验模块
├─ youlan-plugin                        // 三方级别公共模块
│  └─ youlan-plugin-http                // HTTP模块
│  └─ youlan-plugin-mp                  // 小程序模块
│  └─ youlan-plugin-pay                 // 支付模块
│  └─ youlan-plugin-region              // IP定位模块
│  └─ youlan-plugin-sms                 // 短信模块
├─ youlan-system                        // 系统管理模块
├─ youlan-tools                         // 系统工具模块
├─ youlan-web                           // 后台管理【前端Vue2】
~~~

## 业务能力

| 业务名称 | 业务描述                                                      |
|------|-----------------------------------------------------------|
| 用户管理 | 支持用户增删改查、导入导出、分配角色等功能                                     |
| 角色管理 | 支持角色增删改查、导出、数据权限、分配用户，刷新缓存等功能                             |
| 菜单管理 | 支持菜单增删改查等功能                                               |
| 部门管理 | 支持部门增删改查等功能                                               |
| 岗位管理 | 支持岗位增删改查等功能                                               |
| 字典管理 | 支持字典类型&字典值增删改查、导出、刷新缓存等功能                                 |
| 系统参数 | 支持系统参数增删改查、导出等功能                                          |
| 通知公告 | 完成中。。。                                                    |
| 日志管理 | 支持用户操作日志记录、查询、删除、清空、导出等功能<br/>支持用户登录日志记录、查询、导出、清空、账户解锁等功能 |
| 存储管理 | 支持存储配置增删改查、导出、缓存刷新等功能；<br/>支持记录存储记录、删除、清空、文件&图片上传等功能      |
| 短信管理 | 完成中。。。                                                    |
| 在线用户 | 支持在线用户查询、强踢等功能                                            |
| 缓存监控 | 支持Redis运维指标图表展示等功能                                        |
| 系统接口 | 通过iframe展示knife4j生成的接口文档                                  |
| 代码生成 | 支持根据数据库表生成框架配套的前后端代码                                      |

## 框架能力

持续更新中。。。

## 技术选型

| 名称                                                                          | 组件                                                                   | 描述                                                                                                                   |
|-----------------------------------------------------------------------------|----------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| [后台前端项目(Vue2)](https://gitee.com/dgxdks/youlan-boot/tree/master/youlan-web) | Vue2、ElementUI、JavaScript                                            | 基于[ruoyi-ui](https://gitee.com/y_project/RuoYi-Vue/tree/master/ruoyi-ui)重写Vue2版本的后台管理前端项目                            |
| 权限框架                                                                        | [sa-token](https://gitee.com/dromara/sa-token)                       | 使用SaToken替代配置相对反锁的SpringSecurity，足够满足大部分场景。[官方文档](https://sa-token.cc/)                                              |
| ORM框架                                                                       | [mybatis-plus](https://github.com/baomidou/mybatis-plus)             | 简单查询基本不用写SQL，内置各种拦截器插件，数据库操作更省心。[官方文档](https://baomidou.com/)                                                        |
| 多数据源                                                                        | [dynamic-datasource](https://github.com/baomidou/dynamic-datasource) | 结合mybatis-plus可应对大部分多数据源场景，功能丰富使用灵活。[官方文档](https://www.kancloud.cn/tracy5546/dynamic-datasource/2264611)             |
| Redis客户端                                                                    | [redisson](https://github.com/redisson/redisson)                     | 高性能Redis客户端，除了满足对redis的操作，还内置了分布式场景下的锁、限流、原子操作等功能。[官方文档](https://github.com/redisson/redisson/wiki/Table-of-Content) |
| 文件存储                                                                        | [x-file-storage](https://gitee.com/dromara/x-file-storage)           | 支持市面上常见的各种对象存储服务商，兼容S3协议，同时还支持本地、FTP、SFTP、WebDAV等存储方法。[官方文档](https://spring-file-storage.xuyanwu.cn/)                |
| 短信发送                                                                        | [sms4j](https://gitee.com/dromara/sms4j)                             | 支持市面上常见的各种短信服务商。[官方文档](https://wind.kim/)                                                                            |
| Excel工具                                                                     | [EasyExcel](https://gitee.com/easyexcel/easyexcel)                   | 高效的Excel处理工具，使用方便简捷。[官方文档](https://easyexcel.opensource.alibaba.com/)                                                |
| 接口文档                                                                        | [Knife4j](https://github.com/xiaoymin/knife4j)                       | 集Swagger2+OpenApi3为一体的增强解决方案，knife4j生成的接口文档页面支持文档自定义以及更加灵活的调试功能，是开发调试利器。[官方文档](https://doc.xiaominfo.com/)           |
| IP离线定位                                                                      | [ip2region](https://gitee.com/lionsoul/ip2region)                    | 离线IP地址定位库和IP定位数据管理框架，10微秒级别的查询效率。[官方文档](https://gitee.com/lionsoul/ip2region/tree/master/binding/java)               |
| JVM诊断                                                                       | [Arthas](https://github.com/alibaba/arthas)                          | 可以快速定位线上问题，监控JVM性能指标，项目中内置了arthas-spring-boot-starter。[官方文档](https://arthas.aliyun.com/)                             |
| 辅助工具                                                                        | [Hutool](https://www.hutool.cn/)、Lombok                              | Hutool是一个小而全的Java工具类库，更贴近国人使用习惯，避免重复定义很多相似的工具类。                                                                      |
| 数据库连接池                                                                      | HikariCP                                                             | SpringBoot内置数据库连接池，请永远相信官方的技术选型                                                                                      |
| 校验框架                                                                        | Validation                                                           | SpringBoot内置校验框架spring-boot-starter-validation，支持自定义校验，足够满足常见校验场景                                                    |
| Json序列化                                                                     | Jackson                                                              | SpringBoot内置Json序列化框架，请永远相信官方的技术选型                                                                                   |

## 特别鸣谢

* 感谢优秀开源项目[RuoYi](https://gitee.com/y_project/RuoYi)、[RuoYi-Vue-Plus](https://gitee.com/dromara/RuoYi-Vue-Plus)
  及其作者，这个项目在设计开发时有参照过上述开源项目，有些代码甚至是先拿过来再重构的，由于是第一次开源项目没有经验，就连README的编写以及项目文档的搭建也是模仿他们的样子，感谢先行者们照亮后人路！