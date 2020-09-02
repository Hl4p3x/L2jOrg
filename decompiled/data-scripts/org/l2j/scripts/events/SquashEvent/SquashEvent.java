// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.events.SquashEvent;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.quest.LongTimeEvent;

public class SquashEvent extends LongTimeEvent
{
    private static final int MANAGER = 31860;
    private static final int NECTAR_SKILL = 2005;
    private static final IntSet SQUASH_LIST;
    private static final IntSet LARGE_SQUASH_LIST;
    private static final IntSet CHRONO_LIST;
    private static final String[] _NOCHRONO_TEXT;
    private static final String[] _CHRONO_TEXT;
    private static final String[] _NECTAR_TEXT;
    private static final int Atuba_Hammer = 187;
    private static final int Gastraphetes = 278;
    private static final int Maingauche = 224;
    private static final int Staff_of_Life = 189;
    private static final int Sword_of_Revolution = 129;
    private static final int War_Pick = 294;
    private static final int Battle_Axe = 160;
    private static final int Crystal_Staff = 192;
    private static final int Crystallized_Ice_Bow = 281;
    private static final int Flamberge = 71;
    private static final int Orcish_Glaive = 298;
    private static final int Stick_of_Faith = 193;
    private static final int Stormbringer = 72;
    private static final int Berserker_Blade = 5286;
    private static final int Dark_Screamer = 233;
    private static final int Eminence_Bow = 286;
    private static final int Fisted_Blade = 265;
    private static final int Homunkulus_Sword = 84;
    private static final int Poleaxe = 95;
    private static final int Sage_Staff = 200;
    private static final int Sword_of_Nightmare = 134;
    private static final int Divine_Gloves = 2463;
    private static final int Divine_Stockings = 473;
    private static final int Divine_Tunic = 442;
    private static final int Drake_Leather_Armor = 401;
    private static final int Drake_Leather_Boots = 2437;
    private static final int Full_Plate_Armor = 356;
    private static final int Full_Plate_Helmet = 2414;
    private static final int Full_Plate_Shield = 2497;
    private static final int Avadon_Robe = 2406;
    private static final int Blue_Wolf_Breastplate = 358;
    private static final int Blue_Wolf_Gaiters = 2380;
    private static final int Leather_Armor_of_Doom = 2392;
    private static final int Sealed_Avadon_Boots = 600;
    private static final int Sealed_Avadon_Circlet = 2415;
    private static final int Sealed_Avadon_Gloves = 2464;
    private static final int Sealed_Blue_Wolf_Boots = 2439;
    private static final int Sealed_Blue_Wolf_Gloves = 2487;
    private static final int Sealed_Blue_Wolf_Helmet = 2416;
    private static final int Sealed_Doom_Boots = 601;
    private static final int Sealed_Doom_Gloves = 2475;
    private static final int Sealed_Doom_Helmet = 2417;
    private static final int Class_Buff_Scroll_1st = 29011;
    private static final int Angel_Cat_Blessing_Chest = 29584;
    private static final int Major_Healing_Potion = 1539;
    private static final int Rice_Cake_of_Fighting_Spirit = 49080;
    private static final int XP_SP_Scroll_Normal = 29648;
    private static final int XP_SP_Scroll_Medium = 29519;
    private static final int Greater_CP_Potion = 5592;
    private static final int Quick_Healing_Potion = 1540;
    private static final int Class_Buff_Scroll_2nd = 29698;
    private static final int Scroll_Enchant_Armor_D = 956;
    private static final int Scroll_Enchant_Weapon_D = 955;
    private static final int Scroll_Enchant_Armor_C = 952;
    private static final int Scroll_Enchant_Weapon_C = 951;
    private static final int Blessed_Scroll_Enchant_Armor_C = 29022;
    private static final int Blessed_Scroll_Enchant_Weapon_C = 29021;
    private static final int Blessed_Scroll_Enchant_Armor_D = 29020;
    private static final int Blessed_Scroll_Enchant_Weapon_D = 29019;
    private static final int Special_Pirate_Fruit = 49518;
    private static final int XP_SP_Scroll_High = 29010;
    private static final int Blessed_Scroll_of_Escape = 1538;
    private static final int Blessed_Scroll_of_Resurrection = 3936;
    private static final int Rice_Cake_of_Flaming_Fighting_Spirit = 49081;
    private static final int Scroll_Acumen = 3929;
    private static final int Scroll_Berserker_Spirit = 49435;
    private static final int Scroll_Blessed_Body = 29690;
    private static final int Scroll_Death_Whisper = 3927;
    private static final int Scroll_Guidance = 3926;
    private static final int Scroll_Haste = 3930;
    private static final int Scroll_Magic_Barrier = 29689;
    private static final int Scroll_Mana_Regeneration = 4218;
    private static final int Scroll_Regeneration = 29688;
    private static final int Scroll_Dance_of_Fire = 29014;
    private static final int Scroll_Hunter_Song = 29013;
    private static final int Recipe_Atuba_Hammer = 2287;
    private static final int Recipe_Gastraphetes = 2267;
    private static final int Recipe_Maingauche = 2276;
    private static final int Recipe_Staff_of_Life = 2289;
    private static final int Recipe_Sword_of_Revolution = 2272;
    private static final int Recipe_Battle_Axe = 2301;
    private static final int Recipe_Blue_Wolf_Gaiters = 4982;
    private static final int Recipe_Crystal_Staff = 2305;
    private static final int Recipe_Crystallized_Ice_Bow = 2312;
    private static final int Recipe_Divine_Gloves = 3017;
    private static final int Recipe_Divine_Stockings = 2234;
    private static final int Recipe_Flamberge = 2297;
    private static final int Recipe_Full_Plate_Helmet = 3012;
    private static final int Recipe_Full_Plate_Shield = 3019;
    private static final int Recipe_Orcish_Glaive = 2317;
    private static final int Recipe_Sealed_Avadon_Boots = 4959;
    private static final int Recipe_Sealed_Avadon_Gloves = 4953;
    private static final int Recipe_Sealed_Blue_Wolf_Boots = 4992;
    private static final int Recipe_Sealed_Blue_Wolf_Gloves = 4998;
    private static final int Recipe_Stick_of_Faith = 2306;
    private static final int Recipe_Stormbringer = 2298;
    private static final int Recipe_Avadon_Robe = 4951;
    private static final int Recipe_Berserker_Blade = 5436;
    private static final int Recipe_Blue_Wolf_Breastplate = 4981;
    private static final int Recipe_Dark_Screamer = 2345;
    private static final int Recipe_Divine_Tunic = 2233;
    private static final int Recipe_Eminence_Bow = 2359;
    private static final int Recipe_Fisted_Blade = 2346;
    private static final int Recipe_Full_Plate_Armor = 2231;
    private static final int Recipe_Homunkulus_Sword = 2330;
    private static final int Recipe_Leather_Armor_of_Doom = 4985;
    private static final int Recipe_Poleaxe = 2331;
    private static final int Recipe_Sage_Staff = 2341;
    private static final int Recipe_Sealed_Avadon_Circlet = 4952;
    private static final int Recipe_Sealed_Blue_Wolf_Helmet = 4990;
    private static final int Recipe_Sealed_Doom_Helmet = 4991;
    private static final int Recipe_Sword_of_Nightmare = 2333;
    private static final int Animal_Bone = 1872;
    private static final int Coal = 1870;
    private static final int Varnish = 1865;
    private static final int Stone_of_Purity = 1875;
    private static final int Steel = 1880;
    private static final int Mithril_Ore = 1876;
    private static final int Leather = 1882;
    private static final int Cokes = 1879;
    private static final int Coarse_Bone_Powder = 1881;
    private static final int Adamantite_Nugget = 1877;
    private static final int Asofe = 4043;
    private static final int Mold_Glue = 4039;
    private static final int Oriharukon_Ore = 1874;
    private static final int Steel_Mold = 1883;
    private static final int Synthetic_Braid = 1889;
    private static final int Synthetic_Cokes = 1888;
    private static final int Varnish_of_Purity = 1887;
    private static final int High_grade_Suede = 1885;
    private static final int Enria = 4042;
    private static final int Mithril_Alloy = 1890;
    private static final int Mold_Hardener = 4041;
    private static final int Mold_Lubricant = 4040;
    private static final int Crystal_D = 1458;
    private static final int Crystal_C = 1459;
    private static final int Crystal_B = 1460;
    private static final int Silver_Mold = 1886;
    private static final int Oriharukon = 1893;
    private static final int Atuba_Hammer_Head = 2049;
    private static final int Gastraphetes_Shaft = 2029;
    private static final int Maingauche_Edge = 2038;
    private static final int Staff_of_Life_Shaft = 2051;
    private static final int Sword_of_Revolution_Blade = 2034;
    private static final int Stormbringer_Blade = 2060;
    private static final int Stick_of_Faith_Shaft = 2068;
    private static final int Sealed_Blue_Wolf_Glove_Fabric = 4096;
    private static final int Sealed_Blue_Wolf_Boot_Design = 4090;
    private static final int Sealed_Avadon_Glove_Fragment = 4073;
    private static final int Sealed_Avadon_Boot_Design = 4098;
    private static final int Orcish_Glaive_Blade = 2075;
    private static final int Flamberge_Blade = 2059;
    private static final int Crystallized_Ice_Bow_Shaft = 2074;
    private static final int Crystal_Staff_Head = 2067;
    private static final int Blue_Wolf_Gaiter_Material = 4080;
    private static final int Battle_Axe_Head = 2063;
    private static final int Avadon_Robe_Fabric = 4071;
    private static final int Berserker_Blade_Edge = 5530;
    private static final int Blue_Wolf_Breastplate_Part = 4078;
    private static final int Dark_Screamer_Edge = 2107;
    private static final int Divine_Tunic_Fabric = 1988;
    private static final int Eminence_Bow_Shaft = 2121;
    private static final int Fisted_Blade_Piece = 2108;
    private static final int Full_Plate_Armor_Temper = 1986;
    private static final int Poleaxe_Blade = 2093;
    private static final int Sage_Staff_Head = 2109;
    private static final int Sealed_Avadon_Circlet_Pattern = 4072;
    private static final int Sealed_Blue_Wolf_Helmet_Design = 4088;
    private static final int Sealed_Doom_Helmet_Design = 4089;
    private static final int Sword_of_Nightmare_Blade = 2095;
    private static final int[][] DROPLIST;
    
