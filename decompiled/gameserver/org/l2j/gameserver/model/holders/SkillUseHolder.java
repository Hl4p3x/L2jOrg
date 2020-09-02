// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.item.instance.Item;

public class SkillUseHolder extends SkillHolder
{
    private final Item _item;
    private final boolean _ctrlPressed;
    private final boolean _shiftPressed;
    
    public SkillUseHolder(final Skill skill, final Item item, final boolean ctrlPressed, final boolean shiftPressed) {
        super(skill);
        this._item = item;
        this._ctrlPressed = ctrlPressed;
        this._shiftPressed = shiftPressed;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    public boolean isCtrlPressed() {
        return this._ctrlPressed;
    }
    
    public boolean isShiftPressed() {
        return this._shiftPressed;
    }
}
