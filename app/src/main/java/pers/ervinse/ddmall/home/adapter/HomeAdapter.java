package pers.ervinse.ddmall.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import pers.ervinse.ddmall.MedicineInfoActivity;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

/**
 * 首页循环视图适配器
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private static final String TAG = HomeAdapter.class.getSimpleName();

    //商品数据
    private List<Medicine> medicineList;
    //上下文
    private Context mContext;


    /**
     * 创建适配器时传入要加载的商品数据和上下文
     */
    public HomeAdapter(List<Medicine> medicineList, Context context) {
        this.medicineList = medicineList;
        this.mContext = context;
    }

    /**
     * 供外部使用,更新medicineList数据
     *
     * @param medicineList
     */
    public void setmedicineList(List<Medicine> medicineList) {
        this.medicineList = medicineList;
    }

    /**
     * 刷新所有数据
     */
    public void flushView() {
        notifyDataSetChanged();
    }

    /**
     * 创建ViewHolder时调用此方法,加载每一项子数据的布局文件
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.home_recycler_item, null);
        return new HomeViewHolder(itemView);
    }

    /**
     * 将ViewHolder中的组件和数据绑定
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        holder.item_name_tv.setText(medicineList.get(position).getCommodityName());
        holder.item_description_tv.setText(medicineList.get(position).getCommodityDesc());
        holder.item_price_tv.setText("￥" + medicineList.get(position).getCommodityPrice());
        //通过图片名字获取图片资源的id
        try {
            OkhttpUtils.setImage(holder.item_image, medicineList.get(position).getCommodityID().toString(), mContext);
        } catch (IOException e) {
            Log.i("TAG", "图片传输错误");
        }

    }

    /**
     * 获取数据的条数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    /**
     * 内部类,加载子项布局
     */
    public class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView item_name_tv, item_description_tv, item_price_tv;
        private ImageView item_image;
        private Button buy_button;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name_tv = itemView.findViewById(R.id.item_name);
            item_description_tv = itemView.findViewById(R.id.item_description);
            item_price_tv = itemView.findViewById(R.id.item_price);
            item_image = itemView.findViewById(R.id.item_image);
            buy_button = itemView.findViewById(R.id.buy_button);

            /**
             * item监听事件
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //加载数据
                    Medicine medicineByClick = medicineList.get(getLayoutPosition());
                    Intent intent = new Intent(mContext, MedicineInfoActivity.class);
                    intent.putExtra("medicine", medicineByClick);
                    mContext.startActivity(intent);
                }
            });

            /**
             * 购买按钮监听事件
             */
            buy_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread() {
                        @Override
                        public void run() {

                            Medicine medicineByClick = medicineList.get(getLayoutPosition());
                            Intent intent = new Intent(mContext, MedicineInfoActivity.class);
                            intent.putExtra("medicine", medicineByClick);
                            mContext.startActivity(intent);
//                            Log.i(TAG, "进入添加购物车线程");
//
//
//                            Gson gson = new Gson();
//                            String responseJson = null;
//
//                            //获取当前点击的item位置,并获取对应的商品数据
//                            Medicine medicineByClick = medicineList.get(getLayoutPosition());
//                            Medicine medicineForAdd = new Medicine();
//                            medicineForAdd.setCommodityName(medicineByClick.getCommodityName());
//                            String medicineJson = gson.toJson(medicineForAdd);
//                            try {
//                                //发送添加购物车请求
//                                String url = PropertiesUtils.getUrl(mContext);
//                                responseJson = OkhttpUtils.doPost(url + "/cart/addmedicineToCart", medicineJson);
//
//                                Log.i(TAG, "添加购物车商品响应json:" + responseJson);
//                                responseJson = gson.fromJson(responseJson, String.class);
//                                Log.i(TAG, "添加购物车商品响应解析对象:" + responseJson);
//
//                                if (responseJson != null) {
//                                    //添加成功
//                                    if (responseJson.equals("true")) {
//                                        Looper.prepare();
//                                        Toast.makeText(mContext, "商品已添加到购物车", Toast.LENGTH_SHORT).show();
//                                        Looper.loop();
//                                        //添加失败,已经添加到购物车
//                                    } else {
//                                        Looper.prepare();
//                                        Toast.makeText(mContext, "商品已经在购物车啦", Toast.LENGTH_SHORT).show();
//                                        Looper.loop();
//                                    }
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Looper.prepare();
//                                Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
//                                Looper.loop();
//                            }

                        }
                    }.start();
                }
            });
        }
    }
}
