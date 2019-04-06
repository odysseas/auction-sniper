package auctionsniper;

import auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private SniperSnapshot snapshot;

    public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                int bid = price + increment;
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener listener) {
        listeners.addListener(listener);
    }
}
