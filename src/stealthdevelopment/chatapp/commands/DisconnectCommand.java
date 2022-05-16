package stealthdevelopment.chatapp.commands;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;
import eu.derzauberer.javautils.util.Sender.MessageType;
import stealthdevelopment.chatapp.main.Main;

public class DisconnectCommand implements Command {

	@Override
	public boolean onCommand(Sender sender, String label, String[] args) throws Exception {
		if (Main.getServer() != null) Main.disconnectServer();
		if (Main.getClient() != null) Main.disconnectClient();
		sender.sendMessage(MessageType.INFO, "Closed all connections!");
		return true;
	}
	
}
