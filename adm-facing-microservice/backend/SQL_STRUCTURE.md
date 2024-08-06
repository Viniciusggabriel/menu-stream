### schema

- `create schema MENU_STREAM_ADMIN;`

### tb_tables

- `id_table`
- `number_table`
- `total_orders`

```sql
create table MENU_STREAM_ADMIN.TB_TABLES (
    ID_TABLE SERIAL primary key ,
    DS_NUMBER_TABLE CHAR(6),
    DS_TOTAL_ORDER INT
);
-- Exemplo de insert
insert
	into
	MENU_STREAM_ADMIN.tb_tables(ds_number_table, ds_total_order)
values ('Mesa01',0);
```

### tb_itens

- `id_item`
- `name_item`
- `description_item`
- `category_item`
- `value_item`
- `image_item`
- `total_sales`

```sql
create table MENU_STREAM_ADMIN.TB_ITENS (
  ID_ITEM SERIAL primary key,
  DS_NAME_ITEM VARCHAR(50),
  DS_DESCRIPTION_ITEM VARCHAR(255),
  DS_CATEGORY_ITEM CHAR(20),
  DS_VALUE_ITEM real,
  -- coluna de image
  DS_TOTAL_SALES INT
);
-- Exemplo de insert
insert
	into
	MENU_STREAM_ADMIN.tb_itens(ds_name_item,
	ds_description_item,
	ds_category_item,
	ds_value_item,
	ds_total_sales)
values ('Coca cola', 'Bebida, Refrigerante','Bebida', 8, 0);
```

### tb_orders

- `id_order`
- `fk_table_order` (chave estrangeira para `tb_tables`)
- `fk_item_order` (chave estrangeira para `tb_itens`)
- `obs_order`
- `status_order`

```sql
create table MENU_STREAM_ADMIN.TB_ORDERS(
  ID_ORDER SERIAL primary key,
  FK_TABLE_ORDER SERIAL,
  FK_ITEM_ORDER SERIAL,
  DS_OBS_ORDER VARCHAR(255),
  DS_STATUS_ORDER CHAR(20)
);

alter table MENU_STREAM_ADMIN.TB_ORDERS add constraint FK_TABLE_ORDER foreign key(FK_TABLE_ORDER) references MENU_STREAM_ADMIN.TB_TABLES(ID_TABLE);

alter table MENU_STREAM_ADMIN.TB_ORDERS add constraint FK_ITEM_ORDER foreign key(FK_ITEM_ORDER) references MENU_STREAM_ADMIN.TB_ITENS(ID_ITEM);
```

## Procedure para Inserir Pedido

```sql
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
```

## Triggers e Funções Adicionais

### Trigger para Atualizar Contagem de Pedidos por Mesa

```sql
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

create or replace trigger TRG_UPDATE_COUNT
before
insert
	on
	 MENU_STREAM_ADMIN.TB_ORDERS
for each row
execute function  MENU_STREAM_ADMIN.FN_UPDATE_ORDER_COUNT();
```

### Trigger para Atualizar Contagem de Vendas por Item

```sql
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

create or replace trigger TRG_UPDATE_SALES
before
insert
	on
	 MENU_STREAM_ADMIN.TB_ORDERS
for each row
execute function MENU_STREAM_ADMIN.FN_UPDATE_SALES_COUNT();
```

### View para Produtos Mais Vendidos

```sql
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
```