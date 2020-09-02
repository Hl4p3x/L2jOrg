// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.xml;

import org.slf4j.LoggerFactory;
import org.l2j.commons.xml.XmlReader;
import org.l2j.gameserver.model.Clan;
import java.util.function.Consumer;
import java.util.Comparator;
import java.util.Collections;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.w3c.dom.Node;
import java.io.File;
import org.w3c.dom.Document;
import java.util.Objects;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.EnumMap;
import org.l2j.gameserver.model.pledge.ClanRewardBonus;
import java.util.List;
import org.l2j.gameserver.enums.ClanRewardType;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public class ClanRewardManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private final Map<ClanRewardType, List<ClanRewardBonus>> clanRewards;
    private int minRaidBonus;
    
    private ClanRewardManager() {
        this.clanRewards = new EnumMap<ClanRewardType, List<ClanRewardBonus>>(ClanRewardType.class);
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/clan-reward.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/clan-reward.xml");
        this.clanRewards.forEach((type, rewards) -> ClanRewardManager.LOGGER.info("Loaded {} rewards for {}", (Object)(Objects.nonNull(rewards) ? rewards.size() : 0), (Object)type));
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_1         /* doc */
        //     2: invokeinterface org/w3c/dom/Document.getFirstChild:()Lorg/w3c/dom/Node;
        //     7: invokedynamic   BootstrapMethod #1, test:()Ljava/util/function/Predicate;
        //    12: aload_0         /* this */
        //    13: invokedynamic   BootstrapMethod #2, accept:(Lorg/l2j/gameserver/data/xml/ClanRewardManager;)Ljava/util/function/Consumer;
        //    18: invokevirtual   org/l2j/gameserver/data/xml/ClanRewardManager.forEach:(Lorg/w3c/dom/Node;Ljava/util/function/Predicate;Ljava/util/function/Consumer;)V
        //    21: return         
        //    MethodParameters:
        //  Name  Flags  
        //  ----  -----
        //  doc   
        //  f     
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void parseRaidBonus(final Node node) {
        final int progress;
        final ClanRewardBonus bonus;
        final NamedNodeMap skillAttr;
        final ClanRewardBonus clanRewardBonus;
        this.forEach(node, "raid", raidNode -> {
            progress = this.parseInt(raidNode.getAttributes(), "progress");
            if (this.minRaidBonus == 0 || progress < this.minRaidBonus) {
                this.minRaidBonus = progress;
            }
            bonus = new ClanRewardBonus(ClanRewardType.ARENA, progress, progress);
            this.forEach(raidNode, "skill", skillNode -> {
                skillAttr = skillNode.getAttributes();
                clanRewardBonus.setSkillReward(new SkillHolder(this.parseInt(skillAttr, "id"), this.parseInt(skillAttr, "level")));
                return;
            });
            this.clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList()).add(bonus);
        });
    }
    
    private void parseMembersOnline(final Node node) {
        final NamedNodeMap attrs;
        final ClanRewardBonus bonus;
        final NamedNodeMap skillAttr;
        final ClanRewardBonus clanRewardBonus;
        this.forEach(node, "players", memberNode -> {
            attrs = memberNode.getAttributes();
            bonus = new ClanRewardBonus(ClanRewardType.MEMBERS_ONLINE, this.parseInt(attrs, "level"), this.parseInt(attrs, "size"));
            this.forEach(memberNode, "skill", skillNode -> {
                skillAttr = skillNode.getAttributes();
                clanRewardBonus.setSkillReward(new SkillHolder(this.parseInt(skillAttr, "id"), this.parseInt(skillAttr, "level")));
                return;
            });
            this.clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList()).add(bonus);
        });
    }
    
    private void parseHuntingBonus(final Node node) {
        final NamedNodeMap attrs;
        final ClanRewardBonus bonus;
        final NamedNodeMap itemsAttr;
        final ClanRewardBonus clanRewardBonus;
        this.forEach(node, "hunting", hunting -> {
            attrs = hunting.getAttributes();
            bonus = new ClanRewardBonus(ClanRewardType.HUNTING_MONSTERS, this.parseInt(attrs, "level"), this.parseInt(attrs, "points"));
            this.forEach(hunting, "item", itemsNode -> {
                itemsAttr = itemsNode.getAttributes();
                clanRewardBonus.setItemReward(new ItemHolder(this.parseInt(itemsAttr, "id"), this.parselong(itemsAttr, "count")));
                return;
            });
            this.clanRewards.computeIfAbsent(bonus.getType(), key -> new ArrayList()).add(bonus);
        });
    }
    
    public List<ClanRewardBonus> getClanRewardBonuses(final ClanRewardType type) {
        return this.clanRewards.get(type);
    }
    
    public ClanRewardBonus getHighestReward(final ClanRewardType type) {
        return this.clanRewards.getOrDefault(type, Collections.emptyList()).stream().max(Comparator.comparingInt(ClanRewardBonus::getLevel)).orElse(null);
    }
    
    public void forEachReward(final ClanRewardType type, final Consumer<ClanRewardBonus> action) {
        this.clanRewards.getOrDefault(type, Collections.emptyList()).forEach(action);
    }
    
    public void checkArenaProgress(final Clan clan) {
        final List<ClanRewardBonus> arenaRewards = this.clanRewards.get(ClanRewardType.ARENA);
        final ClanRewardBonus anyReward = arenaRewards.get(0);
        clan.removeSkill(anyReward.getSkillReward().getSkillId());
        final int progress = clan.getArenaProgress();
        if (progress >= this.minRaidBonus) {
            final ClanRewardBonus reward = arenaRewards.stream().filter(r -> r.getLevel() < progress).max(Comparator.comparingInt(ClanRewardBonus::getLevel)).orElse(null);
            if (Objects.nonNull(reward)) {
                clan.addNewSkill(reward.getSkillReward().getSkill());
            }
        }
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static ClanRewardManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClanRewardManager.class);
    }
    
    private static class Singleton
    {
        private static final ClanRewardManager INSTANCE;
        
        static {
            INSTANCE = new ClanRewardManager();
        }
    }
}
