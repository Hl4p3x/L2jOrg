// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import java.util.Set;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2j.gameserver.model.WorldObject;
import java.util.Objects;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.InventorySlot;

public class ExCharInfo extends ServerPacket
{
    private static final InventorySlot[] PAPERDOLL_ORDER;
    private final Player player;
    private int dynamicSize;
    
    public ExCharInfo(final Player player) {
        this.player = player;
        this.dynamicSize += player.getAppearance().getVisibleTitle().length() * 2;
        this.dynamicSize += player.getCubics().size() * 2;
        this.dynamicSize += player.getEffectList().getCurrentAbnormalVisualEffects().size() * 2;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHAR_INFO);
        this.writeShort(337 + this.dynamicSize);
        final PlayerAppearance appearence = this.player.getAppearance();
        this.writeInt(this.player.getObjectId());
        this.writeShort(this.player.getRace().ordinal());
        this.writeByte(appearence.isFemale());
        this.writeInt(this.player.getBaseClass());
        this.writeShort(50);
        for (final InventorySlot slot : ExCharInfo.PAPERDOLL_ORDER) {
            this.writeInt(this.player.getInventory().getPaperdollItemDisplayId(slot));
        }
        this.writeShort(26);
        for (final InventorySlot slot : this.getPaperdollOrderAugument()) {
            final VariationInstance augment = this.player.getInventory().getPaperdollAugmentation(slot);
            this.writeInt(Util.zeroIfNullOrElse((Object)augment, VariationInstance::getOption1Id));
        }
        for (final InventorySlot slot : this.getPaperdollOrderAugument()) {
            final VariationInstance augment = this.player.getInventory().getPaperdollAugmentation(slot);
            this.writeInt(Util.zeroIfNullOrElse((Object)augment, VariationInstance::getOption2Id));
        }
        this.writeByte(this.player.getInventory().getArmorMaxEnchant());
        this.writeShort(38);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeByte(this.player.getPvpFlag());
        this.writeInt(this.player.getReputation());
        this.writeInt(this.player.getMAtkSpd());
        this.writeInt(this.player.getPAtkSpd());
        this.writeInt((int)this.player.getRunSpeed());
        this.writeInt((int)this.player.getWalkSpeed());
        this.writeInt((int)this.player.getSwimRunSpeed());
        this.writeInt((int)this.player.getSwimWalkSpeed());
        this.writeInt(0);
        this.writeInt(0);
        final int flySpeed = this.player.isFlying() ? ((int)this.player.getRunSpeed()) : 0;
        this.writeInt(flySpeed);
        this.writeInt(flySpeed);
        this.writeFloat((float)this.player.getMovementSpeedMultiplier());
        this.writeFloat((float)this.player.getAttackSpeedMultiplier());
        this.writeFloat((float)this.player.getCollisionRadius());
        this.writeFloat((float)this.player.getCollisionHeight());
        this.writeInt(this.player.getVisualHair());
        this.writeInt(this.player.getVisualHairColor());
        this.writeInt(this.player.getVisualFace());
        this.writeSizedString((CharSequence)appearence.getVisibleTitle());
        this.writeInt(appearence.getVisibleClanId());
        this.writeInt(appearence.getVisibleClanCrestId());
        this.writeInt(appearence.getVisibleAllyId());
        this.writeInt(appearence.getVisibleAllyCrestId());
        this.writeByte(!this.player.isSitting());
        this.writeByte(this.player.isRunning());
        this.writeByte(this.player.isInCombat());
        this.writeByte(!this.player.isInOlympiadMode() && this.player.isAlikeDead());
        this.writeByte(this.player.getMountType().ordinal());
        this.writeByte(this.player.getPrivateStoreType().getId());
        this.writeInt(this.player.getCubics().size());
        this.player.getCubics().keySet().forEach(x$0 -> this.writeShort(x$0));
        this.writeByte(this.player.isInMatchingRoom());
        this.writeByte(this.player.isInsideZone(ZoneType.WATER) ? 1 : (this.player.isFlyingMounted() ? 2 : 0));
        this.writeShort(this.player.getRecomHave());
        this.writeInt((this.player.getMountNpcId() == 0) ? 0 : (this.player.getMountNpcId() + 1000000));
        this.writeInt(this.player.getActiveClass());
        this.writeInt(0);
        this.writeByte(this.player.isMounted() ? 0 : this.player.getInventory().getWeaponEnchant());
        this.writeByte(this.player.getTeam().getId());
        this.writeInt(appearence.getVisibleClanLargeCrestId());
        this.writeByte(this.player.isNoble());
        this.writeByte((this.player.isHero() || (this.player.isGM() && Config.GM_HERO_AURA)) ? 2 : 0);
        this.writeByte(this.player.isFishing());
        final ILocational baitLocation = this.player.getFishing().getBaitLocation();
        this.writeInt(baitLocation.getX());
        this.writeInt(baitLocation.getY());
        this.writeInt(baitLocation.getZ());
        this.writeInt(appearence.getNameColor());
        this.writeInt(this.player.getHeading());
        this.writeByte(this.player.getPledgeClass());
        this.writeShort(this.player.getPledgeType());
        this.writeInt(appearence.getTitleColor());
        this.writeByte(0);
        this.writeInt(Util.zeroIfNullOrElse((Object)this.player.getClan(), Clan::getReputationScore));
        this.writeInt(this.player.getTransformationDisplayId());
        this.writeInt(this.player.getAgathionId());
        this.writeByte(1);
        this.writeInt((int)Math.round(this.player.getCurrentCp()));
        this.writeInt(this.player.getMaxHp());
        this.writeInt((int)Math.round(this.player.getCurrentHp()));
        this.writeInt(this.player.getMaxMp());
        this.writeInt((int)Math.round(this.player.getCurrentMp()));
        this.writeByte(0);
        final Set<AbnormalVisualEffect> abnormalVisualEffects = this.player.getEffectList().getCurrentAbnormalVisualEffects();
        this.writeInt(abnormalVisualEffects.size());
        abnormalVisualEffects.forEach(effect -> this.writeShort(effect.getClientId()));
        this.writeByte(0);
        this.writeByte(this.player.isHairAccessoryEnabled());
        this.writeByte(this.player.getAbilityPointsUsed());
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt((this.player.getRank() == 1) ? 1 : ((this.player.getRankRace() == 1) ? 2 : 0));
        this.writeShort(0);
        this.writeShort(22 + appearence.getVisibleName().length() * 2);
        this.writeByte(0);
        this.writeByte(0);
        final ILocational loc = Objects.nonNull(this.player.getVehicle()) ? this.player.getInVehiclePosition() : this.player;
        this.writeInt(loc.getX());
        this.writeInt(loc.getY());
        this.writeInt(loc.getZ());
        this.writeInt(Util.zeroIfNullOrElse((Object)this.player.getVehicle(), WorldObject::getObjectId));
        this.writeSizedString((CharSequence)appearence.getVisibleName());
    }
    
    static {
        PAPERDOLL_ORDER = new InventorySlot[] { InventorySlot.PENDANT, InventorySlot.HEAD, InventorySlot.RIGHT_HAND, InventorySlot.LEFT_HAND, InventorySlot.GLOVES, InventorySlot.CHEST, InventorySlot.LEGS, InventorySlot.FEET, InventorySlot.CLOAK, InventorySlot.TWO_HAND, InventorySlot.HAIR, InventorySlot.HAIR2 };
    }
}
