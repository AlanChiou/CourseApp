package com.alan.courseapp;

import com.alan.courseapp.utility.NportalConnector;
import com.alan.courseapp.utility.Utility;
import com.example.courseapp.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginSettingActivity extends Activity {

	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_setting);

		Button savebtn = (Button)findViewById(R.id.save_button);
		savebtn.setOnClickListener(clis);
		Button cancelbtn = (Button)findViewById(R.id.cancel_button);
		cancelbtn.setOnClickListener(clis);
		readSettings();
	}

	private OnClickListener clis = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.save_button:
				boolean result = writeSettings();
				if(result)
				{
					NportalConnector.isLogin = false;
					finish();
				}
				break;

			case R.id.cancel_button:
				finish();
				break;

			}
		}
	};

	private boolean writeSettings() {

		EditText idtext = (EditText) findViewById(R.id.idText);
		String id = idtext.getText().toString();
		EditText passwdtext = (EditText) findViewById(R.id.passWDText);
		String passwd = passwdtext.getText().toString();
		if (id.length() > 0 && passwd.length() > 0) {
			settings = getSharedPreferences("CourseApp", 0);
			settings.edit().putString("id", id).commit();
			settings.edit().putString("passwd", passwd).commit();
			return true;
		} else {
			Utility.ShowDialog("警告", "請確認帳號密碼是否正確！", this);
			return false;
		}
	}

	private boolean readSettings() {

		settings = getSharedPreferences("CourseApp", 0);
		String id = settings.getString("id", "");
		String passwd = settings.getString("passwd", "");
		if (id.length() > 0 && passwd.length() > 0) {
			EditText idtext = (EditText) findViewById(R.id.idText);
			EditText passwdtext = (EditText) findViewById(R.id.passWDText);
			idtext.setText(id);
			passwdtext.setText(passwd);
			return true;
		} else {
			return false;
		}
	}
}
