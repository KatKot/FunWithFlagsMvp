package com.kotwicka.funwithflagsmvp.presenter;

import android.content.res.AssetManager;
import android.util.Log;

import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.model.Quiz;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainPresenter implements QuizContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final QuizContract.View mainView;

    private final Quiz quiz;

    private final SecureRandom random;

    public MainPresenter(final QuizContract.View view, final Quiz quiz, final SecureRandom secureRandom) {
        this.mainView = view;
        this.quiz  = quiz;
        this.random = secureRandom;
    }

    @Override
    public void loadCountries(final Set<String> regions, final int choices, final AssetManager assets) {
        initializeQuiz(choices);
        loadCountries(regions, assets);
    }

    @Override
    public void selectCountriesForQuiz() {
        int selectedFlagsCounter = 0;
        int numberOfAllFlags = quiz.getCountriesSize();

        while (selectedFlagsCounter < quiz.getNumberOfQuestions()) {
            String country = quiz.getCountryAt(random.nextInt(numberOfAllFlags));
            if (!quiz.alreadySelectedCountry(country)) {
                quiz.addCountryToQuiz(country);
                ++selectedFlagsCounter;
            }
        }
    }

    @Override
    public void loadNextQuestion() {
        final String selectedCountry = quiz.selectCountry();
        mainView.setQuestionNumber(quiz.getQuestionNumber(), quiz.getNumberOfQuestions());
        mainView.setFlag(createFlagPath(selectedCountry));
    }

    @Override
    public List<String> selectPossibleAnswers() {
        final List<String> selectedChoices = new ArrayList<>();
        while (selectedChoices.size() < quiz.getNumberOfChoices()) {
            String country = quiz.getCountryAt(random.nextInt(quiz.getCountriesSize()));
            String countryName = quiz.getCountryName(country);
            if (!selectedChoices.contains(countryName)) {
                selectedChoices.add(countryName);
            }
        }
        if (!selectedChoices.contains(quiz.getCorrectCountryName())) {
            selectedChoices.add(random.nextInt(selectedChoices.size()), quiz.getCorrectCountryName());
            selectedChoices.remove(selectedChoices.size() - 1);
        }
        return selectedChoices;
    }

    @Override
    public boolean validateChoice(final String choice) {
        return quiz.isCorrectAnswer(choice);
    }

    @Override
    public boolean isLastAnswer() {
        return quiz.isLastAnswer();
    }

    @Override
    public int getTotalNumberOfGuesses() {
        return quiz.getNumberOfGuesses();
    }

    private void initializeQuiz(final int choices) {
        this.quiz.resetQuiz();
        this.quiz.setNumberOfChoices(choices);
    }

    private String createFlagPath(final String selectedCountry) {
        return selectedCountry.substring(0, selectedCountry.indexOf("-")) + "/" + selectedCountry;
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
