package edu.rosehulman.budocf.exam3;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.rosehulman.me435.FieldGps;
import edu.rosehulman.me435.NavUtils;
import edu.rosehulman.me435.RobotActivity;

public class MainActivity extends RobotActivity {
    private TextView mGpsTextView;
    private TextView mSensorHeadingTextView;
    private TextView mStateTextView;
    private TextView mTimeTextView;
    private TextView mXYTextView;
    private TextView mTargetTextView;
    private TextView mTurnTextView;
    private TextView mCommandTextView;
    private double lastDist;
    private int goingAwayCount;
    private double mGuessX;
    private double mGuessY;

    private enum State {WAITING_FOR_GPS, STRAIGHT_TRY_GPS, CORRECTIVE_SCRIPT, SEEKING_HOME}

    private State mState;
    private long mStateStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGpsTextView = (TextView) findViewById(R.id.target_textView);
        mSensorHeadingTextView = (TextView) findViewById(R.id.xy_textView);
        mStateTextView = (TextView) findViewById(R.id.state_textView);
        mXYTextView = (TextView) findViewById(R.id.sensor_textView);
        mTargetTextView = (TextView) findViewById(R.id.gps_textView);
        mTimeTextView = (TextView) findViewById(R.id.time_textView);
        mCommandTextView = (TextView) findViewById(R.id.state_textView);
        mTurnTextView = (TextView) findViewById(R.id.turn_textView);
        resetButton(null);
        goingAwayCount = 0;
    }

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        super.onLocationChanged(x, y, heading, location);
        mGpsTextView.setText(getString(R.string.xy_format, x, y));
        mGuessX = x;
        mGuessY = y;
        double curDist = NavUtils.getDistance(x, y, 0, 0);
        if (curDist > lastDist) {
            goingAwayCount++;
        }
        if (goingAwayCount > 3) {
            setState(State.CORRECTIVE_SCRIPT);
        }
        lastDist = curDist;
    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {
        super.onSensorChanged(fieldHeading, orientationValues);
        mSensorHeadingTextView.setText(getString(R.string.degrees_format, fieldHeading));
    }

    private long getStateTimeMs() {
        return System.currentTimeMillis() - mStateStartTime;
    }

    public void setState(State newState) {
        mStateStartTime = System.currentTimeMillis();
        mStateTextView.setText(newState.name());
        switch (newState) {
            case WAITING_FOR_GPS:
                break;
            case STRAIGHT_TRY_GPS:
                Toast.makeText(this, "Going Straight", Toast.LENGTH_SHORT).show();
                mSensorHeadingTextView.setText("---");
                mXYTextView.setText("---");
                mTargetTextView.setText("---");
                mTurnTextView.setText("---");
                mCommandTextView.setText("---");
                break;
            case CORRECTIVE_SCRIPT:
                correctiveScript();
                break;
            case SEEKING_HOME:
                break;
        }
        mState = newState;
    }

    public void resetButton(View view) {
        mGpsTextView.setText("---");
        mSensorHeadingTextView.setText("---");
        mStateTextView.setText("---");
        mXYTextView.setText("---");
        mTargetTextView.setText("---");
        mTurnTextView.setText("---");
        mCommandTextView.setText("---");
        onLocationChanged(50, 0, FieldGps.NO_BEARING_AVAILABLE, null);
        mFieldOrientation.setCurrentFieldHeading(0);
        onSensorChanged(0, null);
        setState(State.WAITING_FOR_GPS);
    }

    public void twentyButton(View view) {
        onLocationChanged(20, 0, FieldGps.NO_BEARING_AVAILABLE, null);
    }

    public void fortyButton(View view) {
        onLocationChanged(40, 0, FieldGps.NO_BEARING_AVAILABLE, null);
    }

    public void sixtyButton(View view) {
        onLocationChanged(60, 0, FieldGps.NO_BEARING_AVAILABLE, null);
    }

    public void eightyButton(View view) {
        onLocationChanged(80, 0, FieldGps.NO_BEARING_AVAILABLE, null);
    }

    public void hundredButton(View view) {
        onLocationChanged(100, 0, FieldGps.NO_BEARING_AVAILABLE, null);
    }

    public void zeroButton(View view) {
        mFieldOrientation.unregisterListener();
        onSensorChanged(0, null);
    }

    public void ninetyButton(View view) {
        mFieldOrientation.unregisterListener();
        onSensorChanged(90, null);
    }

    public void negNinetyButton(View view) {
        mFieldOrientation.unregisterListener();
        onSensorChanged(-90, null);
    }

    public void oneEightyButton(View view) {
        mFieldOrientation.unregisterListener();
        onSensorChanged(180, null);
    }

    private void correctiveScript() {
        Toast.makeText(this, "Correcting", Toast.LENGTH_SHORT).show();
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Direction", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == State.CORRECTIVE_SCRIPT) {
                    setState(State.STRAIGHT_TRY_GPS);
                }
            }
        }, 4000);
    }

    public void loop() {
        mTimeTextView.setText("" + getStateTimeMs() / 1000);
        mGuessX += DEFAULT_SPEED_FT_PER_SEC * (double) LOOP_INTERVAL_MS / 1000.0 * Math.cos(Math.toRadians(mCurrentSensorHeading));
        mGuessY += DEFAULT_SPEED_FT_PER_SEC * (double) LOOP_INTERVAL_MS / 1000 * Math.sin(Math.toRadians(mCurrentSensorHeading));
        mXYTextView.setText(getString(R.string.xy_format, mGuessX, mGuessY));
        switch (mState) {
            case WAITING_FOR_GPS:
                if (getStateTimeMs() > 5000) {
                    setState(State.SEEKING_HOME);
                }
                break;
            case STRAIGHT_TRY_GPS:
                if (getStateTimeMs() > 4000) {
                    setState(State.WAITING_FOR_GPS);
                }
                break;
            case CORRECTIVE_SCRIPT:
                break;
            case SEEKING_HOME:
                double targetHeading = NavUtils.getTargetHeading(mGuessX, mGuessY, 0, 0);
                mTargetTextView.setText(getString(R.string.degrees_format, targetHeading));
                break;
        }
    }
}
