package com.example.myrpg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
import java.util.List;

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

    private TextView mTExtView;
    AutoCompleteTextView autocomplete_Gender;

    private static final int ITERATION_COUNT = 1000;
    private static final int KEY_LENGTH = 256;
    private static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int PKCS5_SALT_LENGTH = 32;
    private static final String DELIMITER = "]";
    private static final SecureRandom random = new SecureRandom();

    SharedPreferences sp;
    Date currentTime;

    String username = "";
    String email = "";
    String birthday = "";
    String gender = "";
    String password = "";
    String confirmPassword = "";

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
        List<CarouselPicker.PickerItem> itensImages = new ArrayList<>();
        itensImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_shenojiva_round));
        itensImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_behemoth_round));
        itensImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_garuga_round));
        itensImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_nargacuga_round));
        itensImages.add(new CarouselPicker.DrawableItem(R.mipmap.ic_rajang_round));

        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this, itensImages, 0);
        carouselPicker.setAdapter(imageAdapter);

    }

    private boolean validadeConfirmPassword() {

        confirmPassword = confirmPassword_txt.getText().toString().trim();

        if (confirmPassword.isEmpty()){
            textInputLayout_confirmPassword.setError("Don't forget to put the Confirm Password");
            return false;
        } else if (confirmPassword_txt.getText().toString().length() < 6){
            textInputLayout_confirmPassword.setError("Confirm Password can't be less than 6 digit");
            return false;
        } else if (!confirmPassword.equals(password)){
            textInputLayout_confirmPassword.setError("Confirm Password can't be different from Password");
        } else {
            textInputLayout_confirmPassword.setError(null);
            requestFocus(textInputLayout_confirmPassword);

        }
        return true;
    }

    private boolean validadePassword() {

        password = password_txt.getText().toString().trim();

        if (password.isEmpty()){
            textInputLayout_password.setError("Don't forget to put a Password");
            return false;
        } else if (password.length() < 6){
            textInputLayout_password.setError("Password can't be less than 6 digit");
            return false;
        } else {
            textInputLayout_password.setError(null);
            requestFocus(textInputLayout_password);
        }
        return true;
    }

    private boolean validadeGender() {

        gender = gender_txt.getText().toString().trim();

        if (gender.isEmpty()) {
            textInputLayout_gender.setError("Don't forget to choose one Gender");
            return false;
        }
        return true;
    }

    private boolean validadeBirthday() {

        birthday = birthday_txt.getText().toString().trim();

        if (birthday.isEmpty()){
            textInputLayout_birthday.setError("Don't forget to put a valid Birthday");
            return false;
        }
        else if (birthday.length() != 10){
            textInputLayout_birthday.setError("Don't forget to put a valid Birthday");
        } else {
            textInputLayout_birthday.setError(null);
            requestFocus(textInputLayout_birthday);
        }
        return true;
    }

    private boolean validadeEmail() {

        email = email_txt.getText().toString().trim();

        if (email.isEmpty()){
            textInputLayout_email.setError("Don't forget to put a valid Email");
            return false;
        } else {
            Boolean isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            if (!isValid){
                textInputLayout_email.setError("Invalid Email Address, ex: abc@example.com");
                return false;
            } else {
                textInputLayout_email.setError(null);
                requestFocus(textInputLayout_email);

            }
        }
        return true;
    }

    private boolean validateUser(){

        username = username_txt.getText().toString().trim();

        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

        if(username.isEmpty()){
            textInputLayout_username.setError("Don't forged to put a Username");
        }else if (username.length() <= 4){
            textInputLayout_username.setError("Username can't be less than 4 digit");
            return false;
        } else {
            textInputLayout_username.setError(null);
            requestFocus(textInputLayout_username);
        }
        return true;
    }

    public static String Encrypt(String plaintext, String password) {
        byte[] salt = generateSalt();
        SecretKey key = deriveKey(password, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] iv = generateIV(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            if (salt != null) {
                return String.format("%s%s%s%s%s",
                        toBase64(salt),
                        DELIMITER,
                        toBase64(iv),
                        DELIMITER,
                        toBase64(cipherText));
            }

            return String.format("%s%s%s",
                    toBase64(iv),
                    DELIMITER,
                    toBase64(cipherText));

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static SecretKey deriveKey(String password, byte[] salt) {
        try{
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException e){
            throw new RuntimeException(e);
        }
    }

    private static Object toBase64(byte[] salt) {
        return Base64.encodeToString(salt,Base64.NO_WRAP);
    }

    private static byte[] generateIV(int blockSize) {
        byte[] b = new byte[blockSize];
        random.nextBytes(b);
        return b;
    }

    private static byte[] generateSalt() {
        byte[] b = new byte[PKCS5_SALT_LENGTH];
        random.nextBytes(b);
        return b;
    }

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
            if (validateUser() && validadeEmail() && validadeBirthday() && validadeGender() && validadePassword() && validadeConfirmPassword()){
                btnReady.setEnabled(true);
            } else {
                btnReady.setEnabled(false);
            }
        }
    }
}