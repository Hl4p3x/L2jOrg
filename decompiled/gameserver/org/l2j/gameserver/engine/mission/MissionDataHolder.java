// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.mission;

import java.time.DayOfWeek;
import java.time.chrono.ChronoLocalDate;
import java.time.ZoneId;
import java.time.Instant;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.Function;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.base.ClassId;
import java.util.EnumSet;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;

public class MissionDataHolder
{
    private final int id;
    private final List<ItemHolder> rewardsItems;
    private final EnumSet<ClassId> classRestriction;
    private final int requiredCompletions;
    private final StatsSet params;
    private final MissionCycle cycle;
    private final boolean isDisplayedWhenNotAvailable;
    private final AbstractMissionHandler handler;
    private final int requiredMission;
    
    public MissionDataHolder(final StatsSet set) {
        final Function<MissionDataHolder, AbstractMissionHandler> handler = MissionEngine.getInstance().getHandler(set.getString("handler"));
        this.id = set.getInt("id");
        this.requiredCompletions = set.getInt("required-completion", 1);
        this.rewardsItems = set.getList("rewards", ItemHolder.class);
        this.classRestriction = set.getStringAsEnumSet("classRestriction", ClassId.class);
        this.params = set.getObject("params", StatsSet.class);
        this.cycle = set.getEnum("cycle", MissionCycle.class);
        this.isDisplayedWhenNotAvailable = set.getBoolean("display-not-available", true);
        this.requiredMission = set.getInt("requires-mission", 0);
        this.handler = ((handler != null) ? handler.apply(this) : null);
    }
    
    public int getId() {
        return this.id;
    }
    
    public List<ItemHolder> getRewards() {
        return this.rewardsItems;
    }
    
    public int getRequiredCompletions() {
        return this.requiredCompletions;
    }
    
    public StatsSet getParams() {
        return this.params;
    }
    
    public boolean isOneTime() {
        return this.cycle == MissionCycle.SINGLE;
    }
    
    public boolean isDisplayedWhenNotAvailable() {
        return this.isDisplayedWhenNotAvailable;
    }
    
    public boolean isDisplayable(final Player player) {
        if (!this.classRestriction.isEmpty() && !this.classRestriction.contains(player.getClassId())) {
            return false;
        }
        if (this.requiredMission != 0 && !MissionData.getInstance().isCompleted(player, this.requiredMission)) {
            return false;
        }
        final int status = this.getStatus(player);
        return (this.isDisplayedWhenNotAvailable() || status != MissionStatus.NOT_AVAILABLE.getClientId()) && (!this.isOneTime() || this.getRecentlyCompleted(player) || status != MissionStatus.COMPLETED.getClientId());
    }
    
    public void requestReward(final Player player) {
        if (Objects.nonNull(this.handler)) {
            this.handler.requestReward(player);
        }
    }
    
    public int getStatus(final Player player) {
        return Objects.nonNull(this.handler) ? this.handler.getStatus(player) : MissionStatus.NOT_AVAILABLE.getClientId();
    }
    
    public int getProgress(final Player player) {
        return (this.handler != null) ? this.handler.getProgress(player) : 0;
    }
    
    public boolean getRecentlyCompleted(final Player player) {
        return this.handler != null && this.handler.isRecentlyCompleted(player);
    }
    
    public void reset(final long lastReset) {
        if (this.handler != null && this.canReset(lastReset)) {
            this.handler.reset();
        }
    }
    
    private boolean canReset(final long lastReset) {
        boolean b = false;
        switch (this.cycle) {
            case SINGLE: {
                b = false;
                break;
            }
            case DAILY: {
                b = true;
                break;
            }
            case WEEKLY: {
                b = this.resetWeekly(lastReset);
                break;
            }
            case MONTHLY: {
                b = this.resetMonthly(lastReset);
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        return b;
    }
    
    private boolean resetMonthly(final long lastReset) {
        final LocalDate today = LocalDate.now();
        return today.getDayOfMonth() == 1 || today.with(TemporalAdjusters.firstDayOfMonth()).isAfter(LocalDate.ofInstant(Instant.ofEpochMilli(lastReset), ZoneId.systemDefault()));
    }
    
    private boolean resetWeekly(final long lastReset) {
        final LocalDate today = LocalDate.now();
        return today.getDayOfWeek() == DayOfWeek.SATURDAY || today.with(TemporalAdjusters.previous(DayOfWeek.SATURDAY)).isAfter(LocalDate.ofInstant(Instant.ofEpochMilli(lastReset), ZoneId.systemDefault()));
    }
    
    public boolean isAvailable(final Player player) {
        return Objects.nonNull(this.handler) && this.handler.isAvailable(player);
    }
    
    public boolean isCompleted(final Player player) {
        return this.getStatus(player) == MissionStatus.COMPLETED.getClientId();
    }
    
    EnumSet<ClassId> getClassRestriction() {
        return this.classRestriction;
    }
    
    int getRequiredMission() {
        return this.requiredMission;
    }
}
