package com.alan.courseapp.utility;

import java.util.ArrayList;

import com.alan.courseapp.data.StudentCourse;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;

public class Utility {

	public static StudentCourse cleanString(StudentCourse student) {
		Gson gson = new Gson();
		String json = gson.toJson(student);
		json = json.replace("¡@", " ");
		json = json.replace("\\n", " ");
		student = gson.fromJson(json, StudentCourse.class);
		return student;
	}

	public static ArrayList<String> splitTime(String timeString) {
		//
		// try {
		ArrayList<String> infos = new ArrayList<String>();
		// timeString =timeString.replace("¡@", " ");
		String[] temp = timeString.split(" ");
		for (String t : temp) {
			if (t.equals("A")) {
				infos.add("10");
			} else if (t.equals("B")) {
				infos.add("11");
			} else if (t.equals("C")) {
				infos.add("12");
			} else if (t.equals("D")) {
				infos.add("13");
			} else {
				infos.add(t);
			}
		}
		return infos;
		// } catch (Exception e) {
		// Log.e("splitTime",e.getMessage());
		// }
		// return null;
	}

	public static void ShowDialog(String title, String msg, Context context) {
		Builder MyAlertDialog = new AlertDialog.Builder(context);
		MyAlertDialog.setTitle(title);
		MyAlertDialog.setMessage(msg);
		MyAlertDialog.show();
	}
}
