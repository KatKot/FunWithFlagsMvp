package com.kotwicka.funwithflagsmvp.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.collect.Sets;
import com.kotwicka.funwithflagsmvp.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<MainActivity>(MainActivity.class, true, false);

    Intent intent;
    SharedPreferences.Editor sharedPreferencesEditor;


    @Before
    public void setUp() {
        Context context = getInstrumentation().getTargetContext();
        sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        intent = new Intent();
    }

    @Test
    public void shouldShowCorrectElementsForSettings() throws Exception {
        // given
        sharedPreferencesEditor.putString("pref_numberOfChoices", "4");
        sharedPreferencesEditor.putStringSet("pref_regionsToInclude", Sets.newHashSet("Africa"));
        sharedPreferencesEditor.commit();

        // when
        mActivityRule.launchActivity(intent);

        // then
        onView(withId(R.id.questionNumberTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.flagImageView)).check(matches(isDisplayed()));
        onView(withId(R.id.guessCountryTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.firstAnswerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.secondAnswerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.thirdAnswerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.fourthAnswerButton)).check(matches(isDisplayed()));

        onView(withId(R.id.fifthAnswerButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.sixthAnswerButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.seventhAnswerButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.eightAnswerButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.answerTextView)).check(matches(not(isDisplayed())));
    }

    @Test
    public void shouldDisableButtonAfterClick() throws Exception {
        // given
        sharedPreferencesEditor.putString("pref_numberOfChoices", "4");
        sharedPreferencesEditor.putStringSet("pref_regionsToInclude", Sets.newHashSet("Africa"));
        sharedPreferencesEditor.commit();

        // when
        mActivityRule.launchActivity(intent);

        // then
        onView(withId(R.id.firstAnswerButton)).perform(click());
        onView(withId(R.id.firstAnswerButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void shouldLunchSettingsActivityWhenSettingsIconClick() throws Exception {
        // given
        sharedPreferencesEditor.putString("pref_numberOfChoices", "4");
        sharedPreferencesEditor.putStringSet("pref_regionsToInclude", Sets.newHashSet("Africa"));
        sharedPreferencesEditor.commit();

        // when
        mActivityRule.launchActivity(intent);

        // then
        onView(withId(R.id.action_settings)).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
    }
}
