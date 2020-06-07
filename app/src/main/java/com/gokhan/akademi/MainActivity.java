package com.gokhan.akademi;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout   drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView   postList;
    private Toolbar        mToolbar;
    private ImageButton  addNewPost;
    public ImageView postImage;
    private FirebaseAuth  mAuth;
    private DatabaseReference usersRef,postsRef;
    private CircleImageView navProfileImage;
    private TextView        navProfileUserName;
     String currenUserID;
    StorageReference storageReference;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currenUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        postImage = (ImageView) findViewById(R.id.post_image);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        addNewPost = (ImageButton) findViewById(R.id.add_new_post_button);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);


        postList = (RecyclerView) findViewById(R.id.user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        navProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        navProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);
        usersRef.child(currenUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        navProfileUserName.setText(fullname);
                    }

                    if(dataSnapshot.hasChild("profileimage"))
                    {

                        String image= dataSnapshot.child("profileimage").getValue().toString();

                        StorageReference imageRef = storageReference.child("profileImages").child(image);
                        Glide.with(MainActivity.this).load(image).into(navProfileImage);


                    }
                    else
                    {

                        Toast.makeText(MainActivity.this,"Resim yüklenemiyor",Toast.LENGTH_SHORT).show();
                    }




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                userMenuSelector(item);
                return false;
            }
        });

        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToPostActivity();
            }
        });
        
        DisplayAllUsersPosts();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){

                        case R.id.bottom_nav_home:
                            SendUserToMainActivity();
                            Toast.makeText(MainActivity.this,"Home",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_messages:
                            SendUserToMessagesActivity();
                            Toast.makeText(MainActivity.this,"Messages",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_article:
                            SendUserToArticleActivity();
                            Toast.makeText(MainActivity.this,"Articles",Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.bottom_nav_profile:
                            SendUserToProfileActivity();
                            Toast.makeText(MainActivity.this,"Profile",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }


            };

    private void SendUserToArticleActivity() {
        Intent loginIntent = new Intent(MainActivity.this, Vİew_PDF_Files.class);
        startActivity(loginIntent);

    }

    private void SendUserToMessagesActivity() {
        Intent loginIntent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(loginIntent);
    }


    private void DisplayAllUsersPosts()
    {
        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(postsRef, Posts.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_posts_layout, parent, false);

                return new PostsViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(PostsViewHolder viewHolder, int position, Posts model) {


                viewHolder.setFullname(model.getFullname());
                viewHolder.setTime(model.getTime());
                viewHolder.setDate(model.getDate());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                viewHolder.setPostimage(getApplicationContext(),model.getPostimage());

            }


        };
        adapter.startListening();
        postList.setAdapter(adapter);
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname){
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }
        public void  setTime(String time){

            TextView postTime = (TextView) mView.findViewById(R.id.post_time);
            postTime.setText("  "+time);
        }
        public void  setDate(String date){

            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("    " + date);
        }
        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText("Description : "+description);
        }
        public void setPostimage(Context applicationContext, String postimage) {

            ImageView Postimage = (ImageView) mView.findViewById(R.id.post_image);

            StorageReference imageRef = storageReference.child(postimage);

            Glide.with(MainActivity.this)
                    .load(postimage)
                    .apply(new RequestOptions())
                    .into(Postimage);


        }

        public void setProfileimage(Context applicationContext, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            StorageReference imageRef = storageReference.child(profileimage);
            Glide.with(MainActivity.this)
                    .load(profileimage)
                    .into(image);
        }



    }

    private void SendUserToPostActivity() {

        Intent addNewPostIntent = new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewPostIntent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            checkUserExistence();
        }
    }

    private void checkUserExistence()
    {
        final String user_current_id = mAuth.getCurrentUser().getUid();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user_current_id)){
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }


    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userMenuSelector(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.nav_post:
                SendUserToPostActivity();
                break;

            case R.id.nav_profile:
                SendUserToProfileActivity();
                Toast.makeText(this,"Profile Activity",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                SendUserToPdfActivity();
                Toast.makeText(this,"Pdf Activity",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_follower:
                SendUserToFriendsActivity();
                Toast.makeText(this,"Follower",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_find_people:
                SendUserToFindFriendsActivity();
                Toast.makeText(this,"Find People",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_messages:
                SendUserToFriendsActivity();
                Toast.makeText(this,"Messages",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                SendUserToSettingsActivity();
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;


        }
    }

    private void SendUserToFriendsActivity() {
        Intent loginIntent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToProfileActivity() {
        Intent loginIntent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(loginIntent);
    }
    private void SendUserToFindFriendsActivity() {
        Intent loginIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToSettingsActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(loginIntent);

    }
    private void SendUserToPdfActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, PdfActivity.class);
        startActivity(loginIntent);

    }
}
