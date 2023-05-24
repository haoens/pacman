import matachi.mapeditor.editor.Controller;
import src.*;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import java.io.File;
import java.util.Properties;

public class Driver {
    public static void main(String[] args) {

        if (args.length > 0) {
            File targetFile = new File(args[0]);

            // If File Argument, run in edit mode
            if (!targetFile.isDirectory()) {
                new Controller(args[0]);
            }
            // If Folder Argument run in test mode
            else {
                GameCallback gameCallback = new GameCallback();
                String propertiesPath =  "pacman/properties/test2.properties";
                final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
                Game game = new Game(gameCallback, properties, targetFile.getPath());
                if (game.hasFailedChecking()) {
                    new Controller(game.getGrid().getNthFileSorted(targetFile, game.getCurrentLevel()));
                }
                else {
                    new Controller();
                }
            }
        }
        // No argument, run in edit mode no map
        else {
            new Controller();
        }
    }


}
