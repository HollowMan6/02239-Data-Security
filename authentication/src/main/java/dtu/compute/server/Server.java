package dtu.compute.server;

import dtu.compute.server.print.PrinterServant;
import dtu.compute.util.Configuration;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static void main(String[] args) throws IOException {
		Registry registry = LocateRegistry.createRegistry(Configuration.port);
		registry.rebind("printer", new PrinterServant());
	}
}
