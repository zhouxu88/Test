package com.zx.test.templinedetails;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zx.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yc.Zhao on 2018/1/26 0026.
 */

public class ShowYaxisView extends View {
    //Y轴坐标数据
    private float[] mYData = { 32f, 33f, 34f, 35f, 36f, 37f, 38f, 39f, 40f,41f,42f,43f};
    //Y轴单位坐标之间的距离
    private int mYDistance = 60;
    //表格外边缘距离屏幕外边缘之间的间距
    private int mOutSideDistance = 80;
    private  int mYSize;
    //倒装排序
    private List<String> mYUpSideData;
    //Y坐标单个文字宽高矩形
    private Rect mYRect;
    //刻度文字笔
    private Paint mTextPaint;
    //设置 X，Y轴刻度文字的宽高
    private int mXTextWidth;
    private int mXTextHeight;
    private int mYTextWidth;
    private int mYTextHeight;
    public ShowYaxisView(Context context) {
        this(context,null);
    }

    public ShowYaxisView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowYaxisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mYSize = mYData.length;
        mYRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(30f);
        mTextPaint.setColor(context.getResources().getColor(R.color.whiteLine));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //将数据倒装排序
        mYUpSideData = new ArrayList<>();
        int count = mYData.length;
        for (int i=0;i<mYData.length;i++){
            mYUpSideData.add(String.valueOf((int) (mYData[count-1-i])));
        }
        mTextPaint.getTextBounds(mYUpSideData.get(0),0,mYUpSideData.get(0).length(),mYRect);
        mYTextWidth = mYRect.width();
        mYTextHeight = mYRect.height();
        for (int i=0;i<mYUpSideData.size();i++){
            //第一个数据不写出来
            if (i!=0){
                canvas.drawText(mYUpSideData.get(i),mOutSideDistance/2,mOutSideDistance+mYTextHeight/2+mYDistance*i,mTextPaint);
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mOutSideDistance,mYSize*mYDistance+2*mOutSideDistance);
    }
}
