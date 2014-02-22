package com.alan.courseapp.runnable;

import java.util.ArrayList;

import android.os.Handler;

import com.alan.courseapp.utility.NportalConnector;

public class classmateRunnable implements Runnable {

	public static final int REFRESH_DATA = 0x00000001;
	private String courseNo;
	Handler handler;

	// 建構子，設定要傳的字串
	public classmateRunnable(Handler handler, String courseNo) {
		this.courseNo = courseNo;
		this.handler = handler;
	}

	@Override
	public void run() {
		ArrayList<String> result = NportalConnector.GetClassmate(courseNo);
		handler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}