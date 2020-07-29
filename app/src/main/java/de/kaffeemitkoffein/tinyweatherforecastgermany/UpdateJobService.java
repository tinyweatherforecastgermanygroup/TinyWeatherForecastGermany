package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UpdateJobService extends JobService{
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Intent intent = new Intent(this,WeatherUpdateBroadcastReceiver.class);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(WeatherUpdateBroadcastReceiver.UPDATE_ACTION);
        sendBroadcast(intent);
        PrivateLog.log(this,Tag.UPDATEJOBSERVICE," called, sent broadcast, terminating.");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
