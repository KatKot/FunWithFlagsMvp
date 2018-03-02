package com.kotwicka.funwithflagsmvp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kotwicka.funwithflagsmvp.R;
import com.kotwicka.funwithflagsmvp.components.DaggerQuizComponent;
import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.modules.QuizModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements QuizContract.View, View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CHOICES = "pref_numberOfChoices";
    private static final String REGIONS = "pref_regionsToInclude";

    private boolean hasPreferencesChanged = true;

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

    @Inject
    QuizContract.Presenter mainPresenter;

    @Inject
    Handler handler;

    @Inject
    Animation shakeAnimation;

    @Inject
    SharedPreferences sharedPreferences;

    LinearLayout[] answerLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ButterKnife.bind(this);
        DaggerQuizComponent.builder().quizModule(new QuizModule(this)).build().inject(this);

        initializeData();
        listenOnPreferenceChanges();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasPreferencesChanged) {
            resetQuiz();
            hasPreferencesChanged = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent preferenceIntent = new Intent(this, SettingsActivity.class);
            startActivity(preferenceIntent);
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
    public Context getContext() {
        return this;
    }

    @Override
    public void onClick(View view) {
        final Button guessButton = (Button) view;
        final String guess = guessButton.getText().toString();
        final boolean isValidChoice = mainPresenter.validateChoice(guess);
        if (isValidChoice) {
            handleValidChoice(guess);
        } else {
            handleInvalidChoice(guessButton);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        hasPreferencesChanged = true;
        if (key.equals(REGIONS)) {
            Set<String> regions = sharedPreferences.getStringSet(REGIONS, null);
            if (regions == null || regions.size() == 0) {
                regions = new HashSet<>();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                regions.add(getString(R.string.default_region));
                editor.putStringSet(REGIONS, regions);
                editor.apply();

                Toast.makeText(MainActivity.this, R.string.default_region_message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listenOnPreferenceChanges() {
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    private void initializeData() {
        this.answerLayouts = new LinearLayout[]{answersRow1, answersRow2, answersRow3, answersRow4};
        this.shakeAnimation.setRepeatCount(3);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void resetQuiz() {
        answerTextView.setText("");
        mainPresenter.loadCountries(sharedPreferences.getStringSet(REGIONS, null), Integer.valueOf(sharedPreferences.getString(CHOICES, null)), getAssets());
        mainPresenter.selectCountriesForQuiz();
        selectNextQuestionAndAnswers();
    }

    private void selectNextQuestionAndAnswers() {
        answerTextView.setText("");
        mainPresenter.loadNextQuestion();
        initializeChoiceButtons(mainPresenter.selectPossibleAnswers());
    }

    private void initializeChoiceButtons(final List<String> countryNames) {
        for (LinearLayout linearLayout : answerLayouts) {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                Button button = (Button) linearLayout.getChildAt(i);
                if (!countryNames.isEmpty()) {
                    button.setText(countryNames.remove(0));
                    button.setOnClickListener(this);
                    button.setEnabled(true);
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.GONE);
                }
            }
        }
    }

    private void handleInvalidChoice(Button guessButton) {
        flagImageView.startAnimation(shakeAnimation);
        answerTextView.setText(getString(R.string.incorrect_answer));
        answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getTheme()));
        guessButton.setEnabled(false);
    }

    private void handleValidChoice(String choice) {
        answerTextView.setText(getString(R.string.valid_choice, choice));
        answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getTheme()));
        disableAllButtons();
        if (mainPresenter.isLastAnswer()) {
            showSummaryDialog();
        } else {
            planAnimatedLoadingOfNextQuestion();
        }
    }

    private void disableAllButtons() {
        for (LinearLayout linearLayout : answerLayouts) {
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                Button button = (Button) linearLayout.getChildAt(i);
                button.setEnabled(false);
            }
        }
    }

    private void planAnimatedLoadingOfNextQuestion() {
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
                selectNextQuestionAndAnswers();
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    private void showSummaryDialog() {
        int totalNumberOfGuesses = mainPresenter.getTotalNumberOfGuesses();
        final int percentOfCorrectAnswers = 1000 / totalNumberOfGuesses;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle(R.string.summary);
        dialogBuilder.setMessage(getString(R.string.results, totalNumberOfGuesses, percentOfCorrectAnswers));
        dialogBuilder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetQuiz();
            }
        });
        dialogBuilder.setNeutralButton(getString(R.string.share_on_fb), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ShareLinkContent shareContent = new ShareLinkContent.Builder()
                        .setShareHashtag(new ShareHashtag.Builder().setHashtag(getString(R.string.share_fb_hashtag)).build())
                        .setQuote(getString(R.string.share_fb_quote, percentOfCorrectAnswers))
                        .setContentUrl(Uri.parse(getString(R.string.share_fb_link)))
                        .build();
                ShareDialog.show(MainActivity.this, shareContent);
                resetQuiz();
            }
        });
        dialogBuilder.create().show();
    }
}
