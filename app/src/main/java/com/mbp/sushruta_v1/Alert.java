package com.mbp.sushruta_v1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class Alert extends AppCompatActivity {

    public static final String MESSAGE_STATUS = "message_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        PeriodicWorkRequest.Builder dayWorkBuilder =
                new PeriodicWorkRequest.Builder(AttendanceWorker.class, 2, TimeUnit.MINUTES, 2, TimeUnit.MINUTES);

        PeriodicWorkRequest dayWork = dayWorkBuilder.build();

        WorkManager.getInstance().enqueue(dayWork);
    }
}
