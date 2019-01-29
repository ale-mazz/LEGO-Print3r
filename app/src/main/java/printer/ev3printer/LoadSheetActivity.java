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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_sheet);

        Button back = findViewById(R.id.BackButton);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                Intent i = new Intent (LoadSheetActivity.this, GalleryActivity.class);
                startActivity(i);
            }
        });

        //Button loadSheetButton = findViewById(R.id.loadButton);

        //loadSheetButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::loadSheet)));
        while(!mService.GetBrick().isCancelled()){
            Prelude.trap(() -> mService.GetBrick().run(this::loadSheet));
        }

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
        // Creo intent per EV3Service
        Intent intent = new Intent(this, EV3Service.class);
        // Crea il bind con il service oppure crea prima il service se non presente
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onStop(){
        super.onStop();
        unbindService(mConnection);
        mBound = false;
        mService.GetBrick().cancel();
    }

    private void loadSheet(EV3.Api api) throws InterruptedException, ExecutionException, IOException {
        PrinterManager printerManager = new PrinterManager(api);
        printerManager.LoadSheetWithButton();
    }
}
