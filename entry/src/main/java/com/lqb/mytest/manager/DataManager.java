package com.lqb.mytest.manager;

import ohos.aafwk.ability.Ability;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import org.apache.commons.lang3.StringUtils;

import static ohos.hiviewdfx.HiLog.debug;

/**
 * 一个本地的内存数据管理容器。提供登录状态进行使用
 *
 * @author:wjt
 * @since 2021-03-20
 */
public class DataManager {
    /**
     * username
     */
    private static String username = "HaogeStudio";

    /**
     * 构造函数
     */
    private DataManager() {
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DataManager.username = username;
    }

    public static boolean isIsLogin(Ability context) {

        DatabaseHelper databaseHelper = new DatabaseHelper(context); // context入参类型为ohos.app.Context。
        String fileName = "SYSTEM_USER"; // fileName表示文件名，其取值不能为空，也不能包含路径，默认存储目录可以通过context.getPreferencesDir()获取。
        Preferences preferences = databaseHelper.getPreferences(fileName);
        String token  = preferences.getString("token", "");
        return StringUtils.isNotEmpty(token);
    }
}
