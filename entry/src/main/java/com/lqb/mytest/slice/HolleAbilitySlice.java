package com.lqb.mytest.slice;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.HttpResult.State;
import com.github.ybq.core.style.DoubleBounce;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lqb.mytest.ResourceTable;
import com.lqb.mytest.util.Message;
import com.lqb.mytest.util.okHtpps.HttpUtils;
import com.mingle.widget.ShapeLoadingDialog;
import com.mingle.widget.ShapeLoadingDialog.Builder;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ohos.hiviewdfx.HiLog.debug;
import static ohos.hiviewdfx.HiLog.error;

public class HolleAbilitySlice extends BasisAbilitySlice {

    static final HiLogLabel LOG = new HiLogLabel(HiLog.LOG_APP, 0xfff01, "MY_TEST_HELLO");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_holle);
    }
}
