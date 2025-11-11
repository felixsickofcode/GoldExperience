# GoldExperience# Test
create database
## 🗄️ Database Setup

Run the following SQL commands to create and initialize the **gold_experience** database:

```sql
USE gold_experience;

DROP TABLE IF EXISTS players;

CREATE TABLE IF NOT EXISTS players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    chapter INT NOT NULL DEFAULT 1,
    level INT NOT NULL DEFAULT 1,
    score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_player_level (name, chapter, level),
    INDEX idx_chapter_level_score (chapter, level, score DESC),
    INDEX idx_name (name)
);

DESCRIBE players;

SELECT name, score FROM players WHERE chapter = 1 AND level = 1 
ORDER BY score DESC LIMIT 10;

SELECT name, SUM(score) AS total_score FROM players GROUP BY name 
ORDER BY total_score DESC LIMIT 10;
```
