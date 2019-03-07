package com.mbp.sushruta_v1;

import android.app.Application;

public class GlobalClass extends Application {
    String position;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}

