package com.kotwicka.funwithflagsmvp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Quiz {

    public static final int NUMBER_OF_QUESTIONS = 10;

    public List<String> countries;
    public List<String> selectedCountries;
    private Set<String> countryRegions;
    private String correctCountryName;
    private int numberOfGuesses;
    private int numberOfCorrectGuesses;

    public Quiz() {
        this.countries = new ArrayList<>();
        this.selectedCountries = new ArrayList<>();
        this.countryRegions = new HashSet<>();
        this.numberOfCorrectGuesses = 0;
        this.numberOfCorrectGuesses = 0;
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
 }
