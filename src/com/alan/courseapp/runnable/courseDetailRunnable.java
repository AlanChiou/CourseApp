package com.alan.courseapp.runnable;

import java.util.ArrayList;

import android.os.Handler;

import com.alan.courseapp.utility.NportalConnector;

public class courseDetailRunnable implements Runnable {

	public static final int REFRESH_DATA = 0x00000001;
	private String courseNo;
	Handler handler;

	// �غc�l�A�]�w�n�Ǫ��r��
	public courseDetailRunnable(Handler handler, String courseNo) {
		this.courseNo = courseNo;
		this.handler = handler;
	}

	@Override
	public void run() {
		ArrayList<String> result = NportalConnector.GetCourseDetail(courseNo);
		handler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}