// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.commons.util.StreamUtil;
import java.util.function.ToIntFunction;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.function.Function;
import java.util.stream.Stream;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.database.dao.ManorProductionDAO;
import io.github.joealisson.primitive.IntSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ManorProcureDAO;
import java.util.Map;
import java.util.Iterator;
import org.l2j.gameserver.model.entity.Castle;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.StatsSet;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import io.github.joealisson.primitive.HashIntMap;
import java.util.Calendar;
import org.l2j.gameserver.enums.ManorMode;
import org.l2j.gameserver.data.database.data.SeedProduction;
import org.l2j.gameserver.data.database.data.CropProcure;
import java.util.List;
import org.l2j.gameserver.model.Seed;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;
import org.l2j.gameserver.model.interfaces.IStorable;
import org.l2j.gameserver.util.GameXmlReader;

public final class CastleManorManager extends GameXmlReader implements IStorable
{
    private static final Logger LOGGER;
    private final IntMap<Seed> seeds;
    private final IntMap<List<CropProcure>> procures;
    private final IntMap<List<CropProcure>> procuresNext;
    private final IntMap<List<SeedProduction>> productions;
    private final IntMap<List<SeedProduction>> productionsNext;
    private ManorMode mode;
    private Calendar nextModeChange;
    
    private CastleManorManager() {
        this.seeds = (IntMap<Seed>)new HashIntMap();
        this.procures = (IntMap<List<CropProcure>>)new HashIntMap();
        this.procuresNext = (IntMap<List<CropProcure>>)new HashIntMap();
        this.productions = (IntMap<List<SeedProduction>>)new HashIntMap();
        this.productionsNext = (IntMap<List<SeedProduction>>)new HashIntMap();
        this.mode = ManorMode.APPROVED;
        this.nextModeChange = null;
        if (Config.ALLOW_MANOR) {
            this.load();
            this.loadDb();
            final Calendar currentTime = Calendar.getInstance();
            final int hour = currentTime.get(11);
            final int min = currentTime.get(12);
            final int maintenanceMin = Config.ALT_MANOR_REFRESH_MIN + Config.ALT_MANOR_MAINTENANCE_MIN;
            if ((hour >= Config.ALT_MANOR_REFRESH_TIME && min >= maintenanceMin) || hour < Config.ALT_MANOR_APPROVE_TIME || (hour == Config.ALT_MANOR_APPROVE_TIME && min <= Config.ALT_MANOR_APPROVE_MIN)) {
                this.mode = ManorMode.MODIFIABLE;
            }
            else if (hour == Config.ALT_MANOR_REFRESH_TIME && min >= Config.ALT_MANOR_REFRESH_MIN) {
                this.mode = ManorMode.MAINTENANCE;
            }
            this.scheduleModeChange();
            ThreadPool.scheduleAtFixedRate(this::storeMe, (long)(Config.ALT_MANOR_SAVE_PERIOD_RATE * 60 * 60 * 1000), (long)(Config.ALT_MANOR_SAVE_PERIOD_RATE * 60 * 60 * 1000));
        }
        else {
            this.mode = ManorMode.DISABLED;
            CastleManorManager.LOGGER.info("Manor system is deactivated.");
        }
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/Seeds.xsd");
    }
    
    public final void load() {
        this.parseDatapackFile("data/Seeds.xml");
        CastleManorManager.LOGGER.info("Loaded {} seeds.", (Object)this.seeds.size());
        this.releaseResources();
    }
    
