-- 用于V1.5.1版本中，用户名和用户id区分大小写
-- 修改表的字符集或者排序方式

ALTER TABLE my_user_entity CHANGE username username VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

ALTER TABLE my_user_entity CHANGE user_uid user_uid VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

ALTER TABLE my_user_extend_entity CHANGE user_uid user_uid VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

ALTER TABLE my_user_relate_apps CHANGE user_uid user_uid VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin;

ALTER TABLE my_user_relate_authorities CHANGE user_uid user_uid VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin;

ALTER TABLE my_user_relate_authority_groups CHANGE user_uid user_uid VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin;

ALTER TABLE my_user_relate_question CHANGE user_uid user_uid VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin;

ALTER TABLE account_sys_entity CHANGE creator creator VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

ALTER TABLE app_entity CHANGE developer developer VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin;

ALTER TABLE child_authority_entity CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;

ALTER TABLE wechat_user_entity CHANGE oauth_user_name oauth_user_name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- 检查修改的排序规则是否生效,请一条条执行
SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'my_user_entity'  AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'my_user_extend_entity'  AND  `TABLE_SCHEMA`= 'oauth';


SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'my_user_relate_apps' AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'my_user_relate_authorities' AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'my_user_relate_authority_groups' AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'my_user_relate_question' AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'account_sys_entity' AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'app_entity' AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'child_authority_entity' AND  `TABLE_SCHEMA`= 'oauth';

SELECT `TABLE_SCHEMA`, `TABLE_NAME`, `COLUMN_NAME`, `COLLATION_NAME`
FROM `information_schema`.`COLUMNS`
WHERE `TABLE_NAME` = 'wechat_user_entity' AND  `TABLE_SCHEMA`= 'oauth';