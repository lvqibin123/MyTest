package com.lqb.mytest.slice;

import com.daimajia.ohosanimations.library.Techniques;
import com.daimajia.ohosanimations.library.YoYo;
import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.OkHttps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lqb.mytest.ResourceTable;
import com.lqb.mytest.util.*;
import com.lqb.mytest.util.okHtpps.HttpUtils;
import com.mingle.widget.ShapeLoadingDialog;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginAbilitySlice extends BasisAbilitySlice {

    static final HiLogLabel LOG = new HiLogLabel(HiLog.LOG_APP, 0xfff01, "MY_TEST_LOGIN");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_login);

        // 此处如果是从 Intent 跳转则图片不会加载 ，没有找到具体原因
        YoYo.with(Techniques.SlideInLeft)
                .duration(3000)
                .delay(100)
                .playOn(findComponentById(ResourceTable.Id_top_img));

        Button loginButton = (Button) findComponentById(ResourceTable.Id_login_but);

        TextField userNameValue = (TextField) findComponentById(ResourceTable.Id_user_name_value);
        userNameValue.setBackground(null);

        TextField pwdValue = (TextField) findComponentById(ResourceTable.Id_pwd_value);
        pwdValue.setBackground(null);

        Switch rememberMeSwitch = (Switch) findComponentById(ResourceTable.Id_remember_me_value);


        Switch directLoginSwitch = (Switch) findComponentById(ResourceTable.Id_direct_login_value);

        ShapeElement errorElement = new ShapeElement(this, ResourceTable.Graphic_background_text_field_error);


        DirectionalLayout toastLayout = (DirectionalLayout) LayoutScatter.getInstance(this)
                .parse(ResourceTable.Layout_common_layout_toast, null, false);

        Text commonLayoutToastText = (Text) findComponentById(ResourceTable.Id_common_toast_dialog_toast);
        ToastDialog toastDialog =new ToastDialog(getContext())
                .setComponent(toastLayout)
                .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                .setAlignment(LayoutAlignment.CENTER);


        DatabaseHelper databaseHelper = new DatabaseHelper(getContext()); // context入参类型为ohos.app.Context。
        String fileName = "SYSTEM_USER"; // fileName表示文件名，其取值不能为空，也不能包含路径，默认存储目录可以通过context.getPreferencesDir()获取。
        Preferences preferences = databaseHelper.getPreferences(fileName);
        String oldPwd  = preferences.getString("pwd", "");
        HiLog.debug(LOG, "oldPwd = " + oldPwd );
        String oldLoginName  = preferences.getString("loginName", "");
        HiLog.debug(LOG, "oldLoginName = " + oldLoginName );
        boolean rememberMe  = preferences.getBoolean("rememberMe", false);
        HiLog.debug(LOG, "rememberMe = " + rememberMe );
        boolean directLogin  = preferences.getBoolean("directLogin", false);
        HiLog.debug(LOG, "directLogin = " + directLogin );
        if (rememberMe) {
            userNameValue.setText(oldLoginName);
            pwdValue.setText(oldPwd);
            rememberMeSwitch.setChecked(rememberMe);
            directLoginSwitch.setChecked(directLogin);
            if (directLoginSwitch.isChecked()) {
                if (StringUtils.isNotEmpty(oldLoginName) && StringUtils.isNotEmpty(oldPwd)) {
                    Map<String, String> paramsMap = new HashMap<>();

                    paramsMap.put("userName", oldLoginName);
                    paramsMap.put("password", oldPwd);
                    try{
                        Ability ability = getAbility();
                        HTTP http = new HttpUtils(getContext(),ability).getHttp();
                        http.async("/shool/sysPerson/loginSystemByHarmonyOS")
                                .addUrlPara(paramsMap)
                                .setOnResponse((HttpResult res) -> {
                                    // 响应回调
                                    Gson gs = new GsonBuilder()
                                            .setPrettyPrinting()
                                            .create();
                                    Message massage = gs.fromJson(res.getBody().toString(), new TypeToken<Message>() {}.getType());
                                    HiLog.debug(LOG, "massage ：\n" + massage.toString());
                                    String data = massage.getData().toString();
                                    HiLog.debug(LOG, "data ：\n" + data);
                                    Status status = gs.fromJson(data, new TypeToken<Status>() {}.getType());
                                    HiLog.debug(LOG, "status ：\n" + status.toString());
                                    String success = status.getSuccess();
                                    HiLog.debug(LOG, "success ：\n" + success);
                                    String msg = status.getMsg();
                                    HiLog.debug(LOG, "msg ：\n" + msg);
                                    if (StringUtils.isNotBlank(success) && "true".equals(success)) {
//                                        HiLog.debug(LOG, "登录成功 ：\n" + msg);
                                        new ToastDialog(getContext())
                                                .setText(msg)
        //                              .setTitleText("提示")
                                                .setAlignment(LayoutAlignment.CENTER)
                                                .show();
                                        HiLog.debug(LOG, msg);
//                                        loginErrorPrompt.setVisibility(Component.HIDE);
                                        gotoHome();
                                    } else {
                                        HiLog.debug(LOG, msg);
//                                        loginErrorPrompt.setVisibility(Component.VISIBLE);
//                                        loginErrorPrompt.setText(msg);
                                        new ToastDialog(getContext())
                                                .setText(msg)
        //                              .setTitleText("提示")
                                                .setAlignment(LayoutAlignment.CENTER)
                                                .show();
                                    }
                                })
                                .setOnException((IOException e) -> {
                                    // 异常回调
                                    HiLog.error(LOG, "Get访问出错：" + e.toString());
//                                    loginErrorPrompt.setVisibility(Component.VISIBLE);
//                                    loginErrorPrompt.setText("访问服务器错误");
                                    new ToastDialog(getContext())
                                            .setText("访问服务器失败！")
                                            .setAlignment(LayoutAlignment.CENTER)
                                            .show();
                                })
                                .get();
                    } catch ( Exception e){
                        e.printStackTrace();
                        HiLog.error(LOG, "e  = " + e);
                    }
                }  else {
                    HiLog.error(LOG, "用户名和密码不能为空。");
                    if (StringUtils.isBlank(oldLoginName)) {
                        userNameValue.setBackground(errorElement);
                    } else {
                        userNameValue.setBackground(null);
                    }
                    if (StringUtils.isBlank(oldPwd)) {
                        pwdValue.setBackground(errorElement);
                    }else {
                        pwdValue.setBackground(null);
                    }
                    new ToastDialog(getContext())
                            .setText("用户名和密码不能为空！")
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
            }
        } else {
            userNameValue.setText("");
            pwdValue.setText("");
            rememberMeSwitch.setChecked(false);
            directLoginSwitch.setChecked(false);
        }


//        ShapeElement commonLayoutToastTextErrorElement = new ShapeElement(this, ResourceTable.Graphic_background_common_toast_red_element);

        // 为按钮设置点击事件回调
        loginButton.setClickedListener(new Component.ClickedListener() {

            @Override
            public void onClick(Component component) {

                // 此处添加点击按钮后的事件处理逻辑
                HiLog.debug(LOG, "点击登录按钮了。");

                String userName = userNameValue.getText();
                String pwd = pwdValue.getText();

                if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(pwd)) {
                    ShapeLoadingDialog shapeLoadingDialog = new ShapeLoadingDialog.Builder(getContext())
                            .loadText("加载中...") // 设置加载中的文本
                            .cancelable(false) // 当用户点击返回键时是否消失Dialog
                            .canceledOnTouchOutside(false) // 当用户点击Dialog外部时是否消失Dialog
                            .build();
                    shapeLoadingDialog.show();
                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("userName", userName);
                    paramsMap.put("password", pwd);
                    Ability ability = getAbility();
                    HTTP http = new HttpUtils(getContext(),ability).getHttp();
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
                                            HiLog.debug(LOG, "rememberMeSwitch.isChecked() ：\n" + rememberMeSwitch.isChecked());
                                            if (rememberMeSwitch.isChecked()) {
                                                preferences.putString("id", user.getId());
                                                preferences.putString("email", user.getEmail());
//                                                preferences.putString("photo", user.getPhoto());
                                                preferences.putString("name", user.getName());
                                                preferences.putString("token", user.getToken());
                                                preferences.putString("pwd", pwd);
                                                preferences.putString("loginName", user.getLoginName());
                                                preferences.putBoolean("rememberMe", rememberMeSwitch.isChecked());
                                                HiLog.debug(LOG, "directLoginSwitch.isChecked() ：\n" + directLoginSwitch.isChecked());
                                                preferences.putBoolean("directLogin", directLoginSwitch.isChecked());
                                            }
                                            preferences.flushSync();
//                                            loginErrorPrompt.setVisibility(Component.HIDE);
                                            shapeLoadingDialog.destroy();
                                            gotoHome();
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
                                HiLog.error(LOG, "Get访问出错：" + e.toString());
//                                loginErrorPrompt.setVisibility(Component.VISIBLE);
//                                loginErrorPrompt.setText("访问服务器出错！");
                                shapeLoadingDialog.destroy();
                                new ToastDialog(getContext())
                                        .setText("访问服务器失败！")
                                        .setAlignment(LayoutAlignment.CENTER)
                                        .show();
                            })
                            .get();

//                    HiHttpUtil.get(url, paramsMap, new HiCallBack.HiCallBackString() {
//                        @Override
//                        public void onFailure(int code, String errorMessage) {
//                            HiLog.error(LOG, "Get访问出错：" + errorMessage);
//                            new ToastDialog(getContext())
//                                    .setText("访问服务器失败！")
////                              .setTitleText("提示")
//                                    .setAlignment(LayoutAlignment.CENTER)
//                                    .show();
//                        }
//
//                        @Override
//                        public void onResponse(String response) {
//                            HiLog.debug(LOG, "Get访问页面内容为：\n" + response);
//                            Gson gs = new GsonBuilder()
//                                    .setPrettyPrinting()
//                                    .create();
//                            Message massage = gs.fromJson(response, new TypeToken<Message>() {}.getType());
//                            HiLog.debug(LOG, "massage ：\n" + massage.toString());
//                            String data = massage.getData().toString();
//                            HiLog.debug(LOG, "data ：\n" + data);
//                            try {
//                                Status status = gs.fromJson(data, new TypeToken<Status>() {
//                                }.getType());
//                                HiLog.debug(LOG, "status ：\n" + status.toString());
//                                String success = status.getSuccess();
//                                HiLog.debug(LOG, "success ：\n" + success);
//                                String msg = status.getMsg();
//                                HiLog.debug(LOG, "msg ：\n" + msg);
//                                if (StringUtils.isNotBlank(success) && "true".equals(success)) {
//                                    new ToastDialog(getContext())
//                                            .setText(msg)
//    //                              .setTitleText("提示")
//                                            .setAlignment(LayoutAlignment.CENTER)
//                                            .show();
//                                    HiLog.debug(LOG, msg);
//                                    User user = status.getUser();
//                                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext()); // context入参类型为ohos.app.Context。
//                                    String fileName = "SYSTEM_USER"; // fileName表示文件名，其取值不能为空，也不能包含路径，默认存储目录可以通过context.getPreferencesDir()获取。
//                                    Preferences preferences = databaseHelper.getPreferences(fileName);
//                                    HiLog.debug(LOG, "rememberMeSwitch.isChecked() ：\n" + rememberMeSwitch.isChecked());
//                                    if (rememberMeSwitch.isChecked()) {
//                                        preferences.putString("id", user.getId());
//                                        preferences.putString("email", user.getEmail());
////                                        preferences.putString("photo", user.getPhoto());
//                                        preferences.putString("name", user.getName());
//                                        preferences.putString("token", user.getToken());
//                                        preferences.putString("pwd", pwd);
//                                        preferences.putString("loginName", user.getLoginName());
//                                        preferences.putBoolean("rememberMe", rememberMeSwitch.isChecked());
//                                        HiLog.debug(LOG, "directLoginSwitch.isChecked() ：\n" + directLoginSwitch.isChecked());
//                                        preferences.putBoolean("directLogin", directLoginSwitch.isChecked());
//                                    }
//                                    preferences.flushSync();
//                                    gotoHome();
//                                } else {
//                                    new ToastDialog(getContext())
//                                            .setText(msg)
//    //                              .setTitleText("提示")
//                                            .setAlignment(LayoutAlignment.CENTER)
//                                            .show();
//                                }
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                HiLog.error(LOG, "e ：\n" + e);
//                            }
//                        }
//                    });
                } else {
                    HiLog.error(LOG, "用户名和密码不能为空。");
                    if (StringUtils.isBlank(userName)) {
                        userNameValue.setBackground(errorElement);
                    } else {
                        userNameValue.setBackground(null);
                    }
                    if (StringUtils.isBlank(pwd)) {
                        pwdValue.setBackground(errorElement);
                    }else {
                        pwdValue.setBackground(null);
                    }
//                    CommonDialog dialog = new CommonDialog(component.getContext());
////                    dialog.setContentImage(ResourceTable.Media_icon);
//                    dialog.setTitleText("提示");
//                    dialog.setTitleSubText("温馨提示");
//                    dialog.setContentText("用户名和密码不能为空！");
//                    dialog.setButton(1, "确定", new IDialog.ClickedListener() {
//                        @Override
//                        public void onClick(IDialog iDialog, int i) {
//                            dialog.destroy();
//                        }
//                    });
//                    dialog.show();

//                    new ToastDialog(getContext())
//                            .setComponent(component)
//                            .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
//                            .setAlignment(LayoutAlignment.CENTER)
//                            .show();
//                    new ToastDialog(getContext())
//                            .setText("用户名和密码不能为空！")
//                            .setAlignment(LayoutAlignment.CENTER)
//                            .show();
//                    new ToastDialog(getContext())
//                            .setComponent(toastLayout)
//                            .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
//                            .setAlignment(LayoutAlignment.CENTER)
//                            .show();
//                    commonLayoutToastText.setText("用户名和密码不能为空！");
//                    commonLayoutToastText.setBackground(commonLayoutToastTextErrorElement);
//                    commonLayoutToastText.setBackground(commonLayoutToastTextErrorElement);
//                    toastDialog.show();
//                    toastDialog.destroy();
//                    commonLayoutToastText.setText("用户名和密码不能为空！");
                      new ToastDialog(getContext())
                        .setText("用户名和密码不能为空！")
//                              .setTitleText("提示")
                        .setAlignment(LayoutAlignment.CENTER)
                        .show();
                }
            }
        });

