CREATE KEYSPACE scalaevents
WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};

USE scalaevents;
 CREATE TABLE events(
   id UUID PRIMARY KEY,
   amount decimal,
   content text,
   currency text,
   event_type text,
   payment_type text,
   transaction_time text,
   transaction_id uuid
   );