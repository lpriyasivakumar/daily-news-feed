create table news_articles
(
    id           serial primary key,
    source_id    varchar,
    source_name  varchar not null,
    title        varchar not null,
    description  varchar not null,
    content      varchar not null,
    url          varchar not null,
    image_url    varchar not null,
    published_at timestamp default current_timestamp,
    unique (source_id)
);
