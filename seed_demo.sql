-- Données de démo locales (base helloway_demo) : 2 restaurants pour tester l'isolation.
-- Comptes créés par l'API : admin, client.demo (2), gerant.a (3), gerant.b (4), serveur.a (5).
INSERT INTO spaces (id_space, title_space, description, latitude, longitude, nb_reserve_of_space, number_of_rate, number_of_rating, rating, surface_enm2, phone_number, validation)
VALUES (1, 'Café Medina', 'Café du centre-ville', '36.8065', '10.1815', 0, 0, 0, 0, 120, 71000000, 'gps'),
       (2, 'Resto Marina', 'Restaurant du port', '36.8500', '10.3200', 0, 0, 0, 0, 200, 71000001, 'wifi');
UPDATE users SET moderator_space_id_space = 1 WHERE username = 'gerant.a';
UPDATE users SET moderator_space_id_space = 2 WHERE username = 'gerant.b';
UPDATE users SET id_space_for_server = 1 WHERE username = 'serveur.a';
INSERT INTO zones (id_zone, zone_title, id_space) VALUES (1, 'Terrasse', 1), (2, 'Salle', 2);
INSERT INTO boards (id_table, activated, availability, num_table, place_number, zone_id_zone) VALUES (1, 1, 1, 1, 4, 1), (2, 1, 1, 1, 2, 2);
INSERT INTO categories (id_category, category_title, id_space) VALUES (1, 'Cafés', 1), (2, 'Plats', 2);
INSERT INTO products (id_product, product_title, description, price, available, order_index, id_categorie) VALUES (1, 'Café crème', 'Expresso et lait', 3.5, 1, 0, 1), (2, 'Couscous poisson', 'Poisson du jour', 28, 1, 0, 2);
INSERT INTO wifis (id, ssid, password, space_id) VALUES (1, 'Medina-Guest', 'motdepasse-wifi-secret', 1);
