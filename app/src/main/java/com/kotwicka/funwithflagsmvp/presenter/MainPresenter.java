package com.kotwicka.funwithflagsmvp.presenter;

import android.content.res.AssetManager;
import android.util.Log;

import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.model.Quiz;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Set;

public class MainPresenter implements QuizContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final QuizContract.View mainView;
    private Quiz quiz;
    private SecureRandom random;

    public MainPresenter(final QuizContract.View view) {
        this.mainView = view;
        this.quiz = new Quiz();
        this.random = new SecureRandom();
    }

    @Override
    public void initQuiz(final Set<String> regions, final AssetManager assets) {
        this.quiz = new Quiz();
        loadCountries(regions, assets);
        selectQuizCountries();
        loadQuestions();
    }

    private void loadQuestions() {
        final String selectedCountry = quiz.selectCountry();
        mainView.setQuestionNumber(quiz.getQuestionNumber(), Quiz.NUMBER_OF_QUESTIONS);
        mainView.setFlag(createFlagPath(selectedCountry));
    }

    private String createFlagPath(final String selectedCountry) {
        return selectedCountry.substring(0, selectedCountry.indexOf("-")) + "/" + selectedCountry;
    }

    private void selectQuizCountries() {
        int selectedFlagsCounter = 0;
        int numberOfAllFlags = quiz.getCountriesSize();

        while (selectedFlagsCounter < Quiz.NUMBER_OF_QUESTIONS) {
            String country = quiz.getCountryAt(random.nextInt(numberOfAllFlags));
            if (!quiz.alreadySelectedCountry(country)) {
                quiz.addCountryToQuiz(country);
                ++selectedFlagsCounter;
            }
        }
    }

    private void loadCountries(final Set<String> regions, final AssetManager assets) {
        try {
            for (String region : regions) {
                String[] paths = assets.list(region);
                for (String path : paths) {
                    quiz.addCountry(path);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error in loading flag files", e);
        }
    }
}
