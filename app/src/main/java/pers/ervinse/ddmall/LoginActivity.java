package pers.ervinse.ddmall;

import androidx.annotation.Nullable;

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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import java.io.IOException;

import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.domain.Token;
import pers.ervinse.ddmall.domain.User;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

/**
 * 登录页面
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    //注册请求码
    private static final int REGISTER_REQUEST_CODE = 1;

    private Context mContext;

    private EditText login_name_et, login_password_et;

    private TextView user_register_btn;
    private ImageButton login_btn;

    private String userName, userPassword;
    private String userDesc, userExtendInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        login_name_et = findViewById(R.id.login_name_et);
        login_password_et = findViewById(R.id.login_password_et);
        user_register_btn = findViewById(R.id.user_register_btn);
        login_btn = findViewById(R.id.login_btn);


        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {

        //登录按钮监听事件
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "登录按钮响应事件");

                //获取当前输入框内容
                userName = login_name_et.getText().toString();
                userPassword = login_password_et.getText().toString();

                login();


            }
        });

        //注册按钮监听事件
        user_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "前往注册页面事件");

                //跳转注册页面
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivityForResult(intent, REGISTER_REQUEST_CODE);
            }
        });
    }


    /**
     * 在子线程中发送登录请求
     */
    private void login() {

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "进入请求登录线程");


                User user = new User(userName, userPassword);//userName这里对应UserAccount
                String userJson = JSON.toJSONString(user);
                Log.i(TAG, "登录请求json:" + userJson);
                String responseJson = null;

                try {
                    //发送登录请求
                    String url = PropertiesUtils.getUrl(mContext);
                    responseJson = OkhttpUtils.doPost(url + "/users/login", userJson);

                    Result<Token> response = JSONObject.parseObject(responseJson, new TypeReference<Result<Token>>() {
                    });
                    Integer code = response.getCode();
                    Log.i(TAG, "登录请求响应解析数据:" + responseJson);
                    if (code.equals(200)) {
                        Token tok = response.getData();
                        TokenContextUtils.setToken(tok.getToken());
                        //登录成功
                        //发送请求获取当前用户名对应的简介
                        responseJson = OkhttpUtils.doGetByToken(url + "/users/getDescription/" + userName, TokenContextUtils.getToken());
                        Log.i(TAG, "获取描述请求响应json:" + responseJson);
                        Result<User> responseS = JSONObject.parseObject(responseJson, new TypeReference<Result<User>>() {
                        });
                        User userRes = responseS.getData();

                        Log.i(TAG, "获取描述请求响应解析数据:" + userRes);
                        //回传用户名和简介


                        Intent intent = new Intent();
                        intent.putExtra("user", userRes);

                        //intent.putExtra("userId", userId);
                        //设置数据状态
                        setResult(RESULT_OK, intent);
                        //销毁当前方法

                        finish();
                    } else {
                        //登录失败
                        //子线程中准备Toast
                        Looper.prepare();
                        Toast.makeText(mContext, "登录失败,用户名或密码错误", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    //抛出异常
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "服务器异常", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }.start();

    }

    /**
     * 数据回传接收方法
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //判断由哪一个intent发出当请求（是否是requestCode == 1）
        switch (requestCode) {
            //注册页面回传数据
            case REGISTER_REQUEST_CODE:
                //判断返回当数据是否正常（判断是否是resultCode == RESULT_OK）
                if (resultCode == RESULT_OK) {
                    //获取数据并打印
                    User user = (User) data.getSerializableExtra("user");
                    userName = user.getUserAccount().toString();
                    userPassword = user.getUserPassword();
                    userExtendInfo = user.getUserExtendInfo();
                    Log.i(TAG, "用户注册数据回传: " +
                            "userName = " + userName +
                            ",userPassword = " + userPassword +
                            ",userDesc = " + userExtendInfo);
                    //注册完自动登录
                    login();
                }
                break;
            default:
        }
    }
}