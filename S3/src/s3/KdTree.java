
package s3;


//import java.awt.geom.Point2D;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

public class KdTree {

    private static final boolean VERTICAL = true; //x hnit
    private static final boolean HORIZONTAL = false; //y hnit

    // construct an empty set of points
    private Node root;

    private class Node {
        private Point2D key;
        private Node left;
        private Node right;
        private RectHV area;
        private boolean cutDirection;

        public Node(Point2D key, boolean cutDirection, RectHV area) {
            this.key = key;
            this.cutDirection = cutDirection;
            this.area = area;
        }
    }

    private int size;
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // number of points in the set
    public int size() {
        return size(root);
    }

    public int size(Node node){
        if(node == null){
            return 0;
        }
        else{
            return 1 + size(node.left) + size(node.right);
        }
    }
    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if(root == null){
            RectHV area = new RectHV(0.0, 0.0, 1.0, 1.0);
            root = new Node(p, VERTICAL, area);
        }
        else{
            insert(root, p, root.cutDirection);
        }
    }

    private void insert(Node node, Point2D p, boolean cDirection) {
    	if(contains(p)){
    		return;
    	}
        //we are inspecting a node which cuts the tree vertically
        if(cDirection == VERTICAL){
            //x coordinate of the inserted node is lower than the nodes node.
            if(node.key.x() > p.x()){
                //insert into left side of the tree.
                if(node.left == null){
                    RectHV area = new RectHV(node.area.xmin(), node.area.ymin(), node.key.x(), node.area.ymax());
                    node.left = new Node(p, HORIZONTAL, area);
                }
                else{
                    insert(node.left, p, node.left.cutDirection);
                }
            }
            //x coordinate of the inserted node is higher than the nodes node.
            else{
                //insert into right side of the tree.
                if(node.right == null){
                    RectHV area = new RectHV(node.key.x(), node.area.ymin(), node.area.xmax(), node.area.ymax());
                    node.right = new Node(p, HORIZONTAL, area);
                }
                else{
                    insert(node.right, p, node.right.cutDirection);
                }
            }
        }   
            //we are inspecting a node which cuts the tree horizontally
            else{
                //y coordinate of the inserted node is lower than the nodes node.
                if (node.key.y() > p.y()) {
                    //insert into bottom side of the tree.
                    if (node.left == null) {
                        RectHV area = new RectHV(node.area.xmin(), node.area.ymin(), node.area.xmax(), node.key.y());
                        node.left = new Node(p, VERTICAL, area);
                    } else {
                        insert(node.left, p, node.left.cutDirection);
                    }
                }
                //y coordinate of the inserted node is higher than the nodes node.
                else {
                    //insert into top side of the tree.
                    if (node.right == null) {
                        RectHV area = new RectHV(node.area.xmin(), node.key.y(), node.area.xmax(), node.area.ymax());
                        node.right = new Node(p, VERTICAL, area);
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

    private boolean contains(Node node, Point2D p, boolean cDirection ) {
    	if(node == null){
    		return false;
    	}
    	if(node.key.equals(p)){
    		return true;
    	}else{
    		if(cDirection == VERTICAL){
                if(node.key.x() > p.x()){
                    if(node.left == null){
                        return false;
                    }
                    else{
                        return contains(node.left, p, node.left.cutDirection);
                    }
                }else{
                    if(node.right == null){
                       return false;
                    }
                    else{
                        return contains(node.right, p, node.right.cutDirection);
                    }
                }
    		}else{
                if (node.key.y() > p.y()) {
                    if (node.left == null) {
                        return false;
                    } else {
                        return contains(node.left, p, node.left.cutDirection);
                    }
                }
                else {
                    if (node.right == null) {
                        return false;
                    } else {
                        return contains(node.right, p, node.right.cutDirection);
                    }
                }
    		}
    	}
    }

    // draw all of the points to standard draw
    public void draw() {

    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if(root == null){
            return null;
        }
        return range(root, rect);
    }

    private Iterable<Point2D> range(Node node, RectHV rect) {
        if(node == null){
            return null;
        }
        ArrayList<Point2D> returnList = new ArrayList<>();
        if(node.left != null) {
            if (rect.intersects(node.left.area)) {
                Iterable<Point2D> set = range(node.left, rect);
                for (Point2D p : set) {
                   returnList.add(p);
                }

            }
        }
        if(rect.contains(node.key)){
            returnList.add(node.key);
        }
        if(node.right != null) {
            if(rect.intersects(node.right.area)){
                Iterable<Point2D> set = range(node.right, rect);
                for (Point2D p : set) {
                    returnList.add(p);
                }

            }
        }
        return returnList;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        if(root == null){
            return null;
        }
        return nearest(p, root, root.key);
    }

    private Point2D nearest(Point2D p, Node node, Point2D nearest){
        if(node == null){
            return nearest;
        }
        //if this node is closer to the point we change the nearest point value.
        if(nearest.distanceSquaredTo(p) > node.key.distanceSquaredTo(p)){
            nearest = node.key;
        }
        //if cutdirection is vertical we want to compare the x values to determine which subtree to traverse
        //first. Else we want to compare the y values.
        if(node.cutDirection == VERTICAL) {
            return nearest(p, node, nearest, p.x(), node.key.x());
        }
        else{
            return nearest(p, node, nearest, p.y(), node.key.y());
        }
    }

    private Point2D nearest(Point2D p, Node node, Point2D nearest, double pCompare, double nodeCompare){
        //if the point is on the left/lower side of the rectangle we check first in that subtree.
        if (pCompare < nodeCompare) {
            //traverse the left subtree to look for a new nearest value
            if (node.left != null) {
                if (node.left.area.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
                    nearest = nearest(p, node.left, nearest);
                }
            }
            if(node.right != null) {
                //traverse the right subtree to look for a new nearest value
                if (node.right.area.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
                    nearest = nearest(p, node.right, nearest);
                }
            }
        } else {
            //same as above but here we want to start by traversing the right subtree
            if (node.right != null) {
                if (node.right.area.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
                    nearest = nearest(p, node.right, nearest);
                }
            }
            if(node.left != null){
                if (node.left.area.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
                    nearest = nearest(p, node.left, nearest);
                }
            }
        }
        return nearest;
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
    }
}
