# API Guide - Algo Trading System

This guide provides detailed examples of how to interact with each service's REST API.

## 🔗 Base URLs

| Service | URL |
|---------|-----|
| Dashboard | http://localhost:8080 |
| Market Data | http://localhost:8081 |
| Strategy | http://localhost:8082 |
| Virtual Broker | http://localhost:8083 |

## 📊 Market Data Service API

### Fetch Real-Time Quote
```bash
curl -X GET "http://localhost:8081/api/market-data/quote/AAPL"
```

**Response:**
```json
{
  "symbol": "AAPL",
  "timestamp": "2024-11-16T10:30:00",
  "open": 150.25,
  "high": 151.50,
  "low": 149.75,
  "close": 150.80,
  "volume": 0,
  "timeframe": "1m"
}
```

### Fetch Historical Data
```bash
curl -X GET "http://localhost:8081/api/market-data/historical/AAPL?timeframe=5m&from=2024-11-01T00:00:00&to=2024-11-16T23:59:59"
```

### Get Recent Candles
```bash
curl -X GET "http://localhost:8081/api/market-data/candles/AAPL/recent?timeframe=5m&count=100"
```

### Get Latest Candle
```bash
curl -X GET "http://localhost:8081/api/market-data/candles/AAPL/latest?timeframe=5m"
```

## 🎯 Strategy Service API

### Create a Strategy

#### SMA Strategy
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
    "description": "10/30 SMA crossover strategy for AAPL"
  }'
```

#### RSI Strategy
```bash
curl -X POST "http://localhost:8082/api/strategies" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "RSI Strategy TSLA",
    "type": "RSI",
    "symbol": "TSLA",
    "timeframe": "15m",
    "isActive": true,
    "initialCapital": 20000,
    "riskPerTrade": 1.5,
    "parameters": "{\"period\":14,\"oversold\":30,\"overbought\":70}",
    "description": "RSI mean reversion strategy"
  }'
```

#### MACD Strategy
```bash
curl -X POST "http://localhost:8082/api/strategies" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MACD Strategy GOOGL",
    "type": "MACD",
    "symbol": "GOOGL",
    "timeframe": "1h",
    "isActive": true,
    "initialCapital": 15000,
    "riskPerTrade": 2.5,
    "parameters": "{\"shortPeriod\":12,\"longPeriod\":26,\"signalPeriod\":9}",
    "description": "MACD momentum strategy"
  }'
```

### Get All Strategies
```bash
curl -X GET "http://localhost:8082/api/strategies"
```

### Get Strategy by ID
```bash
curl -X GET "http://localhost:8082/api/strategies/1"
```

### Get Active Strategies
```bash
curl -X GET "http://localhost:8082/api/strategies/active"
```

### Update Strategy
```bash
curl -X PUT "http://localhost:8082/api/strategies/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated SMA Strategy",
    "type": "SMA",
    "symbol": "AAPL",
    "timeframe": "5m",
    "isActive": false,
    "initialCapital": 10000,
    "riskPerTrade": 2.0,
    "parameters": "{\"shortPeriod\":12,\"longPeriod\":26}",
    "description": "Updated parameters"
  }'
```

### Delete Strategy
```bash
curl -X DELETE "http://localhost:8082/api/strategies/1"
```

### Generate Trading Signal
```bash
curl -X GET "http://localhost:8082/api/strategies/1/signal"
```

**Response:**
```json
{
  "strategyId": 1,
  "symbol": "AAPL",
  "signal": "BUY",
  "price": 150.50,
  "suggestedQuantity": null,
  "stopLoss": null,
  "takeProfit": null,
  "timestamp": "2024-11-16T10:35:00",
  "reason": null,
  "indicators": {
    "SMA20": 148.75,
    "RSI14": 55.23,
    "CLOSE": 150.50
  }
}
```

### Run Backtest
```bash
curl -X POST "http://localhost:8082/api/strategies/1/backtest?startDate=2024-01-01T00:00:00&endDate=2024-11-16T23:59:59"
```

**Response:**
```json
{
  "strategyId": 1,
  "strategyName": "SMA Crossover AAPL",
  "symbol": "AAPL",
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-11-16T23:59:59",
  "initialCapital": 10000.00,
  "finalCapital": 12500.00,
  "totalReturn": 2500.00,
  "totalReturnPercentage": 25.00,
  "totalTrades": 45,
  "winningTrades": 28,
  "losingTrades": 17,
  "winRate": 62.22,
  "maxDrawdown": 850.00,
  "sharpeRatio": 1.45,
  "averageWin": 125.50,
  "averageLoss": 75.30
}
```

## 💼 Virtual Broker Service API

### Create Portfolio
```bash
curl -X POST "http://localhost:8083/api/portfolio/create?strategyId=1&initialCapital=10000"
```

### Execute Trade (Buy)
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
    "takeProfit": 160.00,
    "timestamp": "2024-11-16T10:35:00"
  }'
```

**Response:**
```json
{
  "id": 1,
  "strategyId": 1,
  "symbol": "AAPL",
  "side": "BUY",
  "type": "MARKET",
  "quantity": 10,
  "price": 150.50,
  "executedPrice": 150.57,
  "executedQuantity": 10,
  "status": "EXECUTED",
  "stopLoss": 145.00,
  "takeProfit": 160.00,
  "commission": 1.51,
  "entryTime": "2024-11-16T10:35:05",
  "createdAt": "2024-11-16T10:35:05"
}
```

### Execute Trade (Sell)
```bash
curl -X POST "http://localhost:8083/api/trading/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "strategyId": 1,
    "symbol": "AAPL",
    "signal": "SELL",
    "price": 155.00,
    "timestamp": "2024-11-16T11:30:00"
  }'
```

### Get Trades for Strategy
```bash
curl -X GET "http://localhost:8083/api/trading/trades/strategy/1"
```

### Get Open Positions
```bash
curl -X GET "http://localhost:8083/api/trading/positions/strategy/1"
```

### Get Portfolio
```bash
curl -X GET "http://localhost:8083/api/portfolio/strategy/1"
```

### Get Portfolio Summary
```bash
curl -X GET "http://localhost:8083/api/portfolio/strategy/1/summary"
```

**Response:**
```json
{
  "strategyId": 1,
  "strategyName": "SMA Crossover AAPL",
  "initialCapital": 10000.00,
  "currentCapital": 10425.50,
  "totalPnl": 425.50,
  "totalPnlPercentage": 4.26,
  "realizedPnl": 425.50,
  "unrealizedPnl": 0.00,
  "totalTrades": 5,
  "winningTrades": 4,
  "losingTrades": 1,
  "winRate": 80.00,
  "totalCommission": 24.50
}
```

## 📈 Dashboard Service API

### Get System Overview
```bash
curl -X GET "http://localhost:8080/api/dashboard/overview"
```

**Response:**
```json
{
  "totalStrategies": 3,
  "activeStrategies": 2,
  "totalPortfolios": 3,
  "openPositions": 1
}
```

### Get All Portfolio Summaries
```bash
curl -X GET "http://localhost:8080/api/dashboard/portfolios"
```

### Get Strategy Performance
```bash
curl -X GET "http://localhost:8080/api/dashboard/strategy/1/performance"
```

**Response:**
```json
{
  "strategy": { ... },
  "portfolio": { ... },
  "recentTrades": [ ... ],
  "openPositions": [ ... ]
}
```

### Get Recent Trades
```bash
curl -X GET "http://localhost:8080/api/dashboard/trades/recent?limit=20"
```

### Get All Open Positions
```bash
curl -X GET "http://localhost:8080/api/dashboard/positions/open"
```

## 🔄 Complete Trading Workflow

### 1. Fetch Historical Data
```bash
curl -X GET "http://localhost:8081/api/market-data/historical/AAPL?timeframe=5m&from=2024-10-01T00:00:00&to=2024-11-16T23:59:59"
```

### 2. Create Trading Strategy
```bash
curl -X POST "http://localhost:8082/api/strategies" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My AAPL Strategy",
    "type": "SMA",
    "symbol": "AAPL",
    "timeframe": "5m",
    "isActive": true,
    "initialCapital": 10000,
    "parameters": "{\"shortPeriod\":10,\"longPeriod\":30}"
  }'
```

### 3. Run Backtest
```bash
curl -X POST "http://localhost:8082/api/strategies/1/backtest?startDate=2024-10-01T00:00:00&endDate=2024-11-16T23:59:59"
```

### 4. Create Portfolio
```bash
curl -X POST "http://localhost:8083/api/portfolio/create?strategyId=1&initialCapital=10000"
```

### 5. Generate Signal
```bash
curl -X GET "http://localhost:8082/api/strategies/1/signal"
```

### 6. Execute Trade
```bash
curl -X POST "http://localhost:8083/api/trading/execute" \
  -H "Content-Type: application/json" \
  -d '{
    "strategyId": 1,
    "symbol": "AAPL",
    "signal": "BUY",
    "price": 150.50,
    "suggestedQuantity": 10
  }'
```

### 7. Monitor Performance
```bash
curl -X GET "http://localhost:8080/api/dashboard/strategy/1/performance"
```

## 🔍 Testing Tips

### Using Postman
1. Import the Swagger JSON from any service's `/api-docs` endpoint
2. Create environment variables for base URLs
3. Save commonly used requests in collections

### Using curl with variables
```bash
# Set variables
BASE_URL="http://localhost:8082"
STRATEGY_ID=1

# Use in requests
curl -X GET "$BASE_URL/api/strategies/$STRATEGY_ID"
```

### Pretty print JSON responses
```bash
curl -X GET "http://localhost:8080/api/dashboard/overview" | jq .
```

## 📚 Additional Resources

- Swagger UI: Each service has interactive API documentation
- OpenAPI Spec: Available at `/api-docs` endpoint for each service
- Database Schema: See `docs/DATABASE.md` for database structure
