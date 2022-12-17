package com.example.controllerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Button button;
    CompoundButton theme;

    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        theme = findViewById(R.id.switchtheme);

        SharedPreferences sharedPreferences=getSharedPreferences("theme",MODE_PRIVATE);
        theme.setChecked(sharedPreferences.getBoolean("darkmode",false));

        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (theme.isChecked())
                {
                    Toast.makeText(getApplicationContext(),"Dark Mode turned ON",Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SharedPreferences.Editor editor=getSharedPreferences("theme",MODE_PRIVATE).edit();
                    editor.putBoolean("darkmode",true);
                    editor.apply();
                    theme.setChecked(true);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Dark Mode turned OFF",Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SharedPreferences.Editor editor=getSharedPreferences("theme",MODE_PRIVATE).edit();
                    editor.putBoolean("darkmode",false);
                    editor.apply();
                    theme.setChecked(false);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Executor executor = ContextCompat.getMainExecutor(getApplicationContext());

                biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(), "Login Error: "+errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ControllerActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Authentication").setDescription("Use Fingerprint to Login").setDeviceCredentialAllowed(true).build();

                biometricPrompt.authenticate(promptInfo);
            }
        });
    }
}