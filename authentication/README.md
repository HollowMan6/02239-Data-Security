# Authentication Lab
> Group: 19
> - s233509: Giancarlo Andriano
> - s233508: Jawad Zaheer
> - s233511: Songlin Jiang

## Introduction
> (max 1 page)
> The introduction should provide a general introduction to the problem of authentication in client/server applications. It should define the scope of the answer, i.e. explicitly state what problems are considered, and outline the proposed solution. Finally, it should clearly state which of the identified goals are met by the developed software.

Client/server applications necessitate server-controlled client access to maintain sustainable services. An authentication mechanism is crucial for client identification and service provisioning based on server-defined policies.

## Authentication
> (max 3 pages)
> This section should provide a short introduction to the specific problem of password based authentication in client/server systems and analyse the problems relating to password storage (on the server), password transport (between client and server) and password verification.

### Password Storage
The authentication secret is the username and password. The server needs to securely store and access these credentials. Storing passwords in plaintext is avoided. Instead, the password is hashed with a unique salt before saving it.

#### System file
A system file is used for storing data, providing confidentiality and integrity based on file permissions. However, it may suffer from privilege escalation attacks and lacks robust concurrent access support.

#### Public file
A public file with cryptographic protection can be used for storing secrets. Data is encrypted before writing and decrypted before reading. This approach ensures confidentiality and integrity.

#### DBMS
Database systems are the industry standard for storing user credentials. They offer confidentiality and integrity features. While they can be vulnerable to SQL injection attacks, prepared statements can mitigate this risk.

#### Why We Favour DBMS
Database systems excel in scenarios with simultaneous user access. They provide indexing, concurrency support, and isolation mechanisms, making them suitable for multi-user environments.

## Design and Implementation
> (max 3 pages including diagrams)
> A software design for the proposed solution must be presented and explained, i.e. why is this particular design chosen. The implementation of the designed authentication mechanism in the client server application must also be outlined in this section.

### Design
The software employs a client-server architecture. The server exposes methods for client use, and interactions are facilitated via Java Remote Method Invocation (RMI).

### The Implementation of Authentication Mechanism
User authentication is conducted using the designed mechanism. Test users are added to the database, and their passwords are securely stored. Authentication includes initial authentication and session-based authentication.

## Evaluation
> (max 2 pages)
> This section should document that the requirements defined in Section 2 have been satisfied by the implementation. In particular, the evaluation should demonstrate that the user is always authenticated by the server before the service is invoked, e.g. the username and methodname may be written to a logfile every time a service is invoked. The evaluation should provide a simple summary of which of the requirements are satisfied and which are not.

### Security Requirements
#### Password Transport
All communication is secured by TLS, guaranteeing confidentiality, integrity, and authentication. Passwords are hashed on the client side before transmission.

#### Password Verification
Passwords are securely hashed, and authentication responses are generic to prevent user enumeration attacks.

#### Password Storage
Passwords are securely hashed and stored in a database, providing scalability and resilience. Prepared statements are used to prevent SQL injection attacks.

#### Out of Scope
Certain features like password strength checks and password exposure checks are considered out of scope.

### Verifying Security Requirements
#### Logging
User actions are logged, demonstrating authentication precedes service requests.

#### Unit Tests
Unit tests validate database interactions and authentication processes.

## Conclusion
> (max 1 page)
> The conclusions should summarize the problems addressed in the report and clearly identify which of the requirements are satisfied and which are not (a summary of Section 4). The conclusions may also include a brief outline of future work.

The authentication mechanism, combined with secure design practices, ensures the confidentiality, integrity, and availability of the printing service. Further scalability considerations and performance enhancements should be explored for real-world deployment.
