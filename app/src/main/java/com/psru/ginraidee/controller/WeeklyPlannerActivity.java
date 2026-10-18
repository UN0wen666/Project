package com.psru.ginraidee.controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.psru.ginraidee.R;
import com.psru.ginraidee.model.Food;
import com.psru.ginraidee.util.BottomNavHelper;

public class WeeklyPlannerActivity extends AppCompatActivity {

    private TextView tvDayTitle;
    private TextView tvBreakfastName;
    private TextView tvLunchName;
    private TextView tvDinnerName;

    private int currentDayIndex = 0;
    private int editingMealSlot = 0; // 0: เช้า, 1: กลางวัน, 2: เย็น

    private final String[] dayNames = {
            "วันจันทร์ (13 ต.ค.)",
            "วันอังคาร (14 ต.ค.)",
            "วันพุธ (15 ต.ค.)",
            "วันพฤหัสบดี (16 ต.ค.)",
            "วันศุกร์ (17 ต.ค.)",
            "วันเสาร์ (18 ต.ค.)",
            "วันอาทิตย์ (19 ต.ค.)"
    };

    private int[] dayBadgeIds;

    // เก็บข้อมูลเมนู 7 วัน วันละ 3 มื้อ [7][3] (เริ่มต้นเป็นว่างทั้งหมด)
    private final Food[][] weeklyPlan = new Food[7][3];

    private final ActivityResultLauncher<Intent> catalogLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Food selectedFood = (Food) result.getData().getSerializableExtra(MenuCatalogActivity.EXTRA_SELECTED_FOOD);
                    if (selectedFood != null) {
                        weeklyPlan[currentDayIndex][editingMealSlot] = selectedFood;
                        updateMealDisplay();
                        String mealName = editingMealSlot == 0 ? "มื้อเช้า" : (editingMealSlot == 1 ? "มื้อกลางวัน" : "มื้อเย็น");
                        Toast.makeText(this, "อัปเดต " + mealName + " เรียบร้อย ❤️", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_planner);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        tvDayTitle = findViewById(R.id.tvDayTitle);
        tvBreakfastName = findViewById(R.id.tvBreakfastName);
        tvLunchName = findViewById(R.id.tvLunchName);
        tvDinnerName = findViewById(R.id.tvDinnerName);

        dayBadgeIds = new int[]{
                R.id.badgeMon, R.id.badgeTue, R.id.badgeWed,
                R.id.badgeThu, R.id.badgeFri, R.id.badgeSat, R.id.badgeSun
        };

        findViewById(R.id.dayMon).setOnClickListener(v -> selectDay(0));
        findViewById(R.id.dayTue).setOnClickListener(v -> selectDay(1));
        findViewById(R.id.dayWed).setOnClickListener(v -> selectDay(2));
        findViewById(R.id.dayThu).setOnClickListener(v -> selectDay(3));
        findViewById(R.id.dayFri).setOnClickListener(v -> selectDay(4));
        findViewById(R.id.daySat).setOnClickListener(v -> selectDay(5));
        findViewById(R.id.daySun).setOnClickListener(v -> selectDay(6));

        MaterialButton btnAddMenu = findViewById(R.id.btnAddMenu);
        btnAddMenu.setOnClickListener(v -> openCatalogForSlot(1)); // ค่าเริ่มต้นเพิ่มมื้อกลางวัน

        findViewById(R.id.mealBreakfast).setOnClickListener(v -> openCatalogForSlot(0));
        findViewById(R.id.mealLunch).setOnClickListener(v -> openCatalogForSlot(1));
        findViewById(R.id.mealDinner).setOnClickListener(v -> openCatalogForSlot(2));

        selectDay(0);
        BottomNavHelper.setup(this, R.id.navWeekly);
    }

    private void selectDay(int dayIndex) {
        currentDayIndex = dayIndex;
        tvDayTitle.setText(dayNames[dayIndex]);

        for (int i = 0; i < dayBadgeIds.length; i++) {
            TextView badge = findViewById(dayBadgeIds[i]);
            if (badge != null) {
                if (i == dayIndex) {
                    badge.setBackgroundResource(R.drawable.bg_circle_button);
                    badge.setBackgroundTintList(getResources().getColorStateList(R.color.orange_primary, getTheme()));
                    badge.setTextColor(Color.WHITE);
                } else {
                    badge.setBackgroundResource(0);
                    badge.setTextColor(getResources().getColor(R.color.text_primary, getTheme()));
                }
            }
        }

        updateMealDisplay();
    }

    private void updateMealDisplay() {
        Food b = weeklyPlan[currentDayIndex][0];
        Food l = weeklyPlan[currentDayIndex][1];
        Food d = weeklyPlan[currentDayIndex][2];

        if (b != null) {
            tvBreakfastName.setText(b.getEmoji() + "  " + b.getFoodName());
            tvBreakfastName.setTextColor(getResources().getColor(R.color.text_primary, getTheme()));
        } else {
            tvBreakfastName.setText("➕ แตะเพื่อเลือกเมนูมื้อเช้า");
            tvBreakfastName.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));
        }

        if (l != null) {
            tvLunchName.setText(l.getEmoji() + "  " + l.getFoodName());
            tvLunchName.setTextColor(getResources().getColor(R.color.text_primary, getTheme()));
        } else {
            tvLunchName.setText("➕ แตะเพื่อเลือกเมนูมื้อกลางวัน");
            tvLunchName.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));
        }

        if (d != null) {
            tvDinnerName.setText(d.getEmoji() + "  " + d.getFoodName());
            tvDinnerName.setTextColor(getResources().getColor(R.color.text_primary, getTheme()));
        } else {
            tvDinnerName.setText("➕ แตะเพื่อเลือกเมนูมื้อเย็น");
            tvDinnerName.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));
        }
    }

    private void openCatalogForSlot(int slot) {
        editingMealSlot = slot;
        String mealName = slot == 0 ? "มื้อเช้า" : (slot == 1 ? "มื้อกลางวัน" : "มื้อเย็น");
        Toast.makeText(this, "เลือกเมนูสำหรับ " + mealName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MenuCatalogActivity.class);
        intent.putExtra(MenuCatalogActivity.EXTRA_SELECT_MODE, true);
        catalogLauncher.launch(intent);
    }
}
