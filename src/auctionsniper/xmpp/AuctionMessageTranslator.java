package auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import static auctionsniper.AuctionEventListener.PriceSource;
import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

import java.util.HashMap;
import java.util.Map;

public class AuctionMessageTranslator implements MessageListener {
    private AuctionEventListener listener;
    private String sniperId;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener;
    }

    public void processMessage(Chat chat, Message message) {
        try {
            translate(message.getBody());

        } catch (Exception parseException) {
            listener.auctionFailed();
        }

    }

    private void translate(String message) {
        AuctionEvent event = AuctionEvent.from(message);
        String eventType = event.type();
        if("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } if ("PRICE".equals(eventType)) {
            listener.currentPrice(
                    event.currentPrice(),
                    event.increment(),
                    event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {
        private final Map<String, String> fields = new HashMap<>();
        public String type() { return get("Event"); }
        public int currentPrice() { return getInt("CurrentPrice"); }
        public int increment() { return getInt("Increment"); }
        public PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }

        private String get(String fieldName) { return fields.get(fieldName); }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        private String bidder() { return get("Bidder"); }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }
    }
}
