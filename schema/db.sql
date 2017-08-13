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

INSERT INTO userdatatable(firstname, lastname, age, gender, mobilenumber, email, password,
 isadmin) VALUES('Divya', 'Dua', 21, 'female', 8130212805, 'divyaduamzn12@gmail.com',
   'admin123', true);

CREATE TABLE IF NOT EXISTS hobbiestable(
  hid INT NOT NULL,
  hobby VARCHAR(100) NOT NULL,
  PRIMARY KEY(hid)
);

INSERT INTO hobbiestable VALUES(1, 'dancing'),(2, 'listening music'),(3, 'photography'),
  (4, 'reading novels'),(5, 'watching tv');

CREATE TABLE IF NOT EXISTS userhobbiestable(
  id serial NOT NULL,
  useremail VARCHAR(100) NOT NULL,
  hid INT NOT NULL,
  PRIMARY KEY(id)
);

INSERT INTO userhobbiestable(useremail, hid) VALUES('divyaduamzn12@gmail.com', 1),
('divyaduamzn12@gmail.com', 3),('divyaduamzn12@gmail.com', 5);

