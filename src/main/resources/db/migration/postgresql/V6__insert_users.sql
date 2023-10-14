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