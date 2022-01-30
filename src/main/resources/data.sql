INSERT INTO global_settings (id, code, name, value) VALUES
(1, 'MULTIUSER_MODE','Многопользовательский режим','NO');
INSERT INTO global_settings (id, code, name, value) VALUES
(2, 'POST_PREMODERATION','Премодерация постов','NO');
INSERT INTO global_settings (id, code, name, value) VALUES
(3, 'STATISTICS_IS_PUBLIC','Показывать всем статистику блога','NO');

INSERT INTO users (id, is_moderator, reg_time, name, email, password, code, photo) VALUES
(1, 1, '2021-12-01 23:59:59', 'Petr Ivanov', 'petr@ivanov.com', 'c73d08de890479518ed60cf670d1', null, 'src/main/resources/user_photo/Petr.jpeg');
INSERT INTO users (id, is_moderator, reg_time, name, email, password, code, photo) VALUES
(2, 0, '2021-12-02 23:59:59', 'Ivan Petrov', 'ivan@petrov.com', 'c73d08de890470000ed60cf670d1', null, 'src/main/resources/user_photo/Petr.jpeg');
INSERT INTO users (id, is_moderator, reg_time, name, email, password, code, photo) VALUES
(3, 0, '2021-12-03 23:59:59', 'John Smith', 'jhon@smith.com', 'c73d08de000079518ed60cf670d1', null, 'src/main/resources/user_photo/Petr.jpeg');

INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(1, 1, 'ACCEPTED', 1, 1, '2021-12-01 23:00:59', 'First post', 'Hello, world!!!', 3);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(2, 1, 'ACCEPTED', 1 , 2, '2021-12-01 23:05:59', 'News', 'Happy new year!!!', 2);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(3, 1, 'ACCEPTED', 1 , 3, '2021-12-01 23:08:59', 'Sport', 'Good luck!!!', 4);
INSERT INTO posts (id, is_active, moderation_status, moderator_id, user_id, time, title, text, view_count) VALUES
(4, 1, 'NEW', null , 3, '2021-10-01 23:08:59', 'Sport', 'Good game!!!', 4);

INSERT INTO post_votes (id, user_id, post_id, time, value) VALUES
(1, 1, 2, '2021-12-01 23:08:01', 1);
INSERT INTO post_votes (id, user_id, post_id, time, value) VALUES
(2, 2, 3, '2021-12-01 23:08:01', 1);
INSERT INTO post_votes (id, user_id, post_id, time, value) VALUES
(3, 3, 1, '2021-12-01 23:08:01', -1);

INSERT INTO tags (id, name) VALUES
(1, 'Hello');
INSERT INTO tags (id, name) VALUES
(2, 'Sport');
INSERT INTO tags (id, name) VALUES
(3, 'News');

UPDATE post_comments SET parent_id = 1 WHERE id = 1;
INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
(2, null , 3, 2, '2021-12-01 23:03:01', 'Merry christmas!');
INSERT INTO post_comments (id, parent_id, post_id, user_id, time, text) VALUES
(3, null , 2, 1, '2021-12-01 23:05:01', 'Good Luck!');

INSERT INTO captcha_codes (id, time, code, secret_code) VALUES
(1, '2021-11-01 23:01:01', '1213214', '2256');
INSERT INTO captcha_codes (id, time, code, secret_code) VALUES
(2, '2021-08-01 23:04:01', '0022333', '5547');
INSERT INTO captcha_codes (id, time, code, secret_code) VALUES
(3, '2021-12-01 20:04:01', '55566678', '3265');