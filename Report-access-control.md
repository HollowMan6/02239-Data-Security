# Access Control Lab

> Group: 19
>
> - s233509: Giancarlo Andriano
> - s233508: Jawad Zaheer
> - s233511: Songlin Jiang

## Introduction

<!-- > (max 1 page)
> The introduction should provide a general introduction to the problem of access control in client/server applications. It should define the scope of the answer, i.e. explicitly state what problems are considered, and outline the proposed solution. Finally, it should clearly state which of the identified goals are met by the developed software. -->

In applications employing a client/server model, diverse users typically interact with the system in distinct manners, based on their distinct permissions and roles they possess. Consider a teaching management system, where only teachers have the authority to create courses, and students are confined to taking exams. Consequently, a pivotal consideration for our printing system revolves around devising an effective access control mechanism to ensure each user operates within their designated realm of permissions. This report delves into two such mechanisms: access control lists (ACL) and role-based access control (RBAC).

Access control lists involve associating each user with a specific list detailing permitted operations. When a user attempts a specific operation, the application scrutinizes their permissions in the access control list, determining whether to authorize or deny the action.

On the other hand, the role-based access control model connects roles with concrete permission lists rather than individual users. To regulate users, the system assigns roles, utilizing these roles to govern user permissions.

For our devised solution, we've outlined the following objectives:

1. Integration of both access control list and role-based access control mechanisms.
2. The ability to interchange between these mechanisms via a configuration file.
3. Specification of the access control list in an external policy file or database loaded during printer server startup, steering clear of hardcoded elements in the program.
   Definition of the role hierarchy in an external policy file or database loaded during printer server startup.
4. Adaptability of both mechanisms to accommodate shifts in company employee roles.
5. Implementation of pertinent integration testing to evaluate the functionality of both access control list and role-based access control mechanisms. Further details can be found in the ensuing sections.

## Access Control Lists

<!-- > (max 2 pages)
> This section should provide a short overview of the implementation of the access control lists and the motivation behind all non-trivial design choices. -->

This segment offers a concise glimpse into the execution of the access control lists mechanism. Notably, the access control list mechanism is a component of the discretionary access control framework (DAC). Employing DAC involves a general strategy utilizing the access matrix.
• The matrix's one dimension encompasses identified subjects authorized to attempt resource access.
• The other dimension catalogs the objects available for access.
Each matrix entry delineates the access rights of a specific subject to a particular object. Recognizing the typically sparse nature of the access matrix, it can be broken down by columns, resulting in access control lists (ACLs). These ACLs enumerate users and their sanctioned access privileges. Drawing inspiration from the ACL concept, we have delineated the access control list employed for the printing service, presented in table below.

| username | print | queue | topQueue | start | stop | restart | status | readConfig | setConfig |
| -------- | ----- | ----- | -------- | ----- | ---- | ------- | ------ | ---------- | --------- |
| Alice    | 1     | 1     | 1        | 1     | 1    | 1       | 1      | 1          | 1         |
| Cecilia  | 1     | 1     | 1        | 1     | 1    | 1       | 1      | 0          | 0         |
| Bob      | 0     | 0     | 0        | 1     | 1    | 1       | 1      | 1          | 1         |
| David    | 1     | 1     | 1        | 1     | 1    | 1       | 0      | 0          | 0         |
| Erica    | 1     | 1     | 1        | 0     | 0    | 0       | 0      | 0          | 0         |
| Fred     | 1     | 1     | 1        | 0     | 0    | 0       | 0      | 0          | 0         |
| George   | 1     | 1     | 1        | 0     | 0    | 0       | 0      | 0          | 0         |

Within the table, the first column displays the identities of the seven users/entities authorized to access the system. Columns 2 to 10 outline various operations applicable to the printing system. Due to distinct user permissions, the specific entitlement is represented by binary integers (0 & 1). A value of 0 signifies false, indicating the user lacks authorization for that operation, while a value of 1 denotes true, granting the user permission. This table is logged into a database table and stored alongside the user table. Notably, there are no provided APIs for adding user information to the access control list table and user table. Any relevant data inclusion must occur through database manipulation or the utilization of JDBC APIs within the system beforehand.

To seamlessly integrate the access control mechanism into the application, our initial step involves defining a Java interface named AccessControlModel, as illustrated below:

```
public interface AccessControlModel {
  boolean isMethodGranted(String username, String method);
}
```

