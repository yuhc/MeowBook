package com.sadmeepo.meowbook.ui.weight_tracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeightTrackerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WeightTrackerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is weight_tracker fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}