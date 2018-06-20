package com.testone;

import java.awt.*;
import java.util.ArrayList;

/**
 * 绘制网络节点图
 */
public class NetCanves extends Canvas implements FrameSize{

    private int[][] flagset;
    private static Vertex[] vset=CalculationThread.getVset();
    private static ArrayList<int[][]> arrayList=CalculationThread.getArrayList();

    public NetCanves(int i) {
        super();
        flagset=arrayList.get(i);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2D = (Graphics2D) g;
        Color r = new Color(200, 70, 0);
        Color green = new Color(20, 200, 60);
        g.setColor(r);
        //绘图提示-消除锯齿
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画坐标轴
        g2D.setStroke(new BasicStroke(Float.parseFloat("2.0F")));// 轴线粗度


        // 上边
        g.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y);
        // 下边
        g.drawLine(YAxis_X, YAxis_Y, XAxis_X, YAxis_Y);
        // 左边
        g.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y);
        // 右边
        g.drawLine(XAxis_X, XAxis_Y, XAxis_X, YAxis_Y);


        g.setColor(green);
        //绘制随机的节点图
        for (int i = 0; i < vset.length; i++) {
            g.fillRect(vset[i].getX(), vset[i].getY(), 5, 5);
        }

        g.setColor(r);
        for (int i = 0; i < flagset.length; i+=4) {
            for (int j = 0; j < flagset[0].length; j+=4) {
                if (flagset[i][j] != 0) {
                    g.drawRect(i+100, j+100, 4, 4);
                }
            }
        }

        g.setColor(green);
        //绘制随机的节点图
        for (int i = 0; i < vset.length; i++) {
            g.fillRect(vset[i].getX(), vset[i].getY(), 5, 5);
        }

    }
}