    private SquashEvent() {
        this.addAttackId((IntCollection)SquashEvent.SQUASH_LIST);
        this.addKillId((IntCollection)SquashEvent.SQUASH_LIST);
        this.addSpawnId((IntCollection)SquashEvent.SQUASH_LIST);
        this.addSpawnId((IntCollection)SquashEvent.LARGE_SQUASH_LIST);
        this.addSkillSeeId((IntCollection)SquashEvent.SQUASH_LIST);
        this.addStartNpc(31860);
        this.addFirstTalkId(31860);
        this.addTalkId(31860);
    }
    
    public String onSpawn(final Npc npc) {
        npc.setIsImmobilized(true);
        npc.disableCoreAI(true);
        if (SquashEvent.LARGE_SQUASH_LIST.contains(npc.getId())) {
            npc.setIsInvul(true);
        }
        return null;
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isPet) {
        if (SquashEvent.LARGE_SQUASH_LIST.contains(npc.getId())) {
            if (attacker.getActiveWeaponItem() != null && SquashEvent.CHRONO_LIST.contains(attacker.getActiveWeaponItem().getId())) {
                this.ChronoText(npc);
                npc.setIsInvul(false);
                npc.getStatus().reduceHp(10.0, (Creature)attacker);
            }
            else {
                this.noChronoText(npc);
                npc.setIsInvul(true);
            }
        }
        return super.onAttack(npc, attacker, damage, isPet);
    }
    
