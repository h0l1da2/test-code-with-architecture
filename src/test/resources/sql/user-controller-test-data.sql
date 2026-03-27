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

alter table users alter column id restart with 3;
