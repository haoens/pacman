package src;

import src.mapeditor.editor.Controller;
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
                new Controller(args[0], false);
            }
            // If Folder Argument run in test mode
            else {
                GameCallback gameCallback = new GameCallback();
                String propertiesPath =  "pacman/properties/test.properties";
                final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
                Game game = new Game(gameCallback, properties, targetFile.getPath());

                // If failed game check then open empty map editor.
                if (game.getGrid().getFailedGameCheck()) {
                    new Controller();;
                }
                // If failed level check open map editor with failed level.
                if (game.hasFailedChecking()) {
                    new Controller(game.getGrid().getNthFileSorted(targetFile, game.getCurrentLevel()), true);
                }
                // If no game check or level check error then open map editor when pass all levels.
                if (!game.getGrid().getFailedGameCheck() && !game.hasFailedChecking()){
                    new Controller();
                }
                Logger logger = Logger.getInstance();
                logger.closeFileWriter();
            }
        }
        // No argument, run in edit mode no map
        else {
            new Controller();
        }



    }


}
