// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public class ExRequestClassChange extends ClientPacket
{
    private static final Logger LOGGER;
    private int classId;
    
    public void readImpl() {
        this.classId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        boolean canChange = false;
        for (final ClassId cId : player.getClassId().getNextClassIds()) {
            if (cId.getId() == this.classId) {
                canChange = true;
                break;
            }
        }
        if (!canChange) {
            ExRequestClassChange.LOGGER.warn("{} tried to change class from {}  to {}!", new Object[] { player, player.getClassId(), ClassId.getClassId(this.classId) });
            return;
        }
        canChange = false;
        final int playerLevel = player.getLevel();
        if (player.isInCategory(CategoryType.FIRST_CLASS_GROUP) && playerLevel >= 18) {
            canChange = CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, this.classId);
        }
        else if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP) && playerLevel >= 38) {
            canChange = CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, this.classId);
        }
        else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && playerLevel >= 76) {
            canChange = CategoryManager.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, this.classId);
        }
        if (canChange) {
            player.setClassId(this.classId);
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
            player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ExRequestClassChange.class);
    }
}
