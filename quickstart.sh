#!/bin/bash

# Quick Start Script for Algo Trading System
# This script helps you get started quickly

set -e

echo "🚀 Algo Trading System - Quick Start"
echo "===================================="

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose."
    exit 1
fi

echo "✅ All prerequisites are met!"

# Setup environment
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  Optional: Edit .env file to add your API keys (NewsAPI, Finnhub)"
    echo "   The system will work with mock data if keys are not provided."
fi

# Check if PostgreSQL is already running on port 5432
echo ""
echo "🔍 Checking for existing PostgreSQL service..."

POSTGRES_RUNNING=false
DB_URL="jdbc:postgresql://host.docker.internal:5432/algotrading"
COMPOSE_PROFILES=""
COMPOSE_FILES="-f docker-compose.yml"

# Check if port 5432 is in use
if lsof -Pi :5432 -sTCP:LISTEN -t >/dev/null 2>&1 || sudo lsof -Pi :5432 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "✅ Found PostgreSQL running on port 5432"
    POSTGRES_RUNNING=true
    
    # Try to create database if it doesn't exist
    echo "📦 Ensuring 'algotrading' database exists..."
    
    # Check if it's a Docker container
    if docker ps --format '{{.Names}}' | grep -q postgres; then
        POSTGRES_CONTAINER=$(docker ps --format '{{.Names}}' | grep postgres | head -n 1)
        echo "   Using existing Docker PostgreSQL container: $POSTGRES_CONTAINER"
        
        # Create database if it doesn't exist
        docker exec -i $POSTGRES_CONTAINER psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'algotrading'" | grep -q 1 || \
        docker exec -i $POSTGRES_CONTAINER psql -U postgres -c "CREATE DATABASE algotrading;" 2>/dev/null || true
        
        echo "   ✅ Database 'algotrading' is ready"
    else
        echo "   ⚠️  PostgreSQL is running on host. Please ensure 'algotrading' database exists"
        echo "   Run: psql -U postgres -c 'CREATE DATABASE algotrading;'"
    fi
    
    echo ""
    echo "📝 Services will connect to existing PostgreSQL on localhost:5432"
    DB_URL="jdbc:postgresql://host.docker.internal:5432/algotrading"
else
    echo "⚠️  No PostgreSQL found on port 5432"
    echo "🐳 Will start PostgreSQL container on port 5433"
    COMPOSE_PROFILES="--profile with-db"
    DB_URL="jdbc:postgresql://postgres:5432/algotrading"
fi

# Export database connection for services
export DB_URL

# Export database connection for services
export DB_CONNECTION_HOST=$DB_HOST
export DB_CONNECTION_PORT=$DB_PORT

# Check if services are already running
if docker-compose ps | grep -q "Up"; then
    echo "✅ Services are already running!"
    echo ""
    echo "Options:"
    echo "  1. Keep running and view status"
    echo "  2. Restart all services"
    echo "  3. Rebuild and restart"
    echo ""
    read -p "Choose option (1-3): " choice
    
    case $choice in
        1)
            echo "✅ Keeping existing services running..."
            ;;
        2)
            echo "🔄 Restarting services..."
            docker-compose $COMPOSE_PROFILES restart
            ;;
        3)
            echo "🔨 Rebuilding and restarting services..."
            docker-compose $COMPOSE_PROFILES up --build -d
            ;;
        *)
            echo "✅ Keeping existing services running (default)..."
            ;;
    esac
else
    # Build and start services
    echo "🔨 Building and starting services with Docker Compose..."
    
    if [ "$POSTGRES_RUNNING" = false ]; then
        echo "   Starting with PostgreSQL container..."
        docker-compose $COMPOSE_PROFILES up --build -d
    else
        echo "   Starting services only (using existing PostgreSQL)..."
        docker-compose up --build -d
    fi
    
    echo ""
    echo "⏳ Waiting for services to start (30 seconds)..."
    sleep 30
fi

echo ""
echo "✅ Services are starting up!"
echo ""
echo "📊 Access Points:"
echo "   Dashboard Service:   http://localhost:8080/swagger-ui.html"
echo "   Market Data Service: http://localhost:8081/swagger-ui.html"
echo "   Strategy Service:    http://localhost:8082/swagger-ui.html"
echo "   Broker Service:      http://localhost:8083/swagger-ui.html"
echo ""

if [ "$POSTGRES_RUNNING" = false ]; then
    echo "💾 Database:"
    echo "   PostgreSQL:          localhost:5433 (container)"
    echo "   Database:            algotrading"
    echo "   Username:            postgres"
    echo "   Password:            postgres"
else
    echo "💾 Database:"
    echo "   Using existing PostgreSQL on localhost:5432"
    echo "   Database:            algotrading"
fi

echo ""
echo "📝 To view logs:"
echo "   docker-compose logs -f"
echo ""
echo "🛑 To stop services:"
echo "   docker-compose down"
echo ""
echo "🎉 Happy Trading!"

