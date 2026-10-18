package com.psru.ginraidee.controller;

import android.os.Bundle;
import android.widget.TextView;

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

public class SearchResultActivity extends AppCompatActivity {

    public static final String EXTRA_KEYWORD = "extra_keyword";

    private SuggestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        String keyword = getIntent().getStringExtra(EXTRA_KEYWORD);
        if (keyword == null) keyword = "";

        TextView tvResultCount = findViewById(R.id.tvResultCount);
        RecyclerView rvResults = findViewById(R.id.rvResults);
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuggestionAdapter();
        rvResults.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        List<Food> found = FoodManager.getInstance().searchFood(keyword);
        tvResultCount.setText("พบเมนูที่ตรงกับ \"" + keyword + "\" " + found.size() + " รายการ");

        List<AiSuggestion> list = new ArrayList<>();
        for (Food f : found) {
            list.add(new AiSuggestion(f.getFoodName(), f.getTag() + " • " + f.getCalories() + " แคลอรี", f));
        }
        adapter.submit(list);

        BottomNavHelper.setup(this, R.id.navSearch);
    }
}
