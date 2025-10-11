-- V2__node_core.sql
CREATE TABLE IF NOT EXISTS node (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  type VARCHAR(50) NOT NULL,
  name VARCHAR(255) NOT NULL,
  status VARCHAR(50) NOT NULL,
  x DOUBLE DEFAULT 0,
  y DOUBLE DEFAULT 0,
  meta_json JSON NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS node_file (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  node_id BIGINT NOT NULL,
  original_name VARCHAR(512) NOT NULL,
  stored_path VARCHAR(1024) NOT NULL,
  content_type VARCHAR(255),
  size BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_node_file__node
    FOREIGN KEY (node_id) REFERENCES node(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS node_link (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL,
  source_node_id BIGINT NOT NULL,
  target_node_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_node_link__source FOREIGN KEY (source_node_id) REFERENCES node(id) ON DELETE CASCADE,
  CONSTRAINT fk_node_link__target FOREIGN KEY (target_node_id) REFERENCES node(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_node__project ON node(project_id);
CREATE INDEX idx_file__node ON node_file(node_id);
CREATE INDEX idx_link__project ON node_link(project_id);
CREATE INDEX idx_link__target ON node_link(target_node_id);
