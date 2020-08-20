package com.example.baptcserver;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.example.baptcserver.Common.Common;
import com.example.baptcserver.EventBus.CategoryClick;
import com.example.baptcserver.EventBus.ChangeMenuClick;
import com.example.baptcserver.EventBus.ToastEvent;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private int menuClick = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_category, R.id.nav_crop_list, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCategoryClick(CategoryClick categoryClick) {
        if(categoryClick.isSuccess()) {
            if (menuClick != R.id.nav_crop_list) {
                navController.navigate(R.id.nav_crop_list);
                menuClick = R.id.nav_crop_list;
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onToastEvent(ToastEvent toastEvent) {
        if(toastEvent.getAction() == Common.ACTION.CREATE) {
            Toast.makeText(this, "Create Success", Toast.LENGTH_SHORT).show();

        } else if (toastEvent.getAction() == Common.ACTION.UPDATE) {
            Toast.makeText(this, "UPDATE Success", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show();
        }
            EventBus.getDefault().postSticky(new ChangeMenuClick(toastEvent.isFromCropList()));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onChangeMenuClick(ChangeMenuClick changeMenuClick) {
        if(!changeMenuClick.isFromCropList()) {
            navController.popBackStack(R.id.nav_category, true);
            navController.navigate(R.id.nav_category);
        }
//        else {
//            navController.popBackStack(R.id.nav_crop_list, true);
//            navController.navigate(R.id.nav_crop_list);
//        }
        menuClick = -1;
    }
}