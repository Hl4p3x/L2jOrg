// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.model;

public class ActionData
{
    private final int id;
    private final String handler;
    private final int optionId;
    private final boolean autoUse;
    
    public ActionData(final Integer id, final String handler, final Integer optionId, final boolean autoUse) {
        this.id = id;
        this.handler = handler;
        this.optionId = optionId;
        this.autoUse = autoUse;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getHandler() {
        return this.handler;
    }
    
    public int getOptionId() {
        return this.optionId;
    }
    
    public boolean isAutoUse() {
        return this.autoUse;
    }
}
