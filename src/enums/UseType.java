package enums;

import dataset.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//Тип профиля в системе конструкций
//Таблица SYSPROF поле USE_TYPE
public enum UseType implements Enam {
    ANY(0, "Любой тип"),
    FRAME(1, "Коробка"),//профиль первого контура изделия
    STVORKA(2, "Створка"),//профиль не первого контура. Всего может быть до 10 уровней створки в Проеме Изделия. Устанавливается в проем с установкой Фурнитуры
    IMPOST(3, "Импост"),//профиль который делит Проем и Заполнение Проема, но не делит Коробку
    STOIKA(5, "Стойка"),//профиль который делит Проем, Заполнение и Коробку. При автоматической установке стойки на обоих концах образуются L-соединения
    CROSSBAR(6, "Поперечина"),//профиль, который делит Проем, но не делит Заполнение Проема. Используется как декоративная накладка на Заполнение
    RASKLADKA(7, "Раскладка"),//профиль раскладки. При указании этого применения, при наличии модуля "Раскладки, МС и Жалюзи" профиль будет отображаться в окне "Раскладка" в режиме проектирования. НЕ делит Заполнение Проема
    SHTULP(9, "Штульп"),//профиль по свойствам аналогичный профилю импоста. активирует действие опции "Установка створок в соответствии ведомая-ведущая"  окна "Настройки Интерфейса - Проектирование", а так же параметра "Фурнитура штульповая" в Спецификации Фурнитуры
    ERKER(10, "Эркер");//профиль эркера или добора. Делит контур Коробки на 2 контура

    public int id;
    public String name;
    //public Record sysprofRec;
    //public List<Record> sysprofList = new ArrayList<Record>();

    UseType(int id, String name) {
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

    public static UseType get(int id) {
        return Stream.of(values()).filter(en -> en.numb() == id).findFirst().orElse(null);
    }
    
    
}
