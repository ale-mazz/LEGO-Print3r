package printer.ev3printer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.util.Prelude;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Prelude.ReTAG("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            EV3 ev3 = new EV3(new BluetoothConnection("HAL9000").connect());
            //TachoMotor wheels = new TachoMotor(EV3.OutputPort.D);
            Button start_button = findViewById(R.id.start_button);
        }

        catch (IOException e) {
            Log.e(TAG, "fatal error: cannot connect to HAL9000");
            e.printStackTrace();
        }
    }
}
