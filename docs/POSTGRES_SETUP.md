# PostgreSQL Setup Guide for Local Development

This guide walks you through setting up PostgreSQL for the Algo Trading System using Docker.

## 🐳 Quick Start with Docker

### Option 1: Using Docker Run (Standalone PostgreSQL)

```bash
# Pull and run PostgreSQL container
docker run -d \
  --name algo-trading-db \
  -e POSTGRES_DB=algotrading \
  -e POSTGRES_USER=algotrading \
  -e POSTGRES_PASSWORD=algotrading \
  -p 5432:5432 \
  -v algo-trading-data:/var/lib/postgresql/data \
  postgres:15-alpine

# Check if container is running
docker ps

# View logs
docker logs algo-trading-db

# Check container health
docker exec algo-trading-db pg_isready -U algotrading
```

**Access Details:**
- **Host**: localhost
- **Port**: 5432
- **Database**: algotrading
- **Username**: algotrading
- **Password**: algotrading

---

### Option 2: Using Docker Compose (Recommended)

The project already includes a `docker-compose.yml` with PostgreSQL configured.

```bash
# Start only PostgreSQL
docker-compose up -d postgres

# Or start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs postgres

# Stop services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v
```

---

## 🔧 Connecting to PostgreSQL

### Using psql (PostgreSQL CLI)

#### Method 1: From Host Machine
```bash
# Install psql client if not installed
# Ubuntu/Debian:
sudo apt-get install postgresql-client

# macOS:
brew install postgresql

# Connect to database
psql -h localhost -p 5432 -U algotrading -d algotrading
# When prompted, enter password: algotrading
```

#### Method 2: Using Docker Exec
```bash
# Access psql inside the container
docker exec -it algo-trading-db psql -U algotrading -d algotrading

# Or with docker-compose
docker-compose exec postgres psql -U algotrading -d algotrading
```

---

### Using GUI Tools

#### 1. **pgAdmin** (Web-based)
```bash
# Run pgAdmin container
docker run -d \
  --name pgadmin \
  -e PGADMIN_DEFAULT_EMAIL=admin@admin.com \
  -e PGADMIN_DEFAULT_PASSWORD=admin \
  -p 5050:80 \
  --network algo-trading-network \
  dpage/pgadmin4

# Access at: http://localhost:5050
# Login: admin@admin.com / admin

# Add server in pgAdmin:
# - Name: algo-trading
# - Host: postgres (or localhost if not in same network)
# - Port: 5432
# - Database: algotrading
# - Username: algotrading
# - Password: algotrading
```

#### 2. **DBeaver** (Desktop Application)
1. Download from: https://dbeaver.io/
2. Create new connection → PostgreSQL
3. Enter connection details:
   - Host: localhost
   - Port: 5432
   - Database: algotrading
   - Username: algotrading
   - Password: algotrading

#### 3. **IntelliJ IDEA / VS Code Extensions**
- IntelliJ: Database Tools plugin (built-in)
- VS Code: PostgreSQL extension

---

## 📊 Verify Database Setup

Once connected, verify the setup:

```sql
-- Check connection
SELECT version();

-- List all databases
\l

-- Connect to algotrading database
\c algotrading

-- List all tables (will be empty initially)
\dt

-- After running the application, verify tables
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';

-- Check candles table
SELECT COUNT(*) FROM candles;

-- Check strategy configs
SELECT * FROM strategy_configs;
```

---

## 🗄️ Database Management Commands

### Basic Operations

```bash
# Start PostgreSQL
docker start algo-trading-db

# Stop PostgreSQL
docker stop algo-trading-db

# Restart PostgreSQL
docker restart algo-trading-db

# View real-time logs
docker logs -f algo-trading-db

# Remove container (keeps data if using volume)
docker rm algo-trading-db

# Remove container and data
docker rm -v algo-trading-db
```

### Backup and Restore

#### Create Backup
```bash
# Backup entire database
docker exec algo-trading-db pg_dump -U algotrading algotrading > backup.sql

# Backup with timestamp
docker exec algo-trading-db pg_dump -U algotrading algotrading > backup_$(date +%Y%m%d_%H%M%S).sql

# Backup specific table
docker exec algo-trading-db pg_dump -U algotrading -t trades algotrading > trades_backup.sql
```

#### Restore from Backup
```bash
# Restore database
docker exec -i algo-trading-db psql -U algotrading algotrading < backup.sql

# Or from inside container
cat backup.sql | docker exec -i algo-trading-db psql -U algotrading algotrading
```

---

## 🔍 Useful SQL Queries

### Monitor Database Size
```sql
-- Database size
SELECT pg_size_pretty(pg_database_size('algotrading'));

-- Table sizes
SELECT 
    table_name,
    pg_size_pretty(pg_total_relation_size(quote_ident(table_name)))
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY pg_total_relation_size(quote_ident(table_name)) DESC;
```

### Check Active Connections
```sql
SELECT 
    pid,
    usename,
    application_name,
    client_addr,
    state,
    query
FROM pg_stat_activity
WHERE datname = 'algotrading';
```

