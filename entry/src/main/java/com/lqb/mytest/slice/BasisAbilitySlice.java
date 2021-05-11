package com.lqb.mytest.slice;

import com.lzh.nonview.router.Router;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class BasisAbilitySlice extends AbilitySlice {


    static final HiLogLabel LOG = new HiLogLabel(HiLog.LOG_APP, 0xfff01, "MY_TEST_BASIS");

    public final void gotoLogin(Ability ability){
//        MainAbility mainAbility = new MainAbility();
//        try {
//            mainAbility.terminateAbility();
//        } catch (Exception e){
//            e.printStackTrace();
//            HiLog.error(LOG, "e  = " + e);
//        }
//        try {
//            ability.terminateAbility();
//        } catch (Exception e){
//            e.printStackTrace();
//            HiLog.error(LOG, "e  = " + e);
//        }
//        present(new LoginAbilitySlice(), new Intent());
        Router.create("haoge://page/login").open(getAbility());
    }
    public final void gotoHome(){
        Router.create("haoge://page/home").open(getAbility());
//        present(new MainAbilitySlice(), new Intent());
    }
}
