package com.android.test;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.core.slidepanel.SlideContainer;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlideContainer slideContainer = (SlideContainer) findViewById(R.id.slideContainer);
        slideContainer.setContent(R.layout.frame1);
        slideContainer.setBottomView(R.layout.frame2);
        slideContainer.setBottomTopHeader(R.layout.frame3);
        slideContainer.setBottomOffset(200);
        slideContainer.setSlideChangeListener(new SlideContainer.ISlideChange() {
            @Override
            public void onStartSlide(boolean bottomIn) {

            }

            @Override
            public void onSlide(int offset, boolean bottomIn) {

            }

            @Override
            public void onSlideFinish(boolean bottomIn) {
                Log.d("","onSlideFinish "+bottomIn);
                Toast.makeText(MainActivity.this,"onSlideFinish "+bottomIn,Toast.LENGTH_SHORT).show();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.frame1,getBaseFragment("Content"))
                .replace(R.id.frame2,getBaseFragment("Bottom"))
                .replace(R.id.frame3,getBaseFragment("BottomTopHeader"))
                .commit();
    }

    private Fragment getBaseFragment(String top) {
        BaseFragment baseFragment = new BaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag",top);
        baseFragment.setArguments(bundle);
        return baseFragment;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
