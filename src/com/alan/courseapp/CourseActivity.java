package com.alan.courseapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.alan.courseapp.data.CourseInfo;
import com.alan.courseapp.data.StudentCourse;
import com.alan.courseapp.runnable.courseRunnable;
import com.alan.courseapp.runnable.imageRunnable;
import com.alan.courseapp.runnable.loginRunnable;
import com.alan.courseapp.runnable.semesterRunnable;
import com.alan.courseapp.utility.NportalConnector;
import com.alan.courseapp.utility.OCR;
import com.alan.courseapp.utility.Utility;
import com.example.courseapp.R;
import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CourseActivity extends Activity {

	private ArrayList<String> semesters = new ArrayList<String>();
	private CourseTableLinearLayout courseTable;
	private Button searchButton;
	private EditText sidText;
	private StudentCourse studentCourse = null;
	private String sid = "";
	private String lastSid = "";
	private String year = "";
	private String semester = "";
	private SharedPreferences settings;
	private SemesterSpinner spinner;
	private ArrayAdapter<String> adapter;
	private ProgressDialog pd;
	private boolean isRefresh = false;
	private Thread nextThread = null;
	public String selectedCourseNo = "";

	private void ShowAlertDialog(String msg) {
		Utility.ShowDialog("警告", msg, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 2:
			if (resultCode == RESULT_OK) {
				sid = data.getStringExtra("sid");
				pd = ProgressDialog.show(this, null, "資料讀取中~", true);
				Thread student_t = new Thread(new courseRunnable(courseHandler,
						sid, year, semester));
				student_t.start();
				sidText.setText(sid);
			}
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course);

		spinner = (SemesterSpinner) findViewById(R.id.semester);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, semesters);
		spinner.setOnItemSelectedEvenIfUnchangedListener(semesterSelectedLis);
		setSpinnerCheck();
		spinner.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setPromptId(R.string.semester);

		initCourseTable();

		sidText = (EditText) findViewById(R.id.sidText);
		sidText.setLines(1);
		sidText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals(lastSid)) {
					isRefresh = false;
				} else {
					isRefresh = true;
				}
				setSpinnerCheck();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.searchButton) {
					closeSoftKeyboard();

					sid = sidText.getText().toString();
					if (sid.length() > 0) {
						lastSid = sid;
						if (NportalConnector.isLogin) {
							pd = ProgressDialog.show(CourseActivity.this, null,
									"學期資料讀取中~", true);
							Thread t = new Thread(new semesterRunnable(sid,
									semesterHandler));
							t.start();
						} else {
							pd = ProgressDialog.show(CourseActivity.this, null,
									"學期資料讀取中~", true);
							nextThread = new Thread(new semesterRunnable(sid,
									semesterHandler));
							Thread t = new Thread(new imageRunnable(
									imageHandler));
							t.start();
						}
					} else {
						ShowAlertDialog("未輸入學號，請輸入再查詢！");
					}
				}
			}
		});
		if (readSettings()) {
			courseTable.showCourse(studentCourse);
		}

	}

	private void closeSoftKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
				.getWindowToken(), 0);
	}

	private void setSpinnerCheck() {
		if (isRefresh) {
			spinner.setOnTouchListener(null);
		}
		spinner.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					ShowAlertDialog("請先查詢學期再選擇！");
				}
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();

		switch (item_id) {
		case R.id.offline_course:
			writeSettings();
			break;
		case R.id.clear_offline:
			settings = getSharedPreferences("CourseApp", 0);
			settings.edit().remove("studentCourse").commit();
			break;
		case R.id.login_setting:
			Intent intent = new Intent(this, LoginSettingActivity.class);
			startActivity(intent);
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// writeSettings();
	}

	private void initCourseTable() {

		courseTable = (CourseTableLinearLayout) findViewById(R.id.courseTable);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int rowHeight = Math.round(displaymetrics.heightPixels / 12.5f);
		courseTable.initCourseTable(rowHeight, this);
	}

	private void writeSettings() {
		if (studentCourse != null) {
			settings = getSharedPreferences("CourseApp", 0);
			Gson gson = new Gson();
			String json = gson.toJson(studentCourse);
			settings.edit().putString("studentCourse", json).commit();
		}
	}

	private Boolean readSettings() {

		settings = getSharedPreferences("CourseApp", 0);
		Gson gson = new Gson();
		String json = settings.getString("studentCourse", "");
		if (json.length() > 0) {
			studentCourse = gson.fromJson(json, StudentCourse.class);
			sid = studentCourse.getSid();
			sidText.setText(sid);
			year = studentCourse.getYear();
			semester = studentCourse.getSemester();
			semesters.add(year + "-" + semester);
			adapter.notifyDataSetChanged();
			return true;
		} else {
			return false;
		}
	}

	private Handler imageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case imageRunnable.REFRESH_DATA:
				Bitmap result = null;
				if (msg.obj instanceof Bitmap) {
					result = (Bitmap) msg.obj;
				}
				if (result != null) {
					String authcode = OCR.authOCR(
							OCR.bitmap2grayByteArry(result), result.getWidth(),
							result.getHeight());

					settings = getSharedPreferences("CourseApp", 0);
					String id = settings.getString("id", "");
					String passwd = settings.getString("passwd", "");
					if (id.length() > 0 && passwd.length() > 0) {
						Thread t = new Thread(new loginRunnable(id, passwd,
								authcode, loginHandler));
						t.start();
					} else {
						pd.dismiss();
						ShowAlertDialog("請先設定入口網站帳號密碼！");
					}
				} else {
					if (NportalConnector.timeOutFlag) {
						pd.dismiss();
						ShowAlertDialog("此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！");
					} else {
						pd.dismiss();
						ShowAlertDialog("此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！");
					}
				}
				break;
			}
		}
	};

	private Handler loginHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case loginRunnable.REFRESH_DATA:
				String result = null;
				if (msg.obj instanceof String) {
					result = (String) msg.obj;
				}
				if (result != null) {
					NportalConnector.isLogin = true;
					if (nextThread != null) {
						nextThread.start();
						nextThread = null;
					} else {
						pd.dismiss();
					}
				} else {
					NportalConnector.isLogin = false;
					if (NportalConnector.alertString.length() > 0) {
						pd.dismiss();
						ShowAlertDialog(NportalConnector.alertString);
					} else {
						pd.dismiss();
						ShowAlertDialog("此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！");
					}
				}
				break;
			}
		}
	};
	private Handler semesterHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case semesterRunnable.REFRESH_DATA:
				ArrayList<String> result = null;
				if (msg.obj instanceof List<?>) {
					result = (ArrayList<String>) msg.obj;
				}
				if (result != null) {
					semesters.clear();
					semesters.addAll(result);
					adapter.notifyDataSetChanged();
					isRefresh = true;
					spinner.setOnTouchListener(null);
					pd.dismiss();
					spinner.setSelection(0);
				} else {
					if (NportalConnector.timeOutFlag) {
						pd.dismiss();
						ShowAlertDialog("此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！");
					} else {
						pd.dismiss();
						ShowAlertDialog(NportalConnector.alertString);
					}
				}
				break;
			}
		}
	};

	private Handler courseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case courseRunnable.REFRESH_DATA:
				StudentCourse result = null;
				if (msg.obj instanceof StudentCourse) {
					result = (StudentCourse) msg.obj;
				}
				if (result != null) {
					studentCourse = result;
					courseTable.showCourse(studentCourse);
					pd.dismiss();
				} else {
					if (NportalConnector.timeOutFlag) {
						pd.dismiss();
						ShowAlertDialog("此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！");
					} else {
						pd.dismiss();
						ShowAlertDialog("此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！");
					}
				}
				break;
			}
		}
	};

	public DialogInterface.OnClickListener courseDetailDialogLis = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			if (NportalConnector.isLogin) {
				if (selectedCourseNo.equals("0")) {
					ShowAlertDialog("班會課無法查詢瀏覽詳細資訊！");
				} else {
					Intent i = new Intent(CourseActivity.this,
							CourseDetailActivity.class);
					i.putExtra("CourseNo", selectedCourseNo);
					startActivityForResult(i, 2);
				}
			} else {
				pd = ProgressDialog.show(CourseActivity.this, null, "登入校園入口網站中~",
						true);
				nextThread = new Thread(new Runnable() {
					@Override
					public void run() {
						pd.dismiss();
						Intent i = new Intent(CourseActivity.this,
								CourseDetailActivity.class);
						i.putExtra("CourseNo", selectedCourseNo);
						startActivityForResult(i, 2);
					}
				});
				Thread t = new Thread(new imageRunnable(imageHandler));
				t.start();
			}
		}
	};

	public OnItemSelectedListener semesterSelectedLis = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> options,
				View selectedItemView, int position, long id) {
			String[] s = semesters.get(position).split("-");
			year = s[0];
			semester = s[1];
			if (lastSid.length() > 0) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), 0);
				pd = ProgressDialog.show(CourseActivity.this, null, "課表讀取中~",
						true);
				Thread course_t = new Thread(new courseRunnable(courseHandler,
						sid, year, semester));
				course_t.start();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	};
}
