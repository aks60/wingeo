package frames.swing;

//Фильтр, отбирающий файлы
public class FileFilter extends javax.swing.filechooser.FileFilter {

    //Принимаем файл или отказываем ему
    public boolean accept(java.io.File f) {
        // все каталоги принимаем
        if (f.isDirectory()) {
            return true;
        }
        //Для файлов смотрим на расширение
        String fileName = f.getName();
        int i = fileName.lastIndexOf('.');
        if ((i > 0) && (i < (fileName.length() - 1))) {
            String fileExt = fileName.substring(i + 1);
            if ("gdb".equalsIgnoreCase(fileExt) || "fdb".equalsIgnoreCase(fileExt)) {
                return true;
            }
        }
        return false;
    }

    //Иозвращаем описание фильтра
    public String getDescription() {
        return "Файлы баз данных (*.gdb; *.fdb))";
    }
}
