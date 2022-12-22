package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class groupChat extends AppCompatActivity {
    String members;
    CircleImageView civ;
    TextView name,back,send;
    EditText chat;
    RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
    String sname;
    String uid;
    Uri img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Intent i = getIntent();

        try{
            sname = i.getStringExtra("name");
            members = i.getStringExtra("members");
            uid = i.getStringExtra("UID");
            img = i.getParcelableExtra("uri");
        }catch(Exception e){
            name.setText(e.toString());
        }

        civ=findViewById(R.id.gchatProfile);
        name=findViewById(R.id.gchatName);
        back=findViewById(R.id.gbackArrow);
        chat = findViewById(R.id.gchatContent);
        send=findViewById(R.id.gsendChat);
        name.setText(sname);
        recyclerView=findViewById(R.id.grecyclerChat);
        Picasso.get().load(img).fit().centerInside().into(civ);

        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        databaseReference.child("groups").child(members).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for(DataSnapshot dt : snapshot.getChildren()){
                    MessageModel model = dt.getValue(MessageModel.class);
                    messageModels.add(model);
                    final ChatAdapter2 chatAdapter = new ChatAdapter2(messageModels,getApplicationContext());
                    recyclerView.setAdapter(chatAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    chatAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("UID",uid);
                startActivity(i);
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chat.getText().toString();
                final MessageModel model = new MessageModel(uid,message);
                model.setTimestamp(new Date().getTime());
                chat.setText("");
                databaseReference.child("groups").child(members).child("messages").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
        });


    }
}