    public String onSkillSee(final Npc npc, final Player caster, final Skill skill, final WorldObject[] targets, final boolean isPet) {
        if (SquashEvent.SQUASH_LIST.contains(npc.getId()) && skill.getId() == 2005) {
            switch (npc.getId()) {
                case 12774: {
                    this.randomSpawn(13016, 12775, 12776, npc, true);
                    break;
                }
                case 12777: {
                    this.randomSpawn(13017, 12778, 12779, npc, true);
                    break;
                }
            }
        }
        return super.onSkillSee(npc, caster, skill, targets, isPet);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isPet) {
        if (SquashEvent.SQUASH_LIST.contains(npc.getId())) {
            dropItem(npc, killer);
        }
        return super.onKill(npc, killer, isPet);
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    private static final void dropItem(final Npc mob, final Player player) {
        final int npcId = mob.getId();
        for (final int[] drop : SquashEvent.DROPLIST) {
            if (npcId == drop[0]) {
                if (!Rnd.nextBoolean()) {
                    if (Rnd.get(100) < drop[2]) {
                        if (ItemEngine.getInstance().getTemplate(drop[1]).getCrystalType() != CrystalType.NONE) {
                            ((Monster)mob).dropItem((Creature)player, drop[1], 1L);
                            break;
                        }
                        ((Monster)mob).dropItem((Creature)player, drop[1], (long)Rnd.get(1, 3));
                        if (Rnd.nextBoolean()) {
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void randomSpawn(final int low, final int medium, final int high, final Npc npc, final boolean delete) {
        final int _random = Rnd.get(100);
        if (_random < 5) {
            this.spawnNext(low, npc);
        }
        if (_random < 10) {
            this.spawnNext(medium, npc);
        }
        else if (_random < 30) {
            this.spawnNext(high, npc);
        }
        else {
            this.nectarText(npc);
        }
    }
    
    private void ChronoText(final Npc npc) {
        if (Rnd.get(100) < 20) {
            npc.broadcastPacket((ServerPacket)new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), SquashEvent._CHRONO_TEXT[Rnd.get(SquashEvent._CHRONO_TEXT.length)]));
        }
    }
    
    private void noChronoText(final Npc npc) {
        if (Rnd.get(100) < 20) {
            npc.broadcastPacket((ServerPacket)new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), SquashEvent._NOCHRONO_TEXT[Rnd.get(SquashEvent._NOCHRONO_TEXT.length)]));
        }
    }
    
    private void nectarText(final Npc npc) {
        if (Rnd.get(100) < 30) {
            npc.broadcastPacket((ServerPacket)new CreatureSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getName(), SquashEvent._NECTAR_TEXT[Rnd.get(SquashEvent._NECTAR_TEXT.length)]));
        }
    }
    
    private void spawnNext(final int npcId, final Npc npc) {
        addSpawn(npcId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 60000L);
        npc.deleteMe();
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new SquashEvent();
    }
    
    static {
        SQUASH_LIST = IntSet.of(12774, 12775, new int[] { 12776, 12777, 12778, 12779, 13016, 13017 });
        LARGE_SQUASH_LIST = IntSet.of(12778, 12779, new int[] { 13016, 13017 });
        CHRONO_LIST = IntSet.of(4202, 5133, new int[] { 5817, 7058, 8350 });
        _NOCHRONO_TEXT = new String[] { "You cannot kill me without Chrono", "Hehe...keep trying...", "Nice try...", "Tired ?", "Go go ! haha..." };
        _CHRONO_TEXT = new String[] { "Arghh... Chrono weapon...", "My end is coming...", "Please leave me!", "Heeellpppp...", "Somebody help me please..." };
        _NECTAR_TEXT = new String[] { "Yummie... Nectar...", "Plase give me more...", "Hmmm.. More.. I need more...", "I would like you more, if you give me more...", "Hmmmmmmm...", "My favourite..." };
        DROPLIST = new int[][] { { 12775, 29011, 70 }, { 12775, 29584, 60 }, { 12775, 1539, 70 }, { 12775, 49080, 60 }, { 12775, 29648, 50 }, { 12775, 29519, 40 }, { 12775, 1880, 50 }, { 12775, 1877, 50 }, { 12775, 1876, 50 }, { 12775, 1882, 50 }, { 12775, 1879, 50 }, { 12775, 1881, 50 }, { 12775, 1875, 50 }, { 12775, 2060, 50 }, { 12775, 2068, 50 }, { 12775, 4096, 50 }, { 12775, 4090, 50 }, { 12775, 4073, 50 }, { 12775, 4098, 50 }, { 12775, 2075, 50 }, { 12775, 2059, 50 }, { 12775, 2074, 50 }, { 12775, 2067, 50 }, { 12775, 4080, 50 }, { 12775, 2063, 50 }, { 12775, 2301, 50 }, { 12775, 4982, 50 }, { 12775, 2305, 50 }, { 12775, 2312, 50 }, { 12775, 3017, 50 }, { 12775, 2234, 50 }, { 12775, 2297, 50 }, { 12775, 3012, 50 }, { 12775, 3019, 50 }, { 12775, 2317, 50 }, { 12775, 4959, 50 }, { 12775, 4953, 50 }, { 12775, 4992, 50 }, { 12775, 4998, 50 }, { 12775, 2306, 50 }, { 12775, 2298, 50 }, { 12776, 29011, 70 }, { 12776, 29584, 60 }, { 12776, 1539, 70 }, { 12776, 49080, 60 }, { 12776, 29648, 50 }, { 12776, 1870, 50 }, { 12776, 1872, 50 }, { 12776, 1865, 50 }, { 12776, 2287, 50 }, { 12776, 2267, 50 }, { 12776, 2276, 50 }, { 12776, 2289, 50 }, { 12776, 2272, 50 }, { 12776, 2049, 50 }, { 12776, 2029, 50 }, { 12776, 2038, 50 }, { 12776, 2051, 50 }, { 12776, 2034, 50 }, { 12778, 160, 5 }, { 12778, 192, 5 }, { 12778, 281, 5 }, { 12778, 71, 5 }, { 12778, 298, 5 }, { 12778, 193, 5 }, { 12778, 72, 5 }, { 12778, 2463, 5 }, { 12778, 473, 5 }, { 12778, 442, 5 }, { 12778, 401, 5 }, { 12778, 2437, 5 }, { 12778, 356, 5 }, { 12778, 2414, 5 }, { 12778, 2497, 5 }, { 12778, 29698, 50 }, { 12778, 29584, 50 }, { 12778, 1538, 50 }, { 12778, 3936, 50 }, { 12778, 5592, 50 }, { 12778, 1540, 50 }, { 12778, 49081, 50 }, { 12778, 49518, 50 }, { 12778, 29010, 50 }, { 12778, 29519, 50 }, { 12778, 1459, 50 }, { 12778, 952, 50 }, { 12778, 951, 50 }, { 12778, 29014, 70 }, { 12778, 29013, 70 }, { 12778, 1890, 50 }, { 12778, 4041, 50 }, { 12778, 1893, 50 }, { 12778, 1886, 50 }, { 12779, 187, 20 }, { 12779, 278, 20 }, { 12779, 224, 20 }, { 12779, 189, 20 }, { 12779, 129, 20 }, { 12779, 294, 20 }, { 12779, 29011, 50 }, { 12779, 29698, 50 }, { 12779, 29584, 50 }, { 12779, 5592, 50 }, { 12779, 49080, 50 }, { 12779, 49518, 50 }, { 12779, 29010, 50 }, { 12779, 29519, 50 }, { 12779, 1458, 50 }, { 12779, 956, 50 }, { 12779, 955, 50 }, { 12779, 3929, 70 }, { 12779, 49435, 70 }, { 12779, 29690, 70 }, { 12779, 3927, 70 }, { 12779, 3926, 70 }, { 12779, 3930, 70 }, { 12779, 29689, 70 }, { 12779, 4218, 70 }, { 12779, 29688, 70 }, { 12779, 4042, 50 }, { 12779, 1890, 50 }, { 12779, 4041, 50 }, { 12779, 4040, 50 }, { 12779, 1886, 50 }, { 12779, 1887, 50 }, { 13016, 29011, 70 }, { 13016, 29584, 60 }, { 13016, 5592, 60 }, { 13016, 1540, 60 }, { 13016, 49080, 60 }, { 13016, 1877, 50 }, { 13016, 4043, 50 }, { 13016, 1881, 50 }, { 13016, 1879, 50 }, { 13016, 1885, 50 }, { 13016, 1876, 50 }, { 13016, 4039, 50 }, { 13016, 1874, 50 }, { 13016, 1880, 50 }, { 13016, 1883, 50 }, { 13016, 1875, 50 }, { 13016, 1889, 50 }, { 13016, 1888, 50 }, { 13016, 1887, 50 }, { 13016, 4071, 50 }, { 13016, 5530, 50 }, { 13016, 4078, 50 }, { 13016, 2107, 50 }, { 13016, 1988, 50 }, { 13016, 2121, 50 }, { 13016, 2108, 50 }, { 13016, 1986, 50 }, { 13016, 2093, 50 }, { 13016, 2109, 50 }, { 13016, 4072, 50 }, { 13016, 4088, 50 }, { 13016, 4089, 50 }, { 13016, 2095, 50 }, { 13016, 4951, 50 }, { 13016, 5436, 50 }, { 13016, 4981, 50 }, { 13016, 2345, 50 }, { 13016, 2233, 50 }, { 13016, 2359, 50 }, { 13016, 2346, 50 }, { 13016, 2231, 50 }, { 13016, 2330, 50 }, { 13016, 4985, 50 }, { 13016, 2331, 50 }, { 13016, 2341, 50 }, { 13016, 4952, 50 }, { 13016, 4990, 50 }, { 13016, 4991, 50 }, { 13016, 2333, 50 }, { 13017, 5286, 5 }, { 13017, 233, 5 }, { 13017, 286, 5 }, { 13017, 265, 5 }, { 13017, 84, 5 }, { 13017, 95, 5 }, { 13017, 200, 5 }, { 13017, 134, 5 }, { 13017, 2406, 5 }, { 13017, 358, 5 }, { 13017, 2380, 5 }, { 13017, 2392, 5 }, { 13017, 600, 10 }, { 13017, 2415, 10 }, { 13017, 2464, 10 }, { 13017, 2439, 10 }, { 13017, 2487, 10 }, { 13017, 2416, 10 }, { 13017, 601, 10 }, { 13017, 2475, 10 }, { 13017, 2417, 10 }, { 13017, 29698, 50 }, { 13017, 29584, 50 }, { 13017, 1538, 50 }, { 13017, 3936, 50 }, { 13017, 29022, 30 }, { 13017, 29020, 30 }, { 13017, 29021, 20 }, { 13017, 29019, 20 }, { 13017, 1460, 50 }, { 13017, 1459, 50 }, { 13017, 5592, 50 }, { 13017, 1539, 50 }, { 13017, 1540, 70 }, { 13017, 49081, 60 }, { 13017, 29014, 60 }, { 13017, 29013, 60 }, { 13017, 952, 50 }, { 13017, 951, 40 }, { 13017, 49518, 60 }, { 13017, 29010, 60 }, { 13017, 29519, 60 } };
    }
}
