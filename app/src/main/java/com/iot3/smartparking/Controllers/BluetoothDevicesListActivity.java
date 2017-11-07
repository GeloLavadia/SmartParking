package com.iot3.smartparking.Controllers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import static com.iot3.smartparking.Constants.ControllersConstants.BluetoothDevicesListActivityConstants.addressIntentPutExtra;
import static com.iot3.smartparking.Constants.ControllersConstants.BluetoothDevicesListActivityConstants.bluetoothConnectionRequestCode;
import static com.iot3.smartparking.Constants.ControllersConstants.BluetoothDevicesListActivityConstants.noDevices;

public class BluetoothDevicesListActivity extends AppCompatActivity {

    private LinearLayout bluetoothDevicesListLayout;
    private Intent intent;
    private Set<BluetoothDevice> bluetoothDevices;
    private boolean evenRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices_list);
        bluetoothDevicesListLayout = (LinearLayout) findViewById(R.id.bluetoothDevicesListLayout);
        evenRow = false;
        bluetoothDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (bluetoothDevices.size() > noDevices) {
            for (final BluetoothDevice bluetoothDevice : bluetoothDevices) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent = getIntent();
                        intent.putExtra(addressIntentPutExtra, bluetoothDevice.getAddress());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setBackgroundColor(ContextCompat.getColor(this, evenRow ? R.color.white : R.color.lightGray));
                TextView bluetoothName = new TextView(this), bluetoothMac = new TextView(this);
                bluetoothName.setText(bluetoothDevice.getName());
                bluetoothName.setTextSize(getResources().getDimension(R.dimen.bluetooth_device_name_text_size));
                bluetoothName.setTypeface(Typeface.DEFAULT_BOLD);
                bluetoothMac.setText(bluetoothDevice.getAddress());
                linearLayout.addView(bluetoothName, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linearLayout.addView(bluetoothMac, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                bluetoothDevicesListLayout.addView(linearLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                evenRow = true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
