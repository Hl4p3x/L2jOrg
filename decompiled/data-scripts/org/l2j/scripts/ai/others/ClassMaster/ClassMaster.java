// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.ClassMaster;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import java.io.File;
import java.nio.file.Path;
import org.l2j.gameserver.util.GameXmlReader;
import java.util.Collections;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;
import java.util.Arrays;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Iterator;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.CategoryType;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import io.github.joealisson.primitive.IntCollection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import io.github.joealisson.primitive.IntSet;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ClassMaster extends AbstractNpcAI
{
    private static final IntSet CLASS_MASTERS;
    private boolean _isEnabled;
    private boolean _spawnClassMasters;
    private boolean _showPopupWindow;
    private static final Logger LOGGER;
    private final List<ClassChangeData> _classChangeData;
    
    public ClassMaster() {
        this._classChangeData = new LinkedList<ClassChangeData>();
        new DataLoader().load();
        this.addStartNpc((IntCollection)ClassMaster.CLASS_MASTERS);
        this.addTalkId((IntCollection)ClassMaster.CLASS_MASTERS);
        this.addFirstTalkId((IntCollection)ClassMaster.CLASS_MASTERS);
    }
    
    public void onSpawnActivate(final SpawnTemplate template) {
        if (this._spawnClassMasters) {
            template.spawnAllIncludingNotDefault((Instance)null);
        }
    }
    
    public void onSpawnDeactivate(final SpawnTemplate template) {
        template.despawnAll();
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "test_server_helper001.html";
    }
    
    public String onAdvEvent(String event, final Npc npc, final Player player) {
        if (!this._isEnabled) {
            return null;
        }
        String htmltext = null;
        final StringTokenizer st = new StringTokenizer(event);
        final String nextToken;
        event = (nextToken = st.nextToken());
        switch (nextToken) {
            case "buyitems": {
                htmltext = ((npc.getId() == 31756) ? "test_server_helper001a.html" : "test_server_helper001b.html");
                break;
            }
            case "firstclass": {
                htmltext = this.getFirstOccupationChangeHtml(player);
                break;
            }
            case "secondclass": {
                htmltext = this.getSecondOccupationChangeHtml(player);
                break;
            }
            case "thirdclass": {
                if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && player.getLevel() > 75) {
                    if (this.changeToNextClass(player)) {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)new PlaySound("ItemSound.quest_fanfare_2") });
                        player.broadcastUserInfo();
                        htmltext = "test_server_helper021.html";
                        break;
                    }
                    break;
                }
                else {
                    if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
                        htmltext = "test_server_helper011.html";
                        break;
                    }
                    htmltext = "test_server_helper024.html";
                    break;
                }
                break;
            }
            case "setclass": {
                if (!st.hasMoreTokens()) {
                    return null;
                }
                final int classId = Integer.parseInt(st.nextToken());
                boolean canChange = false;
                if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && player.getLevel() >= 40) {
                    canChange = (CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId)));
                }
                else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && player.getLevel() >= 20) {
                    canChange = CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, classId);
                }
                else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && player.getLevel() >= 76) {
                    canChange = CategoryManager.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId);
                }
                if (!canChange) {
                    break;
                }
                int classDataIndex = -1;
                if (st.hasMoreTokens()) {
                    classDataIndex = Integer.parseInt(st.nextToken());
                }
                if (this.checkIfClassChangeHasOptions(player) && classDataIndex == -1) {
                    htmltext = this.getHtml(player, "cc_options.html");
                    htmltext = htmltext.replace("%name%", ClassListData.getInstance().getClass(classId).getClassName());
                    htmltext = htmltext.replace("%options%", this.getClassChangeOptions(player, classId));
                    return htmltext;
                }
                final ClassChangeData data = this.getClassChangeData(classDataIndex);
                if (data != null) {
                    if (!data.getItemsRequired().isEmpty()) {
                        for (final ItemHolder ri : data.getItemsRequired()) {
                            if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount()) {
                                player.sendMessage("You do not have enough items.");
                                return null;
                            }
                        }
                        for (final ItemHolder ri : data.getItemsRequired()) {
                            player.destroyItemByItemId(this.getClass().getSimpleName(), ri.getId(), ri.getCount(), (WorldObject)npc, true);
                        }
                    }
                    if (!data.getItemsRewarded().isEmpty()) {
                        for (final ItemHolder ri : data.getItemsRewarded()) {
                            giveItems(player, ri);
                        }
                    }
                    if (data.isRewardNoblesse()) {
                        player.setNoble(true);
                    }
                    if (data.isRewardHero()) {
                        player.setHero(true);
                    }
                }
                player.setClassId(classId);
                if (player.isSubClassActive()) {
                    ((SubClass)player.getSubClasses().get(player.getClassIndex())).setClassId(player.getActiveClass());
                }
                else {
                    player.setBaseClass(player.getActiveClass());
                }
                if (Config.AUTO_LEARN_SKILLS) {
                    player.giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, true);
                }
                player.store(false);
                player.broadcastUserInfo();
                player.sendSkillList();
                player.sendPacket(new ServerPacket[] { (ServerPacket)new PlaySound("ItemSound.quest_fanfare_2") });
                return "test_server_helper021.html";
            }
            case "clanlevel": {
                htmltext = (player.isClanLeader() ? "test_server_helper022.html" : "pl014.html");
                break;
            }
            case "learnskills": {
                player.giveAvailableSkills(true, true);
                break;
            }
            case "clanlevelup": {
                if (player.getClan() == null || !player.isClanLeader()) {
                    return null;
                }
                if (player.getClan().getLevel() >= 10) {
                    htmltext = "test_server_helper022a.html";
                    break;
                }
                player.getClan().setLevel(player.getClan().getLevel() + 1);
                player.getClan().broadcastClanStatus();
                break;
            }
            case "test_server_helper001.html": {
                if (ClassMaster.CLASS_MASTERS.contains(npc.getId())) {
                    htmltext = event;
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    private String getFirstOccupationChangeHtml(final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) {
            if (player.getLevel() < 20) {
                htmltext = "test_server_helper027.html";
            }
            else {
                switch (player.getClassId()) {
                    case FIGHTER: {
                        htmltext = "test_server_helper026a.html";
                        break;
                    }
                    case MAGE: {
                        htmltext = "test_server_helper026b.html";
                        break;
                    }
                    case ELVEN_FIGHTER: {
                        htmltext = "test_server_helper026c.html";
                        break;
                    }
                    case ELVEN_MAGE: {
                        htmltext = "test_server_helper026d.html";
                        break;
                    }
                    case DARK_FIGHTER: {
                        htmltext = "test_server_helper026e.html";
                        break;
                    }
                    case DARK_MAGE: {
                        htmltext = "test_server_helper026f.html";
                        break;
                    }
                    case ORC_FIGHTER: {
                        htmltext = "test_server_helper026g.html";
                        break;
                    }
                    case ORC_MAGE: {
                        htmltext = "test_server_helper026h.html";
                        break;
                    }
                    case DWARVEN_FIGHTER: {
                        htmltext = "test_server_helper026i.html";
                        break;
                    }
                    case JIN_KAMAEL_SOLDIER: {
                        htmltext = "test_server_helper026j.html";
                        break;
                    }
                }
            }
        }
        else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
            htmltext = "test_server_helper028.html";
        }
        else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "test_server_helper010.html";
        }
        else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
            htmltext = "test_server_helper011.html";
        }
        return htmltext;
    }
    
    private String getSecondOccupationChangeHtml(final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) {
            if (player.getLevel() < 40) {
                htmltext = "test_server_helper023.html";
            }
            else {
                switch (player.getClassId()) {
                    case FIGHTER: {
                        htmltext = "test_server_helper012.html";
                        break;
                    }
                    case WARRIOR: {
                        htmltext = "test_server_helper012a.html";
                        break;
                    }
                    case KNIGHT: {
                        htmltext = "test_server_helper012b.html";
                        break;
                    }
                    case ROGUE: {
                        htmltext = "test_server_helper012c.html";
                        break;
                    }
                    case MAGE: {
                        htmltext = "test_server_helper013.html";
                        break;
                    }
                    case WIZARD: {
                        htmltext = "test_server_helper013a.html";
                        break;
                    }
                    case CLERIC: {
                        htmltext = "test_server_helper013b.html";
                        break;
                    }
                    case ELVEN_FIGHTER: {
                        htmltext = "test_server_helper014.html";
                        break;
                    }
                    case ELVEN_KNIGHT: {
                        htmltext = "test_server_helper014a.html";
                        break;
                    }
                    case ELVEN_SCOUT: {
                        htmltext = "test_server_helper014b.html";
                        break;
                    }
                    case ELVEN_MAGE: {
                        htmltext = "test_server_helper015.html";
                        break;
                    }
                    case ELVEN_WIZARD: {
                        htmltext = "test_server_helper015a.html";
                        break;
                    }
                    case ORACLE: {
                        htmltext = "test_server_helper015b.html";
                        break;
                    }
                    case DARK_FIGHTER: {
                        htmltext = "test_server_helper016.html";
                        break;
                    }
                    case PALUS_KNIGHT: {
                        htmltext = "test_server_helper016a.html";
                        break;
                    }
                    case ASSASSIN: {
                        htmltext = "test_server_helper016b.html";
                        break;
                    }
                    case DARK_MAGE: {
                        htmltext = "test_server_helper017.html";
                        break;
                    }
                    case DARK_WIZARD: {
                        htmltext = "test_server_helper017a.html";
                        break;
                    }
                    case SHILLIEN_ORACLE: {
                        htmltext = "test_server_helper017b.html";
                        break;
                    }
                    case ORC_FIGHTER: {
                        htmltext = "test_server_helper018.html";
                        break;
                    }
                    case ORC_RAIDER: {
                        htmltext = "test_server_helper018a.html";
                        break;
                    }
                    case ORC_MONK: {
                        htmltext = "test_server_helper018b.html";
                        break;
                    }
                    case ORC_MAGE:
                    case ORC_SHAMAN: {
                        htmltext = "test_server_helper019.html";
                        break;
                    }
                    case DWARVEN_FIGHTER: {
                        htmltext = "test_server_helper020.html";
                        break;
                    }
                    case ARTISAN: {
                        htmltext = "test_server_helper020b.html";
                        break;
                    }
                    case SCAVENGER: {
                        htmltext = "test_server_helper020a.html";
                        break;
                    }
                    case JIN_KAMAEL_SOLDIER:
                    case TROOPER: {
                        htmltext = "test_server_helper020c.html";
                        break;
                    }
                    case SOUL_FINDER: {
                        htmltext = "test_server_helper020d.html";
                        break;
                    }
                    case WARDER: {
                        htmltext = "test_server_helper030c.html";
                        break;
                    }
                }
            }
        }
        else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "test_server_helper010.html";
        }
        else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
            htmltext = "test_server_helper011.html";
        }
        else {
            htmltext = "test_server_helper029.html";
        }
        return htmltext;
    }
    
    private boolean changeToNextClass(final Player player) {
        final ClassId newClass = Arrays.stream(ClassId.values()).filter(cid -> player.getClassId() == cid.getParent()).findAny().orElse(null);
        if (newClass == null) {
            ClassMaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return false;
        }
        if (newClass == player.getClassId()) {
            ClassMaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            return false;
        }
        if (this.checkIfClassChangeHasOptions(player)) {
            String html = this.getHtml(player, "cc_options.html");
            html = html.replace("%name%", ClassListData.getInstance().getClass(newClass.getId()).getClassName());
            html = html.replace("%options%", this.getClassChangeOptions(player, newClass.getId()));
            this.showResult(player, html);
            return false;
        }
        final ClassChangeData data = this._classChangeData.stream().filter(ccd -> ccd.isInCategory(player)).findFirst().get();
        if (data != null) {
            if (!data.getItemsRequired().isEmpty()) {
                for (final ItemHolder ri : data.getItemsRequired()) {
                    if (player.getInventory().getInventoryItemCount(ri.getId(), -1) < ri.getCount()) {
                        player.sendMessage("You do not have enough items.");
                        return false;
                    }
                }
                for (final ItemHolder ri : data.getItemsRequired()) {
                    player.destroyItemByItemId(this.getClass().getSimpleName(), ri.getId(), ri.getCount(), (WorldObject)player, true);
                }
            }
            if (!data.getItemsRewarded().isEmpty()) {
                for (final ItemHolder ri : data.getItemsRewarded()) {
                    giveItems(player, ri);
                }
            }
            if (data.isRewardNoblesse()) {
                player.setNoble(true);
            }
            if (data.isRewardHero()) {
                player.setHero(true);
            }
        }
        player.setClassId(newClass.getId());
        if (player.isSubClassActive()) {
            ((SubClass)player.getSubClasses().get(player.getClassIndex())).setClassId(player.getActiveClass());
        }
        else {
            player.setBaseClass(player.getActiveClass());
        }
        if (Config.AUTO_LEARN_SKILLS) {
            player.giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, true);
        }
        player.store(false);
        player.broadcastUserInfo();
        player.sendSkillList();
        return true;
    }
    
    private void showPopupWindow(final Player player) {
        if (!this._showPopupWindow) {
            return;
        }
        if ((player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && player.getLevel() >= 20) || ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && player.getLevel() >= 40) || (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && player.getLevel() >= 76)) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerPressTutorialMark(final OnPlayerPressTutorialMark event) {
        final Player player = event.getPlayer();
        if (!this._showPopupWindow || event.getMarkId() != 2) {
            return;
        }
        String html = null;
        if ((player.isInCategory(CategoryType.SECOND_CLASS_GROUP) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP)) && player.getLevel() >= 40) {
            html = this.getHtml(player, this.getSecondOccupationChangeHtml(player));
        }
        else if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && player.getLevel() >= 20) {
            html = this.getHtml(player, this.getFirstOccupationChangeHtml(player));
        }
        else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && player.getLevel() >= 76) {
            html = this.getHtml(player, "qm_thirdclass.html");
        }
        if (html != null) {
            this.showResult(event.getPlayer(), html);
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_BYPASS)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerBypass(final OnPlayerBypass event) {
        if (event.getCommand().startsWith("Quest ClassMaster ")) {
            final String html = this.onAdvEvent(event.getCommand().substring(18), null, event.getPlayer());
            event.getPlayer().sendPacket(new ServerPacket[] { (ServerPacket)TutorialCloseHtml.STATIC_PACKET });
            this.showResult(event.getPlayer(), html);
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerProfessionChange(final OnPlayerProfessionChange event) {
        this.showPopupWindow(event.getActiveChar());
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLevelChanged(final OnPlayerLevelChanged event) {
        this.showPopupWindow(event.getActiveChar());
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(final OnPlayerLogin event) {
        final Player player = event.getPlayer();
        this.showPopupWindow(event.getPlayer());
    }
    
    private String getClassChangeOptions(final Player player, final int selectedClassId) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this._classChangeData.size(); ++i) {
            final ClassChangeData option = this.getClassChangeData(i);
            if (option != null) {
                if (option.getCategories().stream().anyMatch(ct -> player.isInCategory(ct))) {
                    sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
                    sb.append("<tr><td><table bgcolor=3f3f3f width=100%>");
                    sb.append(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, selectedClassId, i, option.getName()));
                    sb.append("<tr><td><table width=276>");
                    sb.append("<tr><td>Requirements:</td></tr>");
                    if (option.getItemsRequired().isEmpty()) {
                        sb.append("<tr><td><font color=LEVEL>Free</font></td></tr>");
                    }
                    else {
                        option.getItemsRequired().forEach(ih -> sb.append(invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, ih.getCount(), ItemEngine.getInstance().getTemplate(ih.getId()).getName())));
                    }
                    sb.append("<tr><td>Rewards:</td></tr>");
                    if (option.getItemsRewarded().isEmpty()) {
                        if (option.isRewardNoblesse()) {
                            sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
                        }
                        if (option.isRewardHero()) {
                            sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
                        }
                        if (!option.isRewardNoblesse() && !option.isRewardHero()) {
                            sb.append("<tr><td><font color=LEVEL>none</font></td></tr>");
                        }
                    }
                    else {
                        option.getItemsRewarded().forEach(ih -> sb.append(invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, ih.getCount(), ItemEngine.getInstance().getTemplate(ih.getId()).getName())));
                        if (option.isRewardNoblesse()) {
                            sb.append("<tr><td><font color=\"LEVEL\">Noblesse status.</font></td></tr>");
                        }
                        if (option.isRewardHero()) {
                            sb.append("<tr><td><font color=\"LEVEL\">Hero status.</font></td></tr>");
                        }
                    }
                    sb.append("</table></td></tr>");
                    sb.append("</table></td></tr>");
                    sb.append("<tr><td><img src=L2UI_CT1.ChatBalloon_DF_TopCenter width=276 height=1 /></td></tr>");
                }
            }
        }
        return sb.toString();
    }
    
    private boolean checkIfClassChangeHasOptions(final Player player) {
        boolean showOptions = this._classChangeData.stream().filter(ccd -> !ccd.getItemsRequired().isEmpty()).anyMatch(ccd -> ccd.isInCategory(player));
        if (!showOptions) {
            showOptions = (this._classChangeData.stream().filter(ccd -> !ccd.getItemsRewarded().isEmpty()).filter(ccd -> ccd.isInCategory(player)).count() > 1L);
        }
        return showOptions;
    }
    
    private ClassChangeData getClassChangeData(final int index) {
        if (index >= 0 && index < this._classChangeData.size()) {
            return this._classChangeData.get(index);
        }
        return null;
    }
    
    public static AbstractNpcAI provider() {
        return new ClassMaster();
    }
    
    static {
        CLASS_MASTERS = IntSet.of(31756, 31757);
        LOGGER = LoggerFactory.getLogger((Class)ClassMaster.class);
    }
    
    private static class ClassChangeData
    {
        private final String _name;
        private final List<CategoryType> _appliedCategories;
        private boolean _rewardNoblesse;
        private boolean _rewardHero;
        private List<ItemHolder> _itemsRequired;
        private List<ItemHolder> _itemsRewarded;
        
        public ClassChangeData(final String name, final List<CategoryType> appliedCategories) {
            this._name = name;
            this._appliedCategories = ((appliedCategories != null) ? appliedCategories : Collections.emptyList());
        }
        
        public String getName() {
            return this._name;
        }
        
        public List<CategoryType> getCategories() {
            return (this._appliedCategories != null) ? this._appliedCategories : Collections.emptyList();
        }
        
        public boolean isInCategory(final Player player) {
            if (this._appliedCategories != null) {
                for (final CategoryType category : this._appliedCategories) {
                    if (player.isInCategory(category)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        public boolean isRewardNoblesse() {
            return this._rewardNoblesse;
        }
        
        public void setRewardNoblesse(final boolean rewardNoblesse) {
            this._rewardNoblesse = rewardNoblesse;
        }
        
        public boolean isRewardHero() {
            return this._rewardHero;
        }
        
        public void setRewardHero(final boolean rewardHero) {
            this._rewardHero = rewardHero;
        }
        
        void setItemsRequired(final List<ItemHolder> itemsRequired) {
            this._itemsRequired = itemsRequired;
        }
        
        public List<ItemHolder> getItemsRequired() {
            return (this._itemsRequired != null) ? this._itemsRequired : Collections.emptyList();
        }
        
        void setItemsRewarded(final List<ItemHolder> itemsRewarded) {
            this._itemsRewarded = itemsRewarded;
        }
        
        public List<ItemHolder> getItemsRewarded() {
            return (this._itemsRewarded != null) ? this._itemsRewarded : Collections.emptyList();
        }
    }
    
    private class DataLoader extends GameXmlReader
    {
        protected Path getSchemaFilePath() {
            return Path.of("config/xsd/classMaster.xsd", new String[0]);
        }
        
        public void load() {
            ClassMaster.this._classChangeData.clear();
            this.parseFile(new File("config/ClassMaster.xml"));
            ClassMaster.LOGGER.info("Loaded {} class change options.", (Object)ClassMaster.this._classChangeData.size());
            this.releaseResources();
        }
        
        public void parseDocument(final Document doc, final File f) {
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equals(n.getNodeName())) {
                    for (Node cm = n.getFirstChild(); cm != null; cm = cm.getNextSibling()) {
                        NamedNodeMap attrs = cm.getAttributes();
                        if ("classMaster".equals(cm.getNodeName())) {
                            if (!(ClassMaster.this._isEnabled = this.parseBoolean(attrs, "classChangeEnabled", Boolean.valueOf(false)))) {
                                return;
                            }
                            ClassMaster.this._spawnClassMasters = this.parseBoolean(attrs, "spawnClassMasters", Boolean.valueOf(true));
                            ClassMaster.this._showPopupWindow = this.parseBoolean(attrs, "showPopupWindow", Boolean.valueOf(false));
                            for (Node c = cm.getFirstChild(); c != null; c = c.getNextSibling()) {
                                attrs = c.getAttributes();
                                if ("classChangeOption".equals(c.getNodeName())) {
                                    final List<CategoryType> appliedCategories = new LinkedList<CategoryType>();
                                    final List<ItemHolder> requiredItems = new LinkedList<ItemHolder>();
                                    final List<ItemHolder> rewardedItems = new LinkedList<ItemHolder>();
                                    boolean setNoble = false;
                                    boolean setHero = false;
                                    final String optionName = this.parseString(attrs, "name", "");
                                    for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling()) {
                                        attrs = b.getAttributes();
                                        if ("appliesTo".equals(b.getNodeName())) {
                                            for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling()) {
                                                attrs = r.getAttributes();
                                                if ("category".equals(r.getNodeName())) {
                                                    final CategoryType category = CategoryType.findByName(r.getTextContent().trim());
                                                    if (category == null) {
                                                        ClassMaster.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, r.getNodeValue()));
                                                    }
                                                    else {
                                                        appliedCategories.add(category);
                                                    }
                                                }
                                            }
                                        }
                                        if ("rewards".equals(b.getNodeName())) {
                                            for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling()) {
                                                attrs = r.getAttributes();
                                                if ("item".equals(r.getNodeName())) {
                                                    final int itemId = this.parseInteger(attrs, "id");
                                                    final int count = this.parseInteger(attrs, "count", Integer.valueOf(1));
                                                    rewardedItems.add(new ItemHolder(itemId, (long)count));
                                                }
                                                else if ("setNoble".equals(r.getNodeName())) {
                                                    setNoble = true;
                                                }
                                                else if ("setHero".equals(r.getNodeName())) {
                                                    setHero = true;
                                                }
                                            }
                                        }
                                        else if ("conditions".equals(b.getNodeName())) {
                                            for (Node r = b.getFirstChild(); r != null; r = r.getNextSibling()) {
                                                attrs = r.getAttributes();
                                                if ("item".equals(r.getNodeName())) {
                                                    final int itemId = this.parseInteger(attrs, "id");
                                                    final int count = this.parseInteger(attrs, "count", Integer.valueOf(1));
                                                    requiredItems.add(new ItemHolder(itemId, (long)count));
                                                }
                                            }
                                        }
                                    }
                                    if (appliedCategories.isEmpty()) {
                                        ClassMaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, optionName));
                                    }
                                    else {
                                        final ClassChangeData classChangeData = new ClassChangeData(optionName, appliedCategories);
                                        classChangeData.setItemsRequired(requiredItems);
                                        classChangeData.setItemsRewarded(rewardedItems);
                                        classChangeData.setRewardHero(setHero);
                                        classChangeData.setRewardNoblesse(setNoble);
                                        ClassMaster.this._classChangeData.add(classChangeData);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
