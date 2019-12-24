package com.pxy.android.CPRAuxiliary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.pxy.android.CPRAuxiliary.Fragment.HomeFragment;
import com.pxy.android.CPRAuxiliary.Fragment.MineFragment;
import com.pxy.android.CPRAuxiliary.Fragment.SocialFragment;
import com.pxy.android.CPRAuxiliary.Fragment.MessageFragment;
import com.pxy.android.CPRAuxiliary.View.HeadImageView;


public class MainActivity extends AppCompatActivity {
    private final int selectedColor = 0xff11b7f3;
    private final int unSelectedColor = 0xff888888;
    private static final int TAB_INDEX_FIRST = 0;
    private static final int TAB_INDEX_SECOND = 1;
    private static final int TAB_INDEX_THIRD = 2;
    private static final int TAB_INDEX_FOURTH = 3;
    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
    private SocialFragment mSocialFragment;
    private MessageFragment mMessageFragment;
    private MineFragment mMineFragment;
    private Fragment lastFragment;
    private Animation animation;
    private HeadImageView iv_head;
    private HeadImageView iv_head_sex;

    @BindView(R.id.bottom_home)
    LinearLayout bottom_home;

    @OnClick(R.id.bottom_home)
    public void home() {
        setTabSelection(TAB_INDEX_FIRST);
        iv_home.startAnimation(animation);
    }

    @BindView(R.id.bottom_task)
    LinearLayout bottom_task;

    @OnClick(R.id.bottom_task)
    public void task() {
        setTabSelection(TAB_INDEX_SECOND);
        iv_task.startAnimation(animation);
    }

    @BindView(R.id.bottom_clinic)
    LinearLayout bottom_clinic;

    @OnClick(R.id.bottom_clinic)
    public void clinic() {
        setTabSelection(TAB_INDEX_THIRD);
        iv_clinic.startAnimation(animation);
    }

    @BindView(R.id.bottom_mine)
    LinearLayout bottom_mine;

    @OnClick(R.id.bottom_mine)
    public void mine() {
        setTabSelection(TAB_INDEX_FOURTH);
        iv_mine.startAnimation(animation);
    }

    @BindView(R.id.image_home)
    ImageView iv_home;
    @BindView(R.id.image_task)
    ImageView iv_task;
    @BindView(R.id.image_clinic)
    ImageView iv_clinic;
    @BindView(R.id.image_mine)
    ImageView iv_mine;

    @BindView(R.id.tv_icon_home)
    TextView tv_home;
    @BindView(R.id.tv_icon_task)
    TextView tv_task;
    @BindView(R.id.tv_icon_clinic)
    TextView tv_clinic;
    @BindView(R.id.tv_icon_mine)
    TextView tv_mine;


    @BindView(R.id.nv_main_navigation)
    NavigationView nv_main_navigation;

