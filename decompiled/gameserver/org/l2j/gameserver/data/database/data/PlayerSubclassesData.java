// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("character_subclasses")
public class PlayerSubclassesData
{
    private long exp;
    private long sp;
    private int level;
    @Column("vitality_points")
    private int vitalityPoints;
    @Column("class_index")
    private int classIndex;
    @Column("dual_class")
    private int dualClass;
    
    public long getExp() {
        return this.exp;
    }
    
    public long getSp() {
        return this.sp;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getVitalityPoints() {
        return this.vitalityPoints;
    }
    
    public int getClassIndex() {
        return this.classIndex;
    }
    
    public int getDualClass() {
        return this.dualClass;
    }
}
