package app.com.bledemo.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kxyu on 2018/10/22
 */

public class Bluetooth {

    private boolean isConnect;
    private BluetoothDevice device;

    public Bluetooth(boolean isConnect, BluetoothDevice device) {
        this.isConnect = isConnect;
        this.device = device;
    }

    public boolean isConnect() {
        return isConnect;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
