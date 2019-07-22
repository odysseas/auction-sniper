package test.auctionsniper;

import auctionsniper.*;
import auctionsniper.AuctionEventListener.PriceSource;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static auctionsniper.SniperState.*;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(JMock.class)
public class AuctionSniperTest {
    private static final Item item = new Item("item-id", 2000);
    private final Mockery context = new Mockery();
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(item, auction);
    private final States sniperState = context.states("sniper");

    @Before public void attachListener() { sniper.addSniperListener(sniperListener); }

    @Test public void
    reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(item.identifier, 0, 0, LOST));
        }});

        sniper.auctionClosed();
    }

    @Test public void
    reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowingSniperBidding();

            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(item.identifier, 123, 168, LOST));
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test public void
    bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {{
            one(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(item.identifier, price, bid, BIDDING));
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test public void
    reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowingSniperBidding();
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(item.identifier, 135, 135, WINNING));
                        when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }

    @Test public void
    reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(WINNING)));
                        then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(WON)));
                        when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test public void
    doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(item.identifier, 2345, bid, LOSING));
                                        when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(
                equalTo(state), "sniper that is ", "was")
        {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }

    @Test public void
    doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(item.identifier, 2345, 0, LOSING));
        }});

        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    @Test public void
    reportsLostIfAuctionClosesWhenLosing() {
        context.checking(new Expectations() {{

            allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(LOSING)));
            then(sniperState.is("losing"));

            atLeast(1).of(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(LOST)));
            when(sniperState.is("losing"));
        }});

        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test public void
    continuesToBeLosingOnceStopPriceHasBeenReached() {
        final Sequence states = context.sequence("sniper states");
        final int price1 = 2001;
        final int price2 = 2002;
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(item.identifier, price1, 0, LOSING));
                        inSequence(states);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(item.identifier, price2, 0, LOSING));
                        inSequence(states);
        }});

        sniper.currentPrice(price1, 1, PriceSource.FromOtherBidder);
        sniper.currentPrice(price2, 2, PriceSource.FromOtherBidder);
    }

    @Test public void
    doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(WINNING)));
            then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(LOSING)));
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    @Test public void
    reportsFailedIfAuctionFailsWhenBidding() {
        ignoringAuction();
        allowingSniperBidding();

        expectSniperToFailWhenIts("bidding");

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionFailed();
    }

    private void ignoringAuction() {
        context.checking(new Expectations() {{
            ignoring(auction);
        }});
    }

    private void expectSniperToFailWhenIts(final String state) {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(item.identifier, 00, 0, FAILED));
            when(sniperState.is(state));
        }});
    }

    private void allowingSniperBidding() {
        context.checking(new Expectations() {{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                                                        then(sniperState.is("bidding"));
        }});
    }
}
