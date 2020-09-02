// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial.fighter;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.quests.tutorial.Tutorial;

public class Q201_Tutorial extends Tutorial
{
    private static final int SUPERVISOR = 30008;
    private static final int NEWBIE_HELPER = 30009;
    private static final Location HELPER_LOCATION;
    private static final Location VILLAGE_LOCATION;
    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML;
    
    public Q201_Tutorial() {
        super(201, new ClassId[] { ClassId.FIGHTER });
        this.addFirstTalkId(30008);
    }
    
    @Override
    protected int newbieHelperId() {
        return 30009;
    }
    
    @Override
    protected ILocational villageLocation() {
        return (ILocational)Q201_Tutorial.VILLAGE_LOCATION;
    }
    
    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return Q201_Tutorial.STARTING_VOICE_HTML;
    }
    
    @Override
    protected Location helperLocation() {
        return Q201_Tutorial.HELPER_LOCATION;
    }
    
    static {
        HELPER_LOCATION = new Location(-71424, 258336, -3109);
        VILLAGE_LOCATION = new Location(-84046, 243283, -3728, 18316);
        STARTING_VOICE_HTML = new QuestSoundHtmlHolder("tutorial_voice_001a", "main.html");
    }
}
