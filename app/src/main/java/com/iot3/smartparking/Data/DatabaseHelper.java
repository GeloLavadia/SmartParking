package com.iot3.smartparking.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iot3.smartparking.Models.ParkingModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.databaseFilename;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.databaseVersion;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.nullValue;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingCreateQuery;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingListQuery;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingLotNumberColumn;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingTableName;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingTimeInColumn;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingTimeOutColumn;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingUpdateQueryBegin;
import static com.iot3.smartparking.Constants.DataConstants.DatabaseHelperConstants.parkingUpdateQueryEnd;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, databaseFilename, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(parkingCreateQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void parkIn(int parkLotNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(parkingLotNumberColumn, parkLotNumber);
        contentValues.put(parkingTimeInColumn, DateFormat.getDateTimeInstance().format(new Date()));
        contentValues.put(parkingTimeOutColumn, nullValue);
        getWritableDatabase().insert(parkingTableName, null, contentValues);
    }

    public void parkOut(int parkLotNumber) {
        getWritableDatabase().execSQL(parkingUpdateQueryBegin + DateFormat.getDateTimeInstance().format(new Date()) + parkingUpdateQueryEnd + String.valueOf(parkLotNumber));
    }

    public ArrayList<ParkingModel> parkHistoryList(int parkLotNumber) {
        ArrayList<ParkingModel> parkingModels = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery(parkingListQuery + String.valueOf(parkLotNumber), null);
        while (cursor.moveToNext()) {
            ParkingModel parkingModel = new ParkingModel();
            parkingModel.setParkIn(cursor.getString(0));
            parkingModel.setParkOut(cursor.getString(1));
            parkingModels.add(parkingModel);
        }
        return parkingModels;
    }
}
