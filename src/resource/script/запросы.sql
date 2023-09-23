
update  parlist set pname = pnumb || '-' || pname where pnumb > 0
ALTER TABLE artikls DROP CONSTRAINT IARTIKLS;
ALTER TABLE artikls ALTER COLUMN aname TYPE VARCHAR(120);
update artikls set aname = cast(atypm as varchar(2)) || '.' || cast(atypp as varchar(2)) || '-' || aname

Ветки системы
select c.name, j.name || ' / ' || k.name || ' / ' || e.name || ' / ' || d.name, b.code,  b.name
from sysprof a
left join artikl b on a.artikl_id = b.id
left join syssize c on b.syssize_id = c.id
left join systree d on a.systree_id = d.id
left join systree e on d.parent_id = e.id
left join systree k on e.parent_id = k.id
left join systree j on k.parent_id = j.id
order by c.id, k.name, e.name, d.name, b.code

Используемые параметры
select groups_id, text from elempar1 where groups_id in (4801,15010) union
select groups_id, text from elempar2 where groups_id in (4801,15010) union
select groups_id, text from furnpar1 where groups_id in (4801,15010) union
select groups_id, text from furnpar2 where groups_id in (4801,15010) union
select groups_id, text from glaspar1 where groups_id in (4801,15010) union
select groups_id, text from glaspar2 where groups_id in (4801,15010) union
select groups_id, text from joinpar1 where groups_id in (4801,15010) union
select groups_id, text from joinpar2 where groups_id in (4801,15010) order by 1

Вставки по артикулу элемента спецификации
select b.id, a.* from element a,  elemdet b
where  a.id = b.element_id and b.artikl_id = 2233

Вставки по первому параметру
select a.text, b.id, b.name, c.code, c.name from elempar1 a, element b, artikl c
where a.element_id = b.id and b.artikl_id = c.id and a.groups_id = 37010

Фурнитура по артикулу элемента спецификации
select b.id, a.* from furniture a,  furndet b
where  a.id = b.furniture_id1 and b.artikl_id = 4620
select b.id, c.id,  a.* from furniture a,  furndet b, furndet c
where  a.id = b.furniture_id1 and b.id = c.furndet_id and c.artikl_id = 4620

Поиск артикула в конструктиве (PS4)
select * from VSTASPC where anumb = 'Самор.3,9х25 с/св'
select * from CONNSPC where anumb = 'Самор.3,9х25 с/св'
select * from GLASART where anumb = 'Самор.3,9х25 с/св'
select * from FURNSPC where anumb = 'Самор.3,9х25 с/св'

Подбор текстуры (кажется)
select  id, bin_shr(bin_and(solor_us, 3840), 8), bin_shr(bin_and(solor_us, 240), 4), bin_and(solor_us, 15) from elemdet  union
select  id, bin_shr(bin_and(solor_us, 3840), 8), bin_shr(bin_and(solor_us, 240), 4), bin_and(solor_us, 15) from glasdet  union
select  id, bin_shr(bin_and(solor_us, 3840), 8), bin_shr(bin_and(solor_us, 240), 4), bin_and(solor_us, 15) from joindet  union
select  id, bin_shr(bin_and(solor_us, 3840), 8), bin_shr(bin_and(solor_us, 240), 4), bin_and(solor_us, 15) from furndet

SELECT u.RDB$USER, u.RDB$RELATION_NAME
FROM RDB$USER_PRIVILEGES u
WHERE u.rdb$relation_name = 'DEFROLE'
ORDER BY 1, 2

SELECT CURRENT_USER FROM RDB$DATABASE

Удаление всех данных в бд
delete from artikl;
delete from kits;
delete from joining;
delete from glasgrp;
delete from furniture;
delete from project;
delete from prjpart;
delete from rulecalc;
delete from parmap; 
delete from params;  
delete from color;    
delete from syssize; 
delete from currenc;
delete from sysprod;
delete from sysmodel;
delete from groups where grup != 9;
alter table systree drop constraint fk_systree1;
delete from systree;
