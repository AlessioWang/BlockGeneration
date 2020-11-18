package Block;

import processing.core.PApplet;
import wblut.processing.WB_Render;

/**
 * @auther Alessio
 * @date 2020/10/27
 **/
public interface Display {
    public void display(WB_Render wb_render);
    public void display(PApplet applet);
}
