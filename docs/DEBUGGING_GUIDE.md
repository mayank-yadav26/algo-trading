# 🐛 Docker Debugging Guide

## Overview
This guide explains how to debug your Spring Boot microservices running in Docker containers using VS Code.

## 🔧 Configuration Summary

### Debug Ports
Each service has a dedicated debug port exposed:

| Service | Application Port | Debug Port |
|---------|-----------------|------------|
| **Market Data Service** | 8081 | 5005 |
| **Strategy Service** | 8082 | 5006 |
| **Virtual Broker Service** | 8083 | 5007 |
| **Dashboard Service** | 8080 | 5008 |

### JDWP Parameters Explained
```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```

- `transport=dt_socket` - Use socket transport for debugging
- `server=y` - JVM acts as debug server (waits for debugger to attach)
- `suspend=n` - Don't wait for debugger before starting application (use `suspend=y` to pause at startup)
- `address=*:5005` - Listen on all interfaces on port 5005

## 🚀 How to Debug

### Step 1: Ensure Containers are Running
```bash
docker-compose ps
```

All services should show **Up** status with debug ports exposed.

### Step 2: Open VS Code Debug Panel
1. Click on the **Run and Debug** icon in the left sidebar (or press `Ctrl+Shift+D`)
2. Select the service you want to debug from the dropdown menu

Available debug configurations:
- `Debug Market Data Service (Docker)`
- `Debug Strategy Service (Docker)`
- `Debug Virtual Broker Service (Docker)`
- `Debug Dashboard Service (Docker)`

### Step 3: Set Breakpoints
1. Open the Java file you want to debug
2. Click on the left margin next to the line number to set a breakpoint
3. A red dot will appear indicating a breakpoint

**Example locations to set breakpoints:**
```
market-data-service/src/main/java/com/mayank/algotrading/marketdata/controller/MarketDataController.java
strategy-service/src/main/java/com/mayank/algotrading/strategy/service/StrategyService.java
virtual-broker-service/src/main/java/com/mayank/algotrading/broker/service/TradingService.java
dashboard-service/src/main/java/com/mayank/algotrading/dashboard/controller/DashboardController.java
```

### Step 4: Start Debugging
1. Select the appropriate debug configuration from the dropdown
2. Press **F5** or click the green **Start Debugging** button
3. VS Code will attach to the running Docker container
4. You should see "Debugger attached" message in the Debug Console

### Step 5: Trigger Your Code
Make an API request to trigger the breakpoint:
```bash
# Example: Debug Market Data Service
curl http://localhost:8081/api/market-data/quote/AAPL
```

The execution will pause at your breakpoint, and you can:
- **Step Over** (F10) - Execute current line and move to next
- **Step Into** (F11) - Enter into method calls
- **Step Out** (Shift+F11) - Exit current method
- **Continue** (F5) - Resume execution
- **Inspect Variables** - Hover over variables or use Variables panel
- **Evaluate Expressions** - Use Debug Console to evaluate expressions

## 📋 Common Debugging Scenarios

### Scenario 1: Debug Strategy Signal Generation
1. Set breakpoint in `StrategyService.java` at `generateSignal()` method
2. Attach debugger to Strategy Service (port 5006)
3. Make request: `GET http://localhost:8082/api/strategies/1/signal`
4. Step through the TA4J indicator calculations

### Scenario 2: Debug Trade Execution
1. Set breakpoint in `TradingService.java` at `executeTrade()` method
2. Attach debugger to Virtual Broker Service (port 5007)
3. Make request: `POST http://localhost:8083/api/trading/execute`
4. Inspect portfolio calculations and position updates

### Scenario 3: Debug Market Data Fetching
1. Set breakpoint in `MarketDataService.java` at `fetchQuote()` method
2. Attach debugger to Market Data Service (port 5005)
3. Make request: `GET http://localhost:8081/api/market-data/quote/AAPL`
4. Step through external API calls and data transformation

### Scenario 4: Debug Backtesting
1. Set breakpoint in `BacktestService.java` at `runBacktest()` method
2. Attach debugger to Strategy Service (port 5006)
3. Make request: `POST http://localhost:8082/api/strategies/1/backtest?days=90`
4. Step through TA4J strategy execution and PnL calculations

## 🛠️ Advanced Debugging Tips

### 1. Conditional Breakpoints
Right-click on a breakpoint → **Edit Breakpoint** → Add condition
```java
// Example: Break only when symbol equals "AAPL"
symbol.equals("AAPL")
```

### 2. Logpoints
Right-click on line → **Add Logpoint** → Enter log message
```
Symbol: {symbol}, Price: {price}
```
This logs without stopping execution.

### 3. Watch Expressions
Add expressions to **Watch** panel to monitor values:
```
portfolio.getTotalValue()
strategy.getParameters()
```

