package com.lqb.mytest;

import com.lqb.mytest.slice.LoginAbilitySlice;
import com.lzh.nonview.router.anno.RouterRule;
import ohos.aafwk.content.Intent;

@RouterRule("login")
public class LoginAbility extends BaseAbility {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(LoginAbilitySlice.class.getName());
//        super.addActionRoute("action.system.login", LoginAbilitySlice.class.getName());
    }

}
