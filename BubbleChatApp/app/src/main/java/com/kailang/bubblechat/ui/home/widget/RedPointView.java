package com.kailang.bubblechat.ui.home.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.kailang.bubblechat.R;

/**
 * created by huangbo at 2019/6/26 15:52
 */

public class RedPointView extends FrameLayout {


    //画笔
    private Paint rPaint, tPaint;
    //半径
    private int rRadius;

    //点 PointF 和 Point 主要是 传参类型不一样，Point 是整形，PointF 是float
    // private PointF rDragPointF,rCenterPointF;

    //  private boolean rTouch;

    //固定圆的圆心
    PointF tCenterPointF = new PointF(300, 400);

    //拖拽圆的圆心
    PointF tDragPointF = new PointF(200, 550);

    //固定圆的半径
    private float tCenterRadius = 30;

    //拖拽圆的半径
    private float tDragRadius = 30;

    //线条
    private Path rPath;

    //圆的最小半径
    private float minRadius = 8;
    //默认拖拽最大的距离
    private float maxDistance = 560;

    //标识 拖拽距离是否大于规定的拖拽范围
    private boolean isOut;

    //标识 如果超出拖拽范围
    private boolean isOverStep;

    //标识 超出范围并且抬起手指
    private boolean isOverandUp;

    //爆炸图片
    private int[] explodeImgaes = new int[]{
            R.mipmap.explode_1,
            R.mipmap.explode_2,
            R.mipmap.explode_3,
            R.mipmap.explode_4,
            R.mipmap.explode_5
    };

    //爆炸ImageView
    private ImageView explodeImage;


    //一个参数的构造函数,调用两个参数的构造函数
    public RedPointView(Context context) {
        this(context, null);
    }

    //两个参数的构造函数,调用三个参数的构造函数
    public RedPointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    //三个参数的构造函数
    public RedPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    //初始化小圆
    private void init() {
        //初始化Paint对象
        rPaint = new Paint();
        //设置颜色 红色
        rPaint.setColor(Color.RED);
        //设置抗锯齿
        rPaint.setAntiAlias(true);
        //设置填充
        rPaint.setStyle(Paint.Style.FILL);

        tPaint = new Paint();
        //设置颜色 红色
        tPaint.setColor(Color.RED);
        //设置抗锯齿
        tPaint.setAntiAlias(true);
        //半径 25
        rRadius = 25;
        //创建PointF对象
        //  rCenterPointF = new PointF();
        //拖拽圆
        //  rDragPointF = new PointF();

        rPath = new Path();

        //添加爆炸图像
        explodeImage = new ImageView(getContext());
        //设置布局参数
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        explodeImage.setLayoutParams(lp);
        explodeImage.setImageResource(R.mipmap.explode_1);
        //一开始不显示
        explodeImage.setVisibility(View.INVISIBLE);
        //增加到viewGroup中
        addView(explodeImage);

    }

    //当视图大小改变后会回调 onSizeChanged方法比onDraw方法前调用
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    //绘制方法
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();