Contained within the interface is solely a single method titled isMethodGranted. This method accepts the user name and method name as parameters, undertaking the verification of user authorization for method usage. The determination is contingent upon the specific implementation within the class. Each concrete implementation class delineates the protocol for evaluating user permissions, with variability based on distinct access control mechanisms.

Facilitating the transition between these mechanisms necessitates the introduction of a novel entry in the service.properties file, denoted as accessControlModel. The alteration of the key's value to accessControlList is imperative for the utilization of the access control list mechanism, as demonstrated below:

```
accessControlModel = accessControlList
```

Upon initialization, the program will interpret this configuration and opt for an appropriate sub-class to function as the AccessControlModel entity. As illustrated here:

```
if (definedAccessControlModel.equals("accessControlList"))
 accessControlModel = new AccessControlList();
```

The AccessControlList class serves as a derivative [TODO: is this the designated term for class implementation] implementing the AccessControlModel interface through the utilization of the access control list mechanism. It customizes the isMethodGranted method in the subsequent manner.

```
@Override
public boolean isMethodGranted(String username, String method) {
    Map<String, Boolean> accessControlListByName = accessControlRepository.
    getAccessControlListByName(username);
    return accessControlListByName.get(method);
}
```

The accessControlRepository object is employed to interact with the database, retrieving authorization details based on the username. It retrieves user permission details from the access control list table, organizing them into a map. The conversion involves treating 0 as false and 1 as true. Subsequently, the method cross-references the map with the method name, providing a boolean value indicating whether the user is permitted to employ this method.

For every method within the printing system, the access control mechanism is applied by verifying user permissions through the specific instance of the AccessControlModel interface. The specifics are outlined as follows:

```
String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
String userName = sessionUsernames.get(cookie);
if (!accessControlModel.isMethodGranted(userName, methodName))
return "You are not allowed to perform this operation";
```

The initial line of code retrieves the name of the presently invoked method, while the subsequent line fetches the username from the `sessionUsernames` object. Subsequently, the acquired username and method name are supplied to the `isMethodGranted` function of the `accessControlModel` object to scrutinize permissions. Any lack of authorization prompts the notification of the user.

Regarding our design decisions, we opted to store the access control list in the database due to its suitability for concurrent access scenarios. Given our previous decision to store user information in the database, consolidating all sensitive information in one place seemed more appropriate than dispersing it across different locations. Additionally, the use of Java interfaces was preferred for a degree of abstraction and encapsulation, providing flexibility for potential future changes in the access control mechanisms, such as the integration of role-based access control.

To assess the functionality of the access control list mechanism, we introduced a corresponding integration test named `AccessControlListTest`. Notably, the database tables are devoid of information at program startup. During test execution, user information and associated permissions are inserted, and subsequently, these entries are cleared upon test completion. In a practical implementation, user credentials and the access control list ought to be contained in the database.

## Role Based Access Control

<!-- > (max 3 pages including diagrams)
> This section should document the results of the role mining process performed in in Task 2 and provide a short overview of the implementation of the role based access control mechanism implemented in Task 3 along with the motivation behind all non-trivial design choices. In particular, it must describe the syntax used to specify the RBAC policy. -->

For the subsequent assignment, our objective involves recognizing the roles, outlining a hierarchical structure for these roles, and specifying permissions for each role in order to enact the prescribed access control strategy. The roles and their hierarchical arrangement are presented in Table 2, as well as depicted in Figure 1 and Figure 2.

Table 2:
| username | print | queue | topQueue | start | stop | restart | status | readConfig | setConfig |
| ------------- | ----- | ----- | -------- | ----- | ---- | ------- | ------ | ---------- | --------- |
| manager | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 |
| ordinary_user | 1 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
| janitor | 0 | 0 | 0 | 1 | 1 | 0 | 0 | 0 | 0 |
| power_user | 1 | 1 | 1 | 0 | 0 | 1 | 0 | 0 | 0 |
| service_tech | 0 | 0 | 0 | 0 | 0 | 0 | 1 | 1 | 1 |

<figure align="center" >
  <img src="./role-heirarchy.png" caption="" alt="Sublime's custom image"/>
  <figcaption>Figure: 1 Role Heirarchy</figcaption>
</figure>

<figure align="center" >
  <img src="./role-heirarchy-venn-diagram.png" caption="" alt="Sublime's custom image"/>
  <figcaption>Figure: 2 Role Heirarchy Venn Diagram</figcaption>
</figure>

The presence of five distinct roles is evident, with the table illustrating the permissions associated with each operation for every role. Similar to Table 1, the representation involves assigning 1 for true and 0 for false.

