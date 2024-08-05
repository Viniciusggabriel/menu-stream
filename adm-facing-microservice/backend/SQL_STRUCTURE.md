# Tabelas e Procedures do Projeto de Cardápio Online

## Tabelas do Banco de Dados

### bd_adm.tb_user

- `id_user`
- `username`
- `password`
- `role`

### bd_cliente.tb_tables

- `id_mesa`
- `number_table`
- `all_orders`

### bd_cliente.tb_itens

- `id_item`
- `name_item`
- `description_item`
- `category_item`
- `value_item`
- `image_item`
- `total_sales`

### bd_adm.tb_orders

- `id_order`
- `fk_table_order` (chave estrangeira para `bd_cliente.tb_tables`)
- `fk_item_order` (chave estrangeira para `bd_cliente.tb_itens`)
- `obs_order`
- `status_order`

## Procedure para Inserir Pedido

```sql
CREATE OR REPLACE PROCEDURE pr_insert_order(
    p_table CHAR(6),
    p_item_name VARCHAR(50),
    p_item_quantity INT,
    p_item_obs VARCHAR(255)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_table INT;
    v_id_item INT;
BEGIN
    -- Obter o ID do item com base no name
    SELECT it.id_item INTO v_id_item FROM bd_cliente.tb_itens AS it WHERE it.name_item = p_item_name;

    -- Obter o ID da table
    SELECT me.id_table INTO v_id_table FROM bd_cliente.tb_tables AS me WHERE me.number_table = p_table;

    -- Verificar se a quantity do item é válida
    IF p_item_quantity > 0 THEN
        -- Inserir o pedido na tabela Pedido
        FOR i IN 1..p_item_quantity LOOP
            INSERT INTO bd_adm.tb_orders(fk_table_pedido, fk_item_order, obs_order, status_order)
            VALUES (v_id_table, v_id_item, p_item_obs, 'processo');
        END LOOP;
    ELSE
        RAISE EXCEPTION 'Quantidade de item inválida: %', p_item_quantity;
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE EXCEPTION 'Item ou table não encontrada';
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Erro ao inserir pedido: %', SQLERRM;
END $$;
```

## Triggers e Funções Adicionais

### Trigger para Atualizar Contagem de Pedidos por Mesa

```sql
CREATE OR REPLACE FUNCTION fn_update_order_count() RETURNS TRIGGER AS $$
BEGIN
    UPDATE bd_cliente.tb_tables
    SET all_orders = all_orders + 1
    WHERE id_table = NEW.fk_table_order;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_count
AFTER INSERT ON bd_adm.tb_orders
FOR EACH ROW
EXECUTE FUNCTION fn_update_order_count();
```

### Trigger para Atualizar Contagem de Vendas por Item

```sql
CREATE OR REPLACE FUNCTION fn_update_sales_count() RETURNS TRIGGER AS $$
BEGIN
    UPDATE bd_cliente.tb_itens
    SET total_sales = total_sales + 1
    WHERE id_item = NEW.fk_item_order;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_sales
AFTER INSERT ON bd_adm.tb_orders
FOR EACH ROW
EXECUTE FUNCTION pr_update_sales_count();
```

### View para Produtos Mais Vendidos

```sql
CREATE VIEW vw_top_selling_products AS
SELECT
    name_item,
    category_item,
    total_sales
FROM bd_cliente.tb_itens
ORDER BY total_sales DESC;
```