//        rememberMeSwitch.setCheckedStateChangedListener(new AbsButton.CheckedStateChangedListener() {
//            // 回调处理Switch状态改变事件
//            @Override
//            public void onCheckedChanged(AbsButton button, boolean isChecked) {
//                rememberMeValue = isChecked;
//            }
//        });
//
//        directLoginSwitch.setCheckedStateChangedListener(new AbsButton.CheckedStateChangedListener() {
//            // 回调处理Switch状态改变事件
//            @Override
//            public void onCheckedChanged(AbsButton button, boolean isChecked) {
//                directLoginValue = isChecked ? "" : "";
//            }
//        });

        userNameValue.setFocusChangedListener((component, isFocused) -> {

            String userName = userNameValue.getText();
            String pwd = pwdValue.getText();

            if (isFocused) {
                // 获取到焦点
                HiLog.debug(LOG, "用户名文本框获取到焦点。");
//                HiLog.debug(LOG, " userName  = " + userName);
//                HiLog.debug(LOG, " StringUtils.isBlank(userName)  = " + StringUtils.isBlank(userName));
                if (StringUtils.isBlank(userName)) {
                    userNameValue.setBackground(errorElement);
                } else {
                    userNameValue.setBackground(null);
                }
                if (StringUtils.isBlank(pwd)) {
                    pwdValue.setBackground(errorElement);
                }else {
                    pwdValue.setBackground(null);
                }
            } else {
                // 失去焦点
                HiLog.debug(LOG, "用户名文本框失去焦点。");
            }
        });

        pwdValue.setFocusChangedListener((component, isFocused) -> {
            String userName = userNameValue.getText();
            String pwd = pwdValue.getText();

            if (isFocused) {
                // 获取到焦点
                HiLog.debug(LOG, "密码文本框获取到焦点。");
                if (StringUtils.isBlank(userName)) {
                    userNameValue.setBackground(errorElement);
                } else {
                    userNameValue.setBackground(null);
                }
                if (StringUtils.isBlank(pwd)) {
                    pwdValue.setBackground(errorElement);
                }else {
                    pwdValue.setBackground(null);
                }
            } else {
                // 失去焦点
                HiLog.debug(LOG, "密码文本框失去焦点。");
            }
        });

//        Button gotoHomeButton = (Button) findComponentById(ResourceTable.Id_goto_home_but);
//        // 为按钮设置点击事件回调
//        gotoHomeButton.setClickedListener(new Component.ClickedListener() {
//            @Override
//            public void onClick(Component component) {
//                HiLog.debug(LOG, "点击返回首页按钮了。");
//                gotoHome();
//            }
//        });


//        Button loginBackButton = (Button) findComponentById(ResourceTable.Id_login_back_but);
//        // 为按钮设置点击事件回调
//        loginBackButton.setClickedListener(new Component.ClickedListener() {
//            @Override
//            public void onClick(Component component) {
//                HiLog.debug(LOG, "点击返回按钮了。");
//                gotoHome();
//            }
//        });
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case 0:
                HiLog.debug(LOG, "登录页面接收首页给的数据了。");
                return;
            default:
        }
    }
}
