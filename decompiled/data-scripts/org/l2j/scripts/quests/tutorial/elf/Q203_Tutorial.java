// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial.elf;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.quests.tutorial.Tutorial;

public class Q203_Tutorial extends Tutorial
{
    private static final int SUPERVISOR = 30370;
    private static final int NEWBIE_HELPER = 30400;
    private static final Location HELPER_LOCATION;
    private static final Location VILLAGE_LOCATION;
    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML;
    
    public Q203_Tutorial() {
        super(203, new ClassId[] { ClassId.ELVEN_FIGHTER, ClassId.ELVEN_MAGE });
        this.addFirstTalkId(30370);
    }
    
    @Override
    protected int newbieHelperId() {
        return 30400;
    }
    
    @Override
    protected ILocational villageLocation() {
        return (ILocational)Q203_Tutorial.VILLAGE_LOCATION;
    }
    
    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return Q203_Tutorial.STARTING_VOICE_HTML;
    }
    
    @Override
    protected Location helperLocation() {
        return Q203_Tutorial.HELPER_LOCATION;
    }
    
    static {
        HELPER_LOCATION = new Location(46112, 41200, -3504);
        VILLAGE_LOCATION = new Location(45479, 48318, -3056, 55707);
        STARTING_VOICE_HTML = new QuestSoundHtmlHolder("tutorial_voice_001c", "main.html");
    }
}
