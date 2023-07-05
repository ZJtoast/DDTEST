package pers.ervinse.ddmall.home.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.ddmall.BaseFragment;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.home.adapter.HomeAdapter;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

/**
 * 首页碎片
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private List<Medicine> medicineList;
    private Handler handler = new Handler();
    private RecyclerView rvHome;

    private ImageView ib_top;
    private TextView tv_search_home;
    private TextView tv_message_home;
    private HomeAdapter adapter;
    private RadioGroup fourType;

    private Thread current;
    private List<Integer> typelist;
    private RadioButton householdEssentials, specializedMedication, nutritionalSupplements, medicalEquipment;


    /**
     * 初始化视图
     *
     * @return
     */
    @Override
    public View initView() {
        Log.i(TAG, "主页视图初始化");
        //为当前fragment加载布局文件
        View view = View.inflate(mContext, R.layout.fragment_home, null);
        rvHome = view.findViewById(R.id.rv_home);
        tv_search_home = view.findViewById(R.id.tv_search_home);
        tv_message_home = view.findViewById(R.id.tv_message_home);

        //首页分类框
        fourType = view.findViewById(R.id.category_btn_group);
        householdEssentials = view.findViewById(R.id.category_house_btn);
        specializedMedication = view.findViewById(R.id.category_spec_btn);
        nutritionalSupplements = view.findViewById(R.id.category_nutri_btn);
        medicalEquipment = view.findViewById(R.id.category_equip_btn);
        initListener();
        return view;
    }

    /**
     * 初始化监听器
     */
    public void initListener() {
        fourType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                typelist = new ArrayList<>();
                //判断当前被选中的按钮
                switch (checkedId) {
                    case R.id.category_house_btn:
                        typelist.add(1);
                        typelist.add(5);
                        typelist.add(6);
                        break;
                    case R.id.category_spec_btn:
                        typelist.add(2);
                        typelist.add(4);
                        typelist.add(6);
                        break;
                    case R.id.category_nutri_btn:
                        typelist.add(8);
                        break;
                    case R.id.category_equip_btn:
                        typelist.add(9);
                        break;
                }
                getDate();
            }
        });
    }

    public void getDate() {
        Log.i(TAG, "联网刷新数据");
        Thread dataThread = new Thread(() -> {
            @SuppressLint("NotifyDataSetChanged")
            List<Medicine> medicines = new ArrayList<>();
            String responseJson = null;
            for (Integer type : typelist) {
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
                    medicines.addAll(medicineResponse.getData());
                    Log.i(TAG, "获取商品响应json:" + responseJson);

                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }
            }
            if (current.equals(this)) {
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
            }
            medicineList = medicines;
        });
        current = dataThread;
        dataThread.start();
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

    public void firstFresh(List<Medicine> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                //创建首页循环视图适配器,加载数据
                adapter = new HomeAdapter(list, mContext);
                //循环视图加载适配器
                rvHome.setAdapter(adapter);
                //创建网格布局
                GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                //循环视图加载网格布局
                rvHome.setLayoutManager(manager);
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initData() {
        super.initData();
        Log.i(TAG, "主页数据初始化");
        Thread initThread = new Thread(() -> {
            List<Medicine> medicines = new ArrayList<>();
            Log.i(TAG, "进入获取商品线程");
            String responseJson = null;
            try {
                //发送获取商品请求
                String url = PropertiesUtils.getUrl(mContext);
                responseJson = OkhttpUtils.doGet(url + "/medicines/hotMedicine");
                Log.i(TAG, "获取热点商品响应json:" + responseJson);
                Result<List<Medicine>> medicineResponse = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {
                });
                Log.i(TAG, "获取热点响应解析对象:" + medicineResponse);
                //获取商品成功
                medicines = medicineResponse.getData();

                //接下来接收药品图片
                Log.i(TAG, "获取商品图片解析对象:" + medicines.getClass());

                if (medicines != null && current.equals(this)) {
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

    /**
     * 刷新数据
     */
    @Override
    public void refreshData() {
        if (typelist == null) {
            typelist = new ArrayList<>();
            typelist.add(2);
            typelist.add(1);
            typelist.add(3);
            typelist.add(4);
        }
        getDate();
    }

    @Override
    public void saveData() {
    }
}
