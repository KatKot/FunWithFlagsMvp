package com.kotwicka.funwithflagsmvp.view;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotwicka.funwithflagsmvp.R;
import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.presenter.MainPresenter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements QuizContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CHOICES = "pref_numberOfChoices";
    private static final String REGIONS = "pref_regionsToInclude";

    private QuizContract.Presenter mainPresenter;
    private SharedPreferences sharedPreferences;


    @BindView(R.id.flagImageView)
    ImageView flagImageView;

    @BindView(R.id.questionNumberTextView)
    TextView questionNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        this.mainPresenter = new MainPresenter(this);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainPresenter.initQuiz(sharedPreferences.getStringSet(REGIONS, null), getAssets());
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
    public void setCountryNameChoices(Set<String> countryNames) {

    }
}
