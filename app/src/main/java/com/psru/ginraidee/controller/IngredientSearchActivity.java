package com.psru.ginraidee.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.psru.ginraidee.R;
import com.psru.ginraidee.util.BottomNavHelper;

public class IngredientSearchActivity extends AppCompatActivity {

    private EditText etSearchKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_search);

        etSearchKeyword = findViewById(R.id.etSearchKeyword);
        Button btnSearch = findViewById(R.id.btnSearch);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupChip(R.id.chipEgg, "ไข่");
        setupChip(R.id.chipChicken, "ไก่");
        setupChip(R.id.chipPork, "หมู");
        setupChip(R.id.chipFish, "ปลา");
        setupChip(R.id.chipShrimp, "กุ้ง");
        setupChip(R.id.chipVeg, "ผัก");
        setupChip(R.id.chipMushroom, "เห็ด");
        setupChip(R.id.chipTofu, "เต้าหู้");
        setupChip(R.id.chipRice, "ข้าว");

        btnSearch.setOnClickListener(v -> performSearch(etSearchKeyword.getText().toString()));

        BottomNavHelper.setup(this, R.id.navSearch);
    }

    private void setupChip(int buttonId, String keyword) {
        findViewById(buttonId).setOnClickListener(v -> {
            etSearchKeyword.setText(keyword);
            performSearch(keyword);
        });
    }

    private void performSearch(String keyword) {
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(SearchResultActivity.EXTRA_KEYWORD, keyword);
        startActivity(intent);
    }
}
