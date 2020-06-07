package com.gokhan.akademi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, needNewAccountLink;
    private EditText userEmail, userPassword;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail        =  (EditText)  findViewById(R.id.login_email);
        userPassword     =  (EditText)  findViewById(R.id.login_password);
        loginButton      =  (Button)    findViewById(R.id.login_button);
        needNewAccountLink = (Button) findViewById(R.id.login_register_navigator);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowingToUser();
            }
        });


        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent ourIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(ourIntent);
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void AllowingToUser() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please write your email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password",Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait a while...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                SendUserToMainActivity();

                                Toast.makeText(LoginActivity.this,"You're logged in succesfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {

                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error occured "+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }



}
