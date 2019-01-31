package printer.ev3printer;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;

public class EV3Service extends Service {

    private final IBinder mBinder = new LocalBinder();
    private final String legoBrickName = "EV3_Printer";

    private EV3 ev3Brick = null;
    private BluetoothConnection connection;
    private BluetoothConnection.BluetoothChannel channel = null;

    /**
     * The constructor of the service. When the service is created it holds
     * the connection with the EV3 object and it has to be binded to the
     * activities of the application
     */
    public EV3Service() {
        System.out.println("EV3 service created");
        try{
                connection = new BluetoothConnection(legoBrickName);
                channel = connection.connect();
        } catch (IOException exception) {
            Log.w("Service", exception);
        }
        if (ev3Brick == null && connection != null) {
            ev3Brick = new EV3(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        EV3Service getService() {
            return EV3Service.this;
        }
    }

    public EV3 GetBrick(){
        return ev3Brick;
    }

    //region Utility functions
    public void PrintBrickStatus(){
        if(ev3Brick != null){
            System.out.println("Brick PRESENTE.");
        } else {
            System.out.println("Brick ASSENTE.");
        }
    }
    public boolean isBrickNull(){
        if(ev3Brick == null){
            return  true;
        } else{
            return  false;
        }
    }
    //endregion

    public boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (bluetoothAdapter != null
                && bluetoothAdapter.isEnabled()
                && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON);
    }
}
