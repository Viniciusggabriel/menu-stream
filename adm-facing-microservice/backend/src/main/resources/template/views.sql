CREATE VIEW vw_top_selling_products AS
SELECT
    name_item,
    category_item,
    total_sales
FROM bd_cliente.tb_itens
ORDER BY total_sales DESC;