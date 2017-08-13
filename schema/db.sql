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

INSERT INTO userdatatable(id, firstname, lastname, age, gender, mobilenumber, email, password,
 isadmin) VALUES(1,'Divya', 'Dua', 21, 'female', 8130212805, 'divyaduamzn12@gmail.com',
   'admin123', true);