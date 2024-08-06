create or replace
function MENU_STREAM_ADMIN.FN_UPDATE_SALES_COUNT() returns trigger as $$
begin
    RAISE NOTICE 'Trigger executed for item ID: %', NEW.FK_ITEM_ORDER;

    update
        MENU_STREAM_ADMIN.TB_ITENS
    set
        DS_TOTAL_SALES = DS_TOTAL_SALES + 1
    where
        ID_ITEM = NEW.FK_ITEM_ORDER;

    return new;
end;
$$ language PLPGSQL;
----------------------------------------------------------------------------------
create or replace
function MENU_STREAM_ADMIN.FN_UPDATE_ORDER_COUNT() returns trigger as $$
begin
    update
         MENU_STREAM_ADMIN.TB_TABLES
    set
        DS_TOTAL_ORDER = DS_TOTAL_ORDER + 1
    where
	    ID_TABLE = NEW.FK_TABLE_ORDER;

return new;
end;

$$ language PLPGSQL;