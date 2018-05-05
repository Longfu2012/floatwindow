package window.afloat.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

import window.afloat.floatwindow.arc.ArcMenu;


public class FloatingLayout extends LinearLayout implements View.OnClickListener{

    private LinearLayout mLinearLayoutFloating;
    private LinearLayout mLinearLayoutRunStop;
    private LinearLayout mLinearLayoutSetting;
    private LinearLayout mLinearLayoutBackApp;
    private ImageView mFloatingImg;
    //private ArcMenu mFloatingImg;
    private ImageView mRunStopImg;
    private ImageView mSettingImg;
    private ImageView mBackAppImg;

    private CountDownTimer mCountTimer,mMoveTimer;
    private float mDownX,mDownY;//控件区
    private float mCurrentX,mCurrentY;//屏幕区
    private int lock;

    private int mMoveIndex;
    private int[] moveImgs = {R.drawable.go_1,R.drawable.go_2,R.drawable.go_3,R.drawable.go_4,R.drawable.go_5};

    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };

    private WindowManager mWindownManager;
    private WindowManager.LayoutParams mWindowParams;
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
                        FloatingLayout.this.updatePosition(((int)(mCurrentX - mDownX)), ((int)(mCurrentY - mDownY)));
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
            //addItems();
            Toast.makeText(getContext(),"功能开发中...",Toast.LENGTH_SHORT).show();
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

    public FloatingLayout(Context context) {
        this(context,null);
    }

    public FloatingLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FloatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
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
        initView();
        initWindowParam();
    }

    private void initView(){

        LayoutInflater.from(getContext()).inflate(R.layout.ll_floating,this);
        mLinearLayoutFloating = (LinearLayout) findViewById(R.id.linearlayout_floating);
        mFloatingImg = (ImageView) findViewById(R.id.imageview_floating);
        mFloatingImg.setOnClickListener(null);
        mFloatingImg.setOnTouchListener(mOnTouchListener);

        mLinearLayoutBackApp = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.ll_floating_item,mLinearLayoutFloating,false);
        mLinearLayoutRunStop = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.ll_floating_item,mLinearLayoutRunStop,false);
        mLinearLayoutSetting = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.ll_floating_item,mLinearLayoutSetting,false);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int v1 = ((int) TypedValue.applyDimension(1, 5f, this.getResources().getDisplayMetrics()));
        itemParams.setMargins(v1, 0, v1, 0);

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
        mWindowParams.width = LayoutParams.WRAP_CONTENT;//窗口的宽和高
        mWindowParams.height = LayoutParams.WRAP_CONTENT;
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

    @Override
    public void onClick(View v) {

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
        if(lock == 0){
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

    private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(getContext());
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "开发中:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addItems() {
        if(this.getChildCount() == 1) {
            this.removItems();
            this.mLinearLayoutFloating.addView(this.mLinearLayoutRunStop);
            this.mLinearLayoutFloating.addView(this.mLinearLayoutSetting);
            this.mLinearLayoutFloating.addView(this.mLinearLayoutBackApp);
        }
    }

    private void removItems() {
        this.mLinearLayoutFloating.removeView(this.mLinearLayoutBackApp);
        this.mLinearLayoutFloating.removeView(this.mLinearLayoutSetting);
        this.mLinearLayoutFloating.removeView(this.mLinearLayoutRunStop);
    }

}
