use moviedb;

ALTER TABLE sales
	ADD quantity int,
    ADD orderId int UNIQUE;
    