        if (!isOut) {
            //绘制固定中心圆
            tCenterRadius = changeCenterRadius();
            canvas.drawCircle(tCenterPointF.x, tCenterPointF.y, tCenterRadius, rPaint);
            //绘制拖拽圆
            canvas.drawCircle(tDragPointF.x, tDragPointF.y, tDragRadius, rPaint);

            float x = tCenterPointF.x - tDragPointF.x;
            float y = tDragPointF.y - tCenterPointF.y;

            double a = Math.atan(y / x);

            //中心圆的p1 x坐标偏移
            float offsetX1 = (float) (tCenterRadius * Math.sin(a));
            float offsetY1 = (float) (tCenterRadius * Math.cos(a));

            //拖拽圆的p2 x坐标偏移
            float offsetX2 = (float) (tDragRadius * Math.sin(a));
            float offsetY2 = (float) (tDragRadius * Math.cos(a));

            //p1的坐标
            float p1_x = tCenterPointF.x - offsetX1;
            float p1_y = tCenterPointF.y - offsetY1;


            //p2的坐标
            float p2_x = tCenterPointF.x + offsetX1;
            float p2_y = tCenterPointF.y + offsetY1;


            //p3的坐标
            float p3_x = tDragPointF.x - offsetX2;
            float p3_y = tDragPointF.y - offsetY2;

            //p4的坐标
            float p4_x = tDragPointF.x + offsetX2;
            float p4_y = tDragPointF.y + offsetY2;


            //控制点的坐标
            float controll_x = (tCenterPointF.x + tDragPointF.x) / 2;
            float controll_y = (tDragPointF.y + tCenterPointF.y) / 2;


            rPath.reset();
            rPath.moveTo(p1_x, p1_y);
            rPath.quadTo(controll_x, controll_y, p3_x, p3_y);
            rPath.lineTo(p4_x, p4_y);
            rPath.quadTo(controll_x, controll_y, p2_x, p2_y);
            rPath.lineTo(p1_x, p1_y);
            rPath.close();
            canvas.drawPath(rPath, tPaint);
            //canvas.restore();
        }


        if (isOut) {
            //如果一开始超出拖拽范围 后面又移动拖拽圆与中心圆的距离少于30，就恢复中心圆位置
            if (getDistanceTwo(tCenterPointF, tDragPointF) < 30 && isOverandUp) {
                canvas.drawCircle(tCenterPointF.x, tCenterPointF.y, tCenterRadius, rPaint);
                isOut = false;
                isOverandUp = false;
            }


        }


