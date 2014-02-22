package com.alan.courseapp.runnable;

import android.graphics.Bitmap;
import android.os.Handler;

import com.alan.courseapp.utility.NportalConnector;

public class imageRunnable implements Runnable {

	public static final int REFRESH_DATA = 0x00000001;

	Handler handler;
	// �غc�l�A�]�w�n�Ǫ��r��
	public imageRunnable(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		Bitmap result = NportalConnector.LoadImageFromWebOperations();
		handler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}