package ru.dev2dev.firebasetest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.dev2dev.firebasetest.models.Joke;

/**
 * Created by dmitriy on 04.01.17.
 */

public class JokeListActivity extends BaseActivity {

    private RecyclerView jokeList;
    private FloatingActionButton addBtn;
    private FirebaseRecyclerAdapter<Joke, JokeViewHolder> adapter;
    private DatabaseReference database;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_list);
        database = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        jokeList = (RecyclerView) findViewById(R.id.joke_list);
        addBtn = (FloatingActionButton) findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateJokeDialog();
            }
        });

        Query jokesQuery = database.child("jokes");

        adapter = new FirebaseRecyclerAdapter<Joke, JokeViewHolder>(
                Joke.class,
                R.layout.joke_item,
                JokeViewHolder.class,
                jokesQuery) {

            @Override
            protected void populateViewHolder(JokeViewHolder viewHolder, Joke model, int position) {

//                final DatabaseReference jokeRef = getRef(position);
                final DatabaseReference jokeRef = database.child("jokes").child(getRef(position).getKey());

                viewHolder.bindToJoke(
                        model,
                        //for like
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                DatabaseReference ref = database.child("jokes").child(jokeRef.getKey());
//                                voteForJoke(true, ref);
                                voteForJoke(true, jokeRef);
                            }
                        },
                        //for dislike
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                DatabaseReference ref = database.child("jokes").child(jokeRef.getKey());
//                                voteForJoke(false, ref);
                                voteForJoke(false, jokeRef);
                            }
                        });
            }
        };
        jokeList.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.cleanup();
        }
    }

    private void openCreateJokeDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View jokeCreatingView = layoutInflater.inflate(R.layout.joke_create_dialog, null);
        final EditText editText = (EditText) jokeCreatingView.findViewById(R.id.editText);

        new AlertDialog.Builder(this)
                .setView(jokeCreatingView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.length()<1) {
                            return;
                        } else {
                            saveJoke(editText.getText().toString());
                            dialog.dismiss();
                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void saveJoke(String jokeText) {

        Joke joke = new Joke(
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date()),
                jokeText,
                firebaseUser.getEmail()
        );
        /**
         * posting by key
         */
//        String key = database.child("jokes").push().getKey();
//        database.child("jokes").child(key).setValue(joke);

        /**
         * posting by push()
         */
        database.child("jokes").push().setValue(joke);
    }

    private void voteForJoke(final boolean isLike, DatabaseReference ref) {
        ref.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Log.d(TAG, "doTransaction: mutableData "+mutableData.toString());
                Joke joke = mutableData.getValue(Joke.class);
                if (joke==null) {
                    return Transaction.success(mutableData);
                }

                if (!joke.voteUserNames.contains(firebaseUser.getEmail())) {
                    joke.voteUserNames.add(firebaseUser.getEmail());
                    if (isLike) {
                        joke.likeCount++;
                    } else {
                        joke.likeCount--;
                    }
                }

                mutableData.setValue(joke);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d(TAG, "voteForJoke: onComplete: databaseError" + databaseError);
                Log.d(TAG, "voteForJoke: onComplete: b " + b);
                Log.d(TAG, "voteForJoke: onComplete: dataSnapshot " + dataSnapshot.toString());

            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
