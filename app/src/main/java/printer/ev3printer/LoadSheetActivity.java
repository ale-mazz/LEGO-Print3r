package printer.ev3printer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.plugs.TouchSensor;
import it.unive.dais.legodroid.lib.util.Prelude;

public class LoadSheetActivity extends AppCompatActivity {

    // Service ev3
    EV3Service mService;
    EV3 ev3;
    boolean mBound = false;
    private TouchSensor touch;
    private boolean started = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_sheet);

        System.out.println("onCreate creato");

    }

    // Passaggio EV3Service
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            EV3Service.LocalBinder binder = (EV3Service.LocalBinder) service;
            mService = binder.getService();
            ev3 = mService.GetBrick();
            try{
                if(!ev3.isCancelled())
                    System.out.println("I'm calling the ev3 method.");
                    ev3.run(LoadSheetActivity.this::loadSheet);
            } catch (EV3.AlreadyRunningException e){
                Log.e("EV3", "EV3 task is already running.");
            }

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();


        System.out.println("onStart creato");

        // Creo intent per EV3Service
        Intent intent = new Intent(this, EV3Service.class);
        // Crea il bind con il service oppure crea prima il service se non presente
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        System.out.println("Ho appena creato il bind con il SERVICE.");

    }
    @Override
    public void onStop(){
        super.onStop();
        unbindService(mConnection);
        mBound = false;
        //mService.GetBrick().cancel();
    }

    private void loadSheet(EV3.Api api) {
        Log.d("Load sheet method: ","Loading SHEET started.");
        PrinterManager printerManager = new PrinterManager(api);
        try{
            printerManager.LoadSheetWithButton();
        } catch(IOException e){

        } catch (ExecutionException e){

        } catch(InterruptedException e){

        }

    }
}
