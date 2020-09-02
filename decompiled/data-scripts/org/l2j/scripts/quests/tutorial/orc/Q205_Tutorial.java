// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial.orc;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.quests.tutorial.Tutorial;

public class Q205_Tutorial extends Tutorial
{
    private static final int SUPERVISOR = 30573;
    private static final int NEWBIE_HELPER = 30575;
    private static final Location HELPER_LOCATION;
    private static final Location VILLAGE_LOCATION;
    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML;
    
    public Q205_Tutorial() {
        super(205, new ClassId[] { ClassId.ORC_FIGHTER, ClassId.ORC_MAGE });
        this.addFirstTalkId(30573);
    }
    
    @Override
    protected int newbieHelperId() {
        return 30575;
    }
    
    @Override
    protected ILocational villageLocation() {
        return (ILocational)Q205_Tutorial.VILLAGE_LOCATION;
    }
    
    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return Q205_Tutorial.STARTING_VOICE_HTML;
    }
    
    @Override
    protected Location helperLocation() {
        return Q205_Tutorial.HELPER_LOCATION;
    }
    
    static {
        HELPER_LOCATION = new Location(-56736, -113680, -672);
        VILLAGE_LOCATION = new Location(-45113, -113598, -192, 45809);
        STARTING_VOICE_HTML = new QuestSoundHtmlHolder("tutorial_voice_001g", "main.html");
    }
}
