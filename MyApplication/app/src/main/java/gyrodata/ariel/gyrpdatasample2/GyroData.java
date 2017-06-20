package gyrodata.ariel.gyrpdatasample2;

import io.realm.RealmObject;
import io.realm.annotations.Index;

public class GyroData extends RealmObject{

    @Index
    private int id;
    private float deviceZOrientation;

    public GyroData() {
    }

    public float getDeviceZOrientation() {
        return deviceZOrientation;
    }

    public void setDeviceZOrientation(float deviceZOrientation) {
        this.deviceZOrientation = deviceZOrientation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
