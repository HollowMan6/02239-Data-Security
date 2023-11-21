package dtu.compute.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

import dtu.compute.server.print.PrinterService;
import dtu.compute.util.Configuration;
import dtu.compute.util.Crypto;
import dtu.compute.util.db.AccessControl;
import dtu.compute.util.db.User;

public class Client {
    private static final User user = new User();
    private static final AccessControl accessControl = new AccessControl();

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        String testUserPWHash = Crypto.hash(Configuration.testUserPassword);

        String[] acTestUsers = Configuration.testACUser;
        int[] userACLists = Configuration.testACList;
        for (int i = 0; i < acTestUsers.length; i++) {
            accessControl.addAccessControlList(acTestUsers[i], userACLists[i]);
            user.addUser(acTestUsers[i], Crypto.salt(testUserPWHash), "none");
        }

        PrinterService printer = (PrinterService) Naming.lookup(Configuration.url + "/printer");
        String testUsername = "B";
        String access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start allowed
        String response = printer.start(access_token);
        assert response.contains("started");
        // print rejected
        response = printer.print("test1_B", "printer1", access_token);
        assert response.contains("not allowed to");
        // queue rejected
        response = printer.queue("printer1", access_token);
        assert response.contains("not allowed to");
        // topQueue rejected
        printer.print("test2_B", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramB", "B", access_token);
        assert response.contains(": ");
        // readConfig allowed
        printer.readConfig("paramB", access_token);
        assert response.contains("paramB");
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        printer.start(access_token);

        testUsername = "C";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_C", "printer1", access_token);
        System.out.println(response);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_C", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig rejected
        response = printer.setConfig("paramC", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramC", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        testUsername = "D";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_C", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue rejected
        printer.print("test2_C", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart rejected
        response = printer.restart(access_token);
        assert response.contains("not allowed to");
        // setConfig rejected
        response = printer.setConfig("paramC", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramC", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        testUsername = "A";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // start allowed
        response = printer.start(access_token);
        assert response.contains("started");
        // print allowed
        response = printer.print("test1_A", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_A", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramA", "A", access_token);
        assert response.contains(": ");
        // readConfig allowed
        response = printer.readConfig("paramA", access_token);
        assert response.contains("paramA");

        printer.stop(access_token);

        testUsername = "B";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start allowed
        response = printer.start(access_token);
        assert response.contains("started");
        // print allowed
        response = printer.print("test1_B", "printer1", access_token);
        assert response.contains("not allowed to");
        // queue rejected
        response = printer.queue("printer1", access_token);
        assert response.contains("not allowed to");
        // topQueue rejected
        printer.print("test2_B", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramB", "B", access_token);
        assert response.contains(": ");
        // readConfig allowed
        printer.readConfig("paramB", access_token);
        assert response.contains("paramB");
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        printer.start(access_token);

        testUsername = "C";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_C", "printer1", access_token);
        System.out.println(response);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_C", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig rejected
        response = printer.setConfig("paramC", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramC", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // User D, E, F and G have same permission, therefore, only D is tested here.
        testUsername = "D";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_C", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue rejected
        printer.print("test2_C", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart rejected
        response = printer.restart(access_token);
        assert response.contains("not allowed to");
        // setConfig rejected
        response = printer.setConfig("paramC", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramC", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        testUsername = "A";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // start allowed
        response = printer.start(access_token);
        assert response.contains("started");
        // print allowed
        response = printer.print("test1_A", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_A", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramA", "A", access_token);
        assert response.contains(": ");
        // readConfig allowed
        response = printer.readConfig("paramA", access_token);
        assert response.contains("paramA");

        printer.stop(access_token);

        // Delete user B 
        user.deleteUserByName("B");
        accessControl.deleteAccessControlListByName("B");
        // Change the permissions of G 
        accessControl.updateAccessControlList("G", 0b110000111);
        // Add H 
        accessControl.addAccessControlList("H", 0b110000000);
        user.addUser("H", Crypto.salt(testUserPWHash), "none");
        // Add I 
        accessControl.addAccessControlList("I", 0b111001000);
        user.addUser("I", Crypto.salt(testUserPWHash), "none");

        access_token = printer.authenticate("A", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.start(access_token);
        assert response.contains("started");

        // Test the permissions of G 
        access_token = printer.authenticate("G", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_G", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");

        // Test the permissions of H 
        access_token = printer.authenticate("H", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_H", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // Test the permissions of I 
        access_token = printer.authenticate("I", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_I", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        // Delete user B 
        user.deleteUserByName("B");
        // Delete user H
        user.deleteUserByName("H");
        // Delete user I
        user.deleteUserByName("I");
        // Change the role of G 
        String originalRole = user.getUserRoleByName("G");
        user.updateUserRoleByName(originalRole + "&" + "tech", "G");
        // Add new employee H 
        user.addUser("H", Crypto.salt(testUserPWHash), "ordinary_user");
        // Add new employee I 
        user.addUser("I", Crypto.salt(testUserPWHash), "power_user");

        access_token = printer.authenticate("A", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.start(access_token);
        assert response.contains("started");

        // Test the permissions of G 
        access_token = printer.authenticate("G", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_G", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");

        // Test the permissions of H 
        access_token = printer.authenticate("H", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_H", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // Test the permissions of I 
        access_token = printer.authenticate("I", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_I", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        user.clear();
        accessControl.clear();
    }
}
