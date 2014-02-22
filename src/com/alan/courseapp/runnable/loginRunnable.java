package com.alan.courseapp.runnable;

import android.os.Handler;

import com.alan.courseapp.utility.NportalConnector;

public class loginRunnable implements Runnable {

	public static final int REFRESH_DATA = 0x00000001;
	String muid;
	String mpassword;
	String authcode;
	Handler handler;
	// 建構子，設定要傳的字串
	public loginRunnable(String muid, String mpassword, String authcode,Handler handler) {
		this.handler = handler;
		this.muid = muid;
		this.mpassword = mpassword;
		this.authcode = authcode;
	}

	@Override
	public void run() {
		String result = NportalConnector.login(muid, mpassword, authcode);
		handler.obtainMessage(REFRESH_DATA, result).sendToTarget();
	}
}