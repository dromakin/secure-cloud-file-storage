INSERT INTO public.roles(
    id, name
) VALUES
      (1, 'ROLE_USER'),
      (2, 'ROLE_WRITER'),
      (3, 'ROLE_ADMIN');

INSERT INTO public.users(
    id,
    login,
    password,
    first_name,
    last_name,
    email,
    last_enter,
    created,
    updated,
    status
) VALUES
      --admin/admin
      (1, 'admin@localhost', '$2a$04$.09kYd8b8jvedKVFrSrvRuyCVILYwc/.NVZ3QvxGXYvzoq5PEo3PC', 'admin', 'admin', 'admin@localhost', 1, NOW(), NOW(), 'ACTIVE'),
      (2, 'test@localhost', '$2a$04$.09kYd8b8jvedKVFrSrvRuyCVILYwc/.NVZ3QvxGXYvzoq5PEo3PC', 'test', 'test', 'test@localhost', 1, NOW(), NOW(), 'ACTIVE'),
      (3, 'writer@localhost', '$2a$04$.09kYd8b8jvedKVFrSrvRuyCVILYwc/.NVZ3QvxGXYvzoq5PEo3PC', 'writer', 'writer', 'writer@localhost', 1, NOW(), NOW(), 'ACTIVE');

INSERT INTO public.user_roles(user_id, role_id)
VALUES (1, 3), (2, 1), (3, 2);