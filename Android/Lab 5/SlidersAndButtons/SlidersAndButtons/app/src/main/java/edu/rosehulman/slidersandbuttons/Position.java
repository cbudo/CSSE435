package edu.rosehulman.slidersandbuttons;

import android.content.Context;

/**
 * Created by budocf on 4/26/2016.
 */
public class Position {
    int joint1Angle, joint2Angle, joint3Angle, joint4Angle, joint5Angle;
    Context contextUsed;
    public Position(Context curContext, int joint1Angle, int joint2Angle, int joint3Angle, int joint4Angle, int joint5Angle){
        contextUsed = curContext;
        this.joint1Angle = joint1Angle;
        this.joint2Angle = joint2Angle;
        this.joint3Angle = joint3Angle;
        this.joint4Angle = joint4Angle;
        this.joint5Angle = joint5Angle;
    }
    @Override
    public String toString() {
        return contextUsed.getString(R.string.position_command,joint1Angle, joint2Angle, joint3Angle, joint4Angle, joint5Angle);
    }
}
