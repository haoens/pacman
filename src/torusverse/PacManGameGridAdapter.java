package torusverse;
import src.PacManGameGrid;
public class PacManGameGridAdapter implements MapInterface {
    private PacManGameGrid adaptee;
    private int level;

    public PacManGameGridAdapter(PacManGameGrid adaptee, int level){
        this.adaptee = adaptee;
        this.level = level;
    }

    public char[][] intMapToChar(int[][] intMap) {
        char[][] arr = new char[intMap.length][intMap[0].length];
        for (int y = 0; y < intMap.length; y++){
            for (int x = 0; x < intMap[0].length; x++){
                int a = intMap[y][x];
                char c ;
                switch (a) {
                    case 0 -> c = 'b';
                    case 1 -> c = 'c';
                    case 3 -> c = 'd';
                    case 4 -> c = 'e';
                    case 5 -> c = 'f';
                    case 6 -> c = 'g';
                    case 7 -> c = 'h';
                    case 8 -> c = 'i';
                    case 9 -> c = 'j';
                    case 10 -> c = 'k';
                    case 11 -> c = 'l';
                    default -> c = 'a';
                }
                arr[y][x] = c;
            }
        }
        return arr;
    }
    @Override
    public char[][] getMap() {
        int[][] map = this.adaptee.getNthMazeArray(this.level);
        return intMapToChar(map);
    }
}
