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
        response = printer.print("test_1_B", "printer1", access_token);
        assert response.contains("not allowed to");
        // queue rejected
        response = printer.queue("printer1", access_token);
        assert response.contains("not allowed to");
        // topQueue rejected
        printer.print("test_2_B", "printer1", access_token);
        response = printer.topQueue("printer1", 1, access_token);
        assert response.contains("not allowed to");
        // status allowed
        response = printer.status("printer1", access_token);
        assert !response.contains("not allowed to");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // setConfig allowed
        response = printer.setConfig("config_B", "B", access_token);
        assert response.contains(": ");
        // readConfig allowed
        printer.readConfig("config_B", access_token);
        assert response.contains("config_B");
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
        response = printer.print("test_1_C", "printer2", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer2", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test_2_C", "printer2", access_token);
        response = printer.topQueue("printer2", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status rejected
        response = printer.status("printer2", access_token);
        assert response.contains("not allowed to");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // setConfig rejected
        response = printer.setConfig("config_C", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("config_C", access_token);
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
        response = printer.print("test_1_C", "printer2", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer2", access_token);
        assert Objects.equals(response, "");
        // topQueue rejected
        printer.print("test_2_C", "printer2", access_token);
        response = printer.topQueue("printer2", 2, access_token);
        assert response.contains("not allowed to");
        // status rejected
        response = printer.status("printer2", access_token);
        assert response.contains("not allowed to");
        // restart rejected
        response = printer.restart(access_token);
        assert response.contains("not allowed to");
        // setConfig rejected
        response = printer.setConfig("config_C", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("config_C", access_token);
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
        response = printer.print("test_1_A", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test_2_A", "printer1", access_token);
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
        response = printer.setConfig("config_A", "A", access_token);
        assert response.contains(": ");
        // readConfig allowed
        response = printer.readConfig("config_A", access_token);
        assert response.contains("config_A");
        printer.stop(access_token);

        testUsername = "B";
        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start allowed
        response = printer.start(access_token);
        assert response.contains("started");
        // print allowed
        response = printer.print("test_1_B", "printer1", access_token);
        assert response.contains("not allowed to");
        // queue rejected
        response = printer.queue("printer1", access_token);
        assert response.contains("not allowed to");
        // topQueue rejected
        printer.print("test_2_B", "printer1", access_token);
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
        response = printer.setConfig("config_B", "B", access_token);
        assert response.contains(": ");
        // readConfig allowed
        printer.readConfig("config_B", access_token);
        assert response.contains("config_B");
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
        response = printer.print("test_1_C", "printer2", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer2", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test_2_C", "printer2", access_token);
        response = printer.topQueue("printer2", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status rejected
        response = printer.status("printer2", access_token);
        assert response.contains("not allowed to");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // setConfig rejected
        response = printer.setConfig("config_C", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("config_C", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // D, E, F, and G have same permission, so only D is tested here.
        testUsername = "D";
        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test_1_C", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue rejected
        printer.print("test_2_C", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart rejected
        response = printer.restart(access_token);
        assert response.contains("not allowed to");
        // setConfig rejected
        response = printer.setConfig("config_C", "C", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("config_C", access_token);
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
        response = printer.print("test_1_A", "printer2", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer2", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test_2_A", "printer2", access_token);
        response = printer.topQueue("printer2", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status allowed
        response = printer.status("printer2", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // setConfig allowed
        response = printer.setConfig("config_A", "A", access_token);
        assert response.contains(": ");
        // readConfig allowed
        response = printer.readConfig("config_A", access_token);
        assert response.contains("config_A");
        printer.stop(access_token);

        // Delete user B 
        user.deleteUserByName("B");
        accessControl.deleteAccessControlListByName("B");
        // Change G 
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

        // Test G's permissions
        access_token = printer.authenticate("G", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_G", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");

        // Test H's permissions
        access_token = printer.authenticate("H", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_H", "printer2", access_token);
        assert response.contains("Print task added to ");
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // Test I's permissions
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
        user.updateUserRoleByName(originalRole + "," + "tech", "G");
        // Add new employee H 
        user.addUser("H", Crypto.salt(testUserPWHash), "user");
        // Add new employee I 
        user.addUser("I", Crypto.salt(testUserPWHash), "root_user");

        access_token = printer.authenticate("A", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.start(access_token);
        assert response.contains("started");

        // Test G's permissions
        access_token = printer.authenticate("G", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_G", "printer2", access_token);
        assert response.contains("Print task added to ");
        response = printer.status("printer2", access_token);
        assert response.contains("tasks");

        // Test H's permissions
        access_token = printer.authenticate("H", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_H", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // Test I's permissions
        access_token = printer.authenticate("I", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_I", "printer2", access_token);
        assert response.contains("Print task added to ");
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        user.clear();
        accessControl.clear();
    }
}
