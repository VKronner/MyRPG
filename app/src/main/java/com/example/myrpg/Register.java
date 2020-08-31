package com.example.myrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import in.goodiebag.carouselpicker.CarouselPicker;

public class Register extends AppCompatActivity {

    AutoCompleteTextView autocomplete_Gender;

    SharedPreferences sp;

    String username = "";
    String email = "";
    String birthday = "";
    String gender = "";
    String password = "";
    String confirmPassword = "";
    String myCharImage = "";;

    boolean username_bool;
    boolean email_bool;
    boolean birthday_bool;
    boolean gender_bool;
    boolean password_bool;
    boolean confirmPassword_bool;
    boolean everythingOk;

    TextInputLayout textInputLayout;

    TextInputLayout textInputLayout_username;
    TextInputLayout textInputLayout_email;
    TextInputLayout textInputLayout_birthday;
    TextInputLayout textInputLayout_gender;
    TextInputLayout textInputLayout_password;
    TextInputLayout textInputLayout_confirmPassword;

    TextInputEditText username_txt;
    TextInputEditText email_txt;
    TextInputEditText birthday_txt;
    AutoCompleteTextView gender_txt;
    TextInputEditText password_txt;
    TextInputEditText confirmPassword_txt;

    Button btnReady;

    CarouselPicker carouselPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnReady = findViewById(R.id.button_ready);

        sp = getSharedPreferences("SaveRegisterInfo", Context.MODE_PRIVATE);

        username_txt = findViewById(R.id.username_text);
        email_txt = findViewById(R.id.Email_text);
        birthday_txt = findViewById(R.id.Birthday_text);
        gender_txt = findViewById(R.id.autocomplete_Gender);
        password_txt = findViewById(R.id.Password_text);
        confirmPassword_txt = findViewById(R.id.ConfirmPassword_text);

