package exocr.exocrengine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

public class DictManager {
	static private boolean hasInit = false;
	
	static public boolean hasInit() {
		return hasInit;
	}
	
	static public void InitDict(Activity activity) {
		byte dbpath[] = new byte[256];
		Context context = activity.getApplicationContext();
		String dictpath = context.getFilesDir().getAbsolutePath();
		// if the dict not exist, copy from the assets
		if (CheckExist(dictpath + "/zocr0.lib") == false) {
			File destDir = new File(dictpath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}

			boolean a = copyFile(context, "zocr0.lib", dictpath + "/zocr0.lib");
			if (a == false) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("识别核心初始化失败\n");
				builder.setMessage("请检查识字典文件是否存在");
				builder.setCancelable(true);
				builder.create().show();
				return;
			}
		}

		String filepath = dictpath;

		// string to byte
		for (int i = 0; i < filepath.length(); i++)
			dbpath[i] = (byte) filepath.charAt(i);
		dbpath[filepath.length()] = 0;

		int nres = EXOCREngine.nativeInit(dbpath);

		if (nres < 0) {
			hasInit = false;
			Log.d("ExTranslator.nativeExInit", "Init Error = " + nres);
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("识别核心初始化失败\n");
			builder.setMessage("请检查识别核心授权是否过期");
			builder.setCancelable(true);
			builder.create().show();
			return;
		} else {
			hasInit = true;
		}
		// sign ocr sdk
		EXOCREngine.nativeCheckSignature(context);
	}

	private static boolean copyFile(Context context, String from, String to) {
		// 例：from:890.salid;
		// to:/mnt/sdcard/to/890.salid
		boolean ret = false;
		try {
			int bytesum = 0;
			int byteread = 0;

			InputStream inStream = context.getResources().getAssets().open(from);// 将assets中的内容以流的形式展示出来
			File file = new File(to);
			OutputStream fs = new FileOutputStream(file);// to为要写入sdcard中的文件名称
			byte[] buffer = new byte[1024];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
			ret = true;

		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}
	
	// check one file
	private static boolean CheckExist(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	public static void FinishDict() {
		EXOCREngine.nativeDone();
	}
}
