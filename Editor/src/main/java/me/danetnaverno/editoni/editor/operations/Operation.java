package me.danetnaverno.editoni.editor.operations;

import org.jetbrains.annotations.NotNull;

public abstract class Operation
{
    public abstract void apply();

    public abstract void rollback();

    @NotNull
    public abstract String getDisplayName();
}