### 4. Debug Multiple Services Simultaneously
You can attach debuggers to multiple services at the same time:
1. Start debugging Market Data Service
2. In Debug Panel, click "+" to add another debug session
3. Select Strategy Service and attach
4. Both services will be debuggable simultaneously

### 5. Hot Reload Changes
For rapid development without rebuilding Docker:
1. Make code changes
2. Build locally: `mvn clean package -DskipTests`
3. Copy new JAR to container:
```bash
docker cp market-data-service/target/*.jar market-data-service:/app/app.jar
docker restart market-data-service
```

## 🔄 Restart Services After Code Changes

### Option 1: Rebuild Specific Service
```bash
docker-compose up -d --build market-data-service
```

### Option 2: Rebuild All Services
```bash
docker-compose down
docker-compose up -d --build
```

### Option 3: Quick Restart (Without Rebuild)
```bash
docker restart market-data-service
```

## 🐞 Debugging Production Issues

### View Container Logs
```bash
# Real-time logs
docker-compose logs -f market-data-service

# Last 100 lines
docker-compose logs --tail=100 market-data-service

# All services
docker-compose logs -f
```

### Execute Commands in Container
```bash
# Open shell in container
docker exec -it market-data-service sh

# View Java process
docker exec -it market-data-service ps aux | grep java
```

### Check Debug Port Connectivity
```bash
# From host machine
telnet localhost 5005

# Should show: Connected to localhost.
```

### Inspect Container Environment
```bash
docker exec -it market-data-service env | grep SPRING
```

## 🔧 Troubleshooting

### Issue: Debugger Won't Attach
**Solution:**
1. Check if container is running: `docker-compose ps`
2. Verify debug port is exposed: `docker port market-data-service 5005`
3. Check logs: `docker logs market-data-service`
4. Restart container: `docker restart market-data-service`

### Issue: Breakpoint Not Hit
**Solution:**
1. Ensure code matches running version (rebuild if needed)
2. Check if source path is correct in `.vscode/launch.json`
3. Verify request actually reaches that code path
4. Check logs to see if endpoint was called

### Issue: Variables Show "Not Available"
**Solution:**
1. Code might be optimized - add `-Xdebug` to JVM args
2. Rebuild with debug symbols: `mvn clean package -DskipTests`
3. Ensure you're debugging correct service

### Issue: Timeout Connecting to Debug Port
**Solution:**
1. Service might still be starting - wait 30 seconds
2. Check firewall settings
3. Restart Docker: `sudo systemctl restart docker`

### Issue: Code Changes Not Reflected
**Solution:**
1. Rebuild service: `docker-compose up -d --build <service-name>`
2. Clear Maven cache: `mvn clean`
3. Remove old image: `docker rmi algo-trading_<service-name>`

## 📊 Performance Debugging

### Enable JMX Monitoring
Add to docker-compose.yml environment:
```yaml
environment:
  JAVA_TOOL_OPTIONS: >
    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
    -Dcom.sun.management.jmxremote
    -Dcom.sun.management.jmxremote.port=9010
    -Dcom.sun.management.jmxremote.local.only=false
    -Dcom.sun.management.jmxremote.authenticate=false
    -Dcom.sun.management.jmxremote.ssl=false
```

### Memory Analysis
```bash
# Heap dump
docker exec market-data-service jmap -dump:live,format=b,file=/tmp/heap.bin 1

# Copy to host
docker cp market-data-service:/tmp/heap.bin ./heap.bin

# Analyze with Eclipse MAT or VisualVM
```

### Thread Dump
```bash
docker exec market-data-service jstack 1
```

## 🎯 Best Practices

1. **Always set meaningful breakpoints** - Don't break in loops unless necessary
2. **Use conditional breakpoints** - For high-frequency code paths
3. **Watch expressions carefully** - Evaluating expressions can have side effects
4. **Detach when done** - Don't leave debugger attached unnecessarily
5. **Use logging** - Add strategic log statements for production debugging
6. **Version control .vscode/** - Share debug configurations with team
7. **Document complex flows** - Add comments explaining debugging strategy

## 🔗 Useful Commands Reference

```bash
# Start all services with debug
docker-compose up -d --build

# Stop all services
docker-compose down

# Restart specific service
docker restart market-data-service

# View logs
docker-compose logs -f market-data-service

# Attach to running container
docker exec -it market-data-service sh

# Check debug port
netstat -an | grep 5005

# Rebuild and restart
docker-compose up -d --build --force-recreate market-data-service
```

## 📚 Additional Resources

- [VS Code Java Debugging](https://code.visualstudio.com/docs/java/java-debugging)
- [Spring Boot Docker Debugging](https://spring.io/guides/topicals/spring-boot-docker)
- [JDWP Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/jdwp-spec.html)
- [TA4J Documentation](https://github.com/ta4j/ta4j/wiki)

---

Happy Debugging! 🎉
