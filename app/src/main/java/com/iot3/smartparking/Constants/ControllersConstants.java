package com.iot3.smartparking.Constants;

import java.util.UUID;

public class ControllersConstants {
    public static final class MainActivityConstants {
        public static final int enableBluetoothRequestCode = 0;
        public static final int bluetoothDevicesListRequestCode = 1;
        public static final String addressIntentGetExtra = "addressIntentExtra";
        public static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        public static final int handlerState = 0;
        public static final String delimeter = "~";
        public static final String data1Tag = "1";
        public static final String data2Tag = "2";
        public static final String distanceUnit = " inches";
        public static final int minimumAvailableDistance = 4;
        public static final int minimumOccupiedDistance = 9;
        public static final String parkAvailable = "AVAILABLE";
        public static final String parkOccupied = "OCCUPIED";
        public static final String nullString = "";
    }
    public static final class BluetoothDevicesListActivityConstants {
        public static final int noDevices = 0;
        public static final int bluetoothConnectionRequestCode = 0;
        public static final String addressIntentPutExtra = "addressIntentExtra";
    }
}
