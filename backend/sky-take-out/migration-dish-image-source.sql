-- Dish images hosted by Unsplash. This migration only replaces the original local placeholder filenames.
UPDATE dish
SET image = CASE id
    WHEN 46 THEN 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=800&q=80'
    WHEN 47 THEN 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=800&q=80'
    WHEN 48 THEN 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&w=800&q=80'
    WHEN 49 THEN 'https://unsplash.com/photos/n-zlxaLfS7k/download?force=true&w=800'
    WHEN 50 THEN 'https://unsplash.com/photos/n-zlxaLfS7k/download?force=true&w=800'
    WHEN 51 THEN 'https://unsplash.com/photos/EMRcqDxu62Q/download?force=true&w=800'
    WHEN 52 THEN 'https://unsplash.com/photos/3wT2zoO2KV0/download?force=true&w=800'
    WHEN 53 THEN 'https://unsplash.com/photos/EMRcqDxu62Q/download?force=true&w=800'
    WHEN 54 THEN 'https://unsplash.com/photos/q-M8nQqBJvU/download?force=true&w=800'
    WHEN 55 THEN 'https://unsplash.com/photos/q-M8nQqBJvU/download?force=true&w=800'
    WHEN 56 THEN 'https://unsplash.com/photos/q-M8nQqBJvU/download?force=true&w=800'
    WHEN 57 THEN 'https://unsplash.com/photos/q-M8nQqBJvU/download?force=true&w=800'
    WHEN 58 THEN 'https://unsplash.com/photos/3wT2zoO2KV0/download?force=true&w=800'
    WHEN 59 THEN 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800&q=80'
    WHEN 60 THEN 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=800&q=80'
    WHEN 61 THEN 'https://unsplash.com/photos/VgcD5HcHVuU/download?force=true&w=800'
    WHEN 62 THEN 'https://unsplash.com/photos/v3rgYgCaEuA/download?force=true&w=800'
    WHEN 63 THEN 'https://unsplash.com/photos/v3rgYgCaEuA/download?force=true&w=800'
    WHEN 64 THEN 'https://unsplash.com/photos/v3rgYgCaEuA/download?force=true&w=800'
    WHEN 65 THEN 'https://unsplash.com/photos/3wT2zoO2KV0/download?force=true&w=800'
    WHEN 66 THEN 'https://unsplash.com/photos/3wT2zoO2KV0/download?force=true&w=800'
    WHEN 67 THEN 'https://unsplash.com/photos/3wT2zoO2KV0/download?force=true&w=800'
    WHEN 68 THEN 'https://images.unsplash.com/photo-1484723091739-30a097e8f929?auto=format&fit=crop&w=800&q=80'
    WHEN 69 THEN 'https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=800&q=80'
    WHEN 71 THEN 'https://unsplash.com/photos/n-zlxaLfS7k/download?force=true&w=800'
END
WHERE id IN (46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 71)
  AND (image IS NULL OR image = '' OR image REGEXP '^[0-9]+[.]png$');
