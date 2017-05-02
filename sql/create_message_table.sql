create table if not exists message
   (id bigint auto_increment primary key,
    foreign key (sender_id) references voter(id),
    foreign key (reciever_id) references voter(id),
    message_text varchar(255) not null,
    time_sent timestamp not null);