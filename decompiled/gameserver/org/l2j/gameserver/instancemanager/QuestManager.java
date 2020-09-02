// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.Config;
import java.util.Iterator;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.quest.Quest;
import java.util.Map;
import org.slf4j.Logger;

public final class QuestManager
{
    private static final Logger LOGGER;
    private final Map<String, Quest> _quests;
    private final Map<String, Quest> _scripts;
    
    private QuestManager() {
        this._quests = new ConcurrentHashMap<String, Quest>();
        this._scripts = new ConcurrentHashMap<String, Quest>();
    }
    
    public boolean reload(final String questFolder) {
        final Quest q = this.getQuest(questFolder);
        return q != null && q.reload();
    }
    
    public boolean reload(final int questId) {
        final Quest q = this.getQuest(questId);
        return q != null && q.reload();
    }
    
    public void reloadAllScripts() {
        this.unloadAllScripts();
        QuestManager.LOGGER.info("Reloading all server scripts.");
        try {
            ScriptEngineManager.getInstance().executeScriptLoader();
        }
        catch (Exception e) {
            QuestManager.LOGGER.error("Failed executing script list!", (Throwable)e);
        }
        getInstance().report();
    }
    
    public void unloadAllScripts() {
        QuestManager.LOGGER.info("Unloading all server scripts.");
        for (final Quest quest : this._quests.values()) {
            if (quest != null) {
                quest.unload(false);
            }
        }
        this._quests.clear();
        for (final Quest script : this._scripts.values()) {
            if (script != null) {
                script.unload(false);
            }
        }
        this._scripts.clear();
    }
    
    public void report() {
        QuestManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._quests.size()));
        QuestManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), this._scripts.size()));
    }
    
    public void save() {
        for (final Quest quest : this._quests.values()) {
            quest.onSave();
        }
        for (final Quest script : this._scripts.values()) {
            script.onSave();
        }
    }
    
    public Quest getQuest(final String name) {
        if (this._quests.containsKey(name)) {
            return this._quests.get(name);
        }
        return this._scripts.get(name);
    }
    
    public Quest getQuest(final int questId) {
        for (final Quest q : this._quests.values()) {
            if (q.getId() == questId) {
                return q;
            }
        }
        return null;
    }
    
    public void addQuest(final Quest quest) {
        if (quest == null) {
            throw new IllegalArgumentException("Quest argument cannot be null");
        }
        final Quest old = this._quests.put(quest.getName(), quest);
        if (old != null) {
            old.unload();
            QuestManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, old.getName(), old.getId()));
        }
        if (Config.ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS) {
            final String questName = quest.getName().contains("_") ? quest.getName().substring(quest.getName().indexOf(95) + 1) : quest.getName();
            QuestManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CommonUtil.splitWords(questName)));
        }
    }
    
    public boolean removeScript(final Quest script) {
        if (this._quests.containsKey(script.getName())) {
            this._quests.remove(script.getName());
            return true;
        }
        if (this._scripts.containsKey(script.getName())) {
            this._scripts.remove(script.getName());
            return true;
        }
        return false;
    }
    
    public Map<String, Quest> getQuests() {
        return this._quests;
    }
    
    public boolean unload(final Quest ms) {
        ms.onSave();
        return this.removeScript(ms);
    }
    
    public Map<String, Quest> getScripts() {
        return this._scripts;
    }
    
    public void addScript(final Quest script) {
        final Quest old = this._scripts.put(script.getClass().getSimpleName(), script);
        if (old != null) {
            old.unload();
            QuestManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, old.getName()));
        }
        if (Config.ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS) {
            QuestManager.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, CommonUtil.splitWords(script.getClass().getSimpleName())));
        }
    }
    
    public static QuestManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)QuestManager.class);
    }
    
    private static class Singleton
    {
        private static final QuestManager INSTANCE;
        
        static {
            INSTANCE = new QuestManager();
        }
    }
}
