package SearchJob;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;

public class main {

	public static void main(String[] args) {		
		
		String userCategory;
		Scanner scanner = new Scanner(System.in);
		int month, day;
		int hrefIdx = 0, rank = 1;
		List<String> href = new ArrayList<>();
		String cate_num = "";
		
		Site site = new Site();
		Category category = new Category();
		
		System.out.println("1: 프로그래머스      2: 잡코리아");
		int userSite = scanner.nextInt();
		if(userSite==1) {
			site = new programers();
			category = new pCategory();
		}else if(userSite==2) {
			site = new JobKorea();
			category = new jkCotegory();
		}
		
		System.out.println("직무를 입력하세요");
		
		//print all key (category)
		System.out.print("[ ");
		Set<String> keys = category.getAllCate();
		keys.forEach(key->System.out.print(key+" "));
        System.out.println("]");
        System.out.println();
         
        //input user's category
        userCategory = scanner.next();
		cate_num = category.getCateNum(userCategory);
		if(cate_num==null) {
			System.out.println("직무 리스트에 존재하지 않습니다.");
			return;
		}
		
		//get jobkorea's html source
		Document doc = site.getdoc(cate_num);
		
		//get top 20
		Elements top20 = doc.select(site.getTop20_Tag());	
		Iterator<Element> iter = top20.iterator();
		
		//get current time
		Calendar calendar = Calendar.getInstance();
		month = calendar.get(Calendar.MONTH)+1;
		day = calendar.get(Calendar.DATE);
		
		//print top 20
		System.out.println("[ " + userCategory + "직군 " + month + "월 " + day + "일 "+ site.getSiteName()+" 채용소식 ]");
		System.out.println("=======================================");
				
		while(iter.hasNext()) {
			System.out.println(rank + ") " + iter.next().text());
			rank++;
		}
		
		System.out.println("=======================================");
		System.out.println();
		
		
		//get top 20's link
		Elements top20_link = doc.select(site.getTop20_hrefTag());
		iter = top20_link.iterator();

		//set href array to top 20's link
		while(iter.hasNext()) {
			String hrefLink = iter.next().attr("abs:href"); 
			href.add(hrefIdx, hrefLink);
			hrefIdx++;
		}
		
		System.out.println("원하는 채용소식 번호를 입력하세요");
		
		//input user's link
		rank = 0;
		rank = scanner.nextInt();
		
		//open link in browser
		try {
			Desktop.getDesktop().browse(new URI(href.get(rank-1)));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}

class Category{
	protected Set<String> getAllCate(){return null;}
	protected String getCateNum(String category) {	return "";}
	
}

class jkCotegory extends Category{
	@Override
	public Set<String> getAllCate(){return HASHMAP.keySet();}
	@Override
	public String getCateNum(String category) {	return HASHMAP.get(category);}
	
	private final HashMap<String, String> HASHMAP = new HashMap<String,String>() {
		{
			put("경영","10012");
			put("마케팅","10013");
			put("it","10016");
			put("디자인","10019");
			put("무역","10014");
			put("영업","10015");
			put("연구개발","10018");
			put("서비스","10022");
			put("생산","10017");
			put("교육","10023");
			put("건설","10021");
			put("의료","10024");
			put("미디어","10020");
			put("특수직","10025");
		}
	};
}
class pCategory extends Category{
	@Override
	public Set<String> getAllCate(){return HASHMAP.keySet();}
	@Override
	public String getCateNum(String category) {	return HASHMAP.get(category);}
	
	private final HashMap<String, String> HASHMAP = new HashMap<String,String>() {
		{
			put("서버","1");
			put("프론트엔드","4");
			put("웹풀스택","25");
			put("안드로이드앱","2");
			put("아이폰앱","3");
			put("머신러닝","5");
			put("인공지능","11");
			put("데이터엔지니어","12");
			put("모바일게임","7");
			put("게임클라이언트","16");
			put("게임서버","20");
			put("시스템/네트워크","9");
			put("시스템소프트웨어","18");
			put("인터넷보안","22");
			put("응용프로그램","17");

		}
	};
}

class Site implements parsingDoc, makeURL{
	
	public String getURL(String cate_num) {return "";}
	protected String getTop20_Tag() {return top20_tag;}
	protected String getTop20_hrefTag() {return "";}
	protected String getSiteName() {return "";}
	
	protected String url;
	protected String top20_tag;
	protected String top20_hrefTag;
	
	@Override
	public Document getdoc(String cate_num) {
		
		//web croll
		url = getURL(cate_num);
				
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
}

class JobKorea extends Site implements parsingDoc{
	@Override
	public String getTop20_Tag() {return top20_tag;}
	@Override
	public String getTop20_hrefTag() {return top20_hrefTag;}
	@Override
	protected String getSiteName() {return sitename; }
	
	@Override
	public Document getdoc(String cate_num) {return super.getdoc(cate_num);}
	
	@Override
	public String getURL(String cate_num) {	return url + cate_num;	}
	
	private String sitename = "잡코리아";
	private String url = "http://www.jobkorea.co.kr/Top100/?Main_Career_Type=1&Search_Type=1&BizJobtype_Bctgr_Code=";
	private String top20_tag = "a.coLink>b";
	private String top20_hrefTag = "div.tit>a[href]";
}

class programers extends Site implements parsingDoc{
	@Override
	public String getTop20_Tag() {return top20_tag;}
	@Override
	public String getTop20_hrefTag() {return top20_hrefTag;}
	@Override
	protected String getSiteName() {return sitename; }
	
	@Override
	public Document getdoc(String cate_num) {return super.getdoc(cate_num);}
	
	@Override
	public String getURL(String cate_num) {	return url + cate_num + "&amp;job_position%5Bdummy%5D=0&amp;job_position%5Bmin_career%5D=0&amp;order=recent";	}
	
	private String sitename = "프로그래머스";
	private String url = "https://programmers.co.kr/job?job_position%5Bjob_category_ids%5D%5B%5D=";
	private String top20_tag = "h6.company-name";
	private String top20_hrefTag = "h5.position-title>a[href]";
}

@FunctionalInterface
interface makeURL{public abstract String getURL(String cate_num);}

@FunctionalInterface
interface parsingDoc{public abstract Document getdoc(String cate_num);}
