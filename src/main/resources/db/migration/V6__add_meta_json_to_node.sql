-- V3__add_meta_json_to_node.sql
ALTER TABLE node
  ADD COLUMN IF NOT EXISTS meta_json JSON NULL;
