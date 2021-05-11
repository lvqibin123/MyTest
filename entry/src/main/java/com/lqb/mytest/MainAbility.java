package com.lqb.mytest;

import com.lqb.mytest.interceptors.LoginInterceptor;
import com.lqb.mytest.slice.MainAbilitySlice;
import com.lzh.nonview.router.anno.RouteInterceptors;
import com.lzh.nonview.router.anno.RouterRule;
import ohos.aafwk.content.Intent;

@RouteInterceptors(LoginInterceptor.class)// 指定所有往此页面跳转的路由，均要进行登录检查
@RouterRule("home")
public class MainAbility extends BaseAbility {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }

}
