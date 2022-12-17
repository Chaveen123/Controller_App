package com.example.controllerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ControllerActivity extends AppCompatActivity{

    ToggleButton relaytogmanual,relaytogauto;
    TextView temperature;
    String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferencerel = database.getReference("relay");
        DatabaseReference databaseReferencetemp = database.getReference("temperature");

        relaytogmanual = findViewById(R.id.relaytogmanual);
        relaytogauto = findViewById(R.id.relaytogauto);
        temperature = findViewById(R.id.temp);

        SharedPreferences sharedPreferences=getSharedPreferences("save",MODE_PRIVATE);
        relaytogmanual.setChecked(sharedPreferences.getBoolean("manual",false));
        relaytogauto.setChecked(sharedPreferences.getBoolean("auto",false));

        databaseReferencetemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                temp = dataSnapshot.getValue().toString();
                temperature.setText(new BigDecimal(temp).round(new MathContext(3, RoundingMode.HALF_UP)).toString()+"°C");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        databaseReferencerel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        relaytogmanual.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                databaseReferencerel.setValue(1);
                showMessage("Manual Temperature Control is ON");
                SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                editor.putBoolean("manual",true);
                editor.apply();
                relaytogmanual.setChecked(true);
            }else
            {
                databaseReferencerel.setValue(0);
                showMessage("Manual Temperature Control is OFF");
                SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                editor.putBoolean("manual",false);
                editor.apply();
                relaytogmanual.setChecked(false);
            }
        });
        relaytogauto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
            {
                databaseReferencerel.setValue(2);
                showMessage("Automatic Temperature Control is ON");
                SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                editor.putBoolean("auto",true);
                editor.apply();
                relaytogauto.setChecked(true);
            }else
            {
                databaseReferencerel.setValue(3);
                showMessage("Automatic Temperature Control is OFF");
                SharedPreferences.Editor editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                editor.putBoolean("auto",false);
                editor.apply();
                relaytogauto.setChecked(false);
            }
        });
    }
    private void showMessage(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}