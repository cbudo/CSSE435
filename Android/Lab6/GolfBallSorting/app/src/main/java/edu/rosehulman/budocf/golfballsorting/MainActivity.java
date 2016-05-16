package edu.rosehulman.budocf.golfballsorting;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.rosehulman.budocf.golfballsorting.me435.RobotActivity;

public class MainActivity extends RobotActivity {
    TextView ball1, ball2, ball3;
    private boolean dropped1, dropped2, dropped3;
    private int RGLoc, WLoc, BYLoc;
    private ObjectInputStream inputStream;


    /**
     * An enum used for calibration
     */
    private enum CalibrationStatus {
        CALIBRATED, NOT_CALIBRATED, NOW_CALIBRATING
    }

    private CalibrationStatus cal_state;
    private int cal_stage = -1;
    private Button calibrateButton, loadCalibration;

    String filename = "cal.data";
    ObjectOutputStream outputStream;


    private int calibrationData[][][] = new int[7][7][3]; //  7 colors, 7 readings, 3 sensors.
    private int idMatrix[] = new int[3];
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
        cal_state = CalibrationStatus.NOT_CALIBRATED;
        calibrateButton = (Button)findViewById(R.id.calibrateButton);
        loadCalibration = (Button)findViewById(R.id.loadCalibration);
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
        if (cal_state == CalibrationStatus.CALIBRATED) {
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
        } else if (cal_state == CalibrationStatus.NOW_CALIBRATING){
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
        }
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

    private void setBallPos(int colorIndex, int index){
        String ballColor = indexToColor(colorIndex);
        setBallPos(ballColor, index);
    }

    private void setBallPos(String ballColor, int index) {

       switch (index) {
            case 1:
                ball1.setText(ballColor);
                break;
            case 2:
                ball2.setText(ballColor);
                break;
            case 3:
                ball3.setText(ballColor);
                break;
        }
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
    private String indexToColor(int colorIndex) {
        switch(colorIndex){
            case 0:
                return "None";
            case 1:
                return "Black";
            case 2:
                return "White";
            case 3:
                return "Red";
            case 4:
                return "Green";
            case 5:
                return "Yellow";
            case 6:
                return "Blue";
            default:
                return "None"; //Should be Error, but whatever.
        }
    }

    public void handleLoad(View view){
        loadCalibrationData();
    }

    public void handleCalibration(View view) {
        if (cal_state != CalibrationStatus.NOW_CALIBRATING) {
            cal_state = CalibrationStatus.NOW_CALIBRATING;
            Toast.makeText(MainActivity.this, "Now Calibrating!", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Set: None, None, None", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Then, click Test Colors", Toast.LENGTH_SHORT).show();
            calibrateButton.setText("Next 6");
            idMatrix = new int[]{0, 0, 0};
            cal_stage = 1;
            //onCommandReceived("[0,0,0,0,0,0,0]"); //DEBUG
        } else {
            nextCalibrationStage();
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
    private void nextCalibrationStage() {
        cal_stage++;
        switch (cal_stage) {
            case 1:
                //You should never be here, but this is here just for show
                Toast.makeText(MainActivity.this,"Set: None, None, None", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{0, 0, 0};
                calibrateButton.setText("Next 6");
                break;
            case 2:
                Toast.makeText(MainActivity.this,"Set: Black, White, Red", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{1, 2, 3};
                calibrateButton.setText("Next 5");
                break;
            case 3:
                Toast.makeText(MainActivity.this,"Set: White, Red, Black", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{2, 3, 1};
                calibrateButton.setText("Next 4");
                break;
            case 4:
                Toast.makeText(MainActivity.this,"Set: Red, Black, White", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{3, 1, 2};
                calibrateButton.setText("Next 3");
                break;
            case 5:
                Toast.makeText(MainActivity.this,"Set: Green, Yellow, Blue", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{4, 5, 6};
                calibrateButton.setText("Next 2");
                break;
            case 6:
                Toast.makeText(MainActivity.this,"Set: Yellow, Blue, Green", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{5, 6, 4};
                calibrateButton.setText("Next 1");
                break;
            case 7:
                Toast.makeText(MainActivity.this,"Set: Blue, Green, Yellow", Toast.LENGTH_SHORT).show();
                idMatrix = new int[]{6, 4, 5};
                calibrateButton.setText("Confirm");
                break;
            default:
                Toast.makeText(MainActivity.this, "Done Calibrating!", Toast.LENGTH_SHORT).show();
                cal_stage = -1;
                cal_state = CalibrationStatus.CALIBRATED;
                saveCalibrationData();
                break;
        }
    }

    private void saveCalibrationData() {
        try {
            outputStream = new ObjectOutputStream(openFileOutput(filename, Context.MODE_PRIVATE));
            outputStream.writeObject(calibrationData);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadCalibrationData(){
        try {
            inputStream = new ObjectInputStream(openFileInput(filename));
            calibrationData = (int[][][])inputStream.readObject();
            inputStream.close();
            cal_state = CalibrationStatus.CALIBRATED;
            loadCalibration.setText("Reload");
        } catch (Exception e) {
            e.printStackTrace();
            cal_state = CalibrationStatus.NOT_CALIBRATED;
        }
    }


    @Override
    protected void onCommandReceived(String receivedCommand) {
        super.onCommandReceived(receivedCommand);
        Toast.makeText(MainActivity.this, receivedCommand, Toast.LENGTH_SHORT).show();
        int startIndex = receivedCommand.indexOf('[');
        int endIndex = receivedCommand.indexOf(']');
        if (startIndex == -1 || endIndex == -1){
            Toast.makeText(MainActivity.this, "Error! Malformed data", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Malformed Data "+receivedCommand);
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
                for (int color = 0; color < 7; color++) {
                    score = 0;
                    for (int j = 1; j < splitData.length - 1; j++) { // data[0] and data[6] are not important here.
                        score += Math.abs(calibrationData[color][j][i] - Integer.parseInt(splitData[j]));
                    }
                    if (bestScore > score){
                        bestScore = score;
                        scoredColor = color;
                    }
                }
                setBallPos(scoredColor, i+1); // The GUI is 1-based index for reasons.
                break;
            case NOT_CALIBRATED:
                Toast.makeText(MainActivity.this, "Discarding data: not calibrated", Toast.LENGTH_SHORT).show();
                break;
            case NOW_CALIBRATING:
                //RECALL:    calibrationData[][][] = new int[7][7][3]; //  7 colors, 7 data values, 3 sensors.
                // idMatrix is a vector of [ loc1color, loc2color, loc3color ] for this calibration stage.
                for (int j = 0; j < splitData.length; j++) {
                    calibrationData[idMatrix[i]][j][i] = Integer.parseInt(splitData[j]);
                }
                break;
        }

    }
}
