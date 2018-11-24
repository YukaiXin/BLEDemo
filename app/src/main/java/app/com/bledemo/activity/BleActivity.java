package app.com.bledemo.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.com.bledemo.Format.FastMode;
import app.com.bledemo.R;
import app.com.bledemo.adapter.BlueToothDeviceAdapter;
import app.com.bledemo.app.RuntimeData;
import app.com.bledemo.bean.Bluetooth;
import app.com.bledemo.service.BleServiceMain;

public class BleActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "kxyu";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 3;
    private TextView tvBtnSend;
    private EditText etInput;
    private ListView listView;
    private Handler mHandler;
//    private BluetoothLeService bluetoothLeService;
    private BlueToothDeviceAdapter adapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter = null;

    private static final long SCAN_PERIOD = 10000;
    private List<Bluetooth> mDeviceList;
    private boolean mScanning;
    private Set<BluetoothDevice> deviceSet;
    private FastMode fastMode;

    private String mDeviceAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        tvBtnSend = findViewById(R.id.tv_send);
        etInput   = findViewById(R.id.et_input);
        tvBtnSend.setOnClickListener(this);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "设备不支持蓝牙  ！！", Toast.LENGTH_SHORT).show();
            finish();
        }

        mHandler = new Handler();
        listView = findViewById(R.id.listview);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        deviceSet = new HashSet<>();
        mDeviceList = new ArrayList<>();
        listView.setAdapter(adapter = new BlueToothDeviceAdapter(this, R.layout.item_bluetooth_device, mDeviceList));
        listView.setOnItemClickListener(onItemClickListener);


        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothLeScanner == null) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mDeviceList.clear();
        deviceSet.clear();
        adapter.setList(mDeviceList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        scanLeDevice(true);

        if (RuntimeData.blueService != null) {
            final boolean result = RuntimeData.blueService.serviceMain.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }



    private void scanLeDevice(final boolean enable) {
        deviceSet.clear();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.i(TAG, " 、停止扫描 ！！！！ ");
                    mBluetoothLeScanner.stopScan(scanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            // 扫描时，可以设置
            // bleScanFilters  扫描过滤器列表，可设置通过蓝牙名称或蓝牙服务UUID过滤蓝牙设备。
            // bleScanSettings 扫描设置，主要设置扫描模式，SCAN_MODE_LOW_POWER为低能耗。

            Log.i(TAG, " 开始扫描 ！！！！ ");

            mBluetoothLeScanner.startScan(scanCallback);

        } else {
            mScanning = false;

            Log.i(TAG, " 、停止扫描 ！！！！ ");
            mBluetoothLeScanner.stopScan(scanCallback);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_send:
                String data = etInput.getText().toString();
                if(TextUtils.isEmpty(data)){
                   Toast.makeText(this," 请输入发送内容   ", Toast.LENGTH_LONG).show();
                   return;
                }
                RuntimeData.blueService.serviceMain.write(data.getBytes(), data.length());
                break;

        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

        }
    };

    protected ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);

            Log.i(TAG, " onScanResult ： ");
            BluetoothDevice device = result.getDevice();
            if (TextUtils.isEmpty(device.getName()) || TextUtils.isEmpty(device.getAddress())) {
                return;
            }
            Log.i(TAG, " device  :  "+device.getName()+ "  adress : "+device.getAddress());
            if(deviceSet.add(device)){
                mDeviceList.add(new Bluetooth(false, device));
                adapter.setList(mDeviceList);
            }
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            super.onBatchScanResults(results);

            Log.i(TAG, " onBatchScanResults ： ");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (ScanResult item : results) {
                        BluetoothDevice device = item.getDevice();
                        if (TextUtils.isEmpty(device.getName()) || TextUtils.isEmpty(device.getAddress())) {
                            continue;
                        }
                        Log.i(TAG, " device  :  "+device.getName()+ "  adress : "+device.getAddress());
                        if(deviceSet.add(device)){
                            mDeviceList.add(new Bluetooth(false, device));
                            adapter.setList(mDeviceList);
                        }
                    }

                }
            });
        }

        @Override
        public void onScanFailed(final int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, " 扫描失败 ： errorCode ： "+errorCode);
        }
    };




    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Bluetooth bluetooth = adapter.getBluetooths().get(i);
            if(bluetooth.isConnect()) return;
                RuntimeData.blueService.serviceMain.disconnect();
                mDeviceAddress = adapter.getBluetooths().get(i).getDevice().getAddress();
                if(RuntimeData.blueService.serviceMain.connect(mDeviceAddress)){
                    Log.i(TAG," 蓝牙链接 成功 ！！！！！");
                    for (Bluetooth b: adapter.getBluetooths()){
                        b.setConnect(false);
                    }
                    adapter.getBluetooths().get(i).setConnect(true);
                    Collections.swap(adapter.getBluetooths(), 0 , i);
                    adapter.notifyDataSetChanged();
                }else {
                    Log.i(TAG," 蓝牙链接 失败  ！！！！ ");
                    adapter.getBluetooths().get(i).setConnect(false);
                }
        }
    };

}
