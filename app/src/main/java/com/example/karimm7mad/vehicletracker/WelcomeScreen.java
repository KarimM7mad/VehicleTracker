package com.example.karimm7mad.vehicletracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeScreen extends AppCompatActivity {
    public static final String TAG = "asdasd";
    public DBAdapter keyStoreDBman = null;
    public Intent gotoReceiverMapIntent = null;
    public Intent gotoTransmiterMapIntent = null;
    public Intent gotoPhoneStateIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.gotoReceiverMapIntent = new Intent(WelcomeScreen.this, ReceiverMapsActivity.class);
        this.gotoTransmiterMapIntent = new Intent(WelcomeScreen.this, TransmiterMapsActivity.class);
        this.gotoPhoneStateIntent = new Intent(WelcomeScreen.this, PhoneStateActivity.class);
        //create the Database Object
        this.keyStoreDBman = new DBAdapter(this);
        this.keyStoreDBman.open();
        //get the stored key
        Cursor c = this.keyStoreDBman.getAllRows();
        if (c.moveToFirst()) {
            // Already assigned APP
            int id = c.getInt(DBAdapter.COL_ROWID);
            String jsonKeyToUse = c.getString(DBAdapter.COL_JSONKEY_NAME);
            //if this app is a user mode
            if (jsonKeyToUse.split("_")[0].equalsIgnoreCase("USER")) {
                jsonKeyToUse = jsonKeyToUse.substring(jsonKeyToUse.indexOf("_") + 1, jsonKeyToUse.length());
                this.gotoReceiverMapIntent.putExtra("userkey", jsonKeyToUse);
                startActivity(this.gotoReceiverMapIntent);
            }
            //if this app is a Vehicle mode
            else if (jsonKeyToUse.split("_")[0].equalsIgnoreCase("CAR")) {
                jsonKeyToUse = jsonKeyToUse.substring(jsonKeyToUse.indexOf("_") + 1, jsonKeyToUse.length());
                this.gotoTransmiterMapIntent.putExtra("carkey", jsonKeyToUse);
                if (c.moveToNext()) {
                    id = c.getInt(DBAdapter.COL_ROWID);
                    jsonKeyToUse = c.getString(DBAdapter.COL_JSONKEY_NAME);
                    if (jsonKeyToUse.split("_")[0].equalsIgnoreCase("USER")) {
                        jsonKeyToUse = jsonKeyToUse.substring(jsonKeyToUse.indexOf("_") + 1, jsonKeyToUse.length());
                        this.gotoTransmiterMapIntent.putExtra("userkey", jsonKeyToUse);
                        startActivity(this.gotoTransmiterMapIntent);
                    }
                }
            }
        } else {
            // Unassigned APP
            startActivity(this.gotoPhoneStateIntent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.keyStoreDBman.close();
    }


}
