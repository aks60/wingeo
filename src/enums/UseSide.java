package enums;

// Стороны для профилей (SYSPROA.ASETS)

import static enums.UseType.values;
import static enums.TypeOpen1.values;
import java.util.stream.Stream;
/**
 * 
 * select distinct ASETS from PRO4_SYSPROA where region_id = 177 order by ASETS
 */
// 
public enum UseSide implements Enam { 
    VERT(-3, "Вертикальная"),//разрешена вертикальная установка профиля
    HORIZ(-2, "Горизонтальная"),//разрешена горизонтальная установка профиля
    ANY(-1, "Любая"),//разрешена установка профиля на любую сторону и в любое положение
    MANUAL(0, "Вручную"),//запрет на автоматическую установку уникального артикула при установке по умолчанию любого контейнера 
    BOT(1, "Нижняя"),//разрешена установка профиля только на нижнюю сторону изделия или створки
    RIGHT(2, "Правая"),// разрешена установка профиля только на правую сторону изделия или створки
    TOP(3, "Верхняя"),//разрешена установка профиля только на верхнюю сторону изделия или створки
    LEFT(4, "Левая");//разрешена установка профиля только на нижнюю левую изделия или створки

    public int id;
    public String name;

    UseSide(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int numb() {
        return id;
    }

    public String text() {
        return name;
    }
    
    public Enam[] fields() {
        return values();
    }
    
    public static UseSide get(int id) {
        return Stream.of(values()).filter(en -> en.numb() == id).findFirst().orElse(null);
    }
}
