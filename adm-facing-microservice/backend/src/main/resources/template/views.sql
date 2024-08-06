create or replace view MENU_STREAM_ADMIN.VW_TOP_SELLING_PRODUCTS as
select
	TI.DS_NAME_ITEM,
	TI.DS_VALUE_ITEM,
	TI.DS_CATEGORY_ITEM,
	TI.DS_TOTAL_SALES
from
	 MENU_STREAM_ADMIN.TB_ITENS TI
order by
	TI.DS_TOTAL_SALES desc;