package com.kotwicka.funwithflagsmvp.components;

import com.kotwicka.funwithflagsmvp.contracts.QuizContract;
import com.kotwicka.funwithflagsmvp.model.Quiz;
import com.kotwicka.funwithflagsmvp.modules.QuizModule;
import com.kotwicka.funwithflagsmvp.presenter.MainPresenter;

import dagger.Component;

@Component(modules =  QuizModule.class)
public interface QuizComponent {

    void inject(MainPresenter presenter);
}
