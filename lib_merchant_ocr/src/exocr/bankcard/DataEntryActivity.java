package exocr.bankcard;

/* DataEntryActivity.java
 * See the file "LICENSE.md" for the full license governing this code.
 */
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.DateKeyListener;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class DataEntryActivity extends Activity implements TextWatcher {
	
    private static final String PADDING_DIP = "4dip";
    private static final String LABEL_LEFT_PADDING_DEFAULT = "2dip";
    private static final String LABEL_LEFT_PADDING_HOLO = "12dip";
    private static final String FIELD_HALF_GUTTER = PADDING_DIP;
    private int viewIdCounter = 1;
    private static final int editTextIdBase = 100;
    private int editTextIdCounter = editTextIdBase;

    private TextView activityTitleTextView;
    private EditText numberEdit;
    private ImageView cardView;
    private Button doneBtn;
    private Button cancelBtn;
    private EXBankCardInfo capture;
    private EXBankCardInfo recoResult;
    private EXBankCardInfo finalResult;
    
    private LinearLayout cardNumLayout;
    private EditText bankNameET;
    private EditText cardNameET;
    private EditText cardTypeET;
    private EditText validET;
    
    private LinearLayout banknameLayout;
	private LinearLayout cardnameLayout;
	private LinearLayout cardtypeLayout;
	private LinearLayout validdateLayout;
	private LinearLayout cardnumImageLayout;

    private boolean autoAcceptDone;
    private String labelLeftPadding;
        
    private int resultBeginId;//显示结果的EditText控件的起始ID，按Done时获取结果需要用到
    private int resultEndId;  //显示结果的EditText控件的终止ID，按Done时获取结果需要用到

    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
		// hide titlebar of application
		// must be before setting the layout
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		int bankcardrsteditId = ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "layout", "bankcardrstedit");
		setContentView(bankcardrsteditId);
//		setContentView(R.layout.bankcardrstedit);
		
		bankNameET = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankNameEditText"));
		cardNameET = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardNameEditText"));
		cardTypeET = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardTypeEditText"));
		validET = (EditText) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardValidEditText"));
		
		cardNumLayout = (LinearLayout) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardNumBG"));
		
		banknameLayout = (LinearLayout) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankNameBG"));
		cardnameLayout = (LinearLayout) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardNameBG"));
		cardtypeLayout = (LinearLayout) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardTypeBG"));
		validdateLayout = (LinearLayout) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardValidBG"));
		cardnumImageLayout = (LinearLayout) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardImageViewBG"));
		
//		bankNameET = (EditText) findViewById(R.id.bankNameEditText);
//		cardNameET = (EditText) findViewById(R.id.bankcardNameEditText);
//		cardTypeET = (EditText) findViewById(R.id.bankcardTypeEditText);
//		validET = (EditText) findViewById(R.id.bankcardValidEditText);
		
//		cardNumLayout = (LinearLayout) findViewById(R.id.bankcardNumBG);

//		banknameLayout = (LinearLayout) findViewById(R.id.bankNameBG);
//		cardnameLayout = (LinearLayout) findViewById(R.id.bankcardNameBG);
//		cardtypeLayout = (LinearLayout) findViewById(R.id.bankcardTypeBG);
//		validdateLayout = (LinearLayout) findViewById(R.id.bankcardValidBG);
//		cardnumImageLayout = (LinearLayout) findViewById(R.id.bankcardImageViewBG);
		
		if (!EXBankCardInfo.SHOW_BANKNAME_BANK) {
			banknameLayout.setVisibility(View.GONE);
		}
		if (!EXBankCardInfo.SHOW_CARDNAME_BANK) {
			cardnameLayout.setVisibility(View.GONE);
		}
		if (!EXBankCardInfo.SHOW_CARDTYPE_BANK) {
			cardtypeLayout.setVisibility(View.GONE);
		}
		if (!EXBankCardInfo.SHOW_VALIDDATE_BANK) {
			validdateLayout.setVisibility(View.GONE);
		}
		if (!EXBankCardInfo.SHOW_CARDNUM_BANK) {
			cardNumLayout.setVisibility(View.GONE);
		}
		if (!EXBankCardInfo.SHOW_CARDNUM_IMG_BANK) {
			cardnumImageLayout.setVisibility(View.GONE);
		}
		
		initResult();
		
		Bundle extras = getIntent().getExtras();
		if (extras == null)
			throw new IllegalStateException("Didn't find any extras!");
        ActivityHelper.setActivityTheme(this, extras.getBoolean(CardRecoActivity.EXTRA_KEEP_APPLICATION_THEME));
		capture = extras.getParcelable(CardRecoActivity.EXTRA_SCAN_RESULT);
		if (capture == null) {
			return;
		}
		recoResult = capture;
		bankNameET.setText(capture.strBankName);
		cardNameET.setText(capture.strCardName);
		cardTypeET.setText(capture.strCardType);
		validET.setText(capture.strValid);
		ImageView cardImg = (ImageView) findViewById(ViewUtil.getResourseIdByName(getApplicationContext().getPackageName(), "id", "bankcardImageView"));
