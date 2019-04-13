package auctionsniper;

import auctionsniper.ui.SnipersTableModel;

import java.util.ArrayList;

public class SniperLauncher implements UserRequestListener {
    private final ArrayList<Auction> notToBeGCd = new ArrayList<>();
    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item.identifier);
        notToBeGCd.add(auction);
        AuctionSniper sniper = new AuctionSniper(item.identifier, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
