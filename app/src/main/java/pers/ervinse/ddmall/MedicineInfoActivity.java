package pers.ervinse.ddmall;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pers.ervinse.ddmall.domain.Comment;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.domain.User;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

/**
 * 商品详情页面
 */
public class MedicineInfoActivity extends Activity {

    private static final String TAG = MedicineInfoActivity.class.getSimpleName();
    ;
    private Medicine medicine;
    private Context mContext;
    //返回按钮,商品图片
    private ImageView medicine_info_back_btn, medicine_image, cart_item_add_btn, cart_item_sub_btn;
    private TextView comment_all_tv, medicine_cart_tv, medicine_name_tv, medicine_price_tv, medicine_description_tv, medicine_location_tv, cart_item_value_tv;
    //添加到购物车按钮
    private Button medicine_info_add_cart_btn, medicine_info_purchase_btn;
    private Integer number, commentNum;

    private Handler handler = new Handler();
    private ListView sv;

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void SetAdapter() {
        Log.i("TAG", "设置ADAPTER");
        Thread dataThread = new Thread(() -> {
            List<Comment> commentList = null;
            List<Map<String, String>> comments = new ArrayList<>();
            String responseJson = null;
            String url = PropertiesUtils.getUrl(mContext);
            try {
                responseJson = OkhttpUtils.doGet(url + "/medicines/review/" + medicine.getCommodityID());
                Result<List<Comment>> result = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Comment>>>() {
                });
                Log.i(TAG, "获取response" + responseJson);
                if (result.getCode().equals(200)) {
                    commentList = result.getData();
                    if (commentList != null)
                        for (Comment comment : commentList) {
                            Map<String, String> com = new HashMap<>();
                            com.put("userName", "东东用户:" + comment.getUserID().toString() + "说");
                            com.put("Comment", comment.getReviewText());
                            comments.add(com);
                            Log.i(TAG, "获取用户评价" + com.get("Comment"));
                        }
                } else {
                    Log.i(TAG, "获取商品详细信息--评价出现传输码异常");
                }
            } catch (IOException e) {
                Log.i(TAG, "获取商品详细信息--评价出现IO异常" + e.toString());
            }
            commentNum = comments.size();
            comment_all_tv.setText("药品评价" + "(" + commentNum.toString() + ")");
            SimpleAdapter comment = new SimpleAdapter(
                    MedicineInfoActivity.this, comments, R.layout.item_comment,
                    new String[]{"userName", "Comment"},
                    new int[]{R.id.comment_userName_tv, R.id.comment_tv});
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //全局刷新
                    sv.setAdapter(comment);
                }
            });

        });
        dataThread.start();
    }

    /**
     * 创建视图
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_info);
        sv = findViewById(R.id.comment_lv);
        mContext = this;
        medicine_info_back_btn = findViewById(R.id.medicine_info_back_btn);
        medicine_name_tv = findViewById(R.id.medicine_name_tv);
        medicine_price_tv = findViewById(R.id.medicine_price_tv);
        medicine_description_tv = findViewById(R.id.medicine_description_tv);
        medicine_location_tv = findViewById(R.id.medicine_location_tv);
        medicine_info_add_cart_btn = findViewById(R.id.medicine_info_add_cart_btn);
        medicine_image = findViewById(R.id.medicine_image);
        cart_item_value_tv = findViewById(R.id.cart_item_value_tv);
        cart_item_add_btn = findViewById(R.id.cart_item_add_btn);
        cart_item_sub_btn = findViewById(R.id.cart_item_sub_btn);
        medicine_cart_tv = findViewById(R.id.medicine_cart_tv);
        medicine_info_purchase_btn = findViewById(R.id.medicine_info_purchase_btn);
        comment_all_tv = findViewById(R.id.comment_all_tv);
        number = 1;
        initListener();
    }

    private void initListener() {
        medicine_info_purchase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                if (!TokenContextUtils.getToken().equals("null")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            // 创建一个AlertDialog.Builder对象，并传入当前的上下文（mContext）
                            .setTitle("立即购买药品")
                            // 设置对话框的标题为"完成结算"
                            .setMessage("是否提交订单?")
                            // 设置对话框的消息内容，包括商品总计价格（totalPrice）
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 设置对话框的确定按钮，当用户点击确定时触发此处的代码   这里是结算的代码
                                    String url = PropertiesUtils.getUrl(mContext);

                                    if (medicine != null) {
                                        new Thread(() -> {
                                            String responseJson = null;
                                            String ID = medicine.getCommodityID().toString();
                                            String Num = medicine.getCommodityNum().toString();
                                            try {
                                                responseJson = OkhttpUtils.doPostByToken(url + "/order/add?CommodityID=" + ID + "&CommodityNum=" + Num, "", TokenContextUtils.getToken());
                                            } catch (IOException e) {
                                                Log.i("IO异常", "结算失败");
                                            }
                                            Result<String> result = JSONObject.parseObject(responseJson, new TypeReference<Result<String>>() {
                                            });
                                            if (result.getCode().equals(200)) {
                                                Looper.prepare();
                                                Toast.makeText(mContext, "完成药品购买", Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            } else {
                                                Looper.prepare();
                                                Toast.makeText(mContext, "药品购买失败，地址未填写", Toast.LENGTH_SHORT).show();
                                                Looper.loop();
                                            }
                                        }).start();
                                    }


                                    // 显示一个短暂的Toast提示，显示完成商品购买的信息和总计价格
                                }
                            })

                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 设置对话框的取消按钮，当用户点击取消时触发此处的代码
                                    // 在这里可以添加取消操作的逻辑代码
                                    Toast.makeText(mContext, "取消成功", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // 根据以上设置创建AlertDialog对象，但并没有显示对话框
                    //创建删除对话框并显示
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        medicine_cart_tv.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("type", "cart");
            setResult(RESULT_OK, intent);
            finish();
        });
        medicine_info_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        medicine_info_add_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        Log.i(TAG, "进入获取商品线程");
                        String responseJson = null;

                        //获取当前商品信息
                        Medicine medicineForAdd = new Medicine();
                        medicineForAdd.setCommodityID(medicine.getCommodityID());
                        medicineForAdd.setCommodityNum(number);
                        String medicineJson = JSONObject.toJSONString(medicineForAdd);
                        try {
                            //发送添加到购物车请求
                            String url = PropertiesUtils.getUrl(mContext);
                            responseJson = OkhttpUtils.doPostByToken(url + "/shoppingCart", medicineJson, TokenContextUtils.getToken());
                            Log.i(TAG, "添加购物车商品响应json:" + responseJson);
                            Result<Boolean> result = JSONObject.parseObject(responseJson, new TypeReference<Result<Boolean>>() {
                            });
                            Log.i(TAG, "添加购物车商品响应解析对象:" + responseJson);

                            if (result.getCode() != null) {
                                //添加购物车成功
                                if (result.getCode().equals(200)) {
                                    Looper.prepare();
                                    Toast.makeText(mContext, "添加购物车成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    //添加购物车失败,商品已经在购物车
                                } else if (result.getCode().equals(403)) {
                                    Looper.prepare();
                                    Toast.makeText(mContext, "未登录无法添加购物车", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(mContext, "商品已经在购物车啦", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                }.start();
            }
        });

        cart_item_add_btn.setOnClickListener(e -> {
            number = number + 1;
            cart_item_value_tv.setText(number.toString());
        });
        cart_item_sub_btn.setOnClickListener(e -> {
            number = number - 1;
            if (number <= 0) number = 0;
            cart_item_value_tv.setText(number.toString());
        });
    }

    /**
     * Activity创建时调用
     * 根据intent中携带的商品信息创建视图数据
     */
    @Override
    protected void onStart() {
        super.onStart();

        //加载商品数据
        Intent intent = getIntent();
        medicine = (Medicine) intent.getSerializableExtra("medicine");
        medicine.setCommodityNum(1);
        medicine_name_tv.setText(medicine.getCommodityName());
        Double price = medicine.getCommodityPrice().doubleValue() / 100.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String priceString = decimalFormat.format(price);
        medicine_price_tv.setText(String.valueOf(priceString));
        medicine_description_tv.setText(medicine.getCommodityDesc());
        medicine_location_tv.setText("东东快药自营店铺");
        try {
            OkhttpUtils.setImage(medicine_image, medicine.getCommodityID().toString(), mContext);
        } catch (IOException e) {
            Log.i("TAG", "图片传输错误");
        }
        SetAdapter();
    }

}