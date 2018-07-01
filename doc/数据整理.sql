
#update account usernum 
update tl_account t set usernum=(select count(distinct userid) from tl_job_user c where c.account=t.id and job_id='auto');
update tl_account t set groupnum=(select count(distinct chatid) from tl_chat c where c.account=t.id);


#query 
select count(*) from tl_job_user  where job_id='auto';
select account,count(*) from tl_job_user where job_id ='auto' group by account;
select * from tl_job_user where userid='263197640';
#copy to tl_job_user
insert into tl_job_user 
select u.id,'auto',u.account,u.from_group,u.userId,u.username,u.userHash,tu.firstname,tu.lastname,'0','1',CURRENT_TIMESTAMP,'1',CURRENT_TIMESTAMP,'0' from tl_users u
left join tl_user tu on tu.id=u.userId
where u.username is not null and u.username!='';


#delete repeat user
delete from tl_job_user  where id in (select * from (
SELECT id FROM tl_job_user a WHERE  a.job_id='auto' and  a.userid IN (
SELECT userid fROM  tl_job_user GROUP BY  userid HAVING COUNT(*) > 1 ) AND id NOT IN (
SELECT MIN(id) FROM  tl_job_user GROUP BY  userid HAVING COUNT(*) > 1 )) aa);


#in job_user  but not in tl_user
select * from tl_user
#copy to tl_user
insert into tl_user
select userid,username,firstname,lastname,null,CURRENT_TIMESTAMP,0,0,CURRENT_TIMESTAMP from  tl_job_user where userid not in (select id from tl_user);

##
UPDATE tl_task a SET 
			usernum = (select count(*) from tl_job_user where job_id='20180701-01' and account=a.account)
		WHERE job_id = '20180701-01'
		

#
update tl_group set out_ ='0' where out_ is null;		
 