package com.gokhan.akademi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private TextView userName,userFullname,userDepartment,userUniversity,userStatus,userSkills,userMeslek;
    private CircleImageView userProfImage;
    private Button myPost;
    private DatabaseReference profileUserRef,PostsRef;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private  int countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = (TextView) findViewById(R.id.my_username);
        userFullname = (TextView) findViewById(R.id.my_profile_full_name);
        userUniversity = (TextView) findViewById(R.id.my_profile_university);
        userDepartment = (TextView) findViewById(R.id.my_profile_department);
        userProfImage = (CircleImageView) findViewById(R.id.my_profile_pic);
        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userSkills = (TextView) findViewById(R.id.my_profile_skills);
        userMeslek = (TextView) findViewById(R.id.vasif);

        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);





        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myuserName = dataSnapshot.child("username").getValue().toString();
                    String myuserFullname = dataSnapshot.child("fullname").getValue().toString();
                    String myuserUniversity = dataSnapshot.child("university").getValue().toString();
                    String myuserDepartment = dataSnapshot.child("department").getValue().toString();
                    String myUserStatus = dataSnapshot.child("status").getValue().toString();
                    String mySkills = dataSnapshot.child("skills").getValue().toString();
                    String myMeslek = dataSnapshot.child("relationshipStatus").getValue().toString();

                    Glide.with(ProfileActivity.this)
                            .load(myProfileImage)
                            .into(userProfImage);

                    userName.setText("@"+myuserName);
                    userUniversity.setText("University : \n"+myuserUniversity);
                    userDepartment.setText("Department : \n"+myuserDepartment);
                    userFullname.setText(myuserFullname);
                    userStatus.setText(myUserStatus);
                    userSkills.setText("Skills : "+mySkills);
                    userMeslek.setText("Profession : "+myMeslek);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){

                        case R.id.bottom_nav_home:
                            SendUserToMainActivity();
                            Toast.makeText(ProfileActivity.this,"Home",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_messages:
                            SendUserToMessagesActivity();
                            Toast.makeText(ProfileActivity.this,"Messages",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_article:
                            SendUserToArticleActivity();
                            Toast.makeText(ProfileActivity.this,"Articles",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_profile:
                            SendUserToProfileActivity();
                            Toast.makeText(ProfileActivity.this,"Profile",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }


            };
    private void SendUserToProfileActivity() {
        Intent loginIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToArticleActivity() {
        Intent loginIntent = new Intent(ProfileActivity.this, Vİew_PDF_Files.class);
        startActivity(loginIntent);
    }

    private void SendUserToMessagesActivity() {
        Intent loginIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }


    public void btn_action(View view) {

        startActivity(new Intent(getApplicationContext(),Vİew_PDF_Files.class));
    }
}
