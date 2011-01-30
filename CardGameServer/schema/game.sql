DROP TABLE game IF EXISTS game;

CREATE TABLE game
(
	id INTEGER NOT NULL auto_increment PRIMARY KEY,
	current_players INTEGER NOT NULL,
	max_players INTEGER NOT NULL,
	state INTEGER NOT NULL /* 0 -> ACTIVE, 1 -> WAITING */
);