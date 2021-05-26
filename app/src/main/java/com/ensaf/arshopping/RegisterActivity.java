package com.ensaf.arshopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ensaf.arshopping.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEt, passET,confirm_pass,fullName;
    Button register_btn,login_btn ;
    CheckBox checkBox;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference all_usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt = findViewById(R.id.register_email_et);
        passET = findViewById(R.id.register_password_et);
        confirm_pass = findViewById(R.id.register_confirmpassword_et);
        register_btn = findViewById(R.id.button_register);
        checkBox = findViewById(R.id.register_checkbox);
        progressBar = findViewById(R.id.progrssbar_register);
        login_btn = findViewById(R.id.signup_to_login);
        fullName = findViewById(R.id.full_name_register);
        mAuth = FirebaseAuth.getInstance();



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirm_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    passET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirm_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullName.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Sasie Votre Nom!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = emailEt.getText().toString();
                String pass = passET.getText().toString();
                final String name = fullName.getText().toString();
                String confirm_password = confirm_pass.getText().toString();
                if(!TextUtils.isEmpty(email) ||!TextUtils.isEmpty(confirm_password) ||!TextUtils.isEmpty(pass) ){
                    if(pass.equals(confirm_password)){

                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToMain();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error "+error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Mot de passe ne correspond pas!",Toast.LENGTH_SHORT).show();

                    }

                }else {
                    Toast.makeText(RegisterActivity.this,"Veuillez remplir tous les champs !",Toast.LENGTH_SHORT).show();
                }


            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void sendToMain() {

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.putExtra("email",emailEt.getText().toString());
        intent.putExtra("name",fullName.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }
}