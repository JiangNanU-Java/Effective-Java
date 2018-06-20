package com.testone;

import java.awt.*;

/**
 * 绘制网络节点图
 */
public class NetCanves extends Canvas implements FrameSize{

    private CalculationThread obj;
    private Vertex[] vset;
    private int[][] matrix;

    public NetCanves(CalculationThread obj) {
        super();
        this.obj=obj;
        this.vset = obj.getVset();
        this.matrix = obj.getMatrix().getA_matrix();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2D = (Graphics2D) g;
        Color c = new Color(200, 70, 0);
        g.setColor(c);
        //绘图提示-消除锯齿
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画坐标轴
        g2D.setStroke(new BasicStroke(Float.parseFloat("2.0F")));// 轴线粗度

        g.drawString("n r:"+obj.getN()+"  "+obj.getR(),Origin_X,Origin_Y-30);

        // 上边
        g.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y);
        // 下边
        g.drawLine(YAxis_X, YAxis_Y, XAxis_X, YAxis_Y);
        // 左边
        g.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y);
        // 右边
        g.drawLine(XAxis_X, XAxis_Y, XAxis_X, YAxis_Y);

        //绘制随机的节点图
        for (int i = 0; i < vset.length; i++) {
            g.fillRect(vset[i].getX(), vset[i].getY(), 5, 5);
        }

        //连线
        Color edge = new Color(20, 200, 60);
        g.setColor(c);
        g2D.setStroke(new BasicStroke(Float.parseFloat("1.0F")));

        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                if (matrix[i][j] != 0) {
                    g.drawLine(vset[i].getX(), vset[i].getY(), vset[j].getX(), vset[j].getY());
                }
            }
        }
    }
}
