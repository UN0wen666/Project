package com.psru.ginraidee.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.psru.ginraidee.R;
import com.psru.ginraidee.model.FoodCategory;

/**
 * หน้าเลือกหมวดความอยาก (Story Board หน้าที่ 2)
 *
 * <p>ปุ่มแต่ละปุ่มผูกกับค่าใน {@link FoodCategory} หมวดละ 1 ค่า
 * ซึ่งตรงกับคลาสลูกของ Food หมวดละ 1 คลาสพอดี</p>
 */
public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        bindCategory(R.id.btnHealthy, FoodCategory.HEALTHY);
        bindCategory(R.id.btnFast, FoodCategory.FAST);
        bindCategory(R.id.btnSharing, FoodCategory.SHARING);
        bindCategory(R.id.btnDessert, FoodCategory.DESSERT);
        bindCategory(R.id.btnAll, FoodCategory.ALL);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    /** ผูกปุ่ม 1 ปุ่มเข้ากับหมวด 1 หมวด แล้วส่งต่อไปหน้าสุ่ม */
    private void bindCategory(int buttonId, FoodCategory category) {
        Button button = findViewById(buttonId);
        button.setText(getString(R.string.choice_button_format,
                category.getEmoji(), category.getDisplayName()));
        button.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, RandomActivity.class);
            intent.putExtra(RandomActivity.EXTRA_CATEGORY, category.name());
            startActivity(intent);
        });
    }
}
