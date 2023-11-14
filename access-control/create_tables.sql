CREATE TABLE IF NOT EXISTS users (
  username varchar(12) NOT NULL,
  password_hash char(60) NOT NULL,
  role varchar(20) NOT NULL,
  PRIMARY KEY (username)
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
