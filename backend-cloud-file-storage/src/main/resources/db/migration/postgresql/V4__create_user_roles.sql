CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id int,
    role_id int,
    CONSTRAINT user_role_pk PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES public.users(id),
    FOREIGN KEY (role_id) REFERENCES public.roles(id)
);