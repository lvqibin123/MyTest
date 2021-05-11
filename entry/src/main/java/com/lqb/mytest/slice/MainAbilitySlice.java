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
import com.lqb.mytest.util.Status;
import com.lqb.mytest.util.User;
import com.lqb.mytest.util.okHtpps.HttpUtils;
import com.lzh.nonview.router.Router;
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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static ohos.hiviewdfx.HiLog.debug;
import static ohos.hiviewdfx.HiLog.error;

public class MainAbilitySlice extends BasisAbilitySlice {

    static final HiLogLabel LOG = new HiLogLabel(HiLog.LOG_APP, 0xfff01, "MY_TEST_MAIN");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
//        setMainRoute(MainAbilitySlice.class.getName());
//        super.setMainRoute(MainAbilitySlice.class.getName());
        Button gotoLoginButton = (Button) findComponentById(ResourceTable.Id_goto_login_but);

        Button testButton = (Button) findComponentById(ResourceTable.Id_test_but);


        Button testGotoLoginButton = (Button) findComponentById(ResourceTable.Id_test_goto_login_but);


        Button loginOutGotoLoginButton = (Button) findComponentById(ResourceTable.Id_login_out_goto_login_but);


        Button testGetDataButton = (Button) findComponentById(ResourceTable.Id_test_get_data_but);

        Button gotoHelloButton = (Button) findComponentById(ResourceTable.Id_goto_hello_but);

//        ProgressBar progressBar = (ProgressBar) findComponentById(ResourceTable.Id_pb);
//        Button spinKitButton = (Button) findComponentById(ResourceTable.Id_spin_kit_but);
//        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_list_container);
//        TableLayoutManager layoutManager = new TableLayoutManager();
//        layoutManager.setColumnCount(4);
//        listContainer.setLayoutManager(layoutManager);

