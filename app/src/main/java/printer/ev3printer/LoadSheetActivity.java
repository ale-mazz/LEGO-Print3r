package printer.ev3printer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.unive.dais.legodroid.lib.EV3;

public class LoadSheetActivity extends Activity {

    EV3 ev3;
    boolean mBound;
    EV3Service mService;
    boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("LoadSheetActivity");
        setContentView(R.layout.activity_load_sheet);
        //TextView galleryHelpText = findViewById(R.id.calibrationButtonHelpText);
        setOverlay();
    }

    //region EV3Service connection
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

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
        Intent intent = new Intent(this, EV3Service.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onStop(){
        super.onStop();
        ev3.cancel();

        unbindService(mConnection);
        mBound = false;
    }
    //endregion

    private void loadSheet(EV3.Api api) {
        PrinterManager printerManager = new PrinterManager(api);
        try{
            isPressed = printerManager.LoadSheetWithButton();
            if(isPressed){
                this.finish();
            }
        } catch(IOException e){
            Log.e("loadSheet", "IOException error.");
        } catch (ExecutionException e){
            Log.e("loadSheet", "ExecutionException error.");
        } catch(InterruptedException e){
            Log.d("loadSheet", "Interrupted");
        }
    }

    public void setOverlay(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);
    }
}
