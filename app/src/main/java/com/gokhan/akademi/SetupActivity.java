package com.gokhan.akademi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText userName, fullName, countryName, universityName, departmentName, skills;
    private Button saveInformatButton;
    private CircleImageView profileImage;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageRef;
    private Spinner  spinner;
    private String[] meslek={"STUDENT","ACADEMICIAN"};

    private ArrayAdapter<String> dataAdapterForIller;
    String currentUserId;
    final static int gallery_pick = 1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profileImages");
        loadingBar = new ProgressDialog(this);
        spinner = (Spinner) findViewById(R.id.spinner);
        dataAdapterForIller = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, meslek);
        dataAdapterForIller.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapterForIller);



        userName = (EditText) findViewById(R.id.setup_username);
        fullName = (EditText) findViewById(R.id.setup_fullname);
        countryName = (EditText) findViewById(R.id.setup_country);
        universityName = (EditText) findViewById(R.id.setup_universityt);
        departmentName = (EditText) findViewById(R.id.setup_department);
        skills = (EditText) findViewById(R.id.setup_skills);
        saveInformatButton = (Button) findViewById(R.id.setup_button);
        profileImage = (CircleImageView) findViewById(R.id.setup_profile_image);



        saveInformatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveAccountSetupInformation();
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallery_pick );
            }
        });


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profileimage").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.child(currentUserId+".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();

                            usersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void SaveAccountSetupInformation() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String country = countryName.getText().toString();
        String department = departmentName.getText().toString();
        String university = universityName.getText().toString();
        String skill = skills.getText().toString();

        if (TextUtils.isEmpty(username))
            Toast.makeText(this, "Username must be assigned", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(fullname))
            Toast.makeText(this, "Full name must be assigned", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(country))
            Toast.makeText(this, "Country must be assigned", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(department))
            Toast.makeText(this, "Departmen must be defined", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(university))
            Toast.makeText(this, "University must be assigned", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(skill))
            Toast.makeText(this, "Skills must be assigned", Toast.LENGTH_SHORT).show();
        else {


            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("We're creating a new account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            String text = spinner.getSelectedItem().toString();
            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("country", country);
            userMap.put("department", department);
            userMap.put("university", university);
            userMap.put("skills", skill);
            userMap.put("status", "true");
            userMap.put("gender", "gender");
            userMap.put("dob", "dob");
            userMap.put("relationshipStatus", text);

            usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this,"Succesfull",Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,"Error occured"+message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });



    }
}

    private void SendUserToMainActivity() {


        Intent setupIntent = new Intent(SetupActivity.this, MainActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

}