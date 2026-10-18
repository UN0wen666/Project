package com.psru.ginraidee.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * ตัวจัดการคลังเมนูอาหารทั้งหมด — เป็น "สมอง" ของฝั่ง Model
 *
 * <p>เหตุผลที่แยกคลาสนี้ออกจาก {@link Food}: การสุ่ม / ค้นหา / กรอง
 * เป็นงานของ "คลังเมนู" ไม่ใช่งานของ "อาหาร 1 จาน"
 * ถ้าเอา randomFood() ไปไว้ใน Food จะผิดหลัก Single Responsibility</p>
 *
 * <p>ใช้รูปแบบ Singleton เพื่อให้ทุก Activity เห็นข้อมูลชุดเดียวกัน
 * (เพิ่ม/ลบเมนูจากหน้าไหนก็เห็นเหมือนกันหมด)</p>
 */
public class FoodManager {

    private static FoodManager instance;

    private final List<Food> foodList;
    private final Random random;

    /** เมนูที่สุ่มได้ล่าสุด ใช้กันไม่ให้กด "หมุนใหม่" แล้วได้จานเดิมซ้ำติดกัน */
    private Food lastPicked;

    /** ใช้ getInstance() แทนการ new ตรง ๆ */
    private FoodManager() {
        this.foodList = new ArrayList<>(FoodRepository.createDefaultFoods());
        this.random = new Random();
    }

    /** constructor สำหรับเทสต์ ให้ยัดรายการอาหารเองได้ */
    FoodManager(List<Food> foods, Random random) {
        this.foodList = new ArrayList<>(foods);
        this.random = random;
    }

    public static synchronized FoodManager getInstance() {
        if (instance == null) {
            instance = new FoodManager();
        }
        return instance;
    }

    // ------------------------------------------------------------------
    // อ่านข้อมูล
    // ------------------------------------------------------------------

    /** คืนสำเนาของรายการทั้งหมด (คืน copy เพื่อไม่ให้ข้างนอกมาแก้ list ตรง ๆ) */
    public List<Food> getFoodList() {
        return new ArrayList<>(foodList);
    }

    public int size() {
        return foodList.size();
    }

    // ------------------------------------------------------------------
    // รายการโปรด (Favorites)
    // ------------------------------------------------------------------

    /** สลับสถานะรายการโปรด */
    public boolean toggleFavorite(Food food) {
        if (food == null) {
            return false;
        }
        boolean newState = !food.isFavorite();
        food.setFavorite(newState);
        for (Food item : foodList) {
            if (item.getFoodName().equalsIgnoreCase(food.getFoodName())) {
                item.setFavorite(newState);
                break;
            }
        }
        return newState;
    }

    /** ดึงรายการอาหารโปรดทั้งหมด */
    public List<Food> getFavoriteFoods() {
        List<Food> favorites = new ArrayList<>();
        for (Food food : foodList) {
            if (food.isFavorite()) {
                favorites.add(food);
            }
        }
        return favorites;
    }

    // ------------------------------------------------------------------
    // สุ่มเมนู
    // ------------------------------------------------------------------

    /** สุ่มจากเมนูทั้งหมด */
    public Food randomFood() {
        return randomFood(FoodCategory.ALL, FoodFilter.NONE);
    }

    /** สุ่มเฉพาะในหมวดที่เลือก */
    public Food randomFood(FoodCategory category) {
        return randomFood(category, FoodFilter.NONE);
    }

    /**
     * สุ่มในหมวดที่เลือก + ผ่านเงื่อนไขที่กรองไว้
     *
     * @return เมนูที่สุ่มได้ หรือ null ถ้าไม่มีเมนูไหนเข้าเงื่อนไขเลย
     */
    public Food randomFood(FoodCategory category, FoodFilter filter) {
        List<Food> candidates = findCandidates(category, filter);
        if (candidates.isEmpty()) {
            return null;
        }

        // ถ้ามีให้เลือกมากกว่า 1 จาน อย่าเพิ่งคืนจานเดิมที่เพิ่งสุ่มไป
        if (candidates.size() > 1 && lastPicked != null) {
            candidates.remove(lastPicked);
        }

        Food picked = candidates.get(random.nextInt(candidates.size()));
        lastPicked = picked;
        return picked;
    }

