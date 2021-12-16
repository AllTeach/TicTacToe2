package com.example.tictactoe;


import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;

public class FirebaseComm
{
    private final static FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private static final String TAG = "Firebase Comm";
    private IOnFirebaseResult onFirebaseResult;

    enum ResultType
    {
        REGISTER,
        LOGIN,



    }
    interface IOnFirebaseResult
    {
        void firebaseResult(ResultType result,boolean success);
    }

    public FirebaseComm(IOnFirebaseResult onFirebaseResult) {
        this.onFirebaseResult = onFirebaseResult;
    }

    public void registerUser(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: success ");
                            onFirebaseResult.firebaseResult(ResultType.REGISTER,true);
                        }
                        else {
                            Log.d(TAG, "onComplete: failed ");
                            onFirebaseResult.firebaseResult(ResultType.REGISTER,false);
                        }
                    }
                });
    }
}
