package pers.ervinse.ddmall.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.RegisterActivity;
import pers.ervinse.ddmall.domain.Comment;
import pers.ervinse.ddmall.domain.Order;
import pers.ervinse.ddmall.domain.Result;
import pers.ervinse.ddmall.utils.OkhttpUtils;
import pers.ervinse.ddmall.utils.PropertiesUtils;
import pers.ervinse.ddmall.utils.TokenContextUtils;

public class CommentInfoActivity extends AppCompatActivity {

    private Context mContext;
    private TextView tv_hint;
    private EditText et_comment;
    private RatingBar rb_score;
    private Button btn_commit;
    private Order order;
    private ImageButton comment_info_back_btn;

    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mContext = this;
        order = (Order) getIntent().getSerializableExtra("comment");
        comment_info_back_btn = findViewById(R.id.comment_info_back_btn);
        tv_hint = findViewById(R.id.tv_hint);
        et_comment = findViewById(R.id.et_comment);
        rb_score = findViewById(R.id.rb_score);
        btn_commit = findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(v -> {
            String url = PropertiesUtils.getUrl(mContext);
            String responseJson = null;
            Comment comment = new Comment();
            comment.setCommodityID(order.getCommodityID());
            comment.setReviewText(et_comment.getText().toString());
            responseJson = JSONObject.toJSONString(comment);
            try {
                responseJson = OkhttpUtils.doPostByToken(url + "/order/review", responseJson, TokenContextUtils.getToken());
                Result<String> result = JSONObject.parseObject(responseJson, new TypeReference<Result<String>>() {
                });
                if (result.getCode().equals(200)) {
                    Looper.prepare();
                    Toast.makeText(mContext, "评价提交成功！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toast.makeText(mContext, "评价提交失败！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (IOException e) {
                Log.i("发送评论错误", "评价商品异常");
            }
        });


        comment_info_back_btn.setOnClickListener(v -> {
            finish();
        });
        mContext = this;


    }


}