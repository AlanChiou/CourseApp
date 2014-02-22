package com.alan.courseapp.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.alan.courseapp.data.CourseInfo;
import com.alan.courseapp.data.StudentCourse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class NportalConnector {
	private static DefaultHttpClient client = new DefaultHttpClient();
	public static boolean timeOutFlag = false;
	public static boolean isLogin = false;
	static String imageUri = "http://nportal.ntut.edu.tw/authImage.do";
	static String loginUri = "http://nportal.ntut.edu.tw/login.do";
	static String nportalUri = "http://nportal.ntut.edu.tw/";
	static String postCoursesUri = "http://nportal.ntut.edu.tw/ssoIndex.do?apUrl=http://aps.ntut.edu.tw/course/tw/courseSID.jsp&apOu=aa_0010&sso=big5";
	static String coursesUri = "http://aps.ntut.edu.tw/course/tw/courseSID.jsp";
	static String semesterUri = "http://aps.ntut.edu.tw/course/tw/Select.jsp?format=-3&code=";
	static String courseUri = "http://aps.ntut.edu.tw/course/tw/Select.jsp";
	static String courseDetailUri = "http://aps.ntut.edu.tw/course/tw/Select.jsp?format=-1&code=189362";
	public static String alertString = "";

	private static String getDataByPost(String uri, List<NameValuePair> params,
			String charsetName) {
		HttpPost httpRequest = new HttpPost(uri);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		timeOutFlag = false;
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			client.setParams(httpParameters);
			HttpResponse httpResponse = client.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String dataString = convertStreamToString(httpResponse
						.getEntity().getContent(), charsetName);
				return dataString;
			} else if (httpResponse.getStatusLine().getStatusCode() == 404) {
				return null;
			}
		} catch (Exception ex) {
			timeOutFlag = true;
			Log.e("getDataByPost", ex.getMessage());
		}
		return null;
	}

	public static String login(String muid, String mpassword, String authcode) {
		alertString = "";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("muid", muid));
		params.add(new BasicNameValuePair("mpassword", mpassword));
		params.add(new BasicNameValuePair("authcode", authcode));
		params.add(new BasicNameValuePair("Submit2", "登入（Login）"));
		try {

			String result = getDataByPost(loginUri, params, "utf-8");
			if (result.contains("帳號或密碼錯誤")) {
				alertString = "帳號或密碼錯誤！";
				return null;
			} else if (result.contains("驗證碼")) {
				alertString = "驗證碼錯誤，請通知作者！";
				return null;
			}
			result = getDataByPost(postCoursesUri,
					new ArrayList<NameValuePair>(), "big5");

			TagNode tagNode;
			tagNode = new HtmlCleaner().clean(result);
			TagNode[] nodes = tagNode.getElementsByAttValue("name",
					"sessionId", true, false);
			String sessionId = nodes[0].getAttributeByName("value").toString();

			List<NameValuePair> courseParams = new ArrayList<NameValuePair>();
			courseParams.add(new BasicNameValuePair("sessionId", sessionId));
			courseParams.add(new BasicNameValuePair("userid", muid));
			courseParams.add(new BasicNameValuePair("userType", "50"));

			result = getDataByPost(coursesUri, courseParams, "big5");

			return result;
		} catch (Exception ex) {
			alertString = "登入時發生錯誤，請重新查詢！";
			Log.e("login", ex.getMessage());
		}
		return null;
	}

	public static ArrayList<String> getCourseSemesters(String sid) {

		try {
			ArrayList<String> semesters = new ArrayList<String>();
			String result = getDataByPost(semesterUri + sid,
					new ArrayList<NameValuePair>(), "big5");
			TagNode tagNode;
			tagNode = new HtmlCleaner().clean(result);
			if (result.contains("查無該學號的學生基本資料")) {
				alertString = "查無該學號的學生基本資料！";
				return null;
			}
			TagNode[] nodes = tagNode.getElementsByName("a", true);
			for (TagNode a : nodes) {
				String[] split = a.getText().toString().split(" ");
				semesters.add(split[0] + "-" + split[2]);
			}
			return semesters;
		} catch (Exception ex) {
			alertString = "查詢學期時發生錯誤，請重新查詢！";
			Log.e("getCourseSemesters", ex.getMessage());
		}
		return null;
	}

	public static StudentCourse getStudentCourse(String sid, String year,
			String semester) {
		StudentCourse student = new StudentCourse();
		student.setSid(sid);
		student.setYear(year);
		student.setSemester(semester);
		ArrayList<CourseInfo> courseList = getCourses(sid, year, semester);
		if (courseList != null) {
			student.setCourseList(courseList);
		} else {
			return null;
		}
		student = Utility.cleanString(student);
		return student;
	}

	public static ArrayList<String> GetCourseDetail(String courseNo) {
		try {
			ArrayList<String> courseDetail = new ArrayList<String>();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("format", "-1"));
			params.add(new BasicNameValuePair("code", courseNo));
			String result = getDataByPost(courseUri, params, "big5");
			TagNode tagNode;
			tagNode = new HtmlCleaner().clean(result);
			TagNode[] tables = tagNode.getElementsByAttValue("border", "1",
					true, false);

			TagNode[] rows = tables[0].getElementsByName("tr", true);
			for (int i = 0; i < rows.length; i++) {
				TagNode[] cols = rows[i].getElementsByName("th", true);
				String d = cols[0].getText().toString();
				cols = rows[i].getElementsByName("td", true);
				d = d + "：" + cols[0].getText().toString();
				d = d.replace("　", " ");
				d = d.replace("\n", " ");
				courseDetail.add(d);
			}
			return courseDetail;
		} catch (Exception ex) {
			Log.e("GetCourseDetail", ex.getMessage());
		}
		return null;
	}

	public static ArrayList<String> GetClassmate(String courseNo) {
		try {
			ArrayList<String> classmates = new ArrayList<String>();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("format", "-1"));
			params.add(new BasicNameValuePair("code", courseNo));
			String result = getDataByPost(courseUri, params, "big5");
			TagNode tagNode;
			tagNode = new HtmlCleaner().clean(result);
			TagNode[] tables = tagNode.getElementsByAttValue("border", "1",
					true, false);

			TagNode[] rows = tables[1].getElementsByName("tr", true);
			for (int i = 1; i < rows.length; i++) {
				TagNode[] cols = rows[i].getElementsByName("td", true);
				String d = cols[0].getText().toString();
				d = d + "," + cols[1].getText().toString();
				d = d + "," + cols[2].getText().toString();
				d = d.replace("　", "");
				d = d.replace("\n", "");
				classmates.add(d);
			}
			return classmates;
		} catch (Exception ex) {
			Log.e("GetClassmate", ex.getMessage());
		}
		return null;
	}

	public static ArrayList<CourseInfo> getCourses(String sid, String year,
			String semester) {

		try {
			ArrayList<CourseInfo> courses = new ArrayList<CourseInfo>();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("format", "-2"));
			params.add(new BasicNameValuePair("code", sid));
			params.add(new BasicNameValuePair("year", year));
			params.add(new BasicNameValuePair("sem", semester));
			String result = getDataByPost(courseUri, params, "big5");
			TagNode tagNode;
			tagNode = new HtmlCleaner().clean(result);
			TagNode[] nodes = tagNode.getElementsByAttValue("border", "1",
					true, false);
			TagNode[] rows = nodes[0].getElementsByName("tr", true);
			for (int i = 3; i < rows.length - 1; i++) {
				TagNode[] cols = rows[i].getElementsByName("td", true);
				CourseInfo course = new CourseInfo();
				TagNode[] a = cols[0].getElementsByName("a", true);
				if (a.length == 0) {
					course.setCourseNo("0");
				} else {
					course.setCourseNo(a[0].getText().toString());
				}
				course.setCourseName(cols[1].getText().toString());
				course.setCourseTeacher(cols[6].getText().toString());
				course.setCourseRoom(cols[15].getText().toString());
				course.setCourseTime(new String[] {
						cols[8].getText().toString(),
						cols[9].getText().toString(),
						cols[10].getText().toString(),
						cols[11].getText().toString(),
						cols[12].getText().toString(),
						cols[13].getText().toString(),
						cols[14].getText().toString() });
				courses.add(course);
			}
			return courses;
		} catch (Exception ex) {
			Log.e("getCourses", ex.getMessage());
		}
		return null;
	}

	public static Bitmap LoadImageFromWebOperations() {
		try {
			getDataByPost(nportalUri, new ArrayList<NameValuePair>(), "big5");

			HttpGet httpRequest = new HttpGet(imageUri);

			HttpResponse response = (HttpResponse) client.execute(httpRequest);

			HttpEntity entity = response.getEntity();
			BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
			InputStream input = b_entity.getContent();

			Bitmap bitmap = BitmapFactory.decodeStream(input);

			return bitmap;

		} catch (Exception ex) {

		}
		return null;
	}

	private static String convertStreamToString(InputStream is,
			String charsetName) {
		InputStreamReader isr = null;
		StringBuffer buffer = new StringBuffer();
		try {
			isr = new InputStreamReader(is, charsetName);

			Reader in = new BufferedReader(isr);

			int ch;
			while ((ch = in.read()) != -1) {
				buffer.append((char) ch);
			}

			isr.close();
			is.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}
}
