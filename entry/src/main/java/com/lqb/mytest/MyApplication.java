package com.lqb.mytest;

import com.lqb.mytest.interceptors.DefaultInterceptor;
import com.lzh.nonview.router.Router;
import com.lzh.nonview.router.RouterConfiguration;
import com.lzh.nonview.router.anno.RouteConfig;
import com.lzh.nonview.router.interceptors.RouteInterceptor;
import ohos.aafwk.ability.AbilityPackage;

@RouteConfig(baseUrl = "haoge://page/", pack = "com.haoge.studio")
public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();
        /**
         * 注册通过apt生成的路由表
         * new com.haoge.studio.RouterRuleCreator()这个是编译的时候自动生成的不用新建
         */
            RouterConfiguration.get().addRouteCreator(new com.haoge.studio.RouterRuleCreator());
        /**
         * 设置默认路由拦截器：所有路由跳转均会被触发(除了需要直接打开浏览器的链接)
         */
        //
        RouteInterceptor interceptor = new DefaultInterceptor();
            RouterConfiguration.get().setInterceptor(interceptor);
        Router.DEBUG = true;
    }
}
