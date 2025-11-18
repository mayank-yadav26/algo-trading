I want you to generate a complete Spring Boot based Algorithmic Trading project for me.

### 🎯 Project Goal
Build an Algo Trading system that:
- Fetches live market data (free API like Finnhub or Alpha Vantage)
- Stores OHLC data in DB
- Runs strategies (SMA, EMA, RSI, MACD) using TA4J
- Performs backtesting on historical data
- Performs live paper trading (virtual trading) with virtual capital
- Logs trades, PnL, portfolio value
- Generates buy/sell signals automatically
- Executes trades in a virtual broker module
- Exposes REST APIs for:
  - starting/stopping strategy
  - fetching indicators
  - portfolio details
  - PnL reports
- Has scheduled tasks for periodic data fetching
- Is built using clean architecture + layered design

### 🧱 Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- MySQL or PostgreSQL
- Lombok
- Spring Scheduler
- TA4J (technical indicators)
- Docker + Docker Compose
- Optional: Kafka (event-driven design)

### 📦 Required Project Structure
Create a multi-module Maven project like this:

algo-trading/
 ├── market-data-service
 │      - Fetch candle data
 │      - Scheduled jobs
 │      - Save to DB
 │      - Call external APIs
 │
 ├── strategy-service
 │      - Use TA4J
 │      - Implement indicators
 │      - Generate buy/sell signals
 │      - Backtesting engine
 │
 ├── virtual-broker-service
 │      - Simulated virtual trades
 │      - Portfolio + PnL calculation
 │      - Apply SL/TP
 │      - Log order history
 │
 ├── common-library
 │      - Shared models (Candle, Trade, IndicatorResponse)
 │      - Shared utilities
 │
 ├── dashboard-service (optional)
 │      - REST APIs for visualization
 │
 └── docker-compose.yml

### ✨ Features to Generate Code For
You must generate:
- Complete pom.xml for parent and each module
- Entity classes (Candle, Trade, StrategyConfig, Portfolio)
- Repositories
- REST controllers
- Services
- Scheduler classes
- Algorithm logic using TA4J
- Backtesting service
- Virtual trading engine
- Portfolio valuation service
- Application.yml examples
- Dockerfile for each service
- docker-compose.yml to run all services + DB

### 📚 Final Deliverables
I want:
1. Full directory structure
2. All pom.xml files
3. Full code for controllers, services, entities, repositories
4. Full TA4J strategy implementations
5. Backtesting engine
6. Paper trading engine (virtual)
7. Scheduler for market data
8. DB schema
9. Dockerfile + docker-compose
10. README with instructions

Generate everything cleanly, modularly, and production-ready.
Use best practices for naming, architecture, and quality.

