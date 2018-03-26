package com.routee.qianbaotest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.routee.qianbaotest.R;
import com.routee.qianbaotest.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.view.View.MeasureSpec.AT_MOST;

/**
 * @author: Routee
 * @date 2018/3/24
 * @mail wangc4@qianbaocard.com
 * ------------1.本类由Routee开发,阅读、修改时请勿随意修改代码排版格式后提交到git。
 * ------------2.阅读本类时，发现不合理请及时指正.
 * ------------3.如需在本类内部进行修改,请先联系Routee,若未经同意修改此类后造成损失本人概不负责。
 */

public class RouteeFormView extends View {

    public static class Units {
        int    y;
        String x;

        public Units(int y, String x) {
            this.x = x;
            this.y = y;
        }
    }

    private int mMinSize;
    private Map<Integer, List<Units>> mDatas      = new HashMap();  //需要展示的数据
    private Map<Integer, List<Point>> mDataPoints = new HashMap();  //需要展示的点
    private Map<Integer, List<Rect>>  mDataRects  = new HashMap();  //需要展示的点的附近的坐标范围
    private int   mBaseColor;                                       //基础线条颜色
    private Paint mPaint;                                           //画笔
    private int   mBaseStrokeWidth;                                 //基础线条粗细
    private int   mUsefulY;                                         //Y轴数值有效值
    private int   mMinUsefulY;                                      //Y轴数值最小值
    private int   mMaxUsefulY;                                      //Y轴数值最大值
    private int   mYDataSpacing;                                    //Y轴数值间隔大小
    private int   mBaseTextSize;                                    //文字大小
    private int   mMaxXTextWidth;                                   //X轴坐标值最大文字宽度
    private int   mMaxXTextHeight;                                  //X轴坐标值最大文字高度
    private int   mMaxYTextWidth;                                   //Y轴坐标值最大文字宽度
    private int   mMaxYTextHeight;                                  //Y轴坐标值最大文字高度
    private int   mTextMarginX;                                     //X方向文字与表格间距
    private int   mTextMarginY;                                     //Y方向文字与表格间距
    private List<String> mYTexts = new ArrayList<>();               //Y轴坐标值集合
    private List<String> mXTexts = new ArrayList<>();               //X轴坐标值集合
    private float mFormWidth;                                     //表格有效宽度（px值）
    private float mFormHeight;                                    //表格有效高度（px值）
    private int   mXSpacingCount;                                 //X轴坐标值元素的间隔个数
    private int   mLineSpacingCount;                              //Y轴坐标线的间隔个数
    private int   mLineSpacingCountRemainer;                      //Y轴坐标线的首个间隔个数
    private int   mMaxYValue;                                     //Y轴最大值
    private int   mMinYValue;                                     //Y轴最小值
    private float mXPosition;                                     //event事件X轴位置
    private float mYPosition;                                     //event事件Y轴位置
    //    private boolean mIsTouch;                                       //是否有触摸事件
    private Rect  mPreRect;                                       //记录上一次选中点的范围

    public RouteeFormView(Context context) {
        this(context, null);
    }