To articulate the role-based access control (RBAC) policy, an additional column is introduced into the user table to store the user's designated role. If a user assumes multiple roles, these roles are linked with the "&" symbol, exemplified in Table 3.

The application of the specified role table for RBAC mechanism implementation begins with the creation of a new subclass termed RoleBasedControl, which implements the AccessControlModel interface, as outlined below:

Table 3: User Table with Roles:

| **username** | **role**        | **password_hash** |
| ------------ | --------------- | ----------------- |
| Alice        | manager         | xxx               |
| Cecilia      | power_user      | xxx               |
| David        | ordinary_user   | xxx               |
| Bob          | janitor/service | xxx               |
| Fred         | ordinary_user   | xxx               |
| George       | ordinary_user   | xxx               |
| Erica        | ordinary_user   | xxx               |

```
public class RoleBasedControl implements AccessControlModel{
    @Override
    public boolean isMethodGranted(String username, String method)
    {
        Set<String> roleSet = new HashSet<>();
        String userRoleByName = userRepository.getUserRoleByName(username);
        if (!userRoleByName.contains("&"))
        roleSet.add(userRoleByName);
        else
        roleSet.addAll(Arrays.stream(userRoleByName.split("&")).toList());
        return isMethodGrantedForRole(method, roleSet);
    }
}
```

The modified function, `isMethodGranted`, entails retrieving the roles linked to the user from the user table. Subsequently, the role string is disentangled using the "&" symbol, and the resulting components are stored in the role set. The ensuing method, `isMethodGrantedForRole`, examines whether the role linked to the user facilitates the execution of the specified method. The specifics of the `isMethodGrantedForRole` function are expounded upon as follows:

```
private boolean isMethodGrantedForRole(String method, Set<String> roleSet) {
    for (var i : roleSet) {
        Map<String, Boolean> accessControlListByRole = roleRepository.
        getAccessControlListByRole(i);
        if (accessControlListByRole.get(method))
        return true;
    }
    return false;
}

```

For the activation of the access control mechanism based on roles, an adjustment in the configuration file service.properties is requisite. The alteration pertains to the key accessControlModel, with the new value set as "roleBasedAccessControl," as exemplified below:

```
accessControlModel = roleBasedAccessControl
```

Similar to the approach adopted for the access control list, we opt to maintain the role table in the database to ensure uniformity in data storage. Additionally, we introduce a relevant integration test titled RoleBasedControlTest to validate the efficacy of the Role Based Access Control mechanism.

## Evaluation

<!-- > (max 4 pages)
> This section should document that the prototype enforces the access control policies defined in this assignment; both ACL and RBAC and both before and after the changes.
The evaluation should provide a simple summary of which of the requirements are satisfied and which are not. -->

Within this segment, we provide an account of the prototype that enforces the access control policies outlined in this task. Commencing with the discussion on access control list (ACL), two tables are pertinent to ACL, specifically the user table and the access control list table. The existing contents of these tables before any modifications are presented below:

Table 4: User Table

| **username** | **role** | **password_hash** |
| ------------ | -------- | ----------------- |
| Alice        | none     | xxx               |
| Cecilia      | none     | xxx               |
| David        | none     | xxx               |
| Bob          | none     | xxx               |
| Fred         | none     | xxx               |
| George       | none     | xxx               |
| Erica        | none     | xxx               |

In this context, the role attribute for all users is configured as "none" since we are presently utilizing the access control list mechanism. The role attribute is irrelevant to our current scenario, and its assignment to "none" signifies that this attribute is not active. Additionally, the password hashes are excluded in this presentation.

The user table, in this instance, solely handles user authentication. Detailed access control policies are stored in the access control list table, as illustrated in Table 5.

Table 5: Access Control List

| username | print | queue | topQueue | start | stop | restart | status | readConfig | setConfig |
| -------- | ----- | ----- | -------- | ----- | ---- | ------- | ------ | ---------- | --------- |
| Alice    | 1     | 1     | 1        | 1     | 1    | 1       | 1      | 1          | 1         |
| Cecilia  | 1     | 1     | 1        | 0     | 0    | 1       | 0      | 0          | 0         |
| Bob      | 0     | 0     | 0        | 1     | 1    | 1       | 1      | 1          | 1         |
| David    | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| Erica    | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| Fred     | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| George   | 1     | 1     | 1        | 0     | 0    | 0       | 0      | 0          | 0         |

