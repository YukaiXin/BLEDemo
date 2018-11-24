package app.com.bledemo.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import app.com.bledemo.Format.FastMode;
import app.com.bledemo.Format.FastModeCallback;
import app.com.bledemo.SampleGattAttributes;
import app.com.bledemo.Utils.Convert;
import app.com.bledemo.Utils.MLog;

/**
 * Created by kxyu on 2018/11/23
 */

public class BleServiceMain implements FastModeCallback{

    private final String TAG = "BleServiceMain";

    private final  UUID SERVICE_UUID  = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final  UUID READ_UUID     = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private final  UUID WRITE_UUID    = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");


    private Context mContext;
    public static BleServiceMain __instance = null;

    public static BleServiceMain getInstance(Context context){
        if(__instance == null){
            __instance = new BleServiceMain(context);
        }
        return __instance;
    }

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private BluetoothGattService mBluetoothGattService;
    public BluetoothGattCharacteristic mReadCharacteristic;
    public BluetoothGattCharacteristic mWriteCharacteristic;
    public FastMode fastMode;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
//
//    public final static UUID UUID_HEART_RATE_MEASUREMENT =
//            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
//                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
//                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                mBluetoothGattService = gatt.getService(SERVICE_UUID);
                mReadCharacteristic   = mBluetoothGattService.getCharacteristic(READ_UUID);
                mWriteCharacteristic  = mBluetoothGattService.getCharacteristic(WRITE_UUID);

//                writeCharacteristic(mWriteCharacteristic);
                if(mWriteCharacteristic != null && mReadCharacteristic != null
                        && mBluetoothGattService != null){
                    //TODO：  蓝牙链接

                    readCharacteristic(mReadCharacteristic);
                    setCharacteristicNotification(mReadCharacteristic, true);
                    MLog.d(TAG," onServicesDiscovered  链接 ");
                }else{
                    //
                    MLog.d(TAG,"  onServicesDiscovered 不链接 ");
                }

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                MLog.d(TAG,"  onCharacteristicRead  "+ Convert.bcd2Str(characteristic.getValue()));
                fastMode.onMRecieve(characteristic.getValue(), characteristic.getValue().length);
                if(characteristic.getUuid().equals(READ_UUID)){

//                    fastMode.onMRecieve(characteristic.getValue(), characteristic.getValue().length);
                    MLog.d(TAG,"  onCharacteristicChanged  收到消息 UUID 相等 "+ Convert.bytesToHexString(characteristic.getValue()));
                }else{
                    MLog.d(TAG,"  onCharacteristicChanged  收到消息 UUID 不相等 "+ Convert.bytesToHexString(characteristic.getValue()));
                }
            }else {
                MLog.d(TAG,"  onCharacteristicRead  " + " status  :  "+status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            if(characteristic.getUuid().equals(READ_UUID)){

                fastMode.onMRecieve(characteristic.getValue(), characteristic.getValue().length);
                MLog.d(TAG,"  onCharacteristicChanged  收到消息 UUID 相等 "+ Convert.bytesToHexString(characteristic.getValue()));
            }else{
                MLog.d(TAG,"  onCharacteristicChanged  收到消息 UUID 不相等 "+ Convert.bytesToHexString(characteristic.getValue()));
            }

        }

        @Override
        public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            if(BluetoothGatt.GATT_SUCCESS == status){

                MLog.i(TAG, "    onCharacteristicWrite     "+status + "   value   : "+Convert.bytesToHexString(characteristic.getValue()) );
            }else {
                MLog.i(TAG, "    onCharacteristicWrite     "+status + "   value   : "+Convert.bytesToHexString(characteristic.getValue()) );
            }
//            readCharacteristic(mReadCharacteristic);
        }
    };

    public BleServiceMain(Context context) {
        this.mContext = context;
        this.fastMode = new FastMode(this);
    }


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

         mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }



    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }
    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

//        // This is specific to Heart Rate Measurement.
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    @Override
    public void sendData(byte[] data) {
        byte[] value = new byte[20];
        value[0] = (byte) 0x00;
        mWriteCharacteristic.setValue(value[0],
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        mWriteCharacteristic.setValue(data);
//        mWriteCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        mBluetoothGatt.setCharacteristicNotification(mWriteCharacteristic, true);

        mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);
    }

    @Override
    public void revice(byte[] data, int len) {

    }

    @Override
    public void write(final byte[] data, final int len) {

        new Thread(){
            @Override
            public void run() {
                fastMode.onSendBlock(data, len);
            }
        }.start();
    }
}
