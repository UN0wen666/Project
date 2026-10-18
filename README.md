# 🍜 Gin Rai Dee? — มื้อนี้กินอะไรดี?

แอปพลิเคชัน Android ช่วยตัดสินใจเลือกเมนูอาหาร เขียนด้วยภาษา Java
ออกแบบตามหลัก **OOP + MVC**

> วิชา: การเขียนโปรแกรมคอมพิวเตอร์ขั้นสูง — อาจารย์วนารัตน์ จุฬพันธ์ทอง
> สาขาวิศวกรรมคอมพิวเตอร์ มหาวิทยาลัยราชภัฏพิบูลสงคราม (PSRU)

---

## เปิดโปรเจคยังไง

1. เปิด Android Studio → **File → Open** → เลือกโฟลเดอร์นี้
2. รอ Gradle sync จบ (ครั้งแรกจะโหลด dependency สักพัก)
3. กด **Run ▶** เลือก emulator หรือมือถือจริง (ต้องเป็น Android 7.0 / API 24 ขึ้นไป)


---

## โครงสร้างไฟล์

```
app/src/main/java/com/psru/ginraidee/
├── model/                      ← ชั้น Model (ข้อมูล + กฎการทำงาน)
│   ├── Food.java               abstract class — คลาสแม่ของอาหารทุกจาน
│   ├── HealthyFood.java        อาหารเพื่อสุขภาพ
│   ├── FastFood.java           อาหารเน้นความเร็ว
│   ├── SharingFood.java        อาหารสายปาร์ตี้-ครอบครัว
│   ├── DessertFood.java        อาหารประเภทของหวาน-มื้อเบา ๆ
│   ├── FoodCategory.java       enum หมวดความอยาก
│   ├── FoodFilter.java         เงื่อนไขการกรอง (ราคา/แคลอรี่/มังสวิรัติ)
│   ├── FoodManager.java        ตรรกะ สุ่ม/ค้นหา/กรอง/เพิ่ม-แก้-ลบ
│   ├── FoodRepository.java     คลังเมนู offline 40 จาน
│   └── AiSuggestion.java       ผลลัพธ์ 1 รายการจาก AI
│
├── controller/                 ← ชั้น Controller (รับ input เชื่อม Model กับ View)
│   ├── MainActivity.java       หน้าแรก
│   ├── ChoiceActivity.java     เลือกหมวด
│   ├── RandomActivity.java     หมุนสุ่ม + ตั้งเงื่อนไข
│   ├── ResultActivity.java     แสดงผลลัพธ์
│   ├── AiActivity.java         หน้าคุยกับ AI
│   ├── AiSuggestController.java ตรรกะเรียก Gemini + แปลงผลลัพธ์
│   └── SuggestionAdapter.java  เอาข้อมูลใส่ RecyclerView
│
└── util/
    └── GeminiApiClient.java    ยิง HTTP ไป Gemini API

app/src/main/res/layout/        ← ชั้น View (XML ล้วน ไม่มี logic)
├── activity_main.xml
├── activity_choice.xml
├── activity_random.xml
├── activity_result.xml
├── activity_ai.xml
└── item_food.xml
```

รายละเอียดการออกแบบ + Class Diagram อยู่ที่ [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)

---

## หลัก OOP 4 ข้อ อยู่ตรงไหนบ้าง

| หลักการ | อยู่ที่ไหน | เปิดไฟล์ไหนดู |
|---|---|---|
| **Abstraction** | `Food` เป็น abstract class สร้าง object ตรง ๆ ไม่ได้ | `model/Food.java` |
| **Inheritance** | คลาสลูก 4 ตัว `extends Food` | `model/HealthyFood.java` และอีก 3 ไฟล์ |
| **Polymorphism** | เรียก `getInfo()` / `getTag()` ตัวเดียว แต่ได้ผลต่างกันตามคลาสลูก | `controller/ResultActivity.showFood()` |
| **Encapsulation** | field เป็น `private` ทุกตัว เข้าถึงผ่าน getter/setter | `model/Food.java` |

---

## ขอบเขตของโปรเจค (Scope)

ทำงาน **offline เป็นหลัก** ข้อมูลอาหารเก็บในเครื่องทั้งหมด 

✅ วางแผนใช้ ❌ ตัดออก

| สถานะ | ฟีเจอร์ |
|---|---|
| ✅ | เลือกหมวดความอยาก → สุ่มเมนู |     
| ✅ | Filter ตามเงื่อนไข (ราคา / แคลอรี่ / มังสวิรัติ) |
| ✅ | AI แนะนำเมนูจากคำอธิบายของผู้ใช้ (Gemini API) |
| ❌ | ค้นหาร้านใกล้เคียงจาก GPS / Google Places | 
| ❌ | แนะนำเมนูตามกระแสโซเชียล |
| ❌ | โหมดกลุ่มแบบ real-time |  

**จุดเดียวที่ต่อ internet คือหน้า AI** ที่เหลือทำงานได้แม้ปิดเน็ต

---

## ทีมพัฒนา

นักศึกษาสาขาวิศวกรรมคอมพิวเตอร์ มหาวิทยาลัยราชภัฏพิบูลสงคราม จำนวน 5 คน

นาย ณัฐกรณ์ น้อยเลิศบุญ  รหัสนักศึกษา    6812247007

นาย ธีรภัทร ทองมี  รหัสนักศึกษา    6812247014

นาย จิรครินทร์ ปาผล    รหัสนักศึกษา    6812247040

นาย ณัฐวัฒน์ คงแพง รหัสนักศึกษา    6812247041

นาย ปัณณพัฒน์ นิพัทธ์คุณ   รหัสนักศึกษา    6812247047

## เครื่องมือหลัก
Claude Code,Chat GPT, Gemini