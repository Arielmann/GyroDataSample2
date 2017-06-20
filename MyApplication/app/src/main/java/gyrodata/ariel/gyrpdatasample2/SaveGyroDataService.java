package gyrodata.ariel.gyrpdatasample2;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import io.realm.Realm;
import io.realm.exceptions.RealmException;

public class SaveGyroDataService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private String TAG = SaveGyroDataService.class.getSimpleName();
    private int MILLISECONDS_CONSTANT = 300000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                MILLISECONDS_CONSTANT);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                RealmController.forWriting(SaveGyroDataService.this);
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    handleAccelerometer(event);
                }
            }
        });
        thread.start();

    }

    private void handleAccelerometer(SensorEvent event) {
        final float zOrientation = event.values[2];
        saveGyroDataToRealm(zOrientation);
    }

    private void saveGyroDataToRealm(final float zOrientation) {

        Realm realm = RealmController.getWritingInstance().getRealm();
        try { //NOTE: Creating debugging points here triggers IllegalStateException
            int gyroDataArraySize = RealmController.getWritingInstance().getAllGyroData().size();
            if (gyroDataArraySize > 500) {
                RealmController.getWritingInstance().clearAllGyroData(); // Prevent from exceeding 500
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    GyroData gyroData = new GyroData();
                    gyroData.setId(RealmController.getWritingInstance().getAllGyroData().size() + 1);
                    gyroData.setDeviceZOrientation(zOrientation);
                    realm.copyToRealm(gyroData);
                }
            });
        } catch (IllegalStateException | RealmException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            realm.close();
        }
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}