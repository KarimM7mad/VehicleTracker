package com.example.karimm7mad.vehicletracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ReceiverForm extends AppCompatActivity {

    public String userName, userEmail, carName, carNum, carColor, numToCall;
    public EditText userNameEditTxt, userEmailEditTxt, carNameEditTxt, carNumEditTxt, carColorEditTxt, numForCarToCallEditText;

    public Button submitBtn;

    public DatabaseReference firebaseDBMan;
    public DBAdapter localDBman;

    public Intent goToReceiverMapIntent;
    public AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_form);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.goToReceiverMapIntent = new Intent(ReceiverForm.this, ReceiverMapsActivity.class);
        this.firebaseDBMan = FirebaseDatabase.getInstance().getReference();
        //user info
        this.userNameEditTxt = findViewById(R.id.userNameEditTxt);
        this.userEmailEditTxt = findViewById(R.id.userEmailEditTxt);
        //car info
        this.carNameEditTxt = findViewById(R.id.carNameEditTxt);
        this.carNumEditTxt = findViewById(R.id.carNumEditTxt);
        this.carColorEditTxt = findViewById(R.id.carColorEditTxt);
        this.numForCarToCallEditText = findViewById(R.id.numForCarTocallEditTxt);
        //submit btns
        this.submitBtn = findViewById(R.id.submitBtnRe);
        this.submitBtn.setOnClickListener(new View.OnClickListener() {
            ArrayList<String> prevMailsBuffer = new ArrayList<>();

            @Override
            public void onClick(View v) {
                userName = userNameEditTxt.getText().toString();
                userEmail = userEmailEditTxt.getText().toString();
                carName = carNameEditTxt.getText().toString();
                carNum = carNumEditTxt.getText().toString();
                carColor = carColorEditTxt.getText().toString();
                numToCall = numForCarToCallEditText.getText().toString();

                if (!prevMailsBuffer.isEmpty()) {
                    if (prevMailsBuffer.contains(userEmail)) {
                        return;
                    } else {
                        prevMailsBuffer.add(userEmail);
                    }
                } else {
                    prevMailsBuffer.add(userEmail);
                }


                boolean b1 = (Patterns.EMAIL_ADDRESS).matcher(userEmail).matches();
                boolean b2 = (Patterns.PHONE).matcher(numToCall).matches();
                boolean b3 = userName.isEmpty() || carName.isEmpty() || carNum.isEmpty() || carColor.isEmpty();

                if (b1 && b2 && !b3) {
                    //save User In FireBase Database
                    String userID = firebaseDBMan.child("Users").push().getKey();
                    Userr usrToSave = new Userr(userName, userEmail);
                    firebaseDBMan.child("Users").child(userID).setValue(usrToSave);
                    //save Car Data in Firebase Database
                    firebaseDBMan = FirebaseDatabase.getInstance().getReference("Cars").child(userID);
                    String carID = firebaseDBMan.push().getKey();
                    Car carToSave = new Car(carName, carNum, carColor, numToCall, 0, 0);
                    firebaseDBMan.child(carID).setValue(carToSave);
                    firebaseDBMan = firebaseDBMan.getRoot();
                    showPopUp(carID,userID);

                    localDBman = new DBAdapter(getBaseContext());
                    localDBman.open();
                    localDBman.deleteAll();
                    localDBman.insertRow("USER_" + userID);
                    localDBman.close();
                    goToReceiverMapIntent.putExtra("userkey", userID);
                    startActivity(goToReceiverMapIntent);
                } else {
                    Toast.makeText(getBaseContext(), "Check Your Data and Try Again", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void showPopUp(String carKey, String userkey) {
        this.builder = new AlertDialog.Builder(this);
        this.builder.setTitle("Keys for Transmiter");
        this.builder.setMessage("Car Key:\n\"" + carKey + "\"\nUser Key:\n\"" + userkey + "\"");
        this.builder.setNeutralButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        this.builder.create().show();
    }
}
