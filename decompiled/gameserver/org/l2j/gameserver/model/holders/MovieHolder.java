// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;
import org.l2j.gameserver.enums.Movie;

public final class MovieHolder
{
    private final Movie _movie;
    private final List<Player> _players;
    private final Collection<Player> _votedPlayers;
    
    public MovieHolder(final List<Player> players, final Movie movie) {
        this._votedPlayers = (Collection<Player>)ConcurrentHashMap.newKeySet();
        this._players = players;
        this._movie = movie;
        this._players.forEach(p -> p.playMovie(this));
    }
    
    public Movie getMovie() {
        return this._movie;
    }
    
    public void playerEscapeVote(final Player player) {
        if (this._votedPlayers.contains(player) || !this._players.contains(player) || !this._movie.isEscapable()) {
            return;
        }
        this._votedPlayers.add(player);
        if (this._votedPlayers.size() * 100 / this._players.size() >= 50) {
            this._players.forEach(Player::stopMovie);
        }
    }
    
    public List<Player> getPlayers() {
        return this._players;
    }
}
