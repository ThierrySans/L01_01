CREATE DATABASE IF NOT EXISTS C01ProjectDB;
USE C01ProjectDB;

CREATE TABLE course(
	cid INT AUTO_INCREMENT,
	course VARCHAR(255),
	PRIMARY KEY(cid)
);

CREATE TABLE users(
	uid INT AUTO_INCREMENT,
	uname VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	cid INT,
	type VARCHAR(255),
	PRIMARY KEY (uid),
	FOREIGN KEY(cid) REFERENCES course(cid)
);
CREATE TABLE question(
	qid INT AUTO_INCREMENT,
	question LONGTEXT,
	answer VARCHAR(255),
	course INT,
	type VARCHAR(255),
	PRIMARY KEY(qid),
	FOREIGN KEY(course) REFERENCES course(cid)
);
CREATE TABLE mc(
	qid INT,
	choice LONGTEXT,
	PRIMARY KEY(qid),
	FOREIGN KEY(qid) REFERENCES question(qid)
);
CREATE TABLE assignment(
	aid INT AUTO_INCREMENT, 
	qid INT,
	cid INT,
	PRIMARY KEY(aid),
	FOREIGN KEY(qid) REFERENCES question(qid),
	FOREIGN KEY(cid) REFERENCES course(cid)
);
CREATE TABLE marks(
	student INT,
	aid INT, 
	cid INT,
	mark INT,
	PRIMARY KEY(student, aid),
	FOREIGN KEY(aid) REFERENCES assignment(aid),
	FOREIGN KEY(cid) REFERENCES course(cid),
	FOREIGN KEY(student) REFERENCES users(uid)
);