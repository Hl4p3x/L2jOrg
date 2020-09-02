// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.ToIntFunction;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.actor.instance.Player;

public class ArmorsetSkillHolder extends SkillHolder
{
    private final int _minimumPieces;
    private final int _minEnchant;
    private final int _artifactSlotMask;
    private final int _artifactBookSlot;
    private final boolean _isOptional;
    
    public ArmorsetSkillHolder(final int skillId, final int skillLvl, final int minimumPieces, final int minEnchant, final boolean isOptional, final int artifactSlotMask, final int artifactBookSlot) {
        super(skillId, skillLvl);
        this._minimumPieces = minimumPieces;
        this._minEnchant = minEnchant;
        this._isOptional = isOptional;
        this._artifactSlotMask = artifactSlotMask;
        this._artifactBookSlot = artifactBookSlot;
    }
    
    public int getMinimumPieces() {
        return this._minimumPieces;
    }
    
    public int getMinEnchant() {
        return this._minEnchant;
    }
    
    public boolean isOptional() {
        return this._isOptional;
    }
    
    public boolean validateConditions(final Player player, final ArmorSet armorSet, final ToIntFunction<Item> idProvider) {
        return this._artifactSlotMask <= armorSet.getArtifactSlotMask(player, this._artifactBookSlot) && this._minimumPieces <= armorSet.getPiecesCount(player, idProvider) && this._minEnchant <= armorSet.getLowestSetEnchant(player) && (!this._isOptional || armorSet.hasOptionalEquipped(player, idProvider)) && player.getSkillLevel(this.getSkillId()) != this.getLevel();
    }
}
