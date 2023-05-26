// PacGrid.java
package src;
import ch.aplu.jgamegrid.*;
import java.io.File;
import java.util.*;

public class PacManGameGrid
{
    private final int nbHorzCells;
    private final int nbVertCells;
    private ArrayList<int[][]> mazeArray =  new ArrayList<>();
    private final String filepath;
    private final File targetFile;
    private boolean failedGameCheck = false;
    private final GameFolderController gameFolderController = new GameFolderController(this);

    public PacManGameGrid(int nbHorzCells, int nbVertCells, String filepath)
    {
        this.nbHorzCells = nbHorzCells;
        this.nbVertCells = nbVertCells;
        this.filepath = filepath;
        this.targetFile = new File(filepath);

        if (targetFile.isDirectory()) {
            // Do Game Check.
            if (gameCheck(gameFolderController.getGameFolderContent(targetFile))) {
                // Pass game check load xml to maze array
                mazeArray = gameFolderController.getMazeArray(targetFile);
            }
            else {
                failedGameCheck = true;
            }
        }
        else {
            ArrayList<File> maze = new ArrayList<>();
            maze.add(targetFile);
            mazeArray = gameFolderController.xmlToMazeArray(maze);
        }
    }
    public int getCell(Location location, int level)
    {
        return mazeArray.get(level)[location.y][location.x];
    }

    private boolean gameCheck(File[] gameFolder) {
        Logger logger = Logger.getInstance();
        // Checks if at least one file in gameFolder.
        if (gameFolder.length == 0) {
            logger.logNoMap(filepath);
            return false;
        }
        // Checks if there are more than two files with same digits.
        Map<Integer, ArrayList<String>> fileMap = new HashMap<>();
        for (File file : gameFolder) {
            if (!fileMap.containsKey(GameFolderController.getFileNumber(file))) {
                fileMap.put(GameFolderController.getFileNumber(file), new ArrayList<>());
                fileMap.get(GameFolderController.getFileNumber(file)).add(file.getName());
            }
            else {
                fileMap.get(GameFolderController.getFileNumber(file)).add(file.getName());
            }
        }
        ArrayList<String> dupNumLevels = new ArrayList<>();
        for (ArrayList<String> fileNames: fileMap.values()) {
            if (fileNames.size() > 1) {
                for (String fileName : fileNames) {
                    dupNumLevels.add(fileName);
                }
            }
        }
        if (dupNumLevels.size() > 0) {
            logger.logDupFileNumbers(dupNumLevels, filepath);
            return false;
        }
        return true;
    }

    public int getNumTx5(int level) {
        int count = 0;
        for (int i = 0; i < nbVertCells; i++) {
            for (int j = 0; j < nbHorzCells; j++) {
                if (mazeArray.get(level)[i][j] == 7) {
                    count++;
                }
            }
        }
        return count;
    }
    public int getNumTroll(int level) {
        int count = 0;
        for (int i = 0; i < nbVertCells; i++) {
            for (int j = 0; j < nbHorzCells; j++) {
                if (mazeArray.get(level)[i][j] == 6) {
                    count++;
                }
            }
        }
        return count;
    }
    public int[][] getNthMazeArray(int index) {
        return mazeArray.get(index);
    }
    public int getMazeArraySize() {
        return mazeArray.size();
    }
    public boolean getFailedGameCheck() {
        return failedGameCheck;
    }
    public int getNbHorzCells() {
        return nbHorzCells;
    }
    public int getNbVertCells() {
        return nbHorzCells;
    }
    public GameFolderController getGameFolderController() {
        return gameFolderController;
    }
}
