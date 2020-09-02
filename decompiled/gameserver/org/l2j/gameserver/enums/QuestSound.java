// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import java.util.HashMap;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import java.util.Map;

public enum QuestSound
{
    ITEMSOUND_QUEST_ACCEPT(new PlaySound("ItemSound.quest_accept")), 
    ITEMSOUND_QUEST_MIDDLE(new PlaySound("ItemSound.quest_middle")), 
    ITEMSOUND_QUEST_FINISH(new PlaySound("ItemSound.quest_finish")), 
    ITEMSOUND_QUEST_ITEMGET(new PlaySound("ItemSound.quest_itemget")), 
    ITEMSOUND_QUEST_TUTORIAL(new PlaySound("ItemSound.quest_tutorial")), 
    ITEMSOUND_QUEST_GIVEUP(new PlaySound("ItemSound.quest_giveup")), 
    ITEMSOUND_QUEST_BEFORE_BATTLE(new PlaySound("ItemSound.quest_before_battle")), 
    ITEMSOUND_QUEST_JACKPOT(new PlaySound("ItemSound.quest_jackpot")), 
    ITEMSOUND_QUEST_FANFARE_1(new PlaySound("ItemSound.quest_fanfare_1")), 
    ITEMSOUND_QUEST_FANFARE_2(new PlaySound("ItemSound.quest_fanfare_2")), 
    ITEMSOUND_QUEST_FANFARE_MIDDLE(new PlaySound("ItemSound.quest_fanfare_middle")), 
    ITEMSOUND_ARMOR_WOOD(new PlaySound("ItemSound.armor_wood_3")), 
    ITEMSOUND_ARMOR_CLOTH(new PlaySound("ItemSound.item_drop_equip_armor_cloth")), 
    AMDSOUND_ED_CHIMES(new PlaySound("AmdSound.ed_chimes_05")), 
    HORROR_01(new PlaySound("horror_01")), 
    AMBSOUND_HORROR_01(new PlaySound("AmbSound.dd_horror_01")), 
    AMBSOUND_HORROR_03(new PlaySound("AmbSound.d_horror_03")), 
    AMBSOUND_HORROR_15(new PlaySound("AmbSound.d_horror_15")), 
    ITEMSOUND_ARMOR_LEATHER(new PlaySound("ItemSound.itemdrop_armor_leather")), 
    ITEMSOUND_WEAPON_SPEAR(new PlaySound("ItemSound.itemdrop_weapon_spear")), 
    AMBSOUND_MT_CREAK(new PlaySound("AmbSound.mt_creak01")), 
    AMBSOUND_EG_DRON(new PlaySound("AmbSound.eg_dron_02")), 
    SKILLSOUND_HORROR_02(new PlaySound("SkillSound5.horror_02")), 
    CHRSOUND_MHFIGHTER_CRY(new PlaySound("ChrSound.MHFighter_cry")), 
    AMDSOUND_WIND_LOOT(new PlaySound("AmdSound.d_wind_loot_02")), 
    INTERFACESOUND_CHARSTAT_OPEN(new PlaySound("InterfaceSound.charstat_open_01")), 
    AMDSOUND_HORROR_02(new PlaySound("AmdSound.dd_horror_02")), 
    CHRSOUND_FDELF_CRY(new PlaySound("ChrSound.FDElf_Cry")), 
    AMBSOUND_WINGFLAP(new PlaySound("AmbSound.t_wingflap_04")), 
    AMBSOUND_THUNDER(new PlaySound("AmbSound.thunder_02")), 
    AMBSOUND_DRONE(new PlaySound("AmbSound.ed_drone_02")), 
    AMBSOUND_CRYSTAL_LOOP(new PlaySound("AmbSound.cd_crystal_loop")), 
    AMBSOUND_PERCUSSION_01(new PlaySound("AmbSound.dt_percussion_01")), 
    AMBSOUND_PERCUSSION_02(new PlaySound("AmbSound.ac_percussion_02")), 
    ITEMSOUND_BROKEN_KEY(new PlaySound("ItemSound2.broken_key")), 
    ITEMSOUND_SIREN(new PlaySound("ItemSound3.sys_siren")), 
    ITEMSOUND_ENCHANT_SUCCESS(new PlaySound("ItemSound3.sys_enchant_success")), 
    ITEMSOUND_ENCHANT_FAILED(new PlaySound("ItemSound3.sys_enchant_failed")), 
    ITEMSOUND_SOW_SUCCESS(new PlaySound("ItemSound3.sys_sow_success")), 
    SKILLSOUND_HORROR_1(new PlaySound("SkillSound5.horror_01")), 
    SKILLSOUND_HORROR_2(new PlaySound("SkillSound5.horror_02")), 
    SKILLSOUND_ANTARAS_FEAR(new PlaySound("SkillSound3.antaras_fear")), 
    SKILLSOUND_JEWEL_CELEBRATE(new PlaySound("SkillSound2.jewel.celebrate")), 
    SKILLSOUND_LIQUID_MIX(new PlaySound("SkillSound5.liquid_mix_01")), 
    SKILLSOUND_LIQUID_SUCCESS(new PlaySound("SkillSound5.liquid_success_01")), 
    SKILLSOUND_LIQUID_FAIL(new PlaySound("SkillSound5.liquid_fail_01")), 
    ETCSOUND_ELROKI_SONG_FULL(new PlaySound("EtcSound.elcroki_song_full")), 
    ETCSOUND_ELROKI_SONG_1ST(new PlaySound("EtcSound.elcroki_song_1st")), 
    ETCSOUND_ELROKI_SONG_2ND(new PlaySound("EtcSound.elcroki_song_2nd")), 
    ETCSOUND_ELROKI_SONG_3RD(new PlaySound("EtcSound.elcroki_song_3rd")), 
    BS01_A(new PlaySound("BS01_A")), 
    BS02_A(new PlaySound("BS02_A")), 
    BS03_A(new PlaySound("BS03_A")), 
    BS04_A(new PlaySound("BS04_A")), 
    BS06_A(new PlaySound("BS06_A")), 
    BS07_A(new PlaySound("BS07_A")), 
    BS08_A(new PlaySound("BS08_A")), 
    BS01_D(new PlaySound("BS01_D")), 
    BS02_D(new PlaySound("BS02_D")), 
    BS05_D(new PlaySound("BS05_D")), 
    BS07_D(new PlaySound("BS07_D"));
    
    private static Map<String, PlaySound> soundPackets;
    private final PlaySound _playSound;
    
    private QuestSound(final PlaySound playSound) {
        this._playSound = playSound;
    }
    
    public static PlaySound getSound(final String soundName) {
        if (QuestSound.soundPackets.containsKey(soundName)) {
            return QuestSound.soundPackets.get(soundName);
        }
        for (final QuestSound qs : values()) {
            if (qs._playSound.getSoundName().equals(soundName)) {
                QuestSound.soundPackets.put(soundName, qs._playSound);
                return qs._playSound;
            }
        }
        QuestSound.soundPackets.put(soundName, new PlaySound(soundName));
        return QuestSound.soundPackets.get(soundName);
    }
    
    public String getSoundName() {
        return this._playSound.getSoundName();
    }
    
    public PlaySound getPacket() {
        return this._playSound;
    }
    
    static {
        QuestSound.soundPackets = new HashMap<String, PlaySound>();
    }
}
