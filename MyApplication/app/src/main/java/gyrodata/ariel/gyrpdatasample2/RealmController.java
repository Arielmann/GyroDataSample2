package gyrodata.ariel.gyrpdatasample2;

import android.app.Service;
import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

public class RealmController extends Thread {

    //Had to separate realm instances in order to access from different threads
    private static RealmController readingInstance;
    private static RealmController writingInstance;
    private final Realm realm;

    public RealmController(Context context) {
        realm = Realm.getInstance(context);
    }

    public static RealmController forWriting(SaveGyroDataService saveGyroDataService) {
        writingInstance = new RealmController(saveGyroDataService);
        return writingInstance;
    }

    public static RealmController forReading(ReadLastGyroDataService service) {
        readingInstance = new RealmController(service);
        return readingInstance;
    }

    public static RealmController getReadingInstance() {
        return readingInstance;
    }

    public Realm getRealm() {
        return realm;
    }

    public RealmResults<GyroData> getAllGyroData() {
            return realm.where(GyroData.class).findAll();
    }



    public static RealmController getWritingInstance() {
        return writingInstance;
    }

    public void clearAllGyroData() {
        realm.beginTransaction();
        realm.clear(GyroData.class);
        realm.commitTransaction();
    }
}