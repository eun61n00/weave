#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE auth_service_db;
    CREATE DATABASE user_service_db;
    CREATE DATABASE quiz_service_db;
EOSQL
