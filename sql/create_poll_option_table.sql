create table if not exists poll_option
   (id bigint primary key auto_increment,
    poll_id bigint not null,
    option varchar(255) not null,
    votes bigint not null default 0,
    foreign key (poll_id) references poll(id));