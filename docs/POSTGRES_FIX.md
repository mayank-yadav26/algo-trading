# PostgreSQL Port Conflict Resolution

## Problem
The system was trying to start a PostgreSQL container on port 5432, which was already in use by an existing PostgreSQL instance, causing the error:
```
ERROR: Bind for 0.0.0.0:5432 failed: port is already allocated
```

## Solution
Implemented a **flexible PostgreSQL configuration** that intelligently detects and adapts to the environment.

## Changes Made

### 1. Docker Compose Configuration (`docker-compose.yml`)
- ✅ Added PostgreSQL service with profile `with-db` (starts only when explicitly requested)
- ✅ Changed default port mapping to `5433:5432` to avoid conflicts
- ✅ Services use `host.docker.internal:5432` to connect to existing PostgreSQL by default
- ✅ Added `extra_hosts` configuration for Docker host access

### 2. Docker Compose Override (`docker-compose.override.yml`) - NEW FILE
- ✅ Automatically used when starting with `--profile with-db`
- ✅ Reconfigures services to use containerized PostgreSQL (`postgres:5432`)
- ✅ Adds health check dependencies

### 3. Enhanced Quick Start Script (`quickstart.sh`)
- ✅ **Auto-detects** existing PostgreSQL on port 5432
- ✅ Creates `algotrading` database if it doesn't exist
- ✅ Intelligently chooses connection mode:
  - Uses existing PostgreSQL if available
  - Starts bundled PostgreSQL container if none found
- ✅ Applies appropriate docker-compose profiles and files

### 4. Database Setup Script (`setup-database.sh`) - NEW FILE
- ✅ Interactive database management tool
- ✅ Checks PostgreSQL status
- ✅ Helps create database
- ✅ Provides installation instructions

### 5. Application Configuration (all `application.yml` files)
- ✅ Updated database credentials to use `postgres/postgres`
- ✅ Connection URL: `jdbc:postgresql://localhost:5432/algotrading`
- ✅ Works with both existing and containerized PostgreSQL

### 6. Documentation (`README.md`)
- ✅ Added **Flexible PostgreSQL Setup** section
- ✅ Documented both connection modes
- ✅ Added Database Configuration section
- ✅ Included setup instructions

## Usage

### Scenario 1: PostgreSQL Already Running (Your Case)
```bash
# Script auto-detects and uses existing PostgreSQL
./quickstart.sh
```

The script will:
1. Detect PostgreSQL on port 5432
2. Create `algotrading` database if needed
3. Start services connecting to existing PostgreSQL
4. ✅ **No port conflicts!**

### Scenario 2: No PostgreSQL Available
```bash
# Script starts PostgreSQL container on port 5433
./quickstart.sh
```

The script will:
1. Detect no PostgreSQL on port 5432
2. Start PostgreSQL container (port 5433 externally, 5432 internally)
3. Create `algotrading` database
4. Start services connecting to containerized PostgreSQL

### Manual Control
```bash
# Use existing PostgreSQL
docker-compose up -d

# Start with bundled PostgreSQL
docker-compose --profile with-db up -d

# Database management
./setup-database.sh
```

## Testing

### Verify Configuration
```bash
# Check syntax
bash -n quickstart.sh
bash -n setup-database.sh

# Validate Docker Compose
docker-compose config --quiet
```

### Test Database Connection
```bash
# For existing PostgreSQL container
docker exec -it postgres-database psql -U postgres -d algotrading -c "\dt"

# For bundled PostgreSQL
docker exec -it algo-trading-db psql -U postgres -d algotrading -c "\dt"
```

## Benefits

1. **✅ No Port Conflicts**: Automatically adapts to existing PostgreSQL
2. **✅ Zero Configuration**: Auto-detection and setup
3. **✅ Flexible Deployment**: Works in any environment
4. **✅ Production Ready**: Use existing production database easily
5. **✅ Development Friendly**: Quick start with bundled container
6. **✅ CI/CD Compatible**: Profile-based container control

## Files Modified
- `docker-compose.yml`
- `quickstart.sh`
- `setup-database.sh` (NEW)
- `docker-compose.override.yml` (NEW)
- `README.md`
- `market-data-service/src/main/resources/application.yml`
- `strategy-service/src/main/resources/application.yml`
- `virtual-broker-service/src/main/resources/application.yml`
- `dashboard-service/src/main/resources/application.yml`

## Next Steps

1. **Run the updated quickstart script:**
   ```bash
   ./quickstart.sh
   ```

2. **Verify services are running:**
   ```bash
   docker-compose ps
   ```

3. **Check database connection:**
   ```bash
   docker exec -it postgres-database psql -U postgres -d algotrading -c "SELECT version();"
   ```

4. **Access the services:**
   - Dashboard: http://localhost:8080/swagger-ui.html
   - Market Data: http://localhost:8081/swagger-ui.html
   - Strategy: http://localhost:8082/swagger-ui.html
   - Broker: http://localhost:8083/swagger-ui.html

## Troubleshooting

**Issue**: Services can't connect to database
```bash
# Check if database exists
docker exec -it postgres-database psql -U postgres -l | grep algotrading

# Create database manually
docker exec -it postgres-database psql -U postgres -c "CREATE DATABASE algotrading;"
```

**Issue**: Need to switch connection mode
```bash
# Stop all services
docker-compose down

# Restart with appropriate mode
./quickstart.sh
```
