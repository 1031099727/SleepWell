package com.example.a10310.sleepwell;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ToSend implements Parcelable {
    private BluetoothGatt btg;
    private BluetoothGattCharacteristic btc;

    public ToSend() {

    }

    public BluetoothGatt getBtg() {
        return btg;
    }
    public BluetoothGattCharacteristic getBtc() {
        return btc;
    }
    public void setBtg(BluetoothGatt btg) {
        this.btg =btg;
    }
    public void setBtc(BluetoothGattCharacteristic btc) {
        this.btc = btc;
    }

    protected ToSend(Parcel in) {
    }

    public static final Creator<ToSend> CREATOR = new Creator<ToSend>() {
        @Override
        public ToSend createFromParcel(Parcel in) {
            return new ToSend(in);
        }

        @Override
        public ToSend[] newArray(int size) {
            return new ToSend[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
