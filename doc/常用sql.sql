#带分配账号
SELECT  *
		FROM tl_account a 
where
			a.del_flag = '0'
			and a.role_='0' 
			and a.id not in (select account from tl_job_user j where j.job_id='20180711-01') 	
		order by a.usernum desc limit 40
		
#抽取用户sql
SELECT *
		FROM tl_job_user a 
		left join tl_user u on u.id=a.userid
		where 
			a.del_flag = '0'
			AND a.job_id = 'auto'
		 	AND a.account = '8615087070382'
			and u.star  >= 0
	 	and not exists (select 1 from tl_job_user j where j.job_id='20180711-01' and j.userid=a.userid)	  
order by msg_time desc	 limit 40
  
  
select * from tl_job_user where account='8615087070382'