# --- !Ups
CREATE TABLE IF NOT EXISTS userdatatable(
  id serial NOT NULL,
  firstname VARCHAR(100) NOT NULL,
  middlename VARCHAR(100),
  lastname VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  gender VARCHAR(10) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,

  PRIMARY KEY(id, email)
);

# --- !Downs
DROP TABLE userdatatable;