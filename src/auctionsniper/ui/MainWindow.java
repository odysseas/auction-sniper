package auctionsniper.ui;


import javax.swing.JFrame;
import static auctionsniper.ui.Main.MAIN_WINDOW_NAME;

public class MainWindow extends JFrame {
    public MainWindow() {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