        // 为按钮设置点击事件回调
        gotoLoginButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                // 此处添加点击按钮后的事件处理逻辑
                debug(LOG, "点击跳转到按钮了。");
                DatabaseHelper databaseHelper = new DatabaseHelper(getContext()); // context入参类型为ohos.app.Context。
                String fileName = "SYSTEM_USER"; // fileName表示文件名，其取值不能为空，也不能包含路径，默认存储目录可以通过context.getPreferencesDir()获取。
                Preferences preferences = databaseHelper.getPreferences(fileName);
                String id = preferences.getString("id", "");
                debug(LOG, "id = " + id );
                String email  = preferences.getString("email", "");
                debug(LOG, "email = " + email );
                String name  = preferences.getString("name", "");
                debug(LOG, "name = " + name );
                String token  = preferences.getString("token", "");
                debug(LOG, "token = " + token );
                String pwd  = preferences.getString("pwd", "");
                debug(LOG, "pwd = " + pwd );
                String loginName  = preferences.getString("loginName", "");
                debug(LOG, "loginName = " + loginName );
                boolean rememberMe  = preferences.getBoolean("rememberMe", false);
                debug(LOG, "rememberMe = " + rememberMe );
                boolean directLogin  = preferences.getBoolean("directLogin", false);
                debug(LOG, "directLogin = " + directLogin );
                Ability ability = getAbility();
//                terminateAbility();
                gotoLogin(ability);
            }
        });

        testButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                // 此处添加点击按钮后的事件处理逻辑
                debug(LOG, "点击测试按钮了。");
                try{
                    ShapeLoadingDialog shapeLoadingDialog = new ShapeLoadingDialog.Builder(getContext())
                            .loadText("加载中...") // 设置加载中的文本
                            .cancelable(false) // 当用户点击返回键时是否消失Dialog
                            .canceledOnTouchOutside(false) // 当用户点击Dialog外部时是否消失Dialog
                            .build();
                    shapeLoadingDialog.show();
                    Ability ability = getAbility();
//                    AbilitySlice abilitySlice = component.
                    HTTP http = new HttpUtils(getContext(),ability).getHttp();
                    Map<String, String> paramsMap = new HashMap<>();

                    paramsMap.put("userName", "system");
                    paramsMap.put("password", "123456");
                    http.async("/shool/sysPerson/loginSystemByHarmonyOS")
                            .addUrlPara(paramsMap)
                            .setOnResponse((HttpResult res) -> {
                                // 响应回调
                                HiLog.debug(LOG, "Get访问页面内容为：\n" + res);
                                Gson gs = new GsonBuilder()
                                        .setPrettyPrinting()
                                        .create();
                                Message massage = gs.fromJson(res.getBody().toString(), new TypeToken<Message>() {}.getType());
                                HiLog.debug(LOG, "massage ：\n" + massage.toString());
                                Integer status =  massage.getStatus();
                                HiLog.debug(LOG, "status ：\n" + status);
                                String ms =  massage.getMessage();
                                HiLog.debug(LOG, "ms ：\n" + ms);
                                if (status == 200) {
                                    String dataString = massage.getData().toString();
                                    HiLog.debug(LOG, "dataString ：\n" + dataString);
                                    try {
                                        Status data = gs.fromJson(dataString, new TypeToken<Status>() {
                                        }.getType());
                                        HiLog.debug(LOG, "data ：\n" + data.toString());
                                        String success = data.getSuccess();
                                        HiLog.debug(LOG, "success ：\n" + success);
                                        String msg = data.getMsg();
                                        HiLog.debug(LOG, "msg ：\n" + msg);
                                        if (StringUtils.isNotBlank(success) && "true".equals(success)) {
                                            new ToastDialog(getContext())
                                                    .setText(msg)
                                                    //                              .setTitleText("提示")
                                                    .setAlignment(LayoutAlignment.CENTER)
                                                    .show();
                                            HiLog.debug(LOG, msg);
                                            User user = data.getUser();
                                            DatabaseHelper databaseHelper = new DatabaseHelper(getContext()); // context入参类型为ohos.app.Context。
                                            String fileName = "SYSTEM_USER"; // fileName表示文件名，其取值不能为空，也不能包含路径，默认存储目录可以通过context.getPreferencesDir()获取。
                                            Preferences preferences = databaseHelper.getPreferences(fileName);
                                            preferences.putString("id", user.getId());
                                            preferences.putString("email", user.getEmail());
//                                                preferences.putString("photo", user.getPhoto());
                                            preferences.putString("name", user.getName());
                                            preferences.putString("token", user.getToken());
                                            preferences.putString("pwd", "123456");
                                            preferences.putString("loginName", user.getLoginName());
                                            preferences.putBoolean("rememberMe", true);
                                            preferences.putBoolean("directLogin", true);
                                            preferences.flushSync();
//                                            loginErrorPrompt.setVisibility(Component.HIDE);
                                            shapeLoadingDialog.destroy();
                                        } else {
                                            HiLog.debug(LOG, msg);
//                                            loginErrorPrompt.setVisibility(Component.VISIBLE);
//                                            loginErrorPrompt.setText(msg);
                                            shapeLoadingDialog.destroy();
                                            new ToastDialog(getContext())
                                                    .setText(msg)
                                                    .setAlignment(LayoutAlignment.CENTER)
                                                    .show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        HiLog.error(LOG, "e ：\n" + e);
                                    }
                                } else {
                                    HiLog.debug(LOG, massage.getMessage());
//                                    try{
//                                        loginErrorPrompt.setVisibility(Component.VISIBLE);
//                                        loginErrorPrompt.setText(massage.getMessage());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        HiLog.error(LOG, "e ：\n" + e);
//                                    }
//                                    HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 88888 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                                    try {
                                    shapeLoadingDialog.destroy();
                                    new ToastDialog(getContext())
                                            .setText(massage.getMessage())
                                            .setAlignment(LayoutAlignment.CENTER)
                                            .show();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        HiLog.error(LOG, "e ：\n" + e);
//                                    }
//                                    HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 99999 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                                }
                            })
                            .setOnException((IOException e) -> {
                                // 异常回调
                            })
                            .setOnComplete((State state) -> {
                                // 完成回调，无论成功失败都会执行，并且在 响应|异常回调 之前执行
                                // 可以根据 state 枚举判断执行状态:
                                // State.CANCELED`      请求被取消
                                // State.RESPONSED`     已收到响应
                                // State.TIMEOUT`       请求超时
                                // State.NETWORK_ERROR` 网络错误
                                // State.EXCEPTION`     其它请求异常
                            })
                            .get();
                } catch ( Exception e){
                    e.printStackTrace();
                    error(LOG, "e  = " + e);
                }
            }
        });

        testGotoLoginButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                // 此处添加点击按钮后的事件处理逻辑
                debug(LOG, "点击测试跳转登录页面按钮了。");
                try {
                    debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 3333333 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    Ability ability = getAbility();
                    Intent intent = new Intent();

//                    Operation operation = new Intent.OperationBuilder()
////                            .withAbilityName("action.system.login")
//                            .withAction("action.system.login")
////                            .withBundleName("com.lqb.mytest.slice")
////                            .withAbilityName(LoginAbilitySlice.class.getName())
//
//                            .build();
                    // 通过Intent中的OperationBuilder类构造operation对象，指定设备标识（空串表示当前设备）、应用包名、Ability名称
//                    Operation operation = new Intent.OperationBuilder()
//                            .withAbilityName(LoginAbilitySlice.class.getName())
//                            .build();
                    Operation operation = new Intent.OperationBuilder()
                            .withAction("action.system.login")
                            .build();

                    intent.setOperation(operation);
                    ability.startAbility(intent);
//                    ability.addActionRoute("action.system.login", LoginAbilitySlice.class.getName());
//                    ability.addActionRoute("action.system.login", LoginAbilitySlice.class.getName());
//                    BasisAbilitySlice basisAbilitySlice  =
//                    basisAbilitySlice.gotoLogin();
//                    HiLog.debug(LOG, " abilitySlice = " + abilitySlice.toString());

//                    abilitySlice.gotoLogin();
//                    abilitySlice.presentForResult(new LoginAbilitySlice(), new Intent(), 0);
                    debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 444444 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }catch (Exception e){
                    e.printStackTrace();
                    error(LOG, "e  = " + e);
                }

            }
        });
        // 为按钮设置点击事件回调
        loginOutGotoLoginButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                debug(LOG, "点击清除用户数据，跳转到登录页面按钮了。");
                DatabaseHelper databaseHelper = new DatabaseHelper(getContext()); // context入参类型为ohos.app.Context。
                String fileName = "SYSTEM_USER"; // fileName表示文件名，其取值不能为空，也不能包含路径，默认存储目录可以通过context.getPreferencesDir()获取。
                Preferences preferences = databaseHelper.getPreferences(fileName);
                preferences.putString("id", "");
                preferences.putString("email", "");
                preferences.putString("name", "");
