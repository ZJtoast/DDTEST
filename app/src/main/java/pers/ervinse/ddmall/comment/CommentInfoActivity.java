package pers.ervinse.ddmall.comment;

import android.content.Context;
import android.os.Bundle;
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

    protected void onRestart() {
        super.onRestart();
        setAdapter();
    }

    public List<? extends Map<String, ?>> getlist() {
        ArrayList<HashMap<String, Object>> address = new ArrayList<>();
        //   String responseJson = null;
        //  try {
        //      responseJson = OkhttpUtils.doGet(url + "/暂定");
        //  } catch (IOException e) {
        //      Log.i(TAG, "请求药品评论错误");
        //  }
        // Log.i(TAG, "登录请求响应json:" + responseJson);
        //  UserResponse<Boolean> response = gson.fromJson(responseJson, UserResponse.class);
        //  Log.i(TAG, "登录请求响应解析数据:" + responseJson);
        return address;
    }

    public void setAdapter() {
        SimpleAdapter comment = new SimpleAdapter(
                mContext, getlist(), R.layout.item_medicine,
                new String[]{"商家", "商品名称", "商品图片"},
                new int[]{R.id.merchant_image, R.id.drug_image, R.id.comment_drug_button});
        ListView sv = findViewById(R.id.drug_lv);
        sv.setAdapter(comment);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
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