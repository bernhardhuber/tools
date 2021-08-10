/**
 * Author:  pi
 * Created: Jul 4, 2021
 */

---
create table IF NOT EXISTS  BUG_ENTITY (
    ID bigint generated by default as identity, 
    VERSION bigint not null, 
    CREATED_WHEN timestamp, 
    UPDATED_WHEN timestamp, 

    BUG_ID varchar(128) not null, 
    BUG_TITLE varchar(512) not null, 
    BUG_DESCRIPTION clob, 
    BUG_PRIORITY varchar(64) not null, 
    BUG_STATUS varchar(64) not null, 
    primary key (ID)
);

alter table BUG_ENTITY add constraint UK_BUG_ID unique (BUG_ID);
