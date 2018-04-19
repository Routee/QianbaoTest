package com.routee.qianbaotest.drawable;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @author: Routee
 * @date 2018/4/3
 * @mail wangc4@qianbaocard.com
 * ------------1.本类由Routee开发,阅读、修改时请勿随意修改代码排版格式后提交到git。
 * ------------2.阅读本类时，发现不合理请及时指正.
 * ------------3.如需在本类内部进行修改,请先联系Routee,若未经同意修改此类后造成损失本人概不负责。
 */

public class Rotate3dAnimation extends Animation {
    private final float   mFromDegree;
    private final float   mToDegree;
    private final float   mCenterX;
    private final float   mCenterY;
    private final float   mDepthZ;
    private final boolean mReverse;
    private       Camera  mCamera;

    public Rotate3dAnimation(float fromDegree, float toDegree, float centerX, float centerY, float depthZ, boolean reverse) {
        mFromDegree = fromDegree;
        mToDegree = toDegree;
        mCenterX = centerX;
        mCenterY = centerY;
        mDepthZ = depthZ;
        mReverse = reverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegree = mFromDegree;
        float degrees = fromDegree + (mToDegree - fromDegree) * interpolatedTime;
        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;
        final Matrix matrix = t.getMatrix();
        camera.save();
        if (mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
