package pers.ervinse.ddmall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

public class AddressManageActivity extends Activity {
    private static final String TAG = AddAddressActivity.class.getSimpleName();
    private Context mContext;
    private Button editAddressButton;//编辑地址按钮
    private Button addAddressButton;//添加地址按钮
    private ImageView address_info_back_btn;//导航栏返回按钮

    private static final int Add__Address__CODE = 1;

    protected void onRestart() {
        super.onRestart();
        setAdapter();
    }

    //请求获取数据
    public List<? extends Map<String, ?>> getlist() {
        ArrayList<HashMap<String, Object>> Address = new ArrayList<>();
        Gson gson = new Gson();
        String url = PropertiesUtils.getUrl(mContext);
        String responseJson = null;
        try {
            responseJson = OkhttpUtils.doGet(url + "/users/location/all");
        } catch (IOException e) {
            Log.i(TAG, "请求地址发生错误");
        }

        Log.i(TAG, "请求响应地址列表:" + responseJson);
        Log.i(TAG, "请求响应解析地址列表:" + responseJson);
        return Address;
    }

    private void setAdapter() {
        SimpleAdapter Address = new SimpleAdapter(
                mContext, getlist(), R.layout.item_address,
                new String[]{"userName", "phoneNumber", "detailedAddress"},
                new int[]{R.id.nameTextView, R.id.phoneTextView, R.id.addressTextView});
        ListView sv = findViewById(R.id.addressListView);
        sv.setAdapter(Address);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        mContext = this;
        editAddressButton = findViewById(R.id.editAddressButton);
        addAddressButton = findViewById(R.id.addAddressButton);
        address_info_back_btn = findViewById(R.id.address_info_back_btn);

        setAdapter();
        initLinster();

    }

    private void initLinster() {
        //编辑按钮绑定编辑，暂定为空
        editAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //添加地址绑定添加地址的页面
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "前往添加地址事件");

                //跳转注册页面，这里只管跳转到添加页面，在添加页面把数据传输给后端，然后再跳转回本页面，刷新数据即可
                Intent intent = new Intent(mContext, AddAddressActivity.class);
                startActivity(intent);
            }
        });
    }
}
