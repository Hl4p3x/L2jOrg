// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.instancemanager.tasks.UpdateSoDStateTask;
import org.l2j.gameserver.Config;
import java.util.Calendar;
import org.slf4j.Logger;

public final class GraciaSeedsManager
{
    private static final Logger LOGGER;
    private static final byte SOITYPE = 2;
    private static final byte SOATYPE = 3;
    private static final byte SODTYPE = 1;
    public static String ENERGY_SEEDS;
    private final Calendar _SoDLastStateChangeDate;
    private int _SoDTiatKilled;
    private int _SoDState;
    
    private GraciaSeedsManager() {
        this._SoDTiatKilled = 0;
        this._SoDState = 1;
        this._SoDLastStateChangeDate = Calendar.getInstance();
        this.loadData();
        this.handleSodStages();
    }
    
    public void saveData(final byte seedType) {
        switch (seedType) {
            case 1: {
                GlobalVariablesManager.getInstance().set("SoDState", this._SoDState);
                GlobalVariablesManager.getInstance().set("SoDTiatKilled", this._SoDTiatKilled);
                GlobalVariablesManager.getInstance().set("SoDLSCDate", this._SoDLastStateChangeDate.getTimeInMillis());
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            default: {
                GraciaSeedsManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, seedType));
                break;
            }
        }
    }
    
    public void loadData() {
        if (GlobalVariablesManager.getInstance().hasVariable("SoDState")) {
            this._SoDState = GlobalVariablesManager.getInstance().getInt("SoDState");
            this._SoDTiatKilled = GlobalVariablesManager.getInstance().getInt("SoDTiatKilled");
            this._SoDLastStateChangeDate.setTimeInMillis(GlobalVariablesManager.getInstance().getLong("SoDLSCDate"));
        }
        else {
            this.saveData((byte)1);
        }
    }
    
    private void handleSodStages() {
        switch (this._SoDState) {
            case 1: {
                break;
            }
            case 2: {
                final long timePast = System.currentTimeMillis() - this._SoDLastStateChangeDate.getTimeInMillis();
                if (timePast >= Config.SOD_STAGE_2_LENGTH) {
                    this.setSoDState(1, true);
                    break;
                }
                ThreadPool.schedule((Runnable)new UpdateSoDStateTask(), Config.SOD_STAGE_2_LENGTH - timePast);
                break;
            }
            case 3: {
                this.setSoDState(1, true);
                break;
            }
            default: {
                GraciaSeedsManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._SoDState));
                break;
            }
        }
    }
    
    public void updateSodState() {
        final Quest quest = QuestManager.getInstance().getQuest(GraciaSeedsManager.ENERGY_SEEDS);
        if (quest == null) {
            GraciaSeedsManager.LOGGER.warn(": missing EnergySeeds Quest!");
        }
        else {
            quest.notifyEvent("StopSoDAi", null, null);
        }
    }
    
    public void increaseSoDTiatKilled() {
        if (this._SoDState == 1) {
            ++this._SoDTiatKilled;
            if (this._SoDTiatKilled >= Config.SOD_TIAT_KILL_COUNT) {
                this.setSoDState(2, false);
            }
            this.saveData((byte)1);
            final Quest esQuest = QuestManager.getInstance().getQuest(GraciaSeedsManager.ENERGY_SEEDS);
            if (esQuest == null) {
                GraciaSeedsManager.LOGGER.warn(": missing EnergySeeds Quest!");
            }
            else {
                esQuest.notifyEvent("StartSoDAi", null, null);
            }
        }
    }
    
    public int getSoDTiatKilled() {
        return this._SoDTiatKilled;
    }
    
    public void setSoDState(final int value, final boolean doSave) {
        GraciaSeedsManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), value));
        this._SoDLastStateChangeDate.setTimeInMillis(System.currentTimeMillis());
        this._SoDState = value;
        if (this._SoDState == 1) {
            this._SoDTiatKilled = 0;
        }
        this.handleSodStages();
        if (doSave) {
            this.saveData((byte)1);
        }
    }
    
    public long getSoDTimeForNextStateChange() {
        switch (this._SoDState) {
            case 1: {
                return -1L;
            }
            case 2: {
                return this._SoDLastStateChangeDate.getTimeInMillis() + Config.SOD_STAGE_2_LENGTH - System.currentTimeMillis();
            }
            case 3: {
                return -1L;
            }
            default: {
                return -1L;
            }
        }
    }
    
    public Calendar getSoDLastStateChangeDate() {
        return this._SoDLastStateChangeDate;
    }
    
    public int getSoDState() {
        return this._SoDState;
    }
    
    public static GraciaSeedsManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GraciaSeedsManager.class);
        GraciaSeedsManager.ENERGY_SEEDS = "EnergySeeds";
    }
    
    private static class Singleton
    {
        protected static final GraciaSeedsManager INSTANCE;
        
        static {
            INSTANCE = new GraciaSeedsManager();
        }
    }
}
