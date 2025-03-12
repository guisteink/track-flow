package com.example.track_flow.model;

import java.util.List;

public class DogFactResponse {
    private List<DogFact> data;

    public List<DogFact> getData() {
        return data;
    }

    public void setData(List<DogFact> data) {
        this.data = data;
    }
}