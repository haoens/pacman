package torusverse;

import src.pathfinding.Node;

public class MapPortal {
    private Node node;
    private MapPortal connectedPortal;

    public MapPortal(int x, int y){
        this.node = new Node(x, y);
    }

    public Node getNode(){
        return node;
    }

    public MapPortal getConnectedPortal(){
        return connectedPortal;
    }
    public void joinPortals(MapPortal targetPortal){
        this.connectedPortal = targetPortal;
        targetPortal.connectedPortal = this;
    }
}
