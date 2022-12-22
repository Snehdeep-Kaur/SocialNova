package com.example.socialnova;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    View fl;
    Button chats, groups, explore, lo;
    LinearLayout ll1;
    private String phoneno;
    private static final String FILE_NAME = "LoginActivity";
    private String PARAM_ONE = "userLogState";
    private String PARAM_TWO = "profileId";
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    TextView settings ;
    private static final int READ_CONTACTS_CODE = 100;
    Cursor cursor;
    List<String> phoneContacts = new ArrayList<String>();
    HashMap <String,String>contactNames = new HashMap<>();
    private int flag = 1;
    ListView listView;

    List<String> matchingNames = new ArrayList<String>();
    List<String> matchingContacts = new ArrayList<String>();
    List<Uri> profiles = new ArrayList<Uri>();
    List<String> statusArray = new ArrayList<String>();
    List<String> chatCon = new ArrayList<>();
    List<String> lastChat = new ArrayList<>();
    List<String> groupNames = new ArrayList<>();
    List<String> groupMembers = new ArrayList<>();
    List<String> groupLastChat = new ArrayList<>();
    List<Uri> groupProfiles = new ArrayList<>();
    List<String> groupAbouts = new ArrayList<>();
    List<String> currentmatchingNames = new ArrayList<String>();
    List<Uri> currentprofiles = new ArrayList<Uri>();


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle args = getArguments();
        try {
            phoneno = args.getString("UID");
            checkPermissions();
        } catch (NullPointerException e) {

        }

        fl = view.findViewById(R.id.fl);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(fl);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setPeekHeight(400);
        chats = view.findViewById(R.id.chats);
        groups = view.findViewById(R.id.groups);
        explore = view.findViewById(R.id.explore);
        ll1 = view.findViewById(R.id.ll1);
        listView=view.findViewById(R.id.ll3);
        settings=view.findViewById(R.id.settings);


        /*HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        horizontalScrollView.setLayoutParams(layoutParams);

        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(linearParams);


        horizontalScrollView.addView(linearLayout);*/

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), settings);
                popupMenu.getMenuInflater().inflate(R.menu.home, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.logoutm:
                                databaseReference.child("users").child(phoneno).child("log_report").setValue("logged_out");
                                SharedPreferences preferences = getContext().getSharedPreferences(FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(PARAM_ONE, " ");
                                editor.putString(PARAM_TWO, " ");
                                editor.clear();
                                editor.commit();
                                Intent i = new Intent(getContext(), Login.class);
                                startActivity(i);
                                onDestroy();
                                return true;
                        }
                       return true;
                    }
                });

                popupMenu.show();

            }
        });
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chats.setBackgroundResource(R.drawable.login_register_button);
                groups.setBackgroundResource(R.drawable.inactivebutton);
                explore.setBackgroundResource(R.drawable.inactivebutton);
                flag=1;
                checkPermissions();

            }
        });
        groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groups.setBackgroundResource(R.drawable.login_register_button);
                chats.setBackgroundResource(R.drawable.inactivebutton);
                explore.setBackgroundResource(R.drawable.inactivebutton);
                flag=2;
                checkPermissions();
            }
        });
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                explore.setBackgroundResource(R.drawable.login_register_button);
                chats.setBackgroundResource(R.drawable.inactivebutton);
                groups.setBackgroundResource(R.drawable.inactivebutton);
                flag=3;
                checkPermissions();
            }
        });
        return view;
    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to access files from your device...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_CODE);
        }
    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                requestPermission();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_CODE);
            }
        } else {
            if(flag==1){
                phoneContacts=getAllContacts();
                getCurrentChats();
            }else if(flag==2){
                getGroupChats();
            }else if(flag==3){
                phoneContacts=getAllContacts();
                getAllUserContacts();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Thanks for enabling the permission", Toast.LENGTH_SHORT).show();
                //do something permission is allowed here....
                phoneContacts=getAllContacts();
                getAllUserContacts();

            } else {

                Toast.makeText(getContext(), "Please allow the Permission", Toast.LENGTH_SHORT).show();

            }
        }

    }


    private List<String> getAllContacts() {
        List<String> list = new ArrayList<String>();
        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
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
                        for(int i = 0; i<phoneContacts.size(); i++){
                            if(number.equals(phoneContacts.get(i))){
                                matchingContacts.add(phoneContacts.get(i));
                                matchingNames.add(contactNames.get(number));

                                String status = dt.child("about").getValue().toString();
                                statusArray.add(status);

                                StorageReference storageReference1 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
                                storageReference1.child(number).child("images").child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        profiles.add(uri);
                                        //t1.setText(String.valueOf(profiles.size())+" "+String.valueOf(statusArray.size())+" "+String.valueOf(matchingNames.size()) + statusArray.get(0));
                                        if((int)profiles.size()==(int)matchingNames.size() && (int)statusArray.size()==(int)matchingNames.size()){
                                            matchingContacts = matchingContacts.stream().distinct().collect(Collectors.toList());
                                            matchingNames = matchingNames.stream().distinct().collect(Collectors.toList());
                                            profiles = profiles.stream().distinct().collect(Collectors.toList());
                                            String[] names = matchingNames.toArray(new String[0]);
                                            String[] statuses = statusArray.toArray(new String[0]);
                                            Uri[] profile = profiles.toArray(new Uri[0]);
                                            String[] phone = matchingContacts.toArray(new String[0]);
                                            MyListView adapter=new MyListView(getActivity(), names, statuses, profile);
                                            listView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                    String clname = names[i];
                                                    String clnumber = phone[i];
                                                    Uri clphoto = profile[i];
                                                    Intent it = new Intent(getActivity(),individualChat.class);
                                                    it.putExtra("name",clname);
                                                    it.putExtra("number",clnumber);
                                                    it.putExtra("uri",clphoto);
                                                    it.putExtra("ourNumber",phoneno);
                                                    startActivity(it);
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

    public void getCurrentChats(){
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("chats");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(lastChat.size()>0){
                    lastChat.clear();
                }
                for(DataSnapshot dt : snapshot.getChildren()){
                    String parent = dt.getKey();

                    if(parent.startsWith(phoneno)){
                        String chatContacts = parent.substring(13);
                        chatCon.add(chatContacts);
                        //matchingContacts.add(chatContacts);

                        databaseReference2.child(parent).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String message="";
                                for(DataSnapshot dt1 : snapshot.getChildren()){
                                    message = dt1.child("message").getValue().toString();
                                }
                                lastChat.add(message);
                                        if(contactNames.containsKey(chatContacts)){
                                            currentmatchingNames.add(contactNames.get(chatContacts));
                                        }
                                        else{
                                            currentmatchingNames.add(chatContacts);
                                        }
                                        StorageReference storageReference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
                                        storageReference2.child(chatContacts).child("images").child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                currentprofiles.add(uri);
                                                if((int)currentprofiles.size()==(int)currentmatchingNames.size()){
                                                    //matchingContacts = matchingContacts.stream().distinct().collect(Collectors.toList());
                                                    currentmatchingNames = currentmatchingNames.stream().distinct().collect(Collectors.toList());
                                                    currentprofiles = currentprofiles.stream().distinct().collect(Collectors.toList());
                                                    chatCon = chatCon.stream().distinct().collect(Collectors.toList());
                                                    lastChat = lastChat.stream().distinct().collect(Collectors.toList());
                                                    String[] names = currentmatchingNames.toArray(new String[0]);
                                                    Uri[] profile = currentprofiles.toArray(new Uri[0]);
                                                    //String[] phone = matchingContacts.toArray(new String[0]);
                                                    String[] lchats = lastChat.toArray(new String[0]);
                                                    String[] cCon = chatCon.toArray(new String[0]);
                                                    MyListView3 adapter=new MyListView3(getActivity(), names, lchats, profile);
                                                    listView.setAdapter(adapter);
                                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                            String clname = names[i];
                                                            String clnumber = cCon[i];
                                                            Uri clphoto = profile[i];
                                                            Intent it = new Intent(getActivity(),individualChat.class);
                                                            it.putExtra("name",clname);
                                                            it.putExtra("number",clnumber);
                                                            it.putExtra("uri",clphoto);
                                                            it.putExtra("ourNumber",phoneno);
                                                            startActivity(it);
                                                        }
                                                    });
                                                }
                                            }
                                        });


                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getGroupChats(){
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("groups");
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(groupLastChat.size()>0){
                    groupLastChat.clear();
                }
                for(DataSnapshot dt : snapshot.getChildren()){
                    String parent = dt.getKey();
                    if(parent.contains(phoneno)){
                        String groupName = dt.child("name").getValue().toString();
                        String groupAbout = dt.child("about").getValue().toString();
                        groupNames.add(groupName);
                        groupMembers.add(parent);
                        groupAbouts.add(groupAbout);
                        if(dt.hasChild("messages")){
                            databaseReference3.child(parent).child("messages").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String message="";
                                    for(DataSnapshot dt1 : snapshot.getChildren()){
                                        message = dt1.child("message").getValue().toString();
                                    }
                                    groupLastChat.add(message);
                                    StorageReference storageReference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
                                    storageReference2.child(parent).child("images").child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            groupProfiles.add(uri);
                                            if((int)groupProfiles.size()==(int)groupNames.size()){
                                                groupNames = groupNames.stream().distinct().collect(Collectors.toList());
                                                groupProfiles = groupProfiles.stream().distinct().collect(Collectors.toList());
                                                groupLastChat = groupLastChat.stream().distinct().collect(Collectors.toList());
                                                groupMembers = groupMembers.stream().distinct().collect(Collectors.toList());
                                                String[] names = groupNames.toArray(new String[0]);
                                                Uri[] profile = groupProfiles.toArray(new Uri[0]);
                                                String[] lchats = groupLastChat.toArray(new String[0]);
                                                String[] members = groupMembers.toArray(new String[0]);
                                                MyGroupListView adapter=new MyGroupListView(getActivity(), names, lchats, profile);
                                                listView.setAdapter(adapter);
                                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                        String clname = names[i];
                                                        String clmembers = members[i];
                                                        Uri clphoto = profile[i];
                                                        Intent it = new Intent(getActivity(),groupChat.class);
                                                        it.putExtra("name",clname);
                                                        it.putExtra("members",clmembers);
                                                        it.putExtra("uri",clphoto);
                                                        it.putExtra("UID",phoneno);
                                                        startActivity(it);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else{
                            StorageReference storageReference2 = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");
                            storageReference2.child(parent).child("images").child("profile_picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    groupProfiles.add(uri);
                                    if((int)groupProfiles.size()==(int)groupNames.size()){
                                        groupNames = groupNames.stream().distinct().collect(Collectors.toList());
                                        groupProfiles = groupProfiles.stream().distinct().collect(Collectors.toList());
                                        groupAbouts = groupAbouts.stream().distinct().collect(Collectors.toList());
                                        groupMembers = groupMembers.stream().distinct().collect(Collectors.toList());
                                        String[] names = groupNames.toArray(new String[0]);
                                        Uri[] profile = groupProfiles.toArray(new Uri[0]);
                                        String[] about = groupAbouts.toArray(new String[0]);
                                        String[] members = groupMembers.toArray(new String[0]);
                                        MyGroupListView adapter=new MyGroupListView(getActivity(), names, about, profile);
                                        listView.setAdapter(adapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                String clname = names[i];
                                                String clmembers = members[i];
                                                Uri clphoto = profile[i];
                                                Intent it = new Intent(getActivity(), groupChat.class);
                                                it.putExtra("name",clname);
                                                it.putExtra("members",clmembers);
                                                it.putExtra("uri",clphoto);
                                                it.putExtra("UID",phoneno);
                                                startActivity(it);
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








