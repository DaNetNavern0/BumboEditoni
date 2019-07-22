package me.danetnaverno.editoni.common.renderer;

import javafx.util.Pair;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;

public class Renderer
{
    //*******************
    //todo optimize with glDrawArray or something. This is just a prototype.
    //A working prototype nonetheless
    //*******************

    private static Collection<Pair<Integer,float[]>> faces = new ArrayList<>();

    public static synchronized void addObject(int texture, float[] facez)
    {
        faces.add(new Pair<>(texture,facez));
    }

    public static void draw()
    {
        for (Pair<Integer, float[]> entry : faces)
        {
            float[] face = entry.getValue();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, entry.getKey());
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex3f(face[0], face[1], face[2]);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex3f(face[3], face[4], face[5]);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex3f(face[6], face[7], face[8]);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex3f(face[9], face[10], face[11]);
            GL11.glEnd();
        }
        faces.clear();
    }
}