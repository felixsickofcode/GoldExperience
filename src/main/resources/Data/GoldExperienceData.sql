CREATE DATABASE IF NOT EXISTS gold_experience;
USE gold_experience;

CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_score (score DESC),
    INDEX idx_name (name)
);


SELECT * FROM players ORDER BY score DESC;

-- top 10 players
SELECT name, score FROM players ORDER BY score DESC LIMIT 10;