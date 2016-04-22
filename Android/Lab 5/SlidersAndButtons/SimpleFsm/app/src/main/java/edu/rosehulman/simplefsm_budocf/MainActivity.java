package edu.rosehulman.simplefsm_budocf;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private TextView mCurrentStateTextView, mStateTimeTextView;
    private long mStateStartTime;
    private Handler mCommandHandler = new Handler();

    public enum State {READY, RUNNING_A_SCRIPT, SEEKING_HOME, SUCCESS, MAGIC_CARPET, SEEKING_SOMEWHERE}

    private Timer mTimer;
    public static final int LOOP_INTERVAL_MS = 100;
    private State mState = State.READY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentStateTextView = (TextView) findViewById(R.id.current_state_textview);
        mStateTimeTextView = (TextView) findViewById(R.id.state_time_textview);
        setState(State.READY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
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
        mTimer.cancel();
        mTimer = null;
    }

    public void handleGo(View view) {
        Toast.makeText(this, "You pressed Go!", Toast.LENGTH_SHORT).show();
        // TODO: If in the ready state, set the state to Running a script.
        if (mState == State.READY) {
            setState(State.RUNNING_A_SCRIPT);
        }
        if(mState == State.SEEKING_HOME) {
            setState(State.SEEKING_SOMEWHERE);
        }
    }

    public void handleReset(View view) {
        Toast.makeText(this, "You pressed Reset", Toast.LENGTH_SHORT).show();
        // TODO: Set the state to Ready.
        if (mState == State.SEEKING_HOME || mState == State.SUCCESS) {
            setState(State.READY);
        }
    }

    public void handleHitTarget(View view) {
        Toast.makeText(this, "You pressed Hit Target", Toast.LENGTH_SHORT).show();
        // TODO: If in the seeking home state, set the state to Success.
        if (mState == State.SEEKING_HOME) {
            setState(State.SUCCESS);
        }
        if (mState == State.READY) {
            setState(State.MAGIC_CARPET);
        }
    }

    private void runScript() {
        Toast.makeText(this, "Run script step 0", Toast.LENGTH_SHORT).show();
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Run script step 1", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Run Script step 2", Toast.LENGTH_SHORT).show();
            }
        }, 4000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == State.RUNNING_A_SCRIPT) {
                    if (mState == State.RUNNING_A_SCRIPT) {
                        setState(State.SEEKING_HOME);
                    }
                }
            }
        }, 6000);
    }

    public void magicCartpetScript() {
        Toast.makeText(this, "Magic", Toast.LENGTH_SHORT).show();
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Carpet", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
        mCommandHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == State.MAGIC_CARPET) {
                    setState(State.SUCCESS);
                }
            }
        }, 5000);
    }

    public void setState(State state) {
        mStateStartTime = System.currentTimeMillis();
        mCurrentStateTextView.setText(state.name());

        switch (state) {
            case READY:
                break;
            case RUNNING_A_SCRIPT:
                runScript();
                break;
            case SEEKING_HOME:
                break;
            case SUCCESS:
                break;
            case SEEKING_SOMEWHERE:
                break;
            case MAGIC_CARPET:
                magicCartpetScript();
                break;
        }
        mState = state;
    }

    private long getStateTimeMs() {
        return System.currentTimeMillis() - mStateStartTime;
    }

    public void loop() {
        mStateTimeTextView.setText("" + getStateTimeMs());
        switch (mState) {
            case SEEKING_HOME:
                if (getStateTimeMs() > 6000) {
                    setState(State.READY);
                }
                break;
            case SEEKING_SOMEWHERE:
                if(getStateTimeMs() > 3000) {
                    setState(State.SUCCESS);
                }
            default:
                break;
        }
    }
}
