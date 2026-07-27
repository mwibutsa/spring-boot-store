alter table users
    add constraint users_unique_email_key
        unique (email);