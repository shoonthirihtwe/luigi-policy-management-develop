-- INSERT INTO `maintenance_requests_no` (`id`, `tenant_id`, `request_no`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,1,'0000000000000000000001',1,'2021-08-12 15:15:14',NULL,NULL,NULL,NULL,NULL),(2,2,'0000000000000000000001',1,'2021-08-12 15:15:14',NULL,NULL,NULL,NULL,NULL);
INSERT INTO `products` (`tenant_id`, `product_code`) VALUES (1,'001'),(2,'001');

INSERT INTO `sales_products` (`id`, `tenant_id`,`product_code`, `sales_plan_code`, `sales_plan_type_code`, `start_date`, `end_date`, `sales_plan_name`, `sales_plan_name_display`, `issue_age_upper`, `issue_age_lower`, `active_inactive`, `special_requirement`, `sort_no`, `termination_date_pattern`, `termination_date_order`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,1,'001','M01','MD',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(2,1,'001','P01','PT',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(3,1,'001','H01','HP',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(4,1,'001','E01','EP',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(5,2,'001','M01','MD',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(6,1,'001','P01','PT',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(7,1,'001','H01','HP',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(8,2,'001','E01','EP',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL),(9,2,'001','M01','MD',NULL,NULL,NULL,NULL,NULL,NULL,'A',NULL,NULL,NULL,NULL,1,'2021-08-12 15:38:32',NULL,NULL,NULL,NULL,NULL);

INSERT INTO `customers` (`id`, `tenant_id`, `customer_id`, `corporate_individual_flag`, `index_name`, `notification_flag`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,1,'000000000001','1',NULL,NULL,1,'2021-08-12 12:05:55',NULL,NULL,NULL,NULL,NULL),(2,1,'000000000002','1',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(3,1,'000000000003','1',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(4,1,'000000000004','2',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(5,1,'000000000005','2',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(6,2,'000000000001','1',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(7,2,'000000000002','1',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(8,2,'000000000003','1',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(9,2,'000000000004','2',NULL,NULL,1,'2021-08-12 12:06:26',NULL,NULL,NULL,NULL,NULL),(10,1,'000000000010','1',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL),(11,1,'000000000011','1',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL),(12,1,'000000000012','1',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL),(13,2,'000000000013','2',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL),(14,1,'000000000014','2',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL),(15,1,'000000000015','1',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL),(16,2,'000000000016','1',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL),(17,2,'000000000017','1',NULL,NULL,1,'2021-08-12 16:34:06',NULL,NULL,NULL,NULL,NULL);

INSERT INTO `customers_individual` (`id`, `tenant_id`, `customer_id`, `name_kana_sei`, `name_kana_mei`, `name_knj_sei`, `name_knj_mei`, `sex`, `date_of_birth`, `addr_zip_code`, `addr_kana_pref`, `addr_kana_1`, `addr_kana_2`, `addr_knj_pref`, `addr_knj_1`, `addr_knj_2`, `addr_tel1`, `addr_tel2`, `company_name_kana`, `company_name_kanji`, `place_of_work_kana`, `place_of_work_kanji`, `place_of_work_code`, `group_column`, `email`, `occupation`, `occupation_code`, `enguarded_type`, `guardian_id`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,1,'000000000001','テストコジン','イチノイチ','テスト個人','一之一','1','1972-03-12','151-0072',NULL,NULL,NULL,'東京都','渋谷区幡ヶ谷','8330','03-3104-7244',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ichainbase.test01@gmail.com',NULL,NULL,NULL,NULL,1,'2021-08-12 12:31:55',NULL,'2021-08-12 12:31:55',NULL,NULL,NULL),(2,1,'000000000002','テストコジン','イチノ二','テスト個人','一之二','2','1983-05-04','479-0046',NULL,NULL,NULL,'東京都','西多摩郡檜原村藤原','4-6-9','0569-18-4745','090-3832-0069',NULL,NULL,NULL,NULL,NULL,NULL,'ichainbase.test01@gmail.com',NULL,NULL,NULL,NULL,1,'2021-08-12 12:31:55',NULL,'2021-08-12 12:31:55',NULL,NULL,NULL),(3,1,'000000000003','テストコジン','イチノサン','テスト個人','一之三','1','1988-01-12','503-0637',NULL,NULL,NULL,'岐阜県','海津市海津町安田','4-4-4','0584-85-5947','090-6855-1208 ',NULL,NULL,NULL,NULL,NULL,NULL,'ichainbase.test01@gmail.com',NULL,NULL,NULL,NULL,1,'2021-08-12 12:31:55',NULL,'2021-08-12 12:31:55',NULL,NULL,NULL),(4,2,'000000000001','テストコジン','ニノイチ','テスト個人','二之一','2','1974-09-12','010-0066',NULL,NULL,NULL,'秋田県','秋田市牛島南','7-5-1','018-357-6690','070-5640-0159 ',NULL,NULL,NULL,NULL,NULL,NULL,'ichainbase.test01@gmail.com',NULL,NULL,NULL,NULL,1,'2021-08-12 12:31:55',NULL,'2021-08-12 12:31:55',NULL,NULL,NULL),(5,2,'000000000002','テストコジン','ニノ二','テスト個人','二之二','1','1994-06-25','750-1137',NULL,NULL,NULL,'山口県','下関市小月南町','1-2-3','083-53-1745','090-1816-5565',NULL,NULL,NULL,NULL,NULL,NULL,'ichainbase.test01@gmail.com',NULL,NULL,NULL,NULL,1,'2021-08-12 12:31:55',NULL,'2021-08-12 12:31:55',NULL,NULL,NULL),(6,2,'000000000003','テストコジン','ニノサン','テスト個人','二之三','2','1945-10-01','018-0146',NULL,NULL,NULL,'秋田県','にかほ市象潟町蒲谷地','4-2-10','090-7937-5974',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ichainbase.test01@gmail.com',NULL,NULL,NULL,NULL,1,'2021-08-12 12:31:55',NULL,'2021-08-12 12:31:55',NULL,NULL,NULL);

INSERT INTO `customers_corporate` (`id`, `tenant_id`, `customer_id`, `corp_name_kana`, `corp_name_official`, `corp_addr_zip_code`, `corp_addr_kana_pref`, `corp_addr_kana_1`, `corp_addr_kana_2`, `corp_addr_knj_pref`, `corp_addr_knj_1`, `corp_addr_knj_2`, `rep10e_sex`, `rep10e_date_of_birth`, `rep10e_name_kana_sei`, `rep10e_name_kana_mei`, `rep10e_name_knj_sei`, `rep10e_name_knj_mei`, `rep10e_addr_zip_code`, `rep10e_addr_kana_pref`, `rep10e_addr_kana_1`, `rep10e_addr_kana_2`, `rep10e_addr_knj_pref`, `rep10e_addr_knj_1`, `rep10e_addr_knj_2`, `rep10e_addr_tel1`, `rep10e_addr_tel2`, `contact_name_kana_sei`, `contact_name_kana_mei`, `contact_name_knj_sei`, `contact_name_knj_mei`, `contact_addr_zip_code`, `contact_addr_kana_pref`, `contact_addr_kana_1`, `contact_addr_kana_2`, `contact_addr_knj_pref`, `contact_addr_knj_1`, `contact_addr_knj_2`, `contact_addr_tel1`, `contact_addr_tel2`, `contact_email`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,1,'000000000004','カブシキガイシャホウジントイチノイチ','株式会社法人一之一','190-0202',NULL,NULL,NULL,'東京都','西多摩郡檜原村藤原','4-6-9','1','1983-12-05','ダイヒョウイ','イチノイチノイチ','代表','一之一之一','151-0072','東京都','渋谷区幡ヶ谷','8330','',NULL,NULL,'03-3104-7244',NULL,NULL,'タントウシャ','イチノイチノイチ','担当者','010-0066','一之一之一',NULL,NULL,'秋田県','秋田市牛島南','7-5-1','018-357-6690','070-5640-0159 ','ichainbase.test01@gmail.com',1,'2021-08-12 15:01:49',NULL,NULL,NULL,NULL,NULL),(2,1,'000000000005','カブシキガイシャホウジンイチノニ','株式会社法人一之二',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2',NULL,'ダイヒョウイ','イチノ二ノイチ','代表','一之二之一','479-0046','東京都','西多摩郡檜原村藤原','4-6-9','',NULL,NULL,'0569-18-4745','090-3832-0069',NULL,'タントウシャ','イチノ二ノイチ','担当者','750-1137','一之二之一',NULL,NULL,'山口県','下関市小月南町','1-2-3','083-53-1745','090-1816-5565','ichainbase.test01@gmail.com',1,'2021-08-12 15:01:49',NULL,NULL,NULL,NULL,NULL),(3,2,'000000000004','カブシキガイシャホウジンニノイチ','株式会社法人二之一',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'1',NULL,'ダイヒョウイ','ニノイチノイチ','代表','二之一之一','503-0637','岐阜県','海津市海津町安田','4-4-4','',NULL,NULL,'0584-85-5947','090-6855-1208',NULL,'タントウシャ','ニノイチノイチ','担当者','018-0146','二之一之一',NULL,NULL,'秋田県','にかほ市象潟町蒲谷地','4-2-10','090-7937-5974',NULL,'ichainbase.test01@gmail.com',1,'2021-08-12 15:01:49',NULL,NULL,NULL,NULL,NULL);

INSERT INTO `contracts` (`id`, `tenant_id`, `contract_no`, `contract_branch_no`, `contract_status`, `update_cnt`, `last_contract_id`, `new_contract_id`, `application_date`, `received_date`, `entry_date`, `inception_date`, `complete_date`, `first_premium_date`, `effective_date`, `issue_date`, `expiration_date`, `termination_base_date`, `termination_date`, `termination_title`, `free_lock_date`, `insurance_start_date`, `insurance_end_date`, `premium_start_date`, `premium_end_date`, `number_of_insured`, `coverage_term`, `card_cust_number`, `card_unavailable_flag`, `frequency`, `payment_method`, `product`, `sales_plan_code`, `sales_plan_type_code`, `basic_policy_code`, `hii_other_insurance`, `contractor_customer_id`, `total_premium`, `insured_customer_id`, `relationship`, `premium`, `sales_method`, `reinsurance_comp_code`, `research_comp_code`, `suspend_status`, `agency_code_1`, `agent_code_1`, `agent_share_1`, `agency_code_2`, `agent_code_2`, `agent_share_2`, `mypage_link_date`, `payment_pattern`, `payment_date_order`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,1,'0000000101','01','40',1,NULL,NULL,'2021-07-10','2021-07-10','2021-07-10','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000123',NULL,'12','3',NULL,'M01','MD',NULL,'0','000000000001',10000,'000000000001','01',0,'02',NULL,NULL,NULL,'00001','000001',50,'00002','000002',50,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(2,1,'0000000102','01','40',1,NULL,NULL,'2021-07-10','2021-07-11','2021-07-11','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000124',NULL,'12','3',NULL,'P01','PT',NULL,'0','000000000002',20000,'000000000010','01',0,'02',NULL,NULL,NULL,'00002','000002',100,NULL,NULL,NULL,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(3,1,'0000000103','01','40',1,NULL,NULL,NULL,'2021-07-12','2021-07-12','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000125',NULL,'12','3',NULL,'H01','HP',NULL,'0','000000000003',30000,'000000000011','01',NULL,'02',NULL,NULL,NULL,'00003','000003',100,NULL,NULL,NULL,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(4,1,'0000000104','01','60',1,NULL,NULL,'2021-07-10','2021-07-13','2021-07-13','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000126',NULL,'12','3',NULL,'E01','EP',NULL,'0','000000000004',40000,'000000000012','01',0,'02',NULL,NULL,NULL,'00004','000004',100,NULL,NULL,NULL,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(5,2,'0000000104','01','40',1,NULL,NULL,'2021-07-10','2021-07-14','2021-07-14','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000127',NULL,'12','3',NULL,'M01','MD',NULL,'0','000000000001',50000,'000000000013','01',0,'02',NULL,NULL,NULL,'00004','000004',100,NULL,NULL,NULL,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(6,1,'0000000105','01','40',1,NULL,NULL,'2021-07-10','2021-07-15','2021-07-15','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000128',NULL,'12','3',NULL,'P01','PT',NULL,'0','000000000005',60000,'000000000014',NULL,NULL,NULL,NULL,NULL,NULL,'00005','000005',100,NULL,NULL,NULL,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(7,1,'0000000106','01','60',1,NULL,NULL,'2021-07-10','2021-07-16','2021-07-16','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000129',NULL,'12','3',NULL,'H01','HP',NULL,'0','000000000001',70000,'000000000015',NULL,NULL,NULL,NULL,NULL,NULL,'00006','000006',100,NULL,NULL,NULL,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(8,2,'0000000105','01','NU',1,NULL,NULL,'2021-07-10','2021-07-17','2021-07-17','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000130',NULL,'12','3',NULL,'E01','EP',NULL,'0','000000000002',80000,'000000000016',NULL,NULL,NULL,NULL,NULL,NULL,'00007','000007',100,NULL,NULL,NULL,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL),(9,2,'0000000101','01','40',1,NULL,NULL,'2021-07-10','2021-07-18','2021-07-18','2021-07-10','2021-08-01','2021-07-14','2021-08-01','2021-08-01','2022-07-31',NULL,NULL,NULL,NULL,'2021-08-01','2022-07-31','2021-08-01','2022-07-31',1,1,'1230000131',NULL,'12','3',NULL,'M01','MD',NULL,'0','000000000004',90000,'000000000017',NULL,NULL,NULL,NULL,NULL,NULL,'00001','000001',50,'00002','000002',50,NULL,NULL,NULL,1,'2021-08-12 16:39:31',NULL,NULL,NULL,NULL,NULL);