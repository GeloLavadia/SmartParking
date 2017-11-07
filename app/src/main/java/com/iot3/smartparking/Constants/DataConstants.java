package com.iot3.smartparking.Constants;

public class DataConstants {
    public static final class DatabaseHelperConstants {
        public static final String databaseFilename = "SMART_PARKING.db";
        public static final int databaseVersion = 1;

        public static final String parkingTableName = "PARKING_TABLE";
        public static final String parkingId = "PARK_ID";
        public static final String parkingTimeInColumn = "PARK_IN";
        public static final String parkingTimeOutColumn = "PARK_OUT";
        public static final String parkingLotNumberColumn = "PARK_LOT_NUMBER";
        public static final String nullValue = "";

        public static final String parkingCreateQuery = "CREATE TABLE " + parkingTableName + "(" + parkingId + " INTEGER PRIMARY KEY AUTOINCREMENT, " + parkingTimeInColumn + " TEXT, " + parkingTimeOutColumn + " TEXT, " + parkingLotNumberColumn + " INTEGER)";
        public static final String parkingUpdateQueryBegin = "UPDATE " + parkingTableName + " SET " + parkingTimeOutColumn + " = '";
        public static final String parkingUpdateQueryEnd = "' WHERE " + parkingTimeOutColumn + " = '' AND " + parkingLotNumberColumn + " = " ;
        public static final String parkingListQuery = "SELECT " + parkingTimeInColumn + ", " + parkingTimeOutColumn + " FROM " + parkingTableName + " WHERE " + parkingLotNumberColumn + " = ";
    }
}
