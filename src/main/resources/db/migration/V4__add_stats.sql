-- 1. Включаем генерацию UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 6. AR-объекты (справочник)
CREATE TABLE IF NOT EXISTS ar_objects (
  id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tour_id   UUID NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
  lat       DOUBLE PRECISION NOT NULL,
  lon       DOUBLE PRECISION NOT NULL
);

-- 7. Факт: какие туры пользователь начал/завершил
CREATE TABLE IF NOT EXISTS user_tours (
  user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  tour_id     UUID NOT NULL REFERENCES tours(id) ON DELETE CASCADE,
  started_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  finished_at TIMESTAMPTZ NULL,
  PRIMARY KEY (user_id, tour_id)
);

-- 8. Факт: какие истории пользователь завершил
CREATE TABLE IF NOT EXISTS user_history_progress (
  user_id     UUID NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
  history_id  UUID NOT NULL REFERENCES histories(id) ON DELETE CASCADE,
  completed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, history_id)
);

-- 9. Факт: какие AR-объекты пользователь отсканировал
CREATE TABLE IF NOT EXISTS user_ar_scans (
  user_id      UUID NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
  ar_object_id UUID NOT NULL REFERENCES ar_objects(id) ON DELETE CASCADE,
  scanned_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, ar_object_id)
);

-- 10. Материализованная статистика (опционально)
CREATE TABLE IF NOT EXISTS user_stats (
  user_id              UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  tours_started_count  INTEGER NOT NULL DEFAULT 0,
  tours_finished_count INTEGER NOT NULL DEFAULT 0,
  histories_done_count INTEGER NOT NULL DEFAULT 0,
  ar_scans_count       INTEGER NOT NULL DEFAULT 0,
  updated_at           TIMESTAMPTZ NOT NULL DEFAULT now()
);
