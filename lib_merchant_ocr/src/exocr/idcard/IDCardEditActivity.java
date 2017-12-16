package exocr.idcard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import exocr.exocrengine.EXIDCardResult;

public final class IDCardEditActivity extends Activity {
	public static final String ID_RECO_RESULT = "exocr.idcard.recoResult";
	public static final String ID_FINAL_RESULT = "exocr.idcard.finalResult";
	public static final String ID_EDITED = "exocr.idcard.edited";
	public static final int ID_RETURN_RESULT = 200;

	private static final String PADDING_DIP = "4dip";
	private static final String LABEL_LEFT_PADDING_DEFAULT = "2dip";
	private static final String LABEL_LEFT_PADDING_HOLO = "12dip";
	private static final String FIELD_HALF_GUTTER = PADDING_DIP;
	private int viewIdCounter = 1;
	private static final int editTextIdBase = 100;
	private int editTextIdCounter = editTextIdBase;

	private static final int REQUEST_SCAN = 100;

	private static final int FRONT_BUTTON_ID = 0x1433;
	private static final int BACK_BUTTON_ID = 0x1434;

	private boolean bshouldFront;
	private static final String INTNET_FRONT = "ShouldFront";

	private TextView activityTitleTextView;
	private EditText numberEdit;
	private ImageView cardView;
	private Button doneBtn;
	private Button cancelBtn;
	private EXIDCardResult capture;
	private EXIDCardResult recoResult;
	private EXIDCardResult finalResult;

	private EditText nameValue;
	private EditText sexValue;
	private EditText nationValue;
	private EditText birthdayValue;
	private EditText addressValue;
	private EditText codeValue;
	private EditText officeValue;
	private EditText validDateValue;
		
	private LinearLayout faceImgLayout;
	private LinearLayout nameLayout;
	private LinearLayout sexLayout;
	private LinearLayout nationLayout;
	private LinearLayout birthLayout;
	private LinearLayout addressLayout;
	private LinearLayout cardnumLayout;
	private LinearLayout officeLayout;
	private LinearLayout validLayout;
	private LinearLayout frontFullImageLayout;
	private LinearLayout backFullImageLayout;

	private boolean autoAcceptDone;
	private String labelLeftPadding;

	private int resultBeginId;// 显示结果的EditText控件的起始ID，按Done时获取结果需要用到
	private int resultEndId; // 显示结果的EditText控件的终止ID，按Done时获取结果需要用到

	private final String TAG = this.getClass().getName();
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // hide titlebar of application
        // must be before setting the layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        int idcardrsteditId = ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "layout", "idcardrstedit");
		setContentView(idcardrsteditId);
//        setContentView(R.layout.idcardrstedit);
        Button frontBtn = (Button) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "frontBtn"));
//        Button frontBtn = (Button) findViewById(R.id.frontBtn);
        frontBtn.setId(FRONT_BUTTON_ID);
        Button backBtn = (Button) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "backBtn"));
//        Button backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setId(BACK_BUTTON_ID);
        
        nameValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardNameEditText"));
		sexValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardSexEditText"));
		nationValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardNationEditText"));
		birthdayValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardBirthdayEditText"));
		addressValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardAddressEditText"));
		codeValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardCodeEditText"));
		officeValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardOfficeEditText"));
		validDateValue = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardValidDateEditText"));
//		nameValue = (EditText) findViewById(R.id.IDCardNameEditText);
//		sexValue = (EditText) findViewById(R.id.IDCardSexEditText);
//		nationValue = (EditText) findViewById(R.id.IDCardNationEditText);
//		birthdayValue = (EditText) findViewById(R.id.IDCardBirthdayEditText);
//		addressValue = (EditText) findViewById(R.id.IDCardAddressEditText);
//		codeValue = (EditText) findViewById(R.id.IDCardCodeEditText);
//		officeValue = (EditText) findViewById(R.id.IDCardOfficeEditText);
//		validDateValue = (EditText) findViewById(R.id.IDCardValidDateEditText);
			
		faceImgLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "faceImageViewBG"));
		nameLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardNameBG"));
		sexLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardSexBG"));
		nationLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardNationBG"));
		birthLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardBirthBG"));
		addressLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardAddressBG"));
		cardnumLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardCodeBG"));
		officeLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardOfficeBG"));
		validLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "IDCardValidDateBG"));
		frontFullImageLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "frontFullImageBG"));
		backFullImageLayout = (LinearLayout)findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "backFullImageBG"));
