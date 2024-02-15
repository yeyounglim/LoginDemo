create database loginDemo;
use loginDemo;

drop table `users`;

CREATE TABLE `users` (
                         `user_id` int NOT NULL AUTO_INCREMENT,
                         `name` varchar(100) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `mobile` varchar(20) NOT NULL,
                         `pwd` varchar(500) NOT NULL,
                         `role` varchar(100) NOT NULL,
                         `create_dt` date DEFAULT NULL,
                         `refresh_token` varchar(100) DEFAULT NULL,
                         PRIMARY KEY (`user_id`)
);

INSERT INTO `users` (`name`,`email`,`mobile`, `pwd`, `role`,`create_dt`)
VALUES ('Happy','happy@example.com','9876548337', '$2y$12$oRRbkNfwuR8ug4MlzH5FOeui.//1mkd.RsOAJMbykTSupVy.x/vb2', 'admin',CURDATE());

{
    "name":"myname",
    "email":"hi@example.com",
    "mobile":"01020304050",
    "pwd":"12345",
    "role":"user"
}


CREATE TABLE `authorities` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `user_id` int NOT NULL,
                               `name` varchar(50) NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `user_id` (`user_id`),
                               CONSTRAINT `authorities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);



DELETE FROM `authorities`;

INSERT INTO `authorities` (`user_id`, `name`)
VALUES (1, 'ROLE_USER');

INSERT INTO `authorities` (`user_id`, `name`)
VALUES (1, 'ROLE_ADMIN');

