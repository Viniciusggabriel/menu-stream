create or replace
procedure MENU_STREAM_ADMIN.PR_INSERT_ORDER(
    P_TABLE CHAR(6),
    P_ITEM_NAME VARCHAR(50),
    P_ITEM_QUANTITY INT,
    P_ITEM_OBS VARCHAR(255)
)
language PLPGSQL
as $$
declare
    V_ID_TABLE INT;

V_ID_ITEM INT;

begin
-- OBTER O ID DO ITEM COM BASE NO NAME
    select
	IT.ID_ITEM
into
	V_ID_ITEM
from
	 MENU_STREAM_ADMIN.TB_ITENS as IT
where
	IT.DS_NAME_ITEM = P_ITEM_NAME;

-- OBTER O ID DA TABLE
select
	ME.ID_TABLE
into
	V_ID_TABLE
from
	 MENU_STREAM_ADMIN.TB_TABLES as ME
where
	ME.DS_NUMBER_TABLE = P_TABLE;

-- Verifica se a quantidade itens é válida
if P_ITEM_QUANTITY > 0 then

-- Insere os pedidos na tabela de pedido atrávez de um loop
    for I in 1.. P_ITEM_QUANTITY loop
        insert into MENU_STREAM_ADMIN.TB_ORDERS(
            FK_TABLE_ORDER,
            FK_ITEM_ORDER,
            DS_OBS_ORDER,
            DS_STATUS_ORDER)
       values (V_ID_TABLE, V_ID_ITEM, P_ITEM_OBS, 'PROCESSO');
    end loop;
else
    raise exception 'NÃO É POSSIVEL FAZER O PEDIDO DE ZERO ITENS: %', P_ITEM_QUANTITY;
end if;

exception
when NO_DATA_FOUND then
        raise exception 'ITEM OU TABLE NÃO ENCONTRADA';
when others then
        raise exception 'ERRO AO INSERIR PEDIDO: %',
sqlerrm;
end $$;