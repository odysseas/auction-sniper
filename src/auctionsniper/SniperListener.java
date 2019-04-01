package auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperStateChanged(SniperSnapshot sniperSnapshot);
    void addSniper(SniperSnapshot sniperSnapshot);
}
