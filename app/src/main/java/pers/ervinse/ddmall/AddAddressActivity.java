package pers.ervinse.ddmall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Address;
import pers.ervinse.ddmall.domain.Address_;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

public class AddAddressActivity extends Activity {
    private static final String TAG = AddAddressActivity.class.getSimpleName();
    private Address_ address;//存储页面中输入的数据
    private Context mContext;
    //    private String countryforAddress, provinceforAddress, townforAddress, districtforAddress, streetforAddress, detailAddress, receiveName, receiveTel;
    private EditText nation, province, city, district, street, consignee_name, detailed_address, phone_number;
    private Button comment_commit_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mContext=this;
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        street = findViewById(R.id.street);
        mContext = this;
        consignee_name = findViewById(R.id.consignee_name);
        detailed_address = findViewById(R.id.detailed_address);
        phone_number = findViewById(R.id.phone_number);

        //给地址提交按钮绑定事件
        comment_commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = new Address_(getString(nation), getString(province), getString(city), getString(district), getString(street), getString(consignee_name), getString(detailed_address), getString(phone_number));
                AddData();
                Intent intent = new Intent(mContext, AddressManageActivity.class);
                startActivity(intent);
            }
        });

    }


    public String getString(EditText view) {
        return view.getText().toString();
    }

    public void AddData() {
        Log.i(TAG, "保存数据");

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "进入保存地址数据线程");

                Gson gson = new Gson();
                String addressJson = gson.toJson(address);
                Log.i(TAG, "保存地址数据响应json:" + addressJson);
                String responseJson = null;
                try {
                    //发送获取购物车商品请求
                    String url = PropertiesUtils.getUrl(mContext);
                    responseJson = OkhttpUtils.doPost(url + "/users/location/add", addressJson);
                    Log.i(TAG, "保存地址数据响应json:" + responseJson);
                    responseJson = gson.fromJson(responseJson, String.class);
                    Log.i(TAG, "保存地址数据响应解析对象:" + responseJson);

                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }.start();
    }
}

