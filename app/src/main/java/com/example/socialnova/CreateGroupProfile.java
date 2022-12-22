package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupProfile extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");

    CircleImageView civ;
    EditText name,about;
    Button create;
    String uid, members;
    Uri currentUri;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_profile);
        Intent i = getIntent();
        uid = i.getStringExtra("uid");
        members = i.getStringExtra("members");
        members=members+uid;
        civ = findViewById(R.id.groupProfile);
        name = findViewById(R.id.groupName);
        about = findViewById(R.id.groupAbout);
        create = findViewById(R.id.groupCreate);
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a picture"), 001);
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Name = name.getText().toString().trim();
                groupName=Name;
                final String About = about.getText().toString().trim();
                if(Name.isEmpty() || About.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill all tabs",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(currentUri == null){
                        Toast.makeText(getApplicationContext(),"Please select a profile picture",Toast.LENGTH_SHORT).show();
                    }
                    databaseReference.child("groups").child(members).child("name").setValue(Name);
                    databaseReference.child("groups").child(members).child("about").setValue(About);
                    databaseReference.child("groups").child(members).child("admin").setValue(uid);
                    uploadPicture();

                }
            }
        });
    }
    protected void onActivityResult(
            int requestCode, int resultCode, final Intent data) {

        if (requestCode == 001 && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "Unable to select picture", Toast.LENGTH_SHORT).show();
                return;
            } else {
                currentUri = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),currentUri);
                    civ.setImageBitmap(bitmap);
                }catch(IOException ee){
                    ee.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void uploadPicture(){
        StorageReference imageReference = storageReference.child(members).child("images").child("profile_picture");
        UploadTask uploadTask = imageReference.putFile(currentUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error while uploading profile picture",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        databaseReference.child("groups").child(members).child("img_url").setValue(task.getResult().toString());
                        Intent i = new Intent(CreateGroupProfile.this, groupChat.class);
                        i.putExtra("UID",uid);
                        i.putExtra("members",members);
                        i.putExtra("name",groupName);
                        i.putExtra("uri",task.getResult().toString());
                        startActivity(i);
                        finish();

                    }
                });

            }

        });

    }


}