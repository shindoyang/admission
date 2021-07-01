# 由于微信的昵称Emoji表情是特殊的Unicode编码格式，mysql的utf8无法保存，需要转为utf8mb4
# 建表后请立即修改字段编码集
alter table wechat_user_entity change nick_name nick_name varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci ;