# Flood Zoning for eStates — PoC

A production-grade GIS-enabled flood zoning MVP for real estate risk assessment, built with **Spring Boot 3 + Java 21**, **Next.js 14 + TypeScript**, and **Oracle 19c Spatial**.

## Architecture

```
[ Next.js 14 UI ]  →  [ Spring Boot REST API ]  →  [ Oracle 19c Spatial ]
   Mapbox GL JS           Modular Monolith            SDO_GEOMETRY
   Tailwind CSS           DTO / Service / Repo         Spatial Index
```

## Project Structure

```
lereta-poc/
├── backend/          Spring Boot 3.3 + Maven
├── frontend/         Next.js 14 + TypeScript + Tailwind + Mapbox
└── db/               Oracle SQL scripts (tables, indexes, seed data)
```

## Features

- **Property search** — list and search properties by name
- **Flood zone visualization** — polygons rendered on interactive Mapbox map
- **Flood risk check** — click any point or select a property to evaluate risk
- **Risk classification** — HIGH (inside zone), MEDIUM (within 500m), LOW (outside)
- **Nearest zone lookup** — find closest flood boundary

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/properties` | List/search properties |
| `GET` | `/api/properties/{id}` | Get single property |
| `GET` | `/api/zones` | Get all flood zone polygons (GeoJSON) |
| `GET` | `/api/flood/check?lat=&lon=` | Check flood risk at coordinates |
| `GET` | `/api/flood/nearest?lat=&lon=` | Get nearest flood zone |

## Prerequisites

- **Java 21**
- **Maven 3.9+**
- **Node.js 18+**
- **Oracle 19c** with Spatial enabled
- **Mapbox** access token

## Setup

### 1. Database

Run the SQL scripts in order against your Oracle 19c instance:

```bash
db/01-create-tables.sql
db/02-spatial-metadata.sql
db/03-spatial-indexes.sql
db/04-seed-data.sql
```

### 2. Backend

Update `backend/src/main/resources/application.yml` with your Oracle connection details, then:

```bash
cd backend
mvn spring-boot:run
```

Backend starts at `http://localhost:8080`.

### 3. Frontend

Add your Mapbox token to `frontend/.env.local`:

```
NEXT_PUBLIC_MAPBOX_TOKEN=your_token_here
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

Then:

```bash
cd frontend
npm install
npm run dev
```

Frontend starts at `http://localhost:3000`.

## Tech Stack

- **Backend:** Java 21, Spring Boot 3.3, Spring Data JPA, Oracle JDBC, Lombok, Validation
- **Frontend:** Next.js 14, TypeScript, Mapbox GL JS, Tailwind CSS, Axios, Lucide Icons
- **Database:** Oracle 19c, Oracle Spatial (SDO_GEOMETRY, SDO_CONTAINS, SDO_NN, SDO_WITHIN_DISTANCE)
- **Testing:** JUnit 5, Mockito, MockMvc