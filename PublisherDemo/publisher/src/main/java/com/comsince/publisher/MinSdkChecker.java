package com.comsince.publisher;

import android.os.Build;

public class MinSdkChecker {

	public static boolean isSupportNotificationBuild(){
		return Build.VERSION.SDK_INT >= 16;
	}
	public static boolean isSupportDeviceDefaultLight(){
		return Build.VERSION.SDK_INT >= 14;
	}
	public static boolean isSupportBigTextStyleAndAction(){
		return Build.VERSION.SDK_INT >= 16;
	}
	public static boolean isSupportKeyguardState(){
		return Build.VERSION.SDK_INT >= 16;
	}

    public static boolean isSupportSendNotification(){
        return Build.VERSION.SDK_INT >= 21;
    }

	public static boolean isSupportVideoNotification(){
		return Build.VERSION.SDK_INT >= 19;
	}

	public static boolean isSupportSetDrawableSmallIcon() {
		return Build.VERSION.SDK_INT >= 23;
	}
}
