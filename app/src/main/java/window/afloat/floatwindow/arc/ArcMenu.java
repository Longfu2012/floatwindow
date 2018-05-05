/*
 * Copyright (C) 2012 Capricorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package window.afloat.floatwindow.arc;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import window.afloat.floatwindow.DevicesUtil;
import window.afloat.floatwindow.FloatingLayout;
import window.afloat.floatwindow.R;

/**
 * A custom view that looks like the menu in <a href="https://path.com">Path
 * 2.0</a> (for iOS).
 * 
 * @author Capricorn
 * 
 */
public class ArcMenu extends RelativeLayout {

    private ArcLayout mArcLayout;
    private ImageView mFloatingImg;
    private RelativeLayout mLinearLayoutFloating;

    private WindowManager mWindownManager;
    private WindowManager.LayoutParams mWindowParams;

    private CountDownTimer mCountTimer,mMoveTimer;
    private float mDownX,mDownY;//控件区
    private float mCurrentX,mCurrentY;//屏幕区

    private int mMoveIndex;
    private int[] moveImgs = {R.drawable.go_1,R.drawable.go_2,R.drawable.go_3,R.drawable.go_4,R.drawable.go_5};

    public ArcMenu(Context context) {
        super(context);
        init(context);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        applyAttrs(attrs);
    }

    private void init(Context context) {
        //LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //li.inflate(R.layout.arc_menu, this);
        View view = LayoutInflater.from(context).inflate(R.layout.arc_menu,this);

        mArcLayout = (ArcLayout) view.findViewById(R.id.item_layout);
        mLinearLayoutFloating = (RelativeLayout) view.findViewById(R.id.linearlayout_floating);
        mFloatingImg = (ImageView) findViewById(R.id.control_layout);
        mFloatingImg.setClickable(true);

/*        mFloatingImg.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //mHintView.startAnimation(createHintSwitchAnimation(mArcLayout.isExpanded()));
                    mArcLayout.switchState(true);
                }
                return false;
            }
        });*/

        mFloatingImg.setOnClickListener(null);
        mFloatingImg.setOnTouchListener(mOnTouchListener);

        mCountTimer = new CountDownTimer(7000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished > 2000 && millisUntilFinished < 3000){
                    mFloatingImg.setAlpha(0.5f);
                }
            }

            @Override
            public void onFinish() {
                mFloatingImg.setImageResource(R.mipmap.wait);
            }
        };
        mMoveTimer = new CountDownTimer(1000000000,500){
            @Override
            public void onTick(long millisUntilFinished) {
                move();
            }

            @Override
            public void onFinish() {

            }
        };
        initWindowParam();

    }

    private void applyAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcLayout, 0, 0);

            float fromDegrees = a.getFloat(R.styleable.ArcLayout_fromDegrees, ArcLayout.DEFAULT_FROM_DEGREES);
            float toDegrees = a.getFloat(R.styleable.ArcLayout_toDegrees, ArcLayout.DEFAULT_TO_DEGREES);
            mArcLayout.setArc(fromDegrees, toDegrees);

            int defaultChildSize = mArcLayout.getChildSize();
            int newChildSize = a.getDimensionPixelSize(R.styleable.ArcLayout_childSize, defaultChildSize);
            mArcLayout.setChildSize(newChildSize);

            a.recycle();
        }
    }

    public void addItem(View item, OnClickListener listener) {
        mArcLayout.addView(item);
        item.setOnClickListener(getItemClickListener(listener));
    }

/*    public void setImageResource(int resId){
        if(mFloatingImg != null){
            mFloatingImg.setImageResource(resId);
        }
    }*/

    private OnClickListener getItemClickListener(final OnClickListener listener) {
        return new OnClickListener() {

            @Override
            public void onClick(final View viewClicked) {
                Animation animation = bindItemAnimation(viewClicked, true, 400);
                animation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                itemDidDisappear();
                            }
                        }, 0);
                    }
                });

                final int itemCount = mArcLayout.getChildCount();
                for (int i = 0; i < itemCount; i++) {
                    View item = mArcLayout.getChildAt(i);
                    if (viewClicked != item) {
                        bindItemAnimation(item, false, 300);
                    }
                }

                mArcLayout.invalidate();
                //mHintView.startAnimation(createHintSwitchAnimation(true));

                if (listener != null) {
                    listener.onClick(viewClicked);
                }
            }
        };
    }

    private Animation bindItemAnimation(final View child, final boolean isClicked, final long duration) {
        Animation animation = createItemDisapperAnimation(duration, isClicked);
        child.setAnimation(animation);

        return animation;
    }

    private void itemDidDisappear() {
        final int itemCount = mArcLayout.getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View item = mArcLayout.getChildAt(i);
            item.clearAnimation();
        }

        mArcLayout.switchState(false);
    }

    private static Animation createItemDisapperAnimation(final long duration, final boolean isClicked) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, isClicked ? 2.0f : 0.0f, 1.0f, isClicked ? 2.0f : 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));

        animationSet.setDuration(duration);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setFillAfter(true);

        return animationSet;
    }

    private static Animation createHintSwitchAnimation(final boolean expanded) {
        Animation animation = new RotateAnimation(expanded ? 45 : 0, expanded ? 0 : 45, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setDuration(100);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setFillAfter(true);

        return animation;
    }


    private final OnTouchListener mOnTouchListener = new OnTouchListener() {

        FloatingGestureListener gestureListener = new FloatingGestureListener();
        GestureDetector gestureDetector = new GestureDetector(getContext(),gestureListener);

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean isMove = false;
            if(!gestureDetector.onTouchEvent(event)){
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mCountTimer.cancel();
                        mFloatingImg.setImageResource(R.mipmap.floating);
                        mFloatingImg.setAlpha(1f);
                        mDownX = event.getX();
                        mDownY = event.getY();
                        mCurrentX = event.getRawX();
                        mCurrentY = event.getRawY() - DevicesUtil.getStatusBarHeight(getContext());
                        break;
                    case MotionEvent.ACTION_UP:
                        mCountTimer.start();
                        mMoveTimer.cancel();
                        mFloatingImg.setImageResource(R.mipmap.floating);
                        mCurrentX = event.getRawX();
                        mCurrentY = event.getRawY() - DevicesUtil.getStatusBarHeight(getContext());
                        moveToEdge();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentX = event.getRawX();
                        mCurrentY = event.getRawY() - DevicesUtil.getStatusBarHeight(getContext());
                        updatePosition(((int)(mCurrentX - mDownX)), ((int)(mCurrentY - mDownY)));
                        return isMove;
                }
            }
            return isMove;
        }
    };

    class FloatingGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            //Toast.makeText(getContext(),"按下",Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //Toast.makeText(getContext(),"轻触前",Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Toast.makeText(getContext(),"轻触后",Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(),"功能开发中...",Toast.LENGTH_SHORT).show();
            mArcLayout.switchState(true);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Toast.makeText(getContext(),"拖动",Toast.LENGTH_SHORT).show();
            mMoveTimer.start();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //Toast.makeText(getContext(),"长按",Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Toast.makeText(getContext(),"快滑",Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void initWindowParam(){
        mWindownManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();

        mWindowParams.type = (Build.VERSION.SDK_INT >= 19 ? WindowManager.LayoutParams.TYPE_TOAST : WindowManager.LayoutParams.TYPE_PHONE);
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
        mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowParams.x = DevicesUtil.getResolution(this.getContext()).x - mLinearLayoutFloating.getLayoutParams().width;
        mWindowParams.y = DevicesUtil.getResolution(this.getContext()).y / 4;
        mCurrentY = ((float)(DevicesUtil.getResolution(this.getContext()).y / 4));
        mWindowParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;//窗口的宽和高
        mWindowParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
    }

    public WindowManager.LayoutParams getLayoutParams(){
        return mWindowParams;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFloatingImg.setImageResource(R.mipmap.floating);
        mFloatingImg.setAlpha(1f);
        mCountTimer.start();
    }

    private boolean isLeftEdge() {
        boolean edge = false;
        int[] position = new int[2];
        mFloatingImg.getLocationOnScreen(position);
        if(position[0] < (DevicesUtil.getResolution(this.getContext()).x - mFloatingImg.getWidth())
                / 2) {
            edge = true;
        }

        return edge;
    }

    private void moveToEdge(){
        mFloatingImg.getLocationOnScreen(new int[2]);
        if(this.isLeftEdge()) {
            updatePosition(0, ((int)(mCurrentY - this.mDownY)));
        }
        else {
            this.updatePosition(DevicesUtil.getResolution(this.getContext()).x, ((int)(mCurrentY
                    - this.mDownY)));
        }
    }

    private void move(){
        if(mMoveIndex >= moveImgs.length - 1){
            mMoveIndex = 0;
        }else{
            mMoveIndex++;
        }
        mFloatingImg.setImageResource(moveImgs[mMoveIndex]);
    }

    private void updatePosition(int x,int y){
        mWindowParams.x = x;
        mWindowParams.y = y;

        if(Build.VERSION.SDK_INT > 19){
            if(!isAttachedToWindow()){
                return;
            }
            mWindownManager.updateViewLayout(this,mWindowParams);
        }
        if(null != mLinearLayoutFloating.getParent()){
            mWindownManager.updateViewLayout(this,mWindowParams);
        }

    }

}
