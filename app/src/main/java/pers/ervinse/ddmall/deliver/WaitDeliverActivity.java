package pers.ervinse.ddmall.deliver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import pers.ervinse.ddmall.R;

public class WaitDeliverActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 2000; // 两秒后进入系统

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_deliver);


    }
}
