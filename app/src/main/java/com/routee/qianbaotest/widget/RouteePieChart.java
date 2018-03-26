package com.routee.qianbaotest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.routee.qianbaotest.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec.AT_MOST;

/**
 * @author: Routee
 * @date 2018/3/13
 * @mail wangc4@qianbaocard.com
 * ------------1.本类由Routee开发,阅读、修改时请勿随意修改代码排版格式后提交到git。
 * ------------2.阅读本类时，发现不合理请及时指正.
 * ------------3.如需在本类内部进行修改,请先联系Routee,若未经同意修改此类后造成损失本人概不负责。
 */

public class RouteePieChart extends View {
    public static final int CHART_WIDTH = 20;
    private final int mMinSize;
    private int   mWidthRate = CHART_WIDTH;
    private Paint mPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mRadius;
    private ArrayList<Unit> mUnits = new ArrayList<>();
    private int mTotalValue;
    private int mStrokeWidth;

    public RouteePieChart(Context context) {
        this(context, null);
    }

    public RouteePieChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RouteePieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RouteePieChart);
        mWidthRate = a.getInteger(R.styleable.RouteePieChart_width, CHART_WIDTH);
        mMinSize = a.getInteger(R.styleable.RouteePieChart_min_size, 0);
        a.recycle();
        init();
    }

    private void init() {
        mUnits.add(new Unit(100, Color.RED));
        mUnits.add(new Unit(120, Color.GREEN));
        mUnits.add(new Unit(140, Color.BLUE));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;
        mRadius = Math.min(width, height) / 2;
        mRadius = mRadius < 0 ? 0 : mRadius;
        mStrokeWidth = mRadius * mWidthRate / 100;
        //        mPaint.setStyle(Paint.Style.STROKE);
        calcTotalValue();
        drawArc(canvas, mRadius, width, height);
    }

    private void calcTotalValue() {
        mTotalValue = 0;
        for (Unit unit : mUnits) {
            mTotalValue += unit.mValue;
        }
    }

    private void drawArc(Canvas canvas, int radius, int width, int height) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int startValue = 0;
        for (Unit unit : mUnits) {
            mPaint.setColor(unit.mColor);
            RectF oval = new RectF(paddingLeft + width / 2 - radius
                                          , paddingTop + height / 2 - radius
                                          , paddingLeft + width / 2 + radius
                                          , paddingTop + height / 2 + radius);
            canvas.drawArc(oval, startValue * 360 / mTotalValue + 270, unit.mValue * 360 / mTotalValue, true, mPaint);
            startValue += unit.mValue;
        }
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(paddingLeft + width / 2, paddingTop + height / 2, mRadius - mStrokeWidth, mPaint);
    }

    public static class Unit {
        public int mValue;
        public int mColor;

        public Unit(int value, int color) {
            mValue = value;
            mColor = color;
        }
    }

    public void setData(List<Unit> list) {
        mUnits.clear();
        mUnits.addAll(list);
        invalidate();
    }
}
