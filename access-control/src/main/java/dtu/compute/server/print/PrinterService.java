package dtu.compute.server.print;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrinterService extends Remote {
    String print(String filename, String printer, String access_token) throws RemoteException;
    String queue(String printer, String access_token) throws RemoteException;
    String topQueue(String printer, int job, String access_token) throws RemoteException;
    String start(String access_token) throws RemoteException;
    String stop(String access_token) throws RemoteException;
    String restart(String access_token) throws RemoteException;
    String status(String printer, String access_token) throws RemoteException;
    String readConfig(String parameter, String access_token) throws RemoteException;
    String setConfig(String parameter, String value, String access_token) throws RemoteException;
    String authenticate(String username, String password, int validTime) throws RemoteException;
}
