

CREATE database train_reservation_system;

\c train_reservation_system;

CREATE TABLE TRAIN( train_id INTEGER NOT NULL , doj DATE NOT NULL, PRIMARY KEY(train_id,doj) );
CREATE TABLE AC_COACH( berth_no INTEGER NOT NULL PRIMARY KEY, berth_type VARCHAR(3) NOT NULL);
CREATE TABLE SL_COACH( berth_no INTEGER NOT NULL PRIMARY KEY,berth_type VARCHAR(3) NOT NULL);
CREATE TABLE PASSENGER(name VARCHAR(100) NOT NULL, pnr INTEGER NOT NULL );
CREATE TABLE TRAIN_POOL( train_id INTEGER NOT NULL, doj DATE NOT NULL, COACH_TYPE VARCHAR(5) NOT NULL, coach_no INTEGER NOT NULL, berth_no INTEGER NOT NULL, berth_type VARCHAR(3) NOT NULL, empty INTEGER NOT NULL, PRIMARY KEY(train_id,doj,COACH_TYPE,coach_no,berth_no), FOREIGN KEY(train_id,doj) REFERENCES TRAIN(train_id,doj));
CREATE TABLE TICKET( passenger_name VARCHAR(100) NOT NULL, train_id INTEGER NOT NULL, doj DATE NOT NULL, pnr INTEGER NOT NULL, coach_no INTEGER NOT NULL, COACH_TYPE VARCHAR(5) NOT NULL, berth_no INTEGER NOT NULL, berth_type VARCHAR(3) NOT NULL,PRIMARY KEY(pnr,coach_no,berth_no),FOREIGN KEY(train_id,doj) REFERENCES TRAIN(train_id,doj));


CREATE or REPLACE procedure ADDTRAIN(train_id INTEGER , doj DATE, no_of_ac_coaches INTEGER,no_of_sl_coaches INTEGER )
AS
$$
DECLARE 

ac INTEGER;
sl INTEGER;
flag INTEGER;
rac record;
rsl record;
rc record;
c CURSOR for select * from TRAIN;
cursor1 CURSOR for select *from SL_COACH;
cursor2 CURSOR for select *from AC_COACH;
 i int;
begin
ac:=0;
sl:=0;
flag:=0;
open c;
loop
 fetch c into rc;
       exit when not found;
       if( rc.train_id=train_id and rc.doj=doj) then flag=flag+1;EXIT; end if;
end loop;
close c;
if flag then RAISE INFO 'train already exist on this date '; return; end if;
INSERT INTO TRAIN(train_id,doj)
VALUES (train_id,doj);
for i in 1..no_of_ac_coaches
loop 
open cursor2;
loop
 fetch cursor2 into rac;
       exit when not found;


INSERT INTO TRAIN_POOL(train_id , doj , COACH_TYPE ,coach_no, berth_no , berth_type ,empty )
    VALUES ( train_id, doj, 'AC',i, rac.berth_no, rac.berth_type, 1);

end loop;
close cursor2;
end loop;

for i in 1..no_of_sl_coaches
loop 
open cursor1;
loop
 fetch cursor1 into rsl;
       exit when not found;


INSERT INTO TRAIN_POOL(train_id , doj , COACH_TYPE ,coach_no, berth_no , berth_type ,empty )
    VALUES ( train_id, doj, 'SL',i, rsl.berth_no, rsl.berth_type, 1);

end loop;
close cursor1;
end loop;

end;
$$
language plpgsql;



CREATE OR REPLACE procedure BOOKTICKET(no_of_passengers INTEGER, names VARCHAR(100)[],train_id INTEGER, doj DATE,COACH_TYPE VARCHAR(3),p INTEGER )
AS
$$
DECLARE 

flag INTEGER;
rc record;
pnr INTEGER;

c CURSOR for select *from TRAIN_POOL;
c1 CURSOR FOR select *from TICKET;
c2 CURSOR for select * from TRAIN;
begin
flag:=0;

    -- find 3 digit number for username
    if no_of_passengers=0 then return; end if;

 

pnr=p;
open c2;
loop
 fetch c2 into rc;
       exit when not found;
       if rc.train_id=train_id and rc.doj=doj then flag=flag+1; EXIT ;end if;
end loop;
close c2;

if flag=0 then RAISE INFO 'Sorry the train does not run for this date';return ;end if;

flag=0;


open c;
loop
 fetch c into rc;
       exit when not found;
if rc.train_id=train_id AND rc.doj=doj AND  rc.COACH_TYPE=COACH_TYPE AND rc.empty=1 then 
flag=flag+1;
end if;
end loop;
close c;

if flag<no_of_passengers then RAISE INFO 'SORRY, seats not available'; return ;
end if;

flag=0;
open c;
loop
 fetch c into rc;
 exit when not found;
if flag=no_of_passengers then EXIT; end if;

if rc.train_id=train_id AND rc.doj=doj AND  rc.COACH_TYPE=COACH_TYPE AND rc.empty=1 then 

 UPDATE TRAIN_POOL
set empty=0
where TRAIN_POOL.train_id=rc.train_id AND TRAIN_POOL.coach_no=rc.coach_no AND TRAIN_POOL.doj=rc.doj AND TRAIN_POOL.COACH_TYPE=rc.COACH_TYPE AND TRAIN_POOL.berth_no=rc.berth_no AND TRAIN_POOL.berth_type=rc.berth_type;

INSERT INTO TICKET(passenger_name,train_id,doj,pnr,coach_no, COACH_TYPE,berth_no,berth_type)
VALUES(names[flag+1],train_id,doj,pnr,rc.coach_no, COACH_TYPE,rc.berth_no,rc.berth_type);
INSERT INTO PASSENGER(name,pnr)
VALUES(names[flag+1],pnr);

flag=flag+1;

end if;
end loop;
close c;
end;
$$
language plpgsql;



INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (1,'LB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (2,'LB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (3,'UB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (4,'UB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (5,'SL');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (6,'SU');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (7,'LB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (8,'LB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (9,'UB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (10,'UB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (11,'SL');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (12,'SU');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (13,'LB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (14,'LB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (15,'UB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (16,'UB');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (17,'SL');
INSERT INTO AC_COACH(berth_no,berth_type)
VALUES (18,'SU');




INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (1,'LB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (2,'MB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (3,'UB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (4,'LB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (5,'MB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (6,'UB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (7,'SL');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (8,'SU');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (9,'LB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (10,'MB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (11,'UB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (12,'LB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (13,'MB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (14,'UB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (15,'SL');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (16,'SU');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (17,'LB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (18,'MB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (19,'UB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (20,'LB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (21,'MB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (22,'UB');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (23,'SL');
INSERT INTO SL_COACH(berth_no,berth_type)
VALUES (24,'SU');



