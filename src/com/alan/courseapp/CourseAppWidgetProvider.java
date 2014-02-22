package com.alan.courseapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.alan.courseapp.data.CourseInfo;
import com.alan.courseapp.data.StudentCourse;
import com.alan.courseapp.utility.Utility;
import com.example.courseapp.R;
import com.google.gson.Gson;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

public class CourseAppWidgetProvider extends AppWidgetProvider {

	private static StudentCourse studentCourse = null;
	private static Context context;

	/** Called when the activity is first created. */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		CourseAppWidgetProvider.context = context;
		Log.e("1111111111111111", "2222222222222222222");
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if (intent.getAction().equals(
				"android.appwidget.action.APPWIDGET_UPDATE")) {
			Gson gson = new Gson();
			String json = intent.getStringExtra("StudentCourse");
			Log.e("1111111111111111", json);
			studentCourse = gson.fromJson(json, StudentCourse.class);
			Log.e("1111111111111115", studentCourse.getSid());

		}
		super.onReceive(context, intent);
	}

	private static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.main);
		Log.e("1111111111111111", "3333333333333333");

		clearCourseTable(views);
		if (studentCourse != null) {
			showCourse(views);
			Log.e("1111111111111111", studentCourse.getSid());
		}
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	private static void clearCourseTable(RemoteViews views) {
		for (int i = 1; i < 13; i++) {
			for (int j = 1; j < 7; j++) {
				int id = context.getResources().getIdentifier(
						"text" + i + "_" + j, "id", context.getPackageName());
				views.setTextViewText(id, "");
				views.setInt(id, "setBackgroundColor", Color.TRANSPARENT);

			}
		}
	}

	private static void showCourse(RemoteViews views) {
		int count = 0;
		Boolean[] array = new Boolean[18];
		Arrays.fill(array, Boolean.TRUE);
		for (final CourseInfo item : studentCourse.getCourseList()) {
			Boolean isHaveTime = false;
			Random generator = new Random();
			int r = generator.nextInt(18);
			while (!array[r]) {
				r = generator.nextInt(18);
			}
			array[r] = false;
			float hue = (float) (r * 20);
			for (int i = 1; i < 6; i++) {
				if (item != null) {
					String time = item.getCourseTimes()[i];
					if (time.length() >= 1) {
						ArrayList<String> s = Utility.splitTime(time);
						for (String t : s) {
							if (!t.equals("")) {
								int id = context.getResources().getIdentifier(
										"text" + (Integer.valueOf(t) + 1) + "_"
												+ i, "id",
										context.getPackageName());
								views.setInt(
										id,
										"setBackgroundColor",
										Color.HSVToColor(new float[] { hue,
												0.2f, 1f }));
								views.setTextViewText(id, item.getCourseName());
								isHaveTime = true;
							}
						}
					}
				}
			}
			if (isHaveTime == false) {
				count++;
				int id = context.getResources().getIdentifier(
						"text" + count + "_6", "id", context.getPackageName());
				views.setInt(id, "setBackgroundColor",
						Color.HSVToColor(new float[] { hue, 0.2f, 1f }));
				views.setTextViewText(id, item.getCourseName());
			}
		}
	}
}
