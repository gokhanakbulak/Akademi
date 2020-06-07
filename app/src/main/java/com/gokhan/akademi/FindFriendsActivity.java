package com.gokhan.akademi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton  searchButton;
    private EditText searchInputText;

    private RecyclerView searchResultList;
    private StorageReference storageReference;

    private DatabaseReference allUsersDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        storageReference = FirebaseStorage.getInstance().getReference();


        mToolbar = (Toolbar) findViewById(R.id.find_friends_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find People ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = (ImageButton) findViewById(R.id.search_people_button);
        searchInputText = (EditText) findViewById(R.id.search_box_input);
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String searchBoxInput = searchInputText.getText().toString();

                SearchPeopleAndFriends(searchBoxInput);

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
                            Toast.makeText(FindFriendsActivity.this,"Home",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_messages:
                            SendUserToMessagesActivity();
                            Toast.makeText(FindFriendsActivity.this,"Messages",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_article:
                            SendUserToArticleActivity();
                            Toast.makeText(FindFriendsActivity.this,"Articles",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_profile:
                            SendUserToProfileActivity();
                            Toast.makeText(FindFriendsActivity.this,"Profile",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }


            };
    private void SendUserToProfileActivity() {
        Intent loginIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToArticleActivity() {
        Intent loginIntent = new Intent(FindFriendsActivity.this, Vİew_PDF_Files.class);
        startActivity(loginIntent);
    }

    private void SendUserToMessagesActivity() {
        Intent loginIntent = new Intent(FindFriendsActivity.this, FriendsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(FindFriendsActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void SearchPeopleAndFriends(String searchBoxInput)
    {

        Toast.makeText(this, "Searching..", Toast.LENGTH_LONG).show();

        Query searchPeopleAndFriendsQuery = allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

        FirebaseRecyclerOptions<FindFriends> options =
                new FirebaseRecyclerOptions.Builder<FindFriends>()
                        .setQuery(searchPeopleAndFriendsQuery, FindFriends.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> (options)
        {


            @Override
            public FindFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_display_layout,parent,false);
                return new FindFriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FindFriendsViewHolder viewHolder, final int i, FindFriends model)
            {
                viewHolder.setFullname(model.getFullname());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setProfileimage(model.getProfileimage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String visit_userId = getRef(i).getKey();
                        Intent profileIntent = new Intent(FindFriendsActivity.this,PersonProfileActivity.class);
                        profileIntent.putExtra("visit_userId",visit_userId);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        adapter.startListening();
        searchResultList.setAdapter(adapter);
    }


    public class FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public FindFriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.all_users_profile_name);
            username.setText(fullname);

        }

        public void setStatus(String status)
        {
            TextView username = (TextView) mView.findViewById(R.id.all_users_profile_status);
            username.setText(status);
        }

        public void setProfileimage(String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            StorageReference imageRef = storageReference.child(profileimage);
            Glide.with(FindFriendsActivity.this)
                    .load(profileimage)
                    .into(image);
        }
    }

}
