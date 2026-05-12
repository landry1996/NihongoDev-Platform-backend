#!/bin/bash
# =============================================================================
# NihongoDev Platform - Script de deploiement VPS
# Usage: ./deploy.sh [backend|frontend|all]
# =============================================================================

set -e

DEPLOY_DIR="/opt/nihongodev"
FRONTEND_DIR="/var/www/nihongodev"
BACKEND_REPO="https://github.com/landry1996/NihongoDev-Platform-backend.git"
FRONTEND_REPO="https://github.com/landry1996/NihongoDev-Platform-frontend.git"

echo "=== NihongoDev Platform - Deploiement ==="

# Verifier que .env existe
if [ ! -f "$DEPLOY_DIR/.env" ]; then
    echo "ERREUR: $DEPLOY_DIR/.env n'existe pas."
    echo "Copiez .env.example en .env et remplissez les valeurs."
    exit 1
fi

deploy_backend() {
    echo ""
    echo "--- Deploiement Backend ---"
    cd "$DEPLOY_DIR"

    # Pull latest code
    if [ -d "backend" ]; then
        cd backend && git pull origin main
    else
        git clone "$BACKEND_REPO" backend && cd backend
    fi

    # Build and restart
    docker compose down app 2>/dev/null || true
    docker compose up -d --build app
    echo "Backend demarre. En attente du health check..."

    # Wait for health
    for i in {1..30}; do
        if curl -sf http://localhost:8080/api/health > /dev/null 2>&1; then
            echo "Backend OK!"
            return 0
        fi
        sleep 2
    done
    echo "ATTENTION: Le backend ne repond pas apres 60s."
    docker compose logs app --tail 50
    return 1
}

deploy_frontend() {
    echo ""
    echo "--- Deploiement Frontend ---"
    cd "$DEPLOY_DIR"

    # Pull latest code
    if [ -d "frontend" ]; then
        cd frontend && git pull origin main
    else
        git clone "$FRONTEND_REPO" frontend && cd frontend
    fi

    # Build
    npm install --production=false
    npx ng build --configuration=production

    # Deploy to nginx
    rm -rf "$FRONTEND_DIR/browser"
    cp -r dist/nihongodev-frontend/browser "$FRONTEND_DIR/"

    # Reload nginx
    nginx -t && systemctl reload nginx
    echo "Frontend deploye!"
}

case "${1:-all}" in
    backend)
        deploy_backend
        ;;
    frontend)
        deploy_frontend
        ;;
    all)
        deploy_backend
        deploy_frontend
        ;;
    *)
        echo "Usage: $0 [backend|frontend|all]"
        exit 1
        ;;
esac

echo ""
echo "=== Deploiement termine ==="
