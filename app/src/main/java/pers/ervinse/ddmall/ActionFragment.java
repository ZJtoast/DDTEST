package pers.ervinse.ddmall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ActionFragment extends BaseFragment {

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.activity_action, null);
        return view;
    }
    public void initData() {
        super.initData();

    }
    @Override
    public void refreshData() {

    }

    @Override
    public void saveData() {

    }
}