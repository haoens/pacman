// PacGrid.java
package src;

import ch.aplu.jgamegrid.*;

import java.io.File;
import java.util.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;

public class PacManGameGrid
{
  private int nbHorzCells;
  private int nbVertCells;
  private ArrayList<int[][]> mazeArray =  new ArrayList<>();

  public PacManGameGrid(int nbHorzCells, int nbVertCells)
  {
    this.nbHorzCells = nbHorzCells;
    this.nbVertCells = nbVertCells;

    // Do Game Check.
    if (gameCheck(getGameFolderContent("pacman/gameFolder")) == true) {

      // Pass game check load xml to maze array
      mazeArray = xmlToMazeArray(sortGameFolder(getGameFolderContent("pacman/gameFolder")));
    };
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

  private File[] getGameFolderContent(String gameFolder) {
    File folder = new File(gameFolder);
    File[] listOfFiles = folder.listFiles();
    return listOfFiles;
  }

  // Turns XML to MazeArray of Integer values.
  private ArrayList<int[][]> xmlToMazeArray(ArrayList<File> gameFolder) {
    ArrayList<int[][]> mazeArray = new ArrayList<>();

    try {
      for (int i = 0; i < gameFolder.size(); i++) {
        int[][] mazeArrayTemp = new int[nbVertCells][nbHorzCells];
        File inputFile = gameFolder.get(i);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);

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

    Map<Integer, File> treeMap = new TreeMap<Integer, File>();

    for (int i = 0; i < gameFolder.length; i++) {
      treeMap.put(getFileNum(gameFolder[i].getName()), gameFolder[i]);
    }
    ArrayList<File> sortedGameFolder = new ArrayList(treeMap.values());

//    for (int i = 0; i < sortedGameFolder.size(); i++) {
//      System.out.println(sortedGameFolder.get(i));
//    }
    return sortedGameFolder;
  }

  // Get the starting number of a file.
  private int getFileNum(String fileName) {

    String fileNumber = "";

    for (int i = 0; i < fileName.length(); i++) {
      if (Character.isDigit(fileName.charAt(i))) {
        fileNumber = fileNumber + fileName.charAt(i);
      } else {
        break;
      }
    }
    return Integer.parseInt(fileNumber);
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
    for (int i = 0; i < gameFolder.length; i++) {
      if (!Character.isDigit(gameFolder[i].getName().charAt(0))) {
        System.out.println("not starting with digit");
        return false;
      }
    }
    // Checks if there are more than two files with same digits.
    HashSet<Integer> hashSet = new HashSet<>();
    for (int i = 0; i < gameFolder.length; i++) {
      if (hashSet.contains(getFileNum(gameFolder[i].getName()))) {
        System.out.println("duplicate digit present");
        return false;
      }
      else {
        hashSet.add(getFileNum(gameFolder[i].getName()));
      }
    }
    return true;
  }
}
