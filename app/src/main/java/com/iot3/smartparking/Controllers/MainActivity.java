package com.iot3.smartparking.Controllers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.iot3.smartparking.Data.DatabaseHelper;
import com.iot3.smartparking.Helpers.ArduinoThread;
import com.iot3.smartparking.Models.ParkingModel;

import java.io.IOException;
import java.util.ArrayList;

import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.addressIntentGetExtra;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.bluetoothDevicesListRequestCode;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.data1Tag;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.data2Tag;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.delimeter;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.distanceUnit;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.enableBluetoothRequestCode;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.handlerState;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.minimumAvailableDistance;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.minimumOccupiedDistance;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.myUUID;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.nullString;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.parkAvailable;
import static com.iot3.smartparking.Constants.ControllersConstants.MainActivityConstants.parkOccupied;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private LinearLayout parking2Layout;
    private RelativeLayout bluetoothConnectingLayout;
    private TextView parking1Status, parking1Distance, parking2Status, parking2Distance;
    private BottomNavigationView navigation;
    private DatabaseHelper databaseHelper;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private Intent intent;
    private String macAddress;
    private Handler handler;
    private StringBuilder stringBuilder;
    private boolean parking1Available, parking2Available;
    private int firstTwoThreads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parking2Layout = (LinearLayout) findViewById(R.id.parking2Layout);
        bluetoothConnectingLayout = (RelativeLayout) findViewById(R.id.bluetoothConnectingLayout);
        parking1Status = (TextView) findViewById(R.id.parking1Status);
        parking1Distance = (TextView) findViewById(R.id.parking1Distance);
        parking2Status = (TextView) findViewById(R.id.parking2Status);
        parking2Distance = (TextView) findViewById(R.id.parking2Distance);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        databaseHelper = new DatabaseHelper(this);
        firstTwoThreads = 0;
        parking1Available = displayParkingTable(1);
        parking2Available = displayParkingTable(2);
        stringBuilder = new StringBuilder();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            finish();
        }
        else {
            if (bluetoothAdapter.isEnabled()) {
                openBluetoothDevicesList();
            }
            else {
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, enableBluetoothRequestCode);
            }
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    stringBuilder.append(readMessage);
                    int endOfLineIndex = stringBuilder.indexOf(delimeter);
                    if (endOfLineIndex > 0) {
                        String dataInPrint = stringBuilder.substring(2, endOfLineIndex);
                        if (stringBuilder.substring(0, 1).equals(data1Tag)) {
                            parking1Distance.setText(dataInPrint + distanceUnit);
                            parking1Available = parkingStatusIdentifier(1, Integer.parseInt(dataInPrint), parking1Status, parking1Available);
                        }
                        else if (stringBuilder.substring(0, 1).equals(data2Tag)) {
                            parking2Distance.setText(dataInPrint + distanceUnit);
                            parking2Available = parkingStatusIdentifier(2, Integer.parseInt(dataInPrint), parking2Status, parking2Available);
                        }
                        stringBuilder.delete(0, stringBuilder.length());
                    }
                }
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_parking_1:
                parking2Layout.setVisibility(View.GONE);
                return true;
            case R.id.navigation_parking_2:
                parking2Layout.setVisibility(View.VISIBLE);
                return true;
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case enableBluetoothRequestCode:
                if (resultCode == RESULT_OK) {
                    openBluetoothDevicesList();
                }
                else {
                    finish();
                }
                break;
            case bluetoothDevicesListRequestCode:
                if (resultCode == RESULT_OK) {
                    macAddress = data.getStringExtra(addressIntentGetExtra);
                    bluetoothConnectingLayout.setVisibility(RelativeLayout.VISIBLE);
                    new BluetoothConnectionProcess().execute();
                }
                else {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private boolean parkingStatusIdentifier(int parkingLot, int distance, TextView parkingStatus, boolean parkingAvailable) {
        if ((parkingAvailable && distance < minimumAvailableDistance) || (!parkingAvailable && firstTwoThreads < 2)) {
            parkingStatus.setText(parkOccupied);
            parkingStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            if (parkingAvailable && distance < minimumAvailableDistance) {
                parkingAvailable = false;
                databaseHelper.parkIn(parkingLot);
                displayParkingTable(parkingLot);
            }
        }
        else if((!parkingAvailable && distance > minimumOccupiedDistance) || (parkingAvailable && firstTwoThreads < 2)) {
            parkingStatus.setText(parkAvailable);
            parkingStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            if (!parkingAvailable && distance > minimumOccupiedDistance) {
                parkingAvailable = true;
                databaseHelper.parkOut(parkingLot);
                displayParkingTable(parkingLot);
            }
        }
        if (firstTwoThreads < 2) {
            firstTwoThreads++;
            if (parkingAvailable) {
                databaseHelper.parkOut(parkingLot);
            }
        }
        return parkingAvailable;
    }

    private void openBluetoothDevicesList() {
        intent = new Intent(MainActivity.this, BluetoothDevicesListActivity.class);
        startActivityForResult(intent, bluetoothDevicesListRequestCode);
    }

    private boolean displayParkingTable(int parkingLotNumber) {
        boolean parkingAvailable = true, evenRow = false;
        TableLayout parkingTable = (TableLayout) findViewById(parkingLotNumber == 1 ? R.id.parking1Table : R.id.parking2Table);
        parkingTable.removeAllViews();
        ArrayList<ParkingModel> parkingModels = databaseHelper.parkHistoryList(parkingLotNumber);
        for (ParkingModel parkingModel : parkingModels) {
            TextView parkingInColumn = new TextView(this), parkingOutColumn = new TextView(this);
            parkingInColumn.setGravity(Gravity.CENTER);
            parkingInColumn.setBackgroundColor(ContextCompat.getColor(this, evenRow ? R.color.white :  R.color.lightGray));
            parkingInColumn.setText(parkingModel.getParkIn());
            parkingOutColumn.setGravity(Gravity.CENTER);
            parkingOutColumn.setBackgroundColor(ContextCompat.getColor(this, evenRow ? R.color.white :  R.color.lightGray));
            parkingOutColumn.setText(parkingModel.getParkOut());
            TableRow tableRow = new TableRow(this);
            tableRow.addView(parkingInColumn);
            tableRow.addView(parkingOutColumn);
            parkingTable.addView(tableRow);
            evenRow = !evenRow;
        }
        parking1Available = parkingModels.size() == 0 || !parkingModels.get(parkingModels.size() - 1).getParkOut().equals(nullString);
        return parkingAvailable;
    }

    private class BluetoothConnectionProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (bluetoothSocket == null) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
                    bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                }
            }
            catch (IOException ioException) {
                finish();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bluetoothConnectingLayout.setVisibility(RelativeLayout.GONE);
                    ArduinoThread arduinoThread = new ArduinoThread(bluetoothSocket, handler);
                    arduinoThread.start();
                }
            });
        }
    }
}
