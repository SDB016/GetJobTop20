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
		
		System.out.println("1: ���α׷��ӽ�      2: ���ڸ���");
		int userSite = scanner.nextInt();
		if(userSite==1) {
			site = new programers();
			category = new pCategory();
		}else if(userSite==2) {
			site = new JobKorea();
			category = new jkCotegory();
		}
		
		System.out.println("������ �Է��ϼ���");
		
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
			System.out.println("���� ����Ʈ�� �������� �ʽ��ϴ�.");
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
		System.out.println("[ " + userCategory + "���� " + month + "�� " + day + "�� "+ site.getSiteName()+" ä��ҽ� ]");
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
		
		System.out.println("���ϴ� ä��ҽ� ��ȣ�� �Է��ϼ���");
		
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
			put("�濵","10012");
			put("������","10013");
			put("it","10016");
			put("������","10019");
			put("����","10014");
			put("����","10015");
			put("��������","10018");
			put("����","10022");
			put("����","10017");
			put("����","10023");
			put("�Ǽ�","10021");
			put("�Ƿ�","10024");
			put("�̵��","10020");
			put("Ư����","10025");
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
			put("����","1");
			put("����Ʈ����","4");
			put("��Ǯ����","25");
			put("�ȵ���̵��","2");
			put("��������","3");
			put("�ӽŷ���","5");
			put("�ΰ�����","11");
			put("�����Ϳ����Ͼ�","12");
			put("����ϰ���","7");
			put("����Ŭ���̾�Ʈ","16");
			put("���Ӽ���","20");
			put("�ý���/��Ʈ��ũ","9");
			put("�ý��ۼ���Ʈ����","18");
			put("���ͳݺ���","22");
			put("�������α׷�","17");

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
	
	private String sitename = "���ڸ���";
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
	
	private String sitename = "���α׷��ӽ�";
	private String url = "https://programmers.co.kr/job?job_position%5Bjob_category_ids%5D%5B%5D=";
	private String top20_tag = "h6.company-name";
	private String top20_hrefTag = "h5.position-title>a[href]";
}

@FunctionalInterface
interface makeURL{public abstract String getURL(String cate_num);}

@FunctionalInterface
interface parsingDoc{public abstract Document getdoc(String cate_num);}
