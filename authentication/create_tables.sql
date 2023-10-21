CREATE TABLE IF NOT EXISTS users (
  username varchar(12) NOT NULL,
  password_hash char(60) NOT NULL,
  PRIMARY KEY (username)
);
