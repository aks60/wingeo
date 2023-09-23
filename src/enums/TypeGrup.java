package enums;

import static enums.UseUnit.values;

public enum TypeGrup implements Enam {
    PARAM_USER(1, "Параметры пользов."),
    COLOR_GRP(2, "Группы текстур"),
    SERI_ELEM(3, "Серии МЦ"),
    PRICE_INC(4, "Наценки МЦ"),
    PRICE_DEC(5, "Скидки МЦ"),
    CATEG_ELEM(6, "Категогии МЦ"),
    COLOR_MAP(7, "Параметры соотв.цветов"),
    CATEG_VST(8, "Категории вставок"),
    SYS_DATA(9, "Расчётные данные"),
    CATEG_KIT(10, "Категории комплектов");

    public int id = 0;
    public String name = "";

    TypeGrup(int id, String name) {
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
}