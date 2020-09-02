// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.data.database.data.ItemVariationData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.database.data.PlayerSubclassesData;
import org.l2j.gameserver.data.database.dao.PlayerSubclassesDAO;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.util.LinkedList;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.slf4j.Logger;

public class CharSelectionInfo extends ServerPacket
{
    private static final Logger LOGGER;
    private final String loginName;
    private final int sessionId;
    private final CharSelectInfoPackage[] playersInfo;
    private int activeId;
    
    public CharSelectionInfo(final String loginName, final int sessionId) {
        this(loginName, sessionId, -1);
    }
    
    public CharSelectionInfo(final String loginName, final int sessionId, final int activeId) {
        this.sessionId = sessionId;
        this.loginName = loginName;
        this.activeId = activeId;
        this.playersInfo = this.loadCharacterSelectInfo();
    }
    
    private CharSelectInfoPackage[] loadCharacterSelectInfo() {
        final LinkedList<CharSelectInfoPackage> characterList = new LinkedList<CharSelectInfoPackage>();
        try {
            boolean reloadDatas = false;
            List<PlayerData> charDatas = ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findAllCharactersByAccount(this.loginName);
            for (final PlayerData charData : charDatas) {
                final Player player = World.getInstance().findPlayer(charData.getCharId());
                if (player != null) {
                    reloadDatas = true;
                    Disconnection.of(player).storeMe().deleteMe();
                }
            }
            if (reloadDatas) {
                charDatas = ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findAllCharactersByAccount(this.loginName);
            }
            for (final PlayerData charData : charDatas) {
                final CharSelectInfoPackage playerInfo = this.restoreChar(charData);
                if (playerInfo != null) {
                    characterList.add(playerInfo);
                }
            }
            return characterList.toArray(CharSelectInfoPackage[]::new);
        }
        catch (Exception e) {
            CharSelectionInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            return new CharSelectInfoPackage[0];
        }
    }
    
