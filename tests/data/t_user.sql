CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

do
$$
    begin
        for cnt in 1..1000
            loop
                INSERT INTO t_user (id, account_locked, active, confirmation_string, created_at, email,
                                           last_seen, online,
                                           password, phone_number, roles, username, valid_until)
                VALUES (uuid_generate_v4(), false, true, null, '2023-07-28 14:20:46.421039',
                        concat('dummy', cnt, '@email.com'), null, false,
                        '$2a$12$3PW2mNHzbZnyeKmYrdkn/uUeCYs44WvCDd260NNJKkg4z/9Rc7kt6',
                        concat('', 1000000000 + cnt), 'ROLE_USER', concat('dummy', cnt), null);
            end loop;
    end;
$$
