package com.lqb.mytest.interceptors;

import com.lqb.mytest.manager.DataManager;
import com.lzh.nonview.router.extras.RouteBundleExtras;
import com.lzh.nonview.router.interceptors.RouteInterceptor;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.utils.net.Uri;

/**
 * 默认拦截器。所有路由(除掉直接以浏览器方式打开的路由)均会触发此拦截器可以用作登录开关控制：为登录拦截添加动态登录控制
 *
 * @author:wjt
 * @since 2021-03-20
 **/
public class DefaultInterceptor implements RouteInterceptor {
    @Override
    public boolean intercept(Uri uri, RouteBundleExtras extras, Ability context) {
        return checkRequestLogin(uri) && !DataManager.isIsLogin(context);
    }

    @Override
    public void onIntercepted(Uri uri, RouteBundleExtras extras, Ability context) {
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.lqb.mytest")
                .withAbilityName("com.lqb.mytest.LoginAbility")
                .build();
        Intent intent = new Intent();
        intent.setParam("uri", uri);
        intent.setParam("extras", extras);
        intent.setParam("extrasIntentPararms", extras.getExtras());
        intent.setOperation(operation);
        context.startAbility(intent);
    }

    /**
     * 判断是否为要求进行登录检查的路由链接。
     *
     * @param uri Uri
     * @return 是否为要求进行登录检查的路由链接。
     */
    private boolean checkRequestLogin(Uri uri) {
        return uri.getFirstQueryParamByKey("requestLogin").equals("1");
    }
}
