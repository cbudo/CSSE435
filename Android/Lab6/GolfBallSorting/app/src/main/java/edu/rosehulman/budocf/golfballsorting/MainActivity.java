package edu.rosehulman.budocf.golfballsorting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.rosehulman.budocf.golfballsorting.me435.RobotActivity;

public class MainActivity extends RobotActivity {
    TextView ball1, ball2, ball3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ball1 = (TextView) findViewById(R.id.ball1_textView);
        ball2 = (TextView) findViewById(R.id.ball2_textView);
        ball3 = (TextView) findViewById(R.id.ball3_textView);
    }

    public void clear(View view) {
        ball1.setText("---");
        ball2.setText("---");
        ball3.setText("---");
    }

    public void changeColor(View view) {
        String buttonName = getResources().getResourceEntryName(view.getId());
        Log.wtf(TAG, "changeColor: " + buttonName);
        String buttonColor = buttonName.substring(0, buttonName.length() - 1);
        Toast.makeText(MainActivity.this, buttonColor, Toast.LENGTH_SHORT).show();
        switch (buttonName.charAt(buttonName.length() - 1)) {
            case '1':
                ball1.setText(capitalize(buttonColor));
                break;
            case '2':
                ball2.setText(capitalize(buttonColor));
                break;
            case '3':
                ball3.setText(capitalize(buttonColor));
                break;
        }
    }
    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public void positionTest(View view) {
        // TODO: Send command to arduino to get ball colorz
    }

    public void goClick(View view) {
        // based on colors, move forward and knock balls off
    }
}