Following this, alterations are made to the content of the two tables to align with modifications in the company's personnel. Initially, Bob's details are removed from both the user table and access control list table. Subsequently, as George assumes some of Bob's responsibilities and takes on the role of a service technician, corresponding permissions are assigned to George in the access control list. New employees can be effortlessly incorporated into both the user table and access control list table, accompanied by relevant permission details. Post the organizational changes, the revised content of the two tables is illustrated in Table 6 and Table 7.

For specific testing purposes, two integration tests have been defined. The first, mentioned in section 2, is termed `AccessControlListTest`, examining the functionality of the access control list prototype. The second test, named `StaffChangeOnAccessControlListTest`, involves adding all initial employees to the database and subsequently modifying the database content to simulate shifts in company staff. This test evaluates whether the ACL mechanism effectively adheres to the revised policies.

Table 6: User Table Updated

| **username** | **role** | **password_hash** |
| ------------ | -------- | ----------------- |
| Alice        | none     | xxx               |
| Cecilia      | none     | xxx               |
| David        | none     | xxx               |
| Ida          | none     | xxx               |
| Fred         | none     | xxx               |
| George       | none     | xxx               |
| Erica        | none     | xxx               |
| Henry        | none     | xxx               |

Table 7: Access Control List Updated

| username | print | queue | topQueue | start | stop | restart | status | readConfig | setConfig |
| -------- | ----- | ----- | -------- | ----- | ---- | ------- | ------ | ---------- | --------- |
| Alice    | 1     | 1     | 1        | 1     | 1    | 1       | 1      | 1          | 1         |
| Cecilia  | 1     | 1     | 1        | 0     | 0    | 1       | 0      | 0          | 0         |
| David    | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| Erica    | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| Fred     | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| George   | 1     | 1     | 0        | 0     | 0    | 0       | 1      | 1          | 1         |
| Ida      | 1     | 1     | 1        | 0     | 0    | 1       | 0      | 0          | 0         |
| Henry    | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |

The implementation of the access control list mechanism has successfully met the following requirements:

• Development of the access control list mechanism prototype.
• Specification of the access control list in an external file.
• Incorporation of modifications to the access control list prototype to reflect changes in company personnel.
• Introduction of a unit test named `AccessControlListTest` to assess the functionality of the access control list mechanism, ensuring effective user access control.
• Integration of a unit test named `StaffChangeOnAccessControlListTest` to simulate shifts in company staff and validate the adaptive nature of the access control list mechanism.

Subsequently, we transition to role-based access control (RBAC). Two pertinent tables associated with RBAC include the user table and the roles table, presented in Table 8 and Table 9.

Table 8: User Table with roles

| **username** | **role**             | **password_hash** |
| ------------ | -------------------- | ----------------- |
| Alice        | manager              | xxx               |
| Cecilia      | power_user           | xxx               |
| Bob          | janitor&service_tech | xxx               |
| David        | ordinary_user        | xxx               |
| Erica        | ordinary_user        | xxx               |
| Fred         | ordinary_user        | xxx               |
| George       | ordinary_user        | xxx               |

Table 9: The role table

| username      | print | queue | topQueue | start | stop | restart | status | readConfig | setConfig |
| ------------- | ----- | ----- | -------- | ----- | ---- | ------- | ------ | ---------- | --------- |
| manager       | 1     | 1     | 1        | 1     | 1    | 1       | 1      | 1          | 1         |
| janitor       | 1     | 1     | 1        | 0     | 0    | 1       | 0      | 0          | 0         |
| power_user    | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| service_tech  | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |
| ordinary_user | 1     | 1     | 0        | 0     | 0    | 0       | 0      | 0          | 0         |

In contrast to the previous table, roles are introduced for users in this setup, with the password_hash field still excluded. To accommodate changes in the company's organizational structure, the initial step involves removing Bob from the user table. Subsequently, the role of George is adjusted from "ordinary_user" to "ordinary_user&service_tech," with different roles connected using "&."For the two recently included employees, Ida is assigned the role of power_user, while Henry is designated as an ordinary_user. It's worth noting that the role table remains unchanged, as no alterations to roles have occurred. The updated user table is presented in Table 10.

Table 10: User Table updated

| **username** | **role**                   | **password_hash** |
| ------------ | -------------------------- | ----------------- |
| Alice        | manager                    | xxx               |
| Cecilia      | power_user                 | xxx               |
| David        | ordinary_user              | xxx               |
| Fred         | ordinary_user              | xxx               |
| Erica        | ordinary_user              | xxx               |
| George       | ordinary_user&service_tech | xxx               |
| Henry        | ordinary_user              | xxx               |
| Ida          | power_user                 | xxx               |

