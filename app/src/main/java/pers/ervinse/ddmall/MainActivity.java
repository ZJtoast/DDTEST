package pers.ervinse.ddmall;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import java.util.ArrayList;

import pers.ervinse.ddmall.R;
import pers.ervinse.ddmall.user.fragment.UserFragment;
import pers.ervinse.ddmall.utils.PropertiesUtils;

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();;
    //碎片索引号
    private int fragmentIndex;

    //切换碎片时保存的临时碎片
    private BaseFragment tempFragemnt;


    //底部按钮组
    private RadioGroup bottom_btn_group;



    ArrayList<BaseFragment> fragmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //加载底部按钮组
        bottom_btn_group = findViewById(R.id.bottom_btn_group);
        //设置底部按钮中的'首页'在打开应用时被选中

        //初始化碎片
        initFragment();

        //初始化监听器
        initListener();

        //获取首页fragment,加载布局和数据
        BaseFragment homeFragment = fragmentList.get(0);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.frameLayout, homeFragment).commit();
        tempFragemnt = homeFragment;
        Log.i(TAG, "onCreate: 初始化完成");

        //获取服务器地址
        String url = PropertiesUtils.getUrl(this);
        Log.i(TAG, "服务器地址为: " + url);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 初始化碎片
     * 创建碎片对象,并添加到碎片集合中
     */
    private void initFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new UserFragment());
    }

    /**
     * 初始化监听器,并设置底部按钮中的'首页'在打开应用时被选中
     */
    private void initListener() {
        //为底部按钮组设置监听器
        bottom_btn_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //判断当前被选中的按钮
                switch (checkedId) {
                    case R.id.user_bottom_btn:
                        fragmentIndex = 0;
                        break;
                }
                //根据按钮索引,获取按钮对象
                BaseFragment baseFragment = getFragment(fragmentIndex);
                //切换碎片
                switchFragment(tempFragemnt, baseFragment);
            }
        });




    }

    /**
     * 根据按钮索引,获取按钮对象
     * @param index 碎片索引号
     * @return
     */
    private BaseFragment getFragment(int index) {
        if (fragmentList != null && fragmentList.size() > 0) {
            BaseFragment baseFragment = fragmentList.get(index);
            return baseFragment;
        }
        return null;
    }


    /**
     * 切换上次显示的Fragment
     * @param fromFragment 上次显示的Fragment
     * @param nextFragment 当前正要显示的Fragment
     */
    private void switchFragment(BaseFragment fromFragment, BaseFragment nextFragment) {
        if (tempFragemnt != nextFragment) {
            tempFragemnt = nextFragment;
            if (nextFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment是否添加
                if (!nextFragment.isAdded()) {
                    //隐藏当前Fragment
                    if (fromFragment != null) {
                        fromFragment.saveData();
                        transaction.hide(fromFragment);
                    }
                    transaction.add(R.id.frameLayout, nextFragment).commit();
                } else {
                    //隐藏当前Fragment
                    if (fromFragment != null) {
                        fromFragment.saveData();
                        transaction.hide(fromFragment);
                    }
                    //切换页面时联网刷新数据
                    nextFragment.refreshData();
                    transaction.show(nextFragment).commit();
                }
            }
        }
    }
}