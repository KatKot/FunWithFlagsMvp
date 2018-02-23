package com.kotwicka.funwithflagsmvp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kotwicka.funwithflagsmvp.R;
import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.presenter.MainPresenter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements QuizContract.View, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CHOICES = "pref_numberOfChoices";
    private static final String REGIONS = "pref_regionsToInclude";

    private boolean hasPreferencesChanged = true;
    private QuizContract.Presenter mainPresenter;
    private SharedPreferences sharedPreferences;

    @BindView(R.id.flagImageView)
    ImageView flagImageView;

    @BindView(R.id.questionNumberTextView)
    TextView questionNumberTextView;

    @BindView(R.id.row1LinearLayout)
    LinearLayout answersRow1;

    @BindView(R.id.row2LinearLayout)
    LinearLayout answersRow2;

    @BindView(R.id.row3LinearLayout)
    LinearLayout answersRow3;

    @BindView(R.id.row4LinearLayout)
    LinearLayout answersRow4;

    @BindView(R.id.answerTextView)
    TextView answerTextView;

    @BindView(R.id.quizLinearLayout)
    LinearLayout quizLinearLayout;

    LinearLayout[] answerLayouts;
    Handler handler;
    Animation shakeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        this.answerLayouts = new LinearLayout[]{answersRow1, answersRow2, answersRow3, answersRow4};
        this.mainPresenter = new MainPresenter(this);
        this.handler = new Handler();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        this.shakeAnimation.setRepeatCount(3);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasPreferencesChanged) {
            mainPresenter.initQuiz(sharedPreferences.getStringSet(REGIONS, null), Integer.valueOf(sharedPreferences.getString(CHOICES, null)), getAssets());
            hasPreferencesChanged = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setFlag(final String flagPath) {
        try {
            InputStream stream = getAssets().open(flagPath);
            Drawable flag = Drawable.createFromStream(stream, flagPath);
            flagImageView.setImageDrawable(flag);
        } catch (IOException e) {
            Log.e(TAG, "Could not load : " + flagPath, e);
        }
    }

    @Override
    public void setQuestionNumber(int currentQuestionNumber, int questionsNumber) {
        questionNumberTextView.setText(getString(R.string.question, currentQuestionNumber, questionsNumber));
    }

    @Override
    public void initializeChoices(final List<String> countryNames) {
        for (LinearLayout linearLayout : answerLayouts) {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                Button button = (Button) linearLayout.getChildAt(i);
                if (!countryNames.isEmpty()) {
                    button.setText(countryNames.remove(0));
                    button.setOnClickListener(this);
                    button.setEnabled(true);
                } else {
                    button.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        Button guessButton = (Button) view;
        String guess = guessButton.getText().toString();
        boolean isValidChoice = mainPresenter.validateChoice(guess);
        if (isValidChoice) {
            handleValidChoice(guess);
        } else {
            handleInvalidChoice(guessButton);
        }
    }

    private void handleInvalidChoice(Button guessButton) {
        flagImageView.startAnimation(shakeAnimation);
        answerTextView.setText(getString(R.string.incorrect_answer));
        answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getTheme()));
        guessButton.setEnabled(false);
    }

    private void handleValidChoice(String choice) {
        answerTextView.setText(choice + "!");
        answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getTheme()));
        disableAllButtons();
        if (mainPresenter.isLastAnswer()) {
            showSummaryDialog();
        } else {
            loadNextQuestion();
        }
    }

    private void showSummaryDialog() {
        int totalNumberOfGuesses = mainPresenter.getTotalNumberOfGuesses();
        float percentOfCorrectAnswers = 1000 / (float) totalNumberOfGuesses;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle(R.string.summary);
        dialogBuilder.setMessage(getString(R.string.results, totalNumberOfGuesses, percentOfCorrectAnswers));
        dialogBuilder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                answerTextView.setText("");
                mainPresenter.initQuiz(sharedPreferences.getStringSet(REGIONS, null), Integer.valueOf(sharedPreferences.getString(CHOICES, null)), getAssets());
            }
        });
        dialogBuilder.create().show();
    }

    private void loadNextQuestion() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateAndLoadQuestion();
            }
        }, 2000);
    }

    private void animateAndLoadQuestion() {
        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, radius, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                answerTextView.setText("");
                mainPresenter.loadNextFlag();
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private void disableAllButtons() {
        for (LinearLayout linearLayout : answerLayouts) {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                Button button = (Button) linearLayout.getChildAt(i);
                button.setEnabled(false);
            }
        }
    }
}
