create table if not exists district
   (id bigint primary key auto_increment,
    state varchar(2) not null,
    district_num bigint not null,
    is_senate boolean);