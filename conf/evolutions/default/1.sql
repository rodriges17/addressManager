# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contact (
  id                        bigint not null,
  name                      varchar(255),
  first_name                varchar(255),
  email                     varchar(255),
  street                    varchar(255),
  city                      varchar(255),
  phone                     varchar(255),
  constraint pk_contact primary key (id))
;

create table contact_group (
  name                      varchar(255) not null,
  constraint pk_contact_group primary key (name))
;

create table user (
  email                     varchar(255) not null,
  password                  varchar(255),
  constraint pk_user primary key (email))
;


create table contact_group_user (
  contact_group_name             varchar(255) not null,
  user_email                     varchar(255) not null,
  constraint pk_contact_group_user primary key (contact_group_name, user_email))
;
create sequence contact_seq;

create sequence contact_group_seq;

create sequence user_seq;




alter table contact_group_user add constraint fk_contact_group_user_contact_01 foreign key (contact_group_name) references contact_group (name) on delete restrict on update restrict;

alter table contact_group_user add constraint fk_contact_group_user_user_02 foreign key (user_email) references user (email) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists contact;

drop table if exists contact_group;

drop table if exists contact_group_user;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists contact_seq;

drop sequence if exists contact_group_seq;

drop sequence if exists user_seq;

