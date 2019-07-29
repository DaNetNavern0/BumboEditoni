package me.danetnaverno.editoni.editor.operations;

public class BlankOperation extends Operation
{
    @Override
    public void apply()
    {
    }

    @Override
    public void rollback()
    {
    }

    @Override
    public String getDisplayName()
    {
        return "Loaded";
    }
}
