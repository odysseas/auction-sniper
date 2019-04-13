package test.auctionsniper;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperPortfolio;
import auctionsniper.SniperPortfolio.PortfolioListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class SniperPortfolioTest {
    private final Mockery context = new Mockery();
    private final PortfolioListener listener = context.mock(PortfolioListener.class);
    private final SniperPortfolio portfolio = new SniperPortfolio();

    @Test public void
    notifiesListenersOfNewSnipers() {
        final AuctionSniper sniper = new AuctionSniper("item id", null);
        context.checking(new Expectations() {{
            oneOf(listener).sniperAdded(sniper);
        }});
        portfolio.addPortfolioListener(listener);

        portfolio.addSniper(sniper);
    }
}
