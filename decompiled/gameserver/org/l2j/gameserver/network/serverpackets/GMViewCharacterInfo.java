// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class GMViewCharacterInfo extends ServerPacket
{
    private final Player _activeChar;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;
    
    public GMViewCharacterInfo(final Player cha) {
        this._activeChar = cha;
        this._moveMultiplier = cha.getMovementSpeedMultiplier();
        this._runSpd = (int)Math.round(cha.getRunSpeed() / this._moveMultiplier);
        this._walkSpd = (int)Math.round(cha.getWalkSpeed() / this._moveMultiplier);
        this._swimRunSpd = (int)Math.round(cha.getSwimRunSpeed() / this._moveMultiplier);
        this._swimWalkSpd = (int)Math.round(cha.getSwimWalkSpeed() / this._moveMultiplier);
        this._flyRunSpd = (cha.isFlying() ? this._runSpd : 0);
        this._flyWalkSpd = (cha.isFlying() ? this._walkSpd : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.GM_VIEW_CHARACTER_INFO);
        this.writeInt(this._activeChar.getX());
        this.writeInt(this._activeChar.getY());
        this.writeInt(this._activeChar.getZ());
        this.writeInt(this._activeChar.getHeading());
        this.writeInt(this._activeChar.getObjectId());
        this.writeString((CharSequence)this._activeChar.getName());
        this.writeInt(this._activeChar.getRace().ordinal());
        this.writeInt((int)(this._activeChar.getAppearance().isFemale() ? 1 : 0));
        this.writeInt(this._activeChar.getClassId().getId());
        this.writeInt(this._activeChar.getLevel());
        this.writeLong(this._activeChar.getExp());
        this.writeDouble((double)((this._activeChar.getExp() - LevelData.getInstance().getExpForLevel(this._activeChar.getLevel())) / (float)(LevelData.getInstance().getExpForLevel(this._activeChar.getLevel() + 1) - LevelData.getInstance().getExpForLevel(this._activeChar.getLevel()))));
        this.writeInt(this._activeChar.getSTR());
        this.writeInt(this._activeChar.getDEX());
        this.writeInt(this._activeChar.getCON());
        this.writeInt(this._activeChar.getINT());
        this.writeInt(this._activeChar.getWIT());
        this.writeInt(this._activeChar.getMEN());
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(this._activeChar.getMaxHp());
        this.writeInt((int)this._activeChar.getCurrentHp());
        this.writeInt(this._activeChar.getMaxMp());
        this.writeInt((int)this._activeChar.getCurrentMp());
        this.writeLong(this._activeChar.getSp());
        this.writeInt(this._activeChar.getCurrentLoad());
        this.writeInt(this._activeChar.getMaxLoad());
        this.writeInt(this._activeChar.getPkKills());
        for (final InventorySlot slot : this.getPaperdollOrder()) {
            this.writeInt(this._activeChar.getInventory().getPaperdollObjectId(slot));
        }
        for (final InventorySlot slot : this.getPaperdollOrder()) {
            this.writeInt(this._activeChar.getInventory().getPaperdollItemDisplayId(slot));
        }
        for (final InventorySlot slot : this.getPaperdollOrder()) {
            final VariationInstance augment = this._activeChar.getInventory().getPaperdollAugmentation(slot);
            this.writeInt((augment != null) ? augment.getOption1Id() : 0);
            this.writeInt((augment != null) ? augment.getOption2Id() : 0);
        }
        this.writeByte(this._activeChar.getInventory().getTalismanSlots());
        this.writeByte(this._activeChar.getInventory().canEquipCloak());
        this.writeInt(0);
        this.writeShort(0);
        this.writeInt(this._activeChar.getPAtk());
        this.writeInt(this._activeChar.getPAtkSpd());
        this.writeInt(this._activeChar.getPDef());
        this.writeInt(this._activeChar.getEvasionRate());
        this.writeInt(this._activeChar.getAccuracy());
        this.writeInt(this._activeChar.getCriticalHit());
        this.writeInt(this._activeChar.getMAtk());
        this.writeInt(this._activeChar.getMAtkSpd());
        this.writeInt(this._activeChar.getPAtkSpd());
        this.writeInt(this._activeChar.getMDef());
        this.writeInt(this._activeChar.getMagicEvasionRate());
        this.writeInt(this._activeChar.getMagicAccuracy());
        this.writeInt(this._activeChar.getMCriticalHit());
        this.writeInt((int)this._activeChar.getPvpFlag());
        this.writeInt(this._activeChar.getReputation());
        this.writeInt(this._runSpd);
        this.writeInt(this._walkSpd);
        this.writeInt(this._swimRunSpd);
        this.writeInt(this._swimWalkSpd);
        this.writeInt(this._flyRunSpd);
        this.writeInt(this._flyWalkSpd);
        this.writeInt(this._flyRunSpd);
        this.writeInt(this._flyWalkSpd);
        this.writeDouble(this._moveMultiplier);
        this.writeDouble(this._activeChar.getAttackSpeedMultiplier());
        this.writeDouble(this._activeChar.getCollisionRadius());
        this.writeDouble(this._activeChar.getCollisionHeight());
        this.writeInt((int)this._activeChar.getAppearance().getHairStyle());
        this.writeInt((int)this._activeChar.getAppearance().getHairColor());
        this.writeInt((int)this._activeChar.getAppearance().getFace());
        this.writeInt((int)(this._activeChar.isGM() ? 1 : 0));
        this.writeString((CharSequence)this._activeChar.getTitle());
        this.writeInt(this._activeChar.getClanId());
        this.writeInt(this._activeChar.getClanCrestId());
        this.writeInt(this._activeChar.getAllyId());
        this.writeByte((byte)this._activeChar.getMountType().ordinal());
        this.writeByte((byte)this._activeChar.getPrivateStoreType().getId());
        this.writeByte((byte)(byte)(this._activeChar.hasDwarvenCraft() ? 1 : 0));
        this.writeInt(this._activeChar.getPkKills());
        this.writeInt(this._activeChar.getPvpKills());
        this.writeShort((short)this._activeChar.getRecomLeft());
        this.writeShort((short)this._activeChar.getRecomHave());
        this.writeInt(this._activeChar.getClassId().getId());
        this.writeInt(0);
        this.writeInt(this._activeChar.getMaxCp());
        this.writeInt((int)this._activeChar.getCurrentCp());
        this.writeByte((byte)(byte)(this._activeChar.isRunning() ? 1 : 0));
        this.writeByte((byte)65);
        this.writeInt(this._activeChar.getPledgeClass());
        this.writeByte((byte)(byte)(this._activeChar.isNoble() ? 1 : 0));
        this.writeByte((byte)(byte)(this._activeChar.isHero() ? 1 : 0));
        this.writeInt(this._activeChar.getAppearance().getNameColor());
        this.writeInt(this._activeChar.getAppearance().getTitleColor());
        final AttributeType attackAttribute = this._activeChar.getAttackElement();
        this.writeShort((short)attackAttribute.getClientId());
        this.writeShort((short)this._activeChar.getAttackElementValue(attackAttribute));
        for (final AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            this.writeShort((short)this._activeChar.getDefenseElementValue(type));
        }
        this.writeInt(this._activeChar.getFame());
        this.writeInt(this._activeChar.getVitalityPoints());
        this.writeInt(0);
        this.writeInt(0);
    }
}
