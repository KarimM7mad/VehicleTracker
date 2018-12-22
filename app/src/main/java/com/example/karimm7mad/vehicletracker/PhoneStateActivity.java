package com.example.karimm7mad.vehicletracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class PhoneStateActivity extends AppCompatActivity {

    public String answer = "";
    public Button nextBtn;
    public final static String TAG = "asdasd";
    public Intent gotoReceiverForm;
    public Intent gotoTransmiterForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_state);

        this.gotoTransmiterForm = new Intent(PhoneStateActivity.this, TransmiterForm.class);
        this.gotoReceiverForm = new Intent(PhoneStateActivity.this, ReceiverForm.class);


        this.nextBtn = findViewById(R.id.nextBtn);
        this.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "you chose " + answer, Toast.LENGTH_SHORT).show();
                if (answer.equalsIgnoreCase("transmitter")) {
                    startActivity(gotoTransmiterForm);
                } else if (answer.equalsIgnoreCase("receiver")) {
                    startActivity(gotoReceiverForm);
                }
            }
        });

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.inCarBox:
                if (checked)
                    answer = "transmitter";
                break;
            case R.id.inHandBox:
                if (checked)
                    answer = "receiver";
                break;
        }
    }


    @Override
    public void onBackPressed() {
        this.finish();
        this.moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this.getBaseContext(), "CLOSED THE APP", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
