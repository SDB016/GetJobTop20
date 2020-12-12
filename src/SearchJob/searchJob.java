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
		System.out.println("���ϴ� ����Ʈ�� �����ϼ���");
		
		while(true) {
			System.out.println("���α׷��ӽ�:1\t���ڸ���:2");
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
				System.out.println("�߸� �Է��߽��ϴ�. �ٽ� �Է����ּ���\n");
				scanner = new Scanner(System.in);
			}catch(Exception e1) {
				System.out.println("�߸� �Է��߽��ϴ�. �ٽ� �Է����ּ���\n");
				scanner = new Scanner(System.in);
			}
		}
		
		while(true) {
			System.out.println("������ �Է��ϼ���");
		
			//print all key (category)
			System.out.print("[ ");
			keys = category.getAllCate();
			keys.forEach(key->System.out.print(key+" "));
			System.out.println("]");
         
        	//input user's category
        	userCategory = scanner.next();
			cate_num = category.getCateNum(userCategory);
			if(cate_num==null) {
				System.out.println("���� ����Ʈ�� �������� �ʽ��ϴ�.�ٽ� �Է����ּ���\n");
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
		System.out.println("[ " + userCategory + "���� " + presentTimeArr[0] + "�� " + presentTimeArr[1] + "�� "+ presentTimeArr[2] + "�� " + presentTimeArr[3] + "�� "+site.getSiteName()+" ä��ҽ� ]");
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
			System.out.println("���ϴ� ä��ҽ� ��ȣ�� �Է��ϼ���. (����: \"exit\")");		
		
			String inputRankStr = "";
			int inputRank = 0;
			try {
				//input user's link
				inputRankStr  = scanner.next();				
				
				//if input == "exit"
				if(inputRankStr.equals("exit")||inputRankStr.equals("EXIT")) {System.out.println("���α׷��� �����մϴ�."); return;}
					
				inputRank = Integer.parseInt(inputRankStr);
				if(inputRank<0 || inputRank>rank)throw new Exception();
				
				//open link in browser
				Desktop.getDesktop().browse(new URI(href.get(inputRank -1)));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}catch(InputMismatchException e) {
				System.out.println("�߸� �Է��߽��ϴ�. �ٽ� �Է����ּ���");
				scanner = new Scanner(System.in);
			}catch(Exception e) {
				System.out.println("�߸� �Է��߽��ϴ�. �ٽ� �Է����ּ���");
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
		this.keys = Arrays.asList("�濵","������","����","����","it","����","��������","������","�̵��","�Ǽ�","����","����","�Ƿ�","Ư����");
	}
}

class pCategory extends Category<String,String>{
	public pCategory() {
		this.values = Arrays.asList("1","2","3","4","5","7","9","11","12","16","17","18","20","22","25");
		this.keys = Arrays.asList("����","�ȵ���̵��","��������","����Ʈ����","�ӽŷ���","����ϰ���","�ý���/��Ʈ��ũ","�ΰ�����","�����Ϳ����Ͼ�","����Ŭ���̾�Ʈ","�������α׷�","�ý��ۼ���Ʈ����","���Ӽ���","���ͳݺ���","��Ǯ����");
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
		this.sitename = "���ڸ���";
	}	
	
	@Override
	public String getURL(String cate_num) {	return url + cate_num;	}
}

class Programers extends Site{
	public Programers() {
		this.url = "https://programmers.co.kr/job?job_position%5Bjob_category_ids%5D%5B%5D=";
		this.top20_tag = "h6.company-name";
		this.top20_hrefTag = "h5.position-title>a[href]";
		this.sitename = "���α׷��ӽ�";
	}	
	
	@Override
	public String getURL(String cate_num) {	return url + cate_num + "&amp;job_position%5Bdummy%5D=0&amp;job_position%5Bmin_career%5D=0&amp;order=recent";}
}


@FunctionalInterface
interface MakeURL {public abstract String getURL(String cate_num);}

@FunctionalInterface
interface GetTime{public int[] getTime();}
