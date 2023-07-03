package pers.ervinse.ddmall;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ActionActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGHT = 2000; // 两秒后进入系统

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(ActionActivity.this,
                        MainActivity.class);
                ActionActivity.this.startActivity(mainIntent);
                ActionActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);

    }
}