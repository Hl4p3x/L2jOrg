// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.votereward;

import java.net.URLConnection;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.l2j.gameserver.Config;
import java.util.Map;

public class Topzone extends VoteSystem
{
    public Topzone(final int votesDiff, final boolean allowReport, final int boxes, final Map<Integer, Integer> rewards, final int checkMins) {
        super(votesDiff, allowReport, boxes, rewards, checkMins);
    }
    
    @Override
    public void run() {
        this.reward();
    }
    
    @Override
    public int getVotes() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            final URLConnection con = new URL(Config.TOPZONE_SERVER_LINK).openConnection();
            con.addRequestProperty("User-Agent", "L2TopZone");
            isr = new InputStreamReader(con.getInputStream());
            br = new BufferedReader(isr);
            final String line;
            if ((line = br.readLine()) != null) {
                final int votes = Integer.valueOf(line);
                return votes;
            }
            br.close();
            isr.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Topzone.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getSiteName()));
        }
        return -1;
    }
    
    @Override
    public String getSiteName() {
        return "Topzone";
    }
}
