package pers.ervinse.ddmall.shoppingcart.fragment;

import static pers.ervinse.ddmall.R.id.cart_settle_btn;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import pers.ervinse.ddmall.BaseFragment;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.shoppingcart.adapter.ShoppingCartAdapter;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

/**
 * 购物车碎片
 */
public class ShoppingCartFragment extends BaseFragment {

    private static final String TAG = ShoppingCartFragment.class.getSimpleName();
    RecyclerView cart_item_rv;
    List<Medicine> medicineList;
    ShoppingCartAdapter adapter;
    //线程处理器
    private Handler handler = new Handler();
    //总价
    private TextView cart_total_tv, cart_edit_tv;
    //全选,全删
    private CheckBox cart_check_all_checkbox, cart_delete_all_checkbox;
    private Button cart_settle_btn;

    /**
     * 初始化视图
     *
     * @return
     */
    @Override
    public View initView() {
        Log.i(TAG, "购物车视图被初始化了");

        View view = View.inflate(mContext, R.layout.fragment_shopping_cart, null);//加载购物车碎片的布局
        cart_total_tv = view.findViewById(R.id.cart_total_tv);//总价
        cart_check_all_checkbox = view.findViewById(R.id.cart_check_all_checkbox);//全选
        cart_delete_all_checkbox = view.findViewById(R.id.cart_delete_all_checkbox);//全不选
        View viewById = view.findViewById(R.id.cart_item_price_tv);//商品价格
        cart_item_rv = view.findViewById(R.id.cart_item_rv);//待添入商品的列表
        cart_edit_tv = view.findViewById(R.id.cart_edit_tv);
        cart_settle_btn = view.findViewById(R.id.cart_settle_btn);//结算按钮
        cart_edit_tv.setOnClickListener(v -> {
            saveData();
        });
        //给结算按钮绑定事件
        cart_settle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                Double price = adapter.getTotalPrice() / 100.0;
                String totalPrice = decimalFormat.format(price);
                if (!totalPrice.equals(".00")) {
                    // 检查totalPrice是否不等于".00"，即判断是否有有效的商品总价
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            // 创建一个AlertDialog.Builder对象，并传入当前的上下文（mContext）
                            .setTitle("提交订单")
                            // 设置对话框的标题为"完成结算"
                            .setMessage("商品总计:" + totalPrice + "元,是否提交订单?")
                            // 设置对话框的消息内容，包括商品总计价格（totalPrice）
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 设置对话框的确定按钮，当用户点击确定时触发此处的代码   这里是结算的代码
                                    Toast.makeText(mContext, "完成商品购买,总计价格为:" + totalPrice + "元", Toast.LENGTH_SHORT).show();
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

        return view;
    }

    /**
     * 初始化数据
     */
    public void initData() {
        super.initData();
        Log.i(TAG, "购物车数据被初始化了");
        if (TokenContextUtils.getToken().equals("null")) {
            Log.i(TAG, "查看用户是否登录,Token=" + TokenContextUtils.getToken());
            medicineList = new ArrayList<>();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //创建首页循环视图适配器,加载数据
                    if (adapter == null) {
                        adapter = new ShoppingCartAdapter(mContext, medicineList, cart_total_tv, cart_check_all_checkbox, cart_delete_all_checkbox);
                        //循环视图加载适配器
                        cart_item_rv.setAdapter(adapter);
                        //创建网格布局
                        GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                        //循环视图加载网格布局
                        cart_item_rv.setLayoutManager(manager);
                    }
                    adapter.setmedicineList(medicineList);
                    adapter.flushView();
                }
            });
        }
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "进入获取购物车商品线程");
                String responseJson = null;
                try {
                    //发送登录请求
                    String url = PropertiesUtils.getUrl(mContext);

                    responseJson = OkhttpUtils.doGetByToken(url + "/shoppingCart/list", TokenContextUtils.getToken());

                    Log.i(TAG, "获取购物车商品响应json:" + responseJson);
                    Result<List<Medicine>> result = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {
                    });
                    Log.i(TAG, "获取购物车商品响应解析对象:" + medicineList);
                    medicineList = result.getData();
                    //获取数据成功,加创建商品布局
                    if (medicineList != null) {
                        //切回主线程加载视图
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //创建首页循环视图适配器,加载数据
                                adapter = new ShoppingCartAdapter(mContext, medicineList, cart_total_tv, cart_check_all_checkbox, cart_delete_all_checkbox);
                                //循环视图加载适配器
                                cart_item_rv.setAdapter(adapter);
                                //创建网格布局
                                GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                                //循环视图加载网格布局
                                cart_item_rv.setLayoutManager(manager);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "未登录", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }.start();

    }


    /**
     * 刷新数据
     */
    @Override
    public void refreshData() {

        Log.i(TAG, "联网刷新数据");
        if (TokenContextUtils.getToken().equals("null")) {
            Log.i(TAG, "查看用户是否登录,Token=" + TokenContextUtils.getToken());
            medicineList = new ArrayList<>();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //创建首页循环视图适配器,加载数据
                    if (adapter == null) {
                        adapter = new ShoppingCartAdapter(mContext, medicineList, cart_total_tv, cart_check_all_checkbox, cart_delete_all_checkbox);
                        //循环视图加载适配器
                        cart_item_rv.setAdapter(adapter);
                        //创建网格布局
                        GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                        //循环视图加载网格布局
                        cart_item_rv.setLayoutManager(manager);
                    }
                    adapter.setmedicineList(medicineList);
                    adapter.flushView();
                }
            });
        }
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "进入获取购物车商品线程");
                String responseJson = null;
                try {
                    //发送登录请求
                    String url = PropertiesUtils.getUrl(mContext);

                    responseJson = OkhttpUtils.doGetByToken(url + "/shoppingCart/list", TokenContextUtils.getToken());

                    Log.i(TAG, "获取购物车商品响应json:" + responseJson);
                    Result<List<Medicine>> result = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {
                    });
                    medicineList = result.getData();
                    Log.i(TAG, "获取购物车商品响应解析对象:" + medicineList);

                    //数据获取成功,加创建商品布局
                    if (medicineList != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter == null) {
                                    adapter = new ShoppingCartAdapter(mContext, medicineList, cart_total_tv, cart_check_all_checkbox, cart_delete_all_checkbox);
                                    //循环视图加载适配器
                                    cart_item_rv.setAdapter(adapter);
                                    //创建网格布局
                                    GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                                    //循环视图加载网格布局
                                    cart_item_rv.setLayoutManager(manager);
                                }
                                adapter.setmedicineList(medicineList);
                                adapter.flushView();
                            }
                        });
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

    /**
     * 将当前商品数据保存到服务器中
     */
    @Override
    public void saveData() {
        Log.i(TAG, "保存数据");
        if (TokenContextUtils.getToken().equals("null")) {
            Log.i(TAG, "查看用户是否登录,Token=" + TokenContextUtils.getToken());
            medicineList = new ArrayList<>();
        }
        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "进入保存购物车数据线程");
                String responseJson = null;
                try {
                    //发送获取购物车商品请求
                    String url = PropertiesUtils.getUrl(mContext);
                    if (medicineList != null)
                        for (Medicine medicine : medicineList) {
                            String medicineJson = JSONObject.toJSONString(medicine);
                            responseJson = OkhttpUtils.doPutByToken(url + "/shoppingCart", medicineJson, TokenContextUtils.getToken());

                            Log.i(TAG, "获取保存购物车商品响应json:" + responseJson);
                            Result<List<Medicine>> result = JSONObject.parseObject(responseJson, new TypeReference<Result<List<Medicine>>>() {
                            });
                            Log.i(TAG, "获取购物车商品响应解析对象:" + medicineList);
                            if (!result.getCode().equals(200)) {
                                Log.i(TAG, "保存数据成功");
                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }.start();
    }
}
