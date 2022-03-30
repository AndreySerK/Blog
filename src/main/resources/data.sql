INSERT INTO global_settings (id, code, name, value) VALUES
(1, 'MULTIUSER_MODE', '', 'YES'),
(2, 'POST_PREMODERATION', '', 'NO'),
(3, 'STATISTICS_IS_PUBLIC', '', 'YES');

INSERT INTO posts (id, is_active, moderation_status, moderator_id, text, time, title, user_id, view_count) VALUES
(1, 1, 'ACCEPTED', NULL, 'The Spring Framework has its own Aspect-oriented programming (AOP) framework that modularizes cross-cutting concerns in aspects. The motivation for creating a separate AOP framework comes from the belief that it should be possible to provide basic AOP features without too much complexity in either design, implementation, or configuration. The Spring AOP framework also takes full advantage of the Spring container', '2022-03-19 22:32:52', 'post', 1, 5),
(2, 1, 'ACCEPTED', 1, 'programming language and development platform. It reduces costs, shortens development timeframes,
drives innovation, and improves application services. With millions of developers running more than 51 billion Virtual
Machines worldwide, continues to be the development platform of choice for enterprises and developers.', '2022-03-20 00:46:38', 'Java', 3, 6);

INSERT INTO users (id, code, email, is_moderator, name, password, photo, reg_time, post_comment_id) VALUES
(1, NULL, 'ivan@petrov.com', 1, 'Ivan', '$2a$12$JHGCIj7YSA5OU.MUbso4XeP1O8P4XehDrqEEL/QwXS/xTll36q7pC', NULL, '2022-03-19 20:57:22', NULL),
(2, NULL, 'petr@ivanov.com', 1, 'Petr', '$2a$12$4.5vkx7vRFgso5xCUicAmOb8kGW5FzsZmXEeFkFmhL5g7BwsndFJW', NULL, '2022-03-19 22:38:08', NULL),
(3, 'nr450s4753b3p81nar', 'red_ak@mail.ru', 0, 'Андрей', '$2a$12$nZ46I28Fx2XFsg34K9jQnu1u0bjtnmIkAZ3vqHnIDPfKiuEzZe/R6', 'C:\\Users\\1111\\Blog\\src\\main\\resources\\user_photo\\Leva.jpg', '2022-03-20 00:12:09', NULL);