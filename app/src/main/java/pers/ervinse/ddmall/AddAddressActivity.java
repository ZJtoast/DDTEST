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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Address;
import pers.ervinse.ddmall.domain.Address_;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

public class AddAddressActivity extends Activity {
    private static final String TAG = AddAddressActivity.class.getSimpleName();
    private Address_ address;//存储页面中输入的数据
    private Context mContext;
    private EditText nation, province, city, district, street, consignee_name, detailed_address, phone_number;
    private Button comment_commit_btn;
    private ImageButton medicine_info_back_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mContext = this;

        nation = findViewById(R.id.nation);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        street = findViewById(R.id.street);
        consignee_name = findViewById(R.id.consignee_name);
        detailed_address = findViewById(R.id.detailed_address);
        phone_number = findViewById(R.id.phone_number);
        comment_commit_btn = findViewById(R.id.comment_commit_btn);
        medicine_info_back_btn = findViewById(R.id.medicine_info_back_btn);
        address = new Address_();
        medicine_info_back_btn.setOnClickListener(v -> {
            finish();
        });
        //给地址提交按钮绑定事件
        comment_commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address.setCountryforAddress(nation.getText().toString());
                address.setProvinceforAddress(province.getText().toString());
                address.setTownforAddress(city.getText().toString());
                address.setDistrictforAddress(district.getText().toString());
                address.setStreetforAddress(street.getText().toString());
                address.setDetailAddress(detailed_address.getText().toString());
                address.setReceiveName(consignee_name.getText().toString());
                address.setReceiveTel(phone_number.getText().toString());
                AddData();
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
                Log.i(TAG, "进入添加地址数据线程");
                String addressJson = JSONObject.toJSONString(address);
                Log.i(TAG, "添加地址数据响应json:" + addressJson);
                String responseJson = null;
                try {
                    //发送获取购物车商品请求
                    String url = PropertiesUtils.getUrl(mContext);
                    responseJson = OkhttpUtils.doPostByToken(url + "/users/location/add", addressJson, TokenContextUtils.getToken());
                    Log.i(TAG, "添加地址数据响应json:" + responseJson);
                    Result<String> result = JSONObject.parseObject(responseJson, new TypeReference<Result<String>>() {
                    });
                    Log.i(TAG, "添加地址数据响应解析对象:" + responseJson);
                    if (result.getCode().equals(200)) {
                        Looper.prepare();
                        Toast.makeText(mContext, "添加地址成功", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        finish();
                    }
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