-- !Ups
create table pet
(
    name  varchar(255) not null,
    age   int          not null,
    color varchar(255) not null
);

-- !Downs
drop table pet;