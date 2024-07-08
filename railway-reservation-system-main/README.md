# railway reservation system

## Schema

| Tables     | Attributes                                                   |
| ---------- | ------------------------------------------------------------ |
| train      | train_ID, doj                                                |
| AC_COACH   | berth_no,berth_type                                          |
| SL_COACH   | berth_no,berth_type                                          |
| train_pool | train_ID, doj, coach_type,coah_no,berth_no,berth_type,empty  |
| passenger  | name,pnr                                                     |
| TICKET     | passenger_name,train_id,doj,pnr,coach_no,berth_no,berth_type |

## Stored Procedures

- ADDTRAIN
  - Adds new train in the database
- BOOKTICKET
  - To book ticket

## BASIC CLASSES AND RELATIONAL DIAGRAM

![BASIC CLASSES AND RELATIONAL DIAGRAM](https://github.com/shyam2672/railway_reservation_system/blob/main/ER%20DIAGRAM.jpg)
