### 1. Create DB dump:
``pg_dump -U postgres kishinev > kishinev.sql``

### 2. Copy dump to prod:
`U can use mc or even git`

### 3. Restore dump on prod:
```
#close all connections to DB (stop Tomcat, close Postico, etc..)

psql -U postgres
DROP DATABASE kishinev;
CREATE DATABASE kishinev OWNER postgres;

psql -U postgres kishinev < kishinev.sql
```