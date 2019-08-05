package me.danetnaverno.editoni.editor.operations;

import me.danetnaverno.editoni.util.Translation;

public class BlankOperation extends SaveOperation
{
    @Override
    public void apply()
    {
    }

    @Override
    public String getDisplayName()
    {
        return Translation.INSTANCE.translate("operation.loaded");
    }
}
