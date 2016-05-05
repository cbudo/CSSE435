package edu.rosehulman.onegoodgpsreading;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import edu.rosehulman.me435.AccessoryActivity;
import edu.rosehulman.me435.FieldGps;
import edu.rosehulman.me435.FieldGpsListener;
import edu.rosehulman.me435.FieldOrientation;
import edu.rosehulman.me435.FieldOrientationListener;
import edu.rosehulman.me435.NavUtils;

public class OneGoodGpsReadingActivity extends AccessoryActivity implements FieldGpsListener, FieldOrientationListener {
    // Various constants and member variable names.
    private FieldGps mFieldGps;
    private FieldOrientation mFieldOrientation;
    private static final String TAG = "OneGoodGps";
    private static final double NO_HEADING_KNOWN = 360.0;
    private TextView mCurrentStateTextView, mStateTimeTextView, mGpsInfoTextView, mSensorOrientationTextView;
    private int mGpsCounter = 0;
    private double mCurrentGpsX, mCurrentGpsY, mCurrentGpsHeading;
    private double mCurrentSensorHeading;
    private Handler mCommandHandler = new Handler();
    private Timer mTimer;
    private long mStateStartTime;
    public static final int LOOP_INTERVAL_MS = 100;
    public static final int LOWEST_DESIRABLE_DUTY_CYCLE = 150;
    public static final int LEFT_PWM_VALUE_FOR_STRAIGHT = 245;
    public static final int RIGHT_PWM_VALUE_FOR_STRAIGHT = 255;

    public enum State {READY_FOR_MISSION, FIGURE_8_RED, FIGURE_8_BLUE, WAITING_FOR_GPS, DRIVING_HOME, SEEKING_HOME, WAITING_FOR_PICKUP}

    private State mState = State.READY_FOR_MISSION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_good_gps_reading);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCurrentStateTextView = (TextView) findViewById(R.id.current_state_textview);
        mStateTimeTextView = (TextView) findViewById(R.id.state_time_textview);
        mGpsInfoTextView = (TextView) findViewById(R.id.gps_info_textview);
        mSensorOrientationTextView = (TextView) findViewById(R.id.orientation_textview);
        mFieldGps = new FieldGps(this);
        mFieldOrientation = new FieldOrientation(this);
        setState(State.READY_FOR_MISSION);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFieldOrientation.registerListener(this);
