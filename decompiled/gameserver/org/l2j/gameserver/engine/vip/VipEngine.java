// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.vip;

import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLoad;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.util.function.Consumer;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.network.serverpackets.ExBRNewIconCashBtnWnd;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.util.GameXmlReader;

public final class VipEngine extends GameXmlReader
{
    private static final byte VIP_MAX_TIER = 10;
    private final IntMap<VipInfo> vipTiers;
    
    private VipEngine() {
        this.vipTiers = (IntMap<VipInfo>)new HashIntMap(11);
        final ListenersContainer listeners = Listeners.players();
        final Player player;
        listeners.addListener(new ConsumerEventListener(listeners, EventType.ON_PLAYER_LOAD, event -> {
            player = event.getPlayer();
            player.setVipTier(this.getVipTier(player));
            if (player.getVipTier() > 0) {
                this.manageTier(player);
            }
            else {
                player.sendPacket(new ReceiveVipInfo());
                player.sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
            }
        }, this));
    }
    
    public void manageTier(final Player player) {
        if (!this.checkVipTierExpiration(player)) {
            player.sendPacket(new ReceiveVipInfo());
        }
        if (player.getVipTier() > 1) {
            final int oldSkillId = ((VipInfo)this.vipTiers.get(player.getVipTier() - 1)).getSkill();
            if (oldSkillId > 0) {
                final Skill oldSkill = SkillEngine.getInstance().getSkill(oldSkillId, 1);
                if (Objects.nonNull(oldSkill)) {
                    player.removeSkill(oldSkill);
                }
            }
        }
        final int skillId = ((VipInfo)this.vipTiers.get((int)player.getVipTier())).getSkill();
        if (skillId > 0) {
            final Skill skill = SkillEngine.getInstance().getSkill(skillId, 1);
            if (Objects.nonNull(skill)) {
                player.addSkill(skill);
            }
        }
        if (PrimeShopData.getInstance().canReceiveVipGift(player)) {
            player.sendPacket(ExBRNewIconCashBtnWnd.SHOW);
        }
        else {
            player.sendPacket(ExBRNewIconCashBtnWnd.NOT_SHOW);
        }
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/vip.xsd");
    }
    
    public void load() {
        this.parseDatapackFile("data/vip.xml");
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        this.forEach((Node)doc, "list", list -> this.forEach(list, "vip", (Consumer)this::parseVipTier));
    }
    
    private void parseVipTier(final Node vipNode) {
        NamedNodeMap attributes = vipNode.getAttributes();
        final Byte level = this.parseByte(attributes, "tier");
        final Long pointsRequired = this.parseLong(attributes, "points-required");
        final Long pointsDepreciated = this.parseLong(attributes, "points-depreciated");
        final VipInfo vipInfo = new VipInfo(level, pointsRequired, pointsDepreciated);
        this.vipTiers.put((int)level, (Object)vipInfo);
        final Node bonusNode = vipNode.getFirstChild();
        if (Objects.nonNull(bonusNode)) {
            attributes = bonusNode.getAttributes();
            vipInfo.setSilverCoinChance(this.parseFloat(attributes, "silver-coin-acquisition"));
            vipInfo.setRustyCoinChance(this.parseFloat(attributes, "rusty-coin-acquisition"));
            vipInfo.setSkill(this.parseInteger(attributes, "skill"));
        }
    }
    
    public byte getVipTier(final Player player) {
        return this.getVipInfo(player).getTier();
    }
    
    public byte getVipTier(final long points) {
        return this.getVipInfo(points).getTier();
    }
    
    private VipInfo getVipInfo(final Player player) {
        final long points = player.getVipPoints();
        return this.getVipInfo(points);
    }
    
    private VipInfo getVipInfo(final long points) {
        for (byte i = 0; i < this.vipTiers.size(); ++i) {
            if (points < ((VipInfo)this.vipTiers.get((int)i)).getPointsRequired()) {
                return (VipInfo)this.vipTiers.get(i - 1);
            }
        }
        return (VipInfo)this.vipTiers.get(10);
    }
    
    public long getPointsDepreciatedOnLevel(final byte vipTier) {
        return ((VipInfo)this.vipTiers.get((int)vipTier)).getPointsDepreciated();
    }
    
    public long getPointsToLevel(final int level) {
        if (this.vipTiers.containsKey(level)) {
            return ((VipInfo)this.vipTiers.get(level)).getPointsRequired();
        }
        return 0L;
    }
    
    public float getSilverCoinDropChance(final Player player) {
        return this.getVipInfo(player).getSilverCoinChance();
    }
    
    public float getRustyCoinDropChance(final Player player) {
        return this.getVipInfo(player).getRustyCoinChance();
    }
    
    public boolean checkVipTierExpiration(final Player player) {
        final Instant now = Instant.now();
        if (now.isAfter(Instant.ofEpochMilli(player.getVipTierExpiration()))) {
            player.updateVipPoints(-this.getPointsDepreciatedOnLevel(player.getVipTier()));
            player.setVipTierExpiration(Instant.now().plus(30L, (TemporalUnit)ChronoUnit.DAYS).toEpochMilli());
            return true;
        }
        return false;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static VipEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final VipEngine INSTANCE;
        
        static {
            INSTANCE = new VipEngine();
        }
    }
}
