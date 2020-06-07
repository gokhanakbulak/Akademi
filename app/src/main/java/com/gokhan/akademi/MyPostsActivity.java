package com.gokhan.akademi;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView myPostsLists;
    private FirebaseAuth mAuth;
    private DatabaseReference postsRef;
    private String currentUserID;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        mToolbar = (Toolbar) findViewById(R.id.my_posts_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        myPostsLists = (RecyclerView) findViewById(R.id.my_all_posts_list);

        myPostsLists.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostsLists.setLayoutManager(linearLayoutManager);

        DisplayAllMyPosts();

    }

    private void DisplayAllMyPosts()
    {

    }

    public class myPostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public myPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setFullname(String fullname){
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }
        public void setProfileimage(Context applicationContext, String profileimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            StorageReference imageRef = storageReference.child(profileimage);
            Glide.with(MyPostsActivity.this)
                    .load(profileimage)
                    .into(image);
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
            Glide.with(MyPostsActivity.this)
                    .load(postimage)
                    .into(Postimage);


        }
    }

}
