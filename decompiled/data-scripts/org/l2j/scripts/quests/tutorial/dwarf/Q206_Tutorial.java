// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial.dwarf;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.quests.tutorial.Tutorial;

public class Q206_Tutorial extends Tutorial
{
    private static final int SUPERVISOR = 30528;
    private static final int NEWBIE_HELPER = 30530;
    private static final Location HELPER_LOCATION;
    private static final Location VILLAGE_LOCATION;
    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML;
    
    public Q206_Tutorial() {
        super(206, new ClassId[] { ClassId.DWARVEN_FIGHTER });
        this.addFirstTalkId(30528);
    }
    
    @Override
    protected int newbieHelperId() {
        return 30530;
    }
    
    @Override
    protected ILocational villageLocation() {
        return (ILocational)Q206_Tutorial.VILLAGE_LOCATION;
    }
    
    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return Q206_Tutorial.STARTING_VOICE_HTML;
    }
    
    @Override
    protected Location helperLocation() {
        return Q206_Tutorial.HELPER_LOCATION;
    }
    
    static {
        HELPER_LOCATION = new Location(108567, -173994, -406);
        VILLAGE_LOCATION = new Location(115575, -178014, -904, 9808);
        STARTING_VOICE_HTML = new QuestSoundHtmlHolder("tutorial_voice_001i", "main.html");
    }
}
