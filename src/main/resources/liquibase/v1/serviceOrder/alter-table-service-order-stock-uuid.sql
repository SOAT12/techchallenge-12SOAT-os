-- liquibase formatted sql

-- changeset seu_nome:alter-stock-id-to-uuid

-- 1. (Opcional) Limpa a tabela para evitar erros de conversão de dados antigos BIGINT para UUID.
-- Como é ambiente de desenvolvimento, isso é o mais seguro.
TRUNCATE TABLE service_order_stock;

-- 2. Remove a chave primária atual (o nome padrão que o Postgres dá é nome_da_tabela_pkey)
ALTER TABLE service_order_stock DROP CONSTRAINT service_order_stock_pkey;

-- 3. Altera a coluna para o tipo UUID
-- O "USING (gen_random_uuid())" ensina o Postgres como lidar com a conversão de tipo
ALTER TABLE service_order_stock ALTER COLUMN stock_id TYPE UUID USING (gen_random_uuid());

-- 4. Recria a chave primária com as colunas corretas
ALTER TABLE service_order_stock ADD PRIMARY KEY (service_order_id, stock_id);