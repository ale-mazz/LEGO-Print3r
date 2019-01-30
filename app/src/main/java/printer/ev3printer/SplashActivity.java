package printer.ev3printer;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Handler().postDelayed(() -> {
            Intent mySuperIntent = new Intent(SplashActivity.this, GalleryActivity.class);
            /*Intent i = new Intent(SplashActivity.this, BluetoothErrorActivity.class);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!isBluetoothAvailable()) {
                startActivity(i);
                finish();
            } else { */
                startActivity(mySuperIntent);
                finish();
            //}
        }, SPLASH_TIME);
    }

    public boolean isBluetoothAvailable() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return (bluetoothAdapter != null
                && bluetoothAdapter.isEnabled()
                && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON);
    }
}