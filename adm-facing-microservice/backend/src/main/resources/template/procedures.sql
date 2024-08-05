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