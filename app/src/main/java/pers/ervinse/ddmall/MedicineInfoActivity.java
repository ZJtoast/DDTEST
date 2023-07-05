package pers.ervinse.ddmall;


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


import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

/**
 * 商品详情页面
 */
public class MedicineInfoActivity extends Activity {

    private static final String TAG = MedicineInfoActivity.class.getSimpleName();
    private Medicine medicine;
    private Context mContext;
    //返回按钮,商品图片
    private ImageView medicine_info_back_btn, medicine_image;
    private TextView medicine_name_tv, medicine_price_tv, medicine_description_tv, medicine_location_tv;
    //添加到购物车按钮
    private Button medicine_info_add_cart_btn;

    @Override
    protected void onRestart() {
        super.onRestart();
        setAdapter();
    }

    public List<? extends Map<String, ?>> getlist() {
        ArrayList<HashMap<String, Object>> comments = new ArrayList<>();
        //   String responseJson = null;
        //  try {
        //      responseJson = OkhttpUtils.doGet(url + "/暂定");
        //  } catch (IOException e) {
        //      Log.i(TAG, "请求药品评论错误");
        //  }
        // Log.i(TAG, "登录请求响应json:" + responseJson);
        //  UserResponse<Boolean> response = gson.fromJson(responseJson, UserResponse.class);
        //  Log.i(TAG, "登录请求响应解析数据:" + responseJson);
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

        setAdapter();
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
                        medicineForAdd.setCommodityName(medicine.getCommodityName());
                        String medicineJson = gson.toJson(medicineForAdd);
                        try {
                            //发送添加到购物车请求
                            String url = PropertiesUtils.getUrl(mContext);
                            responseJson = OkhttpUtils.doPost(url + "/cart/addmedicineToCart", medicineJson);
                            Log.i(TAG, "添加购物车商品响应json:" + responseJson);
                            responseJson = gson.fromJson(responseJson, String.class);
                            Log.i(TAG, "添加购物车商品响应解析对象:" + responseJson);

                            if (responseJson != null) {
                                //添加购物车成功
                                if (responseJson.equals("true")) {
                                    Looper.prepare();
                                    Toast.makeText(mContext, "商品已添加到购物车", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                    //添加购物车失败,商品已经在购物车
                                } else {
                                    Looper.prepare();
                                    Toast.makeText(mContext, "商品已经在购物车啦", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
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

        int id = mContext.getResources().getIdentifier(medicine.getCommodityID().toString(), "drawable", mContext.getPackageName());
        medicine_image.setImageResource(id);

    }
}