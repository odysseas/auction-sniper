package test.endtoend.auctionsniper;

import static auctionsniper.ui.MainWindow.STATUS_BIDDING;
import static auctionsniper.ui.MainWindow.STATUS_JOINING;
import static auctionsniper.ui.MainWindow.STATUS_LOST;
import static auctionsniper.ui.MainWindow.STATUS_WINNING;
import static auctionsniper.ui.MainWindow.STATUS_WON;
import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;
import auctionsniper.ui.Main;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@localhost/Auction";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(STATUS_WINNING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }
    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(STATUS_WON);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}