// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.util.Map;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.settings.AttendanceSettings;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.ArrayList;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class AttendanceRewardData extends GameXmlReader
{
    private static final Logger LOGGER;
    private final List<ItemHolder> _rewards;
    private int _rewardsCount;
    
    private AttendanceRewardData() {
        this._rewards = new ArrayList<ItemHolder>();
        this._rewardsCount = 0;
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/AttendanceRewards.xsd");
    }
    
    public void load() {
        if (((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).enabled()) {
            this._rewards.clear();
            this.parseDatapackFile("data/AttendanceRewards.xml");
            this._rewardsCount = this._rewards.size();
            AttendanceRewardData.LOGGER.info("Loaded {}  rewards.", (Object)this._rewardsCount);
            this.releaseResources();
        }
        else {
            AttendanceRewardData.LOGGER.info("Disabled.");
        }
    }
    
    public void parseDocument(final Document doc, final File f) {
        final StatsSet set;
        final int itemId;
        final int itemCount;
        this.forEach((Node)doc, "list", listNode -> this.forEach(listNode, "item", rewardNode -> {
            set = new StatsSet(this.parseAttributes(rewardNode));
            itemId = set.getInt("id");
            itemCount = set.getInt("count");
            if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                AttendanceRewardData.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), itemId));
            }
            else {
                this._rewards.add(new ItemHolder(itemId, itemCount));
            }
        }));
    }
    
    public List<ItemHolder> getRewards() {
        return this._rewards;
    }
    
    public int getRewardsCount() {
        return this._rewardsCount;
    }
    
    public static AttendanceRewardData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AttendanceRewardData.class);
    }
    
    private static class Singleton
    {
        private static final AttendanceRewardData INSTANCE;
        
        static {
            INSTANCE = new AttendanceRewardData();
        }
    }
}
