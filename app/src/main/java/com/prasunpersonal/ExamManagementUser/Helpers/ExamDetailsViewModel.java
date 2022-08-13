package com.prasunpersonal.ExamManagementUser.Helpers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prasunpersonal.ExamManagementUser.Models.Exam;
import com.prasunpersonal.ExamManagementUser.Models.Hall;

public class ExamDetailsViewModel extends ViewModel {
    private final MutableLiveData<Exam> setSelectedExam = new MutableLiveData<>();
    private final MutableLiveData<Hall> setSelectedHall = new MutableLiveData<>();

    public void setSetSelectedExam(Exam exam) {
        setSelectedExam.setValue(exam);
    }

    public void setSetSelectedHall(Hall hall) {
        setSelectedHall.setValue(hall);
    }

    public MutableLiveData<Exam> getSetSelectedExam() {
        return setSelectedExam;
    }

    public MutableLiveData<Hall> getSetSelectedHall() {
        return setSelectedHall;
    }
}
