// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00333_HuntOfTheBlackLion;

import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00333_HuntOfTheBlackLion extends Quest
{
    private static final int ABYSSAL_CELEBRANT_UNDRIAS = 30130;
    private static final int BLACKSMITH_RUPIO = 30471;
    private static final int IRON_GATES_LOCKIRIN = 30531;
    private static final int MERCENARY_CAPTAIN_SOPHYA = 30735;
    private static final int MERCENARY_REEDFOOT = 30736;
    private static final int GUILDSMAN_MORGON = 30737;
    private static final int BLACK_LION_MARK = 1369;
    private static final int CARGO_BOX_1ST = 3440;
    private static final int CARGO_BOX_2ND = 3441;
    private static final int CARGO_BOX_3RD = 3442;
    private static final int CARGO_BOX_4TH = 3443;
    private static final int STATUE_OF_SHILEN_HEAD = 3457;
    private static final int STATUE_OF_SHILEN_TORSO = 3458;
    private static final int STATUE_OF_SHILEN_ARM = 3459;
    private static final int STATUE_OF_SHILEN_LEG = 3460;
    private static final int COMPLETE_STATUE_OF_SHILEN = 3461;
    private static final int FRAGMENT_OF_ANCIENT_TABLET_1ST_PIECE = 3462;
    private static final int FRAGMENT_OF_ANCIENT_TABLET_2ND_PIECE = 3463;
    private static final int FRAGMENT_OF_ANCIENT_TABLET_3RD_PIECE = 3464;
    private static final int FRAGMENT_OF_ANCIENT_TABLET_4TH_PIECE = 3465;
    private static final int COMPLETE_ANCIENT_TABLET = 3466;
    private static final int SOPHYAS_1ST_ORDER = 3671;
    private static final int SOPHYAS_2ND_ORDER = 3672;
    private static final int SOPHYAS_3RD_ORDER = 3673;
    private static final int SOPHYAS_4TH_ORDER = 3674;
    private static final int LIONS_CLAW = 3675;
    private static final int LIONS_EYE = 3676;
    private static final int GUILD_COIN = 3677;
    private static final int UNDEAD_ASH = 3848;
    private static final int BLOODY_AXE_INSIGNIA = 3849;
    private static final int DELU_LIZARDMAN_FANG = 3850;
    private static final int STAKATO_TALON = 3851;
    private static final int ALACRITY_POTION = 735;
    private static final int SCROL_OF_ESCAPE = 736;
    private static final int HELING_POTION = 1061;
    private static final int SOULSHOT_D_GRADE = 1463;
    private static final int SPIRITSHOT_D_GRADE = 2510;
    private static final int GLUDIO_APPLES = 3444;
    private static final int DION_CORN_MEAL = 3445;
    private static final int DIRE_WOLF_PELTS = 3446;
    private static final int MOONSTONE = 3447;
    private static final int GLUDIO_WHEAT_FLOUR = 3448;
    private static final int SPIDERSILK_ROPE = 3449;
    private static final int ALEXANDRITE = 3450;
    private static final int SILVER_TEA_SERVICE = 3451;
    private static final int MECHANIC_GOLEM_SPACE_PARTS = 3452;
    private static final int FIRE_EMERALD = 3453;
    private static final int AVELLAN_SILK_FROCK = 3454;
    private static final int FERIOTIC_PORCELAIN_URM = 3455;
    private static final int IMPERIAL_DIAMOND = 3456;
    private static final int MARSH_STAKATO = 20157;
    private static final int NEER_CRAWLER = 20160;
    private static final int SPECTER = 20171;
    private static final int SORROW_MAIDEN = 20197;
    private static final int NEER_CRAWLER_BERSERKER = 20198;
    private static final int STRAIN = 20200;
    private static final int GHOUL = 20201;
    private static final int OL_MAHUM_GUERILLA = 20207;
    private static final int OL_MAHUM_RAIDER = 20208;
    private static final int OL_MAHUM_MARKSMAN = 20209;
    private static final int OL_MAHUM_SERGEANT = 20210;
    private static final int OL_MAHUM_CAPTAIN = 20211;
    private static final int MARSH_STAKATO_WORKER = 20230;
    private static final int MARSH_STAKATO_SOLDIER = 20232;
    private static final int MARSH_STAKATO_DRONE = 20234;
    private static final int DELU_LIZARDMAN = 20251;
    private static final int DELU_LIZARDMAN_SCOUT = 20252;
    private static final int DELU_LIZARDMAN_WARRIOR = 20253;
    private static final int DELU_LIZARDMAN_HEADHUNTER = 27151;
    private static final int MARSH_STAKATO_MARQUESS = 27152;
    private static final int MIN_LEVEL = 25;
    
    public Q00333_HuntOfTheBlackLion() {
        super(333);
        this.addStartNpc(30735);
        this.addTalkId(new int[] { 30735, 30130, 30471, 30531, 30736, 30737 });
        this.addKillId(new int[] { 20157, 20160, 20171, 20197, 20198, 20200, 20201, 20207, 20208, 20209, 20210, 20211, 20230, 20232, 20234, 20251, 20252, 20253, 27151, 27152 });
        this.registerQuestItems(new int[] { 1369, 3440, 3441, 3442, 3443, 3457, 3458, 3459, 3460, 3461, 3462, 3463, 3464, 3465, 3466, 3671, 3672, 3673, 3674, 3675, 3676, 3677, 3848, 3849, 3850, 3851 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        final int chance = Rnd.get(100);
        final int chance2 = Rnd.get(100);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30735-04.htm": {
                if (qs.isCreated()) {
                    qs.startQuest();
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30735-05.html":
            case "30735-06.html":
            case "30735-07.html":
            case "30735-08.html":
            case "30735-09.html":
            case "30130-05.html":
            case "30531-05.html":
            case "30735-21.html":
            case "30735-24a.html":
            case "30735-25b.html":
            case "30736-06.html":
            case "30736-09.html":
            case "30737-07.html": {
                htmltext = event;
                break;
            }
            case "30735-10.html": {
                if (!hasQuestItems(player, 3671)) {
                    giveItems(player, 3671, 1L);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30735-11.html": {
                if (!hasQuestItems(player, 3672)) {
                    giveItems(player, 3672, 1L);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30735-12.html": {
                if (!hasQuestItems(player, 3673)) {
                    giveItems(player, 3673, 1L);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30735-13.html": {
                if (!hasQuestItems(player, 3674)) {
                    giveItems(player, 3674, 1L);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30735-16.html": {
                if (getQuestItemsCount(player, 3675) < 10L) {
                    htmltext = event;
                    break;
                }
                if (getQuestItemsCount(player, 3675) >= 10L && getQuestItemsCount(player, 3676) < 4L) {
                    giveItems(player, 3676, 1L);
                    if (chance < 25) {
                        giveItems(player, 1061, 20L);
                    }
                    else if (chance < 50) {
                        if (player.isInCategory(CategoryType.FIGHTER_GROUP)) {
                            giveItems(player, 1463, 100L);
                        }
                        else if (player.isInCategory(CategoryType.MAGE_GROUP)) {
                            giveItems(player, 2510, 50L);
                        }
                    }
                    else if (chance < 75) {
                        giveItems(player, 736, 20L);
                    }
                    else {
                        giveItems(player, 735, 3L);
                    }
                    takeItems(player, 3675, 10L);
                    htmltext = "30735-17a.html";
                    break;
                }
                if (getQuestItemsCount(player, 3675) >= 10L && getQuestItemsCount(player, 3676) >= 4L && getQuestItemsCount(player, 3676) <= 7L) {
                    giveItems(player, 3676, 1L);
                    if (chance < 25) {
                        giveItems(player, 1061, 25L);
                    }
                    else if (chance < 50) {
                        if (player.isInCategory(CategoryType.FIGHTER_GROUP)) {
                            giveItems(player, 1463, 200L);
                        }
                        else if (player.isInCategory(CategoryType.MAGE_GROUP)) {
                            giveItems(player, 2510, 100L);
                        }
                    }
                    else if (chance < 75) {
                        giveItems(player, 736, 20L);
                    }
                    else {
                        giveItems(player, 735, 3L);
                    }
                    takeItems(player, 3675, 10L);
                    htmltext = "30735-18b.html";
                    break;
                }
                if (getQuestItemsCount(player, 3675) >= 10L && getQuestItemsCount(player, 3676) >= 8L) {
                    takeItems(player, 3676, 8L);
                    if (chance < 25) {
                        giveItems(player, 1061, 50L);
                    }
                    else if (chance < 50) {
                        if (player.isInCategory(CategoryType.FIGHTER_GROUP)) {
                            giveItems(player, 1463, 400L);
                        }
                        else if (player.isInCategory(CategoryType.MAGE_GROUP)) {
                            giveItems(player, 2510, 200L);
                        }
                    }
                    else if (chance < 75) {
                        giveItems(player, 736, 30L);
                    }
                    else {
                        giveItems(player, 735, 4L);
                    }
                    takeItems(player, 3675, 10L);
                    htmltext = "30735-19b.html";
                    break;
                }
                break;
            }
            case "30735-20.html": {
                takeItems(player, 3671, -1L);
                takeItems(player, 3672, -1L);
                takeItems(player, 3673, -1L);
                takeItems(player, 3674, -1L);
                htmltext = event;
                break;
            }
            case "30735-26.html": {
                if (hasQuestItems(player, 1369)) {
                    this.giveAdena(player, 12400L, true);
                    qs.exitQuest(true, true);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30130-04.html": {
                if (hasQuestItems(player, 3461)) {
                    this.giveAdena(player, 30000L, true);
                    takeItems(player, 3461, 1L);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30471-03.html": {
                if (!hasQuestItems(player, new int[] { 3457, 3458, 3459, 3460 })) {
                    htmltext = event;
                    break;
                }
                if (Rnd.get(100) < 50) {
                    giveItems(player, 3461, 1L);
                    takeItems(player, 3457, 1L);
                    takeItems(player, 3458, 1L);
                    takeItems(player, 3459, 1L);
                    takeItems(player, 3460, 1L);
                    htmltext = "30471-04.html";
                    break;
                }
                takeItems(player, 3457, 1L);
                takeItems(player, 3458, 1L);
                takeItems(player, 3459, 1L);
                takeItems(player, 3460, 1L);
                htmltext = "30471-05.html";
                break;
            }
            case "30471-06.html": {
                if (!hasQuestItems(player, new int[] { 3462, 3463, 3464, 3465 })) {
                    htmltext = event;
                    break;
                }
                if (Rnd.get(100) < 50) {
                    giveItems(player, 3466, 1L);
                    takeItems(player, 3462, 1L);
                    takeItems(player, 3463, 1L);
                    takeItems(player, 3464, 1L);
                    takeItems(player, 3465, 1L);
                    htmltext = "30471-07.html";
                    break;
                }
                takeItems(player, 3462, 1L);
                takeItems(player, 3463, 1L);
                takeItems(player, 3464, 1L);
                takeItems(player, 3465, 1L);
                htmltext = "30471-08.html";
                break;
            }
            case "30531-04.html": {
                if (hasQuestItems(player, 3466)) {
                    this.giveAdena(player, 30000L, true);
                    takeItems(player, 3466, 1L);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30736-03.html": {
                if (getQuestItemsCount(player, 57) < 650L && getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) >= 1L) {
                    htmltext = event;
                    break;
                }
                if (getQuestItemsCount(player, 57) >= 650L && getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) >= 1L) {
                    takeItems(player, 57, 650L);
                    if (hasQuestItems(player, 3440)) {
                        takeItems(player, 3440, 1L);
                    }
                    else if (hasQuestItems(player, 3441)) {
                        takeItems(player, 3441, 1L);
                    }
                    else if (hasQuestItems(player, 3442)) {
                        takeItems(player, 3442, 1L);
                    }
                    else if (hasQuestItems(player, 3443)) {
                        takeItems(player, 3443, 1L);
                    }
                    if (chance < 40) {
                        if (chance2 < 33) {
                            giveItems(player, 3444, 1L);
                            htmltext = "30736-04a.html";
                            break;
                        }
                        if (chance2 < 66) {
                            giveItems(player, 3445, 1L);
                            htmltext = "30736-04b.html";
                            break;
                        }
                        giveItems(player, 3446, 1L);
                        htmltext = "30736-04c.html";
                        break;
                    }
                    else if (chance < 60) {
                        if (chance2 < 33) {
                            giveItems(player, 3447, 1L);
                            htmltext = "30736-04d.html";
                            break;
                        }
                        if (chance2 < 66) {
                            giveItems(player, 3448, 1L);
                            htmltext = "30736-04e.html";
                            break;
                        }
                        giveItems(player, 3449, 1L);
                        htmltext = "30736-04f.html";
                        break;
                    }
                    else if (chance < 70) {
                        if (chance2 < 33) {
                            giveItems(player, 3450, 1L);
                            htmltext = "30736-04g.html";
                            break;
                        }
                        if (chance2 < 66) {
                            giveItems(player, 3451, 1L);
                            htmltext = "30736-04h.html";
                            break;
                        }
                        giveItems(player, 3452, 1L);
                        htmltext = "30736-04i.html";
                        break;
                    }
                    else if (chance < 75) {
                        if (chance2 < 33) {
                            giveItems(player, 3453, 1L);
                            htmltext = "30736-04j.html";
                            break;
                        }
                        if (chance2 < 66) {
                            giveItems(player, 3454, 1L);
                            htmltext = "30736-04k.html";
                            break;
                        }
                        giveItems(player, 3455, 1L);
                        htmltext = "30736-04l.html";
                        break;
                    }
                    else {
                        if (chance < 76) {
                            giveItems(player, 3456, 1L);
                            htmltext = "30736-04m.html";
                            break;
                        }
                        if (Rnd.get(100) < 50) {
                            if (chance2 < 25) {
                                giveItems(player, 3457, 1L);
                            }
                            else if (chance2 < 50) {
                                giveItems(player, 3458, 1L);
                            }
                            else if (chance2 < 75) {
                                giveItems(player, 3459, 1L);
                            }
                            else {
                                giveItems(player, 3460, 1L);
                            }
                            htmltext = "30736-04n.html";
                            break;
                        }
                        if (chance2 < 25) {
                            giveItems(player, 3462, 1L);
                        }
                        else if (chance2 < 50) {
                            giveItems(player, 3463, 1L);
                        }
                        else if (chance2 < 75) {
                            giveItems(player, 3464, 1L);
                        }
                        else {
                            giveItems(player, 3465, 1L);
                        }
                        htmltext = "30736-04o.html";
                        break;
                    }
                }
                else {
                    if (getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) < 1L) {
                        htmltext = "30736-05.html";
                        break;
                    }
                    break;
                }
                break;
            }
            case "30736-07.html": {
                if (player.getAdena() < 200 + qs.getMemoState() * 200) {
                    htmltext = event;
                    break;
                }
                if (qs.getMemoState() * 100 > 200) {
                    htmltext = "30736-08.html";
                    break;
                }
                if (chance < 5) {
                    htmltext = "30736-08a.html";
                }
                else if (chance < 10) {
                    htmltext = "30736-08b.html";
                }
                else if (chance < 15) {
                    htmltext = "30736-08c.html";
                }
                else if (chance < 20) {
                    htmltext = "30736-08d.html";
                }
                else if (chance < 25) {
                    htmltext = "30736-08e.html";
                }
                else if (chance < 30) {
                    htmltext = "30736-08f.html";
                }
                else if (chance < 35) {
                    htmltext = "30736-08g.html";
                }
                else if (chance < 40) {
                    htmltext = "30736-08h.html";
                }
                else if (chance < 45) {
                    htmltext = "30736-08i.html";
                }
                else if (chance < 50) {
                    htmltext = "30736-08j.html";
                }
                else if (chance < 55) {
                    htmltext = "30736-08k.html";
                }
                else if (chance < 60) {
                    htmltext = "30736-08l.html";
                }
                else if (chance < 65) {
                    htmltext = "30736-08m.html";
                }
                else if (chance < 70) {
                    htmltext = "30736-08n.html";
                }
                else if (chance < 75) {
                    htmltext = "30736-08o.html";
                }
                else if (chance < 80) {
                    htmltext = "30736-08p.html";
                }
                else if (chance < 85) {
                    htmltext = "30736-08q.html";
                }
                else if (chance < 90) {
                    htmltext = "30736-08r.html";
                }
                else if (chance < 95) {
                    htmltext = "30736-08s.html";
                }
                else {
                    htmltext = "30736-08t.html";
                }
                takeItems(player, 57, (long)(200 + qs.getMemoState() * 200));
                qs.setMemoState(qs.getMemoState() + 1);
                break;
            }
            case "30737-06.html": {
                if (getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) < 1L) {
                    htmltext = event;
                    break;
                }
                if (hasQuestItems(player, 3440)) {
                    takeItems(player, 3440, 1L);
                }
                else if (hasQuestItems(player, 3441)) {
                    takeItems(player, 3441, 1L);
                }
                else if (hasQuestItems(player, 3442)) {
                    takeItems(player, 3442, 1L);
                }
                else if (hasQuestItems(player, 3443)) {
                    takeItems(player, 3443, 1L);
                }
                if (getQuestItemsCount(player, 3677) < 80L) {
                    giveItems(player, 3677, 1L);
                }
                else {
                    takeItems(player, 3677, 80L);
                }
                if (getQuestItemsCount(player, 3677) < 40L) {
                    this.giveAdena(player, 100L, true);
                    htmltext = "30737-03.html";
                    break;
                }
                if (getQuestItemsCount(player, 3677) >= 40L && getQuestItemsCount(player, 3677) < 80L) {
                    this.giveAdena(player, 200L, true);
                    htmltext = "30737-04.html";
                    break;
                }
                this.giveAdena(player, 300L, true);
                htmltext = "30737-05.html";
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getQuestState(killer, false);
        if (qs != null && qs.isStarted() && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, (WorldObject)npc, (WorldObject)killer, true)) {
            switch (npc.getId()) {
                case 20157: {
                    if (!hasQuestItems(killer, 3674)) {
                        break;
                    }
                    if (Rnd.get(100) < 55) {
                        giveItems(killer, 3851, 1L);
                    }
                    if (Rnd.get(100) < 12) {
                        giveItems(killer, 3443, 1L);
                    }
                    if (Rnd.get(100) < 2 && hasQuestItems(killer, 3674)) {
                        addSpawn(27152, (IPositionable)npc, true, 0L, false);
                        break;
                    }
                    break;
                }
                case 20160: {
                    if (!hasQuestItems(killer, 3671)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3848, 1L);
                    }
                    if (Rnd.get(100) < 11) {
                        giveItems(killer, 3440, 1L);
                        break;
                    }
                    break;
                }
                case 20171: {
                    if (!hasQuestItems(killer, 3671)) {
                        break;
                    }
                    if (Rnd.get(100) < 60) {
                        giveItems(killer, 3848, 1L);
                    }
                    if (Rnd.get(100) < 8) {
                        giveItems(killer, 3440, 1L);
                        break;
                    }
                    break;
                }
                case 20197: {
                    if (!hasQuestItems(killer, 3671)) {
                        break;
                    }
                    if (Rnd.get(100) < 60) {
                        giveItems(killer, 3848, 1L);
                    }
                    if (Rnd.get(100) < 9) {
                        giveItems(killer, 3440, 1L);
                        break;
                    }
                    break;
                }
                case 20198: {
                    if (!hasQuestItems(killer, 3671)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3848, 1L);
                    }
                    if (Rnd.get(100) < 12) {
                        giveItems(killer, 3440, 1L);
                        break;
                    }
                    break;
                }
                case 20200: {
                    if (!hasQuestItems(killer, 3671)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3848, 1L);
                    }
                    if (Rnd.get(100) < 13) {
                        giveItems(killer, 3440, 1L);
                        break;
                    }
                    break;
                }
                case 20201: {
                    if (!hasQuestItems(killer, 3671)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3848, 1L);
                    }
                    if (Rnd.get(100) < 15) {
                        giveItems(killer, 3440, 1L);
                        break;
                    }
                    break;
                }
                case 20207: {
                    if (!hasQuestItems(killer, 3672)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3849, 1L);
                    }
                    if (Rnd.get(100) < 9) {
                        giveItems(killer, 3441, 1L);
                        break;
                    }
                    break;
                }
                case 20208: {
                    if (!hasQuestItems(killer, 3672)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3849, 1L);
                    }
                    if (Rnd.get(100) < 10) {
                        giveItems(killer, 3441, 1L);
                        break;
                    }
                    break;
                }
                case 20209: {
                    if (!hasQuestItems(killer, 3672)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3849, 1L);
                    }
                    if (Rnd.get(100) < 11) {
                        giveItems(killer, 3441, 1L);
                        break;
                    }
                    break;
                }
                case 20210: {
                    if (!hasQuestItems(killer, 3672)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3849, 1L);
                    }
                    if (Rnd.get(100) < 12) {
                        giveItems(killer, 3441, 1L);
                        break;
                    }
                    break;
                }
                case 20211: {
                    if (!hasQuestItems(killer, 3672)) {
                        break;
                    }
                    if (Rnd.get(2) == 0) {
                        giveItems(killer, 3849, 1L);
                    }
                    if (Rnd.get(100) < 13) {
                        giveItems(killer, 3441, 1L);
                        break;
                    }
                    break;
                }
                case 20230: {
                    if (!hasQuestItems(killer, 3674)) {
                        break;
                    }
                    if (Rnd.get(100) < 60) {
                        giveItems(killer, 3851, 1L);
                    }
                    if (Rnd.get(100) < 13) {
                        giveItems(killer, 3443, 1L);
                    }
                    if (Rnd.get(100) < 2 && hasQuestItems(killer, 3674)) {
                        addSpawn(27152, (IPositionable)npc, true, 0L, false);
                        break;
                    }
                    break;
                }
                case 20232: {
                    if (!hasQuestItems(killer, 3674)) {
                        break;
                    }
                    if (Rnd.get(100) < 56) {
                        giveItems(killer, 3851, 1L);
                    }
                    if (Rnd.get(100) < 14) {
                        giveItems(killer, 3443, 1L);
                    }
                    if (Rnd.get(100) < 2 && hasQuestItems(killer, 3674)) {
                        addSpawn(27152, (IPositionable)npc, true, 0L, false);
                        break;
                    }
                    break;
                }
                case 20234: {
                    if (!hasQuestItems(killer, 3674)) {
                        break;
                    }
                    if (Rnd.get(100) < 60) {
                        giveItems(killer, 3851, 1L);
                    }
                    if (Rnd.get(100) < 15) {
                        giveItems(killer, 3443, 1L);
                    }
                    if (Rnd.get(100) < 2 && hasQuestItems(killer, 3674)) {
                        addSpawn(27152, (IPositionable)npc, true, 0L, false);
                        break;
                    }
                    break;
                }
                case 20251:
                case 20252: {
                    if (hasQuestItems(killer, 3673)) {
                        if (Rnd.get(2) == 0) {
                            giveItems(killer, 3850, 1L);
                        }
                        if (Rnd.get(100) < 14) {
                            giveItems(killer, 3442, 1L);
                        }
                    }
                    if (Rnd.get(100) < 3 && hasQuestItems(killer, 3673)) {
                        addSpawn(27151, (IPositionable)npc, true, 0L, false);
                        addSpawn(27151, (IPositionable)npc, true, 0L, false);
                        break;
                    }
                    break;
                }
                case 20253: {
                    if (hasQuestItems(killer, 3673)) {
                        if (Rnd.get(2) == 0) {
                            giveItems(killer, 3850, 1L);
                        }
                        if (Rnd.get(100) < 15) {
                            giveItems(killer, 3442, 1L);
                        }
                    }
                    if (Rnd.get(100) < 3 && hasQuestItems(killer, 3673)) {
                        addSpawn(27151, (IPositionable)npc, true, 0L, false);
                        addSpawn(27151, (IPositionable)npc, true, 0L, false);
                        break;
                    }
                    break;
                }
                case 27151: {
                    if (hasQuestItems(killer, 3673)) {
                        giveItems(killer, 3850, 4L);
                        playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
                        break;
                    }
                    break;
                }
                case 27152: {
                    if (hasQuestItems(killer, 3674)) {
                        giveItems(killer, 3851, 8L);
                        playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
                        break;
                    }
                    break;
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (qs.isCreated()) {
            if (npc.getId() == 30735) {
                if (player.getLevel() < 25) {
                    htmltext = "30735-01.htm";
                }
                else if (!hasQuestItems(player, 1369)) {
                    htmltext = "30735-02.htm";
                }
                else {
                    htmltext = "30735-03.htm";
                }
            }
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 30735: {
                    if (getQuestItemsCount(player, 3671) + getQuestItemsCount(player, 3672) + getQuestItemsCount(player, 3673) + getQuestItemsCount(player, 3674) == 0L) {
                        htmltext = "30735-14.html";
                        break;
                    }
                    if (getQuestItemsCount(player, 3671) + getQuestItemsCount(player, 3672) + getQuestItemsCount(player, 3673) + getQuestItemsCount(player, 3674) == 1L && getQuestItemsCount(player, 3848) + getQuestItemsCount(player, 3849) + getQuestItemsCount(player, 3850) + getQuestItemsCount(player, 3851) < 1L && getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) < 1L) {
                        htmltext = "30735-15.html";
                        break;
                    }
                    if (getQuestItemsCount(player, 3671) + getQuestItemsCount(player, 3672) + getQuestItemsCount(player, 3673) + getQuestItemsCount(player, 3674) == 1L && getQuestItemsCount(player, 3848) + getQuestItemsCount(player, 3849) + getQuestItemsCount(player, 3850) + getQuestItemsCount(player, 3851) < 1L && getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) >= 1L) {
                        htmltext = "30735-15a.html";
                        break;
                    }
                    if (getQuestItemsCount(player, 3671) + getQuestItemsCount(player, 3672) + getQuestItemsCount(player, 3673) + getQuestItemsCount(player, 3674) == 1L && getQuestItemsCount(player, 3848) + getQuestItemsCount(player, 3849) + getQuestItemsCount(player, 3850) + getQuestItemsCount(player, 3851) >= 1L && getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) == 0L) {
                        final long itemcount = getQuestItemsCount(player, 3848) + getQuestItemsCount(player, 3849) + getQuestItemsCount(player, 3850) + getQuestItemsCount(player, 3851);
                        if (itemcount >= 20L) {
                            if (itemcount < 50L) {
                                giveItems(player, 3675, 1L);
                            }
                            else if (itemcount < 100L) {
                                giveItems(player, 3675, 2L);
                            }
                            else {
                                giveItems(player, 3675, 3L);
                            }
                        }
                        final long ash = getQuestItemsCount(player, 3848);
                        final long insignia = getQuestItemsCount(player, 3849);
                        final long fang = getQuestItemsCount(player, 3850);
                        final long talon = getQuestItemsCount(player, 3851);
                        this.giveAdena(player, ash * 10L + insignia * 10L + (fang + 7L + talon * 8L), true);
                        takeItems(player, 3848, -1L);
                        takeItems(player, 3849, -1L);
                        takeItems(player, 3850, -1L);
                        takeItems(player, 3851, -1L);
                        qs.setMemoState(0);
                        htmltext = "30735-22.html";
                        break;
                    }
                    if (getQuestItemsCount(player, 3671) + getQuestItemsCount(player, 3672) + getQuestItemsCount(player, 3673) + getQuestItemsCount(player, 3674) == 1L && getQuestItemsCount(player, 3848) + getQuestItemsCount(player, 3849) + getQuestItemsCount(player, 3850) + getQuestItemsCount(player, 3851) >= 1L && getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) >= 1L) {
                        final long itemcount = getQuestItemsCount(player, 3848) + getQuestItemsCount(player, 3849) + getQuestItemsCount(player, 3850) + getQuestItemsCount(player, 3851);
                        if (itemcount >= 20L) {
                            if (itemcount < 50L) {
                                giveItems(player, 3675, 1L);
                            }
                            else if (itemcount < 100L) {
                                giveItems(player, 3675, 2L);
                            }
                            else {
                                giveItems(player, 3675, 3L);
                            }
                        }
                        this.giveAdena(player, getQuestItemsCount(player, 3848) * 10L, true);
                        this.giveAdena(player, getQuestItemsCount(player, 3849) * 10L, true);
                        this.giveAdena(player, getQuestItemsCount(player, 3850) * 7L, true);
                        this.giveAdena(player, getQuestItemsCount(player, 3851) * 8L, true);
                        takeItems(player, 3848, -1L);
                        takeItems(player, 3849, -1L);
                        takeItems(player, 3850, -1L);
                        takeItems(player, 3851, -1L);
                        qs.setMemoState(0);
                        htmltext = "30735-23.html";
                        break;
                    }
                    break;
                }
                case 30130: {
                    if (hasQuestItems(player, 3461)) {
                        htmltext = "30130-03.html";
                        break;
                    }
                    if (getQuestItemsCount(player, 3457) + getQuestItemsCount(player, 3458) + getQuestItemsCount(player, 3459) + getQuestItemsCount(player, 3460) >= 1L) {
                        htmltext = "30130-02.html";
                        break;
                    }
                    htmltext = "30130-01.html";
                    break;
                }
                case 30471: {
                    if (getQuestItemsCount(player, 3457) + getQuestItemsCount(player, 3458) + getQuestItemsCount(player, 3459) + getQuestItemsCount(player, 3460) >= 1L || getQuestItemsCount(player, 3462) + getQuestItemsCount(player, 3463) + getQuestItemsCount(player, 3464) + getQuestItemsCount(player, 3465) >= 1L) {
                        htmltext = "30471-02.html";
                        break;
                    }
                    htmltext = "30471-01.html";
                    break;
                }
                case 30531: {
                    if (hasQuestItems(player, 3466)) {
                        htmltext = "30531-03.html";
                        break;
                    }
                    if (getQuestItemsCount(player, 3462) + getQuestItemsCount(player, 3463) + getQuestItemsCount(player, 3464) + getQuestItemsCount(player, 3465) >= 1L) {
                        htmltext = "30531-02.html";
                        break;
                    }
                    htmltext = "30531-01.html";
                    break;
                }
                case 30736: {
                    if (getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) >= 1L) {
                        htmltext = "30736-02.html";
                        break;
                    }
                    htmltext = "30736-01.html";
                    break;
                }
                case 30737: {
                    if (getQuestItemsCount(player, 3440) + getQuestItemsCount(player, 3441) + getQuestItemsCount(player, 3442) + getQuestItemsCount(player, 3443) >= 1L) {
                        htmltext = "30737-02.html";
                        break;
                    }
                    htmltext = "30737-01.html";
                    break;
                }
            }
        }
        return htmltext;
    }
}
