#!/bin/bash

# Quick Start Script for Algo Trading System
# This script helps you get started quickly

set -e

echo "🚀 Algo Trading System - Quick Start"
echo "===================================="

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

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
    echo "⚠️  Please edit .env file and add your API keys before continuing."
    echo "   Press Enter to open .env file in nano editor, or Ctrl+C to exit and edit manually."
    read -r
    nano .env
fi

# Build and start services
echo "🔨 Building and starting services with Docker Compose..."
docker-compose up --build -d

echo ""
echo "⏳ Waiting for services to start (30 seconds)..."
sleep 30

echo ""
echo "✅ Services are starting up!"
echo ""
echo "📊 Access Points:"
echo "   Dashboard Service:   http://localhost:8080/swagger-ui.html"
echo "   Market Data Service: http://localhost:8081/swagger-ui.html"
echo "   Strategy Service:    http://localhost:8082/swagger-ui.html"
echo "   Broker Service:      http://localhost:8083/swagger-ui.html"
echo ""
echo "📝 To view logs:"
echo "   docker-compose logs -f"
echo ""
echo "🛑 To stop services:"
echo "   docker-compose down"
echo ""
echo "🎉 Happy Trading!"
