package com.yasee.yaseejava.lifeforever;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class YaseeJob extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
