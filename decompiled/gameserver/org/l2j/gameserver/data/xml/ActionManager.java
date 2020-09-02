// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Util;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.data.xml.model.ActionData;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class ActionManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final IntMap<ActionData> actions;
    private final IntIntMap actionSkills;
    
    private ActionManager() {
        this.actions = (IntMap<ActionData>)new HashIntMap();
        this.actionSkills = (IntIntMap)new HashIntIntMap();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/actions.xsd");
    }
    
    public void load() {
        this.actions.clear();
        this.actionSkills.clear();
        this.parseDatapackFile("data/actions.xml");
        ActionManager.LOGGER.info("Loaded {} player actions.", (Object)this.actions.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NamedNodeMap attrs;
        final int id;
        final int optionId;
        final ActionData action;
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "action", actionNode -> {
            attrs = actionNode.getAttributes();
            id = this.parseInt(attrs, "id");
            optionId = this.parseInt(attrs, "option");
            action = new ActionData(id, this.parseString(attrs, "handler"), optionId, this.parseBoolean(attrs, "auto-use"));
            this.actions.put(id, (Object)action);
            if (this.isActionSkill(action)) {
                this.actionSkills.put(optionId, id);
            }
        }));
    }
    
    private boolean isActionSkill(final ActionData h) {
        return h.getHandler().equals("PetSkillUse") || h.getHandler().equals("ServitorSkillUse");
    }
    
    public ActionData getActionData(final int id) {
        return (ActionData)this.actions.get(id);
    }
    
    public int getSkillActionId(final int skillId) {
        return this.actionSkills.getOrDefault(skillId, -1);
    }
    
    public boolean isAutoUseAction(final int actionId) {
        return Util.falseIfNullOrElse((Object)this.actions.get(actionId), ActionData::isAutoUse);
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ActionManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ActionManager.class);
    }
    
    private static class Singleton
    {
        private static final ActionManager INSTANCE;
        
        static {
            INSTANCE = new ActionManager();
        }
    }
}
