INSERT INTO global_settings (id, code, name, value) VALUES
(1, 'MULTIUSER_MODE','Многопользовательский режим','YES');
INSERT INTO global_settings (id, code, name, value) VALUES
(2, 'POST_PREMODERATION','Премодерация постов','NO');
INSERT INTO global_settings (id, code, name, value) VALUES
(3, 'STATISTICS_IS_PUBLIC','Показывать всем статистику блога','YES');

INSERT INTO users (id, is_moderator, reg_time, name, email, password, code, photo) VALUES
(1, 1, '2021-12-01 23:59:59', 'Petr Ivanov', 'petr@ivanov.com', 'c73d08de890479518ed60cf670d1', null, 'src/main/resources/user_photo/Petr.jpeg');
INSERT INTO users (id, is_moderator, reg_time, name, email, password, code, photo) VALUES
(2, 0, '2021-12-02 23:59:59', 'Ivan Petrov', 'ivan@petrov.com', 'c73d08de890470000ed60cf670d1', null, 'src/main/resources/user_photo/Petr.jpeg');
INSERT INTO users (id, is_moderator, reg_time, name, email, password, code, photo) VALUES
(3, 0, '2021-12-03 23:59:59', 'John Smith', 'jhon@smith.com', 'c73d08de000079518ed60cf670d1', null, 'src/main/resources/user_photo/Petr.jpeg');
INSERT INTO users (id, is_moderator, reg_time, name, email, password, code, photo) VALUES
(4, 0, '2021-10-03 23:59:59', 'John Ott', 'jhon@ott.com', 'c73d08de044079518ed80cf670d1', null, 'src/main/resources/user_photo/Petr.jpeg');

INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(1, 1, 'ACCEPTED', 1, 1, '2021-12-01 23:00:59', 'First post', 'Central to the Spring Framework is its inversion of control (IoC) container, which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language) files or detecting specific Java annotations on configuration classes. These data sources contain the bean definitions that provide the information required to create the beans.
Objects can be obtained by means of either dependency lookup or dependency injection.[13] Dependency lookup is a pattern where a caller asks the container object for an object with a specific name or of a specific type. Dependency injection is a pattern where the container passes objects by name to other objects, via either constructors, properties, or factory methods.', 3);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(2, 1, 'ACCEPTED', 1 , 2, '2020-12-01 23:05:59', 'News', 'Happy new year!!!', 2);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(3, 1, 'ACCEPTED', 1 , 3, '2019-12-01 23:07:59', 'Sport', 'Good luck!!!', 4);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(4, 1, 'ACCEPTED', 1 , 3, '2018-10-01 23:08:59', 'Sport', 'Good game!!!', 4);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(5, 1, 'ACCEPTED', 1, 1, '2019-12-01 23:00:59', 'Second post', 'Central to the Spring Framework is its inversion of control (IoC) container, which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language) files or detecting specific Java annotations on configuration classes. These data sources contain the bean definitions that provide the information required to create the beans.
Objects can be obtained by means of either dependency lookup or dependency injection.[13] Dependency lookup is a pattern where a caller asks the container object for an object with a specific name or of a specific type. Dependency injection is a pattern where the container passes objects by name to other objects, via either constructors, properties, or factory methods.', 3);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(6, 1, 'ACCEPTED', 1, 2, '2019-12-01 23:00:59', 'Third post', 'Central to the Spring Framework is its inversion of control (IoC) container,
which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object
lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language)
files or detecting ', 14);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(7, 1, 'ACCEPTED', 1, 2, '2015-02-01 23:00:59', 'Third post', 'Central to the Spring Framework is its inversion of control (IoC) container,
which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object
lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language)
files or detecting ', 20);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(8, 1, 'ACCEPTED', 1, 2, '2008-02-01 23:00:59', 'Third post', 'Central to the Spring Framework is its inversion of control (IoC) container,
which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object
lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language)
files or detecting ', 28);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(9, 1, 'ACCEPTED', 1, 2, '2015-02-01 23:00:59', 'Third post', 'Central to the Spring Framework is its inversion of control (IoC) container,
which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object
lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language)
files or detecting ', 265);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(10, 1, 'ACCEPTED', 1, 2, '2020-02-01 23:00:59', 'Third post', 'Central to the Spring Framework is its inversion of control (IoC) container,
which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object
lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language)
files or detecting ', 154);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(11, 1, 'ACCEPTED', 1, 2, '2020-02-01 23:00:59', 'Third post', 'Central to the Spring Framework is its inversion of control (IoC) container,
which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object
lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language)
files or detecting ', 211);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(12, 1, 'ACCEPTED', 1, 2, '2000-02-01 23:00:59', 'Third post', 'Central to the Spring Framework is its inversion of control (IoC) container,
which provides a consistent means of configuring and managing Java objects using reflection. The container is responsible for managing object
lifecycles of specific objects: creating these objects, calling their initialization methods, and configuring these objects by wiring them together.
Objects created by the container are also called managed objects or beans. The container can be configured by loading XML (Extensible Markup Language)
files or detecting ', 300);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(13, 1, 'NEW', 1 , 3, '2019-10-04 23:08:59', 'Sport', 'Good game bsa!!!', 8);

INSERT INTO post_votes (id, user_id, post_id, time, value) VALUES
(1, 1, 4, '2021-12-01 23:08:01', 1);
INSERT INTO post_votes (id, user_id, post_id, time, value) VALUES
(2, 2, 4, '2021-12-01 23:08:01', 1);
INSERT INTO post_votes (id, user_id, post_id, time, value) VALUES
(3, 2, 5, '2021-12-01 23:08:01', 1);
INSERT INTO post_votes (id, user_id, post_id, time, value) VALUES
(4, 3, 4, '2021-12-01 23:08:01', -1);

INSERT INTO tags (id, name) VALUES
(1, 'Spring');
INSERT INTO tags (id, name) VALUES
(2, 'Sport');
INSERT INTO tags (id, name) VALUES
(3, 'News');

INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
(1, null , 1, 1, '2019-12-01 23:03:01', 'Merry christmas!///...');
INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
(2, null , 4, 2, '2020-12-01 23:03:01', 'Merry christmas!');
INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
(3, null , 6, 3, '2021-12-01 23:05:01', 'Good Luck!');
--INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
--(4, null , 3, 3, '2021-12-01 23:05:01', 'Good Luck!');
--INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
--(5, null , 5, 2, '2019-12-01 23:05:01', 'Good Luck!');
--INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
--(6, null , 7, 3, '2021-12-01 23:05:01', 'Good Luck!');

--INSERT INTO captcha_codes (id, time, code, secret_code) VALUES
--(1, '2021-11-01 23:01:01', '1213214', '2256');
--INSERT INTO captcha_codes (id, time, code, secret_code) VALUES
--(2, '2021-08-01 23:04:01', '0022333', '5547');
--INSERT INTO captcha_codes (id, time, code, secret_code) VALUES
--(3, '2021-12-01 20:04:01', '55566678', '3265');

INSERT INTO tag2post (id, tag_id, post_id) VALUES
(1, 1, 10);
INSERT INTO tag2post (id, tag_id, post_id) VALUES
(2, 2, 2);
INSERT INTO tag2post (id, tag_id, post_id) VALUES
(3, 2, 7);
INSERT INTO tag2post (id, tag_id, post_id) VALUES
(4, 3, 8);
INSERT INTO tag2post (id, tag_id, post_id) VALUES
(5, 3, 9);
INSERT INTO tag2post (id, tag_id, post_id) VALUES
(6, 3, 6);