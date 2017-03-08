package eu.qm.fiszki;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by mBoiler on 01.12.2016.
 */

public class FirebaseManager {

    private FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseManager(@NonNull Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void sendEvent(@NonNull String name) {
        if(!Params.DEVELOP) {
            Bundle bundle = new Bundle();
            bundle.putString(name, name);
            mFirebaseAnalytics.logEvent(name, bundle);
        }
    }

    public static class Params {

        public static final boolean DEVELOP = false;

        public final static String NOTYFI_ON = "NOTYFI_ON";
        public final static String NOTYFI_OFF = "NOTYFI_OFF";
        public final static String MYWORDS = "MYWORDS_CLICK";
        public final static String LEARNING = "LEARNING_CLICK";
        public final static String LEARNING_ALL = "LEARNING_ALL_CLICK";
        public final static String LEARNING_CAT = "LEARNING_CATEGORY_CLICK";
        public final static String LEARNING_LANG = "LEARNING_LANG_CLICK";
        public final static String EXAM = "EXAM_CLICK";
        public final static String QUICK_ADD_BTN = "QUICK_ADD";
        public final static String ADD_CATEGORY = "ADD_CATEGORY";
        public final static String ADD_CATEGORY_BTN = "ADD_CATEGORY_BTN";
        public final static String ADD_FLASHCARD = "ADD_FLASHCARD";
        public final static String ADD_FLASHCARD_BTN = "ADD_FLASHCARD_BTN";
        public final static String EXAM_SHOW_BAD = "EXAM_SHOW_BAD_ANSWER";
        public final static String EXAM_OK_BTN = "EXAM_OK_BTN";
        public final static String NOTYFI_PASS = "NOTYFICATION_PASS";
        public final static String NOTYFI_WRONG = "NOTYFICATION_WRONG";
        public final static String LEARNING_DIALOG_SKIP = "LEARNING DIALOG SKIP";
        public final static String LEARNING_MENU_SKIP = "LEARNING MENU SKIP";
        public final static String FLASHCARD_STATISTIC_CLICK = "FLASHCARD STATISTIC CLICK";
        public final static String FLASHCARD_STATISTIC_RESET = "FLASHCARD STATISTIC RESET";

    }
}


