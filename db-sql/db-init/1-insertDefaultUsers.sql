
insert into user(usr_name, usr_email, usr_pass, role, isDeleted, isEmailVerified)
values ('adminTest','admin@gmail.com','{bcrypt}$2a$10$LvHjbC6hqpyipZpalGmr6.78LdYuKLwho2Viv48liLZJUwa1CJR2q' , 'admin',false,true);


insert into user(usr_name, usr_email, usr_pass, role, isDeleted, isEmailVerified)
values ('userTest','user@gmail.com','{bcrypt}$2a$10$WAfmHDPRJlPamRbe6rxLDuV34UX/9e1/HI31BWH0iY03D8NMeIIaC' , 'customer',false,true);
insert into customer(cust_id)values (2);


