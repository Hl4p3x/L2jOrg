// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.quest;

import org.slf4j.LoggerFactory;
import java.util.Calendar;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerQuestComplete;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.network.serverpackets.ExShowQuestMark;
import java.util.HashMap;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.QuestManager;
import java.util.Map;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public final class QuestState
{
    private static final Logger LOGGER;
    private final String _questName;
    private final Player _player;
    private byte _state;
    private boolean _simulated;
    private Map<String, String> _vars;
    private boolean _isExitQuestOnCleanUp;
    
    public QuestState(final Quest quest, final Player player, final byte state) {
        this._simulated = false;
        this._isExitQuestOnCleanUp = false;
        this._questName = quest.getName();
        this._player = player;
        this._state = state;
        player.setQuestState(this);
    }
    
    public String getQuestName() {
        return this._questName;
    }
    
    public Quest getQuest() {
        return QuestManager.getInstance().getQuest(this._questName);
    }
    
    public Player getPlayer() {
        return this._player;
    }
    
    public byte getState() {
        return this._state;
    }
    
    public boolean isCreated() {
        return this._state == 0;
    }
    
    public boolean isStarted() {
        return this._state == 1;
    }
    
    public boolean isCompleted() {
        return this._state == 2;
    }
    
    public boolean setState(final byte state) {
        return this.setState(state, true);
    }
    
    public boolean setState(final byte state, final boolean saveInDb) {
        if (this._state == state) {
            return false;
        }
        final boolean newQuest = this.isCreated();
        this._state = state;
        if (saveInDb) {
            if (newQuest) {
                Quest.createQuestInDb(this);
            }
            else {
                Quest.updateQuestInDb(this);
            }
        }
        this._player.sendPacket(new QuestList(this._player));
        return true;
    }
    
    public String setInternal(final String var, String val) {
        if (this._vars == null) {
            this._vars = new HashMap<String, String>();
        }
        if (val == null) {
            val = "";
        }
        this._vars.put(var, val);
        return val;
    }
    
    public String set(final String var, final int val) {
        return this.set(var, Integer.toString(val));
    }
    
    public String set(final String var, String val) {
        if (this._vars == null) {
            this._vars = new HashMap<String, String>();
        }
        if (val == null) {
            val = "";
        }
        final String old = this._vars.put(var, val);
        if (old != null) {
            Quest.updateQuestVarInDb(this, var, val);
        }
        else {
            Quest.createQuestVarInDb(this, var, val);
        }
        if ("cond".equals(var)) {
            try {
                int previousVal = 0;
                try {
                    previousVal = Integer.parseInt(old);
                }
                catch (Exception ex) {}
                this.setCond(Integer.parseInt(val), previousVal);
                this.getQuest().sendNpcLogList(this.getPlayer());
            }
            catch (Exception e) {
                QuestState.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this._player.getName(), this._questName, val, e.getMessage()), (Throwable)e);
            }
        }
        return val;
    }
    
    private void setCond(final int cond, final int old) {
        if (cond == old) {
            return;
        }
        int completedStateFlags = 0;
        if (cond < 3 || cond > 31) {
            this.unset("__compltdStateFlags");
        }
        else {
            completedStateFlags = this.getInt("__compltdStateFlags");
        }
        if (completedStateFlags == 0) {
            if (cond > old + 1) {
                completedStateFlags = -2147483647;
                completedStateFlags |= (1 << old) - 1;
                completedStateFlags |= 1 << cond - 1;
                this.set("__compltdStateFlags", String.valueOf(completedStateFlags));
            }
        }
        else if (cond < old) {
            completedStateFlags &= (1 << cond) - 1;
            if (completedStateFlags == (1 << cond) - 1) {
                this.unset("__compltdStateFlags");
            }
            else {
                completedStateFlags |= 0x80000001;
                this.set("__compltdStateFlags", String.valueOf(completedStateFlags));
            }
        }
        else {
            completedStateFlags |= 1 << cond - 1;
            this.set("__compltdStateFlags", String.valueOf(completedStateFlags));
        }
        this._player.sendPacket(new QuestList(this._player));
        final Quest q = this.getQuest();
        if (!q.isCustomQuest() && cond > 0) {
            this._player.sendPacket(new ExShowQuestMark(q.getId(), this.getCond()));
        }
    }
    
    public String unset(final String var) {
        if (this._vars == null) {
            return null;
        }
        final String old = this._vars.remove(var);
        if (old != null) {
            Quest.deleteQuestVarInDb(this, var);
        }
        return old;
    }
    
    public String get(final String var) {
        if (this._vars == null) {
            return null;
        }
        return this._vars.get(var);
    }
    
    public int getInt(final String var) {
        if (this._vars == null) {
            return 0;
        }
        final String variable = this._vars.get(var);
        if (variable == null || variable.isEmpty()) {
            return 0;
        }
        int varint = 0;
        try {
            varint = Integer.parseInt(variable);
        }
        catch (NumberFormatException nfe) {
            QuestState.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, this._questName, var, variable, this._player.getObjectId()), (Throwable)nfe);
        }
        return varint;
    }
    
    public boolean isCond(final int condition) {
        return this.getInt("cond") == condition;
    }
    
    public int getCond() {
        if (this.isStarted()) {
            int val = this.getInt("cond");
            if ((val & Integer.MIN_VALUE) != 0x0) {
                val &= Integer.MAX_VALUE;
                for (int i = 1; i < 32; ++i) {
                    val >>= 1;
                    if (val == 0) {
                        val = i;
                        break;
                    }
                }
            }
            return val;
        }
        return 0;
    }
    
    public QuestState setCond(final int value) {
        if (this.isStarted()) {
            this.set("cond", Integer.toString(value));
        }
        return this;
    }
    
    public boolean isSet(final String variable) {
        return this.get(variable) != null;
    }
    
    public QuestState setCond(final int value, final boolean playQuestMiddle) {
        if (!this.isStarted()) {
            return this;
        }
        this.set("cond", String.valueOf(value));
        if (playQuestMiddle) {
            this._player.sendPacket(QuestSound.ITEMSOUND_QUEST_MIDDLE.getPacket());
        }
        return this;
    }
    
    public int getMemoState() {
        if (this.isStarted()) {
            return this.getInt("memoState");
        }
        return 0;
    }
    
    public QuestState setMemoState(final int value) {
        this.set("memoState", String.valueOf(value));
        return this;
    }
    
    public boolean isMemoState(final int memoState) {
        return this.getInt("memoState") == memoState;
    }
    
    public int getMemoStateEx(final int slot) {
        if (this.isStarted()) {
            return this.getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, slot));
        }
        return 0;
    }
    
    public QuestState setMemoStateEx(final int slot, final int value) {
        this.set(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, slot), String.valueOf(value));
        return this;
    }
    
    public boolean isMemoStateEx(final int slot, final int memoStateEx) {
        return this.getMemoStateEx(slot) == memoStateEx;
    }
    
    public final boolean isExitQuestOnCleanUp() {
        return this._isExitQuestOnCleanUp;
    }
    
    public void setIsExitQuestOnCleanUp(final boolean isExitQuestOnCleanUp) {
        this._isExitQuestOnCleanUp = isExitQuestOnCleanUp;
    }
    
    public QuestState startQuest() {
        if (this.isCreated() && !this.getQuest().isCustomQuest()) {
            this.set("cond", "1");
            this.setState((byte)1);
            this._player.sendPacket(QuestSound.ITEMSOUND_QUEST_ACCEPT.getPacket());
            this.getQuest().sendNpcLogList(this.getPlayer());
        }
        return this;
    }
    
    public QuestState exitQuest(final QuestType type) {
        switch (type) {
            case DAILY: {
                this.exitQuest(false);
                this.setRestartTime();
                break;
            }
            default: {
                this.exitQuest(type == QuestType.REPEATABLE);
                break;
            }
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestComplete(this._player, this.getQuest().getId(), type), this._player);
        return this;
    }
    
    public QuestState exitQuest(final QuestType type, final boolean playExitQuest) {
        this.exitQuest(type);
        if (playExitQuest) {
            this._player.sendPacket(QuestSound.ITEMSOUND_QUEST_FINISH.getPacket());
        }
        return this;
    }
    
    private QuestState exitQuest(final boolean repeatable) {
        this._player.removeNotifyQuestOfDeath(this);
        if (!this.isStarted()) {
            return this;
        }
        this.getQuest().removeRegisteredQuestItems(this._player);
        Quest.deleteQuestInDb(this, repeatable);
        if (repeatable) {
            this._player.delQuestState(this._questName);
            this._player.sendPacket(new QuestList(this._player));
        }
        else {
            this.setState((byte)2);
        }
        this._vars = null;
        return this;
    }
    
    public QuestState exitQuest(final boolean repeatable, final boolean playExitQuest) {
        this.exitQuest(repeatable);
        if (playExitQuest) {
            this._player.sendPacket(QuestSound.ITEMSOUND_QUEST_FINISH.getPacket());
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerQuestComplete(this._player, this.getQuest().getId(), repeatable ? QuestType.REPEATABLE : QuestType.ONE_TIME), this._player);
        return this;
    }
    
    public void setRestartTime() {
        final Calendar reDo = Calendar.getInstance();
        if (reDo.get(11) >= this.getQuest().getResetHour()) {
            reDo.add(5, 1);
        }
        reDo.set(11, this.getQuest().getResetHour());
        reDo.set(12, this.getQuest().getResetMinutes());
        this.set("restartTime", String.valueOf(reDo.getTimeInMillis()));
    }
    
    public boolean isNowAvailable() {
        final String val = this.get("restartTime");
        return val != null && Long.parseLong(val) <= System.currentTimeMillis();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)QuestState.class);
    }
}
