package app.com.bledemo.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import app.com.bledemo.service.BleServiceMain;
import app.com.bledemo.service.BluetoothLeService;

/**
 * Created by kxyu on 2018/11/23
 */

public class BleApplication extends Application {

    private final String TAG = "BleApplication";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Application   onCreate   ");

        RuntimeData.mContext = getApplicationContext();


        Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            RuntimeData.blueService = ((BluetoothLeService.LocalBinder) service).getService();
            if (RuntimeData.blueService.serviceMain.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");

            }
            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            RuntimeData.blueService = null;
        }
    };



}