//                preferences.putString("photo", "");
                preferences.putString("token", "");
                preferences.putString("pwd", "");
                preferences.putString("loginName", "");
                preferences.putBoolean("rememberMe", false);
                preferences.putBoolean("directLogin", false);
                Ability ability = getAbility();
                gotoLogin(ability);
//                this.terminateAbility();
            }
        });

        testGetDataButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                debug(LOG, "点击测试网络请求数据按钮了。");
                ShapeLoadingDialog shapeLoadingDialog = new Builder(getContext())
                        .loadText("加载中...") // 设置加载中的文本
                        .cancelable(false) // 当用户点击返回键时是否消失Dialog
                        .canceledOnTouchOutside(false) // 当用户点击Dialog外部时是否消失Dialog
                        .build();
                shapeLoadingDialog.show();
                try{
                    Ability ability = getAbility();
                    HTTP http = new HttpUtils(getContext(),ability).getHttp();
                    Map<String, String> paramsMap = new HashMap<>();

                    paramsMap.put("pi", "1");
                    paramsMap.put("ps", "1000");
                    paramsMap.put("keyWord", "");
                    http.async("/shool/sysLog/search")
                            .addUrlPara(paramsMap)
                            .setOnResponse((HttpResult res) -> {
                                // 响应回调
                                try {
                                    debug(LOG, "res.getBody() ：\n" + res.getBody());
                                    shapeLoadingDialog.destroy();
                                } catch (Exception e){
                                    e.printStackTrace();
                                    error(LOG, "e  = " + e);
                                }
                            })
                            .setOnException((IOException e) -> {
                                shapeLoadingDialog.destroy();
                                // 异常回调
                            })
                            .setOnComplete((State state) -> {
                                // 完成回调，无论成功失败都会执行，并且在 响应|异常回调 之前执行
                                // 可以根据 state 枚举判断执行状态:
                                // State.CANCELED`      请求被取消
                                // State.RESPONSED`     已收到响应
                                // State.TIMEOUT`       请求超时
                                // State.NETWORK_ERROR` 网络错误
                                // State.EXCEPTION`     其它请求异常
                                shapeLoadingDialog.destroy();
                            })
                            .get();
                } catch ( Exception e){
                    e.printStackTrace();
                    error(LOG, "e  = " + e);
                }
            }
        });

