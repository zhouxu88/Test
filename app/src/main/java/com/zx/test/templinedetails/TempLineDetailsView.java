package com.zx.test.templinedetails;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zx.test.R;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 温度曲线（详情页大图）
 * Created by yc.Zhao on 2018/1/24 0024.
 */

public class TempLineDetailsView extends View {
    private Context mContext;
    private Canvas mCanvas;
    //屏幕宽度，用来处理滑动位置
    private int widthPixels;
    //X轴坐标数据
    private String[] mXDate = {"6:00", "6:05", "6:10", "6:15", "6:20", "6:25", "6:30", "6:35", "6:40", "6:45"};
    //Y轴坐标数据
    private float[] mYData = {32f, 33f, 34f, 35f, 36f, 37f, 38f, 39f, 40f, 41f, 42f, 43f};
    //数据源数据
    private float[] mData = {35f, 38f, 33f, 35f, 34f, 36f, 38f, 37f, 36f, 33f};
    //将数据源造成数据点
    private List<Point> mPointData;
    //数据源的长度
    private int mXSize;
    private int mYSize;
    //X轴单位坐标之间的距离
    private int mXDistance = 120;
    //Y轴单位坐标之间的距离
    private int mYDistance = 60;
    //表格外边缘距离屏幕外边缘之间的间距
    private int mOutSideDistance = 80;
    //表格线的笔
    private Paint mLinePaint;

    //X坐标单个文字宽度矩形
    private Rect mXRect;
    //Y坐标单个文字宽高矩形
    private Rect mYRect;
    //刻度文字笔
    private Paint mTextPaint;
    //设置 X，Y轴刻度文字的宽高
    private int mXTextWidth;
    private int mXTextHeight;
    private int mYTextWidth;
    private int mYTextHeight;
    //倒装排序
    private List<String> mYUpSideData;
    //曲线的Path
    private Path mArcPath;
    private Path mNewPath;
    private float lineSmoothness = 0.2f;
    private Paint mArcPaint;
    private float drawScale = 1f;
    //用来存储三阶贝塞尔曲线
    private PathMeasure mPathMeasure;
    private float[] pos;

    //定义一个阴影渐变颜色笔
    private Paint mShadowPaint;

    //跟随手指移动相关
    private int mLastX, mLastY;

    //当前时间
    private String mCurrentTime = "6:30";
    //当前时间的温度
    private float mCurrentTem = 38f;
    //焦点圆绿色笔
    private Paint mInnerCriclePaint;
    //焦点外圆白笔
    private Paint mOutSideCriclePaint;
    private Paint mCurrentPaint;
    //焦点上不规则图形的path
    private Path mShowPath;
    //小三角形的边长
    private float mSide = 26;
    //小三角形的高
    private float mHeight = 12;
    //不规则图形笔
    private Paint mShowPaint;
    //圆角矩形的高度
    private float mCricleRectHeight = 50;
    //温度文字笔
    private Paint mTemPaint;

    //接受一个接口，用来判断当一个viewY轴是否隐藏
    private IsYaxisShow isYaxisShow;


    private int downX = 0;
    private int downCurrentY = 0;

    public IsYaxisShow getIsYaxisShow() {
        return isYaxisShow;
    }

    public void setIsYaxisShow(IsYaxisShow isYaxisShow) {
        this.isYaxisShow = isYaxisShow;
    }

    public TempLineDetailsView(Context context) {
        this(context, null);
    }

    public TempLineDetailsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempLineDetailsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mXSize = mXDate.length;
        mYSize = mYData.length;
        mXDistance = dp2px(40);
        mYDistance = dp2px(20);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(1.5f);
        mLinePaint.setColor(mContext.getResources().getColor(R.color.whiteLine));

        mXRect = new Rect();
        mYRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(30f);
        mTextPaint.setColor(mContext.getResources().getColor(R.color.whiteLine));

        mArcPath = new Path();
        mPointData = new ArrayList<>();

        mArcPaint = new Paint();
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(12f);
        mArcPaint.setColor(mContext.getResources().getColor(R.color.white));

        mNewPath = new Path();
        mShadowPaint = new Paint();

        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setStrokeWidth(1.5f);
        mCurrentPaint.setColor(mContext.getResources().getColor(R.color.gray));

        mInnerCriclePaint = new Paint();
        mInnerCriclePaint.setAntiAlias(true);
        mInnerCriclePaint.setStyle(Paint.Style.FILL);
        mInnerCriclePaint.setStrokeWidth(1.5f);
        mInnerCriclePaint.setColor(mContext.getResources().getColor(R.color.main));

