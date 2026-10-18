package com.psru.ginraidee.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.psru.ginraidee.R;
import com.psru.ginraidee.model.AiSuggestion;
import com.psru.ginraidee.model.Food;
import com.psru.ginraidee.model.FoodManager;
import com.psru.ginraidee.util.BottomNavHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private SuggestionAdapter adapter;
    private FoodManager foodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        foodManager = FoodManager.getInstance();

        RecyclerView rvFavorites = findViewById(R.id.rvFavorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuggestionAdapter();
        rvFavorites.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadFavorites();
        BottomNavHelper.setup(this, R.id.navFavorite);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        List<Food> favorites = foodManager.getFavoriteFoods();
        List<AiSuggestion> list = new ArrayList<>();
        for (Food f : favorites) {
            list.add(new AiSuggestion(f.getFoodName(), f.getTag() + " • " + f.getCalories() + " แคลอรี", f));
        }
        adapter.submit(list);
    }
}
