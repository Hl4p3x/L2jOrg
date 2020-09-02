// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

public enum InstanceType
{
    L2Object((InstanceType)null), 
    L2ItemInstance(InstanceType.L2Object), 
    Creature(InstanceType.L2Object), 
    L2Npc(InstanceType.Creature), 
    Playable(InstanceType.Creature), 
    L2Summon(InstanceType.Playable), 
    L2Decoy(InstanceType.Creature), 
    L2PcInstance(InstanceType.Playable), 
    L2NpcInstance(InstanceType.L2Npc), 
    L2MerchantInstance(InstanceType.L2NpcInstance), 
    L2WarehouseInstance(InstanceType.L2NpcInstance), 
    L2StaticObjectInstance(InstanceType.Creature), 
    L2DoorInstance(InstanceType.Creature), 
    L2TerrainObjectInstance(InstanceType.L2Npc), 
    L2EffectPointInstance(InstanceType.L2Npc), 
    CommissionManagerInstance(InstanceType.L2Npc), 
    L2ServitorInstance(InstanceType.L2Summon), 
    L2PetInstance(InstanceType.L2Summon), 
    L2DecoyInstance(InstanceType.L2Decoy), 
    L2TrapInstance(InstanceType.L2Npc), 
    Attackable(InstanceType.L2Npc), 
    L2GuardInstance(InstanceType.Attackable), 
    L2MonsterInstance(InstanceType.Attackable), 
    L2BlockInstance(InstanceType.Attackable), 
    L2ChestInstance(InstanceType.L2MonsterInstance), 
    L2ControllableMobInstance(InstanceType.L2MonsterInstance), 
    L2FeedableBeastInstance(InstanceType.L2MonsterInstance), 
    L2TamedBeastInstance(InstanceType.L2FeedableBeastInstance), 
    L2FriendlyMobInstance(InstanceType.Attackable), 
    L2RaidBossInstance(InstanceType.L2MonsterInstance), 
    L2GrandBossInstance(InstanceType.L2RaidBossInstance), 
    FriendlyNpcInstance(InstanceType.Attackable), 
    L2FlyTerrainObjectInstance(InstanceType.L2Npc), 
    L2Vehicle(InstanceType.Creature), 
    L2BoatInstance(InstanceType.L2Vehicle), 
    L2ShuttleInstance(InstanceType.L2Vehicle), 
    L2DefenderInstance(InstanceType.Attackable), 
    L2ArtefactInstance(InstanceType.L2NpcInstance), 
    L2ControlTowerInstance(InstanceType.L2Npc), 
    L2FlameTowerInstance(InstanceType.L2Npc), 
    L2SiegeFlagInstance(InstanceType.L2Npc), 
    L2FortCommanderInstance(InstanceType.L2DefenderInstance), 
    L2FortLogisticsInstance(InstanceType.L2MerchantInstance), 
    L2FortManagerInstance(InstanceType.L2MerchantInstance), 
    L2FishermanInstance(InstanceType.L2MerchantInstance), 
    L2ObservationInstance(InstanceType.L2Npc), 
    L2OlympiadManagerInstance(InstanceType.L2Npc), 
    L2PetManagerInstance(InstanceType.L2MerchantInstance), 
    L2TeleporterInstance(InstanceType.L2Npc), 
    L2VillageMasterInstance(InstanceType.L2NpcInstance), 
    L2DoormenInstance(InstanceType.L2NpcInstance), 
    L2FortDoormenInstance(InstanceType.L2DoormenInstance), 
    L2ClassMasterInstance(InstanceType.L2NpcInstance), 
    L2SchemeBufferInstance(InstanceType.L2Npc), 
    L2EventMobInstance(InstanceType.L2Npc);
    
    private final InstanceType _parent;
    private final long _typeL;
    private final long _typeH;
    private final long _maskL;
    private final long _maskH;
    
    private InstanceType(final InstanceType parent) {
        this._parent = parent;
        final int high = this.ordinal() - 63;
        if (high < 0) {
            this._typeL = 1L << this.ordinal();
            this._typeH = 0L;
        }
        else {
            this._typeL = 0L;
            this._typeH = 1L << high;
        }
        if (this._typeL < 0L || this._typeH < 0L) {
            throw new Error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.name()));
        }
        if (parent != null) {
            this._maskL = (this._typeL | parent._maskL);
            this._maskH = (this._typeH | parent._maskH);
        }
        else {
            this._maskL = this._typeL;
            this._maskH = this._typeH;
        }
    }
    
    public final InstanceType getParent() {
        return this._parent;
    }
    
    public final boolean isType(final InstanceType it) {
        return (this._maskL & it._typeL) > 0L || (this._maskH & it._typeH) > 0L;
    }
    
    public final boolean isTypes(final InstanceType... it) {
        for (final InstanceType i : it) {
            if (this.isType(i)) {
                return true;
            }
        }
        return false;
    }
}
