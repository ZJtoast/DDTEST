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

import java.io.IOException;
import java.util.List;

import pers.ervinse.ddmall.MedicineInfoActivity;
import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.domain.Comment;
import pers.ervinse.ddmall.domain.Medicine;
import pers.ervinse.ddmall.utils.OkhttpUtils;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentAdapterViewHolder> {

    private static final String TAG = CommentAdapter.class.getSimpleName();
    private List<Comment> commentList;
    private Context mContext;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.mContext = context;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
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
        holder.merchant.setText(commentList.get(position).getMerchantName());
        holder.drug_name.setText(commentList.get(position).getDrugName());
        //通过图片名字获取图片资源的id
        try {
            OkhttpUtils.setImage(holder.merchant_image, commentList.get(position).getImageNumber().toString(), mContext);
        } catch (IOException e) {
            Log.i("TAG", "图片传输错误");
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView merchant, drug_name;
        private Button comment_drug_button;
        private ImageView merchant_image;

        public CommentAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            merchant = itemView.findViewById(R.id.merchant);
            drug_name = itemView.findViewById(R.id.drug_name);
            comment_drug_button = itemView.findViewById(R.id.comment_drug_button);
            merchant_image = itemView.findViewById(R.id.merchant_image);

            //给评价按键绑定跳转事件，跳转到评价页面
            comment_drug_button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Comment commentByClick = commentList.get(getLayoutPosition());
                    Intent intent = new Intent(mContext,CommentInfoActivity.class);
                    intent.putExtra("comment",commentByClick);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}


