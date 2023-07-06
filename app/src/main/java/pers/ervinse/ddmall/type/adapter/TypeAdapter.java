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
    private List<Medicine> medicineList;
    //上下文
    private Context mContext;

    /**
     * 创建适配器时传入要加载的商品数据和上下文
     */
    public TypeAdapter(List<Medicine> medicineList, Context context) {
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

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.item_type, null);
        return new TypeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {

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

    @Override
    public int getItemCount() {
        return medicineList.size();
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
            item_add_cart_btn = itemView.findViewById(R.id.item_desc_btn);

            /**
             * item点击事件监听
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //前往商品详情页面,并传入数据
                    Medicine medicineByClick = medicineList.get(getLayoutPosition());
                    Intent intent = new Intent(mContext, MedicineInfoActivity.class);
                    intent.putExtra("medicine", medicineByClick);
                    mContext.startActivity(intent);
                }
            });

            /**
             * 添加到购物车按钮监听事件
             */
            item_add_cart_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Medicine medicineByClick = medicineList.get(getLayoutPosition());
                    Intent intent = new Intent(mContext, MedicineInfoActivity.class);
                    intent.putExtra("medicine", medicineByClick);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
