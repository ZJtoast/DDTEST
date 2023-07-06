package pers.ervinse.ddmall.search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.domain.User;
import pers.ervinse.ddmall.home.adapter.HomeAdapter;
import pers.ervinse.ddmall.home.fragment.HomeFragment;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

public class SearchResultActivity extends AppCompatActivity {
    private static final String TAG = pers.ervinse.ddmall.home.fragment.HomeFragment.class.getSimpleName();
    private Handler handler = new Handler();
    private RecyclerView rvHome;
    private List<Medicine> medicineList;
    private TextView tv_search_home;
    private TextView tv_put_search;
    private SearchAdapter adapter;
    private Context mContext;

    private ImageButton good_info_back_btn;
    private Thread current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mContext = this;
        Log.i(TAG, "主页视图初始化");
        rvHome = findViewById(R.id.rv_search);
        tv_search_home = findViewById(R.id.tv_search_home);
        tv_put_search = findViewById(R.id.tv_put_search);
        good_info_back_btn = findViewById(R.id.good_info_back_btn);
        initListener();
        Log.i(TAG, "进入搜索界面");
        String que = getIntent().getStringExtra("quest");
        tv_search_home.setText(que);
        Search(true);

    }


    /**
     * 初始化监听器
     */
    public void initListener() {
        tv_put_search.setOnClickListener(v -> {
            Search(false);
        });
        good_info_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void Search(Boolean isFirst) {
        String que = tv_search_home.getText().toString();
        Thread dataThread = null;
        if (!que.equals("")) {
            dataThread = new Thread(() -> {
                List<Medicine> medicines = new ArrayList<>();
                String responseJson = null;
                try {
                    Log.i(TAG, "模糊查询开始");
                    //发送获取商品请求
                    String url = PropertiesUtils.getUrl(mContext);
                    //提交查找请求
                    responseJson = OkhttpUtils.doGet(url + "/medicines/name/" + "?CommodityName=" + que);
                    Result<List<Medicine>> medicineResponse = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {
                    });
                    Log.i(TAG, "获取热点响应解析对象:" + medicineResponse);
                    //获取商品成功
                    //接下来接收药品图片
                    medicines = medicineResponse.getData();
                    Log.i(TAG, "获取商品响应json:" + responseJson);

                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }

                if (current.getName().equals(Thread.currentThread().getName())) {

                    if (isFirst) firstFresh(medicines);
                    else freshInMain(medicines);
                    for (Medicine medicine : medicines) {
                        String url = PropertiesUtils.getUrl(mContext);
                        try {
                            OkhttpUtils.saveImage(url + "/medicines/MedicinePicture/" + medicine.getCommodityID(), medicine.getCommodityID().toString(), mContext);
                        } catch (IOException e) {
                            Log.i(TAG, e.toString());
                        }
                    }
                }
                if (current.getName().equals(Thread.currentThread().getName())) {
                    //切回主线程调整布局
                    freshInMain(medicines);
                    medicineList = medicines;
                }
            });
        }
        current = dataThread;
        if (dataThread != null) dataThread.start();
    }


    public void freshInMain(List<Medicine> list) {
        Log.i(TAG, "输出适配器");
        handler.post(new Runnable() {
            @Override
            public void run() {
                //防止因为初始化未加载布局
                if (adapter != null) {
                    //更新数据适配器中商品数据
                    adapter.setmedicineList(list);
                    //刷新视图
                    adapter.flushView();
                }
            }
        });
    }

    public void firstFresh(List<Medicine> list) {
        Log.i(TAG, "刷新适配器");
        handler.post(new Runnable() {
            @Override
            public void run() {

                //创建首页循环视图适配器,加载数据
                adapter = new SearchAdapter(list, mContext);
                //循环视图加载适配器
                rvHome.setAdapter(adapter);
                //创建网格布局
                GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                //循环视图加载网格布局
                rvHome.setLayoutManager(manager);
            }
        });
    }


}

