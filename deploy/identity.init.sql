-- CREATE DATABASE identity_access;
-- GRANT ALL PRIVILEGES ON identity_access.* TO 'root'@'localhost';
-- FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(45) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    enabled INT NOT NULL
);

CREATE TABLE authorities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    CONSTRAINT fk_authorities_user FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- Password: password (encoded using BCrypt)
INSERT INTO users (username, password, name, email, enabled)
VALUES ('superadmin', '$2a$10$Icdy05HD6OXmfVEaX3Kk0OTN3p/DYz95GNluZqvD5HTCGxLAdbP/C', 'Kent Clark', 'kentc@rpulse.com', 1);

INSERT INTO authorities (username, authority)
VALUES ('superadmin', 'ADMIN');

INSERT INTO users (username, password, name, email, enabled)
VALUES ('batman', '$2a$10$Icdy05HD6OXmfVEaX3Kk0OTN3p/DYz95GNluZqvD5HTCGxLAdbP/C', 'Bruce Wayne', 'brucew@rpulse.com', 1);

INSERT INTO authorities (username, authority)
VALUES ('batman', 'CASHIER');

INSERT INTO users (username, password, name, email, enabled)
VALUES ('ironman', '$2a$10$Icdy05HD6OXmfVEaX3Kk0OTN3p/DYz95GNluZqvD5HTCGxLAdbP/C', 'Tony Stark', 'tonys@rpulse.com', 1);

INSERT INTO authorities (username, authority)
VALUES ('ironman', 'CASHIER');

INSERT INTO users (username, password, name, email, enabled)
VALUES ('blackwidow', '$2a$10$Icdy05HD6OXmfVEaX3Kk0OTN3p/DYz95GNluZqvD5HTCGxLAdbP/C', 'Natasha', 'natasha@rpulse.com', 1);

INSERT INTO authorities (username, authority)
VALUES ('blackwidow', 'MANAGER');

CREATE TABLE oauth2_registered_client (
                                          id varchar(100) NOT NULL,
                                          client_id varchar(100) NOT NULL,
                                          client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                          client_secret varchar(200) DEFAULT NULL,
                                          client_secret_expires_at timestamp,
                                          client_name varchar(200) NOT NULL,
                                          client_authentication_methods varchar(1000) NOT NULL,
                                          authorization_grant_types varchar(1000) NOT NULL,
                                          redirect_uris varchar(1000) DEFAULT NULL,
                                          post_logout_redirect_uris varchar(1000) DEFAULT NULL,
                                          scopes varchar(1000) NOT NULL,
                                          client_settings varchar(2000) NOT NULL,
                                          token_settings varchar(2000) NOT NULL,
                                          PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization (
                                      id varchar(100) NOT NULL,
                                      registered_client_id varchar(100) NOT NULL,
                                      principal_name varchar(200) NOT NULL,
                                      authorization_grant_type varchar(100) NOT NULL,
                                      authorized_scopes varchar(1000) DEFAULT NULL,
                                      attributes blob DEFAULT NULL,
                                      state varchar(500) DEFAULT NULL,
                                      authorization_code_value blob DEFAULT NULL,
                                      authorization_code_issued_at timestamp ,
                                      authorization_code_expires_at timestamp ,
                                      authorization_code_metadata blob DEFAULT NULL,
                                      access_token_value blob DEFAULT NULL,
                                      access_token_issued_at timestamp ,
                                      access_token_expires_at timestamp ,
                                      access_token_metadata blob DEFAULT NULL,
                                      access_token_type varchar(100) DEFAULT NULL,
                                      access_token_scopes varchar(1000) DEFAULT NULL,
                                      oidc_id_token_value blob DEFAULT NULL,
                                      oidc_id_token_issued_at timestamp ,
                                      oidc_id_token_expires_at timestamp ,
                                      oidc_id_token_metadata blob DEFAULT NULL,
                                      refresh_token_value blob DEFAULT NULL,
                                      refresh_token_issued_at timestamp ,
                                      refresh_token_expires_at timestamp ,
                                      refresh_token_metadata blob DEFAULT NULL,
                                      user_code_value blob DEFAULT NULL,
                                      user_code_issued_at timestamp ,
                                      user_code_expires_at timestamp ,
                                      user_code_metadata blob DEFAULT NULL,
                                      device_code_value blob DEFAULT NULL,
                                      device_code_issued_at timestamp ,
                                      device_code_expires_at timestamp ,
                                      device_code_metadata blob DEFAULT NULL,
                                      PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization_consent (
                                              registered_client_id varchar(100) NOT NULL,
                                              principal_name varchar(200) NOT NULL,
                                              authorities varchar(1000) NOT NULL,
                                              PRIMARY KEY (registered_client_id, principal_name)
);