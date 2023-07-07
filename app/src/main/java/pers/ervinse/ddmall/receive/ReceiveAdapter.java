package pers.ervinse.ddmall.receive;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Order;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ReceiveAdapterViewHolder> {

    private static final String TAG = ReceiveAdapter.class.getSimpleName();
    private volatile List<Order> orderList;
    private Handler handler = new Handler();
    private Context mContext;

    public ReceiveAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.mContext = context;
    }

    public void setReceiveList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public void flushView() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReceiveAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.item_receive, null);
        return new ReceiveAdapter.ReceiveAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiveAdapterViewHolder holder, int position) {
        Thread getMedicine = new Thread(() -> {
            Log.i(TAG, "获取商品详细线程");
            String responseJson = null;
            Medicine medicine = null;
            //发送获取商品请求
            String url = PropertiesUtils.getUrl(mContext);
            try {
                responseJson = OkhttpUtils.doGet(url + "/medicines/MedicineInfo/" + orderList.get(position).getCommodityID().toString());

                Log.i(TAG, "获取详细商品响应json:" + responseJson);
                Result<Medicine> medicineResponse = JSONObject.parseObject(responseJson, new TypeReference<Result<Medicine>>() {
                });
                Log.i(TAG, "获取详细响应解析对象:" + medicineResponse);
                //获取商品成功
                medicine = medicineResponse.getData();
            } catch (IOException e) {
                Log.i("TAG", "信息传输错误");
            } catch (NullPointerException e) {
                Log.i("TAG", "未获取到商品");
            }
            if (medicine != null) {
                holder.drug_name.setText(medicine.getCommodityName());
            }
        });
        getMedicine.start();

        holder.merchant.setText("东东快药自营店");
        //通过图片名字获取图片资源的id
        try {
            OkhttpUtils.setImage(holder.medicine_image_receive, orderList.get(position).getCommodityID().toString(), mContext);
        } catch (IOException e) {
            Log.i("TAG", "图片传输错误");
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ReceiveAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView merchant, drug_name;
        private Button receive_drug_button;
        private ImageView medicine_image_receive;

        public ReceiveAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            merchant = itemView.findViewById(R.id.merchant);
            drug_name = itemView.findViewById(R.id.drug_name);
            receive_drug_button = itemView.findViewById(R.id.receive_drug_button);
            medicine_image_receive = itemView.findViewById(R.id.medicine_image_receive);
            //给收货按钮添加事件
            receive_drug_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                            .setTitle("药品收货")
                            .setMessage("是否收货?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            Log.i(TAG, "进入收货商品数量线程");
                                            String responseJson = null;
                                            try {
                                                //发送删除请求
                                                String url = PropertiesUtils.getUrl(mContext);
                                                String ID = orderList.get(getLayoutPosition()).getOrderID().toString();
                                                responseJson = OkhttpUtils.doPutByToken(url + "/order/confirm/" + ID, "", TokenContextUtils.getToken());
                                                Log.i(TAG, "响应json:" + responseJson);
                                                Result<String> result = JSONObject.parseObject(responseJson, new TypeReference<Result<String>>() {
                                                });
                                                Log.i(TAG, "收货商品响应解析对象:" + responseJson);
                                                Integer code = result.getCode();
                                                //删除成功
                                                if (responseJson != null) {
                                                    if (code.equals(200)) {
                                                        //切回主线程刷新视图
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                //全局刷新
                                                                orderList.remove(getLayoutPosition());
                                                                notifyDataSetChanged();
                                                                Toast.makeText(mContext, "成功收货", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
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
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    //创建删除对话框并显示
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });


        }
    }
}


