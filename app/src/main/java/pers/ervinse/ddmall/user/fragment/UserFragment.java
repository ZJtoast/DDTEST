package pers.ervinse.ddmall.user.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;

import pers.ervinse.ddmall.AddAddressActivity;
import pers.ervinse.ddmall.AddressManageActivity;
import pers.ervinse.ddmall.BaseFragment;
import pers.ervinse.ddmall.LoginActivity;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.allorders.AllOrdersActivity;
import pers.ervinse.ddmall.comment.WaitCommentActivity;
import pers.ervinse.ddmall.deliver.WaitDeliverActivity;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.pay.WaitPayActivity;
import pers.ervinse.ddmall.receive.WaitReceiveActivity;
import pers.ervinse.ddmall.domain.User;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

public class UserFragment extends BaseFragment {

    private static final String TAG = UserFragment.class.getSimpleName();
    private static final int LOGIN_REQUEST_CODE = 1;
    //当前登录状态
    private boolean isLogin = false;

    private ImageView user_photo_image;
    private TextView user_cancel;

    private TextView user_all_order;
    private TextView user_id_tv, user_name_tv, user_location;
    private Button user_logout_btn;

    private ImageButton wait_pay, wait_comment, wait_deliver, wait_receive;
    private View user_bar;
    private User userInfo;

    /**
     * 初始化视图
     *
     * @return
     */
    @Override
    public View initView() {
        Log.i(TAG, "用户视图初始化");
        View view = View.inflate(mContext, R.layout.fragment_user, null);
        user_logout_btn = view.findViewById(R.id.user_logout_btn);
        user_bar = view.findViewById(R.id.user_bar);
        user_all_order = view.findViewById(R.id.user_all_order);
        user_id_tv = view.findViewById(R.id.user_id_tv);
        user_name_tv = view.findViewById(R.id.user_name_tv);
        wait_pay = view.findViewById(R.id.wait_pay);
        wait_comment = view.findViewById(R.id.wait_comment);
        wait_deliver = view.findViewById(R.id.wait_deliver);
        wait_receive = view.findViewById(R.id.wait_receive);
        user_location = view.findViewById(R.id.user_location);
        user_cancel = view.findViewById(R.id.user_cancel);
        return view;
    }

    //初始化数据
    public void initData() {
        super.initData();
        Log.i(TAG, "用户数据初始化");

        initListener();

    }

    /**
     * 初始化监听器
     */
    private void initListener() {

        user_cancel.setOnClickListener(v -> {
            if (isLogin) {
                Log.i(TAG, "用户准备注销账号");

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                        .setTitle("注销账号")
                        .setMessage("您是否要注销账号?请慎重决定")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        Log.i(TAG, "进入注销账户数量线程");
                                        String responseJson = null;
                                        //获取要删除的商品ID
                                        try {
                                            //发送删除请求
                                            String url = PropertiesUtils.getUrl(mContext);
                                            responseJson = OkhttpUtils.doDeleteByToken(url + "/users/unsubscribe", TokenContextUtils.getToken());
                                            Log.i(TAG, "注销账户响应json:" + responseJson);
                                            Result<Boolean> result = JSONObject.parseObject(responseJson, new TypeReference<Result<Boolean>>() {
                                            });
                                            Log.i(TAG, "注销账户响应解析对象:" + responseJson);
                                            Integer code = result.getCode();
                                            //删除成功
                                            if (code != null) {
                                                if (code.equals(200)) {
                                                    //切回主线程刷新视图
                                                    isLogin = false;
                                                    TokenContextUtils.setToken("null");
                                                    user_name_tv.setText("点击登录");
                                                    user_id_tv.setText("");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                                            .setTitle("注销账号")
                                                            .setMessage("您的账户已被注销！");
                                                    //创建删除对话框并显示
                                                    Looper.prepare();
                                                    AlertDialog alertDialog = builder.create();
                                                    alertDialog.show();
                                                }
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
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                //创建删除对话框并显示
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else {
                Toast.makeText(mContext, "用户未登录", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        user_all_order.setOnClickListener(v -> {
            if (isLogin) {
                Log.i(TAG, "用户打开全部订单界面");
                Intent intent = new Intent(mContext, AllOrdersActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "用户未登录", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        user_location.setOnClickListener(v -> {
            if (isLogin) {
                Log.i(TAG, "用户打开地址管理界面");
                Intent intent = new Intent(mContext, AddAddressActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "用户未登录", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        wait_pay.setOnClickListener(v -> {
            if (isLogin) {
                Log.i(TAG, "用户打开待支付界面");
                Intent intent = new Intent(mContext, WaitPayActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "用户未登录", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        wait_comment.setOnClickListener(v -> {
            if (isLogin) {
                Log.i(TAG, "用户打开待评价界面");
                Intent intent = new Intent(mContext, WaitCommentActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "用户未登录", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        wait_deliver.setOnClickListener(v -> {
            if (isLogin) {
                Log.i(TAG, "用户打开待发货界面");
                Intent intent = new Intent(mContext, WaitDeliverActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "用户未登录", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        wait_receive.setOnClickListener(v -> {
            if (isLogin) {
                Log.i(TAG, "用户打开待收货界面");
                Intent intent = new Intent(mContext, WaitReceiveActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "用户未登录", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
        //登录
        user_name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "用户登录事件");
                if (!isLogin) {

                    Log.i(TAG, "用户未登录");
                    //前往登录页面
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST_CODE);
                }
            }
        });

        //登出
        user_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "用户登出事件");
                if (isLogin) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle("退出登录")
                            .setMessage("您是否要退出登录?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //关闭对话框
                                    dialogInterface.dismiss();
                                    //用户名恢复,简介不可见
                                    isLogin = false;
                                    TokenContextUtils.setToken("null");
                                    user_name_tv.setText("点击登录");
                                    user_id_tv.setText("");
                                }
                            })

                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //关闭对话框
                                    dialogInterface.dismiss();
                                }
                            });

                    //创建对话框,并显示
                    AlertDialog logoutDialog = builder.create();
                    logoutDialog.show();
                }


            }
        });

    }

    /**
     * 数据回传
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //判断由哪一个intent发出当请求（是否是requestCode == 1）
        switch (requestCode) {
            case LOGIN_REQUEST_CODE:
                //判断返回当数据是否正常（判断是否是resultCode == RESULT_OK）
                if (resultCode == RESULT_OK) {
                    isLogin = true;
                    //获取数据并打印
                    userInfo = (User) data.getSerializableExtra("user");
                    try {
                        String userDesc = userInfo.getUserSex() + "   " + userInfo.getUserAge().toString() + "岁";
                        Log.i(TAG, "用户登录数据回传: userName ");

                    } catch (NullPointerException ne) {
                        Log.i(TAG, "缺少了某些信息");
                    }
                    String userName = userInfo.getUserName();
                    String userAccount = userInfo.getUserAccount();
                    user_name_tv.setText(userName);
                    user_id_tv.setText(userAccount);

                }
                break;
            default:
        }
    }

    @Override
    public void refreshData() {
    }

    @Override
    public void saveData() {

    }

}
