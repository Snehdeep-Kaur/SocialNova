package com.example.socialnova;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private String UID;
    CircleImageView civ;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
    EditText name,about,phone;
    TextView changePic,editAbout,editName;
    Uri currentUri;
    AppCompatButton changeProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UID=getArguments().getString("UID");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = view.findViewById(R.id.showname);
        about = view.findViewById(R.id.abt);
        phone = view.findViewById(R.id.phn);
        civ = view.findViewById(R.id.civ2);
        editAbout=view.findViewById(R.id.editAbout);
        editName=view.findViewById(R.id.editName);
        changePic=view.findViewById(R.id.changePic);
        changeProfile=view.findViewById(R.id.changeProfile);
        storageReference.child(UID).child("images").child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerInside().into(civ);
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String names = snapshot.child("users").child(UID).child("name").getValue(String.class);
                String abouts = snapshot.child("users").child(UID).child("about").getValue(String.class);
                name.setText(names);
                about.setText(abouts);
                phone.setText(UID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Unable to fetch profile",Toast.LENGTH_SHORT).show();

            }
        });
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a picture"), 001);
            }
        });
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setFocusableInTouchMode(true);
            }
        });
        editAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about.setFocusableInTouchMode(true);
            }
        });
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = name.getText().toString().trim();
                String newAbout = about.getText().toString().trim();
                databaseReference.child("users").child(UID).child("name").setValue(newName);
                databaseReference.child("users").child(UID).child("about").setValue(newAbout);
                if(currentUri!=null){
                    uploadPicture();
                }
                name.setFocusable(false);
                about.setFocusable(false);
                Toast.makeText(getContext(),"Profile updated successfully",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    public void onActivityResult(
            int requestCode, int resultCode, final Intent data) {

        if (requestCode == 001 && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(getContext(), "Unable to select picture", Toast.LENGTH_SHORT).show();
                return;
            } else {
                currentUri = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),currentUri);
                    civ.setImageBitmap(bitmap);
                }catch(IOException ee){
                    ee.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    private void uploadPicture(){
        StorageReference imageReference = storageReference.child(UID).child("images").child("profile_picture");
        UploadTask uploadTask = imageReference.putFile(currentUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error while uploading profile picture",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        databaseReference.child("users").child(UID).child("img_url").setValue(task.getResult().toString());
                    }
                });

            }

        });
    }
}