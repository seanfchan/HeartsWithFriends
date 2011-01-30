DROP TABLE users IF EXISTS users;

CREATE TABLE users
 (
  id INTEGER NOT NULL auto_increment PRIMARY KEY,
  email VARCHAR(64),
  password VARCHAR(64),
  state INTEGER
 );
 
CREATE UNIQUE INDEX email_index ON users (email);