    @BindView(R.id.dl_main_drawer)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
        if(savedInstanceState != null){
            Log.d("instance", savedInstanceState.getString("data"));
        }
    }


    private void initView() {
        //初始化控件
        ButterKnife.bind(this);
        //初始化bar
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        //左上角开关
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        //选择用TextView充当标题
        ab.setDisplayShowTitleEnabled(false);
        if(nv_main_navigation != null){
            nv_main_navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //menuItem.setChecked(true);
                    String title = menuItem.getTitle().toString();
                    Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });
        }
        //圆形头像
        View headView = nv_main_navigation.getHeaderView(0);
            //从navigation的headView中获取实例
        iv_head = (HeadImageView)headView.findViewById(R.id.iv_head);
        iv_head_sex = (HeadImageView)headView.findViewById(R.id.iv_head_sex);
        try {
            iv_head.setImageDrawable(getResources().getDrawable(R.drawable.ic_user));
            iv_head_sex.setImageDrawable(getResources().getDrawable(R.drawable.ic_male));
            //iv_head.setImageResource(R.drawable.ic_user);
        }catch (Exception e){
            e.printStackTrace();
        }

        //为了好看，添加滑动监听，让主界面与Navigation一块滑动
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = mDrawerLayout.getChildAt(0);
                float scale = 1 - slideOffset;
                content.setTranslationX(drawerView.getMeasuredWidth()*(1-scale));
                super.onDrawerSlide(drawerView, slideOffset);
            }
        });
        //初始化动画，用于底部点击缩放效果
        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fragment_scale);
        mFragmentManager = getSupportFragmentManager();
        //default selected fragment
        tv_home.setTextColor(selectedColor);
        setTabSelection(TAB_INDEX_FIRST);

    }

    //切换fragment
    private void setTabSelection(int index) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (index) {
            case TAB_INDEX_FIRST:
                toolbarTitle.setText("首页");
                if (mHomeFragment == null){
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.fl_content, mHomeFragment,"first");
                }
                iv_home.setImageResource(R.mipmap.home_1);
                iv_task.setImageResource(R.mipmap.social_0);
                iv_clinic.setImageResource(R.mipmap.message_0);
                iv_mine.setImageResource(R.mipmap.mine_0);
                tv_home.setTextColor(selectedColor);
                tv_task.setTextColor(unSelectedColor);
                tv_clinic.setTextColor(unSelectedColor);
                tv_mine.setTextColor(unSelectedColor);

                transaction.show(mHomeFragment);
                lastFragment = mHomeFragment;

                break;
            case TAB_INDEX_SECOND:
                toolbarTitle.setText("社区");
                if (mSocialFragment == null){
                    mSocialFragment = new SocialFragment();
                    transaction.add(R.id.fl_content, mSocialFragment,"second");
                }
                iv_home.setImageResource(R.mipmap.home_0);
                iv_task.setImageResource(R.mipmap.social_1);
                iv_clinic.setImageResource(R.mipmap.message_0);
                iv_mine.setImageResource(R.mipmap.mine_0);
                tv_home.setTextColor(unSelectedColor);
                tv_task.setTextColor(selectedColor);
                tv_clinic.setTextColor(unSelectedColor);
                tv_mine.setTextColor(unSelectedColor);
                transaction.show(mSocialFragment);
                lastFragment = mSocialFragment;
                break;
            case TAB_INDEX_THIRD:
                toolbarTitle.setText("消息");
                if (mMessageFragment == null){
                    mMessageFragment = new MessageFragment();
                    transaction.add(R.id.fl_content, mMessageFragment,"third");
                }
                iv_home.setImageResource(R.mipmap.home_0);
                iv_task.setImageResource(R.mipmap.social_0);
                iv_clinic.setImageResource(R.mipmap.message_1);
                iv_mine.setImageResource(R.mipmap.mine_0);
                tv_home.setTextColor(unSelectedColor);
                tv_task.setTextColor(unSelectedColor);
                tv_clinic.setTextColor(selectedColor);
                tv_mine.setTextColor(unSelectedColor);
                transaction.show(mMessageFragment);
                lastFragment = mMessageFragment;
                break;
            case TAB_INDEX_FOURTH:
                toolbarTitle.setText("我的");
                if (mMineFragment == null){
                    mMineFragment = new MineFragment();
                    transaction.add(R.id.fl_content, mMineFragment,"fourth");
                }
                iv_home.setImageResource(R.mipmap.home_0);
                iv_task.setImageResource(R.mipmap.social_0);
                iv_clinic.setImageResource(R.mipmap.message_0);
                iv_mine.setImageResource(R.mipmap.mine_1);
                tv_home.setTextColor(unSelectedColor);
                tv_task.setTextColor(unSelectedColor);
                tv_clinic.setTextColor(unSelectedColor);
                tv_mine.setTextColor(selectedColor);
                transaction.show(mMineFragment);
                lastFragment = mMineFragment;
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (lastFragment != null)
            transaction.hide(lastFragment);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawers();
                return false;
            }else{
                finish();
//                //返回主界面
//                Intent home = new Intent(Intent.ACTION_MAIN);
//                home.addCategory(Intent.CATEGORY_HOME);
//                startActivity(home);
                return super.onKeyDown(keyCode, event);
            }
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


}