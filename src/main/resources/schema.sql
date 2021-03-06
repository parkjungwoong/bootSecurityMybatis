DROP TABLE IF EXISTS CITY;

CREATE TABLE CITY (ID INT PRIMARY KEY AUTO_INCREMENT, NAME VARCHAR, COUNTRY VARCHAR, POPULATION INT);

INSERT INTO CITY (NAME, COUNTRY, POPULATION) VALUES ('San Francisco', 'US', 10000);
INSERT INTO CITY (NAME, COUNTRY, POPULATION) VALUES ('서울', 'KR', 20000);
INSERT INTO CITY (NAME, COUNTRY, POPULATION) VALUES ('東京', 'JP', 30000);
INSERT INTO CITY (NAME, COUNTRY, POPULATION) VALUES ('부산', 'KR', 40000);


-- 유저 테이블
DROP TABLE IF EXISTS USER_INFO;

CREATE TABLE USER_INFO (ID VARCHAR PRIMARY KEY, PW VARCHAR, NAME VARCHAR, PHONE VARCHAR, SVC_ID VARCHAR, USE_YN VARCHAR, ROLE VARCHAR);

INSERT INTO USER_INFO (ID, PW , NAME, PHONE, SVC_ID, USE_YN, ROLE) VALUES ('myuoong', '1234' ,'박정웅', '01084226318', '9988', 'Y', 'ROLE_ADMIN');
INSERT INTO USER_INFO (ID, PW , NAME, PHONE, SVC_ID, USE_YN, ROLE) VALUES ('admin', '1234' ,'관리자', '01084226318', '7788', 'Y', 'ROLE_ADMIN');

-- 인증 테이블

DROP TABLE IF EXISTS AUTH_TRADE;

CREATE TABLE AUTH_TRADE (TRADE_NO VARCHAR PRIMARY KEY, USER_ID VARCHAR, REG_DT VARCHAR, AUTO_YN VARCHAR);