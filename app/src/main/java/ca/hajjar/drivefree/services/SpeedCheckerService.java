package ca.hajjar.drivefree.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import ca.hajjar.drivefree.BuildConfig;
import ca.hajjar.drivefree.R;
import ca.hajjar.drivefree.SettingsFragment;

/**
 * No Longer needed as the Recognition API handles background checks of the state of the phone
 */
@Deprecated
public class SpeedCheckerService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = SpeedCheckerService.class.getName();
    public static final int MSG_TOGGLE_FEATURE = 100;


    private SharedPreferences mSharedPreferences;
    private boolean isEnabled = false;

    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;


    private final String FENCE_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "FENCE_RECEIVER_ACTION";

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected Activity Reciever");
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 6000, pendingIntent);
        Toast.makeText(getApplicationContext(), "Activity Reciever Connected", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Activity Reciever suspended", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("SpeedCheckerService", connectionResult.getErrorMessage().toString());
        Toast.makeText(getApplicationContext(), "Activity Receiver failed", Toast.LENGTH_LONG).show();


    }


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TOGGLE_FEATURE:
                    Toast.makeText(getApplicationContext(), "Toggle Feature", Toast.LENGTH_LONG).show();
                    Log.d("TEST", "Toggle Feature");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public SpeedCheckerService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mSharedPreferences = getSharedPreferences(SettingsFragment.PREFS_SETTINGS, 0);
        isEnabled = mSharedPreferences.getBoolean("isEnabled", false);
        buildGoogleApiClient();


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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }
}
