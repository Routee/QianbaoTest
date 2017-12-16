package exocr.idcard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import exocr.exocrengine.EXIDCardResult;
import exocr.exocrengine.EXOCREngine;

public class IDPhoto {
	private static final String TAG = IDPhoto.class.getSimpleName();
	private CaptureActivity mActivity;
	private EXIDCardResult mCardInfo;
	static Bitmap markedCardImage = null;
	private boolean bSucceed;
	
	private ProgressDialog pd;
	private static final int PHOTO_DATA_ENTRY = 0x555;
	
	/** Construction */
	public IDPhoto(CaptureActivity activity) {
		mActivity = activity;
	}
    
	// 定义Handler对象
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
			if (bSucceed) {
				if (mCardInfo != null) {
					mActivity.didFinishPhotoRec();
					Intent intent = new Intent(mActivity, IDPhotoResultActivity.class);
					intent.putExtra(CaptureActivity.EXTRA_SCAN_RESULT, mCardInfo);
					intent.putExtras(mActivity.getIntent());
					intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY
							| Intent.FLAG_ACTIVITY_NO_ANIMATION);
					mActivity.setResult(PHOTO_DATA_ENTRY, intent);
					mActivity.finish();
				}
			} else {
				AlertDialog alertDialog = new AlertDialog.Builder(mActivity).setTitle("提示")
						.setMessage("无法识别该图片，请手动输入身份证信息")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (mCardInfo != null) {
							mActivity.didFinishPhotoRec();
							Intent intent = new Intent(mActivity, IDPhotoResultActivity.class);
							intent.putExtra(CaptureActivity.EXTRA_SCAN_RESULT, mCardInfo);
							intent.putExtras(mActivity.getIntent());
							intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY
									| Intent.FLAG_ACTIVITY_NO_ANIMATION);
							mActivity.setResult(PHOTO_DATA_ENTRY, intent);
							mActivity.finish();
						}
					}
				}).create();
				alertDialog.show();
			}
		}
	};

	public void openPhoto() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
        /* 开启Pictures画面Type设定为image */  
        intent.setType("image/*");  
        /* 取得相片后返回 */  
        mActivity.startActivityForResult(intent, CaptureActivity.PHOTO_ID);
	}
	
	private void _photoRec(Bitmap bitmap) {
		byte []result = new byte[4096];
		int []rets = new int[16];
		int []rects = new int[64];
		int ret = 0;
		
		//recgonise stillImage
		Bitmap cardim = EXOCREngine.nativeRecoIDCardStillImageV2(bitmap, 0, 1, result, 4096, rects, rets);
		Log.i("nativeRecoStillImage", "return="+rets[0]);
		
		ret = rets[0];
		if(ret > 0){			
			mCardInfo = EXIDCardResult.decode(result, ret);
			mCardInfo.SetBitmap(cardim);
			//保存各个条目的矩形框
			mCardInfo.setRects(rects);
			bSucceed = true;
		} else {
			bSucceed = false;
			mCardInfo = new EXIDCardResult();
			if (markedCardImage != null && !markedCardImage.isRecycled()) {
				markedCardImage.recycle();
			}	
			markedCardImage = bitmap;
			return;
		}
		
		if (bSucceed) {
			if (mCardInfo.type == 1) {
				if (CaptureActivity.IDCardFrontFullImage != null && !CaptureActivity.IDCardFrontFullImage.isRecycled()) {
					CaptureActivity.IDCardFrontFullImage.recycle();
				}
				CaptureActivity.IDCardFrontFullImage = mCardInfo.stdCardIm;
				if (CaptureActivity.IDCardFaceImage != null && !CaptureActivity.IDCardFaceImage.isRecycled()) {
					CaptureActivity.IDCardFaceImage.recycle();
				}
				CaptureActivity.IDCardFaceImage = mCardInfo.GetFaceBitmap();
			} else if (mCardInfo.type == 2) {
				if (CaptureActivity.IDCardBackFullImage != null && !CaptureActivity.IDCardBackFullImage.isRecycled()) {
					CaptureActivity.IDCardBackFullImage.recycle();
				}
				CaptureActivity.IDCardBackFullImage = mCardInfo.stdCardIm;
			}
		}
	}
	
	public void photoRec(Intent data) {
		Uri uri = data.getData();  
        Log.d(TAG, uri.toString());  
        ContentResolver cr = mActivity.getContentResolver();  
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			final Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, opt);
			if (bitmap == null) {
				return;
			}
			pd = ProgressDialog.show(mActivity, null, "正在识别，请稍候");
			/* 解析bitmap, 生成cardInfo */
			new Thread() {
				public void run() {
					// 识别图片
					_photoRec(bitmap);
					// 更新UI
					mHandler.sendEmptyMessage(0);
				}
			}.start();
		} catch (FileNotFoundException e) {
			Log.e("Exception", e.getMessage(), e);
		}
	}   
	
	public EXIDCardResult getRecoResult() {
		return mCardInfo;
	}
}