//        DoubleBounce doubleBounce = new DoubleBounce();
//        doubleBounce.setPaintColor(0XFF1AAF5D);
//        doubleBounce.onBoundsChange(0, 0, progressBar.getWidth(), progressBar.getHeight());
//        doubleBounce.setComponent(progressBar);
//        progressBar.setProgressElement(doubleBounce);
//        progressBar.setIndeterminate(true);
//        progressBar.addDrawTask((component, canvas) -> doubleBounce.drawToCanvas(canvas));

//        spinKitButton.setClickedListener(new Component.ClickedListener() {
//            @Override
//            public void onClick(Component component) {
////                listContainer.setVisibility(Component.HIDE);
//                ShapeLoadingDialog shapeLoadingDialog = new ShapeLoadingDialog.Builder(getContext())
//                        .loadText("加载中...") // 设置加载中的文本
//                        .cancelable(false) // 当用户点击返回键时是否消失Dialog
//                        .canceledOnTouchOutside(true) // 当用户点击Dialog外部时是否消失Dialog
//                        .build();
//                shapeLoadingDialog.show(); // 显示Dialog
////                debug(LOG, "点击测试网络请求数据按钮了。");
////                ShapeLoadingDialog shapeLoadingDialog = new Builder(getContext())
////                        .loadText("加载中...") // 设置加载中的文本
////                        .cancelable(false) // 当用户点击返回键时是否消失Dialog
////                        .canceledOnTouchOutside(false) // 当用户点击Dialog外部时是否消失Dialog
////                        .build();
////                shapeLoadingDialog.show();
////                try {
////                    Ability ability = getAbility();
////                    HTTP http = new HttpUtils(getContext(), ability).getHttp();
////                    Map<String, String> paramsMap = new HashMap<>();
////
////                    paramsMap.put("pi", "1");
////                    paramsMap.put("ps", "1000");
////                    paramsMap.put("keyWord", "");
////                    http.async("/shool/sysLog/search")
////                            .addUrlPara(paramsMap)
////                            .setOnResponse((HttpResult res) -> {
////                                // 响应回调
////                                try {
////                                    debug(LOG, "res.getBody() ：\n" + res.getBody());
////                                    shapeLoadingDialog.destroy();
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                    error(LOG, "e  = " + e);
////                                }
////                            })
////                            .setOnException((IOException e) -> {
////                                shapeLoadingDialog.destroy();
////                                // 异常回调
////                            })
////                            .setOnComplete((State state) -> {
////                                // 完成回调，无论成功失败都会执行，并且在 响应|异常回调 之前执行
////                                // 可以根据 state 枚举判断执行状态:
////                                // State.CANCELED`      请求被取消
////                                // State.RESPONSED`     已收到响应
////                                // State.TIMEOUT`       请求超时
////                                // State.NETWORK_ERROR` 网络错误
////                                // State.EXCEPTION`     其它请求异常
////                                shapeLoadingDialog.destroy();
////                            })
////                            .get();
////                } catch (Exception e) {
////                    e.printStackTrace();
////                    error(LOG, "e  = " + e);
////                }
//            }
//        });
        gotoHelloButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                debug(LOG, "点击跳转到hello页面按钮了。");
//                Router.create("http://www.baidu.com").open(getAbility());
                try {
                    Router.create("haoge://page/holle").open(getAbility());
                }catch (Exception e){
                    e.printStackTrace();
                    error(LOG, "e  = " + e);
                }
            }
        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    private static void showToastDialog(Context context,String msg) {
        new ToastDialog(context)
            .setText(msg)
            .setAlignment(LayoutAlignment.CENTER)
            .show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
