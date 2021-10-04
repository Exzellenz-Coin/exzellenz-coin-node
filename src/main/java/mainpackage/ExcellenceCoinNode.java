package mainpackage;

import mainpackage.server.Server;
import mainpackage.server.node.FullNode;
import mainpackage.server.node.INode;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ExcellenceCoinNode {
    public static int DEFAULT_DELAY = 1000; //initial delay
    public static int DEFAULT_PERIOD = 500; //delay between update calls
    public static boolean DEFAULT_FIRST_NODE_STATUS = false;
    /**
     * Executing this program requires the following JVM arguments:
     *
     * @param args TODO: currently unused (overwrite static variables with jvm arguments if available)
     */
    public static void main(String[] args) {
        // TODO: Set database configuration
        // TODO: Update database
        INode node = new FullNode();


        //connect to network
        if (!DEFAULT_FIRST_NODE_STATUS) {
            try {
                node.getServer().connectToPeer(null, Server.DEFAULT_PORT); //TODO: user MUST set hostName themselves
                node.getServer().doInitialConnect();
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Initital network connection failed. Please ensure hostname is correct.");
                return; //connection failed
            }
        }

        //activity loop
        class UpdateTask extends TimerTask
        {
            public void run()
            {
                node.update();
            }
        }
        Timer timer = new Timer();
        TimerTask updateTask = new UpdateTask();
        timer.schedule(updateTask, DEFAULT_DELAY, DEFAULT_PERIOD); //calls node.update() at fixed interval
    }
}
