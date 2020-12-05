package com.website.service.service.impl;

import com.website.service.constant.Constants;
import com.website.service.entity.MailRecord;
import com.website.service.entity.MailSenderModal;
import com.website.service.mapper.MailRecordMapper;
import com.website.service.service.MailService;
import com.website.service.utils.DateTimeUtil;
import com.website.service.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 *
 * 邮件发送接口实现
 *
 * @author Louis
 *
 */
@Service
public class MailServiceImpl implements MailService {

	/**
	 * 注入MailSender
	 */
	@Resource
	private JavaMailSender mailSender;
	@Resource
	private MailRecordMapper mailRecordMapper;
	@Resource
	private IdGenerator idGenerator;

	/**
	 * 读取配置文件中的发送用户信息
	 */
	@Value("${spring.mail.username}")
	private String fromEmail;
	@Value("${spring.mail.nickname}")
	private String nickName;

	/**
	 * 文本类型邮件发送业务实现
	 */
	@Async
	@Override
	public MailSenderModal sendSimple(String userId, String toEmail, String subject, String body) {
		MailSenderModal mailSenderModal = new MailSenderModal();
		try {
			MimeMessage message = mailSender.createMimeMessage();
			InternetAddress internetAddress = new InternetAddress();
			internetAddress.setAddress(fromEmail);
			try {
				internetAddress.setPersonal(nickName,"UTF-8");
			}catch (UnsupportedEncodingException e){
				e.printStackTrace();
			}
			MimeMessageHelper helper = new MimeMessageHelper(message, false);
			helper.setFrom(internetAddress);

			/**
			 * 收件人邮件地址
			 */
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body);

			mailSender.send(message);
		}catch (MessagingException e){
			e.printStackTrace();
			mailSenderModal.setCode(-2);
			mailSenderModal.setMsg(e.getMessage());
		}catch (MailException e) {
			e.printStackTrace();
			mailSenderModal.setCode(-1);
			mailSenderModal.setMsg(e.getMessage());
		}

		MailRecord mailRecord = new MailRecord();
		mailRecord.setId(idGenerator.nextIdStr());
		mailRecord.setToUserId(userId);
		mailRecord.setFromMail(fromEmail);
		mailRecord.setToMail(toEmail);
		mailRecord.setMailSubject(subject);
		mailRecord.setMailContent(body);
		mailRecord.setCreateTime(DateTimeUtil.timeNow());

		if(mailSenderModal.getCode() == 0){
			mailRecord.setSendSts(Constants.YES_OR_NO_1);
		}else{
			mailRecord.setSendSts(Constants.YES_OR_NO_0);
		}
		mailRecordMapper.insertSelective(mailRecord);

		return mailSenderModal;
	}

	/**
	 * Mime类型邮件发送业务实现
	 */
	@Async
	@Override
	public MailSenderModal sendMime(String userId,String toEmail, String subject,String mailBody,
									Map<String, String> photos, boolean isAttachment) {
		MailSenderModal mailSenderModal = new MailSenderModal();
		try {
			MimeMessage message = mailSender.createMimeMessage();

			InternetAddress internetAddress = new InternetAddress();
			internetAddress.setAddress(fromEmail);
			try {
				internetAddress.setPersonal(nickName,"UTF-8");
			}catch (UnsupportedEncodingException e){
				e.printStackTrace();
			}

			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(internetAddress);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(mailBody, true);

			/**
			 * 判断附件是否为空
			 */
			if (!StringUtils.isEmpty(photos)) {
				/**
				 * 多附件处理
				 */
				photos.entrySet().forEach(entry -> {
					try {
						FileSystemResource file = new FileSystemResource(new File(entry.getValue()));

						if (isAttachment) {
							helper.addAttachment(entry.getKey(), file);
						} else {
							helper.addInline(entry.getKey(), file);
						}
					} catch (MessagingException e) {
						e.printStackTrace();
						mailSenderModal.setCode(-2);
						mailSenderModal.setMsg(e.getMessage());
					}

				});

			}

			mailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			mailSenderModal.setCode(-2);
			mailSenderModal.setMsg(e.getMessage());
		} catch (MailException e) {
			e.printStackTrace();
			mailSenderModal.setCode(-1);
			mailSenderModal.setMsg(e.getMessage());
		}

		MailRecord mailRecord = new MailRecord();
		mailRecord.setId(idGenerator.nextIdStr());
		mailRecord.setToUserId(userId);
		mailRecord.setFromMail(fromEmail);
		mailRecord.setToMail(toEmail);
		mailRecord.setMailSubject(subject);
		mailRecord.setMailContent(mailBody);
		mailRecord.setCreateTime(DateTimeUtil.timeNow());

		if(mailSenderModal.getCode() == 0){
			mailRecord.setSendSts(Constants.YES_OR_NO_1);
		}else{
			mailRecord.setSendSts(Constants.YES_OR_NO_0);
		}
		mailRecordMapper.insertSelective(mailRecord);

		return mailSenderModal;
	}

}
