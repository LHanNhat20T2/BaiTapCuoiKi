package com.example.quanlyquanao.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quanlyquanao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    Activity activity = LogInActivity.this;
    EditText userNameET, passET;
    Button logInBT, signInBT;
    String userString, passString;
    FirebaseAuth mAuth;
    CheckBox rememberCB;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AnhXa();
        GetDataFromEditText();
        mAuth = FirebaseAuth.getInstance();
        GetDataBySharePreferences();
        logInBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogIn();
            }
        });
        signInBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvertFromLogInToSignUp();
            }
        });
    }
    public void AnhXa(){
        userNameET = findViewById(R.id.username);
        passET = findViewById(R.id.password);
        logInBT = findViewById(R.id.loginbtn);
        signInBT = findViewById(R.id.buttonSignUp_Login);
        rememberCB = findViewById(R.id.checkBoxRemember);
    }
    public void GetDataFromEditText(){
        userString = userNameET.getText().toString();
        passString = passET.getText().toString();
    }
    public void ConvertFromLogInToHome(){
        Intent intent = new Intent(activity, MainActivity.class);
        startActivity(intent);
    }
    public void LogIn(){
        GetDataFromEditText();
        if(userString.isEmpty()){
            userNameET.setError("Trống");
            userNameET.requestFocus();
        }
        else
        if(passString.isEmpty()){
            passET.setError("Trống");
            passET.requestFocus();
        }
        else
            mAuth.signInWithEmailAndPassword(userString, passString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(activity, "SignUp Successful", Toast.LENGTH_LONG).show();
                        RememberPassWord();
                        ConvertFromLogInToHome();
                    }
                    else {
                        System.out.println(task.getException());
                        Toast.makeText(activity, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
    public void ConvertFromLogInToSignUp(){
        Intent intent = new Intent(activity, SignUpActivity.class);
        startActivity(intent);
    }
    public void RememberPassWord(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(rememberCB.isChecked()){
//            SharedPreferences remember = (SharedPreferences) sharedPreferences.edit();
            editor.putString("gmail", userString);
            editor.putString("pass",passString);
            editor.putBoolean("remember", true);
            editor.commit();
        }
        else{
            editor.remove("gmail");
            editor.remove("pass");
            editor.remove("remember");
            editor.commit();
        }
    }
    public void GetDataBySharePreferences(){
        sharedPreferences = getSharedPreferences("dataLogin", MODE_PRIVATE);
        userNameET.setText(sharedPreferences.getString("gmail",""));
        passET.setText(sharedPreferences.getString("pass",""));
        rememberCB.setChecked(sharedPreferences.getBoolean("remember",false));
    }
}