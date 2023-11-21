CREATE TABLE IF NOT EXISTS users (
  username varchar(12) NOT NULL,
  password_hash char(60) NOT NULL,
  role varchar(20) NOT NULL,
  PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS roles (
  role varchar(12) NOT NULL,
  print int NOT NULL,
  queue int NOT NULL,
  top_queue int NOT NULL,
  start int NOT NULL,
  stop int NOT NULL,
  restart int NOT NULL,
  status int NOT NULL,
  read_config int NOT NULL,
  set_config int NOT NULL,
  PRIMARY KEY (role)
);

CREATE TABLE IF NOT EXISTS access_control_list (
  username varchar(12) NOT NULL,
  print int NOT NULL,
  queue int NOT NULL,
  top_queue int NOT NULL,
  start int NOT NULL,
  stop int NOT NULL,
  restart int NOT NULL,
  status int NOT NULL,
  read_config int NOT NULL,
  set_config int NOT NULL,
  PRIMARY KEY (username)
);

INSERT into roles (role, print, queue, top_queue, start, stop, restart, status, read_config, set_config) VALUES
('boss', 1, 1, 1, 1, 1, 1, 1, 1, 1),
('user', 1, 1, 0, 0, 0, 0, 0, 0, 0),
('staff', 0, 0, 0, 1, 1, 1, 0, 0, 0),
('root_user', 1, 1, 1, 0, 0, 1, 0, 0, 0),
('tech', 0, 0, 0, 0, 0, 0, 1, 1, 1);
