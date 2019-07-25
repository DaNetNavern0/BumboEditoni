package me.danetnaverno.editoni.editor.control;

import lwjgui.scene.Context;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.Labeled;
import me.danetnaverno.editoni.editor.Editor;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

public class DynamicLabel extends Label
{
    private static Field textField;

    private Callable<String> callable;
    private int frequency;

    private long nextUpdate = System.currentTimeMillis();

    static
    {
        try
        {
            textField = Labeled.class.getDeclaredField("text");
            textField.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            Editor.INSTANCE.getLogger().error("Failed to access field 'text'", e);
        }
    }

    public DynamicLabel(int frequency, Callable<String> callable)
    {
        this.frequency = frequency;
        this.callable = callable;
    }

    @Override
    public void render(Context context)
    {
        try
        {
            if (System.currentTimeMillis() > nextUpdate)
            {
                textField.set(this, callable.call());
                nextUpdate = System.currentTimeMillis() + frequency;
            }
        }
        catch (Exception e)
        {
            Editor.INSTANCE.getLogger().error("Failed to process dynamic text", e);
        }
        super.render(context);
    }
}
