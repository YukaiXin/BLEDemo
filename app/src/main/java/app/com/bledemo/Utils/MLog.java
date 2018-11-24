package app.com.bledemo.Utils;


public class MLog {
	public static boolean debug = true;
	private static final String TAG_DEFAULT = "LoggerXXX";

	public static void i(String s){
		i(null,s);
	}
	public static void i(String s, String s1) {
		if (debug)
			if (s != null && s.length() != 0)
				android.util.Log.i(s, s1);
			else
				android.util.Log.i(TAG_DEFAULT, s1);
	}
	public static void w(String s){
		w(null,s);
	}
	public static void w(String s, String s1) {
		if (debug)
			if (s != null && s.length() != 0)
				android.util.Log.w(s, s1);
			else
				android.util.Log.w(TAG_DEFAULT, s1);
	}

	public static void w(String s, String s1, Throwable t) {
		if (debug)
			if (s != null && s.length() != 0)
				android.util.Log.w(s, s1, t);
			else
				android.util.Log.w(TAG_DEFAULT, s1, t);
	}
	public static void v(String s){
		v(null,s);
	}
	public static void v(String s, String s1) {
		if (debug)
			if (s != null && s.length() != 0)
				android.util.Log.v(s, s1);
			else
				android.util.Log.v(TAG_DEFAULT, s1);
	}
	public static void e(String s){
		e(null,s);
	}
	public static void e(String s, String s1) {
			if (debug)
				if (s != null && s.length() != 0)
					android.util.Log.e(s, s1);
				else
					android.util.Log.e(TAG_DEFAULT, s1);
	}

	public static void e(String s, String s1, Throwable t) {
		if (debug)
			if (s != null && s.length() != 0)
				android.util.Log.e(s, s1, t);
			else
				android.util.Log.e(TAG_DEFAULT, s1, t);
	}
	public static void d(String s){
		d(null,s);
	}
	public static void d(String s, String s1) {
		if (debug)
			if (s != null && s.length() != 0)
				android.util.Log.d(s, s1);
			else
				android.util.Log.d(TAG_DEFAULT, s1);
	}

}
