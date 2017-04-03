create table if not exists voter
   (id bigint primary key auto_increment,
    name varchar(255) not null,
    password_hash varchar(255) not null, // BCrypt's hash format includes a salt.
    house_district_id int not null,
    senate_district_id int not null,
    phone_number varchar(10) not null,
    address varchar(255) not null,
    email varchar(255) unique not null,

    // The following field is used only for representatives, and so can be null
    representing_district_id int,

    foreign key (house_district_id) references district(id),
    foreign key (senate_district_id) references district(id),
    foreign key (representing_district_id) references district(id));