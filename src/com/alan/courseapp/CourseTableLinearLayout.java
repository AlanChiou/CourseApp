package com.alan.courseapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.alan.courseapp.data.CourseInfo;
import com.alan.courseapp.data.StudentCourse;
import com.alan.courseapp.utility.Utility;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CourseTableLinearLayout extends LinearLayout {
	private Context context;
	private CourseActivity activity;
	private int tableCol = 9;
	private int tableRow = 14;
	private boolean isDisplayABCD = false;
	private boolean isDisplaySat = false;
	private boolean isDisplaySun = false;
	private boolean isDisplayNoTime = false;
	private final String[] timeArray = { "08:10 - 09:00", "09:10 - 10:00",
			"10:10 - 11:00", "11:10 - 12:00", "13:10 - 14:00", "14:10 - 15:00",
			"15:10 - 16:00", "16:10 - 17:00", "17:10 - 18:00", "18:30 - 19:20",
			"19:20 - 20:10", "20:20 - 21:10", "21:10 - 22:00", "" };

	public CourseTableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void initCourseTable(int rowHeight, CourseActivity activity) {
		this.activity = activity;
		LinearLayout.LayoutParams row = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams view_layout = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT, 1f);
		LinearLayout.LayoutParams view_layout_title = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT, 0.5f);

		for (int i = 0; i < tableRow; i++) {
			LinearLayout tr = new LinearLayout(context);
			tr.setId(i);
			tr.setOrientation(LinearLayout.HORIZONTAL);
			if (i == 0) {
				tr.setLayoutParams(row);
			} else {
				tr.setLayoutParams(row);
			}
			tr.setGravity(Gravity.CENTER);
			// tr.setWeightSum(16);
			if (i % 2 == 0) {
				tr.setBackgroundColor(Color.parseColor("#e5e5e5"));
			}
			for (int j = 0; j < tableCol; j++) {

				TextView text = new TextView(context);
				text.setId(j);
				text.setGravity(Gravity.CENTER);
				text.setTextSize(13);
				if (j == 0) {
					if (i > 0 && i < 10) {
						text.setText(String.valueOf(i));
					}
					if (i == 10) {
						text.setText("A");
					}
					if (i == 11) {
						text.setText("B");
					}
					if (i == 12) {
						text.setText("C");
					}
					if (i == 13) {
						text.setText("D");
					}
					text.setLayoutParams(view_layout_title);
				} else {
					text.setLayoutParams(view_layout);
				}

				if (i == 0) {
					text.setHeight(Math.round(rowHeight / 2.5f));
				} else {
					text.setHeight(rowHeight);
				}
				tr.addView(text);
			}
			this.addView(tr);
		}
		LinearLayout titleTr = (LinearLayout) this.findViewById(0);
		TextView text = (TextView) titleTr.findViewById(1);
		text.setText("一");
		text = (TextView) titleTr.findViewById(2);
		text.setText("二");
		text = (TextView) titleTr.findViewById(3);
		text.setText("三");
		text = (TextView) titleTr.findViewById(4);
		text.setText("四");
		text = (TextView) titleTr.findViewById(5);
		text.setText("五");
		text = (TextView) titleTr.findViewById(6);
		text.setText("六");
		text = (TextView) titleTr.findViewById(7);
		text.setText("日");
	}

	private void clearCourseTable() {
		for (int i = 1; i < tableRow; i++) {
			for (int j = 1; j < tableCol; j++) {
				LinearLayout tr = (LinearLayout) this.getChildAt(Integer
						.valueOf(i));
				TextView text = (TextView) tr.getChildAt(j);
				text.setText("");
				text.setBackgroundColor(Color.TRANSPARENT);
				text.setOnClickListener(null);
			}
		}
		isDisplayABCD = false;
		isDisplaySat = false;
		isDisplaySun = false;
		isDisplayNoTime = false;
		controlRowABCD();
		controlSat();
		controlSun();
		controlNoTime();
	}

	private void controlRowABCD() {
		int mode;
		if (isDisplayABCD) {
			mode = View.VISIBLE;
		} else {
			mode = View.GONE;
		}
		for (int i = 10; i < tableRow; i++) {
			LinearLayout tr = (LinearLayout) this.getChildAt(i);
			tr.setVisibility(mode);
		}
	}

	private void controlSat() {
		int mode;
		if (isDisplaySat) {
			mode = View.VISIBLE;
		} else {
			mode = View.GONE;
		}
		for (int i = 0; i < tableRow; i++) {
			LinearLayout tr = (LinearLayout) this.getChildAt(i);
			TextView text = (TextView) tr.getChildAt(6);
			text.setVisibility(mode);
		}
	}

	private void controlSun() {
		int mode;
		if (isDisplaySun) {
			mode = View.VISIBLE;
		} else {
			mode = View.GONE;
		}
		for (int i = 0; i < tableRow; i++) {
			LinearLayout tr = (LinearLayout) this.getChildAt(i);
			TextView text = (TextView) tr.getChildAt(7);
			text.setVisibility(mode);
		}
	}

	private void controlNoTime() {
		int mode;
		if (isDisplayNoTime) {
			mode = View.VISIBLE;
		} else {
			mode = View.GONE;
		}
		for (int i = 0; i < tableRow; i++) {
			LinearLayout tr = (LinearLayout) this.getChildAt(i);
			TextView text = (TextView) tr.getChildAt(8);
			text.setVisibility(mode);
		}
	}

	public void showCourse(StudentCourse studentCourse) {
		clearCourseTable();
		Boolean[] array = new Boolean[18];
		Arrays.fill(array, Boolean.TRUE);
		int count = 0;
		Random generator = new Random();
		for (final CourseInfo item : studentCourse.getCourseList()) {
			Boolean isHaveTime = false;
			int r = generator.nextInt(18);
			while (!array[r]) {
				r = generator.nextInt(18);
			}
			array[r] = false;
			float hue = (float) (r * 20);
			for (int i = 0; i < 7; i++) {
				if (item != null) {
					String time = item.getCourseTimes()[i];
					ArrayList<String> s = Utility.splitTime(time);
					for (String t : s) {
						if (!t.equals("")) {
							LinearLayout tr = (LinearLayout) this
									.getChildAt(Integer.valueOf(t));
							TextView text;
							if (Integer.valueOf(t) > 9) {
								isDisplayABCD = true;
							}
							if (i == 0) {
								text = (TextView) tr.getChildAt(7);
								isDisplaySun = true;
							} else {
								if (i == 6) {
									isDisplaySat = true;
								}
								text = (TextView) tr.getChildAt(i);
							}
							text.setText(item.getCourseName());
							text.setBackgroundColor(Color
									.HSVToColor(new float[] { hue, 0.2f, 1f }));
							text.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (v.getId() == 8) {
										ShowCourseInfo(14,
												item.getCourseName(), item);
									} else {
										ShowCourseInfo(
												((View) v.getParent()).getId(),
												item.getCourseName(), item);
									}
								}
							});
							isHaveTime = true;
						}
					}
				}
			}
			if (!isHaveTime) {
				count++;
				isDisplayNoTime = true;
				LinearLayout tr = (LinearLayout) this.getChildAt(Integer
						.valueOf(count));
				TextView text = (TextView) tr.getChildAt(8);
				text.setText(item.getCourseName());
				text.setBackgroundColor(Color.HSVToColor(new float[] { hue,
						0.2f, 1f }));
				text.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (v.getId() == 8) {
							ShowCourseInfo(14, item.getCourseName(), item);
						} else {
							ShowCourseInfo(((View) v.getParent()).getId(),
									item.getCourseName(), item);
						}
					}
				});
			}
		}
		controlRowABCD();
		controlSat();
		controlSun();
		controlNoTime();
	}

	private void ShowCourseInfo(int id, String courseName, CourseInfo course) {
		activity.selectedCourseNo = course.getCourseNo();
		Builder course_Dialog = new AlertDialog.Builder(context);
		course_Dialog.setTitle(courseName);
		course_Dialog.setMessage("課號：" + course.getCourseNo() + "\n" + "時間："
				+ timeArray[id - 1] + "\n" + "地點：" + course.getCourseRoom()
				+ "\n" + "授課老師：" + course.getCourseTeacher());
		course_Dialog.setNeutralButton("詳細內容", activity.courseDetailDialogLis);
		course_Dialog.show();
	}
}
