package com.falldetection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Liang on 2015/9/22.
 */
public class BluetoothArduino extends Thread {
    private BluetoothAdapter mBlueAdapter = null;
    private BluetoothSocket mBlueSocket = null;
    private BluetoothDevice mBlueDevice = null;
    OutputStream mOut;
    InputStream mIn;
    private boolean robotFound = false;
    private boolean connected = false;
    private int REQUEST_BLUE_ATIVAR = 10;
    private String robotName;
    private List<String> mMessages  = new ArrayList<String>();
    private String TAG = "BluetoothConnector";
    private char DELIMITER = '#';
    private static final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    private static BluetoothArduino bluetoothArduino = null;

    public static BluetoothArduino getInstance(String n){
        return bluetoothArduino == null ? new BluetoothArduino(n) : bluetoothArduino;
    }

    public static BluetoothArduino getInstance(){
        return bluetoothArduino == null ? new BluetoothArduino() : bluetoothArduino;
    }

    private BluetoothArduino(String name){
        bluetoothArduino = this;

        try{
            for(int i = 0; i < 2048; i++){
                mMessages.add("");
            }

            robotName = name;
            mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBlueAdapter == null){
                Log.i(TAG, "phone does not support bluetooth");
                return;
            }
            if(!isBluetoothEnabled()){
                Log.i(TAG, "bluetooth is not activated");
                return;
            }

            Set<BluetoothDevice> paired = mBlueAdapter.getBondedDevices();
            if(paired.size() > 0){
                for(BluetoothDevice bluetoothDevice : paired){
                    if(bluetoothDevice.getName().equals(robotName)){
                        mBlueDevice = bluetoothDevice;
                        robotFound = true;
                        break;
                    }
                }
            }

            if(!robotFound){
                Log.i(TAG, "there is no devices");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    BluetoothArduino(){
        this("Arduino-Bluetooth");
    }

    public boolean isBluetoothEnabled(){
        return mBlueAdapter.isEnabled();
    }

    public boolean Connect(){
        if(!robotFound){
            return false;
        }
        try{
            Log.i(TAG, "connecting ...");
            UUID uuid = UUID.fromString(BLUETOOTH_UUID);
            mBlueSocket = mBlueDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            mBlueSocket.connect();
            mOut = mBlueSocket.getOutputStream();
            mIn = mBlueSocket.getInputStream();
            connected = true;
            this.start();
            Log.i(TAG, mBlueAdapter.getName());
            Log.i(TAG, "OK");
            return true;
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
            try {
                Log.e("","trying fallback...");

                mBlueSocket =(BluetoothSocket) mBlueDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mBlueDevice,1);
                mBlueSocket.connect();
            }
            catch (Exception e2) {
                Log.e(TAG, e2.getMessage());
                Log.e("", "Couldn't establish Bluetooth connection!");
            }
            return false;
        }
    }

    public void run(){
        while(true){
            if(connected){
                try {
                    byte ch;
                    byte buffer[] = new byte[1024];
                    int i = 0;
                    while((ch = (byte) mIn.read()) != DELIMITER){
                        buffer[i++] = ch;
                    }

                    buffer[i] = '\0';

                    final String msg = new String(buffer);

                    MessageReceived(msg.trim());
                    Log.i(TAG, msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void MessageReceived(String msg){
        try {
            mMessages.add(msg);
            try{
                this.notify();
            }catch(IllegalMonitorStateException e){
                e.printStackTrace();
            }
        }catch(Exception e){
            Log.i(TAG, "failed to receive message");
            e.printStackTrace();
        }

    }

    public boolean hasMensage(int i){
        try{
            String s = mMessages.get(i);
            if(s.length() > 0)
                return true;
            else
                return false;
        } catch (Exception e){
            return false;
        }
    }

    public String getMenssage(int i){
        return mMessages.get(i);
    }

    public void clearMessages(){
        mMessages.clear();
    }

    public int countMessages(){
        return mMessages.size();
    }

    public String getLastMessage(){
        if(countMessages() == 0)
            return "";
        return mMessages.get(countMessages()-1);
    }

    public void SendMessage(String msg){
        try {
            if(connected) {
                mOut.write(msg.getBytes());
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setDelimiter(char d){
        DELIMITER = d;
    }

    public char getDelimiter(){
        return DELIMITER;
    }
}
