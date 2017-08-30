package com.exocr.exocr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import exocr.bankcard.EXBankCardInfo;
import exocr.exocrengine.EXIDCardResult;
import exocr.exocrengine.EXOCREngine;
import exocr.exocrengine.DictManager;
import exocr.idcard.IDCardEditActivity;

public class MainActivity extends Activity{
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int MY_SCAN_REQUEST_CODE = 100; // arbitrary int
	//对于不同的识别应用，定义不同的请求码
	public static final int MY_SCAN_REQUEST_CODE_BANK = 101; //银行卡识别请求码
	public static final int MY_SCAN_REQUEST_CODE_ID = 102; //身份证识别请求码
	//银行卡截图
	private Bitmap BankCardImage = null;
	private Bitmap BankFullImage = null;
	//身份证截图
	private Bitmap IDCardFrontFullImage = null;
	private Bitmap IDCardBackFullImage = null;
	private Bitmap IDCardFaceImage = null;
	
	private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
            return sbar;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return 0;
    }
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		WindowManager wm = this.getWindowManager();

		int screenHeight =  wm.getDefaultDisplay().getHeight();
		int h = getStatusBarHeight();
		screenHeight -= h;
		
	    ImageView barIV =  (ImageView) findViewById(R.id.barIV);
	    LinearLayout.LayoutParams linearParamsBar =(LinearLayout.LayoutParams) barIV.getLayoutParams();   
	    linearParamsBar.height = screenHeight / 3; 
		  
	    ImageView  bankIV = (ImageView) findViewById(R.id.bankCardIV);
		LinearLayout.LayoutParams linearParamsBank =(LinearLayout.LayoutParams) bankIV.getLayoutParams(); 
		linearParamsBank.height = screenHeight / 6;
		
		ImageView  CZKIV = (ImageView) findViewById(R.id.czkCardIV);
		LinearLayout.LayoutParams linearParamsCZK =(LinearLayout.LayoutParams) CZKIV.getLayoutParams();  
		linearParamsCZK.height = screenHeight / 6;
		
		
		ImageView  IDIV = (ImageView) findViewById(R.id.idCardIV);
		LinearLayout.LayoutParams linearParamsID =(LinearLayout.LayoutParams) IDIV.getLayoutParams(); 
		linearParamsID.height = screenHeight / 6;
		
		ImageView  VEIV = (ImageView) findViewById(R.id.veCardIV);
		LinearLayout.LayoutParams linearParamsVE =(LinearLayout.LayoutParams) VEIV.getLayoutParams();  
		linearParamsVE.height = screenHeight / 6;
		
		
		ImageView  DRIV = (ImageView) findViewById(R.id.DRCardIV);
		LinearLayout.LayoutParams linearParamsDR =(LinearLayout.LayoutParams) DRIV.getLayoutParams();   
		linearParamsDR.height = screenHeight / 6;
		
		ImageView  FaceIV = (ImageView) findViewById(R.id.FaceCardIV);
		LinearLayout.LayoutParams linearParamsFace =(LinearLayout.LayoutParams) FaceIV.getLayoutParams();  
		linearParamsFace.height = screenHeight / 6;
		
		ImageView  QrIV = (ImageView) findViewById(R.id.QrIV);
		LinearLayout.LayoutParams linearParamsBtmQr =(LinearLayout.LayoutParams) QrIV.getLayoutParams();   
		linearParamsBtmQr.height = screenHeight / 6;
	    
	  
		ImageView  PhotoImportIV = (ImageView) findViewById(R.id.PhotoImportIV);
		LinearLayout.LayoutParams linearParamsPhotoImport =(LinearLayout.LayoutParams) PhotoImportIV.getLayoutParams();   
		linearParamsPhotoImport.height = screenHeight / 6;
	    
		
		ImageView  aboutIV = (ImageView) findViewById(R.id.aboutIV);
		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) aboutIV.getLayoutParams();   
		linearParams.height = screenHeight / 6;
		  
		aboutIV.setLayoutParams(linearParams); 
		
		//初始化字典
		DictManager.InitDict(this);
	}
		
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		DictManager.FinishDict();
		super.onDestroy();
	}
	
	public void onBankBtnPress(View v) {
			Intent scanIntent = new Intent(this, exocr.bankcard.CardRecoActivity.class);
			startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE_BANK);			
	}
	
	public void onIDBtnPress(View v) {
			Intent scanIntent = new Intent(this, exocr.idcard.IDCardEditActivity.class);
			startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE_ID);	
	}
	
	public void onVEBtnPress(View v) {		
	}
	
	public void onDRBtnPress(View v) {			
	}
	
	public void onCZKBtnPress(View v) {			
	}
	
	public void onQRBtnPress(View v) {			
	}
	
	public void onPhotoImportPress(View v) {
		
	}
	
	// 读取银行卡识别及最终结果并存储于结构体中
	private void getBankCardResult(int resultCode, Intent data) {
		if (data != null && data.hasExtra(exocr.bankcard.CardRecoActivity.BANK_RECO_RESULT)) {
			if (resultCode == exocr.bankcard.CardRecoActivity.BANK_RETURN_RESULT) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					EXBankCardInfo recoResult = extras.getParcelable(exocr.bankcard.CardRecoActivity.BANK_RECO_RESULT); // 识别结果
					EXBankCardInfo finalResult = extras
							.getParcelable(exocr.bankcard.CardRecoActivity.BANK_FINAL_RESULT); // 最终结果，可能被修改过
					boolean edited = extras.getBoolean(exocr.bankcard.CardRecoActivity.BANK_EDITED); // 是否修改过
					Log.d(TAG, "recogResult:" + recoResult.toString());
					Log.d(TAG, "finalResult:" + finalResult.toString());
					Log.d(TAG, "edited:" + edited);
					// 获取照片
					BankCardImage = exocr.bankcard.CardRecoActivity.markedCardImage;
					BankFullImage = exocr.bankcard.CardRecoActivity.cardFullImage;
				}
			}
		}
	}

	// 读取身份证识别及最终结果
	private void getIDCardResult(int resultCode, Intent data) {
		if (data != null && data.hasExtra(exocr.idcard.IDCardEditActivity.ID_RECO_RESULT)) {
			if (resultCode == exocr.idcard.IDCardEditActivity.ID_RETURN_RESULT) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					EXIDCardResult recoResult = extras.getParcelable(exocr.idcard.IDCardEditActivity.ID_RECO_RESULT);
					EXIDCardResult finalResult = extras.getParcelable(exocr.idcard.IDCardEditActivity.ID_FINAL_RESULT);
					boolean edited = extras.getBoolean(exocr.idcard.IDCardEditActivity.ID_EDITED);
					Log.d(TAG, "recogResult:" + recoResult.toString());
					Log.d(TAG, "finalResult:" + finalResult.toString());
					Log.d(TAG, "edited:" + edited);
					// 获取照片
					IDCardFrontFullImage = exocr.idcard.CaptureActivity.IDCardFrontFullImage;
					IDCardBackFullImage = exocr.idcard.CaptureActivity.IDCardBackFullImage;
					IDCardFaceImage = exocr.idcard.CaptureActivity.IDCardFaceImage;
				}
			}
		}
	}
	
	// 当识别结束时，相应的activity被销毁后，此函数会被调用
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, String.format("onActivityResult(requestCode:%d, resultCode:%d, ...", requestCode, resultCode));

		// 根据不同的请求码，调用不同的处理函数，从而区分银行卡、身份证、行驶证或图片导入等识别
		if (requestCode == MY_SCAN_REQUEST_CODE_BANK) {
			getBankCardResult(resultCode, data);
		} else if (requestCode == MY_SCAN_REQUEST_CODE_ID) {
			getIDCardResult(resultCode, data);
		}
	}

}
