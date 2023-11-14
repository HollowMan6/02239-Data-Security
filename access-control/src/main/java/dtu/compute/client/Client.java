package dtu.compute.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import dtu.compute.server.print.PrinterService;
import dtu.compute.util.Configuration;
import dtu.compute.util.Crypto;
import dtu.compute.util.DB;

public class Client {
	private static final DB db = new DB();
	public static void main(String[] args)
			throws MalformedURLException, NotBoundException, RemoteException {

		String pwHash = Crypto.hash(Configuration.testUserPassword);
		// Insert a test user into the database.
		db.addUser(Configuration.testUsername, Crypto.salt(pwHash));

		PrinterService printer = (PrinterService) Naming.lookup(Configuration.url + "/printer");
		String access_token = printer.authenticate(Configuration.testUsername, pwHash, Configuration.validSessionTime);
		System.out.println(printer.start(access_token));
		System.out.println(printer.print("test1.txt", "printer1", access_token));
		System.out.println(printer.print("test2.txt", "printer2", access_token));
		System.out.println(printer.print("test3.txt", "printer2", access_token));
		System.out.println(printer.print("test4.txt", "printer1", access_token));
		System.out.println(printer.print("test5.txt", "printer1", access_token));
		System.out.println(printer.queue("printer2", access_token));
		System.out.println(printer.queue("printer1", access_token));
		System.out.println(printer.setConfig("printers", "2", access_token));
		System.out.println(printer.readConfig("printers", access_token));
		System.out.println(printer.topQueue("printer1", 3, access_token));
		System.out.println(printer.queue("printer1", access_token));
		System.out.println(printer.status("printer1", access_token));
		System.out.println(printer.status("printer2", access_token));
		System.out.println(printer.restart(access_token));
		access_token = printer.authenticate(Configuration.testUsername, pwHash, Configuration.validSessionTime);
		System.out.println(printer.queue("printer1", access_token));
		System.out.println(printer.queue("printer2", access_token));
		System.out.println(printer.stop(access_token));

		db.clear();
	}

}