//        mFieldGps.requestLocationUpdates(this);
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        loop();
                    }
                });
            }
        }, 0, LOOP_INTERVAL_MS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFieldOrientation.unregisterListener();
        mTimer.cancel();
        mTimer = null;
        mFieldGps.removeUpdates();
    }

    public void loop() {
        mStateTimeTextView.setText("" + getStateTimeMS() / 1000);
        switch (mState) {
            case READY_FOR_MISSION:
                break;
            case FIGURE_8_RED:
                break;
            case FIGURE_8_BLUE:
                break;
            case WAITING_FOR_GPS:
                if (getStateTimeMS() > 5000) {
                    setState(State.SEEKING_HOME);
                }
                break;
            case DRIVING_HOME:
                break;
            case SEEKING_HOME:
                seekTargetAt(0, 0);
                if (getStateTimeMS() > 6000) {
                    setState(State.WAITING_FOR_PICKUP);
                }
                break;
            case WAITING_FOR_PICKUP:
                if (getStateTimeMS() > 8000) {
                    setState(State.SEEKING_HOME);
                }
                break;
        }
    }

    private long getStateTimeMS() {
        return System.currentTimeMillis() - mStateStartTime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        mFieldGps.requestLocationUpdates(this);
    }

    public void handleRedTeamGo(View view) {
        if (mState == State.READY_FOR_MISSION) {
            setState(State.FIGURE_8_RED);
        }
    }

    public void handleBlueTeamGo(View view) {
        if (mState == State.READY_FOR_MISSION) {
            setState(State.FIGURE_8_BLUE);
        }
    }

    public void handleFakeGps(View view) {
        onLocationChanged(40, 10, 135, null);
    }

    public void handleMissionComplete(View view) {
        sendCommand("CUSTOM Our team ROCKS!");
        if (mState == State.WAITING_FOR_PICKUP)
            setState(State.READY_FOR_MISSION);
    }

    @Override
    protected void onCommandReceived(String receivedCommand) {
        super.onCommandReceived(receivedCommand);
        Toast.makeText(this, "Received: " + receivedCommand, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        mGpsCounter++;
        mCurrentGpsX = x;
        mCurrentGpsY = y;
        mCurrentGpsHeading = NO_HEADING_KNOWN;
        String gpsInfo = getString(R.string.xy_format, x, y);
        if (heading <= 180.0 && heading > -180.0) {
            gpsInfo += " " + getString(R.string.degrees_format, heading);
            mCurrentGpsHeading = heading;
            mFieldOrientation.setCurrentFieldHeading(heading);
            setState(State.DRIVING_HOME);
        } else {
            gpsInfo += " ?°";
        }
        gpsInfo += "   " + mGpsCounter;
        mGpsInfoTextView.setText(gpsInfo);
    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {
        mCurrentSensorHeading = fieldHeading;
        mSensorOrientationTextView.setText(getString(R.string.degrees_format, fieldHeading));
    }

    private void seekTargetAt(double xTarget, double yTarget) {
        int leftDutyCycle = LEFT_PWM_VALUE_FOR_STRAIGHT;
        int rightDutyCycle = RIGHT_PWM_VALUE_FOR_STRAIGHT;
        double targetHeading = NavUtils.getTargetHeading(mCurrentGpsX, mCurrentGpsY, xTarget, yTarget);
        double leftTurnAmount = NavUtils.getLeftTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        double rightTurnAmount = NavUtils.getRightTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        if (leftTurnAmount < rightTurnAmount) {
            leftDutyCycle = LEFT_PWM_VALUE_FOR_STRAIGHT - (int) leftTurnAmount; // Using a VERY simple plan. :)
            leftDutyCycle = Math.max(leftDutyCycle, LOWEST_DESIRABLE_DUTY_CYCLE);
        } else {
            rightDutyCycle = RIGHT_PWM_VALUE_FOR_STRAIGHT - (int) rightTurnAmount; // Could also scale it.
            rightDutyCycle = Math.max(rightDutyCycle, LOWEST_DESIRABLE_DUTY_CYCLE);
        }
        sendCommand("WHEEL SPEED FORWARD " + leftDutyCycle + " FORWARD " + rightDutyCycle);
    }

    public void setState(State state) {
        mStateStartTime = System.currentTimeMillis();
        mCurrentStateTextView.setText(state.name());
        switch (state) {
            case READY_FOR_MISSION:
                sendCommand("WHEEL SPEED BRAKE 0 BRAKE 0");
                break;
            case FIGURE_8_RED:
                redScript();
                break;
            case FIGURE_8_BLUE:
                blueScript();
                break;
            case WAITING_FOR_GPS:
                break;
            case DRIVING_HOME:
                homeScript();
                break;
            case SEEKING_HOME:
                break;
            case WAITING_FOR_PICKUP:
                sendCommand("WHEEL SPEED BRAKE 0 BRAKE 0");
                break;
        }
        mState = state;
    }

    private void redScript() {
        Toast.makeText(this, "Red", Toast.LENGTH_SHORT).show();
        sendCommand("WHEEL SPEED FORWARD 175 FORWARD 200");
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OneGoodGpsReadingActivity.this, "Driving", Toast.LENGTH_SHORT).show();
                sendCommand("WHEEL SPEED FORWARD 150 FORWARD 175");
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand("WHEEL SPEED FORWARD 150 FORWARD 150");
                if (mState == State.FIGURE_8_RED) {
                    setState(State.WAITING_FOR_GPS);
                }
            }
        }, 4000);
    }

    private void blueScript() {
        Toast.makeText(this, "Blue", Toast.LENGTH_SHORT).show();
        sendCommand("WHEEL SPEED FORWARD 200 FORWARD 175");
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand("WHEEL SPEED FORWARD 175 FORWARD 150");
                Toast.makeText(OneGoodGpsReadingActivity.this, "Driving", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand("WHEEL SPEED FORWARD 150 FORWARD 150");
                if (mState == State.FIGURE_8_BLUE) {
                    setState(State.WAITING_FOR_GPS);
                }
            }
        }, 4000);
    }

    private void homeScript() {
        Toast.makeText(this, "Driving", Toast.LENGTH_SHORT).show();
        sendCommand("WHEEL SPEED FORWARD 150 FORWARD 250");
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OneGoodGpsReadingActivity.this, "Home", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == State.DRIVING_HOME) {
                    setState(State.WAITING_FOR_PICKUP);
                }
            }
        }, 5000);
    }
}
