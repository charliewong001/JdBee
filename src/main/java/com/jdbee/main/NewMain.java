package com.jdbee.main;

import com.jdbee.crawler.JDGoodsList;
import com.jdbee.crawler.JdCategory;
import com.jdbee.crawler.RetailersCrawler;
import com.jdbee.model.Category;
import com.jdbee.model.SecondCategory;
import com.jdbee.model.ThreeCategory;
import com.jdbee.utils.Constants;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * @ClassName: NewMain
 * @Description: 程序入口
 * @author handx 908716835@qq.com
 * @date 2017年6月1日 下午9:47:17
 */
public class NewMain extends RetailersCrawler {

	public static final Logger log = Logger.getLogger(NewMain.class);

	/**
	 * @Title: getCategorySnacksUrlList 
	 * @Description: 获取零食url地址
	 * @param @return 
	 * @return List<String>
	 */
	private static List<String> getCategorySnacksUrlList() {
		List<String> urls = new ArrayList<String>();

		// 获取类目列表
		List<Category> list = JdCategory.getCategory();

		for (Category category : list) {
			if ("食品饮料、保健食品".equals(category.getName())) {
				List<SecondCategory> senondCates = category.getSenondCates();
				for (SecondCategory secondCategory : senondCates) {
					List<ThreeCategory> threeCates = secondCategory.getThreeCates();
					for (final ThreeCategory threeCategory : threeCates) {
						urls.add(threeCategory.getUrl());
					}
				}
			}
		}
		return urls;
	}

	public static void main(String[] args) throws Exception {

		long startTime = System.currentTimeMillis();
		List<String> urls = getCategorySnacksUrlList();
		
		for (String url : urls) {
			NewMain crawler = new NewMain("data", url + Constants.JD_PAGING_PARAMETER);
			crawler.setThreads(5);// 抓取启动线程数
			crawler.start(1);// 层数
		}

		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间： " + (endTime - startTime) + "ms");

	}

	private JDGoodsList goodsList;

	public NewMain(String crawlPath, String seekFormat) {
		super(crawlPath, seekFormat);
		goodsList = new JDGoodsList();
	}

	@Override
	public int getTotalPage(Page page) {
		Element ele = page.getDoc().select("div#J_bottomPage").select("span.p-skip>em").first().select("b").first();
		return ele == null ? 0 : Integer.parseInt(ele.text());
	}

	@Override
	public void visit(Page page, Links links) {
		goodsList.addGoods(page);
	}

}
