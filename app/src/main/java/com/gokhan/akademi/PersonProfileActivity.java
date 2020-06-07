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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private TextView userName,userFullname,userDepartment,userUniversity,userStatus,userSkills,userMeslek;
    private CircleImageView userProfImage;
    private Button SendFriendReqButton, DeclineFriendReqButton,myPost;
    private String senderUserId,receiverUserId, CURRENT_STATE,saveCurrentDate;
    private DatabaseReference FriendRequestRef,  UsersRef, FriendsRef, PostsRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();

        senderUserId = mAuth.getCurrentUser().getUid();


        receiverUserId = getIntent().getExtras().get("visit_userId").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        InitalizeFields();


        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
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


                    Glide.with(PersonProfileActivity.this)
                            .load(myProfileImage)
                            .into(userProfImage);

                    userName.setText("@"+myuserName);
                    userUniversity.setText("University : \n"+myuserUniversity);
                    userDepartment.setText("Department : \n"+myuserDepartment);
                    userFullname.setText(myuserFullname);
                    userStatus.setText(myUserStatus);
                    userSkills.setText("Skills : "+mySkills);
                    userMeslek.setText("Profession : "+myMeslek);



                    MaintananceofButtons();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DeclineFriendReqButton.setVisibility(View.INVISIBLE);
        DeclineFriendReqButton.setEnabled(false);

        if(!senderUserId.equals(receiverUserId))
        {
            SendFriendReqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    SendFriendReqButton.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends"))
                    {
                        SendFriendRequestToaPerson();
                    }
                    if(CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends"))
                    {
                        UnfriendAnExistingFriend();
                    }
                }
            });
        }
        else
        {
            DeclineFriendReqButton.setVisibility(View.INVISIBLE);
            SendFriendReqButton.setVisibility(View.INVISIBLE);
        }


    }

    private void UnfriendAnExistingFriend()
    {
        FriendsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                SendFriendReqButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendReqButton.setText("Send Friend Request");

                                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());


        FriendsRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendsRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {

                                                FriendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        SendFriendReqButton.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        SendFriendReqButton.setText("Unfriend This Person");

                                                                                        DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendReqButton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }

                                        }
                                    });
                        }

                    }
                });


    }

    private void CancelFriendRequest()
    {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                SendFriendReqButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendReqButton.setText("Send Friend Request");

                                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintananceofButtons()
    {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild(receiverUserId))
                        {
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if(request_type.equals("sent"))
                            {
                                CURRENT_STATE ="request_sent";
                                SendFriendReqButton.setText("Cancel Friend Request");

                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                DeclineFriendReqButton.setEnabled(false);
                            }
                            else if(request_type.equals("received"))
                            {
                                CURRENT_STATE = "request_received";
                                SendFriendReqButton.setText("Accept Friend Request");

                                DeclineFriendReqButton.setVisibility(View.VISIBLE);
                                DeclineFriendReqButton.setEnabled(true);

                                DeclineFriendReqButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        CancelFriendRequest();
                                    }
                                });
                            }

                        }
                        else
                        {
                            FriendsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if(dataSnapshot.hasChild(receiverUserId))
                                            {
                                                CURRENT_STATE = "friends";
                                                SendFriendReqButton.setText("Unfriend This Person");

                                                DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendReqButton.setEnabled(false);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequestToaPerson()
    {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                               SendFriendReqButton.setEnabled(true);
                                               CURRENT_STATE = "request_sent";
                                               SendFriendReqButton.setText("Cancel Friend Request");

                                               DeclineFriendReqButton.setVisibility(View.INVISIBLE);
                                               DeclineFriendReqButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void InitalizeFields()
    {

        userName = (TextView) findViewById(R.id.person_username);
        userFullname = (TextView) findViewById(R.id.person_profile_full_name);
        userUniversity = (TextView) findViewById(R.id.person_profile_university);
        userDepartment = (TextView) findViewById(R.id.person_profile_department);
        userProfImage = (CircleImageView) findViewById(R.id.person_profile_pic);
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userSkills = (TextView) findViewById(R.id.person_profile_skills);
        userMeslek = (TextView) findViewById(R.id.person_vasif);



        SendFriendReqButton = (Button) findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendReqButton = (Button) findViewById(R.id.person_decline_friend_request_btn);

        CURRENT_STATE = "not_friends";

    }

    public void btn_action(View view) {

        startActivity(new Intent(getApplicationContext(),Vİew_PDF_Files.class));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){

                        case R.id.bottom_nav_home:
                            SendUserToMainActivity();
                            Toast.makeText(PersonProfileActivity.this,"Home",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_messages:
                            SendUserToMessagesActivity();
                            Toast.makeText(PersonProfileActivity.this,"Messages",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_article:
                            SendUserToArticleActivity();
                            Toast.makeText(PersonProfileActivity.this,"Articles",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_profile:
                            SendUserToProfileActivity();
                            Toast.makeText(PersonProfileActivity.this,"Profile",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }


            };
    private void SendUserToProfileActivity() {
        Intent loginIntent = new Intent(PersonProfileActivity.this, ProfileActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToArticleActivity() {
        Intent loginIntent = new Intent(PersonProfileActivity.this, Vİew_PDF_Files.class);
        startActivity(loginIntent);
    }

    private void SendUserToMessagesActivity() {
        Intent loginIntent = new Intent(PersonProfileActivity.this, FriendsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(PersonProfileActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }


}
