package com.kotwicka.funwithflagsmvp.model;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class Quiz {

    private static final int NUMBER_OF_QUESTIONS = 10;

    private List<String> countries;
    private List<String> selectedCountries;
    private String correctCountryName;
    private int numberOfGuesses;
    private int questionNumber;
    private int numberOfChoices;

    @Inject
    public Quiz() {
        resetQuiz();
    }

    public void resetQuiz() {
        this.countries = new ArrayList<>();
        this.selectedCountries = new ArrayList<>();
        this.correctCountryName = "";
        this.questionNumber = 0;
        this.numberOfChoices = 0;
        this.numberOfGuesses = 0;
    }

    public String selectCountry() {
        final String selectedCountry = selectedCountries.remove(0);
        correctCountryName = getCountryName(selectedCountry);
        ++questionNumber;
        return selectedCountry;
    }

    public void addCountryToQuiz(final String country) {
        selectedCountries.add(country);
    }

    public boolean alreadySelectedCountry(final String country) {
        return selectedCountries.contains(country);
    }

    public void addCountry(final String countryFileName) {
        countries.add(countryFileName);
    }

    public String getCountryAt(final int countryIndex) {
        return countries.get(countryIndex);
    }

    public int getCountriesSize() {
        return countries.size();
    }

    public boolean isLastAnswer() {
        return questionNumber == NUMBER_OF_QUESTIONS;
    }

    public boolean isCorrectAnswer(final String country) {
        ++numberOfGuesses;
        return country.equals(correctCountryName);
    }

    public String getCountryName(String country) {
        return country.substring(country.indexOf("-") + 1).replace("_", " ").replace(".png", "");
    }

    public String getCorrectCountryName() {
        return correctCountryName;
    }

    public int getNumberOfGuesses() {
        return numberOfGuesses;
    }

    public int getNumberOfQuestions() {
        return NUMBER_OF_QUESTIONS;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public int getNumberOfChoices() {
        return numberOfChoices;
    }

    public void setNumberOfChoices(int numberOfChoices) {
        this.numberOfChoices = numberOfChoices;
    }
}
