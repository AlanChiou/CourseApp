package com.alan.courseapp.runnable;

import java.util.List;

import android.os.Handler;

import com.alan.courseapp.utility.NportalConnector;

public class semesterRunnable implements Runnable {

	public static final int REFRESH_DATA = 0x00000001;
	Handler handler;
	String sid = "";

	// �غc�l�A�]�w�n�Ǫ��r��
	public semesterRunnable(String sid, Handler handler) {
		this.handler = handler;
		this.sid = sid;
	}

	@Override
	public void run() {
		List<String> result = NportalConnector.getCourseSemesters(sid);
		handler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}