package edu.rosehulman.budocf.conefinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class ConeFinderActivity extends AppCompatActivity {
    public static final String TAG = "ConeFinder";
    private TextView mLeftRightLocationTextView, mTopBottomLocationTextView, mSizePercentageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cone_finder);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLeftRightLocationTextView = (TextView) findViewById(R.id.left_right_location_value);
        mTopBottomLocationTextView = (TextView) findViewById(R.id.top_bottom_location_value);
        mSizePercentageTextView = (TextView) findViewById(R.id.size_percentage_value);

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            Log.d(TAG, "Everything should be fine with using the camera.");
        } else {
            Log.d(TAG, "Requesting permission to use the camera.");
            String[] CAMERA_PERMISSONS = {
                    Manifest.permission.CAMERA
            };
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSONS, 0);
        }

    }

    /**
     * Displays the blob target info in the text views.
     */
    public void onImageRecComplete(boolean coneFound, double leftRightLocation, double topBottomLocation, double sizePercentage) {
        if (coneFound) {
            mLeftRightLocationTextView.setText(String.format("%.3f", leftRightLocation));
            mTopBottomLocationTextView.setText(String.format("%.3f", topBottomLocation));
            mSizePercentageTextView.setText(String.format("%.5f", sizePercentage));
        } else {
            mLeftRightLocationTextView.setText("---");
            mTopBottomLocationTextView.setText("---");
            mSizePercentageTextView.setText("---");
        }
    }
}
