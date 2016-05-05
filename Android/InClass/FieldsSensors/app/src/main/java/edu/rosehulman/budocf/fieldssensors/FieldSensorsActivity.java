package edu.rosehulman.budocf.fieldssensors;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import edu.rosehulman.me435.FieldGps;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.FieldOrientation;
import edu.rosehulman.me435.FieldOrientationListener;

public class FieldSensorsActivity extends AppCompatActivity implements FieldGpsListener, FieldOrientationListener {

    private int mGpsCounter = 0;
    private TextView mGpsYTextView, mGpsXTextView, mGpsHeadingTextView, mGpsCounterTextView;
    private FieldGps mFieldGPS;
    private TextView mSensorHeadingTextView;
    private FieldOrientation mFieldOrientation;
    private boolean mSetFieldOrientationWithGPSHeading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_sensors);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mFieldGPS = new FieldGps((FieldGpsListener) this);
        mGpsXTextView = (TextView) findViewById(R.id.gps_x_textView);
        mGpsYTextView = (TextView) findViewById(R.id.gps_y_textview);
        mGpsHeadingTextView = (TextView) findViewById(R.id.gps_heading_textview);
        mGpsCounterTextView = (TextView) findViewById(R.id.gps_counter_textview);
        mSensorHeadingTextView = (TextView) findViewById(R.id.artificial_heading_textview);
        mFieldOrientation = new FieldOrientation(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFieldGPS.requestLocationUpdates(this);
        mFieldOrientation.registerListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFieldGPS.removeUpdates();
        mFieldOrientation.unregisterListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mFieldGPS.requestLocationUpdates(this);
    }

    public void handleSetOrigin(View view) {
        Toast.makeText(this, "You Pressed Set Origin", Toast.LENGTH_SHORT).show();
        mFieldGPS.setCurrentLocationAsOrigin();
    }

    public void handleSetXAxis(View view) {
        Toast.makeText(this, "You Pressed Set X Axis", Toast.LENGTH_SHORT).show();
        mFieldGPS.setCurrentLocationAsLocationOnXAxis();
    }

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        mGpsCounter++;
        mGpsCounterTextView.setText("" + mGpsCounter);
        mGpsXTextView.setText((int) x + " ft");
        mGpsYTextView.setText((int) y + " ft");
        if (heading > -180 && heading <= 180) {
            mGpsHeadingTextView.setText((int) heading + " degrees");
            if (mSetFieldOrientationWithGPSHeading) {
                mFieldOrientation.setCurrentFieldHeading(heading);
            }
        } else {
            mGpsHeadingTextView.setText("--");
        }
    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {
        mSensorHeadingTextView.setText((int) fieldHeading + " degrees");
        Log.d("Sensor", "Azimuth = " + orientationValues[0]);
        Log.d("Sensor", "Pitch = " + orientationValues[1]);
        Log.d("Sensor", "Roll = " + orientationValues[2]);
    }

    public void handleToggle(View view) {
        ToggleButton tb = (ToggleButton) view;
        mSetFieldOrientationWithGPSHeading = tb.isChecked();
    }

    public void handleSetHeadingTo0(View view) {
        mFieldOrientation.setCurrentFieldHeading(0);
    }
}