### Performance Monitoring
```sql
-- Most frequent queries
SELECT 
    query,
    calls,
    total_time,
    mean_time
FROM pg_stat_statements
ORDER BY calls DESC
LIMIT 10;

-- Table statistics
SELECT 
    schemaname,
    tablename,
    n_tup_ins as inserts,
    n_tup_upd as updates,
    n_tup_del as deletes
FROM pg_stat_user_tables;
```

---

## 🧹 Clean Up Database

### Drop All Tables
```sql
-- Drop all tables (careful!)
DROP TABLE IF EXISTS positions CASCADE;
DROP TABLE IF EXISTS trades CASCADE;
DROP TABLE IF EXISTS portfolios CASCADE;
DROP TABLE IF EXISTS strategy_configs CASCADE;
DROP TABLE IF EXISTS candles CASCADE;
```

### Reset Sequences
```sql
-- Reset auto-increment counters
ALTER SEQUENCE candles_id_seq RESTART WITH 1;
ALTER SEQUENCE trades_id_seq RESTART WITH 1;
ALTER SEQUENCE strategy_configs_id_seq RESTART WITH 1;
ALTER SEQUENCE portfolios_id_seq RESTART WITH 1;
ALTER SEQUENCE positions_id_seq RESTART WITH 1;
```

### Clean Old Data
```sql
-- Delete old candles (older than 30 days)
DELETE FROM candles 
WHERE timestamp < NOW() - INTERVAL '30 days';

-- Delete test strategies
DELETE FROM strategy_configs 
WHERE name LIKE '%TEST%';

-- Vacuum to reclaim space
VACUUM FULL;
ANALYZE;
```

---

## 🔧 Troubleshooting

### Port Already in Use
```bash
# Check what's using port 5432
sudo lsof -i :5432

# Or on Linux
sudo netstat -tulpn | grep 5432

# Stop local PostgreSQL if running
sudo systemctl stop postgresql

# Or change port in docker-compose.yml
ports:
  - "5433:5432"  # Host:Container
```

### Connection Refused
```bash
# Check if container is running
docker ps -a | grep postgres

# Check logs for errors
docker logs algo-trading-db

# Verify network
docker network ls
docker network inspect algo-trading-network
```

### Authentication Failed
```bash
# Check environment variables
docker exec algo-trading-db env | grep POSTGRES

# Reset password
docker exec -it algo-trading-db psql -U algotrading -c "ALTER USER algotrading WITH PASSWORD 'newpassword';"
```

### Out of Memory
```bash
# Increase Docker memory limit
# Docker Desktop → Settings → Resources → Memory

# Or add to docker-compose.yml
services:
  postgres:
    deploy:
      resources:
        limits:
          memory: 2G
```

---

## 🔒 Security Best Practices

### For Production Use

1. **Change Default Passwords**
```bash
# In .env file
POSTGRES_PASSWORD=your_strong_password_here
```

2. **Restrict Network Access**
```yaml
# docker-compose.yml
services:
  postgres:
    # Don't expose port externally
    # Remove the ports section
    # Only internal services can access
```

3. **Use SSL Connections**
```yaml
services:
  postgres:
    environment:
      POSTGRES_SSL_MODE: require
```

4. **Regular Backups**
```bash
# Add to crontab
0 2 * * * docker exec algo-trading-db pg_dump -U algotrading algotrading > /backups/db_$(date +\%Y\%m\%d).sql
```

---

## 📚 PostgreSQL Configuration

### Customize PostgreSQL Settings

Create `postgresql.conf`:
```conf
# postgresql.conf
max_connections = 100
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
random_page_cost = 1.1
```

Mount in docker-compose:
```yaml
services:
  postgres:
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./postgresql.conf:/etc/postgresql/postgresql.conf
    command: postgres -c config_file=/etc/postgresql/postgresql.conf
```

---

## 🎓 Learning Resources

- **PostgreSQL Docs**: https://www.postgresql.org/docs/
- **Docker Hub**: https://hub.docker.com/_/postgres
- **psql Tutorial**: https://www.postgresql.org/docs/current/app-psql.html
- **pgAdmin**: https://www.pgadmin.org/

---

## 🚀 Quick Reference Card

```bash
# Start database
docker-compose up -d postgres

# Connect to database
docker exec -it algo-trading-db psql -U algotrading -d algotrading

# Common psql commands
\l              # List databases
\dt             # List tables
\d table_name   # Describe table
\q              # Quit

# Backup
docker exec algo-trading-db pg_dump -U algotrading algotrading > backup.sql

# Restore
cat backup.sql | docker exec -i algo-trading-db psql -U algotrading algotrading

# View logs
docker-compose logs -f postgres

# Stop
docker-compose down
```

---

## ✅ Next Steps

After PostgreSQL is running:

1. ✅ Start your Spring Boot services
2. ✅ Tables will be auto-created by Hibernate
3. ✅ Start fetching market data
4. ✅ Create trading strategies
5. ✅ Run backtests and paper trades!

For application configuration, see the `application.yml` files in each service directory.
