package com.gokhan.akademi;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Vİew_PDF_Files extends AppCompatActivity {

    ListView myListView;
    DatabaseReference databaseReference,userReference;
    List<uploadPdf> uploadPDFs;
    private FirebaseAuth mAuth;
    private String current_userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__pdf__files);
        mAuth = FirebaseAuth.getInstance();
        current_userID = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("uploads").child("uid");

        myListView = (ListView) findViewById(R.id.myListView);

        uploadPDFs = new ArrayList<>();

        viewAllFiles();

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                uploadPdf uploadPDF = uploadPDFs.get(position);

                Intent intent =new Intent();
                intent.setType(Intent.ACTION_VIEW);
                Uri uri =Uri.parse(uploadPDF.getUrl()); // Database path
                if(uri.toString().contains(".pdf"))
                {
                    intent.setDataAndType(uri,"application/pdf");
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

    private void viewAllFiles()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("uploads");
        final DatabaseReference uid = databaseReference.child("uid");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uid = (String) dataSnapshot.child("uid").getValue();



                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        uploadPdf uploadPdf = postSnapshot.getValue(com.gokhan.akademi.uploadPdf.class);
                        if(postSnapshot.child("uid").getValue().equals(current_userID)) {
                            uploadPDFs.add(uploadPdf);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"There is no article",Toast.LENGTH_SHORT).show();


                        }
                    }

                final String[] uploads = new String[uploadPDFs.size()];

                for (int i = 0; i < uploads.length; i++) {
                    uploads[i] = uploadPDFs.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, uploads){


                    @Override
                    public View getView(int position, View convertView,  ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        TextView myText = (TextView) view.findViewById(android.R.id.text1);
                        myText.setTextColor(Color.BLACK);


                        return view;
                    }
                };





                myListView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
