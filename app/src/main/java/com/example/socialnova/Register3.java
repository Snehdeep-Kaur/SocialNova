package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register3 extends AppCompatActivity {
    Button b1;
    CircleImageView civ;
    EditText name,about;
    String phoneno;
    Uri currentUri;
    String uid;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences pref;
    private static final String FILE_NAME = "LoginActivity";
    private String PARAM_ONE = "userLogState";
    private String PARAM_TWO = "profileId";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        Intent i = getIntent();
        phoneno = i.getStringExtra("PhoneNo");
        uid = i.getStringExtra("UID");
        b1 = findViewById(R.id.createProfile);
        civ = findViewById(R.id.civ);
        name = findViewById(R.id.showname);
        about = findViewById(R.id.abt);
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a picture"), 001);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String Name = name.getText().toString().trim();
                final String About = about.getText().toString().trim();
                if(Name.isEmpty() || About.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill all tabs",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(currentUri == null){
                        Toast.makeText(getApplicationContext(),"Please select a profile picture",Toast.LENGTH_SHORT).show();
                    }
                    databaseReference.child("users").child(phoneno).child("name").setValue(Name);
                    databaseReference.child("users").child(phoneno).child("about").setValue(About);
                    databaseReference.child("users").child(phoneno).child("log_report").setValue("logged_in");
                    databaseReference.child("users").child(phoneno).child("phoneno").setValue(phoneno);
                    Toast.makeText(getApplicationContext(),"User Created, Uploading profile picture",Toast.LENGTH_SHORT).show();
                    uploadPicture();
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences(FILE_NAME,MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PARAM_ONE,"logged_in");
                    editor.putString(PARAM_TWO,phoneno);
                    editor.clear();
                    editor.commit();
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
        StorageReference imageReference = storageReference.child(phoneno).child("images").child("profile_picture");
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
                        databaseReference.child("users").child(phoneno).child("img_url").setValue(task.getResult().toString());
                        Intent i = new Intent(Register3.this, MainActivity.class);
                        i.putExtra("UID",phoneno);
                        startActivity(i);
                        finish();

                    }
                });

            }

        });

    }
}