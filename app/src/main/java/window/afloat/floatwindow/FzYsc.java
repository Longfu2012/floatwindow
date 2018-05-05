package window.afloat.floatwindow;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class FzYsc extends Service{
    
    public class LocalBinder extends Binder {  
        public FzYsc getService() {  
            return FzYsc.this;  
        }  
    }  
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return new LocalBinder();  
    }  
  
    @Override  
    public void onDestroy() {  
        // TODO Auto-generated method stub  
        super.onDestroy();  
    }  
}
