package com.example.afinal.Listeners;

import com.example.afinal.Models.SimilarRecipeResponse;

import java.util.List;

public interface SimilarRecipesListener {
    void didFetch (List<SimilarRecipeResponse> response, String message);
    void didError (String message);
}
