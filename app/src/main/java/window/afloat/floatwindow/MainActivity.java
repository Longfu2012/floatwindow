package window.afloat.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import window.afloat.floatwindow.arc.ArcMenu;
import window.afloat.floatwindow.arc.RayMenu;

public class MainActivity extends Activity {
    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };
    private Button startBtn;
    private Context mContext;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        Intent intent = new Intent(mContext,FloatService.class);
        startService(intent);
        finish();

    }

    private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "¿ª·¢ÖÐ:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public class OnStartListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext,FloatService.class);
            startService(intent);
        }
    }

}
