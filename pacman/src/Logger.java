package src;

import src.pathfinding.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Logger {
    private static Logger instance;
    private FileWriter fileWriter;

    private Logger() {
        try {
            String logFile = "logfile.txt";
            this.fileWriter = new FileWriter(logFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void logNoMap(String gameFolder) {
        try {
            fileWriter.write(gameFolder + " – no maps found");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logDupFileNumbers(ArrayList<String> duplicateFiles, String gameFolder) {
        try {
            fileWriter.write(gameFolder + " – multiple maps at same level: ");
            for (int i = 0; i < duplicateFiles.size(); i++) {
                if (i == duplicateFiles.size() - 1) {
                    fileWriter.write(duplicateFiles.get(i));
                }
                else {
                    fileWriter.write(duplicateFiles.get(i) + "; ");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logNotOnePac(List<Node> pacmanLocations, String fileName) {
        try {
            if (pacmanLocations.size() == 0) {
                fileWriter.write("Level " + fileName + " - no start for Pacman");
            }
            else if (pacmanLocations.size() > 1) {
                fileWriter.write("Level " + fileName + " - more than one start for Pacman: ");
                for (int i = 0;  i < pacmanLocations.size(); i++) {
                    fileWriter.write("(" + pacmanLocations.get(i).x + "," + pacmanLocations.get(i).y + ")");
                    if (!(i == pacmanLocations.size()-1)) {
                        fileWriter.write("; ");
                    }
                }
            }
            fileWriter.write("\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logGoldPillNotAccessible(List<Node> inaccessibleGold, List<Node> inaccessiblePill, String fileName) {
        try {
            if (inaccessibleGold.size() > 0) {
                fileWriter.write("Level " + fileName + " - Gold not accessible: ");
                for (int i = 0; i < inaccessibleGold.size(); i++) {
                    fileWriter.write("(" + inaccessibleGold.get(i).x + "," + inaccessibleGold.get(i).y + ")");
                    if (!(i == inaccessibleGold.size()-1)) {
                        fileWriter.write("; ");
                    }
                }
            }
            if (inaccessiblePill.size() > 0) {
                if (inaccessibleGold.size() > 0) {
                    fileWriter.write("\n");
                }
                //fileWriter.close();
                fileWriter.write("Level " + fileName + " - Pill not accessible: ");
                for (int i = 0; i < inaccessiblePill.size(); i++) {
                    fileWriter.write("(" + inaccessiblePill.get(i).x + "," + inaccessiblePill.get(i).y + ")");
                    if (!(i == inaccessiblePill.size()-1)) {
                        fileWriter.write("; ");
                    }
                }
            }
            fileWriter.write("\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logAtLeastTwoGoldPill(String fileName) {
        try {
            fileWriter.write("Level " + fileName + "- less than 2 Gold and Pill");
            fileWriter.write("\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logTwoTilesEachPortal(Map<String, List<Node>> portalMap, String fileName) {
        try {
            for (Map.Entry<String, List<Node>> set : portalMap.entrySet()) {
                fileWriter.write("Level " + fileName + " - portal " + set.getKey() + " count is not 2: ");
                for (int i = 0; i < set.getValue().size(); i++) {
                    fileWriter.write("(" + set.getValue().get(i).x + "," + set.getValue().get(i).y + ")");
                }
                fileWriter.write("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeFileWriter() {
        try {
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
