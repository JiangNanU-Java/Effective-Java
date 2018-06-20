package com.testone;

import javax.swing.*;

/**
 * 创建V的网络图
 */
public class NetworkFrame extends JFrame implements FrameSize {
    private NetCanves canves;

    public NetworkFrame(int i) {
        canves = new NetCanves(i);
        this.setLocation(100, 100);
        this.setSize(FrameSize.WIDTH + 2 * FrameSize.Origin_X, FrameSize.HEIGHT + 2 * FrameSize.Origin_Y);
        this.add(canves);
    }
}
