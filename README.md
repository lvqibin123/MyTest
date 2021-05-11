# HarmonyOS 的测试
## 项目介绍
        改项目已经实现http请求，请求时候在其头部默认添加token，用于后端token验证
    对后端返回的token进行拦截，如果token过期，将路由到登录页面。
       路由端登录拦截的实现，没有登录路由到登录页面。
       登录成功之后用户数据保存在本地，使用的时候从本地数据库取出。
        现在只要有三个页面：
            com.lqb.mytest.MainAbility 项目的主页面
            com.lqb.mytest.LoginAbility 项目的登录页面
            com.lqb.mytest.HolleAbility 项目的路由测试页面
##用到的技术：
###lombok
    注意点： 必须先安装 lombok插件
    使用它的好处：可以省略get set toString 等常用的胶水代码
### gson google 提供的序列化和反序列化的包
    特点：进行序列化的时候很灵活，并且比较稳定
### okhttps 提供http访问的包
    特点：使用简单
### router 路由技术
    