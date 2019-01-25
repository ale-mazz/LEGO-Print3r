package printer.ev3printer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import it.unive.dais.legodroid.lib.EV3;

public class EV3Service extends Service {

    // binder
    private final IBinder mBinder = new LocalBinder();
    EV3 ev3Brick = null;
    PrinterManager manager = null;

    public EV3Service() {
        System.out.println("EV3 service created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setBrickConnection(EV3 brick){
        if(brick == null){
            ev3Brick = brick;
            //manager = new PrinterManager(EV3.Api);
        }
    }

    public boolean isBrickNull(){
        if(ev3Brick == null){
            System.out.println("EV3 BRICK inside Service is NULL.");
            return  true;
        } else{
            System.out.println("EV3 BRICK PRESENT inside Service.");
            return  false;
        }
    }

    public class LocalBinder extends Binder {
        EV3Service getService() {
            // Return this instance of LocalService so clients can call public methods
            return EV3Service.this;
        }
    }

    public void PrintBrickStatus(){
        if(ev3Brick != null){
            System.out.println("Brick PRESENTE.");
        } else {
            System.out.println("Brick ASSENTE.");
        }
    }

    public EV3 GetBrick(){
        return ev3Brick;
    }


}
