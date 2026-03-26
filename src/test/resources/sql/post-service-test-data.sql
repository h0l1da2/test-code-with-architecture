insert into users (
                   id,
    email,
    nickname,
    address,
    certification_code,
    status,
    last_login_at
)
values (1,
           'aaaa@naver.com',
           'aaaa',
           'Seoul',
           'aaaaaaaaaaaaaaaa',
           'ACTIVE',
           0
       );

insert into users (
    id,
    email,
    nickname,
    address,
    certification_code,
    status,
    last_login_at
)
values (2,
        'bbbb@naver.com',
        'bbbb',
        'Seoul',
        'bbbbbbbbb',
        'PENDING',
        0
       );

insert into `posts` (id, content, created_at, modified_at, user_id)
values (1, 'hello world', 1678530673958, 0, 1);

alter table `users` alter column id restart with 3;
alter table `posts` alter column id restart with 3;
