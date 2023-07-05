package pers.ervinse.ddmall.user.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import pers.ervinse.ddmall.BaseFragment;
import pers.ervinse.ddmall.LoginActivity;
import pers.ervinse.ddmall.MedicineInfoActivity;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Token;
import pers.ervinse.ddmall.domain.User;

public class UserFragment extends BaseFragment {

    private static final String TAG = UserFragment.class.getSimpleName();
    private static final int LOGIN_REQUEST_CODE = 1;

    //当前登录状态
    private boolean isLogin = false;

    private ImageView user_photo_image;
    private TextView user_desc_tv;
    private TextView user_id_tv, user_name_tv;
    private Button user_logout_btn;
    private Token token;
    private View user_bar;


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
        user_desc_tv = view.findViewById(R.id.user_desc);
        user_id_tv = view.findViewById(R.id.user_id_tv);
        user_name_tv = view.findViewById(R.id.user_name_tv);
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
        user_desc_tv.setOnClickListener((view) -> {
            Intent intent = new Intent(mContext, MedicineInfoActivity.class);
            Medicine medicine = new Medicine(true, "999感冒灵", "这个药太棒了", "广东", "item_example", 11, 1, 1, 2);
            intent.putExtra("Medicines", medicine);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
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
                                user_name_tv.setText("点击登录");
                                user_desc_tv.setText("用户信息");
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
                    User userInfo = (User) data.getSerializableExtra("user");
                    String userName = userInfo.getUserName();
                    String userDesc = userInfo.getUserSex() + "   " + userInfo.getUserAge().toString() + "岁";
                    String userId = userInfo.getUserAccount();
                    Log.i(TAG, "用户登录数据回传: userName = " + userName
                            + ",userDesc = " + userDesc);
                    token = (Token) data.getSerializableExtra("token");
                    user_name_tv.setText(userName);
                    user_id_tv.setText(userId);
                    user_desc_tv.setVisibility(View.VISIBLE);
                    user_desc_tv.setText("用户信息:" + userDesc);
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
