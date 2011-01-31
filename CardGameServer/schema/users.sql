DROP TABLE users IF EXISTS users;

CREATE TABLE users
 (
  id INTEGER NOT NULL auto_increment PRIMARY KEY,
  user_name VARCHAR(64),
  email VARCHAR(64),
  password VARCHAR(64),
  state INTEGER
 );
 
CREATE UNIQUE INDEX user_name_index ON users (user_name);
CREATE UNIQUE INDEX email_index ON users (email);
