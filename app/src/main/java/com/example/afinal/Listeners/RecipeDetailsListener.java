package com.example.afinal.Listeners;

import com.example.afinal.Models.Recipe;
import com.example.afinal.Models.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response, String message);
    void didError(String message);
}
