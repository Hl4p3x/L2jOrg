// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;

@Table("character_summons")
public class SummonData
{
    private int ownerId;
    private int summonId;
    private int summonSkillId;
    private int curHp;
    private int curMp;
    private int time;
    
    public int getOwnerId() {
        return this.ownerId;
    }
    
    public int getSummonId() {
        return this.summonId;
    }
    
    public int getSummonSkillId() {
        return this.summonSkillId;
    }
    
    public int getCurHp() {
        return this.curHp;
    }
    
    public int getCurMp() {
        return this.curMp;
    }
    
    public int getTime() {
        return this.time;
    }
}
