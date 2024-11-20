drop table if exists _user;
create table _user(
	id int GENERATED ALWAYS AS IDENTITY primary key,
	firstname varchar(50),
	lastname varchar(50),
	email varchar(50),
	password varchar(250),
	phone varchar(50),
	position varchar(50),
	enabled boolean default false
);

/*

  private Integer id;
  private String firstname;
  private String lastname;
  private String email;
  private String password;
  private String phone;
  private String position;
*/