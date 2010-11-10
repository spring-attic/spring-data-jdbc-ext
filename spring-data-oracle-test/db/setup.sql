create user proxyuser identified by proxypassword;
grant create session to proxyuser;
alter user scott grant connect through proxyuser;
create user proxyuser2 identified by proxypassword2;
grant create session to proxyuser2;
create user scott2 identified by tiger2;
grant create session to scott2;
grant resource to scott2;
alter user scott2 grant connect through proxyuser2 authenticated using password;

--XML/ADT
CREATE TABLE xml_table ( 
    id INTEGER, 
    xml_text SYS.XMLType, 
    PRIMARY KEY (id));

CREATE TABLE actor (
    id NUMBER(10),
	name VARCHAR2(50),
	age NUMBER,
	PRIMARY KEY (id));
INSERT INTO actor (id ,name, age) VALUES (1,'Thomas', 11);
INSERT INTO actor (id ,name, age) VALUES (2,'Rob', 22);
INSERT INTO actor (id ,name, age) VALUES (3,'Rod', 33);
commit;

CREATE OR REPLACE TYPE actor_type
  AS OBJECT (id NUMBER(10), name VARCHAR2(50), age NUMBER);
/
CREATE OR REPLACE TYPE actor_tab_type AS TABLE OF actor_type;
/
CREATE OR REPLACE PROCEDURE add_actor (in_actor IN actor_type)
AS
BEGIN
  INSERT into actor (id, name, age) VALUES(in_actor.id, in_actor.name, in_actor.age);
END;
/
CREATE OR REPLACE PROCEDURE get_actor (in_actor_id IN NUMBER, out_actor OUT actor_type)
AS
BEGIN
  SELECT actor_type(id, name, age) INTO out_actor FROM actor WHERE id = in_actor_id;
END;
/
CREATE OR REPLACE FUNCTION get_all_actor_types RETURN actor_tab_type
AS
  m_actor_tab actor_tab_type;
  cursor c_actor is
    select id, first_name from actor a;
BEGIN
  m_actor_tab := actor_tab_type();
  FOR r_actor IN c_actor loop
    m_actor_tab.extend;
    m_actor_tab(m_actor_tab.count) := actor_type(r_actor.id, r_actor.first_name, 0);
  END LOOP;
  RETURN m_actor_tab;
END;
/
CREATE OR REPLACE TYPE actor_name_array AS VARRAY(20) OF VARCHAR2(50);
/
CREATE OR REPLACE TYPE actor_id_array AS VARRAY(20) OF NUMBER;
/
CREATE OR REPLACE FUNCTION get_actor_names RETURN actor_name_array
AS
  l_actor_names actor_name_array := actor_name_array();
  CURSOR c_actor IS SELECT name FROM actor;
BEGIN
  FOR actor_rec IN c_actor LOOP
    l_actor_names.extend;
    l_actor_names(l_actor_names.count) := actor_rec.name;
  END LOOP;
  RETURN l_actor_names;
END;
/
CREATE OR REPLACE PROCEDURE delete_actors (in_actor_ids IN actor_id_array)
AS
BEGIN
  FOR i IN 1..in_actor_ids.count loop
    DELETE FROM actor WHERE id = in_actor_ids(i);
  END LOOP;
END;
/
CREATE OR REPLACE PROCEDURE read_actors (out_actors_cur OUT sys_refcursor)
AS
BEGIN
  OPEN out_actors_cur FOR 'select * from actor';
END;
/

--JMS

--Reprocess messages from error queue:
--update jms_product_qtbl set q_name = exception_queue, exception_queue = null, state = 0, deq_tid = null, retry_count = 0 where state = 3;

--Example for XE:

connect / as sysdba

CREATE USER jmsadmin IDENTIFIED BY jmsadmin DEFAULT TABLESPACE users;
GRANT connect TO jmsadmin;
GRANT create type TO jmsadmin;
GRANT aq_administrator_role TO jmsadmin;
ALTER USER jmsadmin QUOTA UNLIMITED ON users;

GRANT aq_user_role TO spring;

--===== Text PAYLOAD =====--

connect jmsadmin/jmsadmin

EXECUTE DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'jmsadmin.jms_text_qtbl', queue_payload_type => 'SYS.AQ$_JMS_TEXT_MESSAGE');
EXECUTE DBMS_AQADM.CREATE_QUEUE (queue_name => 'jmsadmin.jms_text_queue', queue_table => 'jmsadmin.jms_text_qtbl');
EXECUTE DBMS_AQADM.START_QUEUE (Queue_name => 'jmsadmin.jms_text_queue');
EXECUTE DBMS_AQADM.grant_queue_privilege ( privilege => 'ALL', queue_name => 'jmsadmin.jms_text_queue', grantee => 'spring', grant_option => FALSE);
--EXECUTE DBMS_AQADM.STOP_QUEUE (Queue_name => 'jmsadmin.jms_text_queue');

connect spring/spring

