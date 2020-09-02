// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.mission;

import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.mission.ExConnectedTimeAndGettableReward;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.MissionDAO;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import java.util.Objects;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.base.ClassId;
import java.util.EnumSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.ListenersContainer;

public abstract class AbstractMissionHandler extends ListenersContainer
{
    private final MissionDataHolder holder;
    
    protected AbstractMissionHandler(final MissionDataHolder holder) {
        this.holder = holder;
        this.init();
    }
    
    public MissionDataHolder getHolder() {
        return this.holder;
    }
    
    protected boolean canStart(final Player player) {
        final EnumSet<ClassId> classRestriction = this.holder.getClassRestriction();
        final int requiredMission = this.holder.getRequiredMission();
        return (classRestriction.isEmpty() || classRestriction.contains(player.getClassId())) && (requiredMission == 0 || MissionData.getInstance().isCompleted(player, requiredMission));
    }
    
    public boolean isAvailable(final Player player) {
        final boolean b;
        return this.holder.isDisplayable(player) && Util.falseIfNullOrElse((Object)this.getPlayerEntry(player, false), entry -> {
            switch (entry.getStatus()) {
                case AVAILABLE: {
                    break;
                }
                case NOT_AVAILABLE: {
                    if (entry.getProgress() >= this.getRequiredCompletion()) {
                        entry.setStatus(MissionStatus.AVAILABLE);
                        this.storePlayerEntry(entry);
                        break;
                    }
                    else {
                        break;
                    }
                    break;
                }
                default: {
                    break;
                }
            }
            return b;
        });
    }
    
    public abstract void init();
    
    public int getStatus(final Player player) {
        final MissionPlayerData entry = this.getPlayerEntry(player, false);
        return Objects.nonNull(entry) ? entry.getStatus().getClientId() : MissionStatus.NOT_AVAILABLE.getClientId();
    }
    
    protected int getRequiredCompletion() {
        return this.holder.getRequiredCompletions();
    }
    
    public int getProgress(final Player player) {
        return Util.zeroIfNullOrElse((Object)this.getPlayerEntry(player, false), MissionPlayerData::getProgress);
    }
    
    public boolean isRecentlyCompleted(final Player player) {
        return Util.falseIfNullOrElse((Object)this.getPlayerEntry(player, false), MissionPlayerData::isRecentlyCompleted);
    }
    
    public synchronized void reset() {
        ((MissionDAO)DatabaseAccess.getDAO((Class)MissionDAO.class)).deleteById(this.holder.getId());
        MissionData.getInstance().clearMissionData(this.holder.getId());
    }
    
    public void requestReward(final Player player) {
        synchronized (this.holder) {
            if (this.isAvailable(player)) {
                final MissionPlayerData entry = this.getPlayerEntry(player, true);
                entry.setStatus(MissionStatus.COMPLETED);
                entry.setRecentlyCompleted(true);
                this.storePlayerEntry(entry);
                this.giveRewards(player);
            }
        }
    }
    
    private void giveRewards(final Player player) {
        this.holder.getRewards().forEach(i -> player.addItem("One Day Reward", i, player, true));
    }
    
    protected void storePlayerEntry(final MissionPlayerData entry) {
        MissionData.getInstance().storeMissionData(this.holder.getId(), entry);
        ((MissionDAO)DatabaseAccess.getDAO((Class)MissionDAO.class)).save((Object)entry);
    }
    
    protected MissionPlayerData getPlayerEntry(final Player player, final boolean createIfNone) {
        final IntMap<MissionPlayerData> playerMissions = MissionData.getInstance().getStoredMissionData(player);
        if (playerMissions.containsKey(this.holder.getId())) {
            return (MissionPlayerData)playerMissions.get(this.holder.getId());
        }
        MissionPlayerData missionData = ((MissionDAO)DatabaseAccess.getDAO((Class)MissionDAO.class)).findById(player.getObjectId(), this.holder.getId());
        if (Objects.isNull(missionData) && createIfNone) {
            missionData = new MissionPlayerData(player.getObjectId(), this.holder.getId());
            final int progress = this.getProgress(player);
            missionData.setProgress(progress);
            missionData.setStatus((progress >= this.getRequiredCompletion()) ? MissionStatus.AVAILABLE : MissionStatus.NOT_AVAILABLE);
        }
        MissionData.getInstance().storeMissionData(this.holder.getId(), missionData);
        return missionData;
    }
    
    protected void notifyAvailablesReward(final Player player) {
        final Collection<MissionPlayerData> playerMissions = (Collection<MissionPlayerData>)MissionData.getInstance().getStoredMissionData(player).values();
        player.sendPacket(new ExConnectedTimeAndGettableReward((int)playerMissions.stream().filter(MissionPlayerData::isAvailable).count()));
    }
}
