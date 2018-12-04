package printer.ev3printer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import it.unive.dais.legodroid.lib.EV3;
import it.unive.dais.legodroid.lib.comm.BluetoothConnection;
import it.unive.dais.legodroid.lib.plugs.TachoMotor;
import it.unive.dais.legodroid.lib.util.Prelude;
import it.unive.dais.legodroid.lib.util.ThrowingConsumer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = Prelude.ReTAG("MainActivity");
    @Nullable
    private TachoMotor motor;

    private void applyMotor(@NonNull ThrowingConsumer<TachoMotor, Throwable> f) {
        if (motor != null)
            Prelude.trap(() -> f.call(motor));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            // Connect to EV3 (HAL9000) via Bluetooth

            EV3 ev3 = new EV3(new BluetoothConnection("HAL9000").connect());

            Button startButton = findViewById(R.id.startButton);
            TextView test_text = (TextView)findViewById(R.id.test_text);
            /*startButton.setOnClickListener(v -> Prelude.trap(() -> ev3.run(this::legoMain)));*/
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    test_text.setText("success");
                }
            });


            //TODO: fare dei test

            Button stopButton = findViewById(R.id.stopButton);
            stopButton.setOnClickListener(v -> {
                ev3.cancel();   // Cancella l'attività in corso nel main
            });

        } catch (IOException e) {
            Log.e(TAG, "Fatal error: cannot connect to HAL9000");
            e.printStackTrace();
        }
    }

    private void legoMain(EV3.Api api) {

        final String TAG = Prelude.ReTAG("legomain");
        motor = api.getTachoMotor(EV3.OutputPort.A);

        try {
            applyMotor(TachoMotor::resetPosition);

            while (!api.ev3.isCancelled()) {
                try {

                    motor.start();
                    motor.setSpeed(10);

                    Future<Float> speed = motor.getSpeed();

                    Toast.makeText(getApplicationContext(),"Speed", Toast.LENGTH_LONG).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } finally {
            applyMotor(TachoMotor::stop);
        }


    }
}
