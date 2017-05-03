create table if not exists message
   (id bigint auto_increment primary key,
    sender_id bigint not null,
    receiver_id bigint not null,
    message_text varchar(1024) not null,
    time_sent timestamp not null,
    foreign key (sender_id) references voter(id),
    foreign key (receiver_id) references voter(id));