
## Lancer l'API en local

1. Copier `.env.example` en `.env` et remplir les valeurs (base MySQL, `JWT_SECRET`, e-mail, admin initial).
2. Placer la clé Firebase (compte de service) dans `secrets/firebase-adminsdk.json` (dossier ignoré par Git).
3. `./mvnw spring-boot:run` (port 8082). Au premier démarrage, les rôles et le compte admin sont créés.
4. Données de démo : `mysql -uroot helloway_demo < seed_demo.sql` (après une première création de comptes, voir l'en-tête du fichier).
5. Tests de sécurité : `node security_test.mjs` (30 vérifications).
