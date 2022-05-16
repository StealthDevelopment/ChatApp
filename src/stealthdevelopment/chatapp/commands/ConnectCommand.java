package stealthdevelopment.chatapp.commands;

import java.io.IOException;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;
import eu.derzauberer.javautils.util.Sender.MessageType;
import stealthdevelopment.chatapp.main.Main;

public class ConnectCommand implements Command {

	@Override
	public boolean onCommand(Sender sender, String label, String[] args) throws Exception {
		if (Main.getServer() != null) {
			sender.sendMessage(MessageType.ERROR, "A only a client can connect to a server!");
		} else if (Main.getClient() != null) {
			sender.sendMessage(MessageType.ERROR, "You are already connected!");
		} else if (args.length > 2) {
			sender.sendMessage(MessageType.ERROR, "To many arguments!");
		} else if (args.length < 2) {
			sender.sendMessage(MessageType.ERROR, "Not enough arguments!");
		} else if (Main.getClient() != null || Main.getServer() != null) {
			sender.sendMessage(MessageType.ERROR, "Already connected as client!");
		} else {
			sender.sendMessage(MessageType.INFO, "Try to connect to " + args[0] + ":" + args[1] + "!");
			try {
				Main.setClient(args[0], Integer.parseInt(args[1]));
				sender.sendMessage(MessageType.INFO, "Connected!");
				return true;
			} catch (IOException exception) {
				sender.sendMessage(MessageType.ERROR, "Can't connect to server!");
			}
		}
		return false;
	}

}
