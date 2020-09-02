// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.MonumentOfHeroes;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExHeroList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class MonumentOfHeroes extends AbstractNpcAI
{
    private static final int MONUMENT = 31690;
    private static final int HERO_CLOAK = 30372;
    private static final int WINGS_OF_DESTINY_CIRCLET = 6842;
    private static final int[] WEAPONS;
    
    private MonumentOfHeroes() {
        this.addStartNpc(31690);
        this.addFirstTalkId(31690);
        this.addTalkId(31690);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "MonumentOfHeroes-reward.html": {
                htmltext = event;
                break;
            }
            case "index": {
                htmltext = this.onFirstTalk(npc, player);
                break;
            }
            case "heroList": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExHeroList() });
                break;
            }
            case "receiveCloak": {
                final int olympiadRank = this.getOlympiadRank(player);
                if (olympiadRank != 1) {
                    htmltext = "MonumentOfHeroes-cloakNo.html";
                    break;
                }
                if (this.hasAtLeastOneQuestItem(player, new int[] { 30372 })) {
                    htmltext = "MonumentOfHeroes-cloakHave.html";
                    break;
                }
                if (player.isInventoryUnder80(false)) {
                    giveItems(player, 30372, 1L);
                    break;
                }
                player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                break;
            }
            case "heroWeapon": {
                if (!Hero.getInstance().isHero(player.getObjectId())) {
                    htmltext = "MonumentOfHeroes-weaponNo.html";
                    break;
                }
                if (player.isInventoryUnder80(false)) {
                    htmltext = (this.hasAtLeastOneQuestItem(player, MonumentOfHeroes.WEAPONS) ? "MonumentOfHeroes-weaponHave.html" : "MonumentOfHeroes-weaponList.html");
                    break;
                }
                player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                break;
            }
            case "heroCirclet": {
                if (!Hero.getInstance().isHero(player.getObjectId())) {
                    htmltext = "MonumentOfHeroes-circletNo.html";
                    break;
                }
                if (hasQuestItems(player, 6842)) {
                    htmltext = "MonumentOfHeroes-circletHave.html";
                    break;
                }
                if (!player.isInventoryUnder80(false)) {
                    player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                    break;
                }
                giveItems(player, 6842, 1L);
                break;
            }
            case "heroCertification": {
                if (Hero.getInstance().isUnclaimedHero(player.getObjectId())) {
                    htmltext = "MonumentOfHeroes-heroCertification.html";
                    break;
                }
                if (Hero.getInstance().isHero(player.getObjectId())) {
                    htmltext = "MonumentOfHeroes-heroCertificationAlready.html";
                    break;
                }
                htmltext = "MonumentOfHeroes-heroCertificationNo.html";
                break;
            }
            case "heroConfirm": {
                if (!Hero.getInstance().isUnclaimedHero(player.getObjectId())) {
                    htmltext = "MonumentOfHeroes-heroCertificationNo.html";
                    break;
                }
                if (player.isSubClassActive()) {
                    htmltext = "MonumentOfHeroes-heroCertificationSub.html";
                    break;
                }
                if (player.getLevel() >= 55) {
                    Hero.getInstance().claimHero(player);
                    showOnScreenMsg(player, NpcStringId.getNpcStringId(13357 + player.getClassId().getId()), 2, 5000, new String[0]);
                    player.broadcastPacket((ServerPacket)new PlaySound(1, "ns01_f", 0, 0, 0, 0, 0));
                    htmltext = "MonumentOfHeroes-heroCertificationsDone.html";
                    break;
                }
                htmltext = "MonumentOfHeroes-heroCertificationLevel.html";
                break;
            }
            case "give_6611":
            case "give_6612":
            case "give_6613":
            case "give_6614":
            case "give_6616":
            case "give_6617":
            case "give_6618":
            case "give_6619":
            case "give_6620":
            case "give_6621": {
                final int weaponId = Integer.parseInt(event.replace("give_", ""));
                giveItems(player, weaponId, 1L);
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        if ((!player.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || player.getLevel() < 55) {
            return "MonumentOfHeroes-noNoblesse.html";
        }
        return "MonumentOfHeroes-noblesse.html";
    }
    
    private int getOlympiadRank(final Player player) {
        return 1;
    }
    
    public static AbstractNpcAI provider() {
        return new MonumentOfHeroes();
    }
    
    static {
        WEAPONS = new int[] { 6611, 6612, 6613, 6614, 6616, 6617, 6618, 6619, 6620, 6621 };
    }
}
