// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial.kamael;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.quests.tutorial.Tutorial;

public class Q10960_Tutorial extends Tutorial
{
    private static final QuestSoundHtmlHolder STARTING_HTML_VOICE;
    private static final int NEWBIE_HELPER = 34108;
    private static final int SUPERVISOR = 34109;
    private static final Location HELPER_LOC;
    private static final Location VILLAGE_LOCATION;
    
    public Q10960_Tutorial() {
        super(10960, new ClassId[] { ClassId.JIN_KAMAEL_SOLDIER });
        this.addFirstTalkId(34109);
    }
    
    @Override
    protected int newbieHelperId() {
        return 34108;
    }
    
    @Override
    protected ILocational villageLocation() {
        return (ILocational)Q10960_Tutorial.VILLAGE_LOCATION;
    }
    
    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return Q10960_Tutorial.STARTING_HTML_VOICE;
    }
    
    @Override
    protected Location helperLocation() {
        return Q10960_Tutorial.HELPER_LOC;
    }
    
    static {
        STARTING_HTML_VOICE = new QuestSoundHtmlHolder("tutorial_voice_001j", "main.html");
        HELPER_LOC = new Location(-124731, 38070, 1208);
        VILLAGE_LOCATION = new Location(-118073, 45131, 368, 43039);
    }
}
