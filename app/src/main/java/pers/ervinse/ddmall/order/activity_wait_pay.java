package pers.ervinse.ddmall.order;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import pers.ervinse.ddmall.MainActivity;
import pers.ervinse.ddmall.R;

public class activity_wait_pay extends Activity {
    private static final String TAG = activity_wait_pay.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_pay);
        Log.i(TAG,"成功进入待支付界面");
    }
}
