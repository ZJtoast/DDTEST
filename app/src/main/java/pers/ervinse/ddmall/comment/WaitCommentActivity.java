package pers.ervinse.ddmall.comment;

import static com.google.android.material.snackbar.BaseTransientBottomBar.handler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Comment;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.type.adapter.TypeAdapter;
import pers.ervinse.ddmall.type.fragment.TypeFragment;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

public class WaitCommentActivity extends Activity {
    private static final String TAG = TypeFragment.class.getSimpleName();
    private Context mContext;
    private Thread current;
    private Handler handler = new Handler();
    private RecyclerView rv_type;
    private CommentAdapter adapter;
    private RadioGroup commentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_comment);
        mContext = this;
        initData();
    }

    public void firstFresh(List<Comment> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //创建类别循环视图适配器,加载数据
                adapter = new CommentAdapter(list, mContext);
                //循环视图加载适配器
                rv_type.setAdapter(adapter);
                //创建网格布局
                GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                //循环视图加载网格布局
                rv_type.setLayoutManager(manager);
            }
        });
    }

    public void freshInMain(List<Comment> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //防止因为初始化未加载布局
                if (adapter != null) {
                    //更新数据适配器中商品数据
                    adapter.setCommentList(list);
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
        Log.i(TAG, "待评论数据初始化");
        Thread initThread = new Thread(() -> {
            List<Comment> comments = new ArrayList<>();
            Log.i(TAG, "进入获取待评论商品信息线程");
            String responseJson = null;
            try {
                //发送获取商品请求
                String url = PropertiesUtils.getUrl(mContext);
                //提交查找请求
                responseJson = OkhttpUtils.doGet(url + "/order/all");
                Result<List<Comment>> CommentResponse = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Comment>>>() {
                });
                comments = CommentResponse.getData();
                Log.i(TAG, "获取评论响应解析对象:");

                if (current.getName().equals(Thread.currentThread().getName())) {
                    firstFresh(medicines);

                    for (Medicine medicine : medicines) {
                        try {
                            OkhttpUtils.saveImage(url + "/medicines/MedicinePicture/" + medicine.getCommodityID(), medicine.getCommodityID().toString(), mContext);
                        } catch (IOException e) {
                            Log.i(TAG, e.toString());
                        }
                    }
                    freshInMain(medicines);
                    medicineList = medicines;
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

    public void getData() {
        Log.i(TAG, "联网刷新数据");
        Thread dataThread = new Thread(() -> {
            @SuppressLint("NotifyDataSetChanged")
            List<Medicine> medicines = new ArrayList<>();
            String responseJson = null;
            try {
                Log.i(TAG, "进入获取商品类型线程,商品类型为" + type.toString());
                //发送获取商品请求
                String url = PropertiesUtils.getUrl(mContext);


                //提交查找请求
                responseJson = OkhttpUtils.doGet(url + "/medicines/type/" + type.toString());
                Result<List<Medicine>> medicineResponse = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {});
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
                freshInMain(medicines);
                for (Medicine medicine : medicines) {
                    String url = PropertiesUtils.getUrl(mContext);
                    try {
                        OkhttpUtils.saveImage(url + "/medicines/MedicinePicture/" + medicine.getCommodityID(), medicine.getCommodityID().toString(), mContext);
                    } catch (IOException e) {
                        Log.i(TAG, e.toString());
                    }
                }
                //切回主线程调整布局
                freshInMain(medicines);
                medicineList = medicines;
            }
        });
        current = dataThread;
        dataThread.start();
    }

    public void refreshData() {
        getData();
    }
}
