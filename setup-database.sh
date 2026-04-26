#!/bin/bash

# PostgreSQL Database Setup Script
# This script helps manage the PostgreSQL database for the algo-trading system

set -e

echo "🗄️  Algo Trading - PostgreSQL Database Setup"
echo "============================================="
echo ""

# Function to check if PostgreSQL is running on a port
check_postgres() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 || sudo lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Check current PostgreSQL status
echo "🔍 Checking PostgreSQL status..."
echo ""

if check_postgres 5432; then
    echo "✅ PostgreSQL is running on port 5432"
    
    # Check if it's a Docker container
    if docker ps --format '{{.Names}}' | grep -q postgres; then
        POSTGRES_CONTAINER=$(docker ps --format '{{.Names}}' | grep postgres | head -n 1)
        echo "   Container: $POSTGRES_CONTAINER"
        
        # Check if algotrading database exists
        if docker exec -i $POSTGRES_CONTAINER psql -U postgres -tc "SELECT 1 FROM pg_database WHERE datname = 'algotrading'" | grep -q 1; then
            echo "   ✅ Database 'algotrading' exists"
        else
            echo "   ⚠️  Database 'algotrading' does not exist"
            read -p "   Create database now? (y/n): " create_db
            if [ "$create_db" = "y" ]; then
                docker exec -i $POSTGRES_CONTAINER psql -U postgres -c "CREATE DATABASE algotrading;"
                echo "   ✅ Database 'algotrading' created"
            fi
        fi
    else
        echo "   Type: Host PostgreSQL"
        echo ""
        echo "   To create the database, run:"
        echo "   psql -U postgres -c 'CREATE DATABASE algotrading;'"
    fi
else
    echo "⚠️  No PostgreSQL found on port 5432"
    echo ""
    echo "Options:"
    echo "  1. Start PostgreSQL in Docker (recommended)"
    echo "  2. Install PostgreSQL on host"
    echo "  3. Exit"
    echo ""
    read -p "Choose option (1-3): " option
    
    case $option in
        1)
            echo ""
            echo "🐳 Starting PostgreSQL container..."
            docker-compose --profile with-db up -d postgres
            
            echo "⏳ Waiting for PostgreSQL to start..."
            sleep 10
            
            # Create database
            docker exec -i algo-trading-db psql -U postgres -c "CREATE DATABASE algotrading;" || true
            
            echo ""
            echo "✅ PostgreSQL container is running!"
            echo "   Port: 5433 (mapped from container's 5432)"
            echo "   Database: algotrading"
            echo "   Username: postgres"
            echo "   Password: postgres"
            echo ""
            echo "To connect:"
            echo "  docker exec -it algo-trading-db psql -U postgres -d algotrading"
            ;;
        2)
            echo ""
            echo "📝 To install PostgreSQL on Ubuntu/Debian:"
            echo "  sudo apt update"
            echo "  sudo apt install postgresql postgresql-contrib"
            echo "  sudo systemctl start postgresql"
            echo "  sudo -u postgres psql -c 'CREATE DATABASE algotrading;'"
            echo ""
            echo "📝 To install PostgreSQL on macOS:"
            echo "  brew install postgresql"
            echo "  brew services start postgresql"
            echo "  psql postgres -c 'CREATE DATABASE algotrading;'"
            ;;
        *)
            echo "Exiting..."
            exit 0
            ;;
    esac
fi

echo ""
echo "🎉 Database setup complete!"