    private void loadCharacterSubclassInfo(final CharSelectInfoPackage charInfopackage, final int ObjectId, final int activeClassId) {
        final PlayerSubclassesData charSubClassesDatas = ((PlayerSubclassesDAO)DatabaseAccess.getDAO((Class)PlayerSubclassesDAO.class)).findByIdAndClassId(ObjectId, activeClassId);
        try {
            if (charSubClassesDatas != null) {
                charInfopackage.setExp(charSubClassesDatas.getExp());
                charInfopackage.setSp(charSubClassesDatas.getSp());
                charInfopackage.setLevel(charSubClassesDatas.getLevel());
                charInfopackage.setVitalityPoints(charSubClassesDatas.getVitalityPoints());
            }
        }
        catch (Exception e) {
            CharSelectionInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    private CharSelectInfoPackage restoreChar(final PlayerData chardata) {
        final int objectId = chardata.getCharId();
        final String name = chardata.getName();
        final long deletetime = chardata.getDeleteTime();
        if (deletetime > 0L && System.currentTimeMillis() > deletetime) {
            final Clan clan = ClanTable.getInstance().getClan(chardata.getClanId());
            if (clan != null) {
                clan.removeClanMember(objectId, 0L);
            }
            GameClient.deleteCharByObjId(objectId);
            return null;
        }
        final CharSelectInfoPackage charInfopackage = new CharSelectInfoPackage(objectId, name);
        charInfopackage.setAccessLevel(chardata.getAccessLevel());
        charInfopackage.setLevel(chardata.getLevel());
        charInfopackage.setMaxHp((int)chardata.getMaxtHp());
        charInfopackage.setCurrentHp(chardata.getCurrentHp());
        charInfopackage.setMaxMp((int)chardata.getMaxtMp());
        charInfopackage.setCurrentMp(chardata.getCurrentMp());
        charInfopackage.setReputation(chardata.getReputation());
        charInfopackage.setPkKills(chardata.getPk());
        charInfopackage.setPvPKills(chardata.getPvP());
        charInfopackage.setFace(chardata.getFace());
        charInfopackage.setHairStyle(chardata.getHairStyle());
        charInfopackage.setHairColor(chardata.getHairColor());
        charInfopackage.setSex(chardata.isFemale() ? 1 : 0);
        charInfopackage.setExp(chardata.getExp());
        charInfopackage.setSp(chardata.getSp());
        charInfopackage.setVitalityPoints(chardata.getVitalityPoints());
        charInfopackage.setClanId(chardata.getClanId());
        charInfopackage.setRace(chardata.getRace());
        final int baseClassId = chardata.getBaseClass();
        final int activeClassId = chardata.getClassId();
        charInfopackage.setX(chardata.getX());
        charInfopackage.setY(chardata.getY());
        charInfopackage.setZ(chardata.getZ());
        if (Config.MULTILANG_ENABLE) {
            String lang = chardata.getLanguage();
            if (!Config.MULTILANG_ALLOWED.contains(lang)) {
                lang = Config.MULTILANG_DEFAULT;
            }
            charInfopackage.setHtmlPrefix(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, lang));
        }
        if (baseClassId != activeClassId) {
            this.loadCharacterSubclassInfo(charInfopackage, objectId, activeClassId);
        }
        charInfopackage.setClassId(activeClassId);
        int weaponObjId = charInfopackage.getPaperdollObjectId(InventorySlot.RIGHT_HAND);
        if (weaponObjId < 1) {
            weaponObjId = charInfopackage.getPaperdollObjectId(InventorySlot.TWO_HAND);
        }
        if (weaponObjId > 0) {
            final ItemVariationData itemVariation = ((ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class)).findItemVariationByItemId(weaponObjId);
            try {
                if (itemVariation != null) {
                    final int mineralId = itemVariation.getMineralId();
                    final int option1 = itemVariation.getOption1();
                    final int option2 = itemVariation.getOption2();
                    if (option1 != -1 && option2 != -1) {
                        charInfopackage.setAugmentation(new VariationInstance(mineralId, option1, option2));
                    }
                }
            }
            catch (Exception e) {
                CharSelectionInfo.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            }
        }
        if (baseClassId == 0 && activeClassId > 0) {
            charInfopackage.setBaseClassId(activeClassId);
        }
        else {
            charInfopackage.setBaseClassId(baseClassId);
        }
        charInfopackage.setDeleteTimer(deletetime);
        charInfopackage.setLastAccess(chardata.getLastAccess());
        charInfopackage.setNoble(chardata.isNobless());
        return charInfopackage;
    }
    
    public CharSelectInfoPackage[] getCharInfo() {
        return this.playersInfo;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHARACTER_SELECTION_INFO);
        final int size = this.playersInfo.length;
        this.writeInt(size);
        this.writeInt(Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
        this.writeByte(size == Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT);
        this.writeByte(1);
        this.writeInt(2);
        this.writeByte(0);
        this.writeByte(0);
        long lastAccess = 0L;
        if (this.activeId == -1) {
            for (int i = 0; i < size; ++i) {
                if (lastAccess < this.playersInfo[i].getLastAccess()) {
                    lastAccess = this.playersInfo[i].getLastAccess();
                    this.activeId = i;
                }
            }
        }
        for (int i = 0; i < size; ++i) {
            final CharSelectInfoPackage charInfoPackage = this.playersInfo[i];
            this.writeString((CharSequence)charInfoPackage.getName());
            this.writeInt(charInfoPackage.getObjectId());
            this.writeString((CharSequence)this.loginName);
            this.writeInt(this.sessionId);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(charInfoPackage.getSex());
            this.writeInt(charInfoPackage.getRace());
            this.writeInt(charInfoPackage.getClassId());
            this.writeInt(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).serverId());
            this.writeInt(charInfoPackage.getX());
            this.writeInt(charInfoPackage.getY());
            this.writeInt(charInfoPackage.getZ());
            this.writeDouble(charInfoPackage.getCurrentHp());
            this.writeDouble(charInfoPackage.getCurrentMp());
            this.writeLong(charInfoPackage.getSp());
            this.writeLong(charInfoPackage.getExp());
            this.writeDouble((double)((charInfoPackage.getExp() - LevelData.getInstance().getExpForLevel(charInfoPackage.getLevel())) / (float)(LevelData.getInstance().getExpForLevel(charInfoPackage.getLevel() + 1) - LevelData.getInstance().getExpForLevel(charInfoPackage.getLevel()))));
            this.writeInt(charInfoPackage.getLevel());
            this.writeInt(charInfoPackage.getReputation());
            this.writeInt(charInfoPackage.getPkKills());
            this.writeInt(charInfoPackage.getPvPKills());
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            for (final InventorySlot slot : this.getPaperdollOrder()) {
                this.writeInt(charInfoPackage.getPaperdollItemId(slot.getId()));
            }
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeShort(charInfoPackage.getEnchantEffect(InventorySlot.CHEST.getId()));
            this.writeShort(charInfoPackage.getEnchantEffect(InventorySlot.LEGS.getId()));
            this.writeShort(charInfoPackage.getEnchantEffect(InventorySlot.HEAD.getId()));
            this.writeShort(charInfoPackage.getEnchantEffect(InventorySlot.GLOVES.getId()));
            this.writeShort(charInfoPackage.getEnchantEffect(InventorySlot.FEET.getId()));
            this.writeInt(charInfoPackage.getHairStyle());
            this.writeInt(charInfoPackage.getHairColor());
            this.writeInt(charInfoPackage.getFace());
            this.writeDouble((double)charInfoPackage.getMaxHp());
            this.writeDouble((double)charInfoPackage.getMaxMp());
            this.writeInt((charInfoPackage.getDeleteTimer() > 0L) ? ((int)((charInfoPackage.getDeleteTimer() - System.currentTimeMillis()) / 1000L)) : 0);
            this.writeInt(charInfoPackage.getClassId());
            this.writeInt(i == this.activeId);
            this.writeByte(Math.min(charInfoPackage.getEnchantEffect(InventorySlot.RIGHT_HAND.getId()), 127));
            this.writeInt((charInfoPackage.getAugmentation() != null) ? charInfoPackage.getAugmentation().getOption1Id() : 0);
            this.writeInt((charInfoPackage.getAugmentation() != null) ? charInfoPackage.getAugmentation().getOption2Id() : 0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
            this.writeDouble(0.0);
            this.writeDouble(0.0);
            this.writeInt(charInfoPackage.getVitalityPoints());
            this.writeInt((int)(Config.RATE_VITALITY_EXP_MULTIPLIER * 100.0f));
            this.writeInt(charInfoPackage.getVitalityItemsUsed());
            this.writeInt((int)((charInfoPackage.getAccessLevel() != -100) ? 1 : 0));
            this.writeByte(charInfoPackage.isNoble());
            this.writeByte(Hero.getInstance().isHero(charInfoPackage.getObjectId()) ? 2 : 0);
            this.writeByte(charInfoPackage.isHairAccessoryEnabled());
            this.writeInt(0);
            this.writeInt((int)(charInfoPackage.getLastAccess() / 1000L));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CharSelectionInfo.class);
    }
}
