package com.lqb.mytest;

import com.lzh.nonview.router.RouterConfiguration;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

/**
 * 基类
 *
 * @author:wjt
 * @since 2021-03-20
 */
public class BaseAbility extends Ability {
    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        super.onAbilityResult(requestCode, resultCode, resultData);
        RouterConfiguration.get().dispatchActivityResult(this, requestCode, resultCode, resultData);
    }
}
