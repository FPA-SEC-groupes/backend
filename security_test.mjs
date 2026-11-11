// Tests de sécurité de l'API HelloWay (base locale de démo uniquement).
// Usage : node security_test.mjs   (API sur http://localhost:8082, données de seed_demo.sql)
const API = process.env.API || 'http://localhost:8082/api';
let pass = 0, fail = 0;

function check(name, ok, detail = '') {
  if (ok) { pass++; console.log(`  OK   ${name}`); }
  else { fail++; console.log(`  FAIL ${name} ${detail}`); }
}

async function call(method, path, { body, cookie } = {}) {
  const res = await fetch(API + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(cookie ? { Cookie: cookie } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  let json = null;
  try { json = JSON.parse(text); } catch { /* texte brut */ }
  return { status: res.status, text, json, setCookie: res.headers.get('set-cookie') };
}

async function login(username, password) {
  const r = await call('POST', '/auth/signin', { body: { username, password } });
  if (r.status !== 200) throw new Error(`login ${username} -> ${r.status} ${r.text}`);
  return { cookie: r.setCookie.split(';')[0], user: r.json };
}

const admin = await login('admin', 'Admin@2026');
const provA = await login('gerant.a', 'Gerant@2026');
const waiterA = await login('serveur.a', 'Serveur@2026');
const client = await login('client.demo', 'Client@2026');
const ids = { spaceA: 1, spaceB: 2, zoneB: 2, boardA: 1, boardB: 2, catB: 2, prodB: 2, provB: provA.user.id + 1 };

console.log('\n1. Création de comptes');
let r = await call('POST', '/auth/signup', { body: { username: 'pirate1', email: 'pirate1@x.tn', password: 'Pirate@2026', role: ['admin'] } });
check('inscription publique en admin refusée', r.status === 403, r.status);
r = await call('POST', '/auth/signup', { body: { username: 'pirate2', email: 'pirate2@x.tn', password: 'Pirate@2026', role: ['provider'] } });
check('inscription publique en gérant refusée', r.status === 403, r.status);
r = await call('POST', '/auth/signup', { body: { username: 'client' + (Date.now() % 100000), email: 'c' + Date.now() + '@x.tn', password: 'Client@2026' } });
check('inscription publique client acceptée', r.status === 200, r.status);
r = await call('POST', '/auth/signup', { cookie: provA.cookie, body: { username: 'serv' + (Date.now() % 100000), email: 's' + Date.now() + '@x.tn', password: 'Serveur@2026', role: ['waiter'] } });
check('gérant crée un serveur', r.status === 200, r.status + ' ' + r.text);
r = await call('POST', '/auth/signup', { cookie: admin.cookie, body: { username: 'ger' + (Date.now() % 100000), email: 'g' + Date.now() + '@x.tn', password: 'Gerant@2026', role: ['provider'] } });
check('admin crée un gérant', r.status === 200, r.status + ' ' + r.text);

console.log('\n2. Mots de passe');
r = await call('POST', '/auth/reset-password', { body: { email: 'gerant.a@helloway.demo', newPassword: 'Hacked@2026' } });
check('ancien /reset-password public supprimé', r.status === 404 || r.status === 405 || r.status === 401, r.status);
r = await call('POST', '/auth/change-password', { body: { currentPassword: 'x', newPassword: 'Hacked@2026' } });
check('change-password sans connexion refusé', r.status === 401, r.status);
r = await call('POST', '/auth/change-password', { cookie: client.cookie, body: { currentPassword: 'mauvais', newPassword: 'Nouveau@2026' } });
check('change-password avec mauvais ancien mot de passe refusé', r.status === 400, r.status);
r = await call('POST', '/auth/change-password', { cookie: client.cookie, body: { currentPassword: 'Client@2026', newPassword: 'Client@2027' } });
check('change-password correct accepté', r.status === 200, r.status);
await call('POST', '/auth/change-password', { cookie: client.cookie, body: { currentPassword: 'Client@2027', newPassword: 'Client@2026' } });
const resetA = await call('POST', '/auth/reset-password/email', { body: { email: 'inconnu@x.tn' } });
const resetB = await call('POST', '/auth/reset-password/email', { body: { email: 'client.demo@helloway.demo' } });
check('reset e-mail : même réponse compte existant ou non', resetA.text === resetB.text, resetA.text + ' / ' + resetB.text);
r = await login('client.demo', 'Client@2026');
check('reset e-mail ne change pas le mot de passe actuel', !!r.cookie);
for (let i = 0; i < 6; i++) {
  r = await call('POST', '/auth/reset-password/confirm', { body: { email: 'client.demo@helloway.demo', code: '000000', newPassword: 'Hacked@2026' } });
}
check('code de reset faux refusé (et bloqué après 5 essais)', r.status === 400, r.status);

console.log('\n3. Comptes des autres');
r = await call('PUT', '/users/update', { cookie: client.cookie, body: { id: provA.user.id, username: 'gerant.a', email: 'pirate@x.tn' } });
check('client ne modifie pas le compte du gérant', r.status === 403, r.status);
r = await call('PUT', '/users/update', { cookie: provA.cookie, body: { id: ids.provB, username: 'gerant.b', email: 'pirate@x.tn' } });
check('gérant A ne modifie pas le gérant B', r.status === 403, r.status);
r = await call('PUT', '/users/update', { cookie: client.cookie, body: { id: client.user.id, username: 'client.demo', email: 'client.demo@helloway.demo', name: 'Client', activated: true } });
check('client modifie son propre compte', r.status === 200, r.status + ' ' + r.text);
r = await call('DELETE', `/users/delete/${ids.provB}`, { cookie: provA.cookie });
check('gérant A ne supprime pas le gérant B', r.status === 403, r.status);
r = await call('GET', `/users/id/${provA.user.id}`, { cookie: admin.cookie });
check('le hash du mot de passe n\'est jamais renvoyé', r.status === 200 && r.json && !('password' in r.json), r.text.slice(0, 120));

console.log('\n4. Données d\'un autre restaurant');
r = await call('PUT', '/spaces/update', { cookie: provA.cookie, body: { id_space: ids.spaceB, titleSpace: 'Pirate' } });
check('gérant A ne modifie pas l\'espace B', r.status === 403, r.status);
r = await call('DELETE', `/zones/delete/${ids.zoneB}`, { cookie: provA.cookie });
check('gérant A ne supprime pas une zone de B', r.status === 403, r.status);
r = await call('DELETE', `/categories/delete/${ids.catB}`, { cookie: provA.cookie });
check('gérant A ne supprime pas une catégorie de B', r.status === 403, r.status);
r = await call('DELETE', `/products/delete/${ids.prodB}`, { cookie: waiterA.cookie });
check('serveur A ne supprime pas un produit de B', r.status === 403, r.status);
r = await call('GET', `/zones/all/id_space/${ids.spaceA}`, { cookie: provA.cookie });
check('gérant A lit ses zones', r.status === 200, r.status);
r = await call('POST', `/categories/add/id_space/${ids.spaceA}`, { cookie: provA.cookie, body: { categoryTitle: 'Test ' + (Date.now() % 100000) } });
check('gérant A ajoute une catégorie chez lui', r.status === 200, r.status + ' ' + r.text.slice(0, 120));
r = await call('GET', `/qrcodes/${ids.boardB}/zoneId/${ids.zoneB}`, { cookie: provA.cookie });
check('gérant A ne génère pas le QR d\'une table de B', r.status === 403, r.status);

console.log('\n5. Wi-Fi et tables');
r = await call('GET', `/auth/space/${ids.spaceA}`);
check('liste Wi-Fi publique sans mot de passe', r.status === 200 && Array.isArray(r.json) && r.json.length > 0 && r.json.every(w => !('password' in w)), r.text.slice(0, 120));
r = await call('GET', '/wifis/', { cookie: provA.cookie });
check('liste de tous les Wi-Fi réservée à l\'admin', r.status === 403, r.status);
r = await call('POST', `/auth/signin/qr_Code/${ids.boardA}-2026-01-01-1/userLatitude/0/userLongitude/0/0`);
check('QR fabriqué (sans jeton) refusé', r.status === 400, r.status + ' ' + r.text);
r = await call('POST', `/auth/signin/qr_Code/${ids.boardA}`);
check('ancienne connexion table sans contrôle supprimée', r.status === 404 || r.status === 405 || r.status === 401, r.status);
r = await call('POST', '/auth/signin', { body: { username: 'Board1', password: 'Pass1*1' } });
check('compte table : connexion par mot de passe refusée', r.status === 401, r.status);
r = await call('GET', `/qrcodes/${ids.boardA}/zoneId/1`, { cookie: provA.cookie });
check('gérant A génère le QR de sa table', r.status === 200 && r.text.length > 100, r.status);

console.log(`\nRésultat : ${pass} OK, ${fail} échec(s)`);
process.exit(fail ? 1 : 0);
