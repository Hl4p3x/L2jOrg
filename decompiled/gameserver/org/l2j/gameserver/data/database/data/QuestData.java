// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

public class QuestData
{
    @Column("charId")
    private int playerId;
    private String name;
    private String var;
    private String value;
    @Column("class_index")
    private int classIndex;
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getVar() {
        return this.var;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public int getClassIndex() {
        return this.classIndex;
    }
}
