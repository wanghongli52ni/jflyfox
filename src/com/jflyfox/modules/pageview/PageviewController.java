package com.jflyfox.modules.pageview;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jflyfox.jfinal.base.BaseController;
import com.jflyfox.util.DateUtils;
import com.jflyfox.util.StrUtils;

/**
 * 用户PV统计管理
 * 
 * @author flyfox 2014-2-11
 */
public class PageviewController extends BaseController {

	private static final String path = "/pages/pageview/";

	public void index() {
		String start_day = getPara("start_day");
		String end_day = getPara("end_day");
		if (StrUtils.isEmpty(end_day)) {
			start_day = DateUtils.getNow("yyyy-MM") + "-01";
		}
		if (StrUtils.isEmpty(end_day)) {
			end_day = DateUtils.getNow("yyyy-MM") + "-31";
		}
		String sql = "select count(*) as cnt,create_day from tb_pageview  where " //
				+ " create_day >= ? and create_day <= ?" //
				+ " GROUP BY create_day";
		List<Record> list = Db.find(sql, start_day, end_day);
		String cnt = "", createDay = "";
		for (Record record : list) {
			cnt += record.get("cnt") + ",";
			createDay += "'"+record.get("create_day") + "',";
		}
		if (cnt.length() > 1) {
			cnt = cnt.substring(0, cnt.length() - 1);
			createDay = createDay.substring(0, createDay.length() - 1);
		}
		
		setAttr("start_day", start_day);
		setAttr("end_day", end_day);
		setAttr("cnt", cnt);
		setAttr("createDay", createDay);

		render(path + "index.html");
	}
}
