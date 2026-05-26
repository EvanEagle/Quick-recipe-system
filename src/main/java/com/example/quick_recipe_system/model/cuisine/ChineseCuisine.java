package com.example.quick_recipe_system.model.cuisine;

import java.util.List;

import com.example.quick_recipe_system.model.Recipe;

public class ChineseCuisine extends AbstractCuisine {

        @Override
        public String getCuisineName() {
                return "中式料理";
        }

        public ChineseCuisine() {
                // 1.
                this.addRecipe(new Recipe(1, "番茄炒蛋", 15,
                                List.of("番茄", "雞蛋", "蔥花", "薑末"),
                                List.of("番茄醬", "水", "糖", "鹽"),
                                List.of("將雞蛋加鹽巴調味並打散",
                                                "將打好的雞蛋炒拌至７分熟",
                                                "加入薑末爆香",
                                                "加入番茄",
                                                "依照個人喜好加入調味",
                                                "加入少許的水",
                                                "將番茄悶至熟軟熟透",
                                                "將蛋加入炒拌至入味並撒上蔥花即可上桌"),
                                List.of("蛋料理", "家常菜"),
                                "https://www.youtube.com/embed/frFPrY8upmw","arden123"));
                // 2.
                this.addRecipe(new Recipe(2, "麻婆豆腐", 15,
                                List.of("盒裝豆腐", "豬絞肉", "蔥", "蒜末", "薑末"),
                                List.of("紅辣椒醬", "醬油", "白砂糖", "水", "太白粉水", "香油", "花椒粉"),
                                List.of("豆腐切丁，蔥切花，備用",
                                                "熱鍋，倒入少許油，先以小火爆香蒜末、薑末，再放入豬絞肉炒熟",
                                                "加入紅辣椒醬炒香後加入醬油、白砂糖，水，待燒開後放入豆腐丁略煮滾",
                                                "煮滾後開小火，邊慢慢淋入太白粉水，邊搖晃鍋子，使太白粉水，邊搖晃鍋子，使太白粉水均勻散布",
                                                "以鍋鏟輕推，勿使豆腐破爛，加入香油即可裝盤，再灑上蔥花及花椒粉即可"),
                                List.of("豆腐料理", "家常菜"),
                                "https://www.youtube.com/embed/PnCWCnozwvc","arden123"));
                // 3.
                this.addRecipe(new Recipe(3, "紅燒豆腐", 10,
                                List.of("板豆腐", "香菇", "紅蘿蔔絲", "芹菜段"),
                                List.of("醬油", "醬油膏"),
                                List.of("板豆腐洗淨瀝乾、切厚片，備用",
                                                "熱鍋，加入２大匙油，再將板豆腐片放入鍋中，煎至兩面微焦後加入香菇絲炒香，再放入紅蘿蔔絲炒香",
                                                "鍋中續放入所有調味料、水拌勻，煮約１分鐘後再加入芹菜鍛燒煮至所有食材入味即可起鍋"),
                                List.of("素食", "家常菜"),
                                "https://www.youtube.com/embed/n4W022W8d3c","arden123"));
                // 4.
                this.addRecipe(new Recipe(4, "洋蔥煎肉餅", 10,
                                List.of("豬絞肉", "洋蔥末", "蔥花", "薑末"),
                                List.of("鹽", "細砂糖", "黑胡椒粉", "米酒", "太白粉", "番茄醬"),
                                List.of("豬絞肉放入鋼盆中，加入鹽攪拌至有黏性",
                                                "做法１加入洋蔥末、蔥花及薑末拌勻，再加入細砂糖、黑胡椒、米酒及太白粉攪拌至均勻",
                                                "將拌好的豬絞肉平分成約拳頭大小，並整形成圓餅形",
                                                "平底鍋中倒入２大匙的沙拉油，將肉餅排入鍋中，蓋上鍋蓋，以小火煎至兩面微焦香",
                                                "煎熟後取出盛盤，沾番茄醬食用即可"),
                                List.of("早餐"),
                                "https://www.youtube.com/embed/bI70ShwVtEk","arden123"));
                // 5.
                this.addRecipe(new Recipe(5, "香蔥煎蛋", 8,
                                List.of("蛋", "蔥"),
                                List.of("香油", "鹽", "二砂糖", "胡椒鹽"),
                                List.of("蔥去頭，切成細蔥花",
                                                "蔥倒入香油、鹽、二砂糖、胡椒鹽，攪拌均勻後，打入蛋",
                                                "熱鍋下油，將蛋稍微打散後下鍋",
                                                "中火稍微搖晃煎在底部攪拌至定型(約３分鐘)",
                                                "翻面煎，定型後再晃動煎１分鐘煎熟即可"),
                                List.of("蛋料理", "家常菜"),
                                "https://www.youtube.com/embed/oAg53uI8yJc","arden123"));
        }

}
