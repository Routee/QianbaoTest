package exocr.bankcard;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.UUID;

/**
 * Describes a card.             version 2.0
 */
public final class EXBankCardInfo implements Parcelable {	
	//是否显示结果activity
	public static boolean SHOW_RESULT_ACTIVITY_BANK = false;
	//是否显示卡号		SHOW_RESULT_ACTIVITY置true有效
	public static boolean SHOW_CARDNUM_BANK = true;
	//是否显示银行名称	SHOW_RESULT_ACTIVITY置true有效
	public static boolean SHOW_BANKNAME_BANK = true;
	//是否显示卡名称	SHOW_RESULT_ACTIVITY置true有效
	public static boolean SHOW_CARDNAME_BANK = true;
	//是否显示卡类型	SHOW_RESULT_ACTIVITY置true有效
	public static boolean SHOW_CARDTYPE_BANK = true;
	//是否显示有效期	SHOW_RESULT_ACTIVITY置true有效
	public static boolean SHOW_VALIDDATE_BANK = true;
	//是否显示截图		SHOW_RESULT_ACTIVITY置true有效
	public static boolean SHOW_CARDNUM_IMG_BANK = true;
	//卡号是否有空格
	public static boolean BANKCARD_NUM_SPACE = false;
	//是否展示logo
	public final static boolean DISPLAY_LOGO = true;
	//是否过滤银行
	public final static boolean FILTER_BANK = false;
	//recognition data
	public int charCount;
	public char []numbers;
	public Rect []rects;
	public String strNumbers;
	//bitmap to show
	public Bitmap bitmap;
	public Bitmap fullImage;
	//bank Name
	public String strBankName; //银行名称
	public String strCardName; //卡名称
	public String strCardType; //卡类型：借记卡 准贷记卡和贷记卡 预付卡
	public String strValid;
	public int expiryMonth;	   //两个数字的月1-12
	public int expiryYear;     //4个数字的年
	public int bFlip;          //是否是倒立识别的结果
	public int nType;          // 0 不清楚，1平字，2凸字
	public int nRate;          //字符的平均可信度*1024
	//time
	public long timestart;
	public long timeend;
	public float focusScore;
	
	// these should NOT be public
    String scanId;
	
	public EXBankCardInfo() {
		numbers = new char[32];
		rects   = new Rect[32];
		charCount = 0;
		bitmap = null; //bitmap result
		fullImage = null;
		strBankName = null;
		focusScore = 0;
		
		strCardName = null;
		strCardType = null;
		strValid = null;
		expiryMonth = 0;
		expiryYear  = 0;
		bFlip = 0;
		nType = 0;
		nRate = 0;
		
		/////////////////////////////////////
		scanId = UUID.randomUUID().toString();
	}
	//TODO add code
    // parcelable
    private EXBankCardInfo(Parcel src) {
    	numbers = new char[32];
		rects   = new Rect[32];
		charCount = 0;
		bitmap = null; //bitmap result
		fullImage = null;
		strBankName = null;
		focusScore = 0;
		
		strCardName = null;
		strCardType = null;
		strValid = null;
		expiryMonth = 0;
		expiryYear  = 0;
		bFlip = 0;
		nType = 0;
		nRate = 0;
		
		expiryMonth = src.readInt();
		expiryYear  = src.readInt();
    	charCount   = src.readInt();
    	
    	src.readCharArray(numbers);
    	for(int i = 0; i < charCount; ++i){
    		int left, top, right, bottom;
    		left = src.readInt();
    		top  = src.readInt();
    		right = src.readInt();
    		bottom = src.readInt();
    		rects[i] = new Rect(left, top, right, bottom);
    	}
    	strNumbers = src.readString();
    	strBankName = src.readString();
    	strCardName = src.readString();
    	strCardType = src.readString();
    	strValid = src.readString();
    	
        scanId = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public final void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(expiryMonth);
    	dest.writeInt(expiryYear);
    	dest.writeInt(charCount);
    	dest.writeCharArray(numbers);
    	for(int i = 0; i < charCount; ++i){
    		dest.writeInt(rects[i].left);
    		dest.writeInt(rects[i].top);
    		dest.writeInt(rects[i].right);
    		dest.writeInt(rects[i].bottom);
    	}
    	dest.writeString(strNumbers);
    	dest.writeString(strBankName);
    	dest.writeString(strCardName);
    	dest.writeString(strCardType);
    	dest.writeString(strValid);
        dest.writeString(scanId);
    }

    public static final Parcelable.Creator<EXBankCardInfo> CREATOR = new Parcelable.Creator<EXBankCardInfo>() {

        @Override
        public EXBankCardInfo createFromParcel(Parcel source) {
            return new EXBankCardInfo(source);
        }

        @Override
        public EXBankCardInfo[] newArray(int size) {
            return new EXBankCardInfo[size];
        }
    };
	
	/** @return raw text to show */
	public String getText() {
		long timeescape = timeend - timestart;
		String text = "CardNumber:" + strNumbers;
		text += "\nRecoTime=" + timeescape;
		return text;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return strBankName + "\n" + strCardName + "\n" + strCardType + "\n" + strValid + "\n" + strNumbers;
	}
}
