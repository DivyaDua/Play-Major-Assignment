CREATE TABLE IF NOT EXISTS userdatatable(
  id serial NOT NULL,
  firstname VARCHAR(100) NOT NULL,
  middlename VARCHAR(100),
  lastname VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  gender VARCHAR(10) NOT NULL,
  mobilenumber NUMERIC NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  isenabled BOOLEAN NOT NULL DEFAULT true,
  isadmin BOOLEAN NOT NULL DEFAULT false,
  PRIMARY KEY(id, email)
);

CREATE TABLE IF NOT EXISTS hobbiestable(
  hid INT NOT NULL,
  hobby VARCHAR(100) NOT NULL,
  PRIMARY KEY(hid)
);

INSERT INTO hobbiestable VALUES(1, 'dancing'),(2, 'listening music'),(3, 'photography'),
  (4, 'reading novels'),(5, 'watching tv');

CREATE TABLE IF NOT EXISTS userhobbiestable(
  id serial NOT NULL,
  userid INT NOT NULL,
  hid INT NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS assignmenttable(
  id serial NOT NULL,
  title VARCHAR(100) NOT NULL,
  description VARCHAR NULL,
  PRIMARY KEY(id)
);



