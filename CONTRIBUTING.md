# Contributing to Algo Trading System

Thank you for your interest in contributing to the Algo Trading System! This document provides guidelines and instructions for contributing.

## 🌟 How to Contribute

### Reporting Bugs
1. Check if the bug has already been reported in [Issues](https://github.com/mayank-yadav26/algo-trading/issues)
2. If not, create a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - System information (OS, Java version, etc.)
   - Relevant logs or screenshots

### Suggesting Enhancements
1. Check existing [Issues](https://github.com/mayank-yadav26/algo-trading/issues) and [Pull Requests](https://github.com/mayank-yadav26/algo-trading/pulls)
2. Create a new issue describing:
   - The enhancement goal
   - Why it would be useful
   - Possible implementation approach

### Pull Requests
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes following our coding standards
4. Write or update tests as needed
5. Update documentation if required
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

## 💻 Development Setup

### Local Development
```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/algo-trading.git
cd algo-trading

# Start PostgreSQL
docker run -d --name algo-trading-db \
  -e POSTGRES_DB=algotrading \
  -e POSTGRES_USER=algotrading \
  -e POSTGRES_PASSWORD=algotrading \
  -p 5432:5432 postgres:15-alpine

# Build the project
./mvnw clean install

# Run a specific service
cd market-data-service
../mvnw spring-boot:run
```

### Debugging with VS Code

All services are configured for remote debugging in Docker. The `.vscode/launch.json` file contains pre-configured debug profiles.

#### Quick Debug Steps:
1. **Start services in Docker**
   ```bash
   docker-compose up -d
   ```

2. **Open VS Code Debug Panel** (`Ctrl+Shift+D`)

3. **Select debug configuration:**
   - Debug Market Data Service (Docker) - Port 5005
   - Debug Strategy Service (Docker) - Port 5006
   - Debug Virtual Broker Service (Docker) - Port 5007
   - Debug Dashboard Service (Docker) - Port 5008

4. **Set breakpoints** and press `F5` to attach

5. **Make API call** to trigger your breakpoint

#### Debug Configuration (`.vscode/launch.json`)
The project includes ready-to-use launch configurations:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug Strategy Service (Docker)",
      "request": "attach",
      "hostName": "localhost",
      "port": 5006,
      "projectName": "strategy-service"
    }
    // ... other services
  ]
}
```

📚 **Comprehensive debugging guide**: [`DEBUG_QUICKSTART.md`](DEBUG_QUICKSTART.md) and [`docs/DEBUGGING_GUIDE.md`](docs/DEBUGGING_GUIDE.md)

### Code Style
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Keep methods small and focused
- Write unit tests for new functionality

### Commit Messages
- Use present tense ("Add feature" not "Added feature")
- Use imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit first line to 72 characters
- Reference issues and pull requests

## 🧪 Testing
```bash
# Run all tests
./mvnw test

# Run tests for a specific module
cd strategy-service
../mvnw test
```

## 📝 Documentation
- Update README.md if adding new features
- Add JavaDoc comments for public APIs
- Update API documentation if endpoints change
- Include usage examples for new features

## 🔍 Code Review Process
1. All submissions require review
2. Maintainers will review code for:
   - Functionality
   - Code quality
   - Test coverage
   - Documentation
   - Adherence to project standards

## 📋 Areas for Contribution
- Additional trading strategies
- Enhanced backtesting metrics
- UI/Frontend development
- Performance optimizations
- Documentation improvements
- Bug fixes
- Test coverage
- Integration with other data providers

## 🤝 Code of Conduct
- Be respectful and inclusive
- Focus on constructive feedback
- Accept constructive criticism gracefully
- Prioritize community well-being

## 📬 Questions?
Feel free to open an issue for questions or reach out to the maintainers.

Thank you for contributing! 🎉
