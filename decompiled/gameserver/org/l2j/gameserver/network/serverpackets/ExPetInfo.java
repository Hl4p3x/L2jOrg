// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.enums.NpcInfoType;

public class ExPetInfo extends AbstractMaskPacket<NpcInfoType>
{
    private final Summon _summon;
    private final Player _attacker;
    private final int _val;
    private final byte[] _masks;
    private final String _title;
    private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
    private int _initSize;
    private int _blockSize;
    private int _clanCrest;
    private int _clanLargeCrest;
    private int _allyCrest;
    private int _allyId;
    private int _clanId;
    private int _statusMask;
    
    public ExPetInfo(final Summon summon, final Player attacker, final int val) {
        this._masks = new byte[] { 0, 12, 12, 0, 0 };
        this._initSize = 0;
        this._blockSize = 0;
        this._clanCrest = 0;
        this._clanLargeCrest = 0;
        this._allyCrest = 0;
        this._allyId = 0;
        this._clanId = 0;
        this._statusMask = 0;
        this._summon = summon;
        this._attacker = attacker;
        this._title = ((summon.getOwner() != null && summon.getOwner().isOnline()) ? summon.getOwner().getName() : "");
        this._val = val;
        this._abnormalVisualEffects = summon.getEffectList().getCurrentAbnormalVisualEffects();
        if (summon.getTemplate().getDisplayId() != summon.getTemplate().getId()) {
            final byte[] masks = this._masks;
            final int n = 2;
            masks[n] |= 0x10;
            this.addComponentType(NpcInfoType.NAME);
        }
        this.addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.RELATIONS, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING, NpcInfoType.PVP_FLAG);
        if (summon.getHeading() > 0) {
            this.addComponentType(NpcInfoType.HEADING);
        }
        if (summon.getStats().getPAtkSpd() > 0 || summon.getStats().getMAtkSpd() > 0) {
            this.addComponentType(NpcInfoType.ATK_CAST_SPEED);
        }
        if (summon.getRunSpeed() > 0.0) {
            this.addComponentType(NpcInfoType.SPEED_MULTIPLIER);
        }
        if (summon.getWeapon() > 0 || summon.getArmor() > 0) {
            this.addComponentType(NpcInfoType.EQUIPPED);
        }
        if (summon.getTeam() != Team.NONE) {
            this.addComponentType(NpcInfoType.TEAM);
        }
        if (summon.isInsideZone(ZoneType.WATER) || summon.isFlying()) {
            this.addComponentType(NpcInfoType.SWIM_OR_FLY);
        }
        if (summon.isFlying()) {
            this.addComponentType(NpcInfoType.FLYING);
        }
        if (summon.getMaxHp() > 0) {
            this.addComponentType(NpcInfoType.MAX_HP);
        }
        if (summon.getMaxMp() > 0) {
            this.addComponentType(NpcInfoType.MAX_MP);
        }
        if (summon.getCurrentHp() <= summon.getMaxHp()) {
            this.addComponentType(NpcInfoType.CURRENT_HP);
        }
        if (summon.getCurrentMp() <= summon.getMaxMp()) {
            this.addComponentType(NpcInfoType.CURRENT_MP);
        }
        if (!this._abnormalVisualEffects.isEmpty()) {
            this.addComponentType(NpcInfoType.ABNORMALS);
        }
        if (summon.getTemplate().getWeaponEnchant() > 0) {
            this.addComponentType(NpcInfoType.ENCHANT);
        }
        if (summon.getTransformationDisplayId() > 0) {
            this.addComponentType(NpcInfoType.TRANSFORMATION);
        }
        if (summon.isShowSummonAnimation()) {
            this.addComponentType(NpcInfoType.SUMMONED);
        }
        if (summon.getReputation() != 0) {
            this.addComponentType(NpcInfoType.REPUTATION);
        }
        if (summon.getOwner().getClan() != null) {
            this._clanId = summon.getOwner().getAppearance().getVisibleClanId();
            this._clanCrest = summon.getOwner().getAppearance().getVisibleClanCrestId();
            this._clanLargeCrest = summon.getOwner().getAppearance().getVisibleClanLargeCrestId();
            this._allyCrest = summon.getOwner().getAppearance().getVisibleAllyId();
            this._allyId = summon.getOwner().getAppearance().getVisibleAllyCrestId();
            this.addComponentType(NpcInfoType.CLAN);
        }
        this.addComponentType(NpcInfoType.COLOR_EFFECT);
        if (summon.isInCombat()) {
            this._statusMask |= 0x1;
        }
        if (summon.isDead()) {
            this._statusMask |= 0x2;
        }
        if (summon.isTargetable()) {
            this._statusMask |= 0x4;
        }
        this._statusMask |= 0x8;
        if (this._statusMask != 0) {
            this.addComponentType(NpcInfoType.VISUAL_STATE);
        }
    }
    
    @Override
    protected byte[] getMasks() {
        return this._masks;
    }
    
    @Override
    protected void onNewMaskAdded(final NpcInfoType component) {
        this.calcBlockSize(this._summon, component);
    }
    
    private void calcBlockSize(final Summon summon, final NpcInfoType type) {
        switch (type) {
            case ATTACKABLE:
            case RELATIONS: {
                this._initSize += type.getBlockLength();
                break;
            }
            case TITLE: {
                this._initSize += type.getBlockLength() + this._title.length() * 2;
                break;
            }
            case NAME: {
                this._blockSize += type.getBlockLength() + summon.getName().length() * 2;
                break;
            }
            default: {
                this._blockSize += type.getBlockLength();
                break;
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PET_INFO);
        this.writeInt(this._summon.getObjectId());
        this.writeByte((byte)this._val);
        this.writeShort((short)37);
        this.writeBytes(this._masks);
        this.writeByte((byte)this._initSize);
        if (this.containsMask(NpcInfoType.ATTACKABLE)) {
            this.writeByte((byte)(byte)(this._summon.isAutoAttackable(this._attacker) ? 1 : 0));
        }
        if (this.containsMask(NpcInfoType.RELATIONS)) {
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.TITLE)) {
            this.writeString((CharSequence)this._title);
        }
        this.writeShort((short)this._blockSize);
        if (this.containsMask(NpcInfoType.ID)) {
            this.writeInt(this._summon.getTemplate().getDisplayId() + 1000000);
        }
        if (this.containsMask(NpcInfoType.POSITION)) {
            this.writeInt(this._summon.getX());
            this.writeInt(this._summon.getY());
            this.writeInt(this._summon.getZ());
        }
        if (this.containsMask(NpcInfoType.HEADING)) {
            this.writeInt(this._summon.getHeading());
        }
        if (this.containsMask(NpcInfoType.UNKNOWN2)) {
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.ATK_CAST_SPEED)) {
            this.writeInt(this._summon.getPAtkSpd());
            this.writeInt(this._summon.getMAtkSpd());
        }
        if (this.containsMask(NpcInfoType.SPEED_MULTIPLIER)) {
            this.writeFloat((float)this._summon.getStats().getMovementSpeedMultiplier());
            this.writeFloat((float)this._summon.getStats().getAttackSpeedMultiplier());
        }
        if (this.containsMask(NpcInfoType.EQUIPPED)) {
            this.writeInt(this._summon.getWeapon());
            this.writeInt(this._summon.getArmor());
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.ALIVE)) {
            this.writeByte((byte)(byte)(this._summon.isDead() ? 0 : 1));
        }
        if (this.containsMask(NpcInfoType.RUNNING)) {
            this.writeByte((byte)(byte)(this._summon.isRunning() ? 1 : 0));
        }
        if (this.containsMask(NpcInfoType.SWIM_OR_FLY)) {
            this.writeByte((byte)(this._summon.isInsideZone(ZoneType.WATER) ? 1 : (this._summon.isFlying() ? 2 : 0)));
        }
        if (this.containsMask(NpcInfoType.TEAM)) {
            this.writeByte((byte)this._summon.getTeam().getId());
        }
        if (this.containsMask(NpcInfoType.ENCHANT)) {
            this.writeInt(this._summon.getTemplate().getWeaponEnchant());
        }
        if (this.containsMask(NpcInfoType.FLYING)) {
            this.writeInt((int)(this._summon.isFlying() ? 1 : 0));
        }
        if (this.containsMask(NpcInfoType.CLONE)) {
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.COLOR_EFFECT)) {
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.DISPLAY_EFFECT)) {
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.TRANSFORMATION)) {
            this.writeInt(this._summon.getTransformationDisplayId());
        }
        if (this.containsMask(NpcInfoType.CURRENT_HP)) {
            this.writeInt((int)this._summon.getCurrentHp());
        }
        if (this.containsMask(NpcInfoType.CURRENT_MP)) {
            this.writeInt((int)this._summon.getCurrentMp());
        }
        if (this.containsMask(NpcInfoType.MAX_HP)) {
            this.writeInt(this._summon.getMaxHp());
        }
        if (this.containsMask(NpcInfoType.MAX_MP)) {
            this.writeInt(this._summon.getMaxMp());
        }
        if (this.containsMask(NpcInfoType.SUMMONED)) {
            this.writeByte((byte)(this._summon.isShowSummonAnimation() ? 2 : 0));
        }
        if (this.containsMask(NpcInfoType.UNKNOWN12)) {
            this.writeInt(0);
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.NAME)) {
            this.writeString((CharSequence)this._summon.getName());
        }
        if (this.containsMask(NpcInfoType.NAME_NPCSTRINGID)) {
            this.writeInt(-1);
        }
        if (this.containsMask(NpcInfoType.TITLE_NPCSTRINGID)) {
            this.writeInt(-1);
        }
        if (this.containsMask(NpcInfoType.PVP_FLAG)) {
            this.writeByte(this._summon.getPvpFlag());
        }
        if (this.containsMask(NpcInfoType.REPUTATION)) {
            this.writeInt(this._summon.getReputation());
        }
        if (this.containsMask(NpcInfoType.CLAN)) {
            this.writeInt(this._clanId);
            this.writeInt(this._clanCrest);
            this.writeInt(this._clanLargeCrest);
            this.writeInt(this._allyId);
            this.writeInt(this._allyCrest);
        }
        if (this.containsMask(NpcInfoType.VISUAL_STATE)) {
            this.writeByte((byte)this._statusMask);
        }
        if (this.containsMask(NpcInfoType.ABNORMALS)) {
            this.writeShort((short)this._abnormalVisualEffects.size());
            for (final AbnormalVisualEffect abnormalVisualEffect : this._abnormalVisualEffects) {
                this.writeShort((short)abnormalVisualEffect.getClientId());
            }
        }
    }
}
