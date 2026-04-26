# 🚀 Algorithmic Trading System

A comprehensive Spring Boot-based algorithmic trading platform featuring live market data integration, technical analysis strategies, backtesting capabilities, and virtual paper trading.

##  Table of Contents
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Debugging in Docker](#-debugging-in-docker)
- [API Documentation](#-api-documentation)
- [Usage Examples](#-usage-examples)
- [Configuration](#-configuration)
- [Contributing](#-contributing)

## ✨ Features

### Market Data Management
- Real-time market data fetching from Finnhub/Alpha Vantage APIs
- Historical OHLC data storage and retrieval
- Scheduled data updates
- Support for multiple symbols and timeframes

### Trading Strategies
- **Technical Indicators** (powered by TA4J):
  - Simple Moving Average (SMA)
  - Exponential Moving Average (EMA)
  - Relative Strength Index (RSI)
  - MACD (Moving Average Convergence Divergence)
  - Bollinger Bands
  - Combined multi-indicator strategies
- Strategy configuration and management
- Real-time signal generation
- Strategy activation/deactivation

### Backtesting Engine
- Historical data backtesting
- Performance metrics calculation:
  - Total return & percentage
  - Win rate
  - Maximum drawdown
  - Sharpe ratio
  - Average win/loss
  - Profit factor
- Date range selection
- Comprehensive result analysis

### Virtual Broker (Paper Trading)
- Simulated trade execution
- Portfolio management
- Position tracking (open/closed)
- P&L calculation (realized/unrealized)
- Commission and slippage simulation
- Stop-loss and take-profit support
- Order history and trade logs

### Dashboard & Reporting
- System overview
- Portfolio summaries
- Performance metrics
- Recent trades
- Open positions
- Strategy performance reports

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Dashboard Service                      │
│              (Port 8080 - API Gateway)                  │
└──────────┬──────────────┬──────────────┬───────────────┘
           │              │              │
┌──────────▼─────────┐ ┌──▼──────────────▼───┐ ┌─────────▼──────────┐
│  Market Data       │ │  Strategy Service   │ │ Virtual Broker     │
│  Service           │ │  (Port 8082)        │ │ Service            │
│  (Port 8081)       │ │                     │ │ (Port 8083)        │
│                    │ │ - TA4J Strategies   │ │                    │
│ - External APIs    │ │ - Backtesting       │ │ - Paper Trading    │
│ - Data Storage     │ │ - Signal Generation │ │ - Portfolio Mgmt   │
│ - Scheduling       │ │                     │ │ - P&L Calculation  │
└──────────┬─────────┘ └──┬──────────────────┘ └─────────┬──────────┘
           │              │                               │
           └──────────────┴───────────┬───────────────────┘
                                      │
                          ┌───────────▼────────────┐
                          │   PostgreSQL Database  │
                          │      (Port 5432)       │
                          └────────────────────────┘
```

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.5.7
- **Framework**: Spring Web, Spring Data JPA
- **Database**: PostgreSQL 15
- **Technical Analysis**: TA4J 0.15
- **API Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Utilities**: Lombok, Jackson

## 📦 Project Structure

```
algo-trading/
├── common-library/              # Shared models, DTOs, utilities
│   └── src/main/java/com/mayank/algotrading/common/
│       ├── model/              # Entity classes
│       ├── dto/                # Data Transfer Objects
│       ├── enums/              # Enumerations
│       └── util/               # Utility classes
│
├── market-data-service/         # Market data fetching & storage
│   └── src/main/java/com/mayank/algotrading/marketdata/
│       ├── client/             # External API clients
│       ├── controller/         # REST controllers
│       ├── repository/         # JPA repositories
│       ├── scheduler/          # Scheduled jobs
│       └── service/            # Business logic
│
├── strategy-service/            # Trading strategies & backtesting
│   └── src/main/java/com/mayank/algotrading/strategy/
│       ├── controller/         # REST controllers
│       ├── repository/         # JPA repositories
│       ├── service/            # Strategy & backtest services
│       └── ta4j/               # TA4J strategy implementations
│
├── virtual-broker-service/      # Virtual trading execution
│   └── src/main/java/com/mayank/algotrading/broker/
│       ├── controller/         # REST controllers
│       ├── repository/         # JPA repositories
│       └── service/            # Trading & portfolio services
│
├── dashboard-service/           # Aggregation & reporting
│   └── src/main/java/com/mayank/algotrading/dashboard/
│       ├── controller/         # REST controllers
│       └── repository/         # JPA repositories
│
└── docker-compose.yml          # Service orchestration
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for containerized deployment)
- PostgreSQL 15 (if running locally)
- API Keys from [Finnhub](https://finnhub.io/) or [Alpha Vantage](https://www.alphavantage.co/)

### Quick Start with Docker

**🎯 Flexible PostgreSQL Setup**

The system automatically detects if PostgreSQL is already running on your machine:
- **If PostgreSQL is running on port 5432**: Uses the existing instance
- **If no PostgreSQL found**: Starts a PostgreSQL container on port 5433

1. **Clone the repository**
   ```bash
   git clone https://github.com/mayank-yadav26/algo-trading.git
   cd algo-trading
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env
   # Edit .env and add your API keys
   nano .env
   ```

3. **Run the quick start script**
   ```bash
   chmod +x quickstart.sh
   ./quickstart.sh
   ```
   
   The script will:
   - ✅ Detect existing PostgreSQL on port 5432
   - ✅ Create `algotrading` database if needed
   - ✅ Start services with appropriate connection settings
   - ✅ Or start PostgreSQL container if none exists
   
   **Alternative: Manual database setup**
   ```bash
   chmod +x setup-database.sh
   ./setup-database.sh
   ```

4. **Or build and run manually with Docker Compose**
   
   **If PostgreSQL already running on port 5432:**
   ```bash
   docker-compose up --build -d
   ```
   
   **If no PostgreSQL (start bundled container):**
   ```bash
   docker-compose --profile with-db up --build -d
   ```
   
   > 🐛 **Debug Mode Enabled**: All services run with JDWP remote debugging enabled. See [Debugging in Docker](#-debugging-in-docker) section.

5. **Access the services**
   - Dashboard: http://localhost:8080/swagger-ui.html
   - Market Data: http://localhost:8081/swagger-ui.html
   - Strategy: http://localhost:8082/swagger-ui.html
   - Broker: http://localhost:8083/swagger-ui.html

### Local Development Setup

1. **Start PostgreSQL**
   ```bash
   docker run -d \
     --name algo-trading-db \
     -e POSTGRES_DB=algotrading \
     -e POSTGRES_USER=algotrading \
     -e POSTGRES_PASSWORD=algotrading \
     -p 5432:5432 \
     postgres:15-alpine
   ```

2. **Build the project**
   ```bash
   ./mvnw clean install
   ```

3. **Run services individually**
   ```bash
   # Terminal 1 - Market Data Service
   cd market-data-service
   ../mvnw spring-boot:run
   
   # Terminal 2 - Strategy Service
   cd strategy-service
   ../mvnw spring-boot:run
   
   # Terminal 3 - Virtual Broker Service
   cd virtual-broker-service
   ../mvnw spring-boot:run
   
   # Terminal 4 - Dashboard Service
   cd dashboard-service
   ../mvnw spring-boot:run
   ```

## � Debugging in Docker

All services are configured with **Java Debug Wire Protocol (JDWP)** for remote debugging. VS Code launch configurations are included.

### Debug Ports
- **Market Data Service**: `localhost:5005`
- **Strategy Service**: `localhost:5006`
- **Virtual Broker Service**: `localhost:5007`
- **Dashboard Service**: `localhost:5008`

### Quick Debug Setup

1. **Ensure services are running**
   ```bash
   docker-compose up -d
   ```

2. **Open VS Code Debug Panel** (`Ctrl+Shift+D`)
   - Pre-configured launch configurations are in `.vscode/launch.json`
   - Select the service you want to debug from dropdown

3. **Set breakpoints** in any Java file
   - Click left margin next to line number
   - Red dot indicates active breakpoint

4. **Attach debugger** (Press `F5`)
   - VS Code connects to running container
   - Wait for "Debugger attached" message

5. **Trigger your code** with an API call
   ```bash
   curl http://localhost:8082/api/strategies/1/signal
   ```

### Debug Configurations (`.vscode/launch.json`)

The project includes 4 pre-configured debug profiles:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug Market Data Service (Docker)",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005,
      "projectName": "market-data-service"
    },
    {
      "type": "java",
      "name": "Debug Strategy Service (Docker)",
      "request": "attach",
      "hostName": "localhost",
      "port": 5006,
      "projectName": "strategy-service"
    },
    {
      "type": "java",
      "name": "Debug Virtual Broker Service (Docker)",
      "request": "attach",
      "hostName": "localhost",
      "port": 5007,
      "projectName": "virtual-broker-service"
    },
    {
      "type": "java",
      "name": "Debug Dashboard Service (Docker)",
      "request": "attach",
      "hostName": "localhost",
      "port": 5008,
      "projectName": "dashboard-service"
    }
  ]
}
```

### Debug Controls
- **F5** - Continue execution
- **F10** - Step over (next line)
- **F11** - Step into method
- **Shift+F11** - Step out of method
- **Shift+F5** - Stop debugging

### Rebuild After Code Changes
```bash
# Rebuild all services
docker-compose up -d --build

# Rebuild specific service
docker-compose up -d --build strategy-service
```

📖 **Full debugging guide**: See [`DEBUG_QUICKSTART.md`](DEBUG_QUICKSTART.md) and [`docs/DEBUGGING_GUIDE.md`](docs/DEBUGGING_GUIDE.md)

## �📚 API Documentation

Each service exposes its API documentation via Swagger UI:

### Market Data Service (Port 8081)
- `GET /api/market-data/quote/{symbol}` - Fetch real-time quote
- `GET /api/market-data/historical/{symbol}` - Fetch historical data
- `GET /api/market-data/candles/{symbol}` - Get stored candles
- `GET /api/market-data/candles/{symbol}/recent` - Get recent candles
- `GET /api/market-data/candles/{symbol}/latest` - Get latest candle

### Strategy Service (Port 8082)
- `POST /api/strategies` - Create new strategy
- `GET /api/strategies` - List all strategies
- `GET /api/strategies/{id}` - Get strategy details
- `PUT /api/strategies/{id}` - Update strategy
- `DELETE /api/strategies/{id}` - Delete strategy
- `GET /api/strategies/active` - Get active strategies
- `GET /api/strategies/{id}/signal` - Generate trading signal
- `POST /api/strategies/{id}/backtest` - Run backtest

### Virtual Broker Service (Port 8083)
- `POST /api/trading/execute` - Execute trade
- `GET /api/trading/trades/strategy/{strategyId}` - Get trades
- `GET /api/trading/positions/strategy/{strategyId}` - Get positions
- `GET /api/trading/trades/{tradeId}` - Get trade details
- `POST /api/portfolio/create` - Create portfolio
- `GET /api/portfolio/strategy/{strategyId}` - Get portfolio
- `GET /api/portfolio/strategy/{strategyId}/summary` - Get summary

### Dashboard Service (Port 8080)
- `GET /api/dashboard/overview` - System overview
- `GET /api/dashboard/portfolios` - All portfolio summaries
- `GET /api/dashboard/strategy/{strategyId}/performance` - Strategy performance
- `GET /api/dashboard/trades/recent` - Recent trades
- `GET /api/dashboard/positions/open` - All open positions

## 💡 Usage Examples

### 1. Fetch Market Data
```bash
curl -X GET "http://localhost:8081/api/market-data/quote/AAPL"
```

### 2. Create a Trading Strategy
```bash
curl -X POST "http://localhost:8082/api/strategies" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "SMA Crossover AAPL",
    "type": "SMA",
    "symbol": "AAPL",
    "timeframe": "5m",
    "isActive": true,
    "initialCapital": 10000,
    "riskPerTrade": 2.0,
    "parameters": "{\"shortPeriod\":10,\"longPeriod\":30}",
    "description": "Simple moving average crossover strategy"
  }'
```

### 3. Run Backtest
```bash
curl -X POST "http://localhost:8082/api/strategies/1/backtest?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"
```

### 4. Generate Trading Signal
```bash
curl -X GET "http://localhost:8082/api/strategies/1/signal"
```

### 5. Execute Trade
```bash
curl -X POST "http://localhost:8083/api/trading/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "strategyId": 1,
    "symbol": "AAPL",
    "signal": "BUY",
    "price": 150.50,
    "suggestedQuantity": 10,
    "stopLoss": 145.00,
    "takeProfit": 160.00
  }'
```

### 6. View Portfolio Summary
```bash
curl -X GET "http://localhost:8083/api/portfolio/strategy/1/summary"
```

## ⚙️ Configuration

### Database Configuration

The system supports flexible PostgreSQL connection modes:

#### Option 1: Use Existing PostgreSQL (Recommended)
If you already have PostgreSQL running on port 5432:
```bash
# System will auto-detect and use it
./quickstart.sh
```

Ensure the `algotrading` database exists:
```sql
-- For Docker PostgreSQL
docker exec -it <your-postgres-container> psql -U postgres -c "CREATE DATABASE algotrading;"

-- For host PostgreSQL
psql -U postgres -c "CREATE DATABASE algotrading;"
```

#### Option 2: Start Bundled PostgreSQL Container
If no PostgreSQL is detected, the system starts a container on port 5433:
```bash
# Manual start with PostgreSQL profile
docker-compose --profile with-db up -d

# Or use the database setup script
./setup-database.sh
```

#### Connection Details
- **Host**: localhost (for running services) or `host.docker.internal` (for Docker services)
- **Port**: 5432 (existing) or 5433 (bundled container)
- **Database**: algotrading
- **Username**: postgres
- **Password**: postgres

Edit `application.yml` in each service if needed:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/algotrading
    username: postgres
    password: postgres
```

### Market Data Service
Edit `market-data-service/src/main/resources/application.yml`:
```yaml
market-data:
  api:
    provider: finnhub  # or alphavantage
    finnhub:
      api-key: ${FINNHUB_API_KEY}
  schedule:
    enabled: true
    cron: "0 */5 * * * *"  # Every 5 minutes
    symbols: AAPL,GOOGL,MSFT,TSLA,AMZN
```

### Virtual Broker Service
Edit `virtual-broker-service/src/main/resources/application.yml`:
```yaml
broker:
  commission:
    rate: 0.001  # 0.1% commission
  slippage:
    percentage: 0.0005  # 0.05% slippage
```

## 🔒 Security Considerations
⚠️ **This is a development/learning project**
- Not suitable for production trading without additional security measures
- API keys should be stored securely (use environment variables)
- Add authentication/authorization for production use
- Implement rate limiting for external APIs
- Use HTTPS in production

## 🤝 Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License
This project is licensed under the MIT License.

## 🙏 Acknowledgments
- [TA4J](https://github.com/ta4j/ta4j) - Technical Analysis Library
- [Finnhub](https://finnhub.io/) - Market Data API
- [Alpha Vantage](https://www.alphavantage.co/) - Market Data API
- Spring Boot Team

## 📞 Contact
- GitHub: [@mayank-yadav26](https://github.com/mayank-yadav26)
- Repository: [algo-trading](https://github.com/mayank-yadav26/algo-trading)

---

**Disclaimer**: This software is for educational purposes only. Use at your own risk. The authors are not responsible for any financial losses incurred through the use of this software.
