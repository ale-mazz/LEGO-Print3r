package printer.ev3printer;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class BluetoothErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_error);

        Button exitButton = findViewById(R.id.exitButton);

        exitButton.setOnClickListener(v-> {
                finish();
                System.exit(0);
            });
    }
}
