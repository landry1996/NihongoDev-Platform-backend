# NihongoDev Platform — Guide de Deploiement

## Table des matieres

1. [Pre-requis](#1-pre-requis)
2. [Lancement en local (developpement)](#2-lancement-en-local-developpement)
3. [Deploiement sur VPS (production)](#3-deploiement-sur-vps-production)
4. [Configuration Nginx](#4-configuration-nginx)
5. [SSL / HTTPS avec Let's Encrypt](#5-ssl--https-avec-lets-encrypt)
6. [Maintenance et monitoring](#6-maintenance-et-monitoring)
7. [Troubleshooting](#7-troubleshooting)

---

## 1. Pre-requis

### 1.1 Outils necessaires

| Outil | Version minimum | Local | VPS |
|-------|-----------------|-------|-----|
| Git | 2.x | Oui | Oui |
| Java (JDK) | 21 | Oui | Non (Docker) |
| Maven | 3.9+ | Oui | Non (Docker) |
| Node.js | 20+ | Oui | Oui |
| npm | 10+ | Oui | Oui |
| Docker | 24+ | Oui | Oui |
| Docker Compose | 2.20+ | Oui | Oui |
| Nginx | 1.24+ | Non | Oui |
| PostgreSQL | 16 | Via Docker | Via Docker |
| Redis | 7 | Via Docker | Via Docker |
| Kafka | 7.6 (Confluent) | Via Docker | Via Docker |

### 1.2 Ports utilises

| Service | Port | Description |
|---------|------|-------------|
| Backend (Spring Boot) | 8080 | API REST |
| Frontend (Angular dev) | 4200 | Serveur de developpement |
| PostgreSQL | 5432 | Base de donnees |
| Redis | 6379 | Cache et rate limiting |
| Kafka | 9092 | Message broker |
| Zookeeper | 2181 | Coordination Kafka |
| Nginx | 80 / 443 | Reverse proxy (prod) |

### 1.3 Repositories

```bash
# Backend
git clone https://github.com/landry1996/NihongoDev-Platform-backend.git

# Frontend
git clone https://github.com/landry1996/NihongoDev-Platform-frontend.git
```

---

## 2. Lancement en local (developpement)

### 2.1 Etape 1 : Cloner les repositories

```bash
mkdir ~/projects && cd ~/projects
git clone https://github.com/landry1996/NihongoDev-Platform-backend.git NihongoDev
git clone https://github.com/landry1996/NihongoDev-Platform-frontend.git NihongoDev-frontend
```

### 2.2 Etape 2 : Demarrer l'infrastructure (Docker)

```bash
cd NihongoDev

# Demarrer PostgreSQL + Redis + Kafka + Zookeeper
docker compose up -d postgres redis zookeeper kafka
```

Verifier que tout est demarre :

```bash
docker compose ps
```

Resultat attendu :

```
NAME                    STATUS              PORTS
nihongodev-postgres     Up (healthy)        0.0.0.0:5432->5432/tcp
nihongodev-redis        Up (healthy)        0.0.0.0:6379->6379/tcp
nihongodev-zookeeper    Up                  2181/tcp
nihongodev-kafka        Up                  0.0.0.0:9092->9092/tcp
```

### 2.3 Etape 3 : Configurer les secrets

```bash
# Copier le template de secrets
cp src/main/resources/application-secret.example.yml src/main/resources/application-secret.yml
```

Editer `application-secret.yml` :

```yaml
app:
  jwt:
    # Generer avec : openssl rand -base64 64
    secret: VOTRE_SECRET_64_CARACTERES_MINIMUM_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

spring:
  datasource:
    password: nihongodev
  mail:
    username: votre-email@gmail.com
    password: votre-app-password-gmail
  redis:
    password:
```

> **Note** : Pour le `MAIL_PASSWORD` Gmail, creez un "Mot de passe d'application" dans
> les parametres de securite Google : https://myaccount.google.com/apppasswords

> **Note** : Pour la cle LLM, ajoutez dans `application-secret.yml` :
> ```yaml
> app:
>   llm:
>     api-key: sk-ant-api03-VOTRE_CLE_ANTHROPIC
> ```

### 2.4 Etape 4 : Lancer le backend

```bash
cd ~/projects/NihongoDev

# Compiler et lancer (profil dev + secret)
mvn spring-boot:run -Dspring-boot.run.profiles=dev,secret
```

Ou avec le wrapper Maven (si disponible) :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,secret
```

Verifier que le backend demarre :

```bash
curl http://localhost:8080/api/health
```

Reponse attendue :

```json
{
  "status": "UP",
  "service": "nihongodev-platform",
  "version": "1.0.0",
  "timestamp": "2026-05-12T10:00:00Z"
}
```

Documentation API Swagger disponible a : http://localhost:8080/swagger-ui.html

### 2.5 Etape 5 : Lancer le frontend

```bash
cd ~/projects/NihongoDev-frontend

# Installer les dependances
npm install

# Lancer le serveur de developpement
npm start
```

Le frontend est accessible a : http://localhost:4200

> Le frontend en mode dev appelle l'API sur `http://localhost:8080/api` (configure dans
> `src/environments/environment.ts`).

### 2.6 Resume : commandes de lancement local

```bash
# Terminal 1 : Infrastructure
cd ~/projects/NihongoDev
docker compose up -d postgres redis zookeeper kafka

# Terminal 2 : Backend
cd ~/projects/NihongoDev
mvn spring-boot:run -Dspring-boot.run.profiles=dev,secret

# Terminal 3 : Frontend
cd ~/projects/NihongoDev-frontend
npm start
```

### 2.7 Arreter l'environnement local

```bash
# Arreter le backend : Ctrl+C dans le terminal 2
# Arreter le frontend : Ctrl+C dans le terminal 3

# Arreter l'infrastructure Docker
cd ~/projects/NihongoDev
docker compose down

# Pour supprimer aussi les donnees (reset complet) :
docker compose down -v
```

---

## 3. Deploiement sur VPS (production)

### 3.1 Pre-requis VPS

| Specification | Minimum | Recommande |
|---------------|---------|------------|
| RAM | 2 GB | 4 GB |
| CPU | 1 vCPU | 2 vCPU |
| Disque | 20 GB SSD | 40 GB SSD |
| OS | Ubuntu 22.04 LTS | Ubuntu 24.04 LTS |
| Bande passante | 1 TB/mois | Illimitee |

### 3.2 Etape 1 : Preparer le serveur

Connectez-vous en SSH :

```bash
ssh root@VOTRE_IP_VPS
```

Installer les dependances :

```bash
# Mettre a jour le systeme
apt update && apt upgrade -y

# Installer les outils necessaires
apt install -y curl git nginx certbot python3-certbot-nginx ufw

# Installer Docker
curl -fsSL https://get.docker.com | sh
systemctl enable docker && systemctl start docker

# Installer Docker Compose (plugin)
apt install -y docker-compose-plugin

# Installer Node.js 20 (pour build frontend)
curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
apt install -y nodejs

# Verifier les installations
docker --version
docker compose version
node --version
npm --version
nginx -v
```

### 3.3 Etape 2 : Configurer le firewall

```bash
ufw allow OpenSSH
ufw allow 'Nginx Full'
ufw enable

# Verifier
ufw status
```

> **Important** : Ne PAS exposer les ports 5432 (PostgreSQL), 6379 (Redis), 9092 (Kafka)
> au public. Seuls SSH (22), HTTP (80) et HTTPS (443) doivent etre ouverts.

### 3.4 Etape 3 : Creer la structure du projet

```bash
# Creer les repertoires
mkdir -p /opt/nihongodev
mkdir -p /var/www/nihongodev

# Cloner les repositories
cd /opt/nihongodev
git clone https://github.com/landry1996/NihongoDev-Platform-backend.git backend
git clone https://github.com/landry1996/NihongoDev-Platform-frontend.git frontend
```

### 3.5 Etape 4 : Configurer les variables d'environnement

```bash
cd /opt/nihongodev/backend

# Copier le template
cp .env.example .env

# Editer avec les vraies valeurs
nano .env
```

Contenu du fichier `.env` (remplacer les valeurs) :

```bash
# Base de donnees
POSTGRES_DB=nihongodev
POSTGRES_USER=nihongodev
POSTGRES_PASSWORD=Un_Mot_De_Passe_Tres_Fort_2026!

# Connexion DB Spring
DATABASE_URL=jdbc:postgresql://postgres:5432/nihongodev
DATABASE_USERNAME=nihongodev
DATABASE_PASSWORD=Un_Mot_De_Passe_Tres_Fort_2026!

# Redis
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=Redis_Password_Fort_2026!

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
KAFKA_HOST=VOTRE_IP_VPS

# JWT (generer avec : openssl rand -base64 64)
JWT_SECRET=COLLEZ_ICI_LE_RESULTAT_DE_openssl_rand_base64_64

# Email SMTP
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=votre-email@gmail.com
MAIL_PASSWORD=votre-app-password

# LLM Anthropic
LLM_API_KEY=sk-ant-api03-VOTRE_CLE_API
LLM_MODEL=claude-sonnet-4-6-20250514

# CORS (votre domaine)
CORS_ALLOWED_ORIGINS=https://votre-domaine.com

# Spring
SPRING_PROFILES_ACTIVE=prod
```

Generer le JWT_SECRET :

```bash
openssl rand -base64 64
# Copier le resultat dans .env
```

### 3.6 Etape 5 : Lancer le backend (Docker)

```bash
cd /opt/nihongodev/backend

# Build et demarrage de toute la stack
docker compose up -d

# Verifier que tout est lance
docker compose ps

# Suivre les logs du backend
docker compose logs -f app
```

Attendre que le backend soit pret (30-60 secondes au premier lancement, Flyway execute les 13 migrations) :

```bash
# Tester le health check
curl http://localhost:8080/api/health
```

### 3.7 Etape 6 : Builder et deployer le frontend

```bash
cd /opt/nihongodev/frontend

# Installer les dependances
npm install

# Build de production
npx ng build --configuration=production

# Deployer les fichiers statiques
rm -rf /var/www/nihongodev/browser
cp -r dist/nihongodev-frontend/browser /var/www/nihongodev/

# Verifier
ls /var/www/nihongodev/browser/
# Doit contenir : index.html, styles-*.css, chunk-*.js, etc.
```

### 3.8 Etape 7 : Configurer Nginx

```bash
# Supprimer la config par defaut
rm -f /etc/nginx/sites-enabled/default

# Creer la config NihongoDev
nano /etc/nginx/sites-available/nihongodev
```

Contenu du fichier :

```nginx
server {
    listen 80;
    server_name votre-domaine.com www.votre-domaine.com;

    root /var/www/nihongodev/browser;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml text/javascript image/svg+xml;
    gzip_min_length 1000;

    # Cache fichiers statiques (noms hashes par Angular)
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # SPA routing - renvoie index.html pour toutes les routes Angular
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy API vers le backend Spring Boot
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 90s;
        proxy_connect_timeout 30s;
        proxy_send_timeout 90s;

        # Taille max des requetes (upload)
        client_max_body_size 2M;
    }

    # Actuator (acces restreint a localhost uniquement)
    location /actuator/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        allow 127.0.0.1;
        deny all;
    }

    # Headers de securite
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Permissions-Policy "camera=(), microphone=(), geolocation=()" always;
}
```

Activer et tester :

```bash
# Activer le site
ln -s /etc/nginx/sites-available/nihongodev /etc/nginx/sites-enabled/

# Tester la configuration
nginx -t

# Recharger Nginx
systemctl reload nginx
```

Le site est maintenant accessible sur : http://votre-domaine.com

---

## 4. Configuration Nginx

### 4.1 Sans nom de domaine (IP directement)

Si vous n'avez pas encore de nom de domaine, remplacez `server_name` par :

```nginx
server_name _;
```

Le site sera accessible via : http://VOTRE_IP_VPS

### 4.2 Avec sous-domaine API separe

Si vous preferez separer frontend et API :

```nginx
# Frontend : nihongodev.com
server {
    listen 80;
    server_name nihongodev.com;
    root /var/www/nihongodev/browser;
    location / { try_files $uri $uri/ /index.html; }
}

# API : api.nihongodev.com
server {
    listen 80;
    server_name api.nihongodev.com;
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

> **Note** : Si vous utilisez un sous-domaine API separe, modifiez `CORS_ALLOWED_ORIGINS`
> dans `.env` et `environment.prod.ts` dans le frontend avec l'URL complete de l'API.

---

## 5. SSL / HTTPS avec Let's Encrypt

### 5.1 Installer le certificat

```bash
# S'assurer que le domaine pointe vers l'IP du VPS (DNS A record)

# Installer le certificat SSL
certbot --nginx -d votre-domaine.com -d www.votre-domaine.com

# Suivre les instructions :
# - Entrer votre email
# - Accepter les conditions
# - Choisir "redirect HTTP to HTTPS"
```

Certbot modifie automatiquement la config Nginx pour ajouter SSL.

### 5.2 Renouvellement automatique

```bash
# Tester le renouvellement
certbot renew --dry-run

# Le renouvellement automatique est configure via un timer systemd
systemctl status certbot.timer
```

### 5.3 Apres SSL : mettre a jour CORS

Dans `/opt/nihongodev/backend/.env` :

```bash
CORS_ALLOWED_ORIGINS=https://votre-domaine.com,https://www.votre-domaine.com
```

Puis redemarrer le backend :

```bash
cd /opt/nihongodev/backend
docker compose restart app
```

---

## 6. Maintenance et monitoring

### 6.1 Commandes utiles

```bash
# --- Logs ---
# Logs du backend (temps reel)
docker compose -f /opt/nihongodev/backend/docker-compose.yml logs -f app

# Logs de tous les services
docker compose -f /opt/nihongodev/backend/docker-compose.yml logs -f

# Logs Nginx
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# --- Etat des services ---
# Tous les conteneurs
docker compose -f /opt/nihongodev/backend/docker-compose.yml ps

# Health check backend
curl -s http://localhost:8080/api/health | python3 -m json.tool

# Metriques (si actuator actif)
curl -s http://localhost:8080/actuator/health | python3 -m json.tool

# --- Redemarrage ---
# Redemarrer le backend uniquement
docker compose -f /opt/nihongodev/backend/docker-compose.yml restart app

# Redemarrer toute la stack
docker compose -f /opt/nihongodev/backend/docker-compose.yml restart

# --- Base de donnees ---
# Se connecter a PostgreSQL
docker exec -it nihongodev-postgres psql -U nihongodev -d nihongodev

# Backup de la base
docker exec nihongodev-postgres pg_dump -U nihongodev nihongodev > backup_$(date +%Y%m%d).sql

# Restaurer un backup
cat backup_20260512.sql | docker exec -i nihongodev-postgres psql -U nihongodev -d nihongodev
```

### 6.2 Mise a jour du projet

```bash
# --- Mise a jour Backend ---
cd /opt/nihongodev/backend
git pull origin main
docker compose up -d --build app

# Attendre le health check
sleep 30 && curl http://localhost:8080/api/health

# --- Mise a jour Frontend ---
cd /opt/nihongodev/frontend
git pull origin main
npm install
npx ng build --configuration=production
rm -rf /var/www/nihongodev/browser
cp -r dist/nihongodev-frontend/browser /var/www/nihongodev/
```

### 6.3 Backups automatiques (cron)

```bash
# Editer le crontab
crontab -e

# Ajouter ces lignes :
# Backup DB tous les jours a 3h du matin
0 3 * * * docker exec nihongodev-postgres pg_dump -U nihongodev nihongodev | gzip > /opt/nihongodev/backups/db_$(date +\%Y\%m\%d).sql.gz

# Nettoyer les backups de plus de 30 jours
0 4 * * * find /opt/nihongodev/backups -name "*.sql.gz" -mtime +30 -delete
```

Creer le dossier de backups :

```bash
mkdir -p /opt/nihongodev/backups
```

### 6.4 Monitoring des ressources

```bash
# Utilisation memoire/CPU des conteneurs
docker stats --no-stream

# Espace disque
df -h

# Memoire systeme
free -h

# Processus
htop
```

### 6.5 Alertes basiques (optionnel)

Creer un script de surveillance `/opt/nihongodev/monitor.sh` :

```bash
#!/bin/bash
HEALTH=$(curl -sf http://localhost:8080/api/health)
if [ $? -ne 0 ]; then
    echo "ALERTE: Backend NihongoDev DOWN a $(date)" | mail -s "NihongoDev DOWN" votre-email@gmail.com
    # Tentative de redemarrage automatique
    cd /opt/nihongodev/backend && docker compose restart app
fi
```

Ajouter au cron (toutes les 5 minutes) :

```bash
*/5 * * * * /opt/nihongodev/monitor.sh
```

---

## 7. Troubleshooting

### 7.1 Le backend ne demarre pas

```bash
# Voir les logs
docker compose logs app --tail 100

# Causes frequentes :
# 1. PostgreSQL pas encore pret → attendre ou relancer
docker compose restart app

# 2. JWT_SECRET manquant → verifier .env
grep JWT_SECRET .env

# 3. Port 8080 deja utilise
lsof -i :8080
```

### 7.2 Erreur de connexion a la base de donnees

```bash
# Verifier que PostgreSQL est lance et healthy
docker compose ps postgres

# Tester la connexion
docker exec -it nihongodev-postgres psql -U nihongodev -d nihongodev -c "SELECT 1;"

# Verifier les credentials dans .env
grep DATABASE .env
grep POSTGRES .env
```

### 7.3 Kafka ne demarre pas

```bash
# Verifier Zookeeper d'abord
docker compose logs zookeeper --tail 20

# Puis Kafka
docker compose logs kafka --tail 20

# Cause frequente : memoire insuffisante
# Solution : augmenter la RAM du VPS ou limiter Kafka
docker compose restart zookeeper kafka
```

### 7.4 Le frontend affiche une page blanche

```bash
# Verifier que les fichiers sont presents
ls -la /var/www/nihongodev/browser/

# Verifier la config Nginx
nginx -t

# Verifier les permissions
chown -R www-data:www-data /var/www/nihongodev/

# Tester l'acces direct
curl -I http://localhost/
```

### 7.5 Erreurs CORS

```bash
# Verifier CORS_ALLOWED_ORIGINS dans .env
grep CORS .env

# Doit correspondre exactement au domaine (avec https://)
# Exemple : https://votre-domaine.com (pas de / a la fin)

# Apres modification, redemarrer le backend
docker compose restart app
```

### 7.6 Erreurs 502 Bad Gateway

```bash
# Le backend n'est pas accessible par Nginx
# Verifier que le backend tourne
curl http://localhost:8080/api/health

# Si non, redemarrer
docker compose up -d app

# Verifier que Nginx pointe vers le bon port
grep proxy_pass /etc/nginx/sites-available/nihongodev
```

### 7.7 Espace disque plein

```bash
# Identifier ce qui prend de la place
du -sh /opt/nihongodev/backend/
docker system df

# Nettoyer les images Docker inutilisees
docker system prune -a --volumes

# Nettoyer les logs Docker
truncate -s 0 /var/lib/docker/containers/*/*-json.log

# Nettoyer les vieux backups
find /opt/nihongodev/backups -mtime +30 -delete
```

### 7.8 Memoire insuffisante

Si le VPS a 2GB de RAM et que les services sont lents :

```bash
# Reduire la memoire Java dans docker-compose.yml
# Modifier JAVA_OPTS dans le Dockerfile :
JAVA_OPTS="-Xms128m -Xmx384m"

# Ou ajouter dans docker-compose.yml sous le service app :
deploy:
  resources:
    limits:
      memory: 512M
```

---

## Annexe A : Architecture de deploiement

```
┌─────────────────────────────────────────────────────────────┐
│                         VPS                                   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    NGINX (:80/:443)                   │    │
│  │   - SSL termination                                  │    │
│  │   - Static files (/var/www/nihongodev/browser/)      │    │
│  │   - Reverse proxy /api/ → localhost:8080             │    │
│  └──────────────────────┬──────────────────────────────┘    │
│                          │                                    │
│  ┌───────────────────────▼──────────────────────────────┐   │
│  │              Docker Compose Network                    │   │
│  │                                                       │   │
│  │  ┌─────────────┐  ┌───────┐  ┌──────────────────┐   │   │
│  │  │ Spring Boot │  │ Redis │  │  Kafka + Zookeeper│   │   │
│  │  │   (:8080)   │  │(:6379)│  │  (:9092 / :2181) │   │   │
│  │  └──────┬──────┘  └───────┘  └──────────────────┘   │   │
│  │         │                                             │   │
│  │  ┌──────▼──────┐                                     │   │
│  │  │ PostgreSQL  │                                     │   │
│  │  │   (:5432)   │                                     │   │
│  │  └─────────────┘                                     │   │
│  └───────────────────────────────────────────────────────┘   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## Annexe B : Checklist de deploiement

### Avant le deploiement

- [ ] Domaine DNS configure (A record → IP du VPS)
- [ ] VPS accessible en SSH
- [ ] Docker et Docker Compose installes
- [ ] Node.js 20+ installe
- [ ] Nginx installe
- [ ] Firewall configure (ports 22, 80, 443 uniquement)
- [ ] Fichier `.env` cree avec toutes les valeurs remplies
- [ ] `JWT_SECRET` genere (64+ caracteres)
- [ ] Compte SMTP fonctionnel (Gmail app password ou autre)
- [ ] Cle API Anthropic (si LLM actif)

### Pendant le deploiement

- [ ] `docker compose up -d` → tous les conteneurs UP
- [ ] `curl http://localhost:8080/api/health` → status UP
- [ ] Frontend builde sans erreur (`npx ng build`)
- [ ] Fichiers copies dans `/var/www/nihongodev/browser/`
- [ ] `nginx -t` → syntax is ok
- [ ] Site accessible sur http://votre-domaine.com
- [ ] SSL installe (`certbot --nginx`)
- [ ] Site accessible sur https://votre-domaine.com
- [ ] Login fonctionne (creer un compte test)
- [ ] API Swagger desactive en prod (404 sur /swagger-ui.html)

### Apres le deploiement

- [ ] Backup cron configure
- [ ] Monitoring basique en place
- [ ] Tester la connexion mail (notification de bienvenue)
- [ ] Verifier les logs : pas d'erreurs repetitives
- [ ] Tester un redemarrage complet (`docker compose restart`)

---

## Annexe C : Estimation des couts VPS

| Fournisseur | Configuration | Prix/mois |
|-------------|---------------|-----------|
| Hetzner | CX21 (2 vCPU, 4GB RAM, 40GB) | ~5 EUR |
| OVH | VPS Starter (2 vCPU, 4GB RAM, 80GB) | ~7 EUR |
| DigitalOcean | Basic Droplet (2 vCPU, 4GB RAM, 80GB) | ~24 USD |
| Contabo | VPS S (4 vCPU, 8GB RAM, 200GB) | ~7 EUR |
| Vultr | Cloud Compute (2 vCPU, 4GB RAM, 80GB) | ~24 USD |

**Recommandation** : Hetzner CX21 ou Contabo VPS S pour le meilleur rapport qualite/prix avec suffisamment de RAM pour toute la stack (PostgreSQL + Redis + Kafka + Spring Boot).
