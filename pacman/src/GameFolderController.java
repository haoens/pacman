package src;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class GameFolderController {
    private PacManGameGrid grid;
    public GameFolderController(PacManGameGrid grid) {
        this.grid = grid;
    }

    // Get the files in gameFolder into an File array.
    public File[] getGameFolderContent(File gameFolder) {
        // Filter out non-xml files & files that do not begin with digit
        FilenameFilter filter = (dir, name) -> Character.isDigit(name.charAt(0)) && name.toLowerCase().endsWith(".xml");
        return gameFolder.listFiles(filter);
    }
    // Sort files in gameFolder by leading numbers only.
    public ArrayList<File> sortGameFolder(File[] gameFolder) {
        Map<Integer, File> treeMap = new TreeMap<>();
        for (File file : gameFolder) {
            treeMap.put(getFileNumber(file), file);
        }
        return new ArrayList<>(treeMap.values());
    }
    // Get the starting number of a file.
    public static int getFileNumber(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        String filename = file.getName();
        for (char c : filename.toCharArray()) {
            if (Character.isDigit(c)) stringBuilder.append(c);
            else break;
        }
        return Integer.parseInt(stringBuilder.toString());
    }

    // Turns a XML file into mazeArray.
    public ArrayList<int[][]> xmlToMazeArray(ArrayList<File> gameFolder) {
        ArrayList<int[][]> mazeArray = new ArrayList<>();
        try {
            for (File file : gameFolder) {
                int[][] mazeArrayTemp = new int[grid.getNbVertCells()][grid.getNbHorzCells()];
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                NodeList nList = doc.getElementsByTagName("row");
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        for (int j = 0; j < eElement.getElementsByTagName("cell").getLength(); j++) {
                            mazeArrayTemp[temp][j] =
                                    toInt(eElement.getElementsByTagName("cell").item(j).getTextContent());
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

    // Used by xmlToMazeArray to convert string in XML to int values.
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

    // Returns a maze array.
    public ArrayList<int[][]> getMazeArray(File targetFile) {
        return xmlToMazeArray(sortGameFolder(getGameFolderContent(targetFile)));
    }

    // Returns the nth File in sorted array.
    public String getNthFileSorted(File dir, int index) {
        return sortGameFolder(getGameFolderContent(dir)).get(index).getPath();
    }
}