//		ImageView cardImg = (ImageView) findViewById(R.id.bankcardImageView); 
        cardImg.setImageBitmap(CardRecoActivity.markedCardImage);
        LayoutParams para;  
        para = cardImg.getLayoutParams();
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        para.height = (int)(metrics.widthPixels * 0.2);
        Log.d(TAG, "layout height: " + para.height); 
        cardImg.setLayoutParams(para);  
        cardImg.setScaleType(ScaleType.CENTER_CROP);
		if(capture.strNumbers != ""){
        	LinearLayout resultLayout = new LinearLayout(this);
        	LinearLayout.LayoutParams resultLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        	resultLayout.setOrientation(LinearLayout.HORIZONTAL);
        	resultLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        	resultBeginId = 0;
        	resultEndId = 0;
        	int Id;
        	String [] arr = capture.strNumbers.split(" ");
        	for (int i = 0; i < arr.length; i++){
        		if(arr[i] != ""){
        			EditText result = new EditText(this);            			
        			Id = viewIdCounter++;
        			
        			if(Id > resultBeginId && resultBeginId == 0)
        				resultBeginId = Id;
        			if(Id > resultEndId)
        				resultEndId =Id;
        			LinearLayout.LayoutParams resultParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
                	resultParams.weight = arr[i].length();
        			result.setLayoutParams(resultParams);
        			result.setId(Id);
        			result.setMaxLines(1);
        			result.setTextColor(Color.BLACK);
        			result.setTextSize(20.0f);
        			result.setImeOptions(EditorInfo.IME_ACTION_DONE);
        			result.setTextAppearance(getApplicationContext(), android.R.attr.textAppearanceLarge);
        			result.setInputType(InputType.TYPE_CLASS_PHONE);
        			result.setGravity(Gravity.CENTER);
        			result.setText(arr[i]);
        			//ViewUtil.setMargins(mainLayout, "10dip", "8dip", "10dip", "8dip");
        			resultLayout.addView(result);
        		}
        	}
            cardNumLayout.addView(resultLayout, resultLayoutParams);
        }

    }
    
    private void initResult() {
		recoResult = new EXBankCardInfo();
		finalResult = new EXBankCardInfo();

		recoResult.strBankName = "";
		recoResult.strNumbers = "";
		recoResult.strCardName = "";
		recoResult.strCardType = "";
		recoResult.strValid = "";
	}
    
    private String getConfirmResult() {
    	String result = "";
    	//根据resultBeginId和resultEndId获取结果
    	if(resultBeginId > 0 && resultEndId >= resultBeginId){
    		for (int i = resultBeginId; i <= resultEndId; i++){
    			EditText editText = (EditText)findViewById(i);
    			if(i > resultBeginId)
    				result += " ";
    			if(editText.length() > 0 )
    				result += editText.getText();
    		}
    	}
    	
    	return result;
    }

    private void getFinalResult() {
    	finalResult.strBankName = bankNameET.getText().toString();
    	finalResult.strNumbers = getConfirmResult();
    	finalResult.strCardName = cardNameET.getText().toString();
    	finalResult.strCardType = cardTypeET.getText().toString();
    	finalResult.strValid = validET.getText().toString();
    }
    
    private boolean isEdited() {
		if (finalResult.strBankName.equals(recoResult.strBankName) && finalResult.strNumbers.equals(recoResult.strNumbers) && 
				finalResult.strCardName.equals(recoResult.strCardName) && finalResult.strCardType.equals(recoResult.strCardType) 
				&& finalResult.strValid.equals(recoResult.strValid)) {
			return false;
		}	
		return true;
	}
    
    public void completed(View v) {
    	getFinalResult();
    	
    	Intent intent = new Intent(DataEntryActivity.this, CardRecoActivity.class);
		intent.putExtra(CardRecoActivity.BANK_RECO_RESULT, recoResult);
		intent.putExtra(CardRecoActivity.BANK_FINAL_RESULT, finalResult);
		this.setResult(CardRecoActivity.BANK_RETURN_RESULT, intent);
		
		if(isEdited()) {
			intent.putExtra(CardRecoActivity.BANK_EDITED, true);
		} else {
			intent.putExtra(CardRecoActivity.BANK_EDITED, false);
		}
		
		recoResult = null;
	    finalResult = null;
		
		this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        validateAndEnableDoneButtonIfValid();
         if (numberEdit != null) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        Log.i(TAG, "ready for manual entry"); // used by tests. don't delete.
    }

    private EditText advanceToNextEmptyField() {
        int viewId = editTextIdBase;
        EditText et;
        while ((et = (EditText) findViewById(viewId++)) != null) {
            if (et.getText().length() == 0) {
                if (et.requestFocus())
                    return et;
            }
        }
        // all fields have content
        return null;
    }

    private void validateAndEnableDoneButtonIfValid() {
        //doneBtn.setEnabled(numberValidator.isValid());
        //if (autoAcceptDone && numberValidator.isValid()) {
        //    completed();
        //}
    }

    @Override
    public void afterTextChanged(Editable et) {
    	/*
        if (numberEdit != null && et == numberEdit.getText()) {
            if (numberValidator.hasFullLength()) {
                if (!numberValidator.isValid())
                    numberEdit.setTextColor(Appearance.TEXT_COLOR_ERROR);
                else
                    advanceToNextEmptyField();
            } else
                numberEdit.setTextColor(Appearance.TEXT_COLOR_EDIT_TEXT);
        }

        this.validateAndEnableDoneButtonIfValid();
        */
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // leave empty
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // leave empty

    }
}
