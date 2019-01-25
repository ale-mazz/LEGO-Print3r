package printer.ev3printer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import it.unive.dais.legodroid.lib.EV3;

public class EV3Service extends Service {

    // binder
    private final IBinder mBinder = new LocalBinder();
    EV3 ev3Brick;
    PrinterManager manager;

    public EV3Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setBrickConnection(EV3 brick){
        if(brick != null){
            ev3Brick = brick;
        }
    }

    public boolean isBrickActive(){
        return ev3Brick != null;
    }

    public class LocalBinder extends Binder {
        EV3Service getService() {
            // Return this instance of LocalService so clients can call public methods
            return EV3Service.this;
        }
    }
}
