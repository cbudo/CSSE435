package edu.rosehulman.slidersandbuttons;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnSeekBarChangeListener {
    private ArrayList<SeekBar> mSeekBars = new ArrayList<SeekBar>();
    private TextView mJointAnglesTextView;
    private TextView mGripperDistanceTextView;
    private Handler mCommandHandler = new Handler(); // Used for scripts

    private static final String WHEEL_MODE_REVERSE = "REVERSE";
    private static final String WHEEL_MODE_BRAKE = "BRAKE";
    private static final String WHEEL_MODE_FORWARD = "FORWARD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mJointAnglesTextView = (TextView) findViewById(R.id.joint_angles_textview);
        mGripperDistanceTextView = (TextView) findViewById(R.id.gripper_distance_textview);
        mSeekBars.add((SeekBar)findViewById(R.id.gripper_seek_bar)); // Gripper index 0.
        mSeekBars.add((SeekBar)findViewById(R.id.joint_1_seek_bar)); // Joints index 1-5
        mSeekBars.add((SeekBar)findViewById(R.id.joint_2_seek_bar));
        mSeekBars.add((SeekBar)findViewById(R.id.joint_3_seek_bar));
        mSeekBars.add((SeekBar)findViewById(R.id.joint_4_seek_bar));
        mSeekBars.add((SeekBar)findViewById(R.id.joint_5_seek_bar));
        for (SeekBar seekBar : mSeekBars) {
            seekBar.setOnSeekBarChangeListener(this);
        }
    }

    public void updateSlidersForPosition(int joint1Angle, int joint2Angle, int joint3Angle, int joint4Angle, int joint5Angle) {
        mSeekBars.get(1).setProgress(joint1Angle + 90); // Joint 1
        mSeekBars.get(2).setProgress(joint2Angle); // Joint 2
        mSeekBars.get(3).setProgress(joint3Angle + 90); // Joint 3
        mSeekBars.get(4).setProgress(joint4Angle + 180); // Joint 4
        mSeekBars.get(5).setProgress(joint5Angle); // Joint 5
        String jointAnglesStr = getString(R.string.joint_angle_format,
                joint1Angle, joint2Angle, joint3Angle, joint4Angle, joint5Angle);
        mJointAnglesTextView.setText(jointAnglesStr);
    }

    // ------------------------ Button Listeners ------------------------
    public void handleHomeClick(View view) {
        updateSlidersForPosition(0, 90, 0, -90, 90);
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handlePosition1Click(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handlePosition2Click(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleScript1Click(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleScript2Click(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleScript3Click(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleStopClick(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleForwardClick(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleBackClick(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleLeftClick(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleRightClick(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
    }

    public void handleBatteryClick(View view) {
        Toast.makeText(this, "TODO: Implement button", Toast.LENGTH_SHORT).show();
        // Need to send BATTERY VOLTAGE REQUEST
        // Toast all replies.  Arduino will reply with a BATTERY VOLTAGE REPLY.
        // Receive messages will arrive via onCommandReceived
    }

    // ------------------------ OnSeekBarChangeListener ------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return; // Do nothing if the change is not from the user.
        }
        // For simplicity just do a complete UI refresh of the text views.
        int seekBarValues[] = new int[6];
        seekBarValues[0] = mSeekBars.get(0).getProgress();  // Gripper
        seekBarValues[1] = mSeekBars.get(1).getProgress() - 90; // Joint 1
        seekBarValues[2] = mSeekBars.get(2).getProgress(); // Joint 2
        seekBarValues[3] = mSeekBars.get(3).getProgress() - 90; // Joint 3
        seekBarValues[4] = mSeekBars.get(4).getProgress() - 180; // Joint 4
        seekBarValues[5] = mSeekBars.get(5).getProgress(); // Joint 5

        String jointAnglesStr = getString(R.string.joint_angle_format,
                seekBarValues[1], seekBarValues[2], seekBarValues[3],
                seekBarValues[4], seekBarValues[5]);
        String gripperStr = getString(R.string.gripper_format, seekBarValues[0]);

        mJointAnglesTextView.setText(jointAnglesStr);
        mGripperDistanceTextView.setText(gripperStr);

        String command = "";
        switch(seekBar.getId()) {
            case R.id.gripper_seek_bar:
                command = getString(R.string.gripper_command, seekBarValues[0]);
                break;
            case R.id.joint_1_seek_bar:
                command = getString(R.string.joint_angle_command, 1, seekBarValues[1]);
                break;
            case R.id.joint_2_seek_bar:
                command = getString(R.string.joint_angle_command, 2, seekBarValues[2]);
                break;
            case R.id.joint_3_seek_bar:
                command = getString(R.string.joint_angle_command, 3, seekBarValues[3]);
                break;
            case R.id.joint_4_seek_bar:
                command = getString(R.string.joint_angle_command, 4, seekBarValues[4]);
                break;
            case R.id.joint_5_seek_bar:
                command = getString(R.string.joint_angle_command, 5, seekBarValues[5]);
                break;
        }
        // Uncomment this line to send the slider command.
//		sendCommand(command);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

}
