package gyrodata.ariel.gyrpdatasample2;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;

public class ReadLastGyroDataService extends IntentService {

    private static final String TAG = ReadLastGyroDataService.class.getSimpleName();

    public ReadLastGyroDataService() {
        super(ReadLastGyroDataService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        RealmController.forReading(this);
        final Realm realm = RealmController.getReadingInstance().getRealm();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmController controller = RealmController.getReadingInstance();
                    List<GyroData> allGyroData = controller.getAllGyroData();
                    GyroData lastGyroData = allGyroData.get(allGyroData.size() - 1);
                    EventBus.getDefault().post(new OnLastGyroDataSampled(lastGyroData.getDeviceZOrientation()));
                }
            });
        } finally {
            if (realm != null) {
                 realm.close();
            }
        }
    }
}
