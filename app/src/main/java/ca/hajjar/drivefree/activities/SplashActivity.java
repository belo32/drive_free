package ca.hajjar.drivefree.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        finish();
    }
}
