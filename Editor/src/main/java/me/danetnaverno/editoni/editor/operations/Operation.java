package me.danetnaverno.editoni.editor.operations;

public abstract class Operation
{
    public abstract void apply();

    public abstract void rollback();
}
