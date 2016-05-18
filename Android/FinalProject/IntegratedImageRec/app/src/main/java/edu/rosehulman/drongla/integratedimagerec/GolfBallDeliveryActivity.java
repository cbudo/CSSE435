package edu.rosehulman.drongla.integratedimagerec;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.rosehulman.me435.NavUtils;

public class GolfBallDeliveryActivity extends ImageRecActivity {

    /**
     * Used with logging that you'll see later.
	 * Note: is updated to match state name	 
     */
    private String mTAG = "GolfBallDelivery";
    private TextView mDebugLogger;
    private ScrollView mDebugScroller;
    private boolean mHasDebugTextview = false;
    private boolean mFirstRun = true;

    private boolean dropped1;
    private boolean dropped2;
    private boolean dropped3;
    private int TURN_GAIN = 25;

    //**************** Calibration ****************
    /*
        Saving and loading Calibration
     */
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;



    private enum CalibrationStatus {
        CALIBRATED, NOT_CALIBRATED, NOW_CALIBRATING
    }

    private CalibrationStatus cal_state;
    private int cal_smTAGe = -1;
    private Button calibrateButton, loadCalibration, debugTestBalls;

    String filename = "cal.data";
    private int calibrationData[][][] = new int[7][7][3]; //  7 colors, 7 readings, 3 sensors.
    private int idMatrix[] = new int[3];

    private boolean read_1 = false, read_2 = false, read_3 = false;

    //**************** Golf Ball Stand ****************
    /**
     * An array (of size 3) that stores what color is present in each golf ball stand location.
     */
    public BallColor[] mLocationColors = new BallColor[]{BallColor.NONE, BallColor.NONE, BallColor.NONE};

    private TextView m_text_debug_1, m_text_debug_2, m_text_debug_3;
    private int debug_score_1, debug_score_2, debug_score_3;
    private GolfBallObject debug_second_1, debug_second_2, debug_second_3;

    /**
     * An enum used for variables when a ball color needs to be referenced.
     */
    public enum BallColor {
        NONE, BLUE, RED, YELLOW, GREEN, BLACK, WHITE
    }

    private class GolfBallObject implements Comparable<GolfBallObject> {
        public BallColor color;
        public int score;

        public GolfBallObject(BallColor c, int s) {
            color = c;
            score = s;
        }

        @Override
        public int compareTo(GolfBallObject another) {
            return score - another.score;
        }
    }

    //**************** State Machine ****************
    public enum State {
        READY_FOR_MISSION,
        DRIVE_TOWARDS_NEAR_BALL,
        DROP_NEAR,
        NEAR_IMAGE_REC,
        CHECK_DROPPED_NEAR,
        DRIVE_TOWARDS_FAR_BALL,
        FAR_IMAGE_REC,
        DROP_FAR,
        CHECK_DROPPED_FAR,
        DROP_WHITE,
        CHECK_DROPPED_WHITE,
        DRIVE_TOWARDS_HOME,
        WAITING_FOR_PICKUP,
        SEEKING_HOME,
        HOME_IMAGE_REC,
    }

    /**
     * Tracks the robot's current state.
     */
    public State mState;


    /**
     * Simple boolean that is updated when the Team button is pressed to switch teams.
     */
    public boolean mOnRedTeam = false;

    private Scripts mScripts;

    // ---------------------- UI References ----------------------
    /**
     * An array (of size 3) that keeps a reference to the 3 balls displayed on the UI.
     */
    private ImageButton[] mBallImageButtons;

    /**
     * References to the buttons on the UI that can change color.
     */
    private Button mTeamChangeButton, mGoOrMissionCompleteButton, mJumbotron_button;

    /**
     * An array constants (of size 7) that keeps a reference to the different ball color images resources.
     */
    // Note, the order is important and must be the same throughout the app.
    private static final int[] BALL_DRAWABLE_RESOURCES = new int[]{R.drawable.none_ball, R.drawable.blue_ball,
            R.drawable.red_ball, R.drawable.yellow_ball, R.drawable.green_ball, R.drawable.black_ball, R.drawable.white_ball};

    /**
     * TextViews that can change values.
     */
    private TextView mCurrentStateTextView, mStateTimeTextView, mGpsInfoTextView, mSensorOrientationTextView,
            mGuessXYTextView, mLeftDutyCycleTextView, mRightDutyCycleTextView, mMatchTimeTextView, mTargetTextView, mJumboXTextView, mJumboYTextView, mJumboStateView;


    private LinearLayout mJumbotronLayout;
    // ---------------------- End of UI References ----------------------

    // ---------------------- Mission strategy values ----------------------
    /**
     * Constants for the known locations.
     */
    public static final long NEAR_BALL_GPS_X = 90;
    public static final long FAR_BALL_GPS_X = 240;


    /**
     * Variables that will be either 50 or -50 depending on the balls we get.
     */
    private double mNearBallGpsY, mFarBallGpsY;

    /**
     * If that ball is present the values will be 1, 2, or 3.
     * If not present the value will be 0.
     * For example if we have the black ball, then mWhiteBallLocation will equal 0.
     */
    public int mNearBallLocation, mFarBallLocation, mWhiteBallLocation;

    private boolean mRunningScript = false;

    /**
     * Updates the mission strategy variables.
     */
    private void updateMissionStrategyVariables() {
        mNearBallGpsY = -50.0; // Note, X value is a constant.
        mFarBallGpsY = 50.0; // Note, X value is a constant.

        for (int i = 0; i < 3; i++) {
            BallColor ballColor = mLocationColors[i];
            if (ballColor == BallColor.RED || ballColor == BallColor.GREEN) {
                if (mOnRedTeam) {
                    mNearBallLocation = i + 1;
                } else {
                    mFarBallLocation = i + 1;
                }
            } else if (ballColor == BallColor.WHITE) {
                mWhiteBallLocation = i + 1;
            } else if (ballColor == BallColor.BLUE || ballColor == BallColor.YELLOW) {
                if (!mOnRedTeam) {
                    mNearBallLocation = i + 1;
                } else {
                    mFarBallLocation = i + 1;
                }
            }
            if ((ballColor == BallColor.GREEN && !mOnRedTeam) ||
                    (ballColor == BallColor.YELLOW && mOnRedTeam)) {
                mFarBallGpsY = -50;

            }
            if ((ballColor == BallColor.GREEN && mOnRedTeam) ||
                    (ballColor == BallColor.YELLOW && !mOnRedTeam)) {
                mNearBallGpsY = 50;

            }


        }
        if (mOnRedTeam){
            logToDebugWindow(mTAG, "Team Red");
        } else {
            logToDebugWindow(mTAG, "Team Blu");
        }

        logToDebugWindow(mTAG, "Near ball is position " + mNearBallLocation + " so drive to " + mNearBallGpsY);
        logToDebugWindow(mTAG, "Far ball is position " + mFarBallLocation + " so drive to " + mFarBallGpsY);
        logToDebugWindow(mTAG, "White ball is position " + mWhiteBallLocation);
    }
    // ----------------- End of mission strategy values ----------------------


