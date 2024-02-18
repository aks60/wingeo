package common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//Профили проекта
public enum eProfile {
       
    P01("SA.OKNA <АРМ Администратор>", "ADMIN_ALL"),  
    P02("SA.OKNA <АРМ Технолог>", "TEXNOLOG_RO", "TEXNOLOG_RW"), 
    P03("SA.OKNA <АРМ Менеджер>", "MANAGER_RO", "MANAGER_RW"); 

    public final static int[] version = {2, 0}; //версия программы      
    public static eProfile profile = null; //профиль пользователя 
    public static String user = null;
    
    public String title; 
    public Set<String> roleSet;  
     
    //Конструктор
    eProfile(String title, String... role) {
        this.title = title;
        List list = List.of(role);
        this.roleSet = new HashSet<String>(list);
    }
}
