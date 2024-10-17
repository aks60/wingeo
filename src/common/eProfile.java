package common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


//������� �������
public enum eProfile {
       
    P01("SA.OKNA <��� �������������>", "ADMIN_ALL"),  
    P02("SA.OKNA <��� ��������>", "TEXNOLOG_RO", "TEXNOLOG_RW"), 
    P03("SA.OKNA <��� ��������>", "MANAGER_RO", "MANAGER_RW"); 

    public final static int[] version = {2, 0}; //������ ���������      
    public static eProfile profile = null; //������� ������������ 
    public static String user = null;
    
    public String title; 
    public Set<String> roleSet;  
     
    //�����������
    eProfile(String title, String... role) {
        this.title = title;
        List list = List.of(role);
        this.roleSet = new HashSet<String>(list);
    }
}
