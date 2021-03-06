# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table contact (
  id                        bigint not null,
  name                      varchar(255),
  first_name                varchar(255),
  title                     varchar(255),
  email                     varchar(255),
  phone                     varchar(255),
  street                    varchar(255),
  appendix1                 varchar(255),
  appendix2                 varchar(255),
  zipcode                   varchar(255),
  city                      varchar(255),
  country                   varchar(255),
  created_at                timestamp,
  last_edited_at            timestamp,
  is_edited                 boolean,
  membership_since          varchar(255),
  member_category           varchar(255),
  yearbook_subscription     boolean,
  constraint pk_contact primary key (id))
;

create table contact_group (
  id                        bigint not null,
  name                      varchar(255),
  constraint pk_contact_group primary key (id))
;

create table cm_users (
  email                     varchar(255) not null,
  password                  varchar(255),
  is_admin                  boolean,
  constraint pk_cm_users primary key (email))
;


create table contact_group_users (
  contact_group_id               bigint not null,
  cm_users_email                 varchar(255) not null,
  constraint pk_contact_group_users primary key (contact_group_id, cm_users_email))
;

create table contact_group_contact (
  contact_group_id               bigint not null,
  contact_id                     bigint not null,
  constraint pk_contact_group_contact primary key (contact_group_id, contact_id))
;
create sequence contact_seq;

create sequence contact_group_seq;

create sequence cm_users_seq;




alter table contact_group_users add constraint fk_contact_group_users_contac_01 foreign key (contact_group_id) references contact_group (id) on delete restrict on update restrict;

alter table contact_group_users add constraint fk_contact_group_users_cm_use_02 foreign key (cm_users_email) references cm_users (email) on delete restrict on update restrict;

alter table contact_group_contact add constraint fk_contact_group_contact_cont_01 foreign key (contact_group_id) references contact_group (id) on delete restrict on update restrict;

alter table contact_group_contact add constraint fk_contact_group_contact_cont_02 foreign key (contact_id) references contact (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists contact;

drop table if exists contact_group_contact;

drop table if exists contact_group;

drop table if exists contact_group_users;

drop table if exists cm_users;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists contact_seq;

drop sequence if exists contact_group_seq;

drop sequence if exists cm_users_seq;

