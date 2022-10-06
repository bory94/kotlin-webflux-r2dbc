insert into account (email, password, name)
values ('admin@bory.com', 'password!', 'ADMIN');

insert into account (email, password, name)
values ('user@bory.com', 'password!', 'USER');

insert into account_role (name)
values ('ROLE_ADMIN');

insert into account_role (name)
values ('ROLE_USER');

insert into account_role_mapping (account_id, role_id)
select (select id from account where email = 'admin@bory.com') as account_id,
       (select id from account_role where name = 'ROLE_ADMIN') as role_id;

insert into account_role_mapping (account_id, role_id)
select (select id from account where email = 'admin@bory.com') as account_id,
       (select id from account_role where name = 'ROLE_USER')  as role_id;

insert into account_role_mapping (account_id, role_id)
select (select id from account where email = 'user@bory.com') as account_id,
       (select id from account_role where name = 'ROLE_USER') as role_id;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE1'                                                as title,
       'CONTENTS1'                                             as contents,
       (select id from account where email = 'user@bory.com')  as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE2'                                                as title,
       'CONTENTS2'                                             as contents,
       (select id from account where email = 'user@bory.com')  as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE3'                                                as title,
       'CONTENTS3'                                             as contents,
       (select id from account where email = 'user@bory.com')  as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;

insert into blog
    (title, contents, created_by, modified_by)
select 'TITLE4'                                                as title,
       'CONTENTS4'                                             as contents,
       (select id from account where email = 'admin@bory.com') as created_by,
       (select id from account where email = 'admin@bory.com') as modified_by;