    public RouteeFormView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RouteeFormView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RouteeFormView);
        mMinSize = a.getInteger(R.styleable.RouteeFormView_min_size, 0);
        mBaseColor = a.getColor(R.styleable.RouteeFormView_base_stroke_color, Color.parseColor("#d0d0d0"));
        mBaseStrokeWidth = a.getInteger(R.styleable.RouteeFormView_base_stroke_width, 1);
        mBaseTextSize = a.getInteger(R.styleable.RouteeFormView_base_text_size, 12);
        mTextMarginX = ScreenUtils.dpToPxInt(getContext(), a.getInteger(R.styleable.RouteeFormView_text_margin_x, 4));
        mTextMarginY = ScreenUtils.dpToPxInt(getContext(), a.getInteger(R.styleable.RouteeFormView_text_margin_y, 4));
        a.recycle();
        initData();
    }

    private void initData() {
        List<Units> list = new ArrayList<>();
        list.add(new Units(80, "一一"));
        list.add(new Units(43, "二二"));
        list.add(new Units(64, "三三"));
        list.add(new Units(30, "四四"));
        list.add(new Units(52, "五五"));
        list.add(new Units(100, "六六"));
        list.add(new Units(120, "七七"));
        list.add(new Units(23, "八八"));
        list.add(new Units(14, "九九"));
        mDatas.put(Color.RED, list);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == AT_MOST && heightSpecMode == AT_MOST) {
            setMeasuredDimension(mMinSize, mMinSize);
        } else if (widthMeasureSpec == AT_MOST) {
            setMeasuredDimension(mMinSize, heightSpecSize);
        } else if (heightMeasureSpec == AT_MOST) {
            setMeasuredDimension(widthSpecSize, mMinSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        calc();
        drawText(canvas);
        drawLines(canvas);
        drawData(canvas);
        drawHelpLine(canvas);
        drawHelpText(canvas);
    }

    /**
     * 绘制坐标值
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }
        mPaint.setColor(mBaseColor);
        mPaint.setTextSize(ScreenUtils.dpToPx(getContext().getApplicationContext(), mBaseTextSize));
        Rect bounds = new Rect();
        for (int i = 0; i < mYTexts.size(); i++) {
            mPaint.getTextBounds(mYTexts.get(i), 0, mYTexts.get(i).length(), bounds);
            canvas.drawText(mYTexts.get(i), mMaxYTextWidth - bounds.width(), i * mFormHeight / (mYTexts.size() - 1) + mMaxYTextHeight, mPaint);
        }
        for (int i = 0; i < (mXTexts.size() - 1) / mXSpacingCount + 1; i++) {
            mPaint.getTextBounds(mXTexts.get(i), 0, mXTexts.get(i).length(), bounds);
            float x = mMaxYTextWidth + mTextMarginX - bounds.width() / 2 + i * mXSpacingCount * mFormWidth / (mXTexts.size() - 1);
            float y = mFormHeight + mMaxXTextHeight + mTextMarginY + mMaxYTextHeight - 1;
            canvas.drawText(mXTexts.get(i * mXSpacingCount), x, y, mPaint);
        }
    }

    /**
     * 绘制坐标系及辅助坐标
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }
        mPaint.setColor(mBaseColor);
        mPaint.setStrokeWidth(mBaseStrokeWidth);
        for (int i = 0; i < mYTexts.size(); i += mLineSpacingCount) {
            float yPosition = (i + mLineSpacingCountRemainer) * mFormHeight / (mYTexts.size() - 1) + mBaseStrokeWidth / 2 + mMaxYTextHeight;
            canvas.drawLine(mMaxYTextWidth + mTextMarginX, yPosition, getWidth() - mMaxXTextWidth / 2, yPosition, mPaint);
        }
    }

    /**
     * 绘制数据
     *
     * @param canvas
     */
    private void drawData(Canvas canvas) {
        Iterator<Integer> it = mDataPoints.keySet().iterator();
        while (it.hasNext()) {
            Path path = new Path();
            Integer color = it.next();
            List list = mDataPoints.get(color);
            for (int i = 0; i < list.size(); i++) {
                Point o = (Point) list.get(i);
                if (i == 0) {
                    path.moveTo(o.x, o.y);
                } else {
                    path.lineTo(o.x, o.y);
                }
            }
            mPaint.setColor(color);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, mPaint);
        }
    }

    /**
     * 绘制辅助坐标
     *
     * @param canvas
     */
    private void drawHelpLine(Canvas canvas) {
        Iterator<Integer> it = mDataRects.keySet().iterator();
        int color = mBaseColor;
        while (it.hasNext()) {
            color = it.next();
            List<Rect> rects = mDataRects.get(color);
            for (Rect rect : rects) {
                if (rect.contains((int) mXPosition, (int) mYPosition)) {
                    mPreRect = new Rect(rect.centerX() - 4, rect.centerY() - 4, rect.centerX() + 4, rect.centerY() + 4);
                    break;
                }
            }
        }
        if (mPreRect != null) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(color);
            canvas.drawLine(mPreRect.centerX(), 0, mPreRect.centerX(), mFormHeight + mMaxYTextHeight, mPaint);
            canvas.drawLine(mMaxYTextWidth + mTextMarginY, mPreRect.centerY(), getWidth() - mMaxXTextWidth / 2 + 1, mPreRect.centerY(), mPaint);
            canvas.drawCircle(mPreRect.centerX(), mPreRect.centerY(), 4, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            canvas.drawCircle(mPreRect.centerX(), mPreRect.centerY(), 3, mPaint);
        }
    }

    private void drawHelpText(Canvas canvas) {

    }

    private void calc() {
        calcMaxYValue();
        calcMinYValue();
        calcYSpacing();
        calcYTextList();
        calcTextSize();
        calcFormSize();
        calcXTextList();
        calcBaseLines();
        calcData();
    }

    /**
     * 计算Y轴最大值
     *
     * @return Y轴最大值
     */
    private void calcMaxYValue() {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        int max = 0;
        for (Integer color : mDatas.keySet()) {
            for (Units units : mDatas.get(color)) {
                max = Math.max(max, units.y);
            }
        }
        mMaxUsefulY = max;
    }

    /**
     * 计算Y轴最小值
     *
     * @return Y轴最小值
     */
    private void calcMinYValue() {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        int min = 0;
        for (Integer color : mDatas.keySet()) {
            for (Units units : mDatas.get(color)) {
                if (min == 0) {
                    min = units.y;
                    continue;
                }
                min = Math.min(min, units.y);
            }
        }
        mMinUsefulY = min;
    }

    /**
     * 计算Y轴数值间隔大小
     */
    private void calcYSpacing() {
        mUsefulY = mMaxUsefulY - mMinUsefulY;
        int minSpacing = mUsefulY / 6;
        if (minSpacing == 0) {
            int w = (mMaxUsefulY + "").length();
            int spacing = w / 10;
            if (spacing != 0) {
                mYDataSpacing = spacing;
            } else {
                mYDataSpacing = 20;
            }
            return;
        }
        String s = minSpacing + "";
        int length = s.length() - 1 > 0 ? s.length() - 1 : 0;
        int unit = (int) (1 * Math.pow(10, length));
        for (int i = 1; i <= 9; i += 1) {
            if (mUsefulY / (i * unit) < 6) {
                mYDataSpacing = i * unit;
                return;
            }
        }
    }

    /**
     * 计算Y坐标值数值集合
     */
    private void calcYTextList() {
        mYTexts = new ArrayList<>();
        int remainder = mMaxUsefulY % mYDataSpacing;
        for (int i = mMaxUsefulY - remainder + mYDataSpacing; i >= mMinUsefulY - mYDataSpacing; i -= mYDataSpacing) {
            mYTexts.add(i + "");
        }
        String maxY = mYTexts.get(0);
        mMaxYValue = Integer.parseInt(maxY);
        String minY = mYTexts.get(mYTexts.size() - 1);
        mMinYValue = Integer.parseInt(minY);
    }

    /**
     * 计算所有文字大小
     */
    private void calcTextSize() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }
        String xMax = "";
        for (Integer integer : mDatas.keySet()) {
            List<Units> units = mDatas.get(integer);
            for (Units unit : units) {
                xMax = unit.x.length() > xMax.length() ? unit.x : xMax;
            }
        }
        mPaint.setTextSize(ScreenUtils.dpToPx(getContext().getApplicationContext(), mBaseTextSize));
        Rect bounds = new Rect();
        mPaint.getTextBounds(xMax, 0, xMax.length(), bounds);
        mMaxXTextHeight = bounds.height();
        mMaxXTextWidth = bounds.width();
        mPaint.getTextBounds(mYTexts.get(0), 0, mYTexts.get(0).length(), bounds);
        mMaxYTextHeight = bounds.height();
        mMaxYTextWidth = bounds.width();
        mMaxXTextHeight = Math.max(mMaxXTextHeight, mMaxYTextHeight);
        mMaxYTextHeight = Math.max(mMaxXTextHeight, mMaxYTextHeight);
        mMaxYTextWidth = Math.max(mMaxYTextWidth, mMaxXTextWidth / 2 - mTextMarginX);
    }

    /**
     * 计算表格大小
     */
    private void calcFormSize() {
        mFormWidth = getWidth() - mTextMarginX - mMaxYTextWidth - mMaxXTextWidth / 2 - 1;
        mFormHeight = getHeight() - mTextMarginY - mMaxXTextHeight - mMaxYTextWidth;
    }

    /**
     * 计算X轴所有数值
     */
    private void calcXTextList() {
        mXTexts.clear();
        mXSpacingCount = 1;
        Iterator<Integer> it = mDatas.keySet().iterator();
        if (it.hasNext()) {
            Integer next = it.next();
            List<Units> units = mDatas.get(next);
            while ((units.size() / mXSpacingCount + 1) * mMaxXTextWidth > mFormWidth) {
                mXSpacingCount++;
            }
            for (int i = 0; i < units.size(); i++) {
                mXTexts.add(units.get(i).x + "");
            }
        }
    }

    /**
     * 计算基础线条
     */
    private void calcBaseLines() {
        mLineSpacingCount = (mYTexts.size() - 1) * 2 / 5;
        mLineSpacingCountRemainer = (mYTexts.size() - 1) % mLineSpacingCount;
    }

    private void calcData() {
        Iterator<Integer> it = mDatas.keySet().iterator();
        int size = mXTexts.size();
        while (it.hasNext()) {
            List<Point> listPoint = new ArrayList<>();
            List<Rect> listRect = new ArrayList<>();
            Integer color = it.next();
            List<Units> units = mDatas.get(color);
            for (int i = 0; i < units.size(); i++) {
                float x = i * mFormWidth / (size - 1) + mMaxYTextWidth + mTextMarginY;
                float y = (mMaxYValue - units.get(i).y) * mFormHeight / (mMaxYValue - mMinYValue) + mMaxYTextHeight;
                listPoint.add(new Point((int) x, (int) y));
                listRect.add(new Rect((int) (x - 20), (int) (y - 20), (int) (x + 20), (int) (y + 20)));
            }
            mDataPoints.put(color, listPoint);
            mDataRects.put(color, listRect);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            return false;
        }
        mXPosition = event.getX();
        mYPosition = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //                mIsTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                //                mIsTouch = false;
                break;
        }
        //        if (mIsTouch) {
        invalidate();
        //        }
        return true;
    }
}
