package common;

import java.awt.Dimension;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
//
//https://www.vogella.com/tutorials/JavaPreferences/article.html
//
public class UserPreferences extends JFrame {

    private Preferences userPrefs;

    public UserPreferences() {
        // Здесь мы создаем объект для сохранения с именем “prefexample”
        userPrefs = Preferences.userRoot().node("prefexample");
        setToPreferredSize();

        // Форма при закрытии завершает приложение и делаем форму видимой          
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setToPreferredSize() {
        int width = userPrefs.getInt("width", 100);
        int height = userPrefs.getInt("height", 200);
        setSize(width, height);
    }

    public Dimension getDimensions() {
        int width = userPrefs.getInt("width", 100);
        int height = userPrefs.getInt("height", 200);
        return new Dimension(width, height);
    }

    public void putDimensions(Dimension dimension) {
        userPrefs.putInt("width", (int) dimension.getWidth());
        userPrefs.putInt("height", (int) dimension.getHeight());
    }

    public static void main(String[] args) {
        UserPreferences up = new UserPreferences();
        up.putDimensions(new Dimension(400, 500));
    }
}
