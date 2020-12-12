package SearchJob;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class searchJob {

	public static void main(String[] args) throws Exception {		
		
		int hrefIdx = 0, rank = 1;
		int[] presentTimeArr;
		String userCategory;
		String cate_num;
		Document htmlDoc;
		Elements top20, top20_link;
		
		Iterator<Element> iter;
		List<String> href = new ArrayList<>();
		Set<String> keys;
		Category<String,String> category = new Category<>();
		Calendar calendar = Calendar.getInstance();
		Site site = new Site();
		Scanner scanner = new Scanner(System.in);
		
		
		
		//program start!
		System.out.println("원하는 사이트를 선택하세요");
		
		while(true) {
			System.out.println("프로그래머스:1\t잡코리아:2");
			try {
				int userSite = scanner.nextInt();
				
				if(userSite==1) {
					site = new Programers();
					category = new pCategory();
				}else if(userSite==2) {
					site = new JobKorea();
					category = new jkCotegory();
				}else {
					throw new Exception();
				}
				break;
			}catch (InputMismatchException e) {
				System.out.println("잘못 입력했습니다. 다시 입력해주세요\n");
				scanner = new Scanner(System.in);
			}catch(Exception e1) {
				System.out.println("잘못 입력했습니다. 다시 입력해주세요\n");
				scanner = new Scanner(System.in);
			}
		}
		
		while(true) {
			System.out.println("직무를 입력하세요");
		
			//print all key (category)
			System.out.print("[ ");
			keys = category.getAllCate();
			keys.forEach(key->System.out.print(key+" "));
			System.out.println("]");
         
        	//input user's category
        	userCategory = scanner.next();
			cate_num = category.getCateNum(userCategory);
			if(cate_num==null) {
				System.out.println("직무 리스트에 존재하지 않습니다.다시 입력해주세요\n");
			}else break;
        }
		
		
		//get jobkorea's html source
		htmlDoc = site.getdoc(cate_num);
		
		//get top 20
		top20 = htmlDoc.select(site.getTop20_Tag());	
		iter = top20.iterator();
		
		//get current time
		GetTime getPresentTime = ()->{return new int[]{calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE)};};
		presentTimeArr = getPresentTime.getTime();
		
		//print top 20
		System.out.println();
		System.out.println("[ " + userCategory + "직군 " + presentTimeArr[0] + "월 " + presentTimeArr[1] + "일 "+ presentTimeArr[2] + "시 " + presentTimeArr[3] + "분 "+site.getSiteName()+" 채용소식 ]");
		System.out.println("=======================================");
				
		while(iter.hasNext()) {
			System.out.println(rank + ") " + iter.next().text());
			rank++;
		}
		System.out.println("=======================================");
		System.out.println();
		
		
		//get top 20's link
		top20_link = htmlDoc.select(site.getTop20_hrefTag());
		iter = top20_link.iterator();

		//set href array to top 20's link
		while(iter.hasNext()) {
			String hrefLink = iter.next().attr("abs:href"); 
			href.add(hrefIdx, hrefLink);
			hrefIdx++;
		}
		
		while(true) {
			System.out.println("원하는 채용소식 번호를 입력하세요. (종료: \"exit\")");		
		
			String inputRankStr = "";
			int inputRank = 0;
			try {
				//input user's link
				inputRankStr  = scanner.next();				
				
				//if input == "exit"
				if(inputRankStr.equals("exit")||inputRankStr.equals("EXIT")) {System.out.println("프로그램을 종료합니다."); return;}
					
				inputRank = Integer.parseInt(inputRankStr);
				if(inputRank<0 || inputRank>rank)throw new Exception();
				
				//open link in browser
				Desktop.getDesktop().browse(new URI(href.get(inputRank -1)));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}catch(InputMismatchException e) {
				System.out.println("잘못 입력했습니다. 다시 입력해주세요");
				scanner = new Scanner(System.in);
			}catch(Exception e) {
				System.out.println("잘못 입력했습니다. 다시 입력해주세요");
				scanner = new Scanner(System.in);
			}
			System.out.println();
		}
		
	}
}


class Category<K,V>{
	protected Set<K> getAllCate(){setHash(); return HASHMAP.keySet();}
	protected V getCateNum(K category) {setHash(); return HASHMAP.get(category);}
	private void setHash() {for(int i = 0; i<values.size(); i++) HASHMAP.put(keys.get(i), values.get(i));}

	protected final HashMap<K, V> HASHMAP = new HashMap<>();
	protected List<K> keys = new ArrayList<>();
	protected List<V> values = new ArrayList<>();
}

class jkCotegory extends Category<String,String>{
	public jkCotegory() {
		this.values = Arrays.asList("10012","10013","10014","10015","10016","10017","10018","10019","10020","10021","10022","10023","10024","10025");
		this.keys = Arrays.asList("경영","마케팅","무역","영업","it","생산","연구개발","디자인","미디어","건설","서비스","교육","의료","특수직");
	}
}

class pCategory extends Category<String,String>{
	public pCategory() {
		this.values = Arrays.asList("1","2","3","4","5","7","9","11","12","16","17","18","20","22","25");
		this.keys = Arrays.asList("서버","안드로이드앱","아이폰앱","프론트엔드","머신러닝","모바일게임","시스템/네트워크","인공지능","데이터엔지니어","게임클라이언트","응용프로그램","시스템소프트웨어","게임서버","인터넷보안","웹풀스택");
	}
}


class Site implements MakeURL{
	protected String getTop20_Tag() {return top20_tag;}
	protected String getTop20_hrefTag() {return top20_hrefTag;}
	protected String getSiteName() {return sitename; }	
	
	protected Document getdoc(String cate_num) {
		//web crawling
		String url = getURL(cate_num);
				
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	@Override
	public String getURL(String cate_num) {return "";}
	
	protected String url;
	protected String top20_tag;
	protected String top20_hrefTag;
	protected String sitename;
}

class JobKorea extends Site{
	public JobKorea() {
		this.url = "http://www.jobkorea.co.kr/Top100/?Main_Career_Type=1&Search_Type=1&BizJobtype_Bctgr_Code=";
		this.top20_tag = "a.coLink>b";
		this.top20_hrefTag = "div.tit>a[href]";
		this.sitename = "잡코리아";
	}	
	
	@Override
	public String getURL(String cate_num) {	return url + cate_num;	}
}

class Programers extends Site{
	public Programers() {
		this.url = "https://programmers.co.kr/job?job_position%5Bjob_category_ids%5D%5B%5D=";
		this.top20_tag = "h6.company-name";
		this.top20_hrefTag = "h5.position-title>a[href]";
		this.sitename = "프로그래머스";
	}	
	
	@Override
	public String getURL(String cate_num) {	return url + cate_num + "&amp;job_position%5Bdummy%5D=0&amp;job_position%5Bmin_career%5D=0&amp;order=recent";}
}


@FunctionalInterface
interface MakeURL {public abstract String getURL(String cate_num);}

@FunctionalInterface
interface GetTime{public int[] getTime();}