The implementation of the role-based access control mechanism has successfully met the following requirements:

• Development of the role-based access control mechanism prototype.
• Specification of the role hierarchy in an external file.
• Incorporation of modifications to the role-based access control prototypes to reflect changes in company personnel.
• Introduction of an integration test named RoleBasedControlTest to assess the functionality of the role-based access control mechanism, ensuring effective user access control.
• Implementation of an integration test named StaffChangedOnRoleBasedControlTest to simulate shifts in company staff and validate the adaptive nature of the role-based access control mechanism.

However, both access control systems fall short in addressing two major requirements. Firstly, the absence of a registration API necessitates manual insertion of all users into the database. For testing purposes, users are initially inserted through the database API, after which the access control mechanism is tested on them. Secondly, there is a lack of APIs to facilitate changes in user permissions, which can only be achieved through direct manipulation of the database. These limitations contribute to the application's complexity in user management. Additionally, the absence of a proper UI must be acknowledged as a drawback, as it is crucial for the practical usability of the application.

## Discussion

<!-- > (max 2 page)
> This section documents the reflections and discussions of the final task. -->

In contrast to the role-based access control (RBAC) mechanism, we posit that the access control list (ACL) mechanism provides greater flexibility for organizations with less pronounced hierarchies. The ACL allows administrators to finely define the permissibility of each operation for specific users, making it suitable for smaller organizations with manageable effort requirements. Conversely, RBAC governs operation permissions collectively, making ACL a favorable choice for organizations desiring fine-grained access control. For minor adjustments, such as permitting Erica to perform a restart operation, the admin only needs to modify the corresponding ACL table value from 0 to 1. If RBAC is employed, the admin must create a new role for this change, potentially leading to role redundancy and management challenges. If frequent fine-grained permission changes occur, RBAC may prove more cumbersome to handle.

This advantage is particularly evident in systems with fewer users, such as startups with around 20 employees and limited resources. In such dynamic environments where employee roles are fluid, an ACL system offers superior adaptability.

Contrary to ACL, we posit that the strength of RBAC lies in its ability to efficiently manage permissions for a large number of users accessing numerous resources. This is especially beneficial for large organizations, alleviating administrative burdens compared to ACL. In RBAC, the role system acts as middleware, simplifying the complexity of concrete permission control. Admins only need to ensure that users are assigned the correct role, without delving into intricate access control policies to detect potential permission misassignments. In significant organizations where employee position changes occur frequently, RBAC significantly reduces the risk of misconfiguration and enhances system security.

Regarding expressive power, we contend that both ACL and RBAC adeptly capture organizational changes related to user enrollment and departure. ACL excels in specifying user permission adjustments, while RBAC is particularly effective in delineating changes in employee positions, especially when such changes are commonplace.

## Conclusion

<!-- > (max 1 page)
> The conclusions should summarize the problems addressed in the report and clearly identify which of the requirements are satisfied and which are not (a summary of Section 4). The conclusions may also include a brief outline of future work. -->

In this laboratory exercise focused on access control, we integrated access control mechanisms into the system previously developed in the authentication laboratory. The implemented access control mechanisms encompass the access control list (ACL) and role-based access control (RBAC). Initially, we constructed prototypes for both mechanisms and subsequently refined them to adapt to organizational changes in employee roles. Two distinct figures present a clearly defined role hierarchy. To ensure the accuracy of these mechanisms, we developed corresponding integration tests to simulate their expected functionality.

Additionally, we fulfilled the requirement of documenting the access control policy in external media. Similar to the previous lab, we stored relevant information in the database, aligning with real-world system practices. A configuration item was defined to facilitate the switch between the two access control mechanisms, covering all outlined requirements in Section 1 Introduction. However, certain unmet requirements include the absence of an interface for user registration and permission changes, impacting the practical utility of the application. Moreover, from the user's perspective, the system lacks a proper user interface; all functions are invoked through Java Remote Method Invocation, making practical use challenging.

For future endeavors, we posit that exploring the integration of other access control mechanisms, such as attribute-based access control, could be beneficial. A comparative analysis with the mechanisms discussed in this report could guide the selection of the most suitable one. Additionally, prioritizing the implementation of a user-friendly interface would enhance the overall user experience.
