
package s3;


import java.awt.geom.Point2D;
import java.util.Arrays;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.Out;

public class KdTree {

    private static final boolean VERTICAL = true; //x hnit
    private static final boolean HORIZONTAL = false; //y hnit

    // construct an empty set of points
    private Node root;

    private class Node {
        private Point2D key;
        private Node left;
        private Node right;
        private boolean cutDirection;

        public Node(Point2D key, boolean cutDirection) {
            this.key = key;
            this.cutDirection = cutDirection;
        }
    }

    private int size;
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        if(size() == 0){
            return true;
        }
        return false;
    }

    // number of points in the set
    public int size() {
        return size(root);
    }

    public int size(point2D p){
        if(p == null){
            return 0;
        }
        else{
            return 1 + size(root.left) + size(root.right);
        }
    }
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if(root == null){
            root = new Node(p, VERTICAL);
        }
        else{
            insert(root, p, root.cutDirection);
        }
    };

    private void insert(Node node, Point2D p, boolean cDirection) {
        //we are inspecting a node which cuts the tree vertically
        if(cDirection == VERTICAL){
            //x coordinate of the inserted node is lower than the nodes node.
            if(node.key.getX() > p.key.getX()){
                //insert into left side of the tree.
                if(node.left == null){
                    node.left == new Node(p, HORIZONTAL);
                }
                else{
                    insert(node.left, p, node.left.cutDirection);
                }
            }
            //x coordinate of the inserted node is higher than the nodes node.
            else{
                //insert into right side of the tree.
                if(node.right == null){
                    node.right == new Node(p, HORIZONTAL);
                }
                else{
                    insert(node.right, p, node.right.cutDirection);
                }
            }
            //we are inspecting a node which cuts the tree horizontally
            else{
                //y coordinate of the inserted node is lower than the nodes node.
                if (node.key.getY() > p.key.getY()) {
                    //insert into left side of the tree.
                    if (node.left == null) {
                        node.left == new Node(p, VERTICAL);
                    } else {
                        insert(node.left, p, node.left.cutDirection);
                    }
                }
                //y coordinate of the inserted node is higher than the nodes node.
                else {
                    //insert into right side of the tree.
                    if (node.right == null) {
                        node.right == new Node(p, VERTICAL);
                    } else {
                        insert(node.right, p, node.right.cutDirection);
                    }
                }
            }
        }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        if(root == null){
            return false;
        }
        else {
            return contains(root, p, root.cutDirection);
        }
    }

    private boolean contains(Node ) {
    }

    // draw all of the points to standard draw
    public void draw() {

    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        return null;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        return p;
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int nrOfRecangles = in.readInt();
        int nrOfPointsCont = in.readInt();
        int nrOfPointsNear = in.readInt();
        RectHV[] rectangles = new RectHV[nrOfRecangles];
        Point2D[] pointsCont = new Point2D[nrOfPointsCont];
        Point2D[] pointsNear = new Point2D[nrOfPointsNear];
        for (int i = 0; i < nrOfRecangles; i++) {
            rectangles[i] = new RectHV(in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsCont; i++) {
            pointsCont[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsNear; i++) {
            pointsNear[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        KdTree set = new KdTree();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble(), y = in.readDouble();
            set.insert(new Point2D(x, y));
        }
        for (int i = 0; i < nrOfRecangles; i++) {
            // Query on rectangle i, sort the result, and print
            Iterable<Point2D> ptset = set.range(rectangles[i]);
            int ptcount = 0;
            for (Point2D p : ptset)
                ptcount++;
            Point2D[] ptarr = new Point2D[ptcount];
            int j = 0;
            for (Point2D p : ptset) {
                ptarr[j] = p;
                j++;
            }
            Arrays.sort(ptarr);
            out.println("Inside rectangle " + (i + 1) + ":");
            for (j = 0; j < ptcount; j++)
                out.println(ptarr[j]);
        }
        out.println("Contain test:");
        for (int i = 0; i < nrOfPointsCont; i++) {
            out.println((i + 1) + ": " + set.contains(pointsCont[i]));
        }

        out.println("Nearest test:");
        for (int i = 0; i < nrOfPointsNear; i++) {
            out.println((i + 1) + ": " + set.nearest(pointsNear[i]));
        }

        out.println();
    }
}
