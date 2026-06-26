CREATE TABLE disease (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE company (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE trial (
    id              BIGSERIAL PRIMARY KEY,
    disease_id      BIGINT       NOT NULL REFERENCES disease(id),
    company_id      BIGINT       REFERENCES company(id),
    nct_id          VARCHAR(50)  NOT NULL UNIQUE,
    title           TEXT         NOT NULL,
    phase           VARCHAR(50),
    status          VARCHAR(100),
    sponsor         VARCHAR(255),
    start_date      DATE,
    last_updated    TIMESTAMP,
    source_url      TEXT
);

CREATE TABLE publication (
    id             BIGSERIAL PRIMARY KEY,
    disease_id     BIGINT   NOT NULL REFERENCES disease(id),
    pubmed_id      VARCHAR(50) UNIQUE,
    title          TEXT     NOT NULL,
    abstract       TEXT,
    authors        TEXT,
    journal        VARCHAR(255),
    published_date DATE,
    source_url     TEXT
);

CREATE TABLE event (
    id           BIGSERIAL PRIMARY KEY,
    disease_id   BIGINT       NOT NULL REFERENCES disease(id),
    event_type   VARCHAR(100) NOT NULL,
    summary      TEXT         NOT NULL,
    impact_score INTEGER      DEFAULT 5,
    ref_type     VARCHAR(50),
    ref_id       BIGINT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trial_disease ON trial(disease_id);
CREATE INDEX idx_publication_disease ON publication(disease_id);
CREATE INDEX idx_event_disease ON event(disease_id);
CREATE INDEX idx_event_created ON event(created_at DESC);

INSERT INTO disease (name, slug) VALUES
    ('ALS', 'als'),
    ('Alzheimer''s Disease', 'alzheimers'),
    ('Pancreatic Cancer', 'pancreatic-cancer');
