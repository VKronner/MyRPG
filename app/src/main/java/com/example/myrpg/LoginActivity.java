package com.example.myrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.icu.util.ChineseCalendar;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;

    FirebaseAuth mAuth;
    FirebaseUser user;

    TextInputEditText email;
    TextInputEditText password;

    String email_text = "";
    String password_text = "";

    ImageView imageView;
    SharedPreferences sp;

    AnimationDrawable mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Initializations
        sp = getSharedPreferences("SaveRegisterInfo", MODE_PRIVATE);

        registerButton = findViewById(R.id.button_Register);
        loginButton = findViewById(R.id.button_Login);

        email = findViewById(R.id.username_text);
        password = findViewById(R.id.password_text_login);

        //endregion

        //sp.edit().clear().apply();

        String LoggedEmail = sp.getString("userEmail", "");
        String LoggedPassword = sp.getString("password", "");

        if (!LoggedEmail.isEmpty() && !LoggedPassword.isEmpty()){
            Login(LoggedEmail, LoggedPassword);
        }

            Log.e("TAG","Password " + sp.getString("password",""));
            Log.e("TAG","Email " + sp.getString("userEmail",""));

        //region Bonfire Animation
        imageView = findViewById(R.id.imageView_fireplace);
        imageView.setBackgroundResource(R.drawable.animation);

        mAnimation = (AnimationDrawable) imageView.getBackground();
        mAnimation.start();
        //endregion

        //region Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), Register.class);
                startActivity(registerIntent);
            }
        });
        //endregion

        //region Login Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email_text = email.getText().toString().trim();
                password_text = password.getText().toString().trim();

                SharedPreferences.Editor editor = sp.edit();

                editor.putString("password", password_text);
                editor.putString("userEmail", email_text);
                editor.apply();

                Log.e("TAG","Password " + sp.getString("password",""));
                Log.e("TAG","Email " + sp.getString("userEmail",""));

                if (email_text.isEmpty()) {
                    email.setError("Put a Valid Email.");
                }
                if (password_text.isEmpty()) {
                    password.setError("Put a Valid Password.");
                }
                if (!password_text.isEmpty() && !email_text.isEmpty()) {
                    SaveLogin(email_text, password_text);
                }
            }
        });
        //endregion
    }

    //region Login Without Save Preferences
    private void Login(String myEmail, String myPassword) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(myEmail, myPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "LOGIN SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    user = mAuth.getCurrentUser();
                    Intent loginIntent = new Intent(getApplicationContext(), DashBoard.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "LOGIN FAILLED", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //endregion

    //region Login With Save Preferences
    private void SaveLogin(String myEmail, String myPassword) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(myEmail, myPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "LOGIN SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    user = mAuth.getCurrentUser();
                    Intent loginIntent = new Intent(getApplicationContext(), DashBoard.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "LOGIN FAILLED", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //endregion

}