CREATE TABLE optimization_requests (
                                       id          UUID PRIMARY KEY,
                                       max_volume  INT             NOT NULL,
                                       total_volume INT            NOT NULL,
                                       total_revenue DECIMAL(10,2) NOT NULL,
                                       created_at  TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE selected_shipments (
                                    id          BIGSERIAL       PRIMARY KEY,
                                    request_id  UUID            NOT NULL,
                                    name        VARCHAR(255)    NOT NULL,
                                    volume      INT             NOT NULL,
                                    revenue     DECIMAL(10,2)   NOT NULL,
                                    CONSTRAINT fk_request FOREIGN KEY (request_id)
                                        REFERENCES optimization_requests(id) ON DELETE CASCADE
);