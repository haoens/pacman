package torusverse;

import matachi.mapeditor.grid.Grid;
import src.PacManGameGrid;

public class EditorGridModelAdapter implements MapInterface {

    private Grid adaptee;
    public EditorGridModelAdapter(Grid grid) {
        this.adaptee = grid;
    }

    @Override
    public char[][] getMap() {
        return adaptee.getMap();
    }
}
