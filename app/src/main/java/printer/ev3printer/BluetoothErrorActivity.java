package printer.ev3printer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BluetoothErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_error);

        ImageView alertImageView = findViewById(R.id.alertImageView);
        TextView alertTextView = findViewById(R.id.alertTextView);
        Button exitButton = findViewById(R.id.exitButton);


        exitButton.setOnClickListener(v-> {
                finish();
                System.exit(0);
            });
    }
}
