// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.ensoul;

import org.l2j.gameserver.model.holders.SkillHolder;

public class EnsoulOption extends SkillHolder
{
    private final int _id;
    private final String _name;
    private final String _desc;
    
    public EnsoulOption(final int id, final String name, final String desc, final int skillId, final int skillLevel) {
        super(skillId, skillLevel);
        this._id = id;
        this._name = name;
        this._desc = desc;
    }
    
    public int getId() {
        return this._id;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getDesc() {
        return this._desc;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this._id, this._name, this._desc);
    }
}
