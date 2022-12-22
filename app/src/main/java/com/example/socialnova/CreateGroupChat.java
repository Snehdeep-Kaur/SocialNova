package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CreateGroupChat extends AppCompatActivity {
    List<String> phoneContacts = new ArrayList<String>();
    HashMap<String,String> contactNames = new HashMap<>();
    List<String> matchingNames = new ArrayList<String>();
    List<String> matchingContacts = new ArrayList<String>();
    List<Uri> profiles = new ArrayList<Uri>();
    List<String> statusArray = new ArrayList<String>();

    private String phoneno;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");

    ListView listView;
    Button create;
    TextView t1;
    int flag = 1;
    List<String> checkedpos = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat);
        listView=findViewById(R.id.groupSelect);
        create = findViewById(R.id.createGroup);
        Intent i = getIntent();
        phoneno = i.getStringExtra("uid");
        phoneContacts=getAllContacts();
        getAllUserContacts();
        t1=findViewById(R.id.heading);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray sp = listView.getCheckedItemPositions();
                ArrayList<String> selectedParticipants = new ArrayList<>();
                String members="";
                if(sp.size()>1){
                    for(int i=0;i<sp.size();i++){
                        int pos = sp.keyAt(i);
                        if(sp.valueAt(i)){
                            selectedParticipants.add(matchingContacts.get(pos));

                        }
                    }
                    for(int j=0;j<selectedParticipants.size();j++){
                        members += selectedParticipants.get(j);
                    }
                    Intent it = new Intent(CreateGroupChat.this,CreateGroupProfile.class);
                    it.putExtra("members",members);
                    it.putExtra("uid",phoneno);
                    startActivity(it);
                }
                else{
                    Toast.makeText(getApplicationContext(),"A Group Cannot have 1 or no Participants",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private List<String> getAllContacts() {
        List<String> list = new ArrayList<String>();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if(cursor.moveToFirst()){
            for(int i = 0; i<cursor.getCount();i++){
                int j = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int k = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String phoneNo = cursor.getString(j).replaceAll(" ","");
                String displayName = cursor.getString(k);
                if(phoneNo.charAt(0)!='+' && phoneNo.length()==10){
                    phoneNo="+91"+phoneNo;
                }
                if(phoneNo.charAt(0)=='0'){
                    phoneNo="+91"+ phoneNo.substring(1);
                }
                list.add(phoneNo);
                contactNames.put(phoneNo,displayName);
                cursor.moveToNext();
            }
        }
        cursor.close();
        list = list.stream().distinct().collect(Collectors.toList());
        return list;

    }
    private void getAllUserContacts(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dt: snapshot.getChildren()){
                    String number = dt.child("phoneno").getValue().toString();
                    String status = dt.child("about").getValue().toString();
                    for(int i = 0; i<phoneContacts.size(); i++){
                        if(number.equals(phoneContacts.get(i))){
                            matchingContacts.add(phoneContacts.get(i));
                            matchingNames.add(contactNames.get(number));
                            statusArray.add(status);
                            storageReference.child(number).child("images").child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profiles.add(uri);
                                    if((int)profiles.size()==(int)matchingNames.size()){
                                        matchingContacts = matchingContacts.stream().distinct().collect(Collectors.toList());
                                        matchingNames = matchingNames.stream().distinct().collect(Collectors.toList());
                                        profiles = profiles.stream().distinct().collect(Collectors.toList());
                                        String[] names = matchingNames.toArray(new String[0]);
                                        String[] statuses = statusArray.toArray(new String[0]);
                                        Uri[] profile = profiles.toArray(new Uri[0]);
                                        String[] phone = matchingContacts.toArray(new String[0]);
                                        MyListView2 adapter=new MyListView2(CreateGroupChat.this, names, statuses, profile);
                                        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                        //listView.setSelector(R.drawable.splashgradient);
                                        listView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                    if(flag>=2 && checkedpos.contains(String.valueOf(i))==false){
                                                        checkedpos.add(String.valueOf(i));
                                                        checkedpos.stream().distinct().collect(Collectors.toList());
                                                        view.setBackground(getDrawable(R.drawable.login_register_button));
                                                    }else if(flag>=2 && checkedpos.contains(String.valueOf(i))==true){
                                                        checkedpos.remove(String.valueOf(i));
                                                        view.setBackground(getDrawable(R.drawable.inactivebutton));
                                                    }else if(flag==1){
                                                        checkedpos.add(String.valueOf(i));
                                                        view.setBackground(getDrawable(R.drawable.login_register_button));
                                                        flag++;
                                                }
                                                }

                                        });
                                    }
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}