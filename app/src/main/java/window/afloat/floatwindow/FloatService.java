package window.afloat.floatwindow;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import window.afloat.floatwindow.arc.ArcMenu;

public class FloatService extends Service {

    FloatingLayout floatingView;
    Context mContext;
    private static final int NOTIFICATION_ID = 1;
    private AssistServiceConnection mConnection;

    //private ArcMenu floatingView;

    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };

    public FloatService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        if (Build.VERSION.SDK_INT < 18){
            startForeground(NOTIFICATION_ID, getNotification(this));
        }else{
            if (null == mConnection) {
                mConnection = new AssistServiceConnection();
            }
            this.bindService(new Intent(this, FzYsc.class), mConnection,
                    Service.BIND_AUTO_CREATE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initFloatView();
        initNtf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatView();
    }

    public void initFloatView(){
        if(null == floatingView){
            floatingView = new FloatingLayout(mContext);
            ArcMenu menu = new ArcMenu(mContext);
            //floatingView.addView(menu,menu.getLayoutParams());
            initArcMenu(menu,ITEM_DRAWABLES);
        }
        if(null == floatingView.getParent()){
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            windowManager.addView(floatingView,floatingView.getLayoutParams());
        }
    }

    public void removeFloatView(){

    }

    public void initNtf(){
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.setAction("android.intent.action.MAIN");
        mainActivity.addCategory("android.intent.category.LAUNCHER");
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivity,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.floating);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.wait));
        builder.setAutoCancel(false);
        builder.setContentTitle(this.getString(R.string.ntf_dla));
        builder.setContentText("正在运行");
        Notification ntf = builder.build();
        ntf.flags = 2;
        ntf.flags |= 32;
        ntf.flags |= 64;
        this.startForeground(NOTIFICATION_ID, ntf);
    }

    private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(mContext);
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "开发中:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class AssistServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            // sdk >=18
            // 的，会在通知栏显示service正在运行，这里不要让用户感知，所以这里的实现方式是利用2个同进程的service，利用相同的notificationID，
            // 2个service分别startForeground，然后只在1个service里stopForeground，这样即可去掉通知栏的显示
            Service assistService = ((FzYsc.LocalBinder) binder)
                    .getService();
            FloatService.this.startForeground(NOTIFICATION_ID,getNotification(mContext));
            assistService.startForeground(NOTIFICATION_ID,getNotification(mContext));
            assistService.stopForeground(true);

            FloatService.this.unbindService(mConnection);
            mConnection = null;
        }
    }

    public static Notification getNotification(Context paramContext)
    {
        if (Build.VERSION.SDK_INT >= 18){
            //stat_sys_download
            Notification nft = new Notification.Builder(paramContext).setSmallIcon(paramContext.getResources().getIdentifier("stat_sys_download", "drawable","android")).getNotification();
            return nft;
        }
        return new Notification();
    }

}
