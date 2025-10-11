-- src/main/resources/db/migration/V3__fix_node_project_fk.sql
ALTER TABLE node
  DROP FOREIGN KEY fk_node_project;

ALTER TABLE node
  ADD CONSTRAINT fk_node_project
  FOREIGN KEY (project_id) REFERENCES project(id)  -- ← 실제 프로젝트 테이블명으로 교체
  ON DELETE CASCADE;
