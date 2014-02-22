package com.alan.courseapp.runnable;

import android.os.Handler;

import com.alan.courseapp.data.StudentCourse;
import com.alan.courseapp.utility.NportalConnector;

public class courseRunnable implements Runnable {

	public static final int REFRESH_DATA = 0x00000001;
	private String sid;
	private String year;
	private String semester;
	Handler handler;
	// 建構子，設定要傳的字串
	public courseRunnable(Handler handler,String sid, String year,
			String semester) {
		this.sid = sid;
		this.year = year;
		this.semester = semester;
		this.handler = handler;
	}

	@Override
	public void run() {
		StudentCourse result = NportalConnector.getStudentCourse(sid, year, semester);
		handler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}