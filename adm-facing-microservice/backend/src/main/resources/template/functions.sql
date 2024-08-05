CREATE OR REPLACE FUNCTION fn_update_order_count() RETURNS TRIGGER AS $$
BEGIN
    UPDATE bd_cliente.tb_tables
    SET all_orders = all_orders + 1
    WHERE id_table = NEW.fk_table_order;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_update_sales_count() RETURNS TRIGGER AS $$
BEGIN
    UPDATE bd_cliente.tb_itens
    SET total_sales = total_sales + 1
    WHERE id_item = NEW.fk_item_order;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;