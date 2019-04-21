package test.endtoend.auctionsniper;

import static org.junit.Assert.assertThat;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

public class AuctionLogDriver {
    public static final String LOG_FILE_NAME = "auction-sniper.log";
    private final File logfile = new File(LOG_FILE_NAME);

    public void hasEntry(Matcher<String> mathcer) throws IOException {
        assertThat(FileUtils.readFileToString(logfile), mathcer);
    }

    public void clearLog() {
        logfile.delete();
        LogManager.getLogManager().reset();
    }
}
