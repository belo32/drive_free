package ca.hajjar.drivefree.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import ca.hajjar.drivefree.R;
import ca.hajjar.drivefree.SettingsFragment;
import ca.hajjar.drivefree.models.Config;
import ca.hajjar.drivefree.services.ActivityRecognizedService;
import ca.hajjar.drivefree.util.Debug;

/**
 * Created by bilal on 2017-01-19.
 */

public class SettingActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SettingsFragment.OnConfigurationChangeListener {

    public static final String TAG = SettingActivity.class.getName();
    private Config mConfig;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_setting);
        buildGoogleApiClient();
        mConfig = Config.loadConfig(this);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Debug.d(TAG, "onConnected Activity Reciever");
        if (mConfig != null && mConfig.isEnabled()) {
            requestActivityUpdates();
        }
        //Toast.makeText(getApplicationContext(), "Activity Reciever Connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Debug.d(TAG, "onConnectionSuspended " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Debug.e(TAG, "onConnectionFailed " + connectionResult.getErrorMessage().toString());
    }

    protected void requestActivityUpdates() {
        Debug.d(TAG, "RequestActivityUpdates");
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, Config.DEFAULT_RATION_CHECK, pendingIntent);
    }

    protected void removeActivityUpdates() {
        Debug.d(TAG, "RemoveActivityUpdates");
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, pendingIntent);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConfigurationUpdate(Config config) {
        mConfig = config;
        Config.saveConfig(this, config);
        if (mGoogleApiClient != null && (!mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            Debug.e(TAG, "GOOGLEAPI isnull, or connecting");
            return;
        }
        if (mConfig.isEnabled()) {
            requestActivityUpdates();
        } else {
            removeActivityUpdates();
        }
    }
}
