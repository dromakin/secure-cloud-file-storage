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
    --admin/admin https://bcrypt.online/
    (1, 'admin@localhost', '$2y$04$h9MRw6LMhaWcMatU7YL11u2KIYw9odPjqIh6sa0krj04iRBM.G94q', 'admin', 'admin', 'admin@localhost', 1, NOW(), NOW(), 'ACTIVE'),
    (2, 'test@localhost', '$2a$04$.09kYd8b8jvedKVFrSrvRuyCVILYwc/.NVZ3QvxGXYvzoq5PEo3PC', 'test', 'test', 'test@localhost', 1, NOW(), NOW(), 'ACTIVE'),
    (3, 'writer@localhost', '$2a$04$.09kYd8b8jvedKVFrSrvRuyCVILYwc/.NVZ3QvxGXYvzoq5PEo3PC', 'writer', 'writer', 'writer@localhost', 1, NOW(), NOW(), 'ACTIVE');