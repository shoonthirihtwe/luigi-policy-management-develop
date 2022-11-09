INSERT INTO `calendars` (`tenant_id`, `date`, `holiday_flag`, `description`) VALUES (1, '2023-04-10', 0, NULL),(1, '2023-04-11', 0, NULL);

INSERT INTO `customers` (`tenant_id`, `customer_id`, `corporate_individual_flag`, `index_name`, `notification_flag`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,'000000003042','1',NULL,NULL,1,'2021-08-10 18:09:24',NULL,NULL,NULL,NULL,NULL),(1,'000000003045','2',NULL,NULL,1,'2021-08-10 18:09:24',NULL,NULL,NULL,NULL,NULL),(2,'000000003045','2',NULL,NULL,1,'2021-08-10 18:09:24',NULL,NULL,NULL,NULL,NULL),(2,'000000003042','1',NULL,NULL,1,'2021-08-10 18:09:24',NULL,NULL,NULL,NULL,NULL);

INSERT INTO `customers_corporate` (`tenant_id`, `customer_id`, `corp_name_kana`, `corp_name_official`, `corp_addr_zip_code`, `corp_addr_kana_pref`, `corp_addr_kana_1`, `corp_addr_kana_2`, `corp_addr_knj_pref`, `corp_addr_knj_1`, `corp_addr_knj_2`, `rep10e_sex`, `rep10e_date_of_birth`, `rep10e_name_kana_sei`, `rep10e_name_kana_mei`, `rep10e_name_knj_sei`, `rep10e_name_knj_mei`, `rep10e_addr_zip_code`, `rep10e_addr_kana_pref`, `rep10e_addr_kana_1`, `rep10e_addr_kana_2`, `rep10e_addr_knj_pref`, `rep10e_addr_knj_1`, `rep10e_addr_knj_2`, `rep10e_addr_tel1`, `rep10e_addr_tel2`, `contact_name_kana_sei`, `contact_name_kana_mei`, `contact_name_knj_sei`, `contact_name_knj_mei`, `contact_addr_zip_code`, `contact_addr_kana_pref`, `contact_addr_kana_1`, `contact_addr_kana_2`, `contact_addr_knj_pref`, `contact_addr_knj_1`, `contact_addr_knj_2`, `contact_addr_tel1`, `contact_addr_tel2`, `contact_email`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,'000000003045','ナギサショウカイ','宮原　環','8171532',NULL,NULL,NULL,'長崎県','対馬市','上県町伊奈',NULL,NULL,'ワタナベ','ナギサ','渡辺','渚','8710063',NULL,NULL,NULL,'大分県','中津市','袋町','0973-99-3913',NULL,'イズミ','ミフユ','和泉','美冬','8721102',NULL,NULL,NULL,'大分県','豊後高田市','大岩屋','0978-2-6819',NULL,'ichainbase.test02@gmail.com',1,'2021-08-10 18:09:24',NULL,NULL,NULL,NULL,NULL);

INSERT INTO `customers_individual` (`tenant_id`, `customer_id`, `name_kana_sei`, `name_kana_mei`, `name_knj_sei`, `name_knj_mei`, `sex`, `date_of_birth`, `addr_zip_code`, `addr_kana_pref`, `addr_kana_1`, `addr_kana_2`, `addr_knj_pref`, `addr_knj_1`, `addr_knj_2`, `addr_tel1`, `addr_tel2`, `company_name_kana`, `company_name_kanji`, `place_of_work_kana`, `place_of_work_kanji`, `place_of_work_code`, `group_column`, `email`, `occupation`, `occupation_code`, `enguarded_type`, `guardian_id`, `update_count`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES (1,'000000003042','ヤマムラ','コウイチ','安達','正利','1','1996-06-07','0294342',NULL,NULL,NULL,'岩手県','奥州市','衣川高保呂','019-978-4241','090-5664-6089','キンユウチョウ','金融庁','ホケンカ','保険課','0','0','ichainbase.test02@gmail.com','公務員',NULL,NULL,NULL,1,'2021-08-10 18:09:24',NULL,NULL,NULL,NULL,NULL);

INSERT INTO `products` (`tenant_id`, `product_code`) VALUES (1,'001'),(2,'001');

INSERT INTO `sales_products` (`tenant_id`, `product_code`, `sales_plan_code`, `sales_plan_type_code`, `start_date`, `end_date`, `sales_plan_name`, `sales_plan_name_display`, `issue_age_upper`, `issue_age_lower`, `active_inactive`, `special_requirement`, `sort_no`, `termination_date_pattern`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES ('1','001', 'B01', 'PT', '2021-01-01', '9999-12-31', 'ペット保険 ベーシックプラン', 'ペット保険 ベーシック', '00', '99', 'A', NULL, NULL, 'MO', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '001','P01', 'PT', '2021-01-01', '9999-12-31', 'ペット保険 プレミアムプラン', 'ペット保険 プレミアム', '00', '99', 'A', NULL, NULL, 'MO', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1','001', 'S01', 'HP', '2021-01-01', '9999-12-31', '家財保険 スタンダートプラン', '家財保険 スタンダート', '00', '99', 'A', NULL, NULL, 'MO', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('2','001', 'B01', 'PT', '2021-01-01', '9999-12-31', 'ペット保険 ベーシックプラン', 'ペット保険 ベーシック', '00', '99', 'A', NULL, NULL, 'MO', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `contracts` (`tenant_id`, `contract_no`, `contract_branch_no`, `contract_status`, `update_cnt`, `last_contract_id`, `new_contract_id`, `application_date`, `received_date`, `entry_date`, `inception_date`, `complete_date`, `first_premium_date`, `effective_date`, `issue_date`, `expiration_date`, `termination_date`, `free_lock_date`, `insurance_start_date`, `insurance_end_date`, `premium_start_date`, `premium_end_date`, `number_of_insured`, `coverage_term`, `card_cust_number`, `card_unavailable_flag`, `frequency`, `payment_method`, `product`, `sales_plan_code`, `sales_plan_type_code`, `basic_policy_code`, `hii_other_insurance`, `contractor_customer_id`, `total_premium`, `insured_customer_id`, `relationship`, `premium`, `sales_method`, `reinsurance_comp_code`, `research_comp_code`, `suspend_status`, `agency_code_1`, `agent_code_1`, `agent_share_1`, `agency_code_2`, `agent_code_2`, `agent_share_2`, `mypage_link_date`, `payment_pattern`, `payment_date_order`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES ('1', '0000000201', '01', '62', 1, NULL, NULL, '2021-07-10', '2021-07-10', '2021-07-10', '2021-07-10', '2021-08-01', '2021-07-14', '2021-08-01', '2021-08-01', '2022-07-31', '2022-07-31', NULL, '2021-08-01', '2022-07-31', '2021-07-14', '2022-07-31', 1, 1, '1230000123', NULL, '12', '3', NULL, 'B01', 'PT',  NULL, '0', '000000003042', '0', '000000003045', '04', 0, '02', NULL, NULL, NULL, '67', '80', 100, '0', '0', 0, NULL, NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000000201', '02', '40', 1, NULL, NULL, '2021-07-10', '2021-07-10', '2021-07-10', '2021-07-10', '2021-08-01', '2021-07-14', '2021-08-01', '2022-08-01', '2023-07-31', NULL, NULL, '2022-08-01', '2023-07-31', '2022-08-01', '2023-07-31', 1, 1, '1230000123', NULL, '12', '3', NULL, 'B01', 'PT', NULL, '0', '000000003045', '0', '000000003042', '04', 0, '02', NULL, NULL, NULL, '67', '80', 100, '0', '0', 0, NULL, NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000000202', '01', NULL, NULL, NULL, NULL, '2021-07-10', '2021-07-10', '2021-07-10', '2021-07-10', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, '1230000123', NULL, '12', '3', NULL, 'P01', 'PT', NULL, '0', '000000003042', NULL, '000000003045', '04', NULL, '02', NULL, NULL, NULL, '67', '80', 100, '0', '0', 0, NULL, NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000000203', '01', '40', 1, NULL, NULL, '2021-07-10', '2021-07-10', '2021-07-10', '2021-07-10', '2021-08-01', '2021-07-14', '2021-08-01', '2022-08-01', '2023-07-31', NULL, NULL, '2022-08-01', '2023-07-31', '2021-07-14', '2023-07-31', 1, 1, '1230000123', NULL, '12', '3', NULL, 'S01', 'HP', NULL, '0', '000000003042', '0', '000000003045', '04', 0, '02', NULL, NULL, NULL, '67', '80', 100, '0', '0', 0, NULL, NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('2', '0000000204', '01', '40', 1, NULL, NULL, '2021-07-10', '2021-07-10', '2021-07-10', '2021-07-10', '2021-08-01', '2021-07-14', '2021-08-01', '2022-08-01', '2023-07-31', NULL, NULL, '2022-08-01', '2023-07-31', '2021-07-14', '2023-07-31', 1, 1, '1230000123', NULL, '12', '3', NULL, 'B01', 'PT',  NULL, '0', '000000003042', '0', '000000003045', '04', 0, '02', NULL, NULL, NULL, '67', '80', 100, '0', '0', 0, NULL, NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('2', '0000000201', '02', '40', 1, NULL, NULL, '2021-07-10', '2021-07-10', '2021-07-10', '2021-07-10', '2021-08-01', '2021-07-14', '2021-08-01', '2022-08-01', '2023-07-31', NULL, NULL, '2022-08-01', '2023-07-31', '2022-08-01', '2023-07-31', 1, 1, '1230000123', NULL, '12', '3', NULL, 'B01', 'PT', NULL, '0', '000000003045', '0', '000000003042', '04', 0, '02', NULL, NULL, NULL, '67', '80', 100, '0', '0', 0, NULL, NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `maintenance_requests` (`tenant_id`, `request_no`, `contract_no`, `contract_branch_no`, `active_inactive`, `transaction_code`, `request_status`, `application_date`, `application_time`, `application_method`, `received_date`, `received_at`, `comment_underweiter1`, `first_assessment_results`, `comment_underweiter2`, `second_assessment_results`, `communication_column`, `apply_date`, `entry_type`, `payment_method_code`, `factoring_company_code`, `bank_code`, `bank_branch_code`, `bank_account_type`, `bank_account_no`, `bank_account_name`, `token_no`, `email_for_notification`, `termination_base_date`, `termination_title`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES ('1', '0000002211', '0000000201', '01', 'A', '41', 'U', '2021-08-09', NULL, 'D', '2021-08-10', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'O', '4', NULL, '0131', '103', '1', '1020304', 'アダチマサトシ', NULL, 'ichainbase.test02@gmail.com', NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000002212', '0000000201', '02', 'A', '41', '2', '2022-08-09', NULL, 'D', '2022-08-10', NULL, NULL, NULL, NULL, NULL, NULL, NULL,'O', '4', NULL, '0131', '103', '1', '1020304', 'アダチマサトシ', NULL, 'ichainbase.test02@gmail.com', NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000002213', '0000000201', '02', 'A', '41', '0', '2022-08-09', NULL, 'D', '2022-08-09', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'O', '3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ichainbase.test02@gmail.com', NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000002214', '0000000203', '01', 'A', '41', '0', '2022-08-09', NULL, 'D', '2022-08-10', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'O', '3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ichainbase.test02@gmail.com', NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('2', '0000002215', '0000000204', '01', 'A', '41', '0', '2022-08-09', NULL, 'D', '2022-08-10', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'O', '4', NULL, '0131', '103', '1', '1020304', 'アダチマサトシ', NULL, 'ichainbase.test02@gmail.com', NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('2', '0000002213', '0000000201', '02', 'A', '41', '0', '2022-08-09', NULL, 'D', '2022-08-09', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'O', '3', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ichainbase.test02@gmail.com', NULL, NULL, '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `maintenance_documents` (`tenant_id`, `request_no`, `sequence_no`, `document_title`, `document_url`, `upload_date`, `created_at`, `created_by`, `updated_at`, `updated_by`, `deleted_at`, `deleted_by`) VALUES ('1', '0000002213', 1, 'file1.jpg', '(URL1)', '2022-08-09', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000002213', 2, 'file2.xlsx', '(URL2)', '2022-08-09', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('1', '0000002213', 3, 'file3.pdf', '(URL3)', '2022-08-09', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL),('2', '0000002213', 1, 'file4.jpg', '(URL1)', '2022-08-09', '2021-08-02 00:00:00', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `premium_headers` VALUES (1,1,'0000000201','02','2022-08-01','0',19,'202208','2022-08-01',1200,'M','12',NULL,NULL,1,'2021-10-28 16:43:39',NULL,NULL,NULL,NULL,NULL),(2,1,'0000000201','02','2022-09-01','0',20,'202209','2022-09-01',1200,'M','12',NULL,NULL,1,'2021-10-28 16:45:59',NULL,NULL,NULL,NULL,NULL),(3,1,'0000000201','02','2022-10-01','0',21,'202210',NULL,1200,'P','12',NULL,NULL,1,'2021-10-28 16:45:59',NULL,NULL,NULL,NULL,NULL),(4,1,'0000000203','01','2022-08-01','0',10,'202208',NULL,1400,'P','12',NULL,NULL,1,'2021-10-28 16:45:59',NULL,NULL,NULL,NULL,NULL),(5,1,'0000000203','01','2022-09-01','0',11,'202209',NULL,1400,'P','12',NULL,NULL,1,'2021-10-28 16:45:59',NULL,NULL,NULL,NULL,NULL);