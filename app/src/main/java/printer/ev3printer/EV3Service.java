package printer.ev3printer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;

public class EV3Service extends Service {

    private final IBinder mBinder = new LocalBinder();
    private EV3 ev3Brick = null;
    private final String legoBrickName = "HAL9000";

    private BluetoothConnection connection = new BluetoothConnection("HAL9000");
    private BluetoothConnection.BluetoothChannel channel = null;

    public EV3Service() {
        System.out.println("EV3 service created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setBrickConnection(EV3 brick){
        if(ev3Brick == null){
            ev3Brick = brick;
            //connection = new BluetoothConnection("HAL9000");
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
        if (channel == null) {
            try {
                connection = new BluetoothConnection(legoBrickName);
                channel = connection.connect();
            } catch (IOException exception) {
                Log.w("Service", exception);
            }
        }
        if (ev3Brick == null) {
            ev3Brick = new EV3(channel);
        }

        return ev3Brick;
    }

}
