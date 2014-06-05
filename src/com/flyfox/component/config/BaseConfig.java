package com.flyfox.component.config;

import static conf.Config.getDbParam;

import com.flyfox.component.model.AutoBindModels;
import com.flyfox.component.route.AutoBindRoutes;
import com.flyfox.modules.column.ColumnCache;
import com.flyfox.modules.dict.DictCache;
import com.flyfox.modules.user.UserCache;
import com.flyfox.modules.user.UserInterceptor;
import com.flyfox.util.StrUtils;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.log.Log4jLoggerFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;

/**
 * API引导式配置
 */
public class BaseConfig extends JFinalConfig {

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		me.setDevMode(isDevMode());
		me.setViewType(ViewType.JSP); // 设置视图类型为Jsp，否则默认为FreeMarker
		me.setBaseViewPath("/pages");
		me.setLoggerFactory(new Log4jLoggerFactory());
		me.setError404View("/pages/error/404.jsp");
		me.setError500View("/pages/error/500.jsp");
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		// 自动绑定
		// 1.如果没用加入注解，必须以Controller结尾,自动截取前半部分为key
		// 2.加入ControllerBind的 获取 key
		me.add(new AutoBindRoutes());
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 配置C3p0数据库连接池插件
		C3p0Plugin c3p0Plugin = null;
		if (isDevMode()) {
			c3p0Plugin = new C3p0Plugin( //
					getDbParam("jdbcUrl"), getDbParam("user"), //
					getDbParam("password").trim(), getDbParam("driverClass"));
		} else {
			c3p0Plugin = new C3p0Plugin( //
					getDbParam("bae.jdbcUrl"), getDbParam("bae.user"), //
					getDbParam("bae.password").trim(), getDbParam("bae.driverClass"));
		}
		me.add(c3p0Plugin);
		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);
		if (isDevMode()) {
			arp.setShowSql(true);
			arp.setDialect(new PostgreSqlDialect());
		}
		new AutoBindModels(arp);
		//arp.addMapping("sys_dict", "dict_id", SysDict.class);
		//arp.addMapping("sys_dict_detail", "detail_id", SysDictDetail.class);
		//arp.addMapping("sys_user", "userid", SysUser.class);
		//arp.addMapping("tb_column", "id", TbColumn.class);
		//arp.addMapping("tb_project", "id", TbProject.class);
		//arp.addMapping("tb_money", "id", TbMoney.class);
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		// 用户认证
		me.add(new UserInterceptor());
		// session model转换
		me.add(new SessionInViewInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		// 根目录获取
		me.add(new ContextPathHandler("basePath"));
	}

	/**
	 * 初始化
	 */
	@Override
	public void afterJFinalStart() {
		reset();
	}

	
	public static void reset() {
		DictCache.init();
		UserCache.init();
		ColumnCache.init();
	}

	@Override
	public void beforeJFinalStop() {
		super.beforeJFinalStop();
	}

	private boolean isDevMode() {
		String osName = System.getProperty("os.name"); // 电脑系统名称
		boolean flag = StrUtils.isNotEmpty(osName) && osName.indexOf("Windows") >= 0;
		return flag;
	}
	
	/**
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("WebContent", 80, "/", 5);
	}
}
