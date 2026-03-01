ALTER TABLE events
    ADD COLUMN max_participation     INT NOT NULL DEFAULT 100,
    ADD COLUMN current_participation INT NOT NULL DEFAULT 0;
