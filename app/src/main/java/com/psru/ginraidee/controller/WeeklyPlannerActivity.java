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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeeklyPlannerActivity extends AppCompatActivity {

    private TextView tvDayTitle;
    private TextView tvBreakfastName;
    private TextView tvLunchName;
    private TextView tvDinnerName;

    private TextView tvWeekRange;

    private int currentDayIndex = 0;
    private int editingMealSlot = 0; // 0: เช้า, 1: กลางวัน, 2: เย็น

    // เรียงจันทร์ → อาทิตย์ ให้ตรงกับแถบวันใน layout
    private static final String[] DAY_NAMES = {
            "วันจันทร์", "วันอังคาร", "วันพุธ", "วันพฤหัสบดี",
            "วันศุกร์", "วันเสาร์", "วันอาทิตย์"
    };

    private static final String[] THAI_MONTHS = {
            "ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
            "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."
    };

    private int[] dayBadgeIds;

    // วันจันทร์ของสัปดาห์ที่กำลังแสดงอยู่ (คำนวณจากวันที่จริงของเครื่อง)
    private Calendar weekStart;

    // เก็บเมนูวันละ 3 มื้อ โดยใช้วันที่ (yyyy-MM-dd) เป็น key
    // ทำให้เลื่อนไปสัปดาห์ไหนก็ได้ ไม่ถูกจำกัดแค่ 7 วัน
    private final Map<String, Food[]> weeklyPlan = new HashMap<>();

    private final ActivityResultLauncher<Intent> catalogLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Food selectedFood = (Food) result.getData().getSerializableExtra(MenuCatalogActivity.EXTRA_SELECTED_FOOD);
                    if (selectedFood != null) {
                        getMealsForSelectedDay()[editingMealSlot] = selectedFood;
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
        tvWeekRange = findViewById(R.id.tvWeekRange);

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

        findViewById(R.id.btnPrevWeek).setOnClickListener(v -> changeWeek(-1));
        findViewById(R.id.btnNextWeek).setOnClickListener(v -> changeWeek(1));
        tvWeekRange.setOnClickListener(v -> goToToday()); // แตะช่วงวันที่เพื่อกลับมาสัปดาห์นี้

        goToToday();
        BottomNavHelper.setup(this, R.id.navWeekly);
    }

    /** เปิดสัปดาห์ปัจจุบัน และเลือกวันนี้ไว้ให้เลย */
    private void goToToday() {
        Calendar today = Calendar.getInstance();
        weekStart = getMondayOf(today);
        renderWeek();
        selectDay(getDayIndex(today));
    }

    /** เลื่อนสัปดาห์ไปข้างหน้า (+1) หรือย้อนหลัง (-1) */
    private void changeWeek(int offset) {
        weekStart.add(Calendar.WEEK_OF_YEAR, offset);
        renderWeek();
        selectDay(currentDayIndex);
    }

    /** หาวันจันทร์ของสัปดาห์ที่วันนั้นอยู่ */
    private static Calendar getMondayOf(Calendar date) {
        Calendar monday = (Calendar) date.clone();
        monday.set(Calendar.HOUR_OF_DAY, 0);
        monday.set(Calendar.MINUTE, 0);
        monday.set(Calendar.SECOND, 0);
        monday.set(Calendar.MILLISECOND, 0);
        monday.add(Calendar.DAY_OF_MONTH, -getDayIndex(date));
        return monday;
    }

    /** แปลงวันในสัปดาห์เป็น index 0 = จันทร์ ... 6 = อาทิตย์ */
    private static int getDayIndex(Calendar date) {
        return (date.get(Calendar.DAY_OF_WEEK) + 5) % 7;
    }

    private Calendar getDate(int dayIndex) {
        Calendar date = (Calendar) weekStart.clone();
        date.add(Calendar.DAY_OF_MONTH, dayIndex);
        return date;
    }

    private static String formatDate(Calendar date) {
        return date.get(Calendar.DAY_OF_MONTH) + " " + THAI_MONTHS[date.get(Calendar.MONTH)];
    }

    private static boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    /** เขียนตัวเลขวันที่ลงแถบปฏิทิน และช่วงวันที่ของสัปดาห์ (ปี พ.ศ.) */
    private void renderWeek() {
        for (int i = 0; i < dayBadgeIds.length; i++) {
            TextView badge = findViewById(dayBadgeIds[i]);
            badge.setText(String.valueOf(getDate(i).get(Calendar.DAY_OF_MONTH)));
        }
        Calendar sunday = getDate(6);
        int buddhistYear = sunday.get(Calendar.YEAR) + 543;
        tvWeekRange.setText(formatDate(weekStart) + " – " + formatDate(sunday) + " " + buddhistYear);
    }

    /** เมนู 3 มื้อของวันที่เลือกอยู่ ถ้ายังไม่มีก็สร้างช่องว่างไว้ให้ */
    private Food[] getMealsForSelectedDay() {
        Calendar date = getDate(currentDayIndex);
        String key = String.format(Locale.US, "%04d-%02d-%02d",
                date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
        Food[] meals = weeklyPlan.get(key);
        if (meals == null) {
            meals = new Food[3];
            weeklyPlan.put(key, meals);
        }
        return meals;
    }

    private void selectDay(int dayIndex) {
        currentDayIndex = dayIndex;
        Calendar selected = getDate(dayIndex);
        String title = DAY_NAMES[dayIndex] + " (" + formatDate(selected) + ")";
        if (isSameDay(selected, Calendar.getInstance())) {
            title += " • วันนี้";
        }
        tvDayTitle.setText(title);

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
        Food[] meals = getMealsForSelectedDay();
        Food b = meals[0];
        Food l = meals[1];
        Food d = meals[2];

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
