package com.witek.model;//import require classes and packages

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

//Extends JPanel class
public class PlotExample extends JPanel{
    //initialize coordinates
    int[] cord = {0, 20, 40, 80};
    int marg = 60;

    protected void paintComponent(Graphics grf){
        //create instance of the Graphics to use its methods
        super.paintComponent(grf);
        Graphics2D graph = (Graphics2D)grf;

        //Sets the value of a single preference for the rendering algorithms.
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // get width and height
        int width = getWidth();
        int height = getHeight();

        // draw graph
        graph.draw(new Line2D.Double(marg, marg, marg, height-marg));
        graph.draw(new Line2D.Double(marg, height-marg, width-marg, height-marg));


        //find value of x and scale to plot points
        double x = (double)(width-2*marg)/(cord.length-1);
        double scale = (double)(height-2*marg)/getMax();

        //set color for points
        graph.setPaint(Color.RED);
        graph.fill(new Line2D.Double(marg+10, height-marg+10, width-marg+10, height-marg+10));
        // set points to the graph
        double[] a = {8.475717856825038E13, 5.593991506520131, 0.005939691263728734, 1.1167680717528854E13, 5.6495493230570855, 0.0039882867313520125, 4.1259538749237125E13, 6.855039557958428, 0.0032064095697255574, 0.3184598580440302, 0.0013190395042192375, 0.07723525280393695, 1.5112535662070306, 52.82416093144424, -0.21582893035330727};
        //double[] a = {169.60999119769016, 0.20799905778290798, 0.12199995345715572, 4517.018719099791, 0.33503497021342193, 11279.313854332704, 0.533861040653837};
        for(double i=0.001; i<1; i = i+0.001){
            double x1 = (i*100+60);
            double y1 = Function.funkcja_lab2(i,1,850,a);
            graph.fill(new Ellipse2D.Double(x1, y1, 4, 4));
            //graph.fill(new Rectangle2D.Double(x1, y1, 10, 10));
        }
    }

    //create getMax() method to find maximum value
    private int getMax(){
        int max = -Integer.MAX_VALUE;
        for(int i=0; i<cord.length; i++){
            if(cord[i]>max)
                max = cord[i];

        }
        return max;
    }

    //main() method start
    public static void main(String args[]){
        //create an instance of JFrame class
        JFrame frame = new JFrame();
        // set size, layout and location for frame.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new PlotExample());
        frame.setSize(400, 400);
        frame.setLocation(200, 200);
        frame.setVisible(true);
    }
}