package common;

// ласс дл€ преобразовани€ double-числа в рубли-копейки прописью
public class MoneyInWords {
    private static final String dig1[][] = {{"одна", "две", "три", "четыре", "п€ть", "шесть", "семь", "восемь", "дев€ть"},
                                            {"один", "два"}}; //dig[0] - female, dig[1] - male
    private static final String dig10[]  = {"дес€ть","одиннадцать", "двенадцать", "тринадцать", "четырнадцать", 
                                            "п€тнадцать", "шестнадцать", "семнадцать", "восемнадцать", "дев€тнадцать"};
    private static final String dig20[]  = {"двадцать", "тридцать", "сорок", "п€тьдес€т", 
                                            "шестьдес€т", "семьдес€т", "восемьдес€т", "дев€носто"};
    private static final String dig100[] = {"сто","двести", "триста", "четыреста", "п€тьсот", 
                                            "шестьсот", "семьсот", "восемьсот", "дев€тьсот"};
    private static final String leword[][] = { {"копейка", "копейки", "копеек", "0"},
                                               {"рубль", "рубл€", "рублей", "1"},
                                               {"тыс€ча", "тыс€чи", "тыс€ч", "0"},
                                               {"миллион", "миллиона", "миллионов", "1"},
                                               {"миллиард", "миллиарда", "миллиардов", "1"},
                                               {"триллион", "триллиона", "триллионов", "1"}};

    //рекурсивна€ функци€ преобразовани€ целого числа num в рубли
    private static String num2words(long num, int level) { 
        StringBuilder words = new StringBuilder(50);
        if (num==0) words.append("ноль ");         //исключительный случай
        int sex = leword[level][3].indexOf("1")+1; //не красиво конечно, но работает
        int h = (int)(num%1000);    //текущий трехзначный сегмент
        int d = h/100;              //цифра сотен
        if (d>0) words.append(dig100[d-1]).append(" ");
        int n = h%100;              
        d = n/10;                   //цифра дес€тков
        n = n%10;                   //цифра единиц
        switch(d) {
            case 0: break;
            case 1: words.append(dig10[n]).append(" ");
                    break;
            default: words.append(dig20[d-2]).append(" ");
        }
        if (d==1) n=0;              //при двузначном остатке от 10 до 19, цифра едициц не должна учитыватьс€
        switch(n) {
            case 0: break;
            case 1: 
            case 2: words.append(dig1[sex][n-1]).append(" "); 
                    break;
            default: words.append(dig1[0][n-1]).append(" "); 
        }   
        switch(n) {
            case 1: words.append(leword[level][0]);
                    break;
            case 2: 
            case 3:
            case 4: words.append(leword[level][1]);
                    break;
            default: if((h!=0)||((h==0)&&(level==1)))  //если трехзначный сегмент = 0, то добавл€ть нужно только "рублей"
                        words.append(leword[level][2]); 
        } 
        long nextnum = num/1000;
        if(nextnum>0) {
            return (num2words(nextnum, level+1) + " " + words.toString()).trim();
        } else {
            return words.toString().trim();
        }
    }

    //функци€ преобразовани€ вещественного числа в рубли-копейки
    //при значении money более 50-70 триллионов рублей начинает искажать копейки, осторожней при работе такими суммами
    public static String inwords(double money) {  
        if (money<0.0) return "error: отрицательное значение"; 
        String sm = String.format("%.2f", money);
        String skop = sm.substring(sm.length()-2, sm.length());    //значение копеек в строке
        int iw;
        switch(skop.substring(1)) {
            case "1": iw = 0;
                      break;
            case "2": 
            case "3":   
            case "4": iw = 1;
                      break;
            default:  iw = 2;          
        }
        long num = (long)Math.floor(money);
        if (num<1000000000000000l) { 
            return num2words(num, 1) + " " + skop + " " + leword[0][iw];
        } else 
            return "error: слишком много рублей " + skop + " " + leword[0][iw];
    }
 }
