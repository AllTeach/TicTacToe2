package com.example.tictactoe;


import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseComm
{
    private final static FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private static final String TAG = "Firebase Comm";
    private IOnFirebaseResult onFirebaseResult;
    private FirebaseFirestore firebaseFirestore;

    private Game game;
    private User fbUser;
    private DocumentReference gameReference;

    enum ResultType
    {
        REGISTER,
        LOGIN,
        GAME_CREATED,
        GAME_JOINED,
        GAME_STARTED,
        NO_PENDING_GAMES,
        GAME_MOVE

    }
    interface IOnFirebaseResult
    {
        void firebaseResult(ResultType result,boolean success);
        void firebaseGameInfo(ResultType result,boolean success,int row, int col);
    }

    public FirebaseComm(IOnFirebaseResult onFirebaseResult) {
        this.onFirebaseResult = onFirebaseResult;
    }

    public void createFbUser(String email, String password)
    {
        fbUser = new User(email,password,"");
    }

    public void registerUser()
    {
        String email=fbUser.getEmail();
        String password = fbUser.getPassword();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: success ");
                            fbUser.setUser_id(mAuth.getUid());
                            enterUserToFirebase(fbUser);

                        }
                        else {
                            Log.d(TAG, "onComplete: failed ");
                            onFirebaseResult.firebaseResult(ResultType.REGISTER,false);
                        }
                    }
                });
    }

    public void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {

                                    // if logged in success without registering
                                    // create User if required
                                    if(fbUser==null)
                                        createFbUser(email,password);

                                        Log.d(TAG, "onComplete: success ");
                                        onFirebaseResult.firebaseResult(ResultType.LOGIN,true);
                                    }
                                      else {
                                        Log.d(TAG, "onComplete: failed ");
                                        onFirebaseResult.firebaseResult(ResultType.LOGIN,false);
                                    }
                                }
             });
    }

    private void enterUserToFirebase(User u)
    {
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference userRef = firebaseFirestore.collection("users")
                .document(u.getUser_id());
        userRef.set(u)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onFirebaseResult.firebaseResult(ResultType.REGISTER,true);
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    public void createGame()
    {
        game = new Game(fbUser.getEmail(), "" ,"created",-1,-1);
        firebaseFirestore = FirebaseFirestore.getInstance();
       // DocumentReference gameRef = firebaseFirestore.collection("games")
         gameReference = firebaseFirestore.collection("games")
                .document(fbUser.getEmail());
                gameReference.set(game)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // let presenter know game created
                        onFirebaseResult.firebaseGameInfo(ResultType.GAME_CREATED,true,-1,-1);

                        // listen to game moves
                        handleGameMoves(gameReference);

                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error writing document", e);
                    }
                });



    }
    // when trying to start or join a game
    // presenter calls this method to search for an open game to join
    // if none exists it calls the start game to open a enw game and wait
    // there for another player
    public void joinGame( )
    {
        firebaseFirestore.collection("games")
                .whereEqualTo("status", "created")
                .limit(1).get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // notify presenter there are no  open games
                        if (task.isSuccessful()) {
                            if(task.getResult().size() == 0)
                                onFirebaseResult.firebaseGameInfo(ResultType.NO_PENDING_GAMES,true,-1,-1);

                            for( QueryDocumentSnapshot document : task.getResult()) {
                                   Log.d(TAG, document.getId() + " => " + document.getData());
                                   // get the game
                                   game = document.toObject(Game.class);
                                   // join with email
                                   game.setNameOther(fbUser.getEmail());
                                   game.setStatus("started");
                                   gameReference = document.getReference();
                               //    document.getReference().set(game, SetOptions.merge());
                                    gameReference.set(game, SetOptions.merge());

                                   // notify presenter
                                   // let presenter know game started
                                   onFirebaseResult.firebaseGameInfo(ResultType.GAME_JOINED,true,-1,-1);

                                   handleGameMoves(gameReference);



                              }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    // this method listens for changes in the game document
    // whenever fields are changed - the snapshot listener is called
    // The way it is implemented here ->
    // changes from both players trigger this method...
    // possible to impolement in a different way
    // the code:
    // boolean fromOther = !(fbUser.getEmail()).equals(g.getCurrPlayer());
    // checks whether this are changes other player triggerd or this player
    // no need for the presenter to handle again events that where
    // already handled
    private void handleGameMoves(DocumentReference reference) {
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Game g =snapshot.toObject(Game.class);
                    // check game status
                    // if created - it's first time in owner
                    if(game.getStatus().equals("created") && g.getStatus().equals("started"))
                    {
                        game.setStatus(g.getStatus());

                        onFirebaseResult.firebaseGameInfo(ResultType.GAME_STARTED,true,g.getRow(),g.getCol());

                    }
                    // this means ongoing game, either owner played
                    // or other got response as well
                    // this means moving other from joined to started
                    else if(!(g.getRow() ==-1 && g.getCol() ==-1)) {
                        boolean fromOther = !(fbUser.getEmail()).equals(g.getCurrPlayer());
                        onFirebaseResult.firebaseGameInfo(ResultType.GAME_MOVE, fromOther, g.getRow(), g.getCol());
                    }
                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    // update firestore with current move
    public void makeMove(int row,int col)
    {
        game.setRow(row);
        game.setCol(col);
        game.setCurrPlayer(fbUser.getEmail());
        gameReference.set(game, SetOptions.merge());

    }
}
