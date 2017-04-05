create table if not exists poll
   (id bigint auto_increment primary key,
    submitter_id bigint not null,
    district_id bigint not null,
    title varchar(255) not null,
    foreign key (district_id) references district(id),
    foreign key (submitter_id) references voter(id));