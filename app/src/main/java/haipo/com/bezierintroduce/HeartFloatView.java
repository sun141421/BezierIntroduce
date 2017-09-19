package haipo.com.bezierintroduce;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * 心型漂浮view
 * Created by lsc on 2017/9/20.
 */

public class HeartFloatView extends RelativeLayout {

    protected Random random;
    protected PointF pointFStart, pointFEnd, pointFFirst, pointFSecond;
    protected Bitmap bitmap;

    private static final int[] DEFAULT_COLORS ={Color.WHITE,Color.CYAN,Color.YELLOW,Color.BLACK ,Color.LTGRAY,Color.GREEN,Color.RED};
    private int[]colors =DEFAULT_COLORS;

    public HeartFloatView(Context context) {
        super(context);
    }

    public HeartFloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeartFloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeartFloatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void startAnimation(int numOfHeart,@Nullable int[] heartColors){
          if(numOfHeart<=0){
              numOfHeart=10;
          }
          if(heartColors!=null&&heartColors.length>0){
              colors=heartColors;
          }
        for (int i = 0; i < numOfHeart; i++) {
            startAnimationInteral();
        }
    }

    private void startAnimationInteral(){
        pointFStart = new PointF();
        pointFFirst = new PointF();
        pointFSecond = new PointF();
        pointFEnd = new PointF();

        pointFStart.x = getMeasuredWidth() / 2-bitmap.getWidth()/2;
        pointFStart.y = getMeasuredHeight() - bitmap.getHeight();

        pointFEnd.y = 0;
        pointFEnd.x = random.nextFloat()*getMeasuredWidth();

        pointFFirst.x = random.nextFloat()*getMeasuredWidth();
        pointFSecond.x = getMeasuredWidth() - pointFFirst.x;
        pointFSecond.y = random.nextFloat()*getMeasuredHeight() / 2+getMeasuredHeight()/2;
        pointFFirst.y = random.nextFloat()*getMeasuredHeight()  / 2;
        addHeart();
    }


    private void addHeart() {
        ImageView imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(ALIGN_PARENT_BOTTOM);
        imageView.setImageBitmap(drawHeart(colors[random.nextInt(colors.length)]));
        addView(imageView, params);
        moveHeart(imageView);
    }

    private Bitmap drawHeart(int color) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawColor(color, PorterDuff.Mode.SRC_ATOP);
        canvas.setBitmap(null);
        return newBitmap;
    }

    private void moveHeart(final ImageView view){
        PointF pointFFirst = this.pointFFirst;
        PointF pointFSecond = this.pointFSecond;
        PointF pointFStart = this.pointFStart;
        PointF pointFEnd = this.pointFEnd;


        ValueAnimator animator = ValueAnimator.ofObject(new TypeE(pointFFirst, pointFSecond), pointFStart, pointFEnd);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF value = (PointF) animation.getAnimatedValue();
                view.setX(value.x);
                view.setY(value.y);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                HeartFloatView.this.removeView(view);
            }
        });

        ObjectAnimator af = ObjectAnimator.ofFloat(view, "alpha", 1f, 0);
        af.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                HeartFloatView.this.removeView(view);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(3000);
        set.play(animator).with(af);
        set.start();

    }


    /**
     * 绘制一个增值器
     */
    private class TypeE implements TypeEvaluator<PointF> {

        private PointF pointFFirst,pointFSecond;

        TypeE(PointF start, PointF end){
            this.pointFFirst =start;
            this.pointFSecond = end;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            PointF result = new PointF();
            float left = 1 - fraction;
            result.x = (float) (startValue.x*Math.pow(left,3)+3*pointFFirst.x*Math.pow(left,2)*fraction+3*pointFSecond.x*Math.pow(fraction, 2)*left+endValue.x*Math.pow(fraction,3));
            result.y= (float) (startValue.y*Math.pow(left,3)+3*pointFFirst.y*Math.pow(left,2)*fraction+3*pointFSecond.y*Math.pow(fraction, 2)*left+endValue.y*Math.pow(fraction,3));
            return result;
        }
    }

}
