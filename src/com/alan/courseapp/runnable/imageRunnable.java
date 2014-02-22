package com.alan.courseapp.runnable;

import android.graphics.Bitmap;
import android.os.Handler;

import com.alan.courseapp.utility.NportalConnector;

public class imageRunnable implements Runnable {

	public static final int REFRESH_DATA = 0x00000001;

	Handler handler;
	// 建構子，設定要傳的字串
	public imageRunnable(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		Bitmap result = NportalConnector.LoadImageFromWebOperations();
		handler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}