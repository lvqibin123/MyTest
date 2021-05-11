package com.lqb.mytest.util.okHtpps;

import com.ejlchina.okhttps.HTTP;
import com.ejlchina.okhttps.HttpResult;
import com.ejlchina.okhttps.HttpTask;
import com.ejlchina.okhttps.Preprocessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzh.nonview.router.Router;
import com.mingle.widget.ShapeLoadingDialog;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.logging.Handler;

public class HttpUtils {


    static final HiLogLabel LOG = new HiLogLabel(HiLog.LOG_APP, 0xfff01, "MY_TEST_HTTP_UTILS");

    private   ShapeLoadingDialog loading  = null;

    private static Context context;

    private static Ability ability;

    public HttpUtils(Context context,Ability ability) {
        this.context = context;
        this.ability = ability;
    }

    private static HTTP http;


    /**
     * 配置HttpUtils持有的HTTP实例（不调用此方法前默认使用一个没有没有经过任何配置的HTTP懒实例）
     * @param http HTTP实例
     */
    public static void of(HTTP http) {
        if (http != null) {
            HttpUtils.http = http;
        }
    }


    public synchronized HTTP getHttp() {
        if (http != null) {
            return http;
        }
        http = HTTP.builder().baseUrl(Urls.BASE_URL)
                .callbackExecutor(run -> ability.getMainTaskDispatcher().delayDispatch(run , 10)) // 这个地方一定要加上，不然异步请求数据时候不在同一线程，访问组件时候会出错
                .config( builder -> builder.addInterceptor(chain -> { // 进行tokon校验，对数据进行处理
                    Response res = chain.proceed(chain.request());
                    ResponseBody body = res.body();
                    byte[] bodyBate = body.bytes();
                    MediaType contentType = body.contentType();
//                    HiLog.debug(LOG, " body  = " + body);
                    ResponseBody newBody = null;
                    ResponseBody oldBody = null;
                    if (body != null) {
                        newBody = ResponseBody.create(contentType, bodyBate);
                        oldBody = ResponseBody.create(contentType, bodyBate);
                    }
//                    HiLog.debug(LOG, " newBody  = " + newBody);
//                    HiLog.debug(LOG, " oldBody  = " + oldBody);
                    String dodySting = oldBody.string();
//                    HiLog.debug(LOG, "dodySting  = " + dodySting);
//                    HiLog.debug(LOG, " isNull  = " + (StringUtils.isNotBlank(dodySting) ));
//                    HiLog.debug(LOG, " isFindCode  = " + ( dodySting.indexOf("code") !=-1 ));
                    if (StringUtils.isNotBlank(dodySting) && dodySting.indexOf("code") !=-1 ) {
                        try{
                            Gson gs = new GsonBuilder()
                                    .setPrettyPrinting()
                                    .create();

                            ResponseData responseData = gs.fromJson(dodySting, new TypeToken<ResponseData>() {}.getType());
                            HiLog.debug(LOG, " responseData =  " + responseData.toString());
                            if (responseData.getCode() == 1002) {
                                HiLog.debug(LOG, "token验证失败，请重新登录 ! ");
//                                try {
//                                    new ToastDialog(context)
//                                            .setText("token验证失败，请重新登录")
//                                            .setTitleText("提示")
//                                            .setAlignment(LayoutAlignment.CENTER)
//                                            .show();
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                    HiLog.error(LOG, " e  = " + e.toString());
//                                }
                                // 第一种返回登录页面的方式
//                                Intent intent = new Intent();
//                                // 通过Intent中的OperationBuilder类构造operation对象，指定设备标识（空串表示当前设备）、应用包名、Ability名称
//                                Operation operation = new Intent.OperationBuilder()
//                                        .withAction("action.system.login")
//                                        .build();
//                                // 把operation设置到intent中
//                                intent.setOperation(operation);
//                                ability.startAbility(intent);
                                // 第二种返回登录页面的方式
                                Router.create("haoge://page/login").open(ability);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            HiLog.error(LOG, " e  = " + e.toString());
                        }
                    }
                    return res.newBuilder().body(newBody).build();
                }))
                .addPreprocessor((Preprocessor.PreChain chain) -> {
                    HttpTask<?> task = chain.getTask();// 获得当前的HTTP任务
                    DatabaseHelper databaseHelper = new DatabaseHelper(context); // context入参类型为ohos.app.Context。
                    String fileName = "SYSTEM_USER"; // fileName表示文件名，其取值不能为空，也不能包含路径，默认存储目录可以通过context.getPreferencesDir()获取。
                    Preferences preferences = databaseHelper.getPreferences(fileName);
                    String token  = preferences.getString("token", "");
                    task.addHeader("token", token);// 为任务添加头信息
                    // 根据标签判断是否显示加载框
//                    if (task.isTagged(Tags.LOADING)) {
//                        try {
//                            showLoading(context);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                            HiLog.error(LOG, " e  = " + e.toString());
//                        }
//                    }
                    chain.proceed();
                })
//                .responseListener((HttpTask<?> task, HttpResult result) -> {
//                    // 所有异步请求（包括 WebSocket）响应后都会走这里
//
//                    // 返回 true 表示继续执行 task 的 OnResponse 回调，
//                    // 返回 false 则表示不再执行，即 阻断
////                    HttpResult.Body  body = result.getBody();
////                    HiLog.debug(LOG, "body  = " + body);
//                    String dodySting = result.getBody().toString();
//                    HiLog.debug(LOG, "dodySting  = " + dodySting);
////                    HiLog.debug(LOG, "body!=null  = " + (body!=null));
//                    if (StringUtils.isNotBlank(dodySting) && dodySting.indexOf("code") !=-1 ) {
////                        HiLog.debug(LOG, "dodySting  = " + dodySting);
//                        try{
//                            Gson gs = new GsonBuilder()
//                                    .setPrettyPrinting()
//                                    .create();
//
//                            ResponseData responseData = gs.fromJson(dodySting, new TypeToken<ResponseData>() {}.getType());
////                            HiLog.debug(LOG, "responseData  = " + responseData);
//                            if (responseData.getCode() == 1002) {
//
////                                HiLog.debug(LOG, "responseData.getCode()  = " + responseData.getCode());
//                                HiLog.error(LOG, "token验证失败，请重新登录 ! ");
////                                try {
////                                    new ToastDialog(context)
////                                            .setText("token验证失败，请重新登录")
////                                            .setTitleText("提示")
////                                            .setAlignment(LayoutAlignment.CENTER)
////                                            .show();
////                                }catch (Exception e){
////                                    e.printStackTrace();
////                                    HiLog.error(LOG, " e  = " + e.toString());
////                                }
//
////                                ability.addActionRoute("action.system.sssasass", LoginAbilitySlice.class.getName()); //这种方式不能跳转
//                                /**
//                                 * 步骤为：1.在com.lqb.mytest 包下面新建一LoginAbility
//                                 *        2. 在 config.json 添加登录的ability 如下代码
//                                 *            "abilities": [
//                                 *       {
//                                 *         "skills": [
//                                 *           {
//                                 *             "entities": [
//                                 *               "entity.system.home"
//                                 *             ],
//                                 *             "actions": [
//                                 *               "action.system.home"
//                                 *             ]
//                                 *           }
//                                 *         ],
//                                 *         "orientation": "unspecified",
//                                 *         "name": "com.lqb.mytest.MainAbility",
//                                 *         "icon": "$media:icon",
//                                 *         "description": "$string:mainability_description",
//                                 *         "label": "$string:app_name",
//                                 *         "type": "page",
//                                 *         "launchType": "standard"
//                                 *       },
//                                 *       添加登录的ability
//                                 *       {
//                                 *         "skills": [
//                                 *           {
//                                 *             "entities": [
//                                 *               "entity.system.login"
//                                 *             ],
//                                 *             "actions": [
//                                 *               "action.system.login"
//                                 *             ]
//                                 *           }
//                                 *         ],
//                                 *         "orientation": "unspecified",
//                                 *         "name": "com.lqb.mytest.LoginAbility",
//                                 *         "icon": "$media:icon",
//                                 *         "description": "$string:mainability_description",
//                                 *         "label": "$string:app_name",
//                                 *         "type": "page",
//                                 *         "launchType": "standard"
//                                 *       }
//                                 *     ]
//                                 */
//                                Intent intent = new Intent();
//                                // 通过Intent中的OperationBuilder类构造operation对象，指定设备标识（空串表示当前设备）、应用包名、Ability名称
//                                Operation operation = new Intent.OperationBuilder()
//                                        .withAction("action.system.login")
//                                        .build();
//                                // 把operation设置到intent中
//                                intent.setOperation(operation);
//                                ability.startAbility(intent);
//                                return false;
//                            } else {
//                                return true;
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                            HiLog.error(LOG, " e  = " + e.toString());
//                        }
//
//                    }
////                    try{
//////                        AbilityInfo.AbilityType abilityType  = context.getAbilityInfo().getType();
//////                        abilityType.
//////                        BasisAbilitySlice basisAbilitySlice = new BasisAbilitySlice();
//////                        addActionRoute("action.system.ssswss", LoginAbilitySlice.class.getName());
//////                        AbilitySlice.present(new LoginAbilitySlice(), new Intent());
//////                        HiLog.debug(LOG, "basisAbilitySlice  = " + basisAbilitySlice.toString());
//////                        abilitySlice.present(new LoginAbilitySlice(), new Intent());
//////                        HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 111111 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//////                        ability.setMainRoute(MainAbilitySlice.class.getName());
//////                        ability.addActionRoute("action.system.login", LoginAbilitySlice.class.getName());
//////                        Intent intent = new Intent();
//////                        ability.setResult(0,intent);
//////                        HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 222222 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//////                        HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 111111 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//////                        AbilitySlice abilitySlice = new AbilitySlice();
//////                        abilitySlice.present(new LoginAbilitySlice(), new Intent());
//////                        HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 222222 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
////                        HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 111111 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//////                        Intent intent = new Intent();
//////                        Operation operation = new Intent.OperationBuilder()
//////                                .withAction("action.system.login")
//////                                .build();
//////                        intent.setOperation(operation);
//////                        ability.startAbilityForResult(intent, 0);
//////                        ability.addActionRoute("action.system.login", LoginAbilitySlice.class.getName());
////                        Intent intent = new Intent();
////
////// 通过Intent中的OperationBuilder类构造operation对象，指定设备标识（空串表示当前设备）、应用包名、Ability名称
////                        Operation operation = new Intent.OperationBuilder()
////                                .withAction("action.system.login")
////                                .build();
////
////// 把operation设置到intent中
////                        intent.setOperation(operation);
////                        ability.startAbility(intent);
////                        HiLog.debug(LOG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 222222 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
////                    }catch (Exception e){
////                        e.printStackTrace();
////                        HiLog.error(LOG, "e  = " + e);
////                    }
//                    return true;
//                })
                .completeListener((HttpTask<?> task, HttpResult.State state) -> {
                    // 所有异步请求（包括 WebSocket）执行完都会走这里

                    // 返回 true 表示继续执行 task 的 OnComplete 回调，
                    // 返回 false 则表示不再执行，即 阻断
//                    if (task.isTagged(Tags.LOADING)) {
//                        hideLoading();          // 关闭加载框
//                    }
                    return true;
                })
                .exceptionListener((HttpTask<?> task, IOException error) -> {
                    // 所有异步请求（包括 WebSocket）发生异常都会走这里

                    // 返回 true 表示继续执行 task 的 OnException 回调，
                    // 返回 false 则表示不再执行，即 阻断
                    return true;
                })
        .build();
        return http;
    }

    // 显示加载框
    private void showLoading(Context ctx) {
        if (loading == null) {
            // 这里就用 ProgressDialog 来演示了，当然可以替换成你喜爱的加载框
            loading = new ShapeLoadingDialog.Builder(ctx)
                    .loadText("加载中...") // 设置加载中的文本
                    .cancelable(false) // 当用户点击返回键时是否消失Dialog
                    .canceledOnTouchOutside(true) // 当用户点击Dialog外部时是否消失Dialog
                    .build();
        }
        loading.show();
    }

    // 关闭加载框
    private void hideLoading() {
        // 判断是否所有显示加载框的接口都已完成
        if ( loading != null) {
            loading.destroy();
            loading = null;
        }
    }
}
