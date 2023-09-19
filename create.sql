
    create table beer (
        beer_style tinyint not null check (beer_style between 0 and 9),
        price decimal(38,2) not null,
        quantity_on_hand integer,
        version integer,
        created_date datetime(6),
        update_date datetime(6),
        upc varchar(10) not null,
        id varchar(36) not null,
        beer_name varchar(50) not null,
        primary key (id)
    ) engine=InnoDB;

    create table customer (
        version integer,
        created_date datetime(6),
        last_modified_date datetime(6),
        id varchar(36) not null,
        name varchar(255),
        primary key (id)
    ) engine=InnoDB;
