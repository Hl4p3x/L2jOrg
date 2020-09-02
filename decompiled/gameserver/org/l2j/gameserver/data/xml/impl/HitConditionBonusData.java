// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class HitConditionBonusData extends GameXmlReader
{
    private static final Logger LOGGER;
    private int frontBonus;
    private int sideBonus;
    private int backBonus;
    private int highBonus;
    private int lowBonus;
    private int darkBonus;
    private int rainBonus;
    
    private HitConditionBonusData() {
        this.frontBonus = 0;
        this.sideBonus = 0;
        this.backBonus = 0;
        this.highBonus = 0;
        this.lowBonus = 0;
        this.darkBonus = 0;
        this.rainBonus = 0;
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/hitConditionBonus.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/stats/hitConditionBonus.xml");
        HitConditionBonusData.LOGGER.info("Loaded Hit Condition bonuses.");
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        for (Node d = doc.getFirstChild().getFirstChild(); d != null; d = d.getNextSibling()) {
            final NamedNodeMap attrs = d.getAttributes();
            final String nodeName = d.getNodeName();
            switch (nodeName) {
                case "front": {
                    this.frontBonus = this.parseInteger(attrs, "val");
                    break;
                }
                case "side": {
                    this.sideBonus = this.parseInteger(attrs, "val");
                    break;
                }
                case "back": {
                    this.backBonus = this.parseInteger(attrs, "val");
                    break;
                }
                case "high": {
                    this.highBonus = this.parseInteger(attrs, "val");
                    break;
                }
                case "low": {
                    this.lowBonus = this.parseInteger(attrs, "val");
                    break;
                }
                case "dark": {
                    this.darkBonus = this.parseInteger(attrs, "val");
                    break;
                }
                case "rain": {
                    this.rainBonus = this.parseInteger(attrs, "val");
                    break;
                }
            }
        }
    }
    
    public double getConditionBonus(final Creature attacker, final Creature target) {
        double mod = 100.0;
        if (attacker.getZ() - target.getZ() > 50) {
            mod += this.highBonus;
        }
        else if (attacker.getZ() - target.getZ() < -50) {
            mod += this.lowBonus;
        }
        if (WorldTimeController.getInstance().isNight()) {
            mod += this.darkBonus;
        }
        switch (Position.getPosition(attacker, target)) {
            case SIDE: {
                mod += this.sideBonus;
                break;
            }
            case BACK: {
                mod += this.backBonus;
                break;
            }
            default: {
                mod += this.frontBonus;
                break;
            }
        }
        return Math.max(mod / 100.0, 0.0);
    }
    
    public static HitConditionBonusData getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)HitConditionBonusData.class);
    }
    
    private static class Singleton
    {
        protected static final HitConditionBonusData INSTANCE;
        
        static {
            INSTANCE = new HitConditionBonusData();
        }
    }
}
