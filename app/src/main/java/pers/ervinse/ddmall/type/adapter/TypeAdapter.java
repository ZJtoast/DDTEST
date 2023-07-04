package pers.ervinse.ddmall.type.adapter;

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

import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.MedicineInfoActivity;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

/**
 * 全部商品数据适配器
 */
public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder> {

    private static final String TAG = TypeAdapter.class.getSimpleName();

    //商品数据
    private List<Medicine> goodsList;
    //上下文
    private Context mContext;

    /**
     * 创建适配器时传入要加载的商品数据和上下文
     */
    public TypeAdapter(List<Medicine> goodsList, Context context) {
        this.goodsList = goodsList;
        this.mContext = context;
    }

    /**
     * 供外部使用,更新goodsList数据
     *
     * @param goodsList
     */
    public void setGoodsList(List<Medicine> goodsList) {
        this.goodsList = goodsList;
    }

    /**
     * 刷新所有数据
     */
    public void flushView() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.item_type, null);
        return new TypeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {

        holder.item_name_tv.setText(goodsList.get(position).getName());
        holder.item_description_tv.setText(goodsList.get(position).getDescription());
        holder.item_price_tv.setText("￥" + goodsList.get(position).getPrice());
        //通过图片名字获取图片资源的id
        int id = mContext.getResources().getIdentifier(goodsList.get(position).getImage(), "drawable", mContext.getPackageName());
        holder.item_image.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    public class TypeViewHolder extends RecyclerView.ViewHolder {

        private TextView item_name_tv, item_description_tv, item_price_tv;
        private ImageView item_image;
        private Button item_add_cart_btn;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);

            item_name_tv = itemView.findViewById(R.id.item_name);
            item_description_tv = itemView.findViewById(R.id.item_description);
            item_price_tv = itemView.findViewById(R.id.item_price);
            item_image = itemView.findViewById(R.id.item_image);
            item_add_cart_btn = itemView.findViewById(R.id.item_add_cart_btn);

            /**
             * item点击事件监听
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //前往商品详情页面,并传入数据
                    Medicine goodsByClick = goodsList.get(getLayoutPosition());
                    Intent intent = new Intent(mContext, MedicineInfoActivity.class);
                    intent.putExtra("goods", goodsByClick);
                    mContext.startActivity(intent);
                }
            });

            /**
             * 添加到购物车按钮监听事件
             */
            item_add_cart_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Thread() {
                        @Override
                        public void run() {
                            Log.i(TAG, "进入添加购物车商品线程");

                            Gson gson = new Gson();
                            String responseJson = null;

                            //获取当前商品名
                            Medicine goodsByClick = goodsList.get(getLayoutPosition());
                            Medicine goodsForAdd = new Medicine();
                            goodsForAdd.setName(goodsByClick.getName());
                            String goodsJson = gson.toJson(goodsForAdd);
                            try {
                                //发送添加购购物车请求
                                String url = PropertiesUtils.getUrl(mContext);
                                responseJson = OkhttpUtils.doPost(url + "/cart/addGoodsToCart", goodsJson);
                                Log.i(TAG, "添加购物车商品响应json:" + responseJson);
                                responseJson = gson.fromJson(responseJson, String.class);
                                Log.i(TAG, "添加购物车商品响应解析对象:" + responseJson);

                                if (responseJson != null) {
                                    //添加成功
                                    if (responseJson.equals("true")) {
                                        Looper.prepare();
                                        Toast.makeText(mContext, "商品已添加到购物车", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                        //添加失败,商品已经在购物车中
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
    }
}