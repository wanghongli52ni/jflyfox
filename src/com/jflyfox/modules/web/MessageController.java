package com.jflyfox.modules.web;

import com.jflyfox.modules.CommonController;
import com.jflyfox.modules.comment.CommentService;
import com.jflyfox.modules.comment.TbComment;
import com.jflyfox.modules.web.service.WebService;
import com.jflyfox.system.user.SysUser;
import com.jfinal.plugin.activerecord.Page;
import com.jflyfox.jfinal.base.BaseController;
import com.jflyfox.jfinal.component.annotation.ControllerBind;
import com.jflyfox.jfinal.component.util.Attr;

/**
 * 我的消息
 * 
 * 2015年3月10日 下午5:38:24 flyfox 330627517@qq.com
 */
@ControllerBind(controllerKey = "/web_message")
public class MessageController extends BaseController {

	/**
	 * 我的消息
	 */
	public void index() {
		// 目录列表
		new WebService().showDirectory(this, "message");

		SysUser user = getSessionAttr(Attr.SESSION_NAME);
		if (user == null) {
			redirect(CommonController.firstPage);
			return;
		}

		String sql = " from tb_comment t " //
				+ " left join tb_article art on art.id = t.article_id " //
				+ " where t.create_id = ? or t.reply_userid = ? order by t.create_time desc ";
		Page<TbComment> pages = TbComment.dao.paginate(getPaginator(), //
				"select t.*,art.title,art.create_id as article_create_id " //
				, sql, user.getUserid(), user.getUserid());
		// 更新状态为已读
		new CommentService().updateCommentStatusRead(user.getUserid());

		setAttr("page", pages);
		renderAuto(Home.path + "show_message.html");
	}
}