        mOutSideCriclePaint = new Paint();
        mOutSideCriclePaint.setStyle(Paint.Style.STROKE);
        mOutSideCriclePaint.setAntiAlias(true);
        mOutSideCriclePaint.setStrokeWidth(5f);
        mOutSideCriclePaint.setColor(mContext.getResources().getColor(R.color.white));

        mShowPath = new Path();
        mShowPaint = new Paint();
        mShowPaint.setStyle(Paint.Style.FILL);
        mShowPaint.setAntiAlias(true);
        mShowPaint.setColor(mContext.getResources().getColor(R.color.white));

        mTemPaint = new Paint();
        mTemPaint.setAntiAlias(true);
        mTemPaint.setTextSize(30f);
        mTemPaint.setColor(mContext.getResources().getColor(R.color.main));

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;

        if (mXDate.length < 1 || mData.length < 1) {
            return;
        }

        //画表格坐标
        drawCoordinate();
        //画坐标刻度弧线
        drawArc();
        //绘制阴影
        drawShadow();
        //画表格线,因为会被阴影盖住 所以我拿到了后面
        drawLine();
        //处理当前时间相关
        drawCurrentTimeAbout();

        //画点击相关
        drawDownAbout(downX, downCurrentY);
    }

    private void drawDownAbout(int x, int downCurrentY) {
        if (x == 0) {
            return;
        }
        //根据点击的x，获取到当前x轴时间
        int i = (x - mOutSideDistance) / mXDistance;
        int i1 = (x - mOutSideDistance) - i * mXDistance;
        int i2 = 5 * i1 / mXDistance;
        String s = mXDate[i];
        Log.e("TAG", "drawDownLine: " + i);
        String substring = s.substring(s.length() - 2, s.length());
        int i3 = Integer.parseInt(substring) + i2;
        String substring1 = s.substring(0, s.length() - 3);
        String min = String.valueOf(i3);
        if (min.length() < 2) {
            min = "0" + min;
        }
        String downTime = substring1 + ":" + min;
        Log.e("TAG", "drawDownLine: " + downTime);
        mCanvas.drawLine(downX, mOutSideDistance, downX, mYDistance * (mYSize) + mOutSideDistance, mCurrentPaint);

        //获取当前时间Y轴坐标
//        float mCurrentYPosi = getDownPointTimeY(downCurrentY);
//        float mCurrentYPosi


        //定义不规则图形,从三角形顶点开始定义

        //顶点y坐标
        if (downCurrentY == 0) {
            return;
        } else {
            mCanvas.drawCircle(x, downCurrentY, 10, mInnerCriclePaint);
            mCanvas.drawCircle(x, downCurrentY, 10, mOutSideCriclePaint);
            float mVertex = downCurrentY - 12;
            Path p = new Path();
            p.moveTo(x, mVertex);
            p.lineTo(x + mSide / 2, mVertex - mHeight);
            p.lineTo(x + mSide / 2 - mSide, mVertex - mHeight);
            p.close();
            mCanvas.drawPath(p, mShowPaint);
            mCanvas.drawCircle(x + mSide / 2 + mSide, mVertex - mHeight - mCricleRectHeight / 2, mCricleRectHeight / 2, mShowPaint);
            mCanvas.drawCircle(x - mSide / 2 - mSide, mVertex - mHeight - mCricleRectHeight / 2, mCricleRectHeight / 2, mShowPaint);
            mCanvas.drawRect(x - mSide / 2 - mSide, mVertex - mHeight - mCricleRectHeight, x + mSide / 2 + mSide, mVertex - mHeight, mShowPaint);

            //画温度文字
            Rect rt = new Rect();
            String text = mCurrentTem + "°C";
            mTemPaint.getTextBounds(text, 0, text.length(), rt);
            int width = rt.width();
            int height = rt.height();
            mCanvas.drawText(text, x - width / 2, downCurrentY - mHeight - height, mTemPaint);
        }


        //画地下X轴的圆角矩形

//        mCanvas.drawText(mXDate[i],mOutSideDistance/2,mOutSideDistance+mYDistance*mYSize+mOutSideDistance/2,mTextPaint);
        //最下方圆角矩形相关
        if (x == 0) {
            return;
        } else {
            float mUnderHeight = mOutSideDistance / 2;
            float mUnderWidth = mXDistance / 2;
            mCanvas.drawRect(x - mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + 10,
                    x + mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + mUnderHeight + 10, mShowPaint);
            mCanvas.drawCircle(x - mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + 10 + mUnderHeight / 2, mUnderHeight / 2, mShowPaint);
            mCanvas.drawCircle(x + mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + 10 + mUnderHeight / 2, mUnderHeight / 2, mShowPaint);
            Rect rt2 = new Rect();
            mTemPaint.getTextBounds(downTime, 0, downTime.length(), rt2);
            int width2 = rt2.width();
            int height2 = rt2.height();
            mCanvas.drawText(downTime, x - width2 / 2, mOutSideDistance + mYDistance * mYSize + mOutSideDistance / 2, mTemPaint);
        }

    }


    private void drawCurrentTimeAbout() {
        //获取x轴时间跨度，单位分钟，这个等根据数据源格式调整一下，主要是确认当前时间位置
        //获取当前时间X轴坐标
        float mCurrentXPosi = getCurrentTimeX();
        mCanvas.drawLine(mCurrentXPosi, mOutSideDistance, mCurrentXPosi, mYDistance * (mYSize) + mOutSideDistance, mCurrentPaint);

        //获取当前时间Y轴坐标
        float mCurrentYPosi = getCurrentTimeY();

        mCanvas.drawCircle(mCurrentXPosi, mCurrentYPosi, 10, mInnerCriclePaint);
        mCanvas.drawCircle(mCurrentXPosi, mCurrentYPosi, 10, mOutSideCriclePaint);

        //定义不规则图形,从三角形顶点开始定义

        //顶点y坐标
        float mVertex = mCurrentYPosi - 12;


        mShowPath.moveTo(mCurrentXPosi, mVertex);
        mShowPath.lineTo(mCurrentXPosi + mSide / 2, mVertex - mHeight);
        mShowPath.lineTo(mCurrentXPosi + mSide / 2 - mSide, mVertex - mHeight);
        mShowPath.close();
        mCanvas.drawPath(mShowPath, mShowPaint);
        mCanvas.drawCircle(mCurrentXPosi + mSide / 2 + mSide, mVertex - mHeight - mCricleRectHeight / 2, mCricleRectHeight / 2, mShowPaint);
        mCanvas.drawCircle(mCurrentXPosi - mSide / 2 - mSide, mVertex - mHeight - mCricleRectHeight / 2, mCricleRectHeight / 2, mShowPaint);
        mCanvas.drawRect(mCurrentXPosi - mSide / 2 - mSide, mVertex - mHeight - mCricleRectHeight, mCurrentXPosi + mSide / 2 + mSide, mVertex - mHeight, mShowPaint);

        //画温度文字
        Rect rt = new Rect();
        String text = mCurrentTem + "°C";
        mTemPaint.getTextBounds(text, 0, text.length(), rt);
        int width = rt.width();
        int height = rt.height();
        mCanvas.drawText(text, mCurrentXPosi - width / 2, mCurrentYPosi - mHeight - height, mTemPaint);

        //画地下X轴的圆角矩形

//        mCanvas.drawText(mXDate[i],mOutSideDistance/2,mOutSideDistance+mYDistance*mYSize+mOutSideDistance/2,mTextPaint);
        //最下方圆角矩形相关
        float mUnderHeight = mOutSideDistance / 2;
        float mUnderWidth = mXDistance / 2;
        mCanvas.drawRect(mCurrentXPosi - mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + 10,
                mCurrentXPosi + mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + mUnderHeight + 10, mShowPaint);
        mCanvas.drawCircle(mCurrentXPosi - mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + 10 + mUnderHeight / 2, mUnderHeight / 2, mShowPaint);
        mCanvas.drawCircle(mCurrentXPosi + mUnderWidth / 2, mOutSideDistance + mYDistance * mYSize + 10 + mUnderHeight / 2, mUnderHeight / 2, mShowPaint);
        Rect rt2 = new Rect();
        String text2 = mCurrentTime;
        mTemPaint.getTextBounds(text2, 0, text2.length(), rt2);
        int width2 = rt2.width();
        int height2 = rt2.height();
        mCanvas.drawText(text2, mCurrentXPosi - width2 / 2, mOutSideDistance + mYDistance * mYSize + mOutSideDistance / 2, mTemPaint);
    }

    private float getCurrentTimeY() {
        //Y轴坐标的总长度
        float mYTotalDis = mYSize * mYDistance;

        //为了算Y轴 对应数据源的坐标值
        int count = mYData.length;
        //求出当前坐标跨度值
        float dis = mYData[count - 1] - mYData[0] + 1;
        float mdisScale = mCurrentTem - mYData[0] + 1;
        float bili = dis / mdisScale;
        float mCurrentTimeY = (mYTotalDis - mYTotalDis / bili) + mOutSideDistance;
        return mCurrentTimeY;
    }

    private float getDownPointTimeY(float currentTemp) {
        //Y轴坐标的总长度
        float mYTotalDis = mYSize * mYDistance;

        //为了算Y轴 对应数据源的坐标值
        int count = mYData.length;
        //求出当前坐标跨度值
        float dis = mYData[count - 1] - mYData[0] + 1;
        float mdisScale = currentTemp - mYData[0] + 1;
        float bili = dis / mdisScale;
        float mCurrentTimeY = (mYTotalDis - mYTotalDis / bili) + mOutSideDistance;
        return mCurrentTimeY;
    }

    private float getCurrentTimeX() {
        String x1 = mXDate[0];
        String x2 = mXDate[mXSize - 1];
        StringTokenizer st = new StringTokenizer(x1, ":");
        int x1hour = Integer.valueOf(st.nextToken());
        int x1min = Integer.valueOf(x1.substring(x1.length() - 2, x1.length()));
        int x1time = x1hour * 60 + x1min;

        StringTokenizer st2 = new StringTokenizer(x2, ":");
        int x2hour = Integer.valueOf(st2.nextToken());
        int x2min = Integer.valueOf(x2.substring(x2.length() - 2, x2.length()));
        int x2time = x2hour * 60 + x2min;


        StringTokenizer st3 = new StringTokenizer(mCurrentTime, ":");
        int x3hour = Integer.valueOf(st3.nextToken());
        int x3min = Integer.valueOf(mCurrentTime.substring(mCurrentTime.length() - 2, mCurrentTime.length()));
        int x3time = x3hour * 60 + x3min;

        //x轴坐标时间差
        float xTimeDvalue = (x2time - x1time);
        //当前时间与坐标轴之差
        float mCurrentDvalue = (x3time - x1time);

        //时间比例
        float mtimebili = xTimeDvalue / mCurrentDvalue;
        //当前时间X轴坐标
//        float mCurrentX =( (mXSize-1)/mtimebili)*mXDistance+mOutSideDistance;
        float mCurrentX = (((mXSize - 1)) * mXDistance) / mtimebili + mOutSideDistance;

        return mCurrentX;

    }

    private void drawShadow() {
        //为了做渐变求出集合最大点，和最小点
        float max = mData[0];
        float min = mData[0];

        for (int i = 0; i < mData.length; i++) {
            if (mData[i] > max) {
                max = mData[i];
            }
            if (mData[i] < min) {
                min = mData[i];
            }
        }
        //老方法 求出max和min的坐标
        //Y轴坐标的总长度
        int mYTotalDis = mYSize * mYDistance;
        //为了算Y轴 对应数据源的坐标值
        int count = mYData.length;
        //求出当前坐标跨度值
        float dis = mYData[count - 1] - mYData[0];
        float maxScale = max - mYData[0];
        float maxBili = maxScale / dis;

        float minScale = min - mYData[0];
        float minBili = minScale / dis;
        float maxLoc = mYTotalDis - maxBili * mYTotalDis;
        float minLoc = mYTotalDis - minBili * mYTotalDis;


        mNewPath.lineTo(mOutSideDistance + mXDistance * mXSize, mOutSideDistance + mYDistance * mYSize);
        mNewPath.lineTo(mOutSideDistance, mOutSideDistance + mYDistance * mYSize);
        mNewPath.close();
        Shader shader = new LinearGradient(0, maxLoc, 0, minLoc, Color.parseColor("#A2F171"), Color.parseColor("#1ECC9B"), Shader.TileMode.CLAMP);
//        Shader shader = new LinearGradient(mPointData.get(i).x,mPointData.get(i).y,mPointData.get(i).x,mOutSideDistance+mYSize*mYDistance,Color.parseColor("#8DE97A"),Color.parseColor("#1ECC9B"), Shader.TileMode.CLAMP);
        mShadowPaint.setShader(shader);
        mCanvas.drawPath(mNewPath, mShadowPaint);
    }

    private void drawArc() {
        //Y轴坐标的总长度
        float mYTotalDis = mYSize * mYDistance;

        //为了算Y轴 对应数据源的坐标值
        int count = mYData.length;
        //求出当前坐标跨度值
        float dis = mYData[count - 1] - mYData[0] + 1;

        for (int i = 0; i < mData.length; i++) {
            //求出数据源坐标，和当前坐标的比值
            float mdisScale = mData[i] - mYData[0] + 1;
            float bili = dis / mdisScale;
            //根据X坐标，和数据坐标，生成每个点的坐标填进去
            mPointData.add(new Point(((mOutSideDistance) + mXDistance * i), ((int) (mYTotalDis - mYTotalDis / bili) + mOutSideDistance)));
        }
        //三阶贝塞尔曲线，得要确认两个辅助点
        float prePreviousPointX = Float.NaN;
        float prePreviousPointY = Float.NaN;
        float previousPointX = Float.NaN;
        float previousPointY = Float.NaN;
        float currentPointX = Float.NaN;
        float currentPointY = Float.NaN;
        float nextPointX;
        float nextPointY;
        final int lineSize = mPointData.size();
        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {
            if (Float.isNaN(currentPointX)) {
                Point point = mPointData.get(valueIndex);
                currentPointX = point.x;
                currentPointY = point.y;
            }
            if (Float.isNaN(previousPointX)) {
                //是否是第一个点
                if (valueIndex > 0) {
                    Point point = mPointData.get(valueIndex - 1);
                    previousPointX = point.x;
                    previousPointY = point.y;
                } else {
                    //是的话就用当前点表示上一个点
                    previousPointX = currentPointX;
                    previousPointY = currentPointY;
                }
            }

            if (Float.isNaN(prePreviousPointX)) {
                //是否是前两个点
                if (valueIndex > 1) {
                    Point point = mPointData.get(valueIndex - 2);
                    prePreviousPointX = point.x;
                    prePreviousPointY = point.y;
                } else {
                    //是的话就用当前点表示上上个点
                    prePreviousPointX = previousPointX;
                    prePreviousPointY = previousPointY;
                }
            }

            // 判断是不是最后一个点了
            if (valueIndex < lineSize - 1) {
                Point point = mPointData.get(valueIndex + 1);
                nextPointX = point.x;
                nextPointY = point.y;
            } else {
                //是的话就用当前点表示下一个点
                nextPointX = currentPointX;
                nextPointY = currentPointY;
            }

            if (valueIndex == 0) {
                // 将Path移动到开始点
                mArcPath.moveTo(currentPointX, currentPointY);
            } else {
                // 求出控制点坐标
                final float firstDiffX = (currentPointX - prePreviousPointX);
                final float firstDiffY = (currentPointY - prePreviousPointY);
                final float secondDiffX = (nextPointX - previousPointX);
                final float secondDiffY = (nextPointY - previousPointY);
                final float firstControlPointX = previousPointX + (lineSmoothness * firstDiffX);
                final float firstControlPointY = previousPointY + (lineSmoothness * firstDiffY);
                final float secondControlPointX = currentPointX - (lineSmoothness * secondDiffX);
                final float secondControlPointY = currentPointY - (lineSmoothness * secondDiffY);
                //画出曲线
                mArcPath.cubicTo(firstControlPointX, firstControlPointY, secondControlPointX, secondControlPointY,
                        currentPointX, currentPointY);
            }
            prePreviousPointX = previousPointX;
            prePreviousPointY = previousPointY;
            previousPointX = currentPointX;
            previousPointY = currentPointY;
            currentPointX = nextPointX;
            currentPointY = nextPointY;
        }
        mPathMeasure = new PathMeasure(mArcPath, false);
        mNewPath = new Path();
        mNewPath.rLineTo(0, 0);
        float distance = mPathMeasure.getLength() * drawScale;
        if (mPathMeasure.getSegment(0, distance, mNewPath, true)) {
            mCanvas.drawPath(mNewPath, mArcPaint);
            pos = new float[2];
            mPathMeasure.getPosTan(distance, pos, null);

        }


    }

    private void drawCoordinate() {
        //画X坐标
        mTextPaint.getTextBounds(mXDate[0], 0, mXDate[0].length(), mXRect);
        mXTextWidth = mXRect.width();
        mXTextHeight = mXRect.height();
        for (int i = 0; i < mXSize; i++) {
            //第一条数据和Y轴数据对齐，为了美观
            if (i == 0) {
                mCanvas.drawText(mXDate[i], mOutSideDistance / 2, mOutSideDistance + mYDistance * mYSize + mOutSideDistance / 2, mTextPaint);
            } else {
                mCanvas.drawText(mXDate[i], (mOutSideDistance - mXTextWidth / 2) + mXDistance * i, mOutSideDistance + mYDistance * mYSize + mOutSideDistance / 2, mTextPaint);
            }

        }

        //画Y轴坐标

        //将数据倒装排序
        mYUpSideData = new ArrayList<>();
        int count = mYData.length;
        for (int i = 0; i < mYData.length; i++) {
            mYUpSideData.add(String.valueOf((int) (mYData[count - 1 - i])));
        }
        mTextPaint.getTextBounds(mYUpSideData.get(0), 0, mYUpSideData.get(0).length(), mYRect);
        mYTextWidth = mYRect.width();
        mYTextHeight = mYRect.height();
        for (int i = 0; i < mYUpSideData.size(); i++) {
            //第一个数据不写出来
            if (i != 0) {
                mCanvas.drawText(mYUpSideData.get(i), mOutSideDistance / 2, mOutSideDistance + mYTextHeight / 2 + mYDistance * i, mTextPaint);
            }

        }


    }

    private void drawLine() {
        //画表格横线
        for (int i = 0; i < mYSize / 2; i++) {
            if (i != 0) {
                mCanvas.drawLine(mOutSideDistance, mOutSideDistance + (mYDistance * 2) * (i), mOutSideDistance + mXDistance * mXSize, mOutSideDistance + (mYDistance * 2) * (i), mLinePaint);
            }

        }
        //画表格纵线
        for (int i = 0; i < mXSize; i++) {
            if (i != 0) {
                mCanvas.drawLine(mOutSideDistance + mXDistance * i, mOutSideDistance, mOutSideDistance + mXDistance * i, mYDistance * (mYSize) + mOutSideDistance, mLinePaint);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取下屏幕宽度
        widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        //根据X和Y坐标数量，自定义View大小
        setMeasuredDimension(mXSize * mXDistance + 2 * mOutSideDistance, mYSize * mYDistance + 2 * mOutSideDistance);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                drawDownLine(getScrollX()+x);
                downX = getScrollX() + x;
                downCurrentY = (int) getDownPointTimeY(38f);
//                downCurrentY = (int) 38f;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = mLastX - x;
                int dy = mLastY - y;
                int disWidth = mXSize * mXDistance + 2 * mOutSideDistance - widthPixels;
                int scrollX = getScrollX();

                if (Math.abs(dx) > Math.abs(dy)) {//左边界检测
                    if (scrollX + dx <= 0) {
                        scrollTo(0, 0);
                        isYaxisShow.getStatus(false);
                        return true;
                    } else if (scrollX + dx > disWidth) {
                        scrollTo(disWidth, 0);
                        isYaxisShow.getStatus(true);
                        return true;
                    }
//                    mCanvas.translate(dx,0);
//                    invalidate();
                    if (scrollX + dx > mOutSideDistance) {
                        isYaxisShow.getStatus(true);
                    }
                    scrollBy(dx, 0);//item跟随手指滑动
                }

                break;
            case MotionEvent.ACTION_UP:
                downX = 0;
                downCurrentY = 0;
                invalidate();
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    private void drawDownLine(int x) {
        //根据点击的x，获取到当前x轴时间
        int i = (x - mOutSideDistance) / mXDistance;
        int i1 = (x - mOutSideDistance) - i * mXDistance;
        int i2 = 5 * i1 / mXDistance;
        String s = mXDate[i];
        Log.e("TAG", "drawDownLine: " + i);
        String substring = s.substring(s.length() - 2, s.length());
        int i3 = Integer.parseInt(substring) + i2;
        String substring1 = s.substring(0, s.length() - 3);
        String min = String.valueOf(i3);
        if (min.length() < 2) {
            min = "0" + min;
        }
        String downTime = substring1 + ":" + min;
        Log.e("TAG", "drawDownLine: " + downTime);

    }

    /**
     * dp转px
     *
     * @param dpValue
     * @return
     */
    private int dp2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置温度曲线的数据源
     */
    public void setTempData(TempDetailsDataBean tempDetailsDataBean) {
        float[] tempDates = tempDetailsDataBean.getTempDates();
        for (int i = 0; i < tempDates.length; i++) {
            Log.i("tag", "temp: " + tempDates[i]);
        }
        mXDate = tempDetailsDataBean.getTimeDates();
        mData = tempDetailsDataBean.getTempDates();
        mXSize = mXDate.length;
        mYSize= mData.length;

        requestLayout();
        invalidate();
    }
}