CREATE TABLE JMS_TEST (TEXT VARCHAR2(2000), ADDED DATE);

--===== ADT PAYLOAD =====--

connect jmsadmin/jmsadmin

create or replace TYPE PRODUCT_TYPE AS OBJECT
(
  id INTEGER,
  description VARCHAR(50),
  price DECIMAL(12,2)
);
/

GRANT EXECUTE ON PRODUCT_TYPE TO spring;

EXECUTE DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'jmsadmin.jms_product_qtbl', queue_payload_type => 'PRODUCT_TYPE');
EXECUTE DBMS_AQADM.CREATE_QUEUE (queue_name => 'jmsadmin.jms_product_queue', queue_table => 'jmsadmin.jms_product_qtbl');
EXECUTE DBMS_AQADM.START_QUEUE (Queue_name => 'jmsadmin.jms_product_queue');
EXECUTE DBMS_AQADM.grant_queue_privilege ( privilege => 'ALL', queue_name => 'jmsadmin.jms_product_queue', grantee => 'spring', grant_option => FALSE);
--EXECUTE DBMS_AQADM.STOP_QUEUE (Queue_name => 'jmsadmin.jms_product_queue');

connect spring/spring

create TABLE PRODUCT
(
  id INTEGER,
  description VARCHAR(50),
  price DECIMAL(12,2),
  PRIMARY KEY (ID)
);

--===== XML PAYLOAD =====--

connect jmsadmin/jmsadmin

EXECUTE DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'jmsadmin.jms_xml_qtbl', queue_payload_type => 'SYS.XMLType');
EXECUTE DBMS_AQADM.CREATE_QUEUE (queue_name => 'jmsadmin.jms_xml_queue', queue_table => 'jmsadmin.jms_xml_qtbl');
EXECUTE DBMS_AQADM.START_QUEUE (Queue_name => 'jmsadmin.jms_xml_queue');
EXECUTE DBMS_AQADM.grant_queue_privilege ( privilege => 'ALL', queue_name => 'jmsadmin.jms_xml_queue', grantee => 'spring', grant_option => FALSE);
--EXECUTE DBMS_AQADM.STOP_QUEUE (Queue_name => 'jmsadmin.jms_xml_queue');

--===== Map PAYLOAD =====--

connect jmsadmin/jmsadmin

EXECUTE DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'jmsadmin.jms_map_qtbl', queue_payload_type => 'SYS.AQ$_JMS_MAP_MESSAGE');
EXECUTE DBMS_AQADM.CREATE_QUEUE (queue_name => 'jmsadmin.jms_map_queue', queue_table => 'jmsadmin.jms_map_qtbl');
EXECUTE DBMS_AQADM.START_QUEUE (Queue_name => 'jmsadmin.jms_map_queue');
EXECUTE DBMS_AQADM.grant_queue_privilege ( privilege => 'ALL', queue_name => 'jmsadmin.jms_map_queue', grantee => 'spring', grant_option => FALSE);
--EXECUTE DBMS_AQADM.STOP_QUEUE (Queue_name => 'jmsadmin.jms_map_queue');

--===== Byte PAYLOAD =====--

connect jmsadmin/jmsadmin

EXECUTE DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'jmsadmin.jms_bytes_qtbl', queue_payload_type => 'SYS.AQ$_JMS_BYTES_MESSAGE');
EXECUTE DBMS_AQADM.CREATE_QUEUE (queue_name => 'jmsadmin.jms_bytes_queue', queue_table => 'jmsadmin.jms_bytes_qtbl');
EXECUTE DBMS_AQADM.START_QUEUE (Queue_name => 'jmsadmin.jms_bytes_queue');
EXECUTE DBMS_AQADM.grant_queue_privilege ( privilege => 'ALL', queue_name => 'jmsadmin.jms_bytes_queue', grantee => 'spring', grant_option => FALSE);
--EXECUTE DBMS_AQADM.STOP_QUEUE (Queue_name => 'jmsadmin.jms_bytes_queue');

--===== Object PAYLOAD =====--

connect jmsadmin/jmsadmin

EXECUTE DBMS_AQADM.CREATE_QUEUE_TABLE (queue_table => 'jmsadmin.jms_object_qtbl', queue_payload_type => 'SYS.AQ$_JMS_OBJECT_MESSAGE');
EXECUTE DBMS_AQADM.CREATE_QUEUE (queue_name => 'jmsadmin.jms_object_queue', queue_table => 'jmsadmin.jms_object_qtbl');
EXECUTE DBMS_AQADM.START_QUEUE (Queue_name => 'jmsadmin.jms_object_queue');
EXECUTE DBMS_AQADM.grant_queue_privilege ( privilege => 'ALL', queue_name => 'jmsadmin.jms_object_queue', grantee => 'spring', grant_option => FALSE);
--EXECUTE DBMS_AQADM.STOP_QUEUE (Queue_name => 'jmsadmin.jms_object_queue');

