drop table if exists blog;
drop table if exists account_role_mapping;
drop table if exists account;
drop table if exists account_role;
drop table if exists log;

create table account
(
    id            bigint       not null auto_increment,
    email         varchar(100) not null,
    name          varchar(100) not null,
    password      varchar(256) not null,
    refresh_token varchar(512) null,
    created_at    timestamp default current_timestamp,
    modified_at   timestamp default current_timestamp,
    primary key (id)
);

create table account_role
(
    id          bigint       not null auto_increment,
    name        varchar(100) not null,
    created_at  timestamp default current_timestamp,
    modified_at timestamp default current_timestamp,
    primary key (id)
);

create table account_role_mapping
(
    id         bigint not null auto_increment,
    account_id bigint not null,
    role_id    bigint not null,
    primary key (id),
    foreign key (account_id) references account (id) on update cascade,
    foreign key (role_id) references account_role (id) on update cascade
);

create table blog
(
    id          bigint        not null auto_increment,
    title       varchar(400)  not null,
    contents    varchar(4000) not null,
    created_at  timestamp default current_timestamp,
    created_by  bigint        not null,
    modified_at timestamp default current_timestamp,
    modified_by bigint        not null,
    primary key (id),
    foreign key (created_by) references account (id) on update cascade,
    foreign key (modified_by) references account (id) on update cascade
);

create table log
(
    id         bigint        not null auto_increment,
    message    varchar(4000) not null,
    created_at timestamp default current_timestamp,
    primary key (id)
)
