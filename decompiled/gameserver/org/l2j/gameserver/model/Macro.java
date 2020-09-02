// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.List;
import org.l2j.gameserver.model.interfaces.INamable;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class Macro implements IIdentifiable, INamable
{
    private final int _icon;
    private final String _name;
    private final String _descr;
    private final String _acronym;
    private final List<MacroCmd> _commands;
    private int _id;
    
    public Macro(final int id, final int icon, final String name, final String descr, final String acronym, final List<MacroCmd> list) {
        this._id = id;
        this._icon = icon;
        this._name = name;
        this._descr = descr;
        this._acronym = acronym;
        this._commands = list;
    }
    
    @Override
    public int getId() {
        return this._id;
    }
    
    public void setId(final int id) {
        this._id = id;
    }
    
    public int getIcon() {
        return this._icon;
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public String getDescr() {
        return this._descr;
    }
    
    public String getAcronym() {
        return this._acronym;
    }
    
    public List<MacroCmd> getCommands() {
        return this._commands;
    }
}
