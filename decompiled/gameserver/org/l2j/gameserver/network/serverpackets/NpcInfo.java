// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;
import java.util.Iterator;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.actor.instance.Guard;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import java.util.Set;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.enums.NpcInfoType;

public class NpcInfo extends AbstractMaskPacket<NpcInfoType>
{
    private final Npc _npc;
    private final byte[] _masks;
    private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
    private int _initSize;
    private int _blockSize;
    private int _clanCrest;
    private int _clanLargeCrest;
    private int _allyCrest;
    private int _allyId;
    private int _clanId;
    private int _statusMask;
    
    public NpcInfo(final Npc npc) {
        this._masks = new byte[] { 0, 12, 12, 0, 0 };
        this._initSize = 0;
        this._blockSize = 0;
        this._clanCrest = 0;
        this._clanLargeCrest = 0;
        this._allyCrest = 0;
        this._allyId = 0;
        this._clanId = 0;
        this._statusMask = 0;
        this._npc = npc;
        this._abnormalVisualEffects = npc.getEffectList().getCurrentAbnormalVisualEffects();
        this.addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.RELATIONS, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING);
        if (npc.getHeading() > 0) {
            this.addComponentType(NpcInfoType.HEADING);
        }
        if (npc.getStats().getPAtkSpd() > 0 || npc.getStats().getMAtkSpd() > 0) {
            this.addComponentType(NpcInfoType.ATK_CAST_SPEED);
        }
        if (npc.getRunSpeed() > 0.0) {
            this.addComponentType(NpcInfoType.SPEED_MULTIPLIER);
        }
        if (npc.getLeftHandItem() > 0 || npc.getRightHandItem() > 0) {
            this.addComponentType(NpcInfoType.EQUIPPED);
        }
        if (npc.getTeam() != Team.NONE) {
            this.addComponentType(NpcInfoType.TEAM);
        }
        if (npc.getDisplayEffect() > 0) {
            this.addComponentType(NpcInfoType.DISPLAY_EFFECT);
        }
        if (npc.isInsideZone(ZoneType.WATER) || npc.isFlying()) {
            this.addComponentType(NpcInfoType.SWIM_OR_FLY);
        }
        if (npc.isFlying()) {
            this.addComponentType(NpcInfoType.FLYING);
        }
        if (npc.getCloneObjId() > 0) {
            this.addComponentType(NpcInfoType.CLONE);
        }
        if (npc.getMaxHp() > 0) {
            this.addComponentType(NpcInfoType.MAX_HP);
        }
        if (npc.getMaxMp() > 0) {
            this.addComponentType(NpcInfoType.MAX_MP);
        }
        if (npc.getCurrentHp() <= npc.getMaxHp()) {
            this.addComponentType(NpcInfoType.CURRENT_HP);
        }
        if (npc.getCurrentMp() <= npc.getMaxMp()) {
            this.addComponentType(NpcInfoType.CURRENT_MP);
        }
        if (npc.getTemplate().isUsingServerSideName()) {
            this.addComponentType(NpcInfoType.NAME);
        }
        if (npc.getTemplate().isUsingServerSideTitle() || (Config.SHOW_NPC_LVL && GameUtils.isMonster(npc)) || npc.isChampion() || GameUtils.isTrap(npc)) {
            this.addComponentType(NpcInfoType.TITLE);
        }
        if (npc.getNameString() != null) {
            this.addComponentType(NpcInfoType.NAME_NPCSTRINGID);
        }
        if (npc.getTitleString() != null) {
            this.addComponentType(NpcInfoType.TITLE_NPCSTRINGID);
        }
        if (this._npc.getReputation() != 0) {
            this.addComponentType(NpcInfoType.REPUTATION);
        }
        if (!this._abnormalVisualEffects.isEmpty() || npc.isInvisible()) {
            this.addComponentType(NpcInfoType.ABNORMALS);
        }
        if (npc.getEnchantEffect() > 0) {
            this.addComponentType(NpcInfoType.ENCHANT);
        }
        if (npc.getTransformationDisplayId() > 0) {
            this.addComponentType(NpcInfoType.TRANSFORMATION);
        }
        if (npc.isShowSummonAnimation()) {
            this.addComponentType(NpcInfoType.SUMMONED);
        }
        if (npc.getClanId() > 0) {
            final Clan clan = ClanTable.getInstance().getClan(npc.getClanId());
            if (clan != null) {
                this._clanId = clan.getId();
                this._clanCrest = clan.getCrestId();
                this._clanLargeCrest = clan.getCrestLargeId();
                this._allyCrest = clan.getAllyCrestId();
                this._allyId = clan.getAllyId();
                this.addComponentType(NpcInfoType.CLAN);
            }
        }
        this.addComponentType(NpcInfoType.COLOR_EFFECT);
        if (npc.getPvpFlag() > 0) {
            this.addComponentType(NpcInfoType.PVP_FLAG);
        }
        if (npc.isInCombat()) {
            this._statusMask |= 0x1;
        }
        if (npc.isDead()) {
            this._statusMask |= 0x2;
        }
        if (npc.isTargetable()) {
            this._statusMask |= 0x4;
        }
        if (npc.isShowName()) {
            this._statusMask |= 0x8;
        }
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
        this.calcBlockSize(this._npc, component);
    }
    
    private void calcBlockSize(final Npc npc, final NpcInfoType type) {
        switch (type) {
            case ATTACKABLE:
            case RELATIONS: {
                this._initSize += type.getBlockLength();
                break;
            }
            case TITLE: {
                this._initSize += type.getBlockLength() + npc.getTitle().length() * 2;
                break;
            }
            case NAME: {
                this._blockSize += type.getBlockLength() + npc.getName().length() * 2;
                break;
            }
            default: {
                this._blockSize += type.getBlockLength();
                break;
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.NPC_INFO);
        this.writeInt(this._npc.getObjectId());
        this.writeByte(this._npc.isShowSummonAnimation() ? 2 : 0);
        this.writeShort(37);
        this.writeBytes(this._masks);
        this.writeByte(this._initSize);
        if (this.containsMask(NpcInfoType.ATTACKABLE)) {
            this.writeByte(GameUtils.isAttackable(this._npc) && !(this._npc instanceof Guard));
        }
        if (this.containsMask(NpcInfoType.RELATIONS)) {
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.TITLE)) {
            this.writeString((CharSequence)this._npc.getTitle());
        }
        this.writeShort(this._blockSize);
        if (this.containsMask(NpcInfoType.ID)) {
            this.writeInt(this._npc.getTemplate().getDisplayId() + 1000000);
        }
        if (this.containsMask(NpcInfoType.POSITION)) {
            this.writeInt(this._npc.getX());
            this.writeInt(this._npc.getY());
            this.writeInt(this._npc.getZ());
        }
        if (this.containsMask(NpcInfoType.HEADING)) {
            this.writeInt(this._npc.getHeading());
        }
        if (this.containsMask(NpcInfoType.UNKNOWN2)) {
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.ATK_CAST_SPEED)) {
            this.writeInt(this._npc.getPAtkSpd());
            this.writeInt(this._npc.getMAtkSpd());
        }
        if (this.containsMask(NpcInfoType.SPEED_MULTIPLIER)) {
            this.writeFloat((float)this._npc.getStats().getMovementSpeedMultiplier());
            this.writeFloat((float)this._npc.getStats().getAttackSpeedMultiplier());
        }
        if (this.containsMask(NpcInfoType.EQUIPPED)) {
            this.writeInt(this._npc.getRightHandItem());
            this.writeInt(0);
            this.writeInt(this._npc.getLeftHandItem());
        }
        if (this.containsMask(NpcInfoType.ALIVE)) {
            this.writeByte(!this._npc.isDead());
        }
        if (this.containsMask(NpcInfoType.RUNNING)) {
            this.writeByte(this._npc.isRunning());
        }
        if (this.containsMask(NpcInfoType.SWIM_OR_FLY)) {
            this.writeByte((byte)(this._npc.isInsideZone(ZoneType.WATER) ? 1 : (this._npc.isFlying() ? 2 : 0)));
        }
        if (this.containsMask(NpcInfoType.TEAM)) {
            this.writeByte((byte)this._npc.getTeam().getId());
        }
        if (this.containsMask(NpcInfoType.ENCHANT)) {
            this.writeInt(this._npc.getEnchantEffect());
        }
        if (this.containsMask(NpcInfoType.FLYING)) {
            this.writeInt(this._npc.isFlying());
        }
        if (this.containsMask(NpcInfoType.CLONE)) {
            this.writeInt(this._npc.getCloneObjId());
        }
        if (this.containsMask(NpcInfoType.COLOR_EFFECT)) {
            this.writeInt(this._npc.getColorEffect());
        }
        if (this.containsMask(NpcInfoType.DISPLAY_EFFECT)) {
            this.writeInt(this._npc.getDisplayEffect());
        }
        if (this.containsMask(NpcInfoType.TRANSFORMATION)) {
            this.writeInt(this._npc.getTransformationDisplayId());
        }
        if (this.containsMask(NpcInfoType.CURRENT_HP)) {
            this.writeInt((int)this._npc.getCurrentHp());
        }
        if (this.containsMask(NpcInfoType.CURRENT_MP)) {
            this.writeInt((int)this._npc.getCurrentMp());
        }
        if (this.containsMask(NpcInfoType.MAX_HP)) {
            this.writeInt(this._npc.getMaxHp());
        }
        if (this.containsMask(NpcInfoType.MAX_MP)) {
            this.writeInt(this._npc.getMaxMp());
        }
        if (this.containsMask(NpcInfoType.SUMMONED)) {
            this.writeByte((byte)0);
        }
        if (this.containsMask(NpcInfoType.UNKNOWN12)) {
            this.writeInt(0);
            this.writeInt(0);
        }
        if (this.containsMask(NpcInfoType.NAME)) {
            this.writeString((CharSequence)this._npc.getName());
        }
        if (this.containsMask(NpcInfoType.NAME_NPCSTRINGID)) {
            final NpcStringId nameString = this._npc.getNameString();
            this.writeInt((nameString != null) ? nameString.getId() : -1);
        }
        if (this.containsMask(NpcInfoType.TITLE_NPCSTRINGID)) {
            final NpcStringId titleString = this._npc.getTitleString();
            this.writeInt((titleString != null) ? titleString.getId() : -1);
        }
        if (this.containsMask(NpcInfoType.PVP_FLAG)) {
            this.writeByte(this._npc.getPvpFlag());
        }
        if (this.containsMask(NpcInfoType.REPUTATION)) {
            this.writeInt(this._npc.getReputation());
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
            this.writeShort((short)(this._abnormalVisualEffects.size() + (this._npc.isInvisible() ? 1 : 0)));
            for (final AbnormalVisualEffect abnormalVisualEffect : this._abnormalVisualEffects) {
                this.writeShort((short)abnormalVisualEffect.getClientId());
            }
            if (this._npc.isInvisible()) {
                this.writeShort((short)AbnormalVisualEffect.STEALTH.getClientId());
            }
        }
    }
}