    public final void parseDocument(final Document doc, final File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("castle".equalsIgnoreCase(d.getNodeName())) {
                        final int castleId = this.parseInteger(d.getAttributes(), "id");
                        for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
                            if ("crop".equalsIgnoreCase(c.getNodeName())) {
                                final StatsSet set = new StatsSet();
                                set.set("castleId", castleId);
                                final NamedNodeMap attrs = c.getAttributes();
                                for (int i = 0; i < attrs.getLength(); ++i) {
                                    final Node att = attrs.item(i);
                                    set.set(att.getNodeName(), att.getNodeValue());
                                }
                                this.seeds.put(set.getInt("seedId"), (Object)new Seed(set));
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void loadDb() {
        for (final Castle castle : CastleManager.getInstance().getCastles()) {
            this.loadProductions(castle);
            this.loadProcures(castle);
        }
    }
    
    private void loadProcures(final Castle castle) {
        final IntSet cropIds = this.getCropIds();
        final Map<Boolean, List<CropProcure>> partition = ((ManorProcureDAO)DatabaseAccess.getDAO((Class)ManorProcureDAO.class)).findManorProcureByCastle(castle.getId()).stream().filter(procure -> cropIds.contains(procure.getSeedId())).collect((Collector<? super Object, ?, Map<Boolean, List<CropProcure>>>)Collectors.partitioningBy(SeedProduction::isNextPeriod));
        this.procures.put(castle.getId(), (Object)partition.get(false));
        this.procuresNext.put(castle.getId(), (Object)partition.get(true));
    }
    
    private void loadProductions(final Castle castle) {
        final Map<Boolean, List<SeedProduction>> partition = ((ManorProductionDAO)DatabaseAccess.getDAO((Class)ManorProductionDAO.class)).findManorProductionByCastle(castle.getId()).stream().filter(production -> this.seeds.containsKey(production.getSeedId())).collect((Collector<? super Object, ?, Map<Boolean, List<SeedProduction>>>)Collectors.partitioningBy(SeedProduction::isNextPeriod));
        this.productions.put(castle.getId(), (Object)partition.get(false));
        this.productionsNext.put(castle.getId(), (Object)partition.get(true));
    }
    
    private void scheduleModeChange() {
        (this.nextModeChange = Calendar.getInstance()).set(13, 0);
        switch (this.mode) {
            case MODIFIABLE: {
                this.nextModeChange.set(11, Config.ALT_MANOR_APPROVE_TIME);
                this.nextModeChange.set(12, Config.ALT_MANOR_APPROVE_MIN);
                if (this.nextModeChange.before(Calendar.getInstance())) {
                    this.nextModeChange.add(5, 1);
                    break;
                }
                break;
            }
            case MAINTENANCE: {
                this.nextModeChange.set(11, Config.ALT_MANOR_REFRESH_TIME);
                this.nextModeChange.set(12, Config.ALT_MANOR_REFRESH_MIN + Config.ALT_MANOR_MAINTENANCE_MIN);
                break;
            }
            case APPROVED: {
                this.nextModeChange.set(11, Config.ALT_MANOR_REFRESH_TIME);
                this.nextModeChange.set(12, Config.ALT_MANOR_REFRESH_MIN);
                break;
            }
        }
        ThreadPool.schedule(this::changeMode, this.nextModeChange.getTimeInMillis() - System.currentTimeMillis());
    }
    
    public final void changeMode() {
        switch (this.mode) {
            case APPROVED: {
                this.mode = ManorMode.MAINTENANCE;
                for (final Castle castle : CastleManager.getInstance().getCastles()) {
                    final Clan owner = castle.getOwner();
                    if (owner == null) {
                        continue;
                    }
                    final int castleId = castle.getId();
                    final ItemContainer cwh = owner.getWarehouse();
                    for (final CropProcure crop : (List)this.procures.get(castleId)) {
                        if (crop.getStartAmount() > 0L) {
                            if (crop.getStartAmount() != crop.getAmount()) {
                                long count = (long)((crop.getStartAmount() - crop.getAmount()) * 0.9);
                                if (count < 1L && Rnd.get(99) < 90) {
                                    count = 1L;
                                }
                                if (count > 0L) {
                                    cwh.addItem("Manor", this.getSeedByCrop(crop.getSeedId()).getMatureId(), count, null, null);
                                }
                            }
                            if (crop.getAmount() <= 0L) {
                                continue;
                            }
                            castle.addToTreasuryNoTax(crop.getAmount() * crop.getPrice());
                        }
                    }
                    final List<SeedProduction> _nextProduction = (List<SeedProduction>)this.productionsNext.get(castleId);
                    final List<CropProcure> _nextProcure = (List<CropProcure>)this.procuresNext.get(castleId);
                    this.productions.put(castleId, (Object)_nextProduction);
                    this.procures.put(castleId, (Object)_nextProcure);
                    if (castle.getTreasury() < this.getManorCost(castleId, false)) {
                        this.productionsNext.put(castleId, (Object)Collections.emptyList());
                        this.procuresNext.put(castleId, (Object)Collections.emptyList());
                    }
                    else {
                        final List<SeedProduction> production = new ArrayList<SeedProduction>(_nextProduction);
                        for (final SeedProduction s : production) {
                            s.setAmount(s.getStartAmount());
                        }
                        this.productionsNext.put(castleId, (Object)production);
                        final List<CropProcure> procure = new ArrayList<CropProcure>(_nextProcure);
                        for (final CropProcure cr : procure) {
                            cr.setAmount(cr.getStartAmount());
                        }
                        this.procuresNext.put(castleId, (Object)procure);
                    }
                }
                this.storeMe();
                break;
            }
            case MAINTENANCE: {
                for (final Castle castle : CastleManager.getInstance().getCastles()) {
                    final Clan owner = castle.getOwner();
                    if (owner != null) {
                        final ClanMember clanLeader = owner.getLeader();
                        if (clanLeader == null || !clanLeader.isOnline()) {
                            continue;
                        }
                        clanLeader.getPlayerInstance().sendPacket(SystemMessageId.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED);
                    }
                }
                this.mode = ManorMode.MODIFIABLE;
                break;
            }
            case MODIFIABLE: {
                this.mode = ManorMode.APPROVED;
                for (final Castle castle : CastleManager.getInstance().getCastles()) {
                    final Clan owner = castle.getOwner();
                    if (owner == null) {
                        continue;
                    }
                    int slots = 0;
                    final int castleId2 = castle.getId();
                    final ItemContainer cwh2 = owner.getWarehouse();
                    for (final CropProcure crop2 : (List)this.procuresNext.get(castleId2)) {
                        if (crop2.getStartAmount() > 0L && cwh2.getItemsByItemId(this.getSeedByCrop(crop2.getSeedId()).getMatureId()) == null) {
                            ++slots;
                        }
                    }
                    final long manorCost = this.getManorCost(castleId2, true);
                    if (!cwh2.validateCapacity(slots) && castle.getTreasury() < manorCost) {
                        ((List)this.productionsNext.get(castleId2)).clear();
                        ((List)this.procuresNext.get(castleId2)).clear();
                        final ClanMember clanLeader2 = owner.getLeader();
                        if (clanLeader2 == null || !clanLeader2.isOnline()) {
                            continue;
                        }
                        clanLeader2.getPlayerInstance().sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE);
                    }
                    else {
                        castle.addToTreasuryNoTax(-manorCost);
                    }
                }
                break;
            }
        }
        this.scheduleModeChange();
    }
    
    public final void setNextSeedProduction(final List<SeedProduction> list, final int castleId) {
        this.productionsNext.put(castleId, (Object)list);
    }
    
    public final void setNextCropProcure(final List<CropProcure> list, final int castleId) {
        this.procuresNext.put(castleId, (Object)list);
    }
    
    public final List<SeedProduction> getSeedProduction(final int castleId, final boolean nextPeriod) {
        return (List<SeedProduction>)(nextPeriod ? this.productionsNext.get(castleId) : ((List)this.productions.get(castleId)));
    }
    
    public final SeedProduction getSeedProduct(final int castleId, final int seedId, final boolean nextPeriod) {
        for (final SeedProduction sp : this.getSeedProduction(castleId, nextPeriod)) {
            if (sp.getSeedId() == seedId) {
                return sp;
            }
        }
        return null;
    }
    
    public final List<CropProcure> getCropProcure(final int castleId, final boolean nextPeriod) {
        return (List<CropProcure>)(nextPeriod ? this.procuresNext.get(castleId) : ((List)this.procures.get(castleId)));
    }
    
    public final CropProcure getCropProcure(final int castleId, final int cropId, final boolean nextPeriod) {
        for (final CropProcure cp : this.getCropProcure(castleId, nextPeriod)) {
            if (cp.getSeedId() == cropId) {
                return cp;
            }
        }
        return null;
    }
    
    public final long getManorCost(final int castleId, final boolean nextPeriod) {
        final List<CropProcure> procure = this.getCropProcure(castleId, nextPeriod);
        final List<SeedProduction> production = this.getSeedProduction(castleId, nextPeriod);
        long total = 0L;
        for (final SeedProduction seed : production) {
            final Seed s = this.getSeed(seed.getSeedId());
            total += ((s == null) ? 1L : (s.getSeedReferencePrice() * seed.getStartAmount()));
        }
        for (final CropProcure crop : procure) {
            total += crop.getPrice() * crop.getStartAmount();
        }
        return total;
    }
    
    @Override
    public final boolean storeMe() {
        final ManorProductionDAO manorProduceDAO = (ManorProductionDAO)DatabaseAccess.getDAO((Class)ManorProductionDAO.class);
        manorProduceDAO.deleteManorProduction();
        manorProduceDAO.save((Collection<SeedProduction>)Stream.concat(this.productions.values().stream(), this.productionsNext.values().stream()).flatMap((Function<? super Object, ? extends Stream<?>>)Collection::stream).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        final ManorProcureDAO manorProcureDAO = (ManorProcureDAO)DatabaseAccess.getDAO((Class)ManorProcureDAO.class);
        manorProcureDAO.deleteManorProcure();
        manorProcureDAO.save((Collection<CropProcure>)Stream.concat(this.procures.values().stream(), this.procuresNext.values().stream()).flatMap((Function<? super Object, ? extends Stream<?>>)Collection::stream).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return true;
    }
    
    public final void resetManorData(final int castleId) {
        if (!Config.ALLOW_MANOR) {
            return;
        }
        ((List)this.procures.get(castleId)).clear();
        ((List)this.procuresNext.get(castleId)).clear();
        ((List)this.productions.get(castleId)).clear();
        ((List)this.productionsNext.get(castleId)).clear();
    }
    
    public final boolean isUnderMaintenance() {
        return this.mode == ManorMode.MAINTENANCE;
    }
    
    public final boolean isManorApproved() {
        return this.mode == ManorMode.APPROVED;
    }
    
    public final boolean isModifiablePeriod() {
        return this.mode == ManorMode.MODIFIABLE;
    }
    
    public final String getCurrentModeName() {
        return this.mode.toString();
    }
    
    public final String getNextModeChange() {
        return new SimpleDateFormat("dd/MM HH:mm:ss").format(this.nextModeChange.getTime());
    }
    
    public final List<Seed> getCrops() {
        final List<Seed> seeds = new ArrayList<Seed>();
        final List<Integer> cropIds = new ArrayList<Integer>();
        for (final Seed seed : this.seeds.values()) {
            if (!cropIds.contains(seed.getCropId())) {
                seeds.add(seed);
                cropIds.add(seed.getCropId());
            }
        }
        cropIds.clear();
        return seeds;
    }
    
    public final Set<Seed> getSeedsForCastle(final int castleId) {
        return this.seeds.values().stream().filter(s -> s.getCastleId() == castleId).collect((Collector<? super Object, ?, Set<Seed>>)Collectors.toSet());
    }
    
    public final IntSet getCropIds() {
        return StreamUtil.collectToSet(this.seeds.values().stream().mapToInt(Seed::getCropId));
    }
    
    public final Seed getSeed(final int seedId) {
        return (Seed)this.seeds.get(seedId);
    }
    
    public final Seed getSeedByCrop(final int cropId, final int castleId) {
        for (final Seed s : this.getSeedsForCastle(castleId)) {
            if (s.getCropId() == cropId) {
                return s;
            }
        }
        return null;
    }
    
    public final Seed getSeedByCrop(final int cropId) {
        for (final Seed s : this.seeds.values()) {
            if (s.getCropId() == cropId) {
                return s;
            }
        }
        return null;
    }
    
    public static CastleManorManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)CastleManorManager.class);
    }
    
    private static class Singleton
    {
        private static final CastleManorManager INSTANCE;
        
        static {
            INSTANCE = new CastleManorManager();
        }
    }
}
