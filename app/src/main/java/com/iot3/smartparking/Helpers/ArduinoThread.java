package com.iot3.smartparking.Helpers;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.iot3.smartparking.Constants.HelpersConstants.ArduinoThreadConstants.arduinoStarter;
import static com.iot3.smartparking.Constants.HelpersConstants.ArduinoThreadConstants.handlerState;
import static com.iot3.smartparking.Constants.HelpersConstants.ArduinoThreadConstants.readMessageOffset;

public class ArduinoThread extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;
    private Handler handler;

    public ArduinoThread(BluetoothSocket bluetoothSocket, Handler handler) {
        this.handler = handler;
        try {
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        }
        catch (IOException ioException) {

        }
        write(arduinoStarter);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        while(true) {
            try {
                bytes = inputStream.read(buffer);
                String readMessage = new String(buffer, readMessageOffset, bytes);
                handler.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            }
            catch (IOException ioException) {
                break;
            }
        }
    }

    private void write(String input) {
        byte[] messageBuffer = input.getBytes();
        try {
            outputStream.write(messageBuffer);
        }
        catch (IOException ioException) {

        }
    }
}
