package com.psru.ginraidee.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.psru.ginraidee.R;
import com.psru.ginraidee.model.Food;
import com.psru.ginraidee.model.FoodCategory;
import com.psru.ginraidee.model.FoodFilter;
import com.psru.ginraidee.model.FoodManager;

/**
 * หน้าแสดงผลลัพธ์ (Story Board หน้าที่ 4)
 *
 * <p>จุดที่เห็น Polymorphism ชัดที่สุดอยู่ในเมธอด {@link #showFood(Food)} —
 * เราเรียก {@code food.getInfo()} เหมือนกันทุกครั้ง
 * แต่ข้อความที่ออกมาต่างกันตามว่า object จริง ๆ เป็นคลาสลูกตัวไหน
 * โดยที่ Activity นี้ไม่ต้องรู้เลยว่ามันคือคลาสอะไร</p>
 */
public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_FOOD = "extra_food";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_FILTER = "extra_filter";

    private final FoodManager foodManager = FoodManager.getInstance();

    private TextView emojiTextView;
    private TextView foodNameTextView;
    private TextView tagTextView;
    private TextView infoTextView;
    private MaterialButton favoriteButton;
    private TextView ingredientsTextView;
    private TextView recipeTextView;

    private FoodCategory category;
    private FoodFilter filter;
    private Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        emojiTextView = findViewById(R.id.tvEmoji);
        foodNameTextView = findViewById(R.id.tvFoodName);
        tagTextView = findViewById(R.id.tvTag);
        infoTextView = findViewById(R.id.tvInfo);
        favoriteButton = findViewById(R.id.btnFavorite);
        ingredientsTextView = findViewById(R.id.tvIngredients);
        recipeTextView = findViewById(R.id.tvRecipe);

        category = FoodCategory.fromName(getIntent().getStringExtra(EXTRA_CATEGORY));
        filter = readFilter();

        Food food = readFood();
        if (food == null) {
            // กันเหนียว เผื่อเปิดหน้านี้มาโดยไม่มีข้อมูลติดมาด้วย
            food = foodManager.randomFood(category, filter);
        }
        showFood(food);

        favoriteButton.setOnClickListener(v -> toggleFavorite());

        Button spinAgainButton = findViewById(R.id.btnSpinAgain);
        spinAgainButton.setOnClickListener(v -> spinAgain());

        findViewById(R.id.btnViewLocation).setOnClickListener(v -> {
            if (currentFood != null) {
                String query = "ร้าน " + currentFood.getFoodName() + " พิษณุโลก";
                try {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query)));
                        startActivity(webIntent);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "เปิดแผนที่ไม่ได้: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btnHome).setOnClickListener(v -> goHome());
    }

    /** เอา object Food มาแสดงบนหน้าจอ */
    private void showFood(Food food) {
        this.currentFood = food;
        if (food == null) {
            emojiTextView.setText("😅");
            foodNameTextView.setText(R.string.result_empty_title);
            tagTextView.setText("");
            infoTextView.setText(R.string.random_no_match);
            ingredientsTextView.setText("ไม่ระบุวัตถุดิบ");
            recipeTextView.setText("ไม่ระบุวิธีทำ");
            favoriteButton.setText("🤍");
            return;
        }
        emojiTextView.setText(food.getEmoji());
        foodNameTextView.setText(food.getFoodName());
        tagTextView.setText(food.getTag());
        infoTextView.setText(food.getInfo());
        ingredientsTextView.setText(food.getIngredients());
        recipeTextView.setText(food.getRecipe());
        updateFavoriteButton(food);
    }

    private void toggleFavorite() {
        if (currentFood == null) {
            return;
        }
        boolean isFav = foodManager.toggleFavorite(currentFood);
        updateFavoriteButton(currentFood);
        String message = isFav ? "บันทึกเข้าเมนูโปรดแล้ว ❤️" : "ยกเลิกเมนูโปรดแล้ว 🤍";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateFavoriteButton(Food food) {
        if (food != null && food.isFavorite()) {
            favoriteButton.setText("❤️");
        } else {
            favoriteButton.setText("🤍");
        }
    }

    /** ปุ่ม "หมุนใหม่" — สั่ง Model สุ่มใหม่ในหมวดและเงื่อนไขเดิม */
    private void spinAgain() {
        Food next = foodManager.randomFood(category, filter);
        if (next == null) {
            Toast.makeText(this, R.string.random_no_match, Toast.LENGTH_LONG).show();
            return;
        }
        showFood(next);
    }

    /** กลับไปหน้าแรก โดยเคลียร์หน้าที่ค้างอยู่ใน stack ทิ้ง */
    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("deprecation")
    private Food readFood() {
        Object extra = getIntent().getSerializableExtra(EXTRA_FOOD);
        return extra instanceof Food ? (Food) extra : null;
    }

    @SuppressWarnings("deprecation")
    private FoodFilter readFilter() {
        Object extra = getIntent().getSerializableExtra(EXTRA_FILTER);
        return extra instanceof FoodFilter ? (FoodFilter) extra : FoodFilter.NONE;
    }
}
