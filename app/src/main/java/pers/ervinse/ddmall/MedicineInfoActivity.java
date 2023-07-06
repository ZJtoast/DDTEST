package pers.ervinse.ddmall;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Medicine medicine;
    private Context mContext;
    //返回按钮,商品图片
    private ImageView medicine_info_back_btn, medicine_image, cart_item_add_btn, cart_item_sub_btn;
    private TextView medicine_cart_tv, medicine_name_tv, medicine_price_tv, medicine_description_tv, medicine_location_tv, cart_item_value_tv;
    //添加到购物车按钮
    private Button medicine_info_add_cart_btn;

    private Integer number;

    @Override
    protected void onRestart() {
        super.onRestart();
        setAdapter();
    }

    public List<? extends Map<String, ?>> getlist() {
        ArrayList<HashMap<String, Object>> comments = new ArrayList<>();
        Thread dataThread = new Thread(() -> {
            @SuppressLint("NotifyDataSetChanged")
            List<Medicine> medicines = new ArrayList<>();
            String responseJson = null;

        });
        dataThread.start();
        return comments;
    }

    public void setAdapter() {
        SimpleAdapter comment = new SimpleAdapter(
                mContext, getlist(), R.layout.item_comment,
                new String[]{"userName", "Comment"},
                new int[]{R.id.comment_userName_tv, R.id.comment_tv});
        ListView sv = findViewById(R.id.comment_lv);
        sv.setAdapter(comment);
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
        number = 1;
        initListener();
    }

    private void initListener() {
        medicine_cart_tv.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("type", "cart");
            startActivity(intent);
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

                        Gson gson = new Gson();
                        String responseJson = null;

                        //获取当前商品信息
                        Medicine medicineForAdd = new Medicine();
                        medicineForAdd.setCommodityID(medicine.getCommodityID());
                        medicineForAdd.setCommodityNum(medicine.getCommodityNum());
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
                                    Toast.makeText(mContext, "商品已添加到购物车", Toast.LENGTH_SHORT).show();
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

        medicine_name_tv.setText(medicine.getCommodityName());
        medicine_price_tv.setText(String.valueOf(medicine.getCommodityPrice()));
        medicine_description_tv.setText(medicine.getCommodityDesc());
        medicine_location_tv.setText(medicine.getMerchantLocation());

        try {
            OkhttpUtils.setImage(medicine_image, medicine.getCommodityID().toString(), mContext);
        } catch (IOException e) {
            Log.i("TAG", "图片传输错误");
        }
        setAdapter();
    }
}