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
  belongs_to_id             bigint,
  constraint pk_contact primary key (id))
;

create table contact_group (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_contact_group primary key (id))
;

create table users (
  email                     varchar(255) not null,
  password                  varchar(255),
  is_admin                  boolean,
  constraint pk_users primary key (email))
;


create table contact_group_users (
  contact_group_id               bigint not null,
  users_email                    varchar(255) not null,
  constraint pk_contact_group_users primary key (contact_group_id, users_email))
;
create sequence contact_seq;

create sequence contact_group_seq;

create sequence users_seq;

alter table contact add constraint fk_contact_belongsTo_1 foreign key (belongs_to_id) references contact_group (id) on delete restrict on update restrict;
create index ix_contact_belongsTo_1 on contact (belongs_to_id);



alter table contact_group_users add constraint fk_contact_group_users_contac_01 foreign key (contact_group_id) references contact_group (id) on delete restrict on update restrict;

alter table contact_group_users add constraint fk_contact_group_users_users_02 foreign key (users_email) references users (email) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists contact;

drop table if exists contact_group;

drop table if exists contact_group_users;

drop table if exists users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists contact_seq;

drop sequence if exists contact_group_seq;

drop sequence if exists users_seq;

