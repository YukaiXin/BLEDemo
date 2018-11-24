package app.com.bledemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.com.bledemo.R;
import app.com.bledemo.bean.Bluetooth;

/**
 * Created by kxyu on 2018/8/30.
 */
public class BlueToothDeviceAdapter extends ArrayAdapter<Bluetooth> {

    private final LayoutInflater mInflater;
    private int mResource;
    private Context context;
    private List<Bluetooth> bluetooths;


    public BlueToothDeviceAdapter(Context context, int resource, List<Bluetooth> arrays)  {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        mResource = resource;
        this.bluetooths = arrays;
    }

    @Override
    public int getCount() {

        return bluetooths.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BlueToothDeviceAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
            holder = new BlueToothDeviceAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (BlueToothDeviceAdapter.ViewHolder) convertView.getTag();
        }
        BluetoothDevice device = bluetooths.get(position).getDevice();
        holder.tvDeviceID.setText(context.getString(R.string.wallet_id_num, new Object[]{device.getAddress()}));
        if(TextUtils.isEmpty(device.getName())){
            holder.tvDeviceName.setText("未知设备");
        }else {
            holder.tvDeviceName.setText(device.getName());
        }

        if(bluetooths.get(position).isConnect()){
            holder.ivLinkStatus.setBackgroundResource(R.mipmap.icon_tou_link);
        }else {
            holder.ivLinkStatus.setBackgroundResource(R.mipmap.icon_tou_unlink);
        }

        return convertView;

    }

    class ViewHolder {

        TextView tvDeviceName, tvDeviceID, tvUpdateHardware;
        ImageView ivLinkStatus;
        public ViewHolder(View view) {

            tvDeviceName = view.findViewById(R.id.tv_bluetooth_device_name);
            tvDeviceID   = view.findViewById(R.id.tv_bluetooth_device_id);
            tvUpdateHardware = view.findViewById(R.id.tv_update_hardware);
            ivLinkStatus = view.findViewById(R.id.iv_img);
        }
    }

    public void setList(List<Bluetooth> arrays){
        this.bluetooths = arrays;
        notifyDataSetChanged();
    }

    public List<Bluetooth> getBluetooths() {
        return bluetooths;
    }
}
