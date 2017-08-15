# --- !Ups
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

CREATE TABLE IF NOT EXISTS assignmenttable(
  id serial NOT NULL,
  title VARCHAR(100) NOT NULL,
  description VARCHAR NULL,
  PRIMARY KEY(id)
);

# --- !Downs
DROP TABLE userdatatable;
DROP TABLE assignmenttable;

