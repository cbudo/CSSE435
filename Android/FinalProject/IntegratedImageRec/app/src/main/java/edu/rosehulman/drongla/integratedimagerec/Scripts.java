package edu.rosehulman.drongla.integratedimagerec;


import android.os.Handler;
import android.widget.Toast;

import edu.rosehulman.me435.NavUtils;
import edu.rosehulman.me435.RobotActivity;

public class Scripts {
    private int RGLoc, WLoc, BYLoc;

    /**
     * Reference to the primary activity.
     */
    private GolfBallDeliveryActivity mGolfBallDeliveryActivity;

    /**
     * Handler used to create scripts in this class.
     */
    protected Handler mCommandHandler = new Handler();

    /**
     * Time in milliseconds needed to perform a ball removal.
     */
    private int ARM_REMOVAL_TIME_MS = 3000;

    /**
     * Simple constructor.
     */
    public Scripts(GolfBallDeliveryActivity golfBallDeliveryActivity) {
        mGolfBallDeliveryActivity = golfBallDeliveryActivity;
    }

    /**
     * Used to test your values for straight driving.
     */
    public void testStraightDriveScript() {
        Toast.makeText(mGolfBallDeliveryActivity, "Begin Short straight drive test at " +
                        mGolfBallDeliveryActivity.mLeftStraightPwmValue + "  " + mGolfBallDeliveryActivity.mRightStraightPwmValue,
                Toast.LENGTH_SHORT).show();
        mGolfBallDeliveryActivity.sendWheelSpeed(mGolfBallDeliveryActivity.mLeftStraightPwmValue, mGolfBallDeliveryActivity.mRightStraightPwmValue);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mGolfBallDeliveryActivity, "End Short straight drive test", Toast.LENGTH_SHORT).show();
                mGolfBallDeliveryActivity.sendWheelSpeed(0, 0);
            }
        }, 8000);
    }

    /**
     * Runs the script to drive to the near ball (perfectly straight) and drop it off.
     */
    public void nearBallScript() {
        Toast.makeText(mGolfBallDeliveryActivity, "Drive 103 ft to near ball.", Toast.LENGTH_SHORT).show();
        double distanceToNearBall = NavUtils.getDistance(15, 0, 90, 50);
        long driveTimeToNearBallMs = (long) (distanceToNearBall / RobotActivity.DEFAULT_SPEED_FT_PER_SEC * 1000);
        driveTimeToNearBallMs = 3000; // Make this mock script not take so long.
        mGolfBallDeliveryActivity.sendWheelSpeed(mGolfBallDeliveryActivity.mLeftStraightPwmValue, mGolfBallDeliveryActivity.mRightStraightPwmValue);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeBallAtLocation(mGolfBallDeliveryActivity.mNearBallLocation);
            }
        }, driveTimeToNearBallMs);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mGolfBallDeliveryActivity.mState == GolfBallDeliveryActivity.State.DRIVE_TOWARDS_NEAR_BALL) {
                    mGolfBallDeliveryActivity.setState(GolfBallDeliveryActivity.State.DRIVE_TOWARDS_FAR_BALL);
                }
            }
        }, driveTimeToNearBallMs + ARM_REMOVAL_TIME_MS);
    }


    /**
     * Script to drop off the far ball.
     */
    public void farBallScript() {
        mGolfBallDeliveryActivity.sendWheelSpeed(0, 0);
        Toast.makeText(mGolfBallDeliveryActivity, "Figure out which ball(s) to remove and do it.", Toast.LENGTH_SHORT).show();
        removeBallAtLocation(mGolfBallDeliveryActivity.mFarBallLocation);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mGolfBallDeliveryActivity.mWhiteBallLocation != 0) {
                    removeBallAtLocation(mGolfBallDeliveryActivity.mWhiteBallLocation);
                }
                if (mGolfBallDeliveryActivity.mState == GolfBallDeliveryActivity.State.FAR_IMAGE_REC) {
                    mGolfBallDeliveryActivity.setState(GolfBallDeliveryActivity.State.CHECK_DROPPED_FAR);
                }
            }
        }, ARM_REMOVAL_TIME_MS);
    }


    // -------------------------------- Arm script(s) ----------------------------------------

    /**
     * Removes a ball from the golf ball stand.
     */
    public void removeBallAtLocation(final int location) {
        // DONE: Replace with a script that might actually remove a ball. :)
        switch (location) {
            case 1:
                drop1Script();
                break;
            case 2:
                drop2Script();
                break;
            case 3:
                drop3Script();
                break;
            default:
                Toast.makeText(mGolfBallDeliveryActivity, "Not a location", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void drop1Script() {
        mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 33, 87, 80, -67, 157));
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 33, 87, 80, 0, 157));
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 33, 80, 80, 0, 157));
            }
        }, 1500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 50, 87, 80, -67, 157));
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 33, 87, 80, -67, 157));
                mGolfBallDeliveryActivity.sendCommand("CUSTOM Q1");
            }
        }, 3000);
    }

    public void drop2Script() {
        mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 3, 77, 81, -54, 153));
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 3, 77, 81, -54, 153));
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 3, 78, 83, -25, 152));
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 3, 47, 83, -25, 153));
            }
        }, 2500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, 3, 77, 81, -54, 153));
                mGolfBallDeliveryActivity.sendCommand("CUSTOM Q2");
            }
        }, 3000);
    }

    public void drop3Script() {
        mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, -20, 82, 80, -67, 157));
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, -20, 82, 80, 0, 157));
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, -20, 73, 80, 0, 157));
            }
        }, 1500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, -33, 73, 80, -67, 157));
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGolfBallDeliveryActivity.sendCommand(mGolfBallDeliveryActivity.getString(R.string.position_command, -20, 82, 80, -67, 157));
                mGolfBallDeliveryActivity.sendCommand("CUSTOM Q3");
            }
        }, 3000);
    }

}
