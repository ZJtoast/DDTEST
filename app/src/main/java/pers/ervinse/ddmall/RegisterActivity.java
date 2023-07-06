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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import java.io.IOException;

import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.domain.User;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

/**
 * 注册页面
 */
public class RegisterActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Context mContext;

    private EditText register_login_name_et, register_login_password_et, register_login_desc_et, register_login_id_et, register_login_age_et;
    private Button register_btn;
    private RadioGroup sex_select;
    private RadioButton male_btn, female_btn;

    private String userName, userPassword, userExtendInfo, userId, userSex;
    private Integer userAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;

        register_login_name_et = findViewById(R.id.register_login_name_et);
        register_login_password_et = findViewById(R.id.register_login_password_et);
        register_login_desc_et = findViewById(R.id.register_login_desc_et);
        register_btn = findViewById(R.id.register_btn);
        register_login_id_et = findViewById(R.id.register_login_id_et);

        register_login_age_et = findViewById(R.id.register_login_age_et);
        sex_select = findViewById(R.id.sex_select);
        male_btn = findViewById(R.id.male_select_btn);
        female_btn = findViewById(R.id.female_select_btn);

        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        sex_select.setOnCheckedChangeListener((e, v) -> {
            switch (v) {
                case R.id.male_select_btn:
                    userSex = "男性";
                    break;
                case R.id.female_select_btn:
                    userSex = "女性";
                    break;
            }

        });

        //注册按钮监听事件
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "注册请求注册事件");

                userName = register_login_name_et.getText().toString();
                userPassword = register_login_password_et.getText().toString();
                userExtendInfo = register_login_desc_et.getText().toString();
                userId = register_login_id_et.getText().toString();

                String age = register_login_age_et.getText().toString();
                try {
                    userAge = Integer.parseInt(age);
                } catch (NumberFormatException nfe) {
                    Log.i(TAG, "用户输入不合法的年龄信息");
                    register_login_age_et.setText("请输入年龄");
                    Looper.prepare();
                    Toast.makeText(mContext, "登录失败,用户名或密码错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                new Thread() {
                    @Override
                    public void run() {
                        Log.d(TAG, "进入注册请求线程");

                        User user = new User(userId, userName, userPassword, userExtendInfo, userSex, userAge);
                        String userJson = JSONObject.toJSONString(user);
                        Log.i(TAG, "注册请求请求json:" + userJson);
                        String responseJson = null;
                        try {
                            //发送注册请求
                            String url = PropertiesUtils.getUrl(mContext);
                            responseJson = OkhttpUtils.doPost(url + "/users/register", userJson);
                            Log.i(TAG, "注册请求响应json:" + responseJson);
                            Result<Integer> response = JSONObject.parseObject(responseJson, new TypeReference<Result<Integer>>() {
                            });
                            Log.i(TAG, "注册请求响应解析对象:" + response);
                            if (response != null) {
                                //注册成功
                                if (response.getCode().equals(200) && response.getData().equals(1)) {
                                    Log.d(TAG, "注册请求成功");
                                    //将新注册的用户数据回传到登录页面
                                    Intent intent = new Intent();
                                    intent.putExtra("user", user);
                                    //设置数据状态
                                    setResult(RESULT_OK, intent);
                                    //销毁当前方法
                                    finish();

                                } else if (response.getCode().equals(201)) {
                                    //注册失败
                                    //子线程中准备Toast
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this, "注册失败！注册信息不全", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {
                                    //注册失败
                                    //子线程中准备Toast
                                    Looper.prepare();
                                    Toast.makeText(RegisterActivity.this, "注册失败！账号已存在", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(RegisterActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                }.start();


            }
        });
    }
}