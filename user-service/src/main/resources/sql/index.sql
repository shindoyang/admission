--查看索引
--show index from [table];

--索引
alter table my_user_entity add index index_mobile(mobile);
alter table my_user_entity add index index_loginname(login_name);
alter table my_user_relate_apps add index index_username(username);
alter table authority_entity add index index_appkey(app_key);
