package com.example.socialnova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class Login extends AppCompatActivity {
    AppCompatButton contn;
    TextView gtr;
    CountryCodePicker ccpL;
    private String completePhoneno;
    EditText phoneno;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://social-nova-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ccpL = findViewById(R.id.ccpL);
        phoneno=findViewById(R.id.phnnoL);
        contn = findViewById(R.id.contn);
        gtr=findViewById(R.id.goToRegister);
        gtr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this,Register.class);
                startActivity(i);
                finish();
            }
        });
        contn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneno.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter a phone number to proceed",Toast.LENGTH_SHORT).show();
                }else{
                    ccpL.registerCarrierNumberEditText(phoneno);
                    completePhoneno="+"+ccpL.getFullNumber();
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(completePhoneno)){
                                Intent i = new Intent(Login.this,Login2.class);
                                i.putExtra("PhoneNo",completePhoneno);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"User does not exits. Please register first",Toast.LENGTH_SHORT).show();
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