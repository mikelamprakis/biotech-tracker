#!/bin/bash
set -e

# Run on Oracle VM to deploy the backend
# Usage: ./deploy.sh

JAR="biotech-tracker.jar"

echo "==> Stopping existing instance..."
pkill -f "$JAR" || true
sleep 2

echo "==> Starting backend..."
export DB_URL="${DB_URL:-jdbc:postgresql://localhost:5432/bdit}"
export DB_USERNAME="${DB_USERNAME:-bdit}"
export DB_PASSWORD="${DB_PASSWORD:-bdit}"
export CORS_ORIGINS="${CORS_ORIGINS:-https://your-cloudflare-pages-url}"
export PORT="${PORT:-8080}"

nohup java -jar "$JAR" \
  --spring.datasource.url="$DB_URL" \
  --spring.datasource.username="$DB_USERNAME" \
  --spring.datasource.password="$DB_PASSWORD" \
  > app.log 2>&1 &

echo "==> Started. Logs: app.log"
