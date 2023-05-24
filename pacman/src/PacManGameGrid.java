// PacGrid.java
package src;

import ch.aplu.jgamegrid.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;

public class PacManGameGrid
{
    private int nbHorzCells;
    private int nbVertCells;
    private ArrayList<int[][]> mazeArray =  new ArrayList<>();

    public PacManGameGrid(int nbHorzCells, int nbVertCells, String filepath)
    {
        this.nbHorzCells = nbHorzCells;
        this.nbVertCells = nbVertCells;

        File targetFile = new File(filepath);

        if (targetFile.isDirectory()) {
            // Do Game Check.
            if (gameCheck(getGameFolderContent(targetFile))) {
                // Pass game check load xml to maze array
                mazeArray = xmlToMazeArray(sortGameFolder(getGameFolderContent(targetFile)));
            }
        }
        else {
            ArrayList<File> maze = new ArrayList<>();
            maze.add(targetFile);
            mazeArray = xmlToMazeArray(maze);
        }
    }

    public int getCell(Location location, int level)
    {
        return mazeArray.get(level)[location.y][location.x];
    }

    private int toInt(String str)
    {
        if (str.equals("WallTile"))
            return 0;
        if (str.equals("PillTile"))
            return 1;
        if (str.equals("PathTile"))
            return 2;
        if (str.equals("GoldTile"))
            return 3;
        if (str.equals("IceTile"))
            return 4;
        if (str.equals("PacTile"))
            return 5;
        if (str.equals("TrollTile"))
            return 6;
        if (str.equals("TX5Tile"))
            return 7;
        if (str.equals("PortalDarkGoldTile"))
            return 8;
        if (str.equals("PortalDarkGrayTile"))
            return 9;
        if (str.equals("PortalWhiteTile"))
            return 10;
        if (str.equals("PortalYellowTile"))
            return 11;
        return -1;
    }

    private File[] getGameFolderContent(File gameFolder) {
        // Filter out non-xml files & files that do not begin with digit
        FilenameFilter filter = (dir, name) -> {
            if(Character.isDigit(name.charAt(0)) && name.toLowerCase().endsWith(".xml")){
                return true;
            }
            return false;
        };
        return gameFolder.listFiles(filter);
    }

    // Turns XML to MazeArray of Integer values.
    private ArrayList<int[][]> xmlToMazeArray(ArrayList<File> gameFolder) {
        ArrayList<int[][]> mazeArray = new ArrayList<>();

        try {
            for (File file : gameFolder) {
                int[][] mazeArrayTemp = new int[nbVertCells][nbHorzCells];
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);

                NodeList nList = doc.getElementsByTagName("row");

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        for (int j = 0; j < eElement.getElementsByTagName("cell").getLength(); j++) {
                            mazeArrayTemp[temp][j] = toInt(eElement.getElementsByTagName("cell").item(j).getTextContent());
                        }
                    }
                }
                mazeArray.add(mazeArrayTemp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return mazeArray;
    }

    // Sort files in gameFolder by leading numbers only.
    private ArrayList<File> sortGameFolder(File[] gameFolder) {

        Map<Integer, File> treeMap = new TreeMap<>();

        for (File file : gameFolder) {
            treeMap.put(getFileNumber(file), file);
        }

//    for (int i = 0; i < sortedGameFolder.size(); i++) {
//      System.out.println(sortedGameFolder.get(i));
//    }
        return new ArrayList<>(treeMap.values());
    }

    // Get the starting number of a file.
    private int getFileNumber(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        String filename = file.getName();
        for (char c : filename.toCharArray()) {
            if (Character.isDigit(c)) stringBuilder.append(c);
            else break;
        }
        return Integer.parseInt(stringBuilder.toString());
    }
    public int getMazeArraySize() {
        return mazeArray.size();
    }

    private boolean gameCheck(File[] gameFolder) {


        // Checks if at least one file in gameFolder.
        if (!(gameFolder.length > 0)) {
            System.out.println("no files");
            return false;
        }
        // Checks if all files start with a digit.
        for (File file : gameFolder) {
            if (!Character.isDigit(file.getName().charAt(0))) {
                System.out.println("not starting with digit");
                return false;
            }
        }
        // Checks if there are more than two files with same digits.
        HashSet<Integer> hashSet = new HashSet<>();
        for (File file : gameFolder) {
            if (hashSet.contains(getFileNumber(file))) {
                System.out.println("duplicate digit present");
                return false;
            } else {
                hashSet.add(getFileNumber(file));
            }
        }
        return true;
    }

    public String getNthFileSorted(File dir, int index) {
        return sortGameFolder(getGameFolderContent(dir)).get(index).getPath();
    }

    public int[][] getNthMazeArray(int index) {
        return mazeArray.get(index);
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

}
