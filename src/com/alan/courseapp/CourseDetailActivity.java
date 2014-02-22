package com.alan.courseapp;

import java.util.ArrayList;

import com.alan.courseapp.data.StudentCourse;
import com.alan.courseapp.runnable.classmateRunnable;
import com.alan.courseapp.runnable.courseDetailRunnable;
import com.alan.courseapp.runnable.courseRunnable;
import com.alan.courseapp.utility.NportalConnector;
import com.alan.courseapp.utility.Utility;
import com.example.courseapp.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CourseDetailActivity extends Activity {

	ArrayList<String> courseDetail;
	ArrayList<String> classmateList;
	String courseNo;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course_detail);
		Intent i = getIntent();
		courseNo = i.getStringExtra("CourseNo");
		pd = ProgressDialog.show(this, null, "課程資料讀取中~", true);
		Thread t = new Thread(new courseDetailRunnable(courseDetailHandler,
				courseNo));
		t.start();
		Button btn = (Button) findViewById(R.id.backButton);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void showCoursDetail() {
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout courseInfo = (LinearLayout) findViewById(R.id.courseInfo);
		for (int i = 0; i < courseDetail.size(); i++) {
			if (i == 1 || i == 2 || i == 4 || i == 6 || i == 13 || i == 14
					|| i == 15 || i == 16 || i == 17) {

			} else {
				LinearLayout item = (LinearLayout) li.inflate(
						R.layout.course_item, courseInfo, false);
				TextView text = (TextView) item.findViewById(R.id.text);
				text.setText(courseDetail.get(i));
				courseInfo.addView(item);
			}
		}
	}

	private void showClassmates() {
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout classmates = (LinearLayout) findViewById(R.id.classmates);
		for (int i = 0; i < classmateList.size(); i++) {
			LinearLayout classmate = (LinearLayout) li.inflate(
					R.layout.classmate_item, classmates, false);
			LinearLayout item = (LinearLayout)classmate.findViewById(R.id.classmate_item);
			if (i % 2 == 1) {
				item.setBackgroundColor(Color.parseColor("#e5e5e5"));
			}
			else
			{
				item.setBackgroundColor(Color.WHITE);
			}
			String[] temp = classmateList.get(i).split(",");
			TextView text = (TextView) classmate.findViewById(R.id.sclass);
			text.setText(temp[0]);
			text = (TextView) classmate.findViewById(R.id.sid);
			text.setText(temp[1]);
			text = (TextView) classmate.findViewById(R.id.sname);
			text.setText(temp[2]);
			Button submit = (Button) classmate.findViewById(R.id.submit);
			submit.setTag(temp[1]);
			submit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String sid = (String) v.getTag();
					Intent intent = new Intent();
					intent.putExtra("sid", sid);
					setResult(RESULT_OK, intent);
					finish();
				}
			});
			classmates.addView(classmate);

		}
	}

	private Handler courseDetailHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case courseDetailRunnable.REFRESH_DATA:
				ArrayList<String> result = null;
				if (msg.obj instanceof ArrayList<?>) {
					result = (ArrayList<String>) msg.obj;
				}
				if (result != null) {
					courseDetail = result;
					showCoursDetail();
					Thread t = new Thread(new classmateRunnable(
							classmateHandler, courseNo));
					t.start();
				} else {
					if (NportalConnector.timeOutFlag) {
						pd.dismiss();
						Utility.ShowDialog("警告", "此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！",
								CourseDetailActivity.this);
					} else {
						pd.dismiss();
						Utility.ShowDialog("警告", "此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！",
								CourseDetailActivity.this);
					}
				}
				break;
			}
		}
	};
	private Handler classmateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case courseDetailRunnable.REFRESH_DATA:
				ArrayList<String> result = null;
				if (msg.obj instanceof ArrayList<?>) {
					result = (ArrayList<String>) msg.obj;
				}
				if (result != null) {
					classmateList = result;
					showClassmates();
					pd.dismiss();
				} else {
					if (NportalConnector.timeOutFlag) {
						pd.dismiss();
						Utility.ShowDialog("警告", "此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！",
								CourseDetailActivity.this);
					} else {
						pd.dismiss();
						Utility.ShowDialog("警告", "此服務發生錯誤，請檢查網路狀態或使用原網頁查詢！",
								CourseDetailActivity.this);
					}
				}
				break;
			}
		}
	};
}