    // ------------------------------------------------------------------
    // กรอง / ค้นหา
    // ------------------------------------------------------------------

    /**
     * รายชื่อเมนูที่เข้าเงื่อนไขทั้งหมด (หมวด + ตัวกรอง)
     * แยกออกมาเป็นเมธอดสาธารณะเพื่อให้หน้าจอเอาไปโชว์ตอนหมุนได้
     * โดยไม่ต้องเขียนตรรกะการกรองซ้ำอีกรอบฝั่ง Controller
     */
    public List<Food> findCandidates(FoodCategory category, FoodFilter filter) {
        List<Food> candidates = filterByType(category);
        if (filter == null || filter.isEmpty()) {
            return candidates;
        }
        List<Food> passed = new ArrayList<>();
        for (Food food : candidates) {
            if (filter.matches(food)) {
                passed.add(food);
            }
        }
        return passed;
    }

    /**
     * กรองตามหมวด — ถ้าเป็น {@link FoodCategory#ALL} จะคืนทั้งหมด
     * ตรงนี้คือจุดที่ Polymorphism ทำงาน: เรียก getCategory() ตัวเดียว
     * แต่ได้คำตอบต่างกันตามคลาสลูกของแต่ละ object
     */
    public List<Food> filterByType(FoodCategory category) {
        if (category == null || category == FoodCategory.ALL) {
            return getFoodList();
        }
        List<Food> result = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getCategory() == category) {
                result.add(food);
            }
        }
        return result;
    }

    /** กรองด้วยเงื่อนไขราคา/แคลอรี่/มังสวิรัติ */
    public List<Food> filterBy(FoodFilter filter) {
        if (filter == null || filter.isEmpty()) {
            return getFoodList();
        }
        List<Food> result = new ArrayList<>();
        for (Food food : foodList) {
            if (filter.matches(food)) {
                result.add(food);
            }
        }
        return result;
    }

    /** ค้นหาจากชื่อเมนูหรือคำอธิบาย (ไม่สนตัวพิมพ์เล็ก-ใหญ่) */
    public List<Food> searchFood(String keyword) {
        List<Food> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        for (Food food : foodList) {
            String name = food.getFoodName().toLowerCase(Locale.ROOT);
            String desc = food.getDescription() == null
                    ? "" : food.getDescription().toLowerCase(Locale.ROOT);
            if (name.contains(needle) || desc.contains(needle)) {
                result.add(food);
            }
        }
        return result;
    }

    /** หาเมนูที่ชื่อ "ใกล้เคียง" ที่สุด 1 จาน ใช้ตอนจับคู่ผลลัพธ์จาก AI กับคลังเมนูในเครื่อง */
    public Food findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String needle = name.trim().toLowerCase(Locale.ROOT);
        for (Food food : foodList) {           // รอบแรก: ชื่อตรงเป๊ะ
            if (food.getFoodName().toLowerCase(Locale.ROOT).equals(needle)) {
                return food;
            }
        }
        for (Food food : foodList) {           // รอบสอง: ชื่อมีคำนั้นอยู่
            if (food.getFoodName().toLowerCase(Locale.ROOT).contains(needle)) {
                return food;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    // เพิ่ม / แก้ไข / ลบ
    // ------------------------------------------------------------------

    public void addFood(Food food) {
        if (food != null) {
            foodList.add(food);
        }
    }

    /**
     * แก้ไขเมนู โดยยึด foodName เป็นตัวอ้างอิง
     *
     * @return true ถ้าเจอและแก้สำเร็จ
     */
    public boolean editFood(Food food) {
        if (food == null) {
            return false;
        }
        for (int i = 0; i < foodList.size(); i++) {
            if (foodList.get(i).getFoodName().equals(food.getFoodName())) {
                foodList.set(i, food);
                return true;
            }
        }
        return false;
    }

    /** @return true ถ้าลบสำเร็จ */
    public boolean deleteFood(Food food) {
        if (food == null) {
            return false;
        }
        if (food.equals(lastPicked)) {
            lastPicked = null;
        }
        return foodList.remove(food);
    }
}
