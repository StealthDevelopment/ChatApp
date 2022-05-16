package stealthdevelopment.chatapp.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import eu.derzauberer.javautils.events.ClientConnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent.DisconnectCause;
import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;
import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.handler.CommandHandler;
import eu.derzauberer.javautils.sockets.Client;
import eu.derzauberer.javautils.sockets.Server;
import eu.derzauberer.javautils.util.Console;
import eu.derzauberer.javautils.util.Sender;
import eu.derzauberer.javautils.util.Sender.MessageType;
import stealthdevelopment.chatapp.commands.ConnectCommand;
import stealthdevelopment.chatapp.commands.DisconnectCommand;
import stealthdevelopment.chatapp.commands.HostCommand;

public class Main {
	
	private static boolean connected = false;
	private static Server server;
	private static Client client;
	private static Console console = new Console();
	private static CommandHandler clientCommands = new CommandHandler();
	private static CommandHandler serverCommands = new CommandHandler();
	
	private static HashMap<Client, String> names = new HashMap<>();
	
	public static void main(String[] args) {
		clientCommands.registerCommand("/exit", (Sender sender, String label, String[] argss) -> {System.exit(0); return true;});
		clientCommands.registerCommand("/connect", new ConnectCommand());
		clientCommands.registerCommand("/host", new HostCommand());
		clientCommands.registerCommand("/disconnect", new DisconnectCommand());
		clientCommands.setOnCommandNotFound(event -> {
			if (connected) client.sendMessage(event.getString()); 
		});
		console.setOnInput(Main::onConsoleInput);
		console.sendMessage("Welcome in ChatApp!");
	}
	
	public static void onConsoleInput(ConsoleInputEvent event) {
		if (event.getInput().startsWith("/")) clientCommands.executeCommand(console, event.getInput());
		else if (client != null) client.sendMessage(event.getInput()); 
	}
	
	public static void onServerMessageReceive(ClientMessageReceiveEvent event) {
		if (event.getMessage().startsWith("/")) {
			serverCommands.executeCommand(event.getClient(), event.getMessage());
			console.sendMessage(MessageType.INFO, names.get(event.getClient()) + ": " + "run command " + event.getMessage());
		} else {
			server.sendMessage(names.get(event.getClient()) + ": " + event.getMessage());
			console.sendMessage(names.get(event.getClient()) + ": " + event.getMessage());
		}
	}
	
	public static void onServerClientConnect(ClientConnectEvent event) {
		String name = Integer.toString(new Random().nextInt(9999));
		while (name.length() > 4) name = "0" + name;
		name = "User" + name;
		names.put(event.getClient(), name);
		server.sendMessage(name + " connected!");
		console.sendMessage(name + " connected!");
	}
	
	public static void onServerClientDisconnect(ClientDisconnectEvent event) {
		String name = names.get(event.getClient());
		names.remove(event.getClient());
		if (event.getCause() == DisconnectCause.DISCONNECTED) {
			server.sendMessage(name + " disconnected!");
			console.sendMessage(name + " disconnected!");
		} else {
			server.sendMessage(name + " timed out!");
			console.sendMessage(name + " timed out!");
		}
	}
	
	public static void setServer(int port) throws IOException {
		server = new Server(port);
		server.setOnMessageReceive(Main::onServerMessageReceive);
		server.setOnClientConnect(Main::onServerClientConnect);
		server.setOnClientDisconnect(Main::onServerClientDisconnect);
	}
	
	public static void disconnectServer() throws IOException {
		server.close();
		server = null;
	}
	
	public static Server getServer() {
		return server;
	}
	
	public static void setClient(String adress, int port) throws IOException {
		client = new Client(adress, port);
		client.setOnMessageReceive(event -> console.sendMessage(MessageType.DEFAULT, event.getMessage()));
		client.setOnClientDisconnect(event -> console.sendMessage(MessageType.INFO, "Disconnected!"));
		connected = true;
	}
	
	public static void disconnectClient() throws IOException {
		client.close();
		client = null;
		connected = false;
	}
	
	public static Client getClient() {
		return client;
	}
	
	public static CommandHandler getClientCommands() {
		return clientCommands;
	}
	
	public static CommandHandler getServerCommands() {
		return serverCommands;
	}
	
	public static Console getConsole() {
		return console;
	}
	
}
