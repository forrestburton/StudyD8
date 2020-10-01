package com.SAP.studyd8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    EditText regUserEmail,regUserPassword,regUserConfirmPassword;
    Button regButton,regLoginButton;
    FirebaseAuth fAuth;
    TextView termsOfService, privacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regUserPassword = findViewById(R.id.regUserPassword);
        regUserConfirmPassword = findViewById(R.id.regUserConfirmPassword);
        regUserEmail = findViewById(R.id.regUserEmail);
        regButton = findViewById(R.id.regButton);
        regLoginButton = findViewById(R.id.regLoginButton);
        termsOfService = findViewById(R.id.regText);
        privacyPolicy = findViewById(R.id.regText2);
        fAuth = FirebaseAuth.getInstance();

        termsOfService.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        regLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });



        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = regUserEmail.getText().toString().trim();
                String password = regUserPassword.getText().toString().trim();
                String confirmPassword = regUserConfirmPassword.getText().toString().trim();

                if (fAuth.getCurrentUser()!=null){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                if(! password.equals(confirmPassword)){
                    regUserConfirmPassword.setError("Password doesn't match");
                    return;
                }
                if(password.length()<6){
                    regUserPassword.setError("Password should at least be 6 charecters long");
                    return;
                }
                if ( TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
                    regUserEmail.setError("Please enter a valid email address");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Registration successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Profile.class));
                            FirebaseUser user= fAuth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Please verify your email", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Unable to send verification email", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Failed due to"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}