package torusverse;

import src.pathfinding.Node;

public class Portal {
    private Node node;
    private Portal connectedPortal;

    public Portal(int x, int y){
        this.node = new Node(x, y);
    }

    public Node getNode(){
        return node;
    }

    public Portal getConnectedPortal(){
        return connectedPortal;
    }
    public void joinPortals(Portal targetPortal){
        this.connectedPortal = targetPortal;
        targetPortal.connectedPortal = this;
    }
}
