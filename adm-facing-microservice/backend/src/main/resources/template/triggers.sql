CREATE TRIGGER trg_update_count
AFTER INSERT ON bd_adm.tb_orders
FOR EACH ROW
EXECUTE FUNCTION fn_update_order_count();

CREATE TRIGGER trg_update_sales
AFTER INSERT ON bd_adm.tb_orders
FOR EACH ROW
EXECUTE FUNCTION pr_update_sales_count();