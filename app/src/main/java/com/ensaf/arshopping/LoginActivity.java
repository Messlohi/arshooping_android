package com.ensaf.arshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passET;
    TextView geustTv,mdpFogotenTv,mainLabel;
    Button register_btn,login_btn ;
    CheckBox checkBox;
    ProgressBar progressBar;
    Boolean forgetenTest = false;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);
        emailEt = findViewById(R.id.login_email_et);
        passET = findViewById(R.id.login_password_et);
        register_btn = findViewById(R.id.login_to_signup);
        checkBox = findViewById(R.id.login_checkbox);
        progressBar = findViewById(R.id.progrssbar_login);
        login_btn = findViewById(R.id.button_login);
        mainLabel = findViewById(R.id.tv_mainLabel);
        mAuth = FirebaseAuth.getInstance();
        geustTv = findViewById(R.id.asgeust_tv);
        mdpFogotenTv = findViewById(R.id.tv_mdpForgoten_login);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    passET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    passET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });




        mdpFogotenTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetenTest = true;
                login_btn.setText("Envoyer email");
                register_btn.setText("se connecter");
                mainLabel.setText("Récupération de mot de passe");
                mainLabel.setTextSize(30);
                checkBox.setVisibility(View.GONE);
                passET.setVisibility(View.GONE);
                mdpFogotenTv.setVisibility(View.GONE);
                geustTv.setVisibility(View.GONE);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!forgetenTest){
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    login_btn.setText("se connecter");
                    register_btn.setText("s'inscrire");
                    mainLabel.setText("Connexion");
                    mainLabel.setTextSize(40);
                    checkBox.setVisibility(View.VISIBLE);
                    passET.setVisibility(View.VISIBLE);
                    mdpFogotenTv.setVisibility(View.VISIBLE);
                    geustTv.setVisibility(View.VISIBLE);
                    forgetenTest = false;

                }

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!forgetenTest){
                    String email = emailEt.getText().toString();
                    String pass = passET.getText().toString();
                    if(!TextUtils.isEmpty(email) ||!TextUtils.isEmpty(pass) ){
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToMain();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "Error "+error, Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);


                                }
                            }
                        });

                    }else {
                        Toast.makeText(LoginActivity.this,"Veuillez remplir tous les champs !",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }else {
                    progressBar.setVisibility(View.VISIBLE);

                    String email = emailEt.getText().toString();
                    if(email.trim().isEmpty())       {
                        Toast.makeText(LoginActivity.this,"Veuillez remplir le champs !",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this,"Si vous etes inscrit un email de récupération va étre envoyer!",Toast.LENGTH_SHORT).show();
                                        login_btn.setText("se connecter");
                                        register_btn.setText("s'inscrire");
                                        mainLabel.setText("Connexion");
                                        mainLabel.setTextSize(40);
                                        checkBox.setVisibility(View.VISIBLE);
                                        passET.setVisibility(View.VISIBLE);
                                        mdpFogotenTv.setVisibility(View.VISIBLE);
                                        geustTv.setVisibility(View.VISIBLE);
                                        forgetenTest = false;

                                    }else {
                                        Toast.makeText(LoginActivity.this,"Erreur parvient!",Toast.LENGTH_LONG).show();

                                    }
                                    progressBar.setVisibility(View.INVISIBLE);

                                }
                            });


                }

            }
        });


    }



    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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