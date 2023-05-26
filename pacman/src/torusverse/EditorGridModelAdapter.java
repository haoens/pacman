package src.torusverse;

import src.mapeditor.grid.Grid;

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
