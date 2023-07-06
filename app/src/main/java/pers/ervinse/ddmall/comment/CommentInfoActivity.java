package pers.ervinse.ddmall.comment;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pers.ervinse.ddmall.R;

public class CommentInfoActivity extends AppCompatActivity {

    private Context mContext;
    private ListView drug_lv;

    private ImageButton comment_info_back_btn;

    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        comment_info_back_btn = findViewById(R.id.comment_info_back_btn);
        comment_info_back_btn.setOnClickListener(v -> {
            finish();
        });
        mContext = this;
//        List<Map<String, Object>> list = new ArrayList<>();
//        for (int i = 0; i < imgs.length; i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("icon", imgs[i]);
//            map.put("name", tits[i]);
//            map.put("text", strs[i]);
//            list.add(map);
//        }

    }


}