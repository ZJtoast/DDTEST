package pers.ervinse.ddmall.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.comment.CommentAdapter;
import pers.ervinse.ddmall.domain.Order;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.type.fragment.TypeFragment;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

public class WaitPayActivity extends Activity {
    private static final String TAG = WaitPayActivity.class.getSimpleName();
    private Context mContext;
    private List<Order> orderList;
    private Handler handler = new Handler();
    private RecyclerView rv_type;
    private PayAdapter adapter;
    private Thread current;
    private ImageButton good_info_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_pay);
        rv_type = findViewById(R.id.drug_lv);
        good_info_back_btn = findViewById(R.id.good_info_back_btn);
        good_info_back_btn.setOnClickListener(v -> {
            finish();
        });
        mContext = this;
        initData();
    }


    public void firstFresh(List<Order> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //创建类别循环视图适配器,加载数据
                adapter = new PayAdapter(list, mContext);
                //循环视图加载适配器
                rv_type.setAdapter(adapter);
                //创建网格布局
                GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                //循环视图加载网格布局
                rv_type.setLayoutManager(manager);
            }
        });
    }

    public void freshInMain(List<Order> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //防止因为初始化未加载布局
                if (adapter != null) {
                    //更新数据适配器中商品数据
                    adapter.setPayList(list);
                    //刷新视图
                    adapter.flushView();
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initData() {
        Log.i(TAG, "待支付数据初始化");
        Thread initThread = new Thread(() -> {
            List<Order> orders = new ArrayList<>();
            Log.i(TAG, "进入获取待支付商品信息线程");
            String responseJson = null;
            try {
                //发送获取商品请求
                String url = PropertiesUtils.getUrl(mContext);
                //提交查找请求
                responseJson = OkhttpUtils.doGetByToken(url + "/order/all", TokenContextUtils.getToken());
                Result<List<Order>> result = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Order>>>() {
                });
                List<Order> orderTemp = result.getData();
                for (Order order : orderTemp) {
                    if (order.getOrderPayState().equals(1)) {
                        orders.add(order);
                    }
                }
                Log.i(TAG, "获取支付响应解析对象:");

                if (current.getName().equals(Thread.currentThread().getName())) {
                    firstFresh(orders);
                    for (Order order : orders) {
                        try {
                            OkhttpUtils.saveImage(url + "/medicines/MedicinePicture/" + order.getCommodityID(), order.getCommodityID().toString(), mContext);
                        } catch (IOException e) {
                            Log.i(TAG, e.toString());
                        }
                    }
                }
                if (current.getName().equals(Thread.currentThread().getName())) {
                    freshInMain(orders);
                    orderList = orders;
                }

            } catch (IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

        });
        current = initThread;
        initThread.start();
    }
}

