create table if not exists token
   (uuid varchar(36) unique not null,
    voter_id bigint not null,
    expiration_date timestamp not null,
    foreign key (voter_id) references voter(id));