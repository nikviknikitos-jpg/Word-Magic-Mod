package com.example.wordmagic.spell;

import java.util.ArrayList;
import java.util.List;

public class SpellRegistry {
    public static class Spell {
        public final int id;
        public final String name;
        public final int tome;
        public final String catalyst;
        public final int manaCost;
        public Spell(int id, String name, int tome, String catalyst, int manaCost) {
            this.id = id; this.name = name; this.tome = tome;
            this.catalyst = catalyst; this.manaCost = manaCost;
        }
    }
    public static final List<Spell> ALL = new ArrayList<>();
    public static void init() {
        String[][] data = {
            {"Игнис","Пиро-кольцо","Пламенный рывок","Испепеляющий луч","Слово Метеора"},
            {"Порыв Ветра","Крылья Эфира","Воздушный Щит","Вихрь","Грозовой Смерч"},
            {"Ледяное Копьё","Поступь Зимы","Снежная Буря","Ледяной Бастион","Абсолютный Ноль"},
            {"Каменный Снаряд","Купол Защиты","Землетрясение","Зов Недр","Гнев Тектоники"},
            {"Слово Исцеления","Взор Истины","Ослепляющая Вспышка","Очищение","Сияние Архангела"},
            {"Разряд","Цепная Искра","Шаг Молнии","Электро-сфера","Гнев Зевса"},
            {"Теневой Сгусток","Шаг сквозь Тень","Покров Невидимости","Похищение Жизни","Покров Ночи"},
            {"Ядовитый Шип","Цветение","Изумрудное Облако","Дар Леса","Зов Древнего Леса"},
            {"Блинк","Гравитационный Рывок","Отталкивающий Импульс","Небесный Прыжок","Разлом Измерений"},
            {"ДНК Дезинтеграции","Чёрная Дыра","Шторм Архимага","Эгида Архимага","Слово Конца Света"}
        };
        String[][] catalysts = {
            {"","","","","wordmagic:fire_rune"},
            {"","","","","wordmagic:storm_core"},
            {"","","","","wordmagic:aether_crystal"},
            {"","","","","wordmagic:fire_rune"},
            {"","","","","wordmagic:aether_crystal"},
            {"","","","","wordmagic:storm_core"},
            {"","","","","wordmagic:void_dust"},
            {"","","","","wordmagic:aether_crystal"},
            {"","","","","wordmagic:void_dust"},
            {"","","","","wordmagic:void_dust;wordmagic:storm_core"}
        };
        int id = 1;
        for (int t = 0; t < 10; t++) {
            for (int s = 0; s < 5; s++) {
                ALL.add(new Spell(id, data[t][s], t + 1, catalysts[t][s], 8 + t * 4));
                id++;
            }
        }
    }
    public static List<Spell> byTome(int tome) {
        List<Spell> out = new ArrayList<>();
        for (Spell s : ALL) if (s.tome == tome) out.add(s);
        return out;
    }
    public static Spell byId(int id) {
        for (Spell s : ALL) if (s.id == id) return s;
        return null;
    }
    public static List<Spell> all() { return ALL; }
}
