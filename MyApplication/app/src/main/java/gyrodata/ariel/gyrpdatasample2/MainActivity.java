package gyrodata.ariel.gyrpdatasample2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private View coloredView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configRealm();
        coloredView = findViewById(R.id.coloredView);
        coloredView.setBackgroundColor(Color.GREEN);
        Button determineDeviceColor = (Button) findViewById(R.id.determineDeviceColorbutton);
        determineDeviceColor.setOnClickListener(determineDeviceColorFromDGyroData);
        MainActivity.this.startService(new Intent(getApplicationContext(), SaveGyroDataService.class));
    }

    private void configRealm() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private View.OnClickListener determineDeviceColorFromDGyroData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent readGyroData = new Intent(MainActivity.this, ReadLastGyroDataService.class);
            startService(readGyroData);
        }
    };

    //Catch event thrown from ReadLastGyroDataService using Otto EventBus
    @Subscribe
    public void onLastGyroDataDrawn(final OnLastGyroDataSampled event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.getGyroDataColorFloat() < 0) {
                    coloredView.setBackgroundColor(Color.RED);
                        Log.d(TAG, "Device Screen is facing DOWN. X orientation:" + event.getGyroDataColorFloat());
                } else {
                    if (event.getGyroDataColorFloat() >= 8) {
                        Log.d(TAG, "Device Screen is facing UP. X orientation:" + event.getGyroDataColorFloat());
                        coloredView.setBackgroundColor(Color.BLUE);
                    } else {
                        coloredView.setBackgroundColor(Color.GREEN);
                        Log.d(TAG, "Device Screen is facing TOWARDS the user. X orientation:" + event.getGyroDataColorFloat());
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}

