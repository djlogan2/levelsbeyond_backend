drop database if exists levelsbeyond;
create database levelsbeyond;

use levelsbeyond;

create table notes (
	id integer not null auto_increment,
   body varchar(255),
#	body text,
#   fulltext(body),
   primary key(id)
) ENGINE=InnoDB;
