-- movies de demo
insert into movies (id, title, overview, genres_csv, tags_csv, release_year) values
                                                                                 ('11111111-1111-1111-1111-111111111111','Peli A',null,'Drama,Romance','amor,drama',1999),
                                                                                 ('22222222-2222-2222-2222-222222222222','Peli B',null,'Sci-Fi,Action','naves,robots',2010),
                                                                                 ('33333333-3333-3333-3333-333333333333','Peli C',null,'Action,Comedy','coches,persecuciones',2012),
                                                                                 ('44444444-4444-4444-4444-444444444444','Peli D',null,'Drama,Thriller','misterio,policial',2018);

-- ratings de demo (u1/u2/u3)
insert into ratings (movie_id,user_id,score,comment,ts) values
                                                            ('11111111-1111-1111-1111-111111111111','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',5,'Top', current_timestamp),
                                                            ('22222222-2222-2222-2222-222222222222','aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',4,'Guay', current_timestamp),
                                                            ('33333333-3333-3333-3333-333333333333','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',5,'Me flipa', current_timestamp),
                                                            ('11111111-1111-1111-1111-111111111111','bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',4,'Buena', current_timestamp),
                                                            ('44444444-4444-4444-4444-444444444444','cccccccc-cccc-cccc-cccc-cccccccccccc',5,'Obra maestra', current_timestamp);
