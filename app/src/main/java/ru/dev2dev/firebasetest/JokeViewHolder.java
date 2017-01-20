package ru.dev2dev.firebasetest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.dev2dev.firebasetest.models.Joke;

/**
 * Created by dmitriy on 04.01.17.
 */

public class JokeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView dateTv, authorTv, jokeTv, likeCountTv;
    private Button likeBtn, dislikeBtn;

    public JokeViewHolder(View itemView) {
        super(itemView);
        dateTv = (TextView) itemView.findViewById(R.id.date_tv);
        authorTv = (TextView) itemView.findViewById(R.id.author_tv);
        jokeTv = (TextView) itemView.findViewById(R.id.joke_tv);
        likeCountTv = (TextView) itemView.findViewById(R.id.like_count_tv);

        likeBtn = (Button) itemView.findViewById(R.id.like_btn);
        dislikeBtn = (Button) itemView.findViewById(R.id.dislike_btn);
    }

    public void bindToJoke(Joke joke, View.OnClickListener likeListener,
                           View.OnClickListener dislikeListener) {
        dateTv.setText(joke.date);
        authorTv.setText(joke.username);
        jokeTv.setText(joke.jokeText);
        likeCountTv.setText(String.valueOf(joke.likeCount));

        likeBtn.setOnClickListener(likeListener);
        dislikeBtn.setOnClickListener(dislikeListener);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.like_btn:
        }
    }
}
