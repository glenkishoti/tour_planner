# Tour Planner - Docker Setup

This Docker setup runs the complete Tour Planner application with PostgreSQL, Spring Boot backend, and Angular frontend.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose v2.0+

## Quick Start

1. **Navigate to the project root:**
   ```bash
   cd /Users/pari/Documents/GitHub/BIF4/SWEN2/tour_planner
   ```

2. **Start all services:**
   ```bash
   docker-compose up --build
   ```

3. **Access the application:**
   - Frontend: http://localhost:4200
   - Backend API: http://localhost:8080/api

4. **Stop the services:**
   ```bash
   docker-compose down
   ```

   To also remove the database volume:
   ```bash
   docker-compose down -v
   ```

## Services

| Service | Port | Description |
|---------|------|-------------|
| PostgreSQL | 5432 | Database for tours and users |
| Backend | 8080 | Spring Boot REST API |
| Frontend | 4200 | Angular web application |

## Environment Variables

You can customize the setup by creating a `.env` file:

```env
# JWT Configuration
JWT_SECRET=your-base64-encoded-secret-here
JWT_EXPIRATION=86400000

# Database
POSTGRES_DB=tourplanner
POSTGRES_USER=touruser
POSTGRES_PASSWORD=tourpass
```

## Development Mode

To run only the database in Docker and develop locally:

```bash
# Start just PostgreSQL
docker-compose up postgres

# Run backend locally (in another terminal)
cd tourplanner-backend
mvn spring-boot:run

# Run frontend locally (in another terminal)
cd tourplanner-frontend
ng serve
```

## Troubleshooting

**Port already in use:**
```bash
# Find and kill process using port 8080 or 4200
lsof -ti:8080 | xargs kill -9
lsof -ti:4200 | xargs kill -9
```

**Rebuild from scratch:**
```bash
docker-compose down -v
docker-compose up --build
```

**View logs:**
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
```

## Architecture

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Angular   │──────▶│   Nginx     │──────▶│   Spring    │
│  Frontend   │:4200  │   (Proxy)   │:80    │   Backend   │:8080
└─────────────┘      └─────────────┘      └──────┬──────┘
                                                 │
                                                 ▼
                                          ┌─────────────┐
                                          │  PostgreSQL │
                                          │   :5432     │
                                          └─────────────┘
```

Nginx proxies `/api/*` requests to the backend and serves the Angular app for all other routes.
