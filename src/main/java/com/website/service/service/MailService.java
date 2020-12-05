package com.website.service.service;

import com.website.service.entity.MailSenderModal;

import java.util.Map;

/**
 *
 * 邮件发送接口
 *
 * @author Louis
 *
 */
public interface MailService {

	/**
	 *
	 * 文本类型邮件发送业务接口
	 *
	 * @param toEmail 收件人
	 * @param subject 标题
	 * @param body 内容
	 * @return
	 */
	MailSenderModal sendSimple(
            String userId,
            String toEmail,
            String subject,
            String body);

	/**
	 *
	 * Mime类型邮件发送业务接口
	 *
	 * @param toEmail 收件人
	 * @param subject 标题
	 * @param body 内容
	 * @param photos 是否带有附件或静态文件
	 * @param isAttachment 是否为附件,false为资源，true为附件
	 * @return
	 */
	MailSenderModal sendMime(
            String userId,
            String toEmail,
            String subject,
            String mailBody,
            Map<String, String> photos,
            boolean isAttachment);
}
