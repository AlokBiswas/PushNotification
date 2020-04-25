package com.alokbiswas.pushnotification;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "Simplified_Coding";
    private static final String CHANNEL_NAME = "Simplified Coding";
    private static final String CHANNEL_DESC = "Simplified Coding Notifications";

    private EditText etEmail, etPassword;
    private Button signupBtn;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
        signupBtn = findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }




       /* findViewById(R.id.buttonNotify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInformations();
            }
        });*/
    }

    private void createUser() {
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        if (email.isEmpty()){
            etEmail.setError("Email Required");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            etPassword.setError("Password Required");
            etPassword.requestFocus();
            return;
        }
        if (password.length()<6){
            etEmail.setError("Password must be 6 character");
            etEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startProfileActivity();
                        }else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                userLogin(email,password);

                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }

    private void userLogin(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startProfileActivity();
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
           startProfileActivity();
        }
    }

    private void startProfileActivity(){
        Intent intent = new Intent(this,ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



}
