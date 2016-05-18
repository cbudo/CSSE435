package edu.rosehulman.drongla.integratedimagerec;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends GolfBallDeliveryActivity {

    Button jumbotron_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        jumbotron_button = (Button) findViewById(R.id.button_jumbotron);
        mViewFlipper = (ViewFlipper)findViewById(R.id.my_view_flipper);
        registerDebugWindow((TextView) findViewById(R.id.SCROLLING_DEBUG_LOGGGER), (ScrollView) findViewById(R.id.SCROLLING_DEBUG_VIEW));
        mViewFlipper.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewFlipper.setDisplayedChild(1);
            }
        }, 500);
        mViewFlipper.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewFlipper.setDisplayedChild(0);
            }
        }, 1500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_next) {
            mViewFlipper.showNext();
            return true;
        }
        if (id == R.id.action_previous) {
            mViewFlipper.showPrevious();
            return true;
        }
        scrollToBottom();
        return super.onOptionsItemSelected(item);
    }


}
