package window.afloat.floatwindow;

import android.content.Context;
import android.graphics.Point;
import android.os.Vibrator;
import android.view.Display;
import android.view.WindowManager;


public class DevicesUtil {

    public static Point getResolution(Context context) {
        Display v0 = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point v1 = new Point();
        v0.getSize(v1);
        return v1;
    }

    public static int getStatusBarHeight(Context context) {
        int v1 = 0;
        int v0 = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(v0 > 0) {
            v1 = context.getResources().getDimensionPixelSize(v0);
        }

        return v1;
    }

    public static void vibration(Context context, long milliseconds) {
        ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(milliseconds);
    }

}
