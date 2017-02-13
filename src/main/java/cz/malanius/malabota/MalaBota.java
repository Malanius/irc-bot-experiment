package cz.malanius.malabota;

import java.io.*;
import java.net.*;
import static org.slf4j.LoggerFactory.getLogger;

public class MalaBota {

    /**
     * Class logger
     */
    private static final org.slf4j.Logger LOG = getLogger(MalaBota.class);

    public static void main(String[] args) throws Exception {
        LOG.trace("MalaBota - main() - start.");

        // The server to connect to and our details.
        String server = "irc.freenode.net";
        String nick = "malabota";
        String login = "malabota";

        // The channel which the bot will join.
        String channel = "#brmlab";

        // Connect directly to the IRC server.
        LOG.debug("Connecting to IRC server {}.", server);
        Socket socket = new Socket(server, 6667);
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // Log on to the server.
        LOG.debug("Attempting login onto server...");
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + login + " 8 * : Java IRC Hacks Bot\r\n");
        writer.flush();

        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.contains("004")) {
                LOG.info("Login as '{}' sucesfull.", nick);
                break;
            } else if (line.contains("433")) {
                LOG.error("Nickname '{}' is already in use.", nick);
                return;
            }
        }

        // Join the channel.
        LOG.debug("Joining channel {}", channel);
        writer.write("JOIN " + channel + "\r\n");
        writer.flush();

        // Keep reading lines from the server.
        while ((line = reader.readLine()) != null) {
            if (line.toLowerCase().startsWith("ping")) {
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n");
                LOG.debug("Got pinged!");
                writer.flush();
                LOG.info(line);
            } else {
                // Print the raw line received by the bot.
                LOG.info(line);
            }
        }
    }

}
