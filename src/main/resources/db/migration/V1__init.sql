-- USERS
create table if not exists users (
                                     id uuid primary key,
                                     email varchar(120) not null,
    password_hash varchar(120) not null,
    name varchar(60) not null,
    role varchar(20) not null,
    constraint uk_users_email unique(email)
    );

-- MOVIES
create table if not exists movies (
                                      id uuid primary key,
                                      title varchar(255) not null,
    overview varchar(4000),
    release_year integer,
    genres_csv varchar(255),
    tags_csv varchar(255)
    );

-- RATINGS (PK compuesta)
create table if not exists ratings (
                                       movie_id uuid not null,
                                       user_id uuid not null,
                                       score integer not null,
                                       comment varchar(2000),
    ts timestamp with time zone not null,
                     primary key (movie_id, user_id),
    constraint fk_ratings_movie foreign key (movie_id) references movies(id),
    constraint fk_ratings_user  foreign key (user_id) references users(id)
    );
