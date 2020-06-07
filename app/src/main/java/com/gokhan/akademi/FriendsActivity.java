package com.gokhan.akademi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private DatabaseReference friendsRef, usersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);



        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        myFriendList = (RecyclerView) findViewById(R.id.friend_list);
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();

    }


    private void DisplayAllFriends() {

        Query query = friendsRef.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int position, @NonNull Friends friends) {

                friendsViewHolder.setDate(friends.getDate());
                final String usersIDs = getRef(position).getKey();

                usersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String username = dataSnapshot.child("fullname").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileimage").getValue().toString();

                            friendsViewHolder.setFullName(username);
                            friendsViewHolder.setProfileimage(getApplicationContext(), profileimage);

                            friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    username  + "'s Profile",
                                                    "Send Message"
                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                                    builder.setTitle("Select Options");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                          if(which == 0)
                                          {
                                              Intent profileintent = new Intent(FriendsActivity.this,PersonProfileActivity.class);
                                              profileintent.putExtra("visit_userId",usersIDs);

                                              startActivity(profileintent);
                                          }
                                          if(which==1)
                                          {
                                              Intent chatintent = new Intent(FriendsActivity.this,ChatActivity.class);
                                              chatintent.putExtra("visit_userId",usersIDs);
                                              chatintent.putExtra("userName",username);
                                              startActivity(chatintent);
                                          }
                                        }
                                    });
                                    builder.show();

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent ,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        myFriendList.setAdapter(adapter);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){

                        case R.id.bottom_nav_home:
                            SendUserToMainActivity();
                            Toast.makeText(FriendsActivity.this,"Home",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_messages:
                            SendUserToMessagesActivity();
                            Toast.makeText(FriendsActivity.this,"Messages",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_article:
                            SendUserToArticleActivity();
                            Toast.makeText(FriendsActivity.this,"Articles",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_profile:
                            SendUserToProfileActivity();
                            Toast.makeText(FriendsActivity.this,"Profile",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }


            };
    private void SendUserToProfileActivity() {
        Intent loginIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToArticleActivity() {
        Intent loginIntent = new Intent(FriendsActivity.this, Vİew_PDF_Files.class);
        startActivity(loginIntent);
    }

    private void SendUserToMessagesActivity() {
        Intent loginIntent = new Intent(FriendsActivity.this, FriendsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(FriendsActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }
    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(Context applicationContext, String profileimage)
        {

            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            StorageReference imageRef = storageReference.child(profileimage);
            Glide.with(FriendsActivity.this)
                    .load(profileimage)
                    .into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_name);
            myName.setText(fullName);
        }

        public void setDate(String date){
            TextView friendsDate = (TextView) mView.findViewById(R.id.all_users_profile_status);
            friendsDate.setText("Friends scince: " + date);
        }
    }

}
