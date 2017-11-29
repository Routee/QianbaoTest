package com.routee.qianbaotest.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.routee.qianbaotest.R;

/**
 * Created by Routee on 2017/11/22.
 *
 * @author Routee
 */

public class TwoLevelPullDownLayout extends LinearLayout {

    private View         mHeadView;
    private TextView     mTvNotify;
    private ImageView    mIvRefresh;
    private int          mHeight;
    private int          mRefreshHeight;
    private LayoutParams mParams;
    private View         mWrapedView;
    private boolean                       mRvCanScrollTop   = false;
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            mRvCanScrollTop = recyclerView.canScrollVertically(-1);
            android.util.Log.e("xxxxxx", "mRvCanScrollTop = " + mRvCanScrollTop);
            super.onScrolled(recyclerView, dx, dy);
        }
    };
    private float mPrePointY;                           //记录手指移动前的点的Y坐标
    private float mMovePointY;                          //记录手指移动到的点的Y坐标
    private float mDistanceY;                           //计算出的手指在屏幕上移动的距离
    private int   mToatalDistanceY;
    private boolean isRefresh = true;
    private float   mDy50     = getResources().getDimension(R.dimen.y50);
    private PullDownListener mListener;

    public TwoLevelPullDownLayout(Context context) {
        super(context);
    }

    public TwoLevelPullDownLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TwoLevelPullDownLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(final Context context) {
        mHeadView = LayoutInflater.from(context).inflate(R.layout.view_head_pulldow, null, false);
        addView(mHeadView, 0);
        mParams = (LayoutParams) mHeadView.getLayoutParams();
        final ViewTreeObserver viewTreeObserver = mHeadView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeadView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mHeight = mHeadView.getHeight();
                //android.util.Log.e("xxxxxx", "mHeight = " + mHeight);
                mParams.setMargins(0, -mHeight, 0, 0);
                mHeadView.setLayoutParams(mParams);
                mWrapedView = getChildAt(1);
                if (mWrapedView instanceof RecyclerView) {
                    RecyclerView rv = (RecyclerView) mWrapedView;
                    rv.addOnScrollListener(mOnScrollListener);
                }
            }
        });
        mTvNotify = (TextView) mHeadView.findViewById(R.id.tv_notify);
        mIvRefresh = (ImageView) mHeadView.findViewById(R.id.iv_refresh);
        ViewTreeObserver refreshViewTreeObserver = mIvRefresh.getViewTreeObserver();
        refreshViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mIvRefresh.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mRefreshHeight = mIvRefresh.getHeight();
                android.util.Log.e("xxxxxx", "mRefreshHeight = " + mRefreshHeight);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mPrePointY = ev.getY();
            mMovePointY = ev.getY();
            mDistanceY = 0;
            mToatalDistanceY = 0;
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            mMovePointY = ev.getY();
            mDistanceY = mPrePointY - mMovePointY;
            mToatalDistanceY += mDistanceY;
            mPrePointY = mMovePointY;
            if (mDistanceY < 0 && !mRvCanScrollTop) {
                showHead();
            }
            if (isRefresh) {
                showHead();
            }
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (!isRefresh) {
                return super.dispatchTouchEvent(ev);
            }
            if (-mToatalDistanceY < 3 * (mRefreshHeight + mDy50)) {
                setRefreshComplete();
            } else if (-mToatalDistanceY < 4 * mHeight) {
                showRefresh();
                if (mListener != null) {
                    mListener.refresh();
                }
                //                Toast.makeText(getContext(), "显示刷新", Toast.LENGTH_SHORT).show();
            } else if (-mToatalDistanceY > 4 * mHeight) {
                if (mListener != null) {
                    mListener.secondRefresh();
                }
                //                Toast.makeText(getContext(), "跳到2楼", Toast.LENGTH_SHORT).show();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showHead() {
        int margins = -mHeight - mToatalDistanceY / 3;
        isRefresh = margins > -mHeight;
        mParams.setMargins(0, margins, 0, 0);
        mHeadView.setLayoutParams(mParams);
        if (-mToatalDistanceY > 4 * mHeight) {
            mTvNotify.setText("松开手指，我给你惊喜");
        } else {
            mTvNotify.setText("继续下拉有惊喜");
        }
    }

    private void setRefreshComplete() {
        mParams.setMargins(0, -mHeight, 0, 0);
        AnimationDrawable drawable = (AnimationDrawable) mIvRefresh.getDrawable();
        drawable.stop();
        mHeadView.setLayoutParams(mParams);
        isRefresh = false;
    }

    private void showRefresh() {
        mParams.setMargins(0, (int) (mDy50 - mRefreshHeight), 0, 0);
        mHeadView.setLayoutParams(mParams);
        AnimationDrawable drawable = (AnimationDrawable) mIvRefresh.getDrawable();
        drawable.start();
        isRefresh = false;
        mHeadView.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshComplete();
                Toast.makeText(getContext(), "数据刷新完成", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isRefresh;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isRefresh;
    }

    public interface PullDownListener {
        void refresh();

        void secondRefresh();
    }

    public void setPullDownListener(PullDownListener listener) {
        this.mListener = listener;
    }
}
