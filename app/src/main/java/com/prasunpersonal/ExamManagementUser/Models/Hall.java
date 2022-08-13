package com.prasunpersonal.ExamManagementUser.Models;

import org.parceler.Parcel;

import java.util.Map;
import java.util.Objects;

@Parcel
public class Hall {
    private String _id, name, updatedBy;
    private long updatedTime;
    private Map<String, Boolean> candidates;

    public Hall() {}

    public Hall(String name, Map<String, Boolean> candidates) {
        this.name = name;
        this.candidates = candidates;
        this.updatedBy = null;
        this.updatedTime = 0;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Boolean> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<String, Boolean> candidates) {
        this.candidates = candidates;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hall)) return false;
        Hall hall = (Hall) o;
        return get_id().equals(hall.get_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(get_id());
    }

    @Override
    public String toString() {
        return name;
    }
}
