package pers.ervinse.ddmall.type.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.BaseFragment;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.home.adapter.HomeAdapter;
import pers.ervinse.ddmall.type.adapter.TypeAdapter;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

/**
 * 全部商品碎片
 */
public class TypeFragment extends BaseFragment {

    private static final String TAG = TypeFragment.class.getSimpleName();
    List<Medicine> medicineList;
    //线程处理器
    private Handler handler = new Handler();
    private RecyclerView rv_type;
    private TypeAdapter adapter;
    private Integer type = 0;
    private Thread current;
    private RadioGroup typeGroup;
    private RadioButton medicine_chinese, medicine_antipyretic, medicine_vessel, medicine_respiratory, medicine_anticold, medicine_children, medicine_skin, medicine_stomach, medicine_nutrition, medicine_instrument;

    /**
     * 初始化视图
     *
     * @return
     */
    @Override
    public View initView() {
        Log.i(TAG, "类别视图初始化");
        //为当前fragment加载布局文件
        View view = View.inflate(mContext, R.layout.fragment_type, null);
        rv_type = view.findViewById(R.id.rv_type);
        typeGroup = view.findViewById(R.id.type_group);
        medicine_chinese = view.findViewById(R.id.medicine_chinese);
        medicine_antipyretic = view.findViewById(R.id.medicine_antipyretic);
        medicine_vessel = view.findViewById(R.id.medicine_vessel);
        medicine_respiratory = view.findViewById(R.id.medicine_respiratory);
        medicine_anticold = view.findViewById(R.id.medicine_anticold);
        medicine_children = view.findViewById(R.id.medicine_children);
        medicine_skin = view.findViewById(R.id.medicine_skin);
        medicine_stomach = view.findViewById(R.id.medicine_stomach);
        medicine_nutrition = view.findViewById(R.id.medicine_nutrition);
        medicine_instrument = view.findViewById(R.id.medicine_instrument);

        initListener();
        return view;
    }

    public void initListener() {
        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //判断当前被选中的按钮
                switch (checkedId) {
                    case R.id.medicine_chinese:
                        type = 0;
                        break;
                    case R.id.medicine_antipyretic:
                        type = 1;
                        break;
                    case R.id.medicine_vessel:
                        type = 2;
                        break;
                    case R.id.medicine_respiratory:
                        type = 3;
                        break;
                    case R.id.medicine_anticold:
                        type = 4;
                        break;
                    case R.id.medicine_children:
                        type = 5;
                        break;
                    case R.id.medicine_skin:
                        type = 6;
                        break;
                    case R.id.medicine_stomach:
                        type = 7;
                        break;
                    case R.id.medicine_nutrition:
                        type = 8;
                        break;
                    case R.id.medicine_instrument:
                        type = 9;
                        break;
                }
                getDate();
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initData() {
        super.initData();
        Log.i(TAG, "类别数据初始化");
        Thread initThread = new Thread(() -> {
            List<Medicine> medicines = new ArrayList<>();
            Log.i(TAG, "进入获取商品线程");
            String responseJson = null;
            try {
                //发送获取商品请求
                String url = PropertiesUtils.getUrl(mContext);
                //提交查找请求
                responseJson = OkhttpUtils.doGet(url + "/medicines/type/" + type.toString());
                Result<List<Medicine>> medicineResponse = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {
                });
                medicines = medicineResponse.getData();
                Log.i(TAG, "获取商品响应解析对象:");

                if (current.getName().equals(Thread.currentThread().getName())) {
                    firstFresh(medicines);

                    for (Medicine medicine : medicines) {
                        try {
                            OkhttpUtils.saveImage(url + "/medicines/MedicinePicture/" + medicine.getCommodityID(), medicine.getCommodityID().toString(), mContext);
                        } catch (IOException e) {
                            Log.i(TAG, e.toString());
                        }
                    }
                }
                if (current.getName().equals(Thread.currentThread().getName())) {
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

    public void getDate() {
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
                freshInMain(medicines);
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
        current = dataThread;
        dataThread.start();
    }

    public void firstFresh(List<Medicine> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //创建类别循环视图适配器,加载数据
                adapter = new TypeAdapter(list, mContext);
                //循环视图加载适配器
                rv_type.setAdapter(adapter);
                //创建网格布局
                GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                //循环视图加载网格布局
                rv_type.setLayoutManager(manager);
            }
        });
    }

    public void freshInMain(List<Medicine> list) {
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

    /**
     * 刷新数据
     */
    @Override
    public void refreshData() {
        getDate();
    }

    @Override
    public void saveData() {
    }

}