        textInputLayout_username = findViewById(R.id.textInputLayoutUsername);
        textInputLayout_email = findViewById(R.id.textInputLayoutEmail);
        textInputLayout_birthday = findViewById(R.id.textInputLayoutBirthday);
        textInputLayout_gender = findViewById(R.id.textInputLayoutGender);
        textInputLayout_password = findViewById(R.id.textInputLayoutPassword);
        textInputLayout_confirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);

        btnReady.setEnabled(false);

        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username         = username_txt.getText().toString().trim();
                email            = email_txt.getText().toString().trim();
                birthday         = birthday_txt.getText().toString().trim();
                gender           = gender_txt.getText().toString().trim();
                password         = password_txt.getText().toString().trim();
                confirmPassword  = confirmPassword_txt.getText().toString().trim();

                username_bool = validateUser();

                email_bool = validadeEmail();

                birthday_bool = validadeBirthday();

                gender_bool = validadeGender();

                password_bool = validadePassword();

                confirmPassword_bool = validadeConfirmPassword();

                SendToFirebase(email, password);

            }
        });

        username_txt.addTextChangedListener(new ValidationTextWatcher(username_txt));
        email_txt.addTextChangedListener(new ValidationTextWatcher(email_txt));
        birthday_txt.addTextChangedListener(new ValidationTextWatcher(birthday_txt));
        gender_txt.addTextChangedListener(new ValidationTextWatcher(gender_txt));
        password_txt.addTextChangedListener(new ValidationTextWatcher(password_txt));
        confirmPassword_txt.addTextChangedListener(new ValidationTextWatcher(confirmPassword_txt));

        //String encryptedPassword = Encrypt(password_text, currentTime.toString());

        //region DropDown Gender Picker
        autocomplete_Gender = findViewById(R.id.autocomplete_Gender);
        textInputLayout = findViewById(R.id.textInputLayoutGender);
        autocomplete_Gender.setShowSoftInputOnFocus(false);
        String[] Genders = new String[] {"MALE", "FEMALE", "OTHERS", "UNICORN", "EVIL VAMPIRE"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        Register.this,
                        R.layout.dropdown_item,
                        Genders);
        autocomplete_Gender.setAdapter(adapter);

    //endregion

        carouselPicker = findViewById(R.id.carouselPicker);
        List<CarouselPicker.PickerItem> itemsImages = new ArrayList<>();
        itemsImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_shenojiva_foreground));
        itemsImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_behemoth_foreground));
        itemsImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_garuga_foreground));
        itemsImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_nargacuga_foreground));
        itemsImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_rajang_foreground));

        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this, itemsImages, 0);
        carouselPicker.setAdapter(imageAdapter);

        carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 0:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_shenojiva_foreground);
                        break;
                    case 1:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_behemoth_foreground);
                        break;
                    case 2:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_garuga_foreground);
                        break;
                    case 3:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_nargacuga_foreground);
                        break;
                    case 4:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_rajang_foreground);
                        break;

                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_shenojiva_foreground);
                        break;
                    case 1:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_behemoth_foreground);
                        break;
                    case 2:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_garuga_foreground);
                        break;
                    case 3:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_nargacuga_foreground);
                        break;
                    case 4:
                        myCharImage = ""+ getResources().getResourceName(R.mipmap.ic_rajang_foreground);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void SendToFirebase(String email, String password) {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        SharedPreferences.Editor editor = sp.edit();

        editor.putString("password", password);
        editor.putString("userEmail", email);
        editor.apply();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            FirebaseUser user = mAuth.getCurrentUser();

                            Map<String, Object> newUser = new HashMap<>();

                            newUser.put("username", username);
                            newUser.put("email", sp.getString("userEmail",""));
                            newUser.put("UID", user.getUid());
                            newUser.put("birthday",birthday);
                            newUser.put("gender", gender);
                            newUser.put("password", sp.getString("password", ""));
                            newUser.put("imageIconText", myCharImage);

                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();

                            db.collection("Users")
                                    .add(newUser).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(Register.this, "Loggin Success", Toast.LENGTH_SHORT).show();

                                    Intent DashboardIntent = new Intent(getApplicationContext(), DashBoard.class);
                                    startActivity(DashboardIntent);

                                }
                            });
                        } else {
                            Toast.makeText(Register.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //region Validation of Info
    private boolean validadeConfirmPassword() {

        confirmPassword = confirmPassword_txt.getText().toString().trim();

        if (confirmPassword.isEmpty()){
            textInputLayout_confirmPassword.setError("Don't forget to put the Confirm Password");
            confirmPassword_bool = false;
            return false;
        } else if (confirmPassword_txt.getText().toString().length() < 6){
            textInputLayout_confirmPassword.setError("Confirm Password can't be less than 6 digit");
            confirmPassword_bool = false;
            return false;
        } else if (!confirmPassword.equals(password)){
            textInputLayout_confirmPassword.setError("Confirm Password can't be different from Password");
            confirmPassword_bool = false;
        } else {
            textInputLayout_confirmPassword.setError(null);
            requestFocus(textInputLayout_confirmPassword);
            confirmPassword_bool = true;
            return true;
        }
        return true;
    }

    private boolean validadePassword() {

        password = password_txt.getText().toString().trim();

        if (password.isEmpty()){
            textInputLayout_password.setError("Don't forget to put a Password");
            password_bool = false;
            return false;
        } else if (password.length() < 6){
            textInputLayout_password.setError("Password can't be less than 6 digit");
            password_bool = false;
            return false;
        } else {
            textInputLayout_password.setError(null);
            requestFocus(textInputLayout_password);
            password_bool = true;
            return true;
        }
    }

    private boolean validadeGender() {

        gender = gender_txt.getText().toString().trim();

        if (gender.isEmpty()) {
            textInputLayout_gender.setError("Don't forget to choose one Gender");
            gender_bool = false;
            return false;
        }
        gender_bool = true;
        return true;
    }

    private boolean validadeBirthday() {

        birthday = birthday_txt.getText().toString().trim();

        if (birthday.isEmpty()){
            textInputLayout_birthday.setError("Don't forget to put a valid Birthday");
            birthday_bool = false;
            return false;
        }
        else if (birthday.length() != 10){
            textInputLayout_birthday.setError("Don't forget to put a valid Birthday");
            birthday_bool = false;
        } else {
            textInputLayout_birthday.setError(null);
            requestFocus(textInputLayout_birthday);
        }
        birthday_bool = true;
        return true;
    }

    private boolean validadeEmail() {

        email = email_txt.getText().toString().trim();

        if (email.isEmpty()){
            textInputLayout_email.setError("Don't forget to put a valid Email");
            email_bool = false;
            return false;
        } else {
            Boolean isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (!isValid){
                textInputLayout_email.setError("Invalid Email Address, ex: abc@example.com");
                email_bool = false;
                return false;
            } else {
                textInputLayout_email.setError(null);
                requestFocus(textInputLayout_email);
            }
        }
        email_bool = true;
        return true;
    }

    private boolean validateUser(){

        username = username_txt.getText().toString().trim();

        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

        if(username.isEmpty()){
            textInputLayout_username.setError("Don't forged to put a Username");
        }else if (username.length() <= 4){
            textInputLayout_username.setError("Username can't be less than 4 digit");
            username_bool = false;
            return false;
        } else {
            textInputLayout_username.setError(null);
            requestFocus(textInputLayout_username);
        }
        username_bool = true;
        return true;
    }

    //endregion

    private void requestFocus(View view){
        if (view. requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private class ValidationTextWatcher implements TextWatcher {

        private View view;

        private ValidationTextWatcher (View view){
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (view.getId()) {
                case R.id.username_text:
                    validateUser();
                    break;
                case R.id.Email_text:
                    validadeEmail();
                    break;
                case R.id.Birthday_text:
                    validadeBirthday();
                    break;
                case R.id.autocomplete_Gender:
                    validadeGender();
                    break;
                case R.id.Password_text:
                    validadePassword();
                    break;
                case R.id.ConfirmPassword_text:
                    validadeConfirmPassword();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (username_bool && email_bool && birthday_bool && gender_bool && password_bool && confirmPassword_bool){
                btnReady.setEnabled(true);
            } else {
                btnReady.setEnabled(false);
            }
        }
    }
}