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

public class Hopzone extends VoteSystem
{
    public Hopzone(final int votesDiff, final boolean allowReport, final int boxes, final Map<Integer, Integer> rewards, final int checkMins) {
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
            final URLConnection con = new URL(Config.HOPZONE_SERVER_LINK).openConnection();
            con.addRequestProperty("User-Agent", "Mozilla/5.0");
            isr = new InputStreamReader(con.getInputStream());
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<li><span class=\"rank anonymous tooltip\" title")) {
                    final int votes = Integer.valueOf(line.split(">")[2].replace("</span", ""));
                    return votes;
                }
            }
            br.close();
            isr.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            Hopzone.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getSiteName()));
        }
        return -1;
    }
    
    @Override
    public String getSiteName() {
        return "Hopzone";
    }
}
