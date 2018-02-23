package com.kotwicka.funwithflagsmvp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Quiz {

    public static final int NUMBER_OF_QUESTIONS = 10;

    private List<String> countries;
    private List<String> selectedCountries;
    private Set<String> countryRegions;
    private String correctCountryName;
    private int numberOfGuesses;
    private int numberOfCorrectGuesses;
    private int questionNumber;
    private int numberOfChoices;

    public Quiz() {
        this.countries = new ArrayList<>();
        this.selectedCountries = new ArrayList<>();
        this.countryRegions = new HashSet<>();
        this.numberOfCorrectGuesses = 0;
        this.numberOfCorrectGuesses = 0;
        this.questionNumber = 0;
        this.numberOfChoices = 0;
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

    public int getQuestionNumber() {
        return questionNumber;
    }

    public int getNumberOfChoices() {
        return numberOfChoices;
    }

    public void setNumberOfChoices(int numberOfChoices) {
        this.numberOfChoices = numberOfChoices;
    }

    public boolean isCorrectAnswer(final String country) {
        return country.equals(correctCountryName);
    }

    public String getCorrectCountryName() {
        return correctCountryName;
    }

    public String getCountryName(String country) {
        return country.substring(country.indexOf("-") + 1, country.indexOf(".")).replace("_", " ");
    }
}