    // ---------------------------- Timing area ------------------------------
    /**
     * Time when the state began (saved as the number of millisecond since epoch).
     */
    private long mStateStartTime;

    /**
     * Time when the match began, ie when Go! was pressed (saved as the number of millisecond since epoch).
     */
    private long mMatchStartTime;

    /**
     * Constant that holds the maximum length of the match (saved in milliseconds).
     */
    private long MATCH_LENGTH_MS = 300000; // 5 minutes in milliseconds (5 * 60 * 1000)
    // ----------------------- End of timing area --------------------------------

    // ---------------------------- Driving area ---------------------------------
    /**
     * When driving towards a target, using a seek strategy, consider that state a success when the
     * GPS distance to the target is less than (or equal to) this value.
     */
    public static final double ACCEPTED_DISTANCE_AWAY_FT = 10.0; // Within 10 feet is close enough.

    /**
     * Multiplier used during seeking to calculate a PWM value based on the turn amount needed.
     */
    private static final double SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER = 3.0;  // units are (PWM value)/degrees

    /**
     * Variable used to cap the slowest PWM duty cycle used while seeking. Pick a value from -255 to 255.
     */
    private static final int LOWEST_DESIRABLE_SEEKING_DUTY_CYCLE = 150;

    /**
     * PWM duty cycle values used with the drive straight dialog that make your robot drive straightest.
     */
    public int mLeftStraightPwmValue = 255, mRightStraightPwmValue = 255;
    // ------------------------ End of Driving area ------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_golf_ball_delivery);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBallImageButtons = new ImageButton[]{(ImageButton) findViewById(R.id.location_1_image_button),
                (ImageButton) findViewById(R.id.location_2_image_button),
                (ImageButton) findViewById(R.id.location_3_image_button)};
        mTeamChangeButton = (Button) findViewById(R.id.team_change_button);
        mCurrentStateTextView = (TextView) findViewById(R.id.current_state_textview);
        mStateTimeTextView = (TextView) findViewById(R.id.state_time_textview);
        mGpsInfoTextView = (TextView) findViewById(R.id.gps_info_textview);
        mSensorOrientationTextView = (TextView) findViewById(R.id.orientation_textview);
        mGuessXYTextView = (TextView) findViewById(R.id.guess_location_textview);
        mLeftDutyCycleTextView = (TextView) findViewById(R.id.left_duty_cycle_textview);
        mRightDutyCycleTextView = (TextView) findViewById(R.id.right_duty_cycle_textview);
        mMatchTimeTextView = (TextView) findViewById(R.id.match_time_textview);
        mGoOrMissionCompleteButton = (Button) findViewById(R.id.go_or_mission_complete_button);
        mJumbotron_button = (Button) findViewById(R.id.button_jumbotron);
        mJumboStateView = (TextView) findViewById(R.id.JumboStateView);

        mJumboXTextView = (TextView) findViewById(R.id.jumbo_x);
        mJumboYTextView = (TextView) findViewById(R.id.jumbo_y);

        mJumbotronLayout = (LinearLayout) findViewById(R.id.jumbotron_linear_layout);
        mTargetTextView = (TextView) findViewById(R.id.target_location_textview);
        mTargetX = 0;
        mTargetY = 0;

        // When you start using the real hardware you don't need test buttons.
        boolean hideFakeGpsButtons = false;
        if (hideFakeGpsButtons) {
            TableLayout fakeGpsButtonTable = (TableLayout) findViewById(R.id.fake_gps_button_table);
            fakeGpsButtonTable.setVisibility(View.GONE);
        }
        setState(State.READY_FOR_MISSION);
        setLocationToColor(1, BallColor.NONE);
        setLocationToColor(2, BallColor.NONE);
        setLocationToColor(3, BallColor.NONE);

        /*
        Copy and pasted from GolfBallSorting
         */
//        ball1 = (TextView) findViewById(R.id.ball1_textView);
//        ball2 = (TextView) findViewById(R.id.ball2_textView);
//        ball3 = (TextView) findViewById(R.id.ball3_textView);

        m_text_debug_1 = (TextView) findViewById(R.id.textview_ball1);
        m_text_debug_2 = (TextView) findViewById(R.id.textview_ball2);
        m_text_debug_3 = (TextView) findViewById(R.id.textview_ball3);

        dropped1 = false;
        dropped2 = false;
        dropped3 = false;
        mNearBallLocation = 0;
        mFarBallLocation = 0;
        mWhiteBallLocation = 0;

        cal_state = CalibrationStatus.NOT_CALIBRATED;
        calibrateButton = (Button) findViewById(R.id.calibrateButton);
        loadCalibration = (Button) findViewById(R.id.loadCalibration);
        debugTestBalls = (Button) findViewById(R.id.buttonDebugBallTest);
        mRunningScript = false;

        mScripts = new Scripts(this);
    }

    /**
     * Use this helper method to set the color of a ball.
     * The location value here is 1 based.  Send 1, 2, or 3
     * Side effect: Updates the UI with the appropriate ball color resource image.
     */
    public void setLocationToColor(int location, BallColor ballColor) {
        logToDebugWindow(mTAG, "Position "+location+" set to "+ballColor.toString());
        mBallImageButtons[location - 1].setImageResource(BALL_DRAWABLE_RESOURCES[ballColor.ordinal()]);
        mLocationColors[location - 1] = ballColor;
    }

    /**
     * Used to get the state time in milliseconds.
     */
    private long getStateTimeMs() {
        return System.currentTimeMillis() - mStateStartTime;
    }

    /**
     * Used to get the match time in milliseconds.
     */
    private long getMatchTimeMs() {
        return System.currentTimeMillis() - mMatchStartTime;
    }


    // --------------------------- Methods added ---------------------------

    /**
     * Method that is called 10 times per second for updates. Note, the setup was done within RobotActivity.
     */
    public void loop() {
        super.loop(); // Important to call super first so that the RobotActivity loop function is run first.
        // RobotActivity updated the mGuessX and mGuessY already. Here we need to display it.
        // Match timer.
        long matchTimeMs = MATCH_LENGTH_MS;
        long timeRemainingSeconds = MATCH_LENGTH_MS / 1000;
        if (mState != State.READY_FOR_MISSION) {
            matchTimeMs = getMatchTimeMs();
            timeRemainingSeconds = (MATCH_LENGTH_MS - matchTimeMs) / 1000;
            if (getMatchTimeMs() > MATCH_LENGTH_MS) {
                setState(State.READY_FOR_MISSION);
            }
        }

        mJumboXTextView.setText("" + (int) mGuessX);
        mJumboYTextView.setText("" + (int) mGuessY);

        mStateTimeTextView.setText("" + getStateTimeMs() / 1000);

        mMatchTimeTextView.setText(getString(R.string.time_format, timeRemainingSeconds / 60, timeRemainingSeconds % 60));
        mGuessXYTextView.setText("(" + (int) mGuessX + ", " + (int) mGuessY + ")");
        if (mState == State.FAR_IMAGE_REC || mState == State.NEAR_IMAGE_REC || mState == State.HOME_IMAGE_REC) {
            if (!mConeFound) {
                //exit state, lost cone
                Toast.makeText(GolfBallDeliveryActivity.this, "Cone = Lost", Toast.LENGTH_SHORT).show();
                logToDebugWindow(mTAG, "Lost cone");
            }
            int amount = (int) Math.round((mConeLeftRightLocation * 10) * TURN_GAIN);
            if (mConeLeftRightLocation < 0) {
                logToDebugWindow(mTAG, "Turn left some amount("+amount+")");
                sendWheelSpeed(100 - amount, 100 + amount);
            } else if (mConeLeftRightLocation > 0) {
                logToDebugWindow(mTAG, "Turn right some amount("+amount+")");
                sendWheelSpeed(100 + amount, 100 - amount);
            }
            if (mConeSize > 0.1) {
                logToDebugWindow(mTAG, "May want to stop - the cone is pretty big");
                if (mState == State.NEAR_IMAGE_REC)
                    setState(State.DROP_NEAR);
                else if (mState == State.FAR_IMAGE_REC)
                    setState(State.DROP_FAR);
                else if (mState == State.HOME_IMAGE_REC)
                    setState(State.WAITING_FOR_PICKUP);
            }
        } else {
            if (mConeFound) {
                // set ir Near, Far, Home
                if (mState == State.DRIVE_TOWARDS_NEAR_BALL)
                    setState(State.NEAR_IMAGE_REC);
                else if (mState == State.DRIVE_TOWARDS_FAR_BALL)
                    setState(State.FAR_IMAGE_REC);
                else if (mState == State.DRIVE_TOWARDS_HOME || mState == State.SEEKING_HOME)
                    setState(State.HOME_IMAGE_REC);
            }
        }
        switch (mState) {
            case READY_FOR_MISSION:
                break;
            case DRIVE_TOWARDS_NEAR_BALL:
                break;
            case NEAR_IMAGE_REC:
                break;
            case DROP_NEAR:
                if (!mRunningScript) {
                    mRunningScript = true;
                    Toast.makeText(GolfBallDeliveryActivity.this, "Knocking off " + mNearBallLocation, Toast.LENGTH_SHORT).show();
                    logToDebugWindow("DROP_FAR", "Knocking off "+mNearBallLocation);

                    mScripts.removeBallAtLocation(mNearBallLocation);
                    mCommandHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRunningScript = false;
                            setState(State.CHECK_DROPPED_NEAR);
                        }
                    }, 3500);
                }
                break;
            case CHECK_DROPPED_NEAR:
                logToDebugWindow("CHECK_DROPPED_FAR", "Checking dropped far: mWBL:"+mNearBallLocation+" d1:"+dropped1+" d2:"+dropped2+" d3:"+dropped3);

                switch (mNearBallLocation) {
                    case 1:
                        if (!dropped1)
                            setState(State.DROP_NEAR);
                        else {
                            mNearBallLocation = -1;
                            setState(State.DRIVE_TOWARDS_FAR_BALL);
                        }
                        break;
                    case 2:
                        if (!dropped2)
                            setState(State.DROP_NEAR);
                        else {
                            mNearBallLocation = -1;
                            setState(State.DRIVE_TOWARDS_FAR_BALL);
                        }
                        break;
                    case 3:
                        if (!dropped3)
                            setState(State.DROP_NEAR);
                        else {
                            mNearBallLocation = -1;
                            setState(State.DRIVE_TOWARDS_FAR_BALL);
                        }
                        break;
                    default:
                        setState(State.DRIVE_TOWARDS_FAR_BALL);
                }
                break;
            case DRIVE_TOWARDS_FAR_BALL:
                seekTargetAt(FAR_BALL_GPS_X, mFarBallGpsY);
                break;
            case FAR_IMAGE_REC:
                break;
            case DROP_FAR:
                if (!mRunningScript) {
                    mRunningScript = true;
                    Toast.makeText(GolfBallDeliveryActivity.this, "Knocking off " + mFarBallLocation, Toast.LENGTH_SHORT).show();
                    logToDebugWindow("DROP_FAR", "Knocking off "+mFarBallLocation);
                    mScripts.removeBallAtLocation(mFarBallLocation);
                    mCommandHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRunningScript = false;
                            setState(State.CHECK_DROPPED_FAR);
                        }
                    }, 3500);
                }
                break;
            case CHECK_DROPPED_FAR:
                logToDebugWindow("CHECK_DROPPED_FAR", "Checking dropped far: mWBL:"+mFarBallLocation+" d1:"+dropped1+" d2:"+dropped2+" d3:"+dropped3);
                switch (mFarBallLocation) {
                    case 1:
                        if (!dropped1)
                            setState(State.DROP_FAR);
                        else {
                            mFarBallLocation = -1;
                            setState(State.DROP_WHITE);
                        }
                        break;
                    case 2:
                        if (!dropped2)
                            setState(State.DROP_FAR);
                        else {
                            mFarBallLocation = -1;
                            setState(State.DROP_WHITE);
                        }
                        break;
                    case 3:
                        if (!dropped3)
                            setState(State.DROP_FAR);
                        else {
                            mFarBallLocation = -1;
                            setState(State.DROP_WHITE);
                        }
                        break;
                    default:
                        setState(State.DROP_WHITE);
                }
                break;
            case DROP_WHITE:
                if (!mRunningScript) {
                    mRunningScript = true;
                    Toast.makeText(GolfBallDeliveryActivity.this, "Knocking off " + mWhiteBallLocation, Toast.LENGTH_SHORT).show();
                    logToDebugWindow("DROP_WHITE", "Knocking off "+mWhiteBallLocation);
                    mScripts.removeBallAtLocation(mWhiteBallLocation);
                    mCommandHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWhiteBallLocation = -1;
                            mRunningScript = false;
                            setState(State.CHECK_DROPPED_WHITE);
                        }
                    }, 3500);
                }
                break;
            case CHECK_DROPPED_WHITE:
                logToDebugWindow("CHECK_DROPPED_WHITE", "Checking dropped white: mWBL:"+mWhiteBallLocation+" d1:"+dropped1+" d2:"+dropped2+" d3:"+dropped3);
                switch (mWhiteBallLocation) {
                    case 1:
                        if (!dropped1)
                            setState(State.DROP_WHITE);
                        else {
                            mWhiteBallLocation = -1;
                            setState(State.DRIVE_TOWARDS_HOME);
                        }
                        break;
                    case 2:
                        if (!dropped2)
                            setState(State.DROP_WHITE);
                        else {
                            mWhiteBallLocation = -1;
                            setState(State.DRIVE_TOWARDS_HOME);
                        }
                        break;
                    case 3:
                        if (!dropped3)
                            setState(State.DROP_WHITE);
                        else {
                            mWhiteBallLocation = -1;
                            setState(State.DRIVE_TOWARDS_HOME);
                        }
                        break;
                    default:
                        setState(State.DRIVE_TOWARDS_HOME);
                }
                break;
            case DRIVE_TOWARDS_HOME:
                // check if in "IR BOX"
                seekTargetAt(0, 0);
                break;
            case WAITING_FOR_PICKUP:
                if (getStateTimeMs() > 8000) {
                    logToDebugWindow(mTAG, "Pickup expired");
                    setState(State.SEEKING_HOME);
                }
                break;
            case SEEKING_HOME:
                // check if in "IR BOX"
                seekTargetAt(0, 0);
                if (getStateTimeMs() > 8000) {
                    logToDebugWindow(mTAG, "Seeking home expired");
                    setState(State.WAITING_FOR_PICKUP);
                }
                break;
            default:
                // Other states don't need to do anything, but could.
                break;
        }

    }

    private boolean inIRRegion() {
//        NavUtils.getDistance()
        switch (mState) {
            case NEAR_IMAGE_REC:
                return true;
            case FAR_IMAGE_REC:
                return true;
            case HOME_IMAGE_REC:
                return true;
            default:
                break;
        }
        return false;
    }

    public void handleDropNearButton(View view) {
        setState(State.DROP_NEAR);
    }

    public void handleDropWhiteButton(View view) {
        setState(State.DROP_WHITE);
    }

    public void handleDropFarButton(View view) {
        setState(State.DROP_FAR);
    }

    /**
     * Adjust the PWM duty cycles based on the turn amount needed to point at the target heading.
     *
     * @param x GPS X value of the target.
     * @param y GPS Y value of the target.
     */
    private void seekTargetAt(double x, double y) {
        logToDebugWindow(mTAG, "Seeking target at (" + (int) mTargetX + ", " + (int) mTargetY + ")");
        mTargetX = x;
        mTargetY = y;
        //target_location_textview
        mTargetTextView.setText("(" + (int) mTargetX + ", " + (int) mTargetY + ")" + "  ?");

        int leftDutyCycle = mLeftStraightPwmValue;
        int rightDutyCycle = mRightStraightPwmValue;
        double targetHeading = NavUtils.getTargetHeading(mGuessX, mGuessY, x, y);
        double leftTurnAmount = NavUtils.getLeftTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        double rightTurnAmount = NavUtils.getRightTurnHeadingDelta(mCurrentSensorHeading, targetHeading);
        if (leftTurnAmount < rightTurnAmount) {
            leftDutyCycle = mLeftStraightPwmValue - (int) (leftTurnAmount * SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER);
            leftDutyCycle = Math.max(leftDutyCycle, LOWEST_DESIRABLE_SEEKING_DUTY_CYCLE);
        } else {
            rightDutyCycle = mRightStraightPwmValue - (int) (rightTurnAmount * SEEKING_DUTY_CYCLE_PER_ANGLE_OFF_MULTIPLIER);
            rightDutyCycle = Math.max(rightDutyCycle, LOWEST_DESIRABLE_SEEKING_DUTY_CYCLE);
        }
        sendWheelSpeed(leftDutyCycle, rightDutyCycle);
    }

    // --------------------------- Drive command ---------------------------

    /**
     * Send the wheel speeds to the robot and updates the TextViews.
     */
    @Override
    public void sendWheelSpeed(int leftDutyCycle, int rightDutyCycle) {
        super.sendWheelSpeed(leftDutyCycle, rightDutyCycle); // Send the values to the
        mLeftDutyCycleTextView.setText("Left\n" + leftDutyCycle);
        mRightDutyCycleTextView.setText("Right\n" + rightDutyCycle);
    }
    // --------------------------- Sensor listeners ---------------------------

    /**
     * GPS sensor updates.
     */
    @Override
    public void onLocationChanged(double x, double y, double heading, Location location) {
        super.onLocationChanged(x, y, heading, location);
        String gpsInfo = getString(R.string.xy_format, x, y);
        if (heading <= 180.0 && heading > -180.0) {
            gpsInfo += " " + getString(R.string.degrees_format, heading);
            mJumbotronLayout.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else {
            gpsInfo += " ?º";
            mJumbotronLayout.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        }
        gpsInfo += "    " + mGpsCounter;
        mGpsInfoTextView.setText(gpsInfo);
        if (mState == State.DRIVE_TOWARDS_FAR_BALL) {
            double distanceFromTarget = NavUtils.getDistance(mCurrentGpsX, mCurrentGpsY,
                    FAR_BALL_GPS_X, mFarBallGpsY);
            if (distanceFromTarget < ACCEPTED_DISTANCE_AWAY_FT) {
                setState(State.FAR_IMAGE_REC);
            }
        }
        if (mState == State.DRIVE_TOWARDS_HOME) {
            // Shorter to write since the RobotActivity already calculates the distance to 0, 0.
            if (mCurrentGpsDistance < ACCEPTED_DISTANCE_AWAY_FT) {
                setState(State.WAITING_FOR_PICKUP);
            }
        }

        mTargetTextView.setText("(" + (int) mTargetX + ", " + (int) mTargetY + ")" + " " + (int) mCurrentGpsDistance);

    }

    public void handleSetOrigin(View view) {
        mFieldGps.setCurrentLocationAsOrigin();
    }

    public void handleSetXAxis(View view) {
        mFieldGps.setCurrentLocationAsLocationOnXAxis();
    }
    // --------------------------- Button Handlers ----------------------------

    /**
     * Helper method that is called by all three golf ball clicks.
     */
    private void handleBallClickForLocation(final int location) {
        new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("What was the real color?").setItems(R.array.ball_colors,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GolfBallDeliveryActivity.this.setLocationToColor(location, BallColor.values()[which]);
                            }
                        });
                return builder.create();
            }
        }.show(getFragmentManager(), "unused mTAG");
    }

    /**
     * Click to the far left image button (Location 1).
     */
    public void handleBallAtLocation1Click(View view) {
        handleBallClickForLocation(1);
    }

    /**
     * Click to the center image button (Location 2).
     */
    public void handleBallAtLocation2Click(View view) {
        handleBallClickForLocation(2);
    }

    /**
     * Click to the far right image button (Location 3).
     */
    public void handleBallAtLocation3Click(View view) {
        handleBallClickForLocation(3);
    }

    /**
     * Sets the mOnRedTeam boolean value as appropriate
     * Side effects: Clears the balls
     *
     * @param view
     */
    public void handleTeamChange(View view) {
        setLocationToColor(1, BallColor.NONE);
        setLocationToColor(2, BallColor.NONE);
        setLocationToColor(3, BallColor.NONE);
        if (mOnRedTeam) {
            mOnRedTeam = false;
            mTeamChangeButton.setBackgroundResource(R.drawable.blue_button);
            mTeamChangeButton.setText("Team Blue");
            logToDebugWindow(mTAG, "Changed to team blue");
        } else {
            mOnRedTeam = true;
            mTeamChangeButton.setBackgroundResource(R.drawable.red_button);
            mTeamChangeButton.setText("Team Red");
            logToDebugWindow(mTAG, "Changed to team red");
        }
        // setTeamToRed(mOnRedTeam); // This call is optional. It will reset your GPS and sensor heading values.
    }


    /**
     * Sends a message to Arduino to perform a ball color test.
     * DONE
     */
    public void handlePerformBallTest(View view) {
        // DONE: Send command to arduino to get ball colorz
        read_1 = false;
        read_2 = false;
        read_3 = false;
        if (cal_state == CalibrationStatus.CALIBRATED) {
            logToDebugWindow(mTAG, "Reading Stand...");
            dropped1 = false;
            dropped2 = false;
            dropped3 = false;
            sendCommand("ATTACH 111111");
            sendCommand(getString(R.string.gripper_command, 50));

            sendCommand("CUSTOM Q1");
            mCommandHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendCommand("CUSTOM Q2");
                }
            }, 500);
            mCommandHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendCommand("CUSTOM Q3");
                }
            }, 1000);
        } else if (cal_state == CalibrationStatus.NOW_CALIBRATING) {
            logToDebugWindow(mTAG, "Running ball calibration...");
            sendCommand("CUSTOM Q1");
            mCommandHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendCommand("CUSTOM Q2");
                }
            }, 500);
            mCommandHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendCommand("CUSTOM Q3");
                }
            }, 1000);
        } else if (cal_state == CalibrationStatus.NOT_CALIBRATED) {
            Toast.makeText(GolfBallDeliveryActivity.this, "Please calibrate before continuing...", Toast.LENGTH_SHORT).show();
            logToDebugWindow(mTAG, "Refusing ball test: Not calibrated");
            mViewFlipper.setDisplayedChild(3); //Jump to third view
        }
    }

    /**
     * Done
     *
     * @param receivedCommand
     */
    @Override
    protected void onCommandReceived(String receivedCommand) {
        super.onCommandReceived(receivedCommand);
        //Toast.makeText(GolfBallDeliveryActivity.this, receivedCommand, Toast.LENGTH_SHORT).show();
        logToDebugWindow(mTAG, "Recieved:"+receivedCommand);
        int startIndex = receivedCommand.indexOf('[');
        int endIndex = receivedCommand.indexOf(']');
        if (startIndex == -1 || endIndex == -1) {
            Toast.makeText(GolfBallDeliveryActivity.this, "Error! Malformed data", Toast.LENGTH_SHORT).show();
            logToDebugWindow(mTAG, "Malformed Data " + receivedCommand);
            return;
        }
        String data = receivedCommand.substring(startIndex + 1, endIndex - 1);
        String[] splitData = data.split(",");
        int i = Integer.parseInt(splitData[0]); // Location 1 is 0 = :)

        switch (cal_state) {
            case CALIBRATED:
                int bestScore = 999999;
                int score;
                int scoredColor = -1;
                List<GolfBallObject> gbs = new ArrayList<GolfBallObject>();
                for (int color = 0; color < 7; color++) {
                    score = 0;
                    for (int j = 1; j < splitData.length - 1; j++) { // data[0] and data[6] are not important here.
                        score += Math.abs(calibrationData[color][j][i] - Integer.parseInt(splitData[j]));

                    }
                    if (bestScore > score) {
                        bestScore = score;
                        scoredColor = color;
                    }
                    gbs.add(new GolfBallObject(indexToColor(color), score));
                }
                Collections.sort(gbs);
                setDebugScore(i, bestScore, gbs);
                setBallPos(scoredColor, i + 1); // The GUI is 1-based index for reasons.

                break;
            case NOT_CALIBRATED:
                Toast.makeText(GolfBallDeliveryActivity.this, "Discarding data: not calibrated", Toast.LENGTH_SHORT).show();
                break;
            case NOW_CALIBRATING:
                //RECALL:    calibrationData[][][] = new int[7][7][3]; //  7 colors, 7 data values, 3 sensors.
                // idMatrix is a vector of [ loc1color, loc2color, loc3color ] for this calibration smTAGe.
                for (int j = 0; j < splitData.length; j++) {
                    calibrationData[idMatrix[i]][j][i] = Integer.parseInt(splitData[j]);
                }
                // Auto-next smTAGe
                if ((i == 2)) {
                    logToDebugWindow(mTAG, "Auto-Next calibration smTAGe");
                    handleCalibration(null);
                }
                break;
        }

    }

    private void setDebugScore(int pos, int score, List<GolfBallObject> gbs) {
        switch (pos) {
            case 0:
                debug_score_1 = score;
                debug_second_1 = gbs.get(1);
                m_text_debug_1.setText(score + "");
                break;
            case 1:
                debug_score_2 = score;
                debug_second_2 = gbs.get(1);
                m_text_debug_2.setText(score + "");
                break;
            case 2:
                debug_score_3 = score;
                debug_second_3 = gbs.get(1);
                m_text_debug_3.setText(score + "");
                break;
            default:
                break;
        }



    }



    /**
     * Clicks to the red arrow image button that should show a dialog window.
     */
    public void handleDrivingStraight(View view) {
//        Toast.makeText(this, "handleDrivingStraight", Toast.LENGTH_SHORT).show();
        new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Driving Straight Calibration");
                View dialoglayout = getLayoutInflater().inflate(R.layout.driving_straight_dialog, (ViewGroup) getCurrentFocus());
                builder.setView(dialoglayout);
                final NumberPicker rightDutyCyclePicker = (NumberPicker) dialoglayout.findViewById(R.id.right_pwm_number_picker);
                rightDutyCyclePicker.setMaxValue(255);
                rightDutyCyclePicker.setMinValue(0);
                rightDutyCyclePicker.setValue(mRightStraightPwmValue);
                rightDutyCyclePicker.setWrapSelectorWheel(false);
                final NumberPicker leftDutyCyclePicker = (NumberPicker) dialoglayout.findViewById(R.id.left_pwm_number_picker);
                leftDutyCyclePicker.setMaxValue(255);
                leftDutyCyclePicker.setMinValue(0);
                leftDutyCyclePicker.setValue(mLeftStraightPwmValue);
                leftDutyCyclePicker.setWrapSelectorWheel(false);
                Button doneButton = (Button) dialoglayout.findViewById(R.id.done_button);
                doneButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLeftStraightPwmValue = leftDutyCyclePicker.getValue();
                        mRightStraightPwmValue = rightDutyCyclePicker.getValue();
                        dismiss();
                    }
                });
                final Button testStraightButton = (Button) dialoglayout.findViewById(R.id.test_straight_button);
                testStraightButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLeftStraightPwmValue = leftDutyCyclePicker.getValue();
                        mRightStraightPwmValue = rightDutyCyclePicker.getValue();
//                        mScripts.testStraightDriveScript();
                    }
                });
                return builder.create();
            }
        }.show(getFragmentManager(), "unused mTAG");
    }

    /**
     * Test GPS point when going to the Far ball (assumes Blue Team heading to red ball).
     */
    public void handleFakeGpsF0(View view) {
        onLocationChanged(165, 50, NO_HEADING, null); // Midfield
    }

    public void handleFakeGpsF1(View view) {
        onLocationChanged(209, 50, 0, null);  // Out of range so ignored.
    }

    public void handleFakeGpsF2(View view) {
        onLocationChanged(231, 50, 135, null);  // Within range
    }

    public void handleFakeGpsF3(View view) {
        onLocationChanged(240, 41, 35, null);  // Within range
    }

    public void handleFakeGpsH0(View view) {
        onLocationChanged(165, 0, -180, null); // Midfield
    }

    public void handleFakeGpsH1(View view) {
        onLocationChanged(11, 0, -180, null);  // Out of range so ignored.
    }

    public void handleFakeGpsH2(View view) {
        onLocationChanged(9, 0, -170, null);  // Within range
    }

    public void handleFakeGpsH3(View view) {
        onLocationChanged(0, -9, -170, null);  // Within range
    }

    public void handleGoOrMissionComplete(View view) {
        if (mState == State.READY_FOR_MISSION) {
            mMatchStartTime = System.currentTimeMillis();
            updateMissionStrategyVariables();
            mGoOrMissionCompleteButton.setBackgroundResource(R.drawable.red_button);
            mGoOrMissionCompleteButton.setText("Mission Complete!");
            mJumbotron_button.setBackgroundResource(R.drawable.red_button);
            mJumbotron_button.setText("STOP");
            setState(State.DRIVE_TOWARDS_NEAR_BALL);
            clearDebugWindow();
            logToDebugWindow(mTAG, "GO: Per HandleGoOrMissionComplete");

        } else {
            setState(State.READY_FOR_MISSION);
            logToDebugWindow(mTAG, "Ready for misssion: Per HandleGoOrMissionComplete");
        }
    }

    /**
     * Field Orientation sensor updates.
     */
    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {
        super.onSensorChanged(fieldHeading, orientationValues);
        mSensorOrientationTextView.setText(getString(R.string.degrees_format, fieldHeading));
    }

    public void handleZeroHeading(View view) {
        mFieldOrientation.setCurrentFieldHeading(0);
    }

    public void setState(State newState) {
        logToDebugWindow(mTAG, "Setting state to:"+ newState.name());
        mTAG = newState.toString();
        mStateStartTime = System.currentTimeMillis();
        // Make sure when the match ends that no scheduled timer events from scripts change the FSM.
        if (mState == State.READY_FOR_MISSION && newState != State.DRIVE_TOWARDS_NEAR_BALL) {
//            Toast.makeText(this, "Illegal state transition out of READY_FOR_MISSION", Toast.LENGTH_SHORT).show();
//            return;
        }
        mCurrentStateTextView.setText(newState.name());
        mJumboStateView.setText(newState.name());
        speak(newState.name().replace("_", " "));
        switch (newState) {
            case READY_FOR_MISSION:
                mGoOrMissionCompleteButton.setBackgroundResource(R.drawable.green_button);
                mGoOrMissionCompleteButton.setText("Go!");
                mJumbotron_button.setBackgroundResource(R.drawable.green_button);
                mJumbotron_button.setText("Go!");
                sendWheelSpeed(0, 0);
                break;
            case DRIVE_TOWARDS_NEAR_BALL:
                mGpsInfoTextView.setText("---"); // Clear GPS display (optional)
                mGuessXYTextView.setText("---"); // Clear guess display (optional)
//                mScripts.nearBallScript();

                //Re-obtain access to our flipper
                ViewFlipper flipper = (ViewFlipper) findViewById(R.id.my_view_flipper);
                flipper.setDisplayedChild(2);
                break;
            case NEAR_IMAGE_REC:
                sendWheelSpeed(0, 0);
                break;
            case DROP_NEAR:
                sendWheelSpeed(0, 0);
                break;
            case CHECK_DROPPED_NEAR:
                sendWheelSpeed(0, 0);
                break;
            case DRIVE_TOWARDS_FAR_BALL:
                // All actions handled in the loop function.
                sendWheelSpeed(0, 0);
                break;
            case FAR_IMAGE_REC:
//                mScripts.farBallScript();
                sendWheelSpeed(0, 0);
                break;
            case DROP_FAR:
                sendWheelSpeed(0, 0);
                break;
            case CHECK_DROPPED_FAR:
                sendWheelSpeed(0, 0);
                break;
            case DROP_WHITE:
                sendWheelSpeed(0, 0);
                break;
            case CHECK_DROPPED_WHITE:
                sendWheelSpeed(0, 0);
                break;
            case DRIVE_TOWARDS_HOME:
                sendWheelSpeed(0, 0);
                // All actions handled in the loop function.
                break;
            case WAITING_FOR_PICKUP:
                sendWheelSpeed(0, 0);
                break;
            case SEEKING_HOME:
                sendWheelSpeed(0, 0);
                // Actions handled in the loop function.
                break;
        }
        mState = newState;
    }

    /*
    Calibration Functions
     */
        /*
        Color Ids:
    0   none
    1   black
    2   white
    3   red
    4   green
    5   yellow
    6   blue
     */
    private BallColor indexToColor(int colorIndex) {
        switch (colorIndex) {
            case 0:
                return BallColor.NONE;
            case 1:
                return BallColor.BLACK;
            case 2:
                return BallColor.WHITE;
            case 3:
                return BallColor.RED;
            case 4:
                return BallColor.GREEN;
            case 5:
                return BallColor.YELLOW;
            case 6:
                return BallColor.BLUE;
            default:
                return BallColor.NONE; //Should be Error, but whatever.
        }
    }

    public void handleLoad(View view) {
        loadCalibrationData();
    }


    public void handleCalibration(View view) {
        if (cal_state != CalibrationStatus.NOW_CALIBRATING) {
            loadCalibration.setEnabled(false);
            calibrateButton.setVisibility(View.GONE);
            cal_state = CalibrationStatus.NOW_CALIBRATING;
            Toast.makeText(GolfBallDeliveryActivity.this, "Now Calibrating!", Toast.LENGTH_SHORT).show();
            logToDebugWindow(mTAG, "Now Calibrating!");
            debugTestBalls.setBackgroundResource(R.drawable.green_button);
            idMatrix = new int[]{0, 0, 0};
            cal_smTAGe = 0; // Ready to start now.
            nextCalibrationSmTAGe();
            //onCommandReceived("[0,0,0,0,0,0,0]"); //DEBUG
        } else {
            nextCalibrationSmTAGe();
        }


    }

    // Calibration pattern:
    /*
    1        None    None    None
    2        Black   White   Red
    3        White   Red     Black
    4        Red     Black   White
    5        Green   Yellow  Blue
    6        Yellow  Blue    Green
    7        Blue    Green   Yellow

    Color Ids:
    0   none
    1   black
    2   white
    3   red
    4   green
    5   yellow
    6   blue
     */
    private void nextCalibrationSmTAGe() {
        cal_smTAGe++;
        switch (cal_smTAGe) {
            case 1:
                //Toast.makeText(GolfBallDeliveryActivity.this,"Set: None, None, None", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{0, 0, 0};
                calibrateButton.setText("Next 6");
                m_text_debug_1.setText("None");
                m_text_debug_2.setText("None");
                m_text_debug_3.setText("None");
                break;
            case 2:
                //Toast.makeText(GolfBallDeliveryActivity.this,"Set: Black, White, Red", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{1, 2, 3};
                calibrateButton.setText("Next 5");
                m_text_debug_1.setText("Black");
                m_text_debug_2.setText("White");
                m_text_debug_3.setText("Red");
                break;
            case 3:
                //Toast.makeText(GolfBallDeliveryActivity.this,"Set: White, Red, Black", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{2, 3, 1};
                calibrateButton.setText("Next 4");
                m_text_debug_1.setText("White");
                m_text_debug_2.setText("Red");
                m_text_debug_3.setText("Black");

                break;
            case 4:
                //Toast.makeText(GolfBallDeliveryActivity.this,"Set: Red, Black, White", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{3, 1, 2};
                calibrateButton.setText("Next 3");
                m_text_debug_1.setText("Red");
                m_text_debug_2.setText("Black");
                m_text_debug_3.setText("White");
                break;
            case 5:
                //Toast.makeText(GolfBallDeliveryActivity.this,"Set: Green, Yellow, Blue", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{4, 5, 6};
                calibrateButton.setText("Next 2");
                m_text_debug_1.setText("Green");
                m_text_debug_2.setText("Yellow");
                m_text_debug_3.setText("Blue");
                break;
            case 6:
                //Toast.makeText(GolfBallDeliveryActivity.this,"Set: Yellow, Blue, Green", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{5, 6, 4};
                calibrateButton.setText("Next 1");
                m_text_debug_1.setText("Yellow");
                m_text_debug_2.setText("Blue");
                m_text_debug_3.setText("Green");
                break;
            case 7:
                //Toast.makeText(GolfBallDeliveryActivity.this,"Set: Blue, Green, Yellow", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{6, 4, 5};

                calibrateButton.setText("Confirm");
                m_text_debug_1.setText("Blue");
                m_text_debug_2.setText("Green");
                m_text_debug_3.setText("Yellow");
                break;
            default:
                Toast.makeText(GolfBallDeliveryActivity.this, "Done Calibrating!", Toast.LENGTH_SHORT).show();
                logToDebugWindow(mTAG, "Done Calibrating!");
                cal_smTAGe = -1;
                cal_state = CalibrationStatus.CALIBRATED;
                saveCalibrationData();
                loadCalibration.setEnabled(true);
                calibrateButton.setVisibility(View.VISIBLE);
                calibrateButton.setText("Recalibrate");
                debugTestBalls.setBackgroundResource(R.drawable.black_button);
                m_text_debug_1.setText("-");
                m_text_debug_2.setText("-");
                m_text_debug_3.setText("-");
                break;
        }
    }


    private void saveCalibrationData() {
        try {
            outputStream = new ObjectOutputStream(openFileOutput(filename, Context.MODE_PRIVATE));
            outputStream.writeObject(calibrationData);
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(GolfBallDeliveryActivity.this, "Could not save data", Toast.LENGTH_SHORT).show();
            logToDebugWindow(mTAG, "Could not save data");
            e.printStackTrace();
        }
    }

    private void loadCalibrationData() {
        try {
            loadCalibration.setEnabled(false);
            inputStream = new ObjectInputStream(openFileInput(filename));
            calibrationData = (int[][][]) inputStream.readObject();
            inputStream.close();
            cal_state = CalibrationStatus.CALIBRATED;
            loadCalibration.setText("Reload");
            Toast.makeText(GolfBallDeliveryActivity.this, "Calibration Loaded", Toast.LENGTH_SHORT).show();
            logToDebugWindow(mTAG, "Calibration Loaded");
            loadCalibration.setEnabled(true);
        } catch (Exception e) {
            loadCalibration.setText("FAILED");
            Toast.makeText(GolfBallDeliveryActivity.this, "Could not load data", Toast.LENGTH_SHORT).show();
            logToDebugWindow(mTAG, "Could not load data");
            e.printStackTrace();
            cal_state = CalibrationStatus.NOT_CALIBRATED;
        }
    }

    /*
    Our own "Setting ball positions"
     */
    private void setBallPos(int colorIndex, int index) {
        BallColor ballColor = indexToColor(colorIndex);
        setBallPos(ballColor, index);
    }

    private void setBallPos(BallColor ballColor, int index) {
        // Updates the positions on screen
        setLocationToColor(index, ballColor);

        if (ballColor == BallColor.NONE) {
            // Debug toast. 
            switch (index) {
                case 1:
                    dropped1 = true;
                    break;
                case 2:
                    dropped2 = true;
                    break;
                case 3:
                    dropped3 = true;
                    break;
            }
        }

        switch (index) {
            case 1:
                read_1 = true;
                break;
            case 2:
                read_2 = true;
                break;
            case 3:
                read_3 = true;
                break;
        }
        if (read_1 && read_2 && read_3) {
            logToDebugWindow(mTAG, "Starting logic test");
            preformLogicTest();

        }

    }
    private void preformLogicTest() {
        // Duplicate checking. RECALL: A lower score = better candidate
        boolean passed = true;
        if (mLocationColors[0] == mLocationColors[1] && mLocationColors[0] != BallColor.NONE) {
            logToDebugWindow(mTAG, "1 and 2 conflict");
            passed = false;
            if (debug_score_1 > debug_score_2) {
                debug_score_1 = debug_second_1.score;
                setBallPos(debug_second_1.color, 1);

            } else {
                debug_score_2 = debug_second_2.score;
                setBallPos(debug_second_2.color, 2);
            }
        }
        if (mLocationColors[1] == mLocationColors[2] && mLocationColors[1] != BallColor.NONE) {
            logToDebugWindow(mTAG, "2 and 3 conflict");
            passed = false;
            if (debug_score_2 > debug_score_3) {
                debug_score_2 = debug_second_2.score;
                setBallPos(debug_second_2.color, 2);
            } else {
                debug_score_3 = debug_second_3.score;
                setBallPos(debug_second_3.color, 3);
            }

        }
        if (mLocationColors[0] == mLocationColors[2] && mLocationColors[0] != BallColor.NONE) {
            logToDebugWindow(mTAG, "1 and 3 conflict");
            passed = false;
            if (debug_score_1 > debug_score_3) {
                debug_score_1 = debug_second_1.score;
                setBallPos(debug_second_1.color, 1);
            } else {
                debug_score_3 = debug_second_3.score;
                setBallPos(debug_second_3.color, 3);
            }

        }
        if (passed){
            logToDebugWindow(mTAG, "PASS");

        }
        logToDebugWindow(mTAG, "Logic test done");
    }


    public void registerDebugWindow(TextView tv, ScrollView sv){
        mDebugLogger = tv;
        mDebugScroller = sv;
        tv.setText("Opened Logger \n");
        mHasDebugTextview = true;
    }
    public void scrollToBottom()
    {
        if (mHasDebugTextview) {
            mDebugScroller.post(new Runnable() {
                public void run() {
                    mDebugScroller.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }

    public void logToDebugWindow(String mTAG, String message){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        if (mHasDebugTextview) {
            // Clears the window down to 50 lines if you have more than 100
            int index = indexOfLastLineX(mDebugLogger.getText().toString(), 100);
            if (index > 0){
                String t = mDebugLogger.getText().toString().substring(index);
                mDebugLogger.setText("");
                mDebugLogger.setText(t);
            }
            // End clears
            mDebugLogger.append(currentDateandTime + "|" +mTAG +">"+message + '\n');
            scrollToBottom();
        }
        Log.d(mTAG, message);
    }
    public void clearDebugWindow(){
        if ((mHasDebugTextview) && (!mFirstRun)){
            mFirstRun = false;
            mDebugLogger.setText("");
        }
    }
    /*
    For a window with X lines, returns the char index at line X/2. Useful for only occasionally clearing a debug window
     */
    public static int indexOfLastLineX(String str, int X) {
        if(str == null || str.isEmpty())
        {
            return 0;
        }
        int lines = 1;
        int pos = 0;
        while ((pos = str.lastIndexOf("\n", pos) + 1) != 0) {
            lines++;
            if (lines > (X)){
                return pos / 2;
            }
        }
        return 0;
    }
}
