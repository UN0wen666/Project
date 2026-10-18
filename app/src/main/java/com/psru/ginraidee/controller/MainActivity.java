package com.psru.ginraidee.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.psru.ginraidee.R;
import com.psru.ginraidee.model.FoodManager;
import com.psru.ginraidee.util.BottomNavHelper;

/**
 * หน้าแรกของแอป (Story Board หน้าที่ 1)
 *
 * <p>บทบาทในแบบ MVC: เป็น <b>Controller</b> — รับ event การกดปุ่มจาก View (XML)
 * แล้วสั่งเปลี่ยนหน้า ไม่มีตรรกะการสุ่มอยู่ในนี้เลย</p>
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvMenuCount = findViewById(R.id.tvMenuCount);
        MaterialButton btnStart = findViewById(R.id.btnStart);

        // ถาม Model ว่าตอนนี้มีเมนูในคลังกี่จาน แล้วให้ View แสดง
        int menuCount = FoodManager.getInstance().size();
        tvMenuCount.setText(getString(R.string.main_menu_count, menuCount));

        // Start random (ChoiceActivity)
        View.OnClickListener startRandom = v -> startActivity(new Intent(this, ChoiceActivity.class));
        btnStart.setOnClickListener(startRandom);
        findViewById(R.id.bannerRandom).setOnClickListener(startRandom);

        // Catalog (MenuCatalogActivity)
        findViewById(R.id.btnCatalog).setOnClickListener(v ->
                startActivity(new Intent(this, MenuCatalogActivity.class)));

        // 4 Grid Cards
        findViewById(R.id.cardAi).setOnClickListener(v ->
                startActivity(new Intent(this, AiActivity.class)));

        findViewById(R.id.cardFavorite).setOnClickListener(v ->
                startActivity(new Intent(this, FavoriteActivity.class)));

        findViewById(R.id.cardSearch).setOnClickListener(v ->
                startActivity(new Intent(this, IngredientSearchActivity.class)));

        findViewById(R.id.cardWeekly).setOnClickListener(v ->
                startActivity(new Intent(this, WeeklyPlannerActivity.class)));

        BottomNavHelper.setup(this, R.id.navHome);
    }
}
