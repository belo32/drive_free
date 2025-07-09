package ca.hajjar.drivefree.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import ca.hajjar.drivefree.R;
import ca.hajjar.drivefree.activities.SettingActivity;
import ca.hajjar.drivefree.util.Debug;

/**
 * Created by bilal on 2017-01-24.
 */

public class ActivityRecognizedService extends IntentService {
    public final String TAG = ActivityRecognizedService.class.getName();

    private static final int CONFIDENCE_VALUE = 75;

    private NotificationManager mNotificationManager;

    private AudioManager mAudioManager;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Debug.d(TAG, "onHandleIntent");
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }


    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        Debug.d(TAG, "handleDetectedActivities ");

        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Debug.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    if (activity.getConfidence() >= CONFIDENCE_VALUE) {
                        notify("Ringer mode has been set to 'Do Not Disturb'");
                        mute();
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Debug.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Debug.e("ActivityRecogition", "On Foot: " + activity.getConfidence());
                    if (activity.getConfidence() >= CONFIDENCE_VALUE) {
                        dismissNotification();
                        unMute();
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Debug.e("ActivityRecogition", "Running: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.STILL: {
                    Debug.e("ActivityRecogition", "Still: " + activity.getConfidence());
                    if (activity.getConfidence() >= CONFIDENCE_VALUE) {
                        dismissNotification();
                        unMute();
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Debug.e("ActivityRecogition", "Tilting: " + activity.getConfidence());
                   
                    break;
                }
                case DetectedActivity.WALKING: {
                    Debug.e("ActivityRecogition", "Walking: " + activity.getConfidence());
                    if (activity.getConfidence() >= CONFIDENCE_VALUE) {

                        unMute();
                        dismissNotification();

                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Debug.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
                    break;
                }
            }
        }
    }

    private void notify(String message) {
        Debug.d(TAG, "notify: " + message);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_stat_df);
        builder.setContentTitle(getString(R.string.app_name));
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), SettingActivity.class), 0);
        builder.setContentIntent(contentIntent);
        mNotificationManager.notify(TAG, 0, builder.build());
    }

    private void dismissNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        mNotificationManager.cancel(TAG, 0);
    }

    private void mute() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
        int ringerMode = mAudioManager.getRingerMode();
        if(ringerMode != AudioManager.RINGER_MODE_SILENT){
            saveRingerMode(mAudioManager.getRingerMode());
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        }

    }

    private void unMute() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }

        if(mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT){
            int ringerMode = loadRingerMode();
            mAudioManager.setRingerMode(ringerMode);
        }
    }

    private void saveRingerMode(int ringerMode) {
        SharedPreferences sharedPreferences = getSharedPreferences("ringer-mode-saved", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("RINGER_MODE", ringerMode);
        editor.commit();
    }

    private int loadRingerMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("ringer-mode-saved", 0);
        return sharedPreferences.getInt("RINGER_MODE", AudioManager.RINGER_MODE_NORMAL);
    }

}
