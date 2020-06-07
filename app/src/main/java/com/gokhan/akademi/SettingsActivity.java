package com.gokhan.akademi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    
    private Toolbar mToolbar;
    private EditText userName,userFullname,userCountry,userGender,userDepartment,userUniversity,userStatus,userSkills,userDob;
    private Button updateAccountSettingsButton;
    private CircleImageView userProfImage;

    private DatabaseReference settingUserRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        
        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (EditText) findViewById(R.id.settings_username);
        userFullname = (EditText) findViewById(R.id.settings_profile_full_name);

        userCountry = (EditText) findViewById(R.id.settings_country);
        userUniversity = (EditText) findViewById(R.id.settings_university);
        userDepartment = (EditText) findViewById(R.id.settings_department);
        userProfImage = (CircleImageView) findViewById(R.id.settins_profile_image);
        userStatus = (EditText) findViewById(R.id.settings_status);
        userSkills = (EditText) findViewById(R.id.settings_skills);



        updateAccountSettingsButton = (Button) findViewById(R.id.update_account_settings_button);

        settingUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if( dataSnapshot.exists())
                {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myuserName = dataSnapshot.child("username").getValue().toString();
                    String myuserFullname = dataSnapshot.child("fullname").getValue().toString();
                    String myuserCountry = dataSnapshot.child("country").getValue().toString();

                    String myuserUniversity = dataSnapshot.child("university").getValue().toString();
                    String myuserGender = dataSnapshot.child("gender").getValue().toString();
                    String myuserDepartment = dataSnapshot.child("department").getValue().toString();
                    String myUserStatus = dataSnapshot.child("status").getValue().toString();
                    String mySkills = dataSnapshot.child("skills").getValue().toString();
                    String myDob = dataSnapshot.child("dob").getValue().toString();


                    Glide.with(SettingsActivity.this)
                            .load(myProfileImage)
                            .into(userProfImage);

                    userName.setText(myuserName);
                    userCountry.setText(myuserCountry);
                    userUniversity.setText(myuserUniversity);
                    userDepartment.setText(myuserDepartment);
                    userFullname.setText(myuserFullname);
                    userGender.setText(myuserGender);
                    userStatus.setText(myUserStatus);
                    userSkills.setText(mySkills);
                    userDob.setText(myDob);




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });





    }

    private void ValidateAccountInfo()
    {
        String username = userName.getText().toString();
        String profilename = userFullname.getText().toString();
        String country = userCountry.getText().toString();
        String university = userUniversity.getText().toString();
        String department = userDepartment.getText().toString();
        String gender = userGender.getText().toString();
        String dob = userDob.getText().toString();
        String status = userStatus.getText().toString();
        String skills = userSkills.getText().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Write username ",Toast.LENGTH_SHORT).show();

        }
        else
        {
             UpdateAccountInformation(username,profilename,country,university,department,gender,skills,dob,status);
        }


    }

    private void UpdateAccountInformation(String username, String profilename, String country, String university, String department, String gender, String skills, String dob, String status)
    {

        HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",profilename);
            userMap.put("country",country);
            userMap.put("university",university);
            userMap.put("department",department);
            userMap.put("dob",dob);
            userMap.put("gender",gender);
            userMap.put("skills",skills);
            userMap.put("status",status);
         settingUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
             @Override
             public void onComplete(@NonNull Task task)
             {
                 if(task.isSuccessful())
                 {
                     SendUserToMainActivity();
                     Toast.makeText(SettingsActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                 }
                 else
                 {
                     Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_SHORT).show();

                 }

             }
         });


    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
