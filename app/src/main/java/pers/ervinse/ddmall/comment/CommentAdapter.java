package pers.ervinse.ddmall.comment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Comment;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.domain.Order;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentAdapterViewHolder> {

    private static final String TAG = CommentAdapter.class.getSimpleName();
    private List<Order> orderList;
    private Context mContext;

    public CommentAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.mContext = context;
    }

    public void setCommentList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public void flushView() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.item_medicine, null);
        return new CommentAdapter.CommentAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapterViewHolder holder, int position) {
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
            holder.drug_name.setText(medicine.getCommodityName());
        });
        getMedicine.start();

        holder.merchant.setText("东东快药自营店");
        //通过图片名字获取图片资源的id
        try {
            OkhttpUtils.setImage(holder.medicine_image_comment, orderList.get(position).getCommodityID().toString(), mContext);
        } catch (IOException e) {
            Log.i("TAG", "图片传输错误");
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class CommentAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView merchant, drug_name;
        private Button comment_drug_button;
        private ImageView medicine_image_comment;

        public CommentAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            merchant = itemView.findViewById(R.id.merchant);
            drug_name = itemView.findViewById(R.id.drug_name);
            comment_drug_button = itemView.findViewById(R.id.comment_drug_button);
            medicine_image_comment = itemView.findViewById(R.id.medicine_image_comment);
            //给评价按键绑定跳转事件，跳转到评价页面
            comment_drug_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Order commentByClick = orderList.get(getLayoutPosition());
                    Intent intent = new Intent(mContext, CommentInfoActivity.class);
                    intent.putExtra("comment", commentByClick);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}


