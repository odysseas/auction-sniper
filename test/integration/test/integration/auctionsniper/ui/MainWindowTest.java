package test.integration.auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.CoreMatchers.equalTo;

public class MainWindowTest {
    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test public void
    makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe =
                new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");

        mainWindow.addUserRequestListener(
                new UserRequestListener() {
                    public void joinAuction(Item item) {
                        buttonProbe.setReceivedValue(item.identifier);
                    }
                });

        driver.startBiddingFor("an item-id", Integer.MAX_VALUE);
        driver.check(buttonProbe);
    }
}
