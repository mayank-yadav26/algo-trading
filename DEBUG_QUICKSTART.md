# 🚀 Quick Debug Setup Guide

## ✅ All Done! Your services are ready for debugging

### 📍 Debug Ports (Connect from VS Code)
```
Market Data Service     → localhost:5005
Strategy Service        → localhost:5006
Virtual Broker Service  → localhost:5007
Dashboard Service       → localhost:5008
```

### 🎯 Quick Start - 3 Steps

#### 1️⃣ Set a Breakpoint
Open any Java controller/service file and click the left margin to add a red breakpoint dot.

**Example files to debug:**
```
market-data-service/src/main/java/com/mayank/algotrading/marketdata/controller/MarketDataController.java
strategy-service/src/main/java/com/mayank/algotrading/strategy/service/StrategyService.java
virtual-broker-service/src/main/java/com/mayank/algotrading/broker/service/TradingService.java
```

#### 2️⃣ Attach Debugger (Press F5)
1. Open **Run and Debug** panel (Ctrl+Shift+D)
2. Select service from dropdown (e.g., "Debug Strategy Service (Docker)")
3. Press **F5** or click green play button
4. Wait for "Debugger attached" message

#### 3️⃣ Make API Call
Trigger your breakpoint by calling the API:
```bash
curl http://localhost:8081/api/market-data/quote/AAPL
```

### 🎮 Debug Controls
- **F5** - Continue execution
- **F10** - Step over (next line)
- **F11** - Step into (enter method)
- **Shift+F11** - Step out (exit method)
- **Shift+F5** - Stop debugging

### 🔄 After Code Changes
```bash
# Rebuild and restart
docker-compose up -d --build

# Or rebuild single service
docker-compose up -d --build market-data-service
```

### 📊 View Logs
```bash
# Follow logs for all services
docker-compose logs -f

# Follow specific service
docker-compose logs -f strategy-service

# Last 50 lines
docker-compose logs --tail=50 market-data-service
```

### 🛠️ Troubleshooting
```bash
# Check services are running
docker-compose ps

# Restart specific service
docker restart market-data-service

# View detailed logs
docker logs market-data-service

# Check debug port is accessible
telnet localhost 5005
```

### 📚 Full Documentation
See `docs/DEBUGGING_GUIDE.md` for comprehensive debugging guide including:
- Advanced breakpoint techniques
- Multi-service debugging
- Performance analysis
- Production debugging tips
- Common issues and solutions

---

## 🎓 Example Debugging Session

### Debug Strategy Signal Generation

1. **Set breakpoint** in `StrategyService.java` line 45 (generateSignal method)
2. **Attach debugger** to Strategy Service (F5)
3. **Call API**:
   ```bash
   curl http://localhost:8082/api/strategies/1/signal
   ```
4. **Execution pauses** at breakpoint
5. **Inspect variables**:
   - Hover over `strategy` object
   - Check `indicators` values in Variables panel
   - Evaluate `strategy.getParameters()` in Debug Console
6. **Step through code** with F10/F11
7. **Continue** with F5 when done

### Debug Trade Execution

1. **Set breakpoint** in `TradingService.java` at `executeTrade()` method
2. **Attach debugger** to Virtual Broker Service (port 5007)
3. **Call API** with Postman:
   ```json
   POST http://localhost:8083/api/trading/execute
   {
     "strategyId": 1,
     "symbol": "AAPL",
     "orderType": "BUY",
     "quantity": 10,
     "price": 150.25
   }
   ```
4. **Step through**:
   - Portfolio balance check
   - Position creation
   - PnL calculation
5. **Inspect**:
   - `portfolio.getAvailableBalance()`
   - `position.getQuantity()`
   - `trade.getTotalValue()`

---

**Status:** ✅ All services running with debug enabled
**Updated:** November 16, 2025
