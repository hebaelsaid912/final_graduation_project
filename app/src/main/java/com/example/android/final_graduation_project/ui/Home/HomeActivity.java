package com.example.android.final_graduation_project.ui.home;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.android.final_graduation_project.SessionManager;
import com.example.android.final_graduation_project.ui.home.Drawer.SpaceItem;
import com.example.android.final_graduation_project.ui.home.fragments.AboutUsFragment;
import com.example.android.final_graduation_project.ui.home.fragments.ActiveRoomsFragment;
import com.example.android.final_graduation_project.ui.home.fragments.rooms.getRooms.DashboardFragment;
import com.example.android.final_graduation_project.ui.home.Drawer.DrawerAdapter;
import com.example.android.final_graduation_project.ui.home.Drawer.DrawerItem;
import com.example.android.final_graduation_project.ui.home.Drawer.SimpleItem;
import com.example.android.final_graduation_project.ui.home.fragments.FollowerRoomsFragment;
import com.example.android.final_graduation_project.ui.home.fragments.ProfileFragment;
import com.example.android.final_graduation_project.R;
import com.example.android.final_graduation_project.ui.home.fragments.SettingFragment;
import com.example.android.final_graduation_project.StatusBar;
import com.example.android.final_graduation_project.databinding.ActivityHomeBinding;
import com.example.android.final_graduation_project.ui.phone_verifying.sendOtp.RegisterActivity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements DrawerAdapter.onItemSelectedListener{

    private static final int POS_CLOSE = 0 ;
    private static final int POS_DASHBOARD = 2 ;
    private static final int POS_MY_PROFILE = 3 ;
    private static final int POS_ACTIVE_ROOMS = 4 ;
    private static final int POS_FOLLOWER_ROOMS = 5 ;
    private static final int POS_SETTING = 6 ;
    private static final int POS_ABOUT_US = 7 ;
    private static final int POS_LOGOUT = 9 ;

    private  String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        new StatusBar(this , R.color.white);
        setSupportActionBar(binding.toolbar2);
        //getActionBar().setDisplayShowTitleEnabled(false);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(binding.toolbar2)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                new SpaceItem(100),
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_MY_PROFILE),
                createItemFor(POS_ACTIVE_ROOMS),
                createItemFor(POS_FOLLOWER_ROOMS),
                createItemFor(POS_SETTING),
                createItemFor(POS_ABOUT_US),
                new SpaceItem(58),
                createItemFor(POS_LOGOUT)
        ));
        adapter.setListener(this);
        RecyclerView recyclerView = findViewById(R.id.drawer_list);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);
    }
    private DrawerItem createItemFor(int position){
        return  new SimpleItem(screenIcons[position],screenTitles[position])
                .withIconTint(color(R.color.color_1))
                .withTitleTint(color(R.color.color_1))
                .withSelectedIconTint(color(R.color.color_2))
                .withSelectedTitleTint(color(R.color.color_2));
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
    private  String[] loadScreenTitles(){
        return getResources().getStringArray(R.array.id_activityScreenTitles);

    }
    private  Drawable[] loadScreenIcons(){
        TypedArray typedArray = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[typedArray.length()];
        for (int i=0 ; i<icons.length ; i++){
            int id =  typedArray.getResourceId(i,0);
            if (id != 0){
                icons[i] = ContextCompat.getDrawable(this,id);
            }
        }
        typedArray.recycle();
        return icons;
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.i("Home Drawer " , position+"");
        switch (position){
            case POS_DASHBOARD:
                DashboardFragment tab1 = DashboardFragment.newInstance(SessionManager.getAccessToken());
                //DashboardFragment.newInstance(SessionManager.getAccessToken());
                transaction.replace(R.id.container , tab1);
                break;
            case POS_MY_PROFILE:
                ProfileFragment tab2 = new ProfileFragment();
                transaction.replace(R.id.container , tab2);
                break;
            case POS_ACTIVE_ROOMS:
                ActiveRoomsFragment tab3 = new ActiveRoomsFragment();
                transaction.replace(R.id.container , tab3);
                break;
            case POS_FOLLOWER_ROOMS:
                FollowerRoomsFragment tab4 = new FollowerRoomsFragment();
                transaction.replace(R.id.container , tab4);
                break;
            case POS_ABOUT_US:
                AboutUsFragment tab5 = new AboutUsFragment();
                transaction.replace(R.id.container , tab5);
                break;
            case POS_SETTING:
                SettingFragment tab6 = new SettingFragment();
                transaction.replace(R.id.container , tab6);
                break;
        }
        if(position == POS_LOGOUT){
            SessionManager.logoutUserSession();
            Intent intent = new Intent(this , RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        slidingRootNav.closeMenu();
        transaction.commit();
    }
   /* private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

}