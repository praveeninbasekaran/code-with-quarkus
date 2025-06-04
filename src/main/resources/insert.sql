INSERT INTO drm_sit.rcsa_rule_management (
    rule_id, alert_generation_status, rule_name, rule_description, 
    risk_level1, risk_level2, risk_level3, country, legal_entity, 
    business_function_l1, business_function_l2, business_function_l3, 
    process_id, rule_entitlement_stage_1, rule_entitlement_stage_2, rule_entitlement_stage_3,
    category_of_notification, expected_risk_response_stage_1, expected_risk_response_stage_2, expected_risk_response_stage_3,
    stage_due_in_days, due_in_days, email_template_alert_content, comments, 
    created_by, created_date, updated_by, updated_date, 
    rule_status, type_of_alert, execute_now_flag, rule_version, risk_id
) VALUES 
(10001, 'Active', 'Rule A', 'Dummy rule description A', 'AML', 'AML_Sub', 'AML_Sub_3', 'India', 'SCB India', 'Retail', 'CIB', 'Private', 'PROC-001', 'BRM', 'PO', 'RFD', 'Ratings', 'Accept', 'Acknowledge', 'Approve', 10, 10, 'Template 1', 'No comment', 'user1', CURRENT_TIMESTAMP, 'user1', CURRENT_TIMESTAMP, 'Approved', 'Rating', 'Y', 1, 101),
(10002, 'Inactive', 'Rule B', 'Dummy rule description B', 'Compliance', 'Compliance_Sub', 'Sub3', 'China', 'SCB China', 'Ops', 'Risk', 'Audit', 'PROC-002', 'BRM', 'PO', 'RFD', 'Indicator', 'Overlay', 'Refer Back', 'Refer Back', 15, 20, 'Template 2', 'To be reviewed', 'user2', CURRENT_TIMESTAMP, 'user2', CURRENT_TIMESTAMP, 'Draft', 'Indicator', 'N', 1, 102),
(10003, 'Active', 'Rule C', 'Dummy rule description C', 'Sanctions', 'Sanctions_L2', 'Sanctions_L3', 'UK', 'SCB UK', 'Legal', 'Compliance', 'Audit', 'PROC-003', 'PO', 'BRM', 'None', 'Adhoc RCSA', 'Accept', 'Approve', NULL, 7, 5, 'Template 3', NULL, 'user3', CURRENT_TIMESTAMP, 'user3', CURRENT_TIMESTAMP, 'Pending Approval', 'Adhoc RCSA', 'N', 1, 103),
(10004, 'Inactive', 'Rule D', 'Dummy rule description D', 'Fraud', 'Level2', 'Level3', 'Singapore', 'SCB SG', 'CIB', 'Retail', 'Legal', 'PROC-004', 'RFD', 'BRM', 'PO', 'Ratings', 'Acknowledge', 'Approve', 'Refer Back', 5, 10, 'Template 4', 'High priority', 'user4', CURRENT_TIMESTAMP, 'user4', CURRENT_TIMESTAMP, 'Rejected', 'Rating', 'Y', 1, 104),
(10005, 'Active', 'Rule E', 'Dummy rule description E', 'Credit', 'Credit_L2', 'Credit_L3', 'USA', 'SCB US', 'Audit', 'Risk', 'Retail', 'PROC-005', 'BRM', 'None', 'None', 'Ratings', 'Overlay', NULL, NULL, 20, 30, 'Template 5', '', 'user5', CURRENT_TIMESTAMP, 'user5', CURRENT_TIMESTAMP, 'Approved', 'Rating', 'Y', 1, 105);


INSERT INTO drm_sit.rcsa_alert_management (
    alert_id, rule_id, alert_date, type_of_alert, rule_name, rule_criteria_id, 
    process_name, risk_name, inherent_risk, control_effectiveness, previous_risk_rating, 
    calculated_risk_rating, latest_risk_rating, assigned_role, assigned_to, 
    alert_status, due_date, created_date, updated_date, updated_by, 
    last_action_taken, last_action_date, latest_reminder_date, escalation_date
) VALUES
(20001, 10001, CURRENT_TIMESTAMP, 'Rating', 'Rule A', 3001, 'Trade | Pre-processing', 'Erroneous Transaction', '4C', 'Strong', 'Low', 'Medium', 'Medium', 'BRM', 'John Doe (123456)', 'Open', CURRENT_TIMESTAMP + INTERVAL '10 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user1', 'Created', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20002, 10002, CURRENT_TIMESTAMP, 'Indicator', 'Rule B', 3002, 'Ops | Reconciliations', 'Threshold Breach', '3C', 'Moderate', 'Medium', 'High', 'High', 'PO', 'Jane Doe (654321)', 'In Progress', CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user2', 'Escalated', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20003, 10003, CURRENT_TIMESTAMP, 'Adhoc RCSA', 'Rule C', 3003, 'Risk | Evaluation', 'Data Tampering', '5C', 'Weak', 'Medium', 'Low', 'Medium', 'RFD', 'Ali Khan (112233)', 'Overdue', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user3', 'Reminder Sent', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20004, 10004, CURRENT_TIMESTAMP, 'Rating', 'Rule D', 3004, 'Compliance | Checks', 'Unauthorized Access', '2C', 'Strong', 'Low', 'Low', 'Low', 'BRM', 'Anita Roy (998877)', 'Resolved', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user4', 'Resolved', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20005, 10005, CURRENT_TIMESTAMP, 'Rating', 'Rule E', 3005, 'Audit | Trail', 'Missing Logs', '3B', 'Moderate', 'Low', 'Medium', 'Medium', 'PO', 'Tom Li (776655)', 'Closed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'user5', 'Closed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);