package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;

public class Register extends AppCompatActivity {
    private String completePhoneno;
    EditText phoneno;
    CountryCodePicker ccp;
    AppCompatButton ctn;
    TextView gtl;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ccp = findViewById(R.id.ccp);
        phoneno=findViewById(R.id.otp);
        ctn = findViewById(R.id.ctn);
        gtl=findViewById(R.id.goToLogin);
        gtl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this,Login.class);
                startActivity(i);
                finish();
            }
        });
        ctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneno.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter a phone number to proceed",Toast.LENGTH_SHORT).show();
                }else{
                    ccp.registerCarrierNumberEditText(phoneno);
                    completePhoneno="+"+ccp.getFullNumber();
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(completePhoneno)){
                                Toast.makeText(getApplicationContext(),"This number is already registered with a profile. Please navigate to login page instead",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent i = new Intent(Register.this,Register2.class);
                                i.putExtra("PhoneNo",completePhoneno);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }
}