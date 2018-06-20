package com.testone;

import javax.swing.*;

public class DataFrame extends JFrame implements FrameSize{
    private DataCanves canves;

    public DataFrame(){
        canves=new DataCanves();
        this.setLocation(1000,100);
        this.setSize(FrameSize.WIDTH + 2 * FrameSize.Origin_X, FrameSize.HEIGHT + 2 * FrameSize.Origin_Y);
        this.add(canves);
    }
}
