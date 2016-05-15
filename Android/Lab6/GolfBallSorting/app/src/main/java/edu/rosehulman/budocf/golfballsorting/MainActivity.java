package edu.rosehulman.budocf.golfballsorting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.rosehulman.budocf.golfballsorting.me435.RobotActivity;

public class MainActivity extends RobotActivity {
    TextView ball1, ball2, ball3;
    private boolean dropped1, dropped2, dropped3;
    private int RGLoc, WLoc, BYLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ball1 = (TextView) findViewById(R.id.ball1_textView);
        ball2 = (TextView) findViewById(R.id.ball2_textView);
        ball3 = (TextView) findViewById(R.id.ball3_textView);
        dropped1 = false;
        dropped2 = false;
        dropped3 = false;
    }

    public void clear(View view) {
        ball1.setText("---");
        ball2.setText("---");
        ball3.setText("---");
        sendCommand("R");
        sendCommand("ATTACH 111111");
        sendCommand(getString(R.string.gripper_command, 50));
        sendCommand(getString(R.string.position_command, 0, 90, 0, -180, 90));
    }

    public void changeColor(View view) {
        String buttonName = getResources().getResourceEntryName(view.getId());
        Log.wtf(TAG, "changeColor: " + buttonName);
        String buttonColor = buttonName.substring(0, buttonName.length() - 1);
        Toast.makeText(MainActivity.this, buttonColor, Toast.LENGTH_SHORT).show();
        switch (buttonName.charAt(buttonName.length() - 1)) {
            case '1':
                ball1.setText(capitalize(buttonColor));
                setBallPos(capitalize(buttonColor), 1);
                break;
            case '2':
                ball2.setText(capitalize(buttonColor));
                setBallPos(capitalize(buttonColor), 2);
                break;
            case '3':
                ball3.setText(capitalize(buttonColor));
                setBallPos(capitalize(buttonColor), 3);
                break;
        }
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public void positionTest(View view) {
        // DONE: Send command to arduino to get ball colorz
        sendCommand("ATTACH 111111");
        sendCommand(getString(R.string.gripper_command, 50));
        dropped1 = false;
        dropped2 = false;
        dropped3 = false;
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
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand("CUSTOM QX");
            }
        }, 1500);
    }

    public void goClick(View view) {
        // based on colors, move forward and knock balls off
        // drop of yellow/blue
        sendCommand("ATTACH 111111");
        sendCommand(getString(R.string.gripper_command, 50));
        dropOffBY();
        // drive forward 1 sec
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.wheel_speed_command, "FORWARD", 150, "FORWARD", 150));
            }
        }, 4000);
        // drop of white (if there)
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // brake
                sendCommand(getString(R.string.wheel_speed_command, "BRAKE", 0, "BRAKE", 0));
                // knock off white
                dropOffW();
                // drive forward 1 sec
            }
        }, 5500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.wheel_speed_command, "FORWARD", 150, "FORWARD", 150));
            }
        }, 10000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // brake
                sendCommand(getString(R.string.wheel_speed_command, "BRAKE", 0, "BRAKE", 0));
                // knock off green/red
                dropOffRG();
            }
        }, 11000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.wheel_speed_command, "REVERSE", 150, "REVERSE", 150));
            }
        }, 15000);
        // drop of green/red
        // drive backward 2 sec
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // brake
                sendCommand(getString(R.string.wheel_speed_command, "BRAKE", 0, "BRAKE", 0));
                Toast.makeText(MainActivity.this, getString(R.string.droppedStat, dropped1 + "", dropped2 + "", dropped3 + ""), Toast.LENGTH_LONG).show();
            }
        }, 17000);
    }

    private void dropOffBY() {
        if (BYLoc == 1) {
            Toast.makeText(this, "Dropping of BY from 1", Toast.LENGTH_SHORT).show();
            drop1Script();
        } else if (BYLoc == 2) {
            Toast.makeText(this, "Dropping of BY from 2", Toast.LENGTH_SHORT).show();
            drop2Script();
        } else if (BYLoc == 3) {
            Toast.makeText(this, "Dropping of BY from 3", Toast.LENGTH_SHORT).show();
            drop3Script();
        }
    }

    private void dropOffW() {
        if (WLoc == 1) {
            Toast.makeText(this, "Dropping of W from 1", Toast.LENGTH_SHORT).show();
            drop1Script();
        } else if (WLoc == 2) {
            Toast.makeText(this, "Dropping of W from 2", Toast.LENGTH_SHORT).show();
            drop2Script();
        } else if (WLoc == 3) {
            Toast.makeText(this, "Dropping of W from 3", Toast.LENGTH_SHORT).show();
            drop3Script();
        }
    }

    private void dropOffRG() {
        if (RGLoc == 1) {
            Toast.makeText(this, "Dropping of RG from 1", Toast.LENGTH_SHORT).show();
            drop1Script();
        } else if (RGLoc == 2) {
            Toast.makeText(this, "Dropping of RG from 2", Toast.LENGTH_SHORT).show();
            drop2Script();
        } else if (RGLoc == 3) {
            Toast.makeText(this, "Dropping of RG from 3", Toast.LENGTH_SHORT).show();
            drop3Script();
        }
    }

    public void drop1Script(View view) {
        sendCommand("ATTACH 111111");
        drop1Script();
    }

    public void drop2Script(View view) {
        sendCommand("ATTACH 111111");
        drop2Script();
    }

    public void drop3Script(View view) {
        sendCommand("ATTACH 111111");
        drop3Script();
    }


    public void drop1Script() {
        sendCommand(getString(R.string.position_command, 33, 87, 80, -67, 157));
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 33, 87, 80, 0, 157));
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 33, 80, 80, 0, 157));
            }
        }, 1500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 50, 87, 80, -67, 157));
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 33, 87, 80, -67, 157));
                sendCommand("CUSTOM Q1");
            }
        }, 3000);
    }

    public void drop2Script() {
        sendCommand(getString(R.string.position_command, 3, 77, 81, -54, 153));
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 3, 77, 81, -54, 153));
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 3, 78, 83, -25, 152));
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 3, 47, 83, -25, 153));
            }
        }, 2500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, 3, 77, 81, -54, 153));
                sendCommand("CUSTOM Q2");
            }
        }, 3000);
    }

    public void drop3Script() {
        sendCommand(getString(R.string.position_command, -20, 82, 80, -67, 157));
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                sendCommand(getString(R.string.position_command, -20, 82, 80, -67, 157));
            }
        }, 1000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                sendCommand(getString(R.string.position_command, -20, 82, 80, 0, 157));
            }
        }, 1500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                sendCommand(getString(R.string.position_command, -20, 73, 80, 0, 157));
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                sendCommand(getString(R.string.position_command, -20, 82, 80, -67, 157));
            }
        }, 2500);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCommand(getString(R.string.position_command, -20, 82, 80, -67, 157));
                sendCommand("CUSTOM Q3");
            }
        }, 3000);
    }

    private void setBallPos(String ballColor, int index) {
        if (ballColor.equals("Red") || ballColor.equals("Green"))
            RGLoc = index;
        else if (ballColor.equals("White"))
            WLoc = index;
        else if (ballColor.equals("Blue") || ballColor.equals("Yellow"))
            BYLoc = index;
        else {
            Toast.makeText(MainActivity.this, "Ball Dropped off", Toast.LENGTH_SHORT).show();
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
    }

    private String getBallColor(char c) {
        switch (c) {
            case 'N':
                return "None";
            case 'K':
                return "Black";
            case 'B':
                return "Blue";
            case 'G':
                return "Green";
            case 'R':
                return "Red";
            case 'Y':
                return "Yellow";
            case 'W':
                return "White";
        }
        return "None";
    }

    @Override
    protected void onCommandReceived(String receivedCommand) {
        super.onCommandReceived(receivedCommand);
        for (String s :
                receivedCommand.split(",")) {
            String ballColor = getBallColor(s.charAt(0));
            switch (s.charAt(1)) {
                case '1':
                    ball1.setText(ballColor);
                    setBallPos(ballColor, 1);
                    break;
                case '2':
                    ball2.setText(ballColor);
                    setBallPos(ballColor, 2);
                    break;
                case '3':
                    ball3.setText(ballColor);
                    setBallPos(ballColor, 3);
                    break;
            }
        }
    }
}
