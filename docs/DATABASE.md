# Database Schema Documentation

## Overview
The Algo Trading System uses PostgreSQL as its primary database. The schema is automatically created and managed by Hibernate (JPA) with `ddl-auto: update` setting.

## Tables

### 1. candles
Stores OHLC (Open, High, Low, Close) candlestick data.

| Column     | Type           | Description                    |
|------------|----------------|--------------------------------|
| id         | BIGINT (PK)    | Primary key                    |
| symbol     | VARCHAR        | Stock symbol (e.g., AAPL)      |
| timestamp  | TIMESTAMP      | Candle timestamp               |
| open       | DECIMAL(19,4)  | Opening price                  |
| high       | DECIMAL(19,4)  | Highest price                  |
| low        | DECIMAL(19,4)  | Lowest price                   |
| close      | DECIMAL(19,4)  | Closing price                  |
| volume     | BIGINT         | Trading volume                 |
| timeframe  | VARCHAR        | Timeframe (1m, 5m, 1h, etc.)   |
| created_at | TIMESTAMP      | Record creation time           |

**Indexes:**
- idx_symbol_timestamp (symbol, timestamp)
- idx_timestamp (timestamp)

### 2. strategy_configs
Stores trading strategy configurations.

| Column          | Type           | Description                    |
|-----------------|----------------|--------------------------------|
| id              | BIGINT (PK)    | Primary key                    |
| name            | VARCHAR (UK)   | Unique strategy name           |
| type            | VARCHAR        | Strategy type (SMA, RSI, etc.) |
| symbol          | VARCHAR        | Target symbol                  |
| timeframe       | VARCHAR        | Trading timeframe              |
| is_active       | BOOLEAN        | Active status                  |
| initial_capital | DECIMAL(19,4)  | Starting capital               |
| risk_per_trade  | DECIMAL(5,2)   | Risk percentage                |
| parameters      | TEXT           | JSON strategy parameters       |
| description     | TEXT           | Strategy description           |
| created_at      | TIMESTAMP      | Creation time                  |
| updated_at      | TIMESTAMP      | Last update time               |

### 3. portfolios
Tracks portfolio performance for each strategy.

| Column           | Type           | Description                    |
|------------------|----------------|--------------------------------|
| id               | BIGINT (PK)    | Primary key                    |
| strategy_id      | BIGINT (UK, FK)| Reference to strategy          |
| initial_capital  | DECIMAL(19,4)  | Starting capital               |
| current_capital  | DECIMAL(19,4)  | Current capital                |
| total_pnl        | DECIMAL(19,4)  | Total profit/loss              |
| realized_pnl     | DECIMAL(19,4)  | Realized profit/loss           |
| unrealized_pnl   | DECIMAL(19,4)  | Unrealized profit/loss         |
| total_trades     | INTEGER        | Number of trades               |
| winning_trades   | INTEGER        | Number of winning trades       |
| losing_trades    | INTEGER        | Number of losing trades        |
| win_rate         | DECIMAL(5,2)   | Win rate percentage            |
| total_commission | DECIMAL(19,4)  | Total commission paid          |
| max_drawdown     | DECIMAL(19,4)  | Maximum drawdown               |
| sharpe_ratio     | DECIMAL(10,4)  | Sharpe ratio                   |
| created_at       | TIMESTAMP      | Creation time                  |
| updated_at       | TIMESTAMP      | Last update time               |

**Indexes:**
- idx_strategy_id (strategy_id)

### 4. trades
Records all trade executions.

| Column            | Type           | Description                    |
|-------------------|----------------|--------------------------------|
| id                | BIGINT (PK)    | Primary key                    |
| strategy_id       | BIGINT (FK)    | Reference to strategy          |
| symbol            | VARCHAR        | Trading symbol                 |
| side              | VARCHAR        | BUY or SELL                    |
| type              | VARCHAR        | MARKET, LIMIT, etc.            |
| quantity          | DECIMAL(19,8)  | Trade quantity                 |
| price             | DECIMAL(19,4)  | Order price                    |
| executed_price    | DECIMAL(19,4)  | Actual execution price         |
| executed_quantity | DECIMAL(19,8)  | Actual executed quantity       |
| status            | VARCHAR        | Order status                   |
| stop_loss         | DECIMAL(19,4)  | Stop loss price                |
| take_profit       | DECIMAL(19,4)  | Take profit price              |
| pnl               | DECIMAL(19,4)  | Profit/loss                    |
| commission        | DECIMAL(19,4)  | Commission paid                |
| entry_time        | TIMESTAMP      | Entry timestamp                |
| exit_time         | TIMESTAMP      | Exit timestamp                 |
| notes             | TEXT           | Additional notes               |
| created_at        | TIMESTAMP      | Creation time                  |
| updated_at        | TIMESTAMP      | Last update time               |

**Indexes:**
- idx_symbol (symbol)
- idx_strategy_id (strategy_id)
- idx_status (status)

### 5. positions
Tracks open and closed positions.

| Column          | Type           | Description                    |
|-----------------|----------------|--------------------------------|
| id              | BIGINT (PK)    | Primary key                    |
| strategy_id     | BIGINT (FK)    | Reference to strategy          |
| symbol          | VARCHAR        | Trading symbol                 |
| side            | VARCHAR        | LONG or SHORT                  |
| quantity        | DECIMAL(19,8)  | Position quantity              |
| entry_price     | DECIMAL(19,4)  | Entry price                    |
| current_price   | DECIMAL(19,4)  | Current price                  |
| unrealized_pnl  | DECIMAL(19,4)  | Unrealized profit/loss         |
| stop_loss       | DECIMAL(19,4)  | Stop loss price                |
| take_profit     | DECIMAL(19,4)  | Take profit price              |
| is_open         | BOOLEAN        | Position status                |
| entry_time      | TIMESTAMP      | Entry timestamp                |
| exit_time       | TIMESTAMP      | Exit timestamp                 |
| created_at      | TIMESTAMP      | Creation time                  |
| updated_at      | TIMESTAMP      | Last update time               |

**Indexes:**
- idx_strategy_symbol (strategy_id, symbol)
- idx_is_open (is_open)

## Relationships

```
strategy_configs (1) -----> (1) portfolios
strategy_configs (1) -----> (*) trades
strategy_configs (1) -----> (*) positions
```

## Sample Queries

### Get Recent Candles
```sql
SELECT * FROM candles 
WHERE symbol = 'AAPL' 
  AND timeframe = '5m'
ORDER BY timestamp DESC 
LIMIT 100;
```

### Get Portfolio Performance
```sql
SELECT s.name, p.* 
FROM portfolios p
JOIN strategy_configs s ON p.strategy_id = s.id
WHERE s.is_active = true;
```

### Get Winning Trades
```sql
SELECT * FROM trades
WHERE strategy_id = 1 
  AND pnl > 0
ORDER BY pnl DESC;
```

### Get Open Positions
```sql
SELECT * FROM positions
WHERE strategy_id = 1 
  AND is_open = true;
```