        //一旦超出给定的拖拽距离 就绘制拖拽圆
        if (!isOverStep) {
            //如果超出并且抬起
            if (!isOverandUp && isOut) {
                canvas.drawCircle(tDragPointF.x, tDragPointF.y, tDragRadius, rPaint);
            }

        }


    }

    //重写onTouchEvent方法
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //event.getRawX:表示的是触摸点距离屏幕左边界的距离
                //event.getRawY:表示的是触摸点距离屏幕上边界的距离
                //event.getX()取相对于你触摸的view的左边的偏移(X坐标)
                //event.getY()取相对于你触摸的view的顶边的偏移(Y坐标)
                float originalDragX = event.getX();
                float originalDragy = event.getY();
                updateDragPoint(originalDragX, originalDragy);
                break;
            case MotionEvent.ACTION_MOVE:
                float overDragX = event.getX();
                float overDragy = event.getY();

                //移动的时候不断更新拖拽圆的位置
                updateDragPoint(overDragX, overDragy);

                float tDragDistance = getDistanceTwo(tCenterPointF, tDragPointF);

                //判断如果拖拽距离大于给定距离时
                if (tDragDistance > maxDistance) {
                    isOut = true;
                } else {
                    //这里要注意 不能赋值isOut为false 因为一旦超出给定的拖拽距离就没办法恢复了
                    isOverStep = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                getDistanceTwo(tCenterPointF, tDragPointF);
                //这里要判断
                if (!isOut) {
                    //没有超出
                    kickBack();
                }
                if (isOut) {
                    //抬起标识
                    isOverandUp = true;
                    //让爆炸图片在原点中央
                    explodeImage.setX(event.getX() - tDragRadius);
                    explodeImage.setY(event.getY() - tDragRadius);
                    //如果中心圆和拖拽圆大于拖拽距离 就播放爆炸
                    if (getDistanceTwo(tCenterPointF, tDragPointF) > maxDistance) {
                        showExplodeImage();
                    }
                    //这里是如果拖拽圆和中心圆距离已经超出拖拽距离 然后又把拖拽圆移动与中心圆大于30 还是会爆炸
                    if (getDistanceTwo(tCenterPointF, tDragPointF) >= 30) {
                        showExplodeImage();
                    }

                }
                postInvalidate();
                break;
        }
        return true;

    }


    //回弹的动画


    /**
     * 获取状态栏高度
     *
     * @param v
     * @return
     */
    public static int getStatusBarHeight(View v) {
        if (v == null) {
            return 0;
        }
        Rect frame = new Rect();
        v.getWindowVisibleDisplayFrame(frame);
        return frame.top;

    }

    /**
     * 更新拖拽圆心坐标
     *
     * @param x
     * @param y
     */
    private void updateDragPoint(float x, float y) {
        tDragPointF.set(x, y);
        postInvalidate();

    }


    /**
     * 计算拖动过程中中心圆的半径
     *
     * @return
     */
    private float changeCenterRadius() {
        float mDistance_x = tDragPointF.x - tCenterPointF.x;
        float mDistance_y = tDragPointF.y - tCenterPointF.y;
        //两个圆之间的距离
        float mDistance = (float) Math.sqrt(Math.pow(mDistance_x, 2) + Math.pow(mDistance_y, 2));
        //计算中心圆的半径 这里用拖拽圆默认的半径去减距离变化的长度（这里可以自己定义变化的半径）
        float r = tDragRadius - minRadius * (mDistance / maxDistance);
        //计算出半径如果小于最小的半径 就赋值最小半径
        if (r < minRadius) {
            r = minRadius;
        }
        return r;


    }


    /**
     * 计算两个圆之间的距离
     *
     * @param tCenterPointF 中心固定圆
     * @param tDragPointF   拖拽圆
     * @return
     */
    private float getDistanceTwo(PointF tCenterPointF, PointF tDragPointF) {

        return (float) Math.sqrt(Math.pow(tCenterPointF.x - tDragPointF.x, 2) + Math.pow(tCenterPointF.y - tDragPointF.y, 2));
    }

    /**
     * 拖拽圆回弹动画
     */
    private void kickBack() {
        final PointF initPoint = new PointF(tDragPointF.x,
                tDragPointF.y);
        final PointF finishPoint = new PointF(tCenterPointF.x,
                tCenterPointF.y);
        //值从0平滑过渡1
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取动画执行进度
                float rFraction = animation.getAnimatedFraction();
                //更新拖拽圆的圆心
                PointF updateDragPoint = getPoint(
                        initPoint, finishPoint, rFraction);
                updateDragPoint(updateDragPoint.x, updateDragPoint.y);
            }
        });
        //设置动画插值器
        animator.setInterpolator(new OvershootInterpolator(3.0f));
        //动画时间
        animator.setDuration(500);
        animator.start();
    }


    /**
     * 超过拖拽范围外显示爆炸效果
     */
    private void showExplodeImage() {
        //属性动画
        ValueAnimator animator = ValueAnimator.ofInt(0, explodeImgaes.length - 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //不断更新图像变化
                explodeImage.setBackgroundResource(explodeImgaes[(int) animation.getAnimatedValue()]);
            }
        });
        //为动画添加监听
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //结束了 把图像设置不可见状态
                explodeImage.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                //开始时 设置为可见
                explodeImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        });
        //时间
        animator.setDuration(600);
        //播放一次
        animator.setRepeatMode(ValueAnimator.RESTART);
        //差值器
        animator.setInterpolator(new OvershootInterpolator());
        animator.start();
    }


    /**
     * 根据百分比获取两点之间的某个点坐标
     *
     * @param initPoint   初识圆
     * @param finishPoint 最终圆
     * @param percent     百分比
     * @return
     */
    public PointF getPoint(PointF initPoint, PointF finishPoint, float percent) {
        return new PointF(getValue(initPoint.x, finishPoint.x, percent), getValue(initPoint.y, finishPoint.y, percent));
    }

    /**
     * 获取分度值
     *
     * @param start
     * @param finish
     * @param fraction
     * @return
     */
    public float getValue(Number start, Number finish, float fraction) {
        return start.floatValue() + (finish.floatValue() - start.floatValue()) * fraction;
    }

}
