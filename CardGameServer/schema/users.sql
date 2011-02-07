DROP TABLE IF EXISTS users;

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

INSERT INTO users(id, user_name, email, password, state) VALUES(1, 'Bot1', 'bot1@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(2, 'Bot2', 'bot2@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(3, 'Bot3', 'bot3@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(4, 'Bot4', 'bot4@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(5, 'Bot5', 'bot5@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(6, 'Bot6', 'bot6@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(7, 'Bot7', 'bot7@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(8, 'Bot8', 'bot8@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(9, 'Bot9', 'bot9@fakemail.com', '', 0);
INSERT INTO users(id, user_name, email, password, state) VALUES(10, 'Bot10', 'bot10@fakemail.com', '', 0);

