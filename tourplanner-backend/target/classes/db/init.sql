CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash TEXT NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tours (
                       id SERIAL PRIMARY KEY,
                       user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
                       name VARCHAR(255) NOT NULL,
                       description TEXT,
                       start_location VARCHAR(255),
                       end_location VARCHAR(255),
                       distance_km FLOAT,
                       estimated_time INTEGER,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tour_stops (
                            id SERIAL PRIMARY KEY,
                            tour_id INTEGER REFERENCES tours(id) ON DELETE CASCADE,
                            name VARCHAR(255),
                            latitude DOUBLE PRECISION,
                            longitude DOUBLE PRECISION,
                            position_order INTEGER
);

CREATE TABLE tour_logs (
                           id SERIAL PRIMARY KEY,
                           tour_id INTEGER REFERENCES tours(id) ON DELETE CASCADE,
                           date DATE,
                           comment TEXT,
                           difficulty INTEGER,
                           rating INTEGER,
                           total_time INTEGER
);