//		faceImgLayout = (LinearLayout)findViewById(R.id.faceImageViewBG);
//		nameLayout = (LinearLayout)findViewById(R.id.IDCardNameBG);
//		sexLayout = (LinearLayout)findViewById(R.id.IDCardSexBG);
//		nationLayout = (LinearLayout)findViewById(R.id.IDCardNationBG);
//		birthLayout = (LinearLayout)findViewById(R.id.IDCardBirthBG);
//		addressLayout = (LinearLayout)findViewById(R.id.IDCardAddressBG);
//		cardnumLayout = (LinearLayout)findViewById(R.id.IDCardCodeBG);
//		officeLayout = (LinearLayout)findViewById(R.id.IDCardOfficeBG);
//		validLayout = (LinearLayout)findViewById(R.id.IDCardValidDateBG);
//		frontFullImageLayout = (LinearLayout)findViewById(R.id.frontFullImageBG);
//		backFullImageLayout = (LinearLayout)findViewById(R.id.backFullImageBG);
        
		WindowManager wm = this.getWindowManager();
		int screenWidth =  wm.getDefaultDisplay().getWidth();
		
		LinearLayout.LayoutParams linearParamsFrontFullImageLayout =(LinearLayout.LayoutParams) frontFullImageLayout.getLayoutParams();   
		linearParamsFrontFullImageLayout.height = (int)(screenWidth * 0.6);
		frontFullImageLayout.setLayoutParams(linearParamsFrontFullImageLayout);
		
		LinearLayout.LayoutParams linearParamsBackFullImageLayout =(LinearLayout.LayoutParams) backFullImageLayout.getLayoutParams();   
		linearParamsBackFullImageLayout.height = (int)(screenWidth * 0.6);
		backFullImageLayout.setLayoutParams(linearParamsBackFullImageLayout);
		
		if (!EXIDCardResult.SHOW_FACEIMG_ID) {
			faceImgLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_NAME_ID) {
			nameLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_SEX_ID) {
			sexLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_NATION_ID) {
			nationLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_BIRTH_ID) {
			birthLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_ADDRESS_ID) {
			addressLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_CARDNUM_ID) {
			cardnumLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_OFFICE_ID) {
			officeLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_VALID_ID) {
			validLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_FRONTFULLIMG_ID) {
			frontFullImageLayout.setVisibility(View.GONE);
		}
		if (!EXIDCardResult.SHOW_BACKFULLIMG_ID) {
			backFullImageLayout.setVisibility(View.GONE);
		}
		
        initResult();         
    }

	private void initResult() {
		recoResult = new EXIDCardResult();
		finalResult = new EXIDCardResult();

		recoResult.name = "";
		recoResult.sex = "";
		recoResult.nation = "";
		recoResult.birth = "";
		recoResult.address = "";
		recoResult.cardnum = "";
		recoResult.office = "";
		recoResult.validdate = "";      
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
			
		if(requestCode == REQUEST_SCAN) {
			Bundle extras = data.getExtras();

			if (extras != null) {
				capture = extras.getParcelable(CaptureActivity.EXTRA_SCAN_RESULT);
				int type = 1;
				if (capture != null) {
					type = capture.type;
				} else {
					return;
				}
				if (type == 1) {
					// ImageView idcardFullImage = (ImageView)
					// findViewById(R.id.IDCardFullImage);
					// idcardFullImage.setImageBitmap(CaptureActivity.IDCardNameImage);
					ImageView faceImg = (ImageView) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "faceImageView"));
//					ImageView faceImg = (ImageView) findViewById(R.id.faceImageView);
					faceImg.setImageBitmap(CaptureActivity.IDCardFaceImage);

					nameValue.setText(capture.name);
					recoResult.name = capture.name;

					sexValue.setText(capture.sex);
					recoResult.sex = capture.sex;

					nationValue.setText(capture.nation);
					recoResult.nation = capture.nation;

					birthdayValue.setText(capture.birth);
					recoResult.birth = capture.birth;

					addressValue.setText(capture.address);
					recoResult.address = capture.address;

					codeValue.setText(capture.cardnum);
					recoResult.cardnum = capture.cardnum;
					
					ImageView frontFullImg = (ImageView) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "frontFullImageView"));
//					ImageView frontFullImg = (ImageView) findViewById(R.id.frontFullImageView);	
					frontFullImg.setScaleType(ScaleType.FIT_XY);
					frontFullImg.setImageBitmap(CaptureActivity.IDCardFrontFullImage);

				} else {
					officeValue.setText(capture.office);
					recoResult.office = capture.office;

					validDateValue.setText(capture.validdate);
					recoResult.validdate = capture.validdate;
					
					ImageView backFullImg = (ImageView) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "backFullImageView"));
//					ImageView backFullImg = (ImageView) findViewById(R.id.backFullImageView);
					backFullImg.setScaleType(ScaleType.FIT_XY);
					backFullImg.setImageBitmap(CaptureActivity.IDCardBackFullImage);
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onClickScan(View v) {
		hardwareSupportCheck();
		Intent scanIntent = new Intent(IDCardEditActivity.this, exocr.idcard.CaptureActivity.class);
		if (v.getId() == FRONT_BUTTON_ID) {
			bshouldFront = true;
		} else if (v.getId() == BACK_BUTTON_ID) {
			bshouldFront = false;
		}
		scanIntent.putExtra(INTNET_FRONT, bshouldFront);
		startActivityForResult(scanIntent, REQUEST_SCAN);
	}
	
	public static boolean hardwareSupportCheck() {
		// Camera needs to open
		Camera c = null;
		boolean ret = true;
		try {
			c = Camera.open();
		} catch (RuntimeException e) {
			// throw new RuntimeException();
			ret = false;
		}
		if (c == null) { // 没有背摄像头
			return false;
		}
		if (ret) {
			c.release();
			c = null;
		}
		return ret;
	}
	
	private void getFinalResult() {
		if (nameValue != null) {
			finalResult.name = nameValue.getText().toString();
		}
		if (sexValue != null) {
			finalResult.sex = sexValue.getText().toString();
		}
		if (nationValue != null) {
			finalResult.nation = nationValue.getText().toString();
		}
		if (birthdayValue != null) {
			finalResult.birth = birthdayValue.getText().toString();
		}
		if (addressValue != null) {
			finalResult.address = addressValue.getText().toString();
		}
		if (codeValue != null) {
			finalResult.cardnum = codeValue.getText().toString();
		}
		if (officeValue != null) {
			finalResult.office = officeValue.getText().toString();
		}
		if (validDateValue != null) {
			finalResult.validdate = validDateValue.getText().toString();
		}
	}
		
	private boolean isEdited() {
		if (finalResult.name.equals(recoResult.name)  && finalResult.sex.equals(recoResult.sex) && finalResult.nation.equals(recoResult.nation) && finalResult.birth.equals(recoResult.birth) && finalResult.address.equals(recoResult.address) && finalResult.cardnum.equals(recoResult.cardnum) && finalResult.office.equals(recoResult.office) && finalResult.validdate.equals(recoResult.validdate)) {
			return false;
		}	
		return true;
	}
	
	public void onIDReturn(View v) {
		//get data from editText
		getFinalResult();
		
		Intent intent = new Intent();
		intent.putExtra(ID_RECO_RESULT, recoResult);
		intent.putExtra(ID_FINAL_RESULT, finalResult);
		this.setResult(ID_RETURN_RESULT, intent);
		
		if(isEdited()) {
			intent.putExtra(ID_EDITED, true);
		} else {
			intent.putExtra(ID_EDITED, false);
		}
		
		recoResult = null;
	    finalResult = null;
		
		this.finish();
	}
}
