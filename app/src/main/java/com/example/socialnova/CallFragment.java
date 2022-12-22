package com.example.socialnova;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CallFragment extends Fragment {

    List<String> phoneContacts = new ArrayList<String>();
    HashMap<String,String> contactNames = new HashMap<>();
    List<String> matchingNames = new ArrayList<String>();
    List<String> matchingContacts = new ArrayList<String>();
    List<Uri> profiles = new ArrayList<Uri>();
    List<String> statusArray = new ArrayList<String>();
    private static final int CALL_CODE = 101;

    private String phoneno;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://social-nova.appspot.com");

    ListView listView;
    Button call;
    TextView t1;
    int flag = -1;

    public CallFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        listView=view.findViewById(R.id.callSelect);
        call = view.findViewById(R.id.makeCall);
        Bundle args = getArguments();
        t1=view.findViewById(R.id.heading);
        try {
            phoneno = args.getString("UID");
            phoneContacts=getAllContacts();
            getAllUserContacts();
        } catch (NullPointerException e) {

            t1.setText(e.toString());
        }
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
                String selphn = matchingContacts.get(flag);
                Uri u = Uri.parse("tel:" + selphn);
                Intent i = new Intent(Intent.ACTION_CALL, u);
                try
                {
                    startActivity(i);
                }
                catch (SecurityException s)
                {
                    Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

        return view;



    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to access files from your device...")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_CODE);
        }
    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                requestPermission();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_CODE);
            }
        } else {


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_CODE) {
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
                                        MyListView2 adapter=new MyListView2(getActivity(), names, statuses, profile);
                                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                        listView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                listView.setSelector(R.drawable.login_register_button);
                                                flag=i;
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