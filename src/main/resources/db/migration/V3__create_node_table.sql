-- src/main/resources/db/migration/V2__create_node_table.sql
CREATE TABLE IF NOT EXISTS node (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  type VARCHAR(50) NOT NULL,         -- 'PDB' 등
  name VARCHAR(255) NOT NULL,
  status VARCHAR(50) NOT NULL,       -- 'PENDING','RUNNING','SUCCESS','FAILED'
  x DOUBLE NOT NULL,
  y DOUBLE NOT NULL,
  file_path VARCHAR(1000),           -- PDB 파일 저장 경로(옵션)
  meta JSON NULL,                    -- 타입별 확장 정보(옵션)
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_node_project FOREIGN KEY (project_id) REFERENCES sample(id) -- 기존 Project 테이블명/PK에 맞춰 조정
);
