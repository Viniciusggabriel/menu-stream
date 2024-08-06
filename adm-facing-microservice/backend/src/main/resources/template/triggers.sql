create or replace trigger TRG_UPDATE_SALES
before
insert
	on
	 MENU_STREAM_ADMIN.TB_ORDERS
for each row
execute function MENU_STREAM_ADMIN.FN_UPDATE_SALES_COUNT();
----------------------------------------------------------------------------------
create or replace trigger TRG_UPDATE_COUNT
before
insert
	on
	 MENU_STREAM_ADMIN.TB_ORDERS
for each row
execute function  MENU_STREAM_ADMIN.FN_UPDATE_ORDER_COUNT();