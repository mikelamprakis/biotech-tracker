-- Two pulmonary and two hepatobiliary diseases.
-- Slugs use the abbreviation only where it is the name people actually search
-- for (copd, mash), matching the existing als / alzheimers / parkinsons rows.
INSERT INTO disease (name, slug) VALUES
    ('Idiopathic Pulmonary Fibrosis', 'idiopathic-pulmonary-fibrosis'),
    ('Chronic Obstructive Pulmonary Disease', 'copd'),
    ('Metabolic Dysfunction-Associated Steatohepatitis', 'mash'),
    ('Primary Sclerosing Cholangitis', 'primary-sclerosing-cholangitis');
