package com.hanmanyi.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.HtmlPage;
/**
 * safmarine ��ѯ������Ϣ
 * @author:hanmanyi
 * @time:Aug 11, 2014 9:34:24 PM
 */
public class HtmlParser {

	private static int connectionTimeout = 5000;
    private static int soTimeout = 12000;
    private static String proxyHost = null;
    private static int proxyPort;
    private static String ENCODE = "UTF-8";
	public static int RETRY = 10;
   
	public static void main(String[] args) throws Exception {
		
		// ��ѯurl��ַ
		String url = "http://mysaf2.safmarine.com/wps/portal/Safmarine/etrackUnregistered?linktype=unreg&queryorigin=Header&queryoriginauto=HeaderNonSecure&searchType=Document&searchNumberString=";
		// ��ѯ����
		String num = "754315522";
		// �������ݱ�ǩ
		String tagName = "TABLE";
		// ��ǩ����
		String className = "results";
		Tag t = getTag(url,num,tagName,className);
		if(t != null){
			System.out.println(t.toHtml().toString());
			System.out.println("��ϲ����������һ�ξ͵õ��ˡ�");
			
		} else {
			int i = 0;
			while (t == null && i < RETRY){
				i ++;
				Thread.sleep(500);
				System.out.println("��" + i +"������");
				t = getTag(url,num,tagName,className);
			}
			if(t != null){
				System.out.println(t.toHtml().toString());
				System.out.println("��������"+i+"���ˣ����ڳɹ��ˡ�");
			}else{
				System.out.println("���λ�ȡʧ��");
			}
		}
	}
	
	 // ���ڷ��ʺ�������Ĳ��ȶ��ԣ�������÷�ֵ��ʧ�ܺ� ���ٴη��ʡ�
	public static Tag getTag(String url, String num,String tagName, String className ) throws Exception{
		
		String szContent = openFile(url+num);
		Parser parser = Parser.createParser(szContent, ENCODE);

		HtmlPage page = new HtmlPage(parser);
		parser.visitAllNodesWith(page);

		NodeList nodelist = page.getBody();
		NodeFilter filter = new TagNameFilter(tagName);
		nodelist = nodelist.extractAllNodesThatMatch(filter, true);
		for (int i = 0; i < nodelist.size(); i++) {
			Tag t = (Tag) nodelist.elementAt(i);
			String tagClass = t.getAttribute("class");
			if(className.equals(tagClass)){
				return t;
			}
		}
		System.out.println(tagName +":"+ className + " unfound.");
		return null;
	}
	
	public static String openFile( String url ) {
        try {
            BufferedReader bis = new BufferedReader(new InputStreamReader(getInputStream(url)) );
            String szContent="";
            String szTemp;
            
            while ( (szTemp = bis.readLine()) != null) {
                szContent+=szTemp+"\n";
            }
            bis.close();
            return szContent;
        }
        catch( Exception e ) {
            return "openFile fail";
        }
    }
	public static InputStream getInputStream(String url) {
	     HttpClient client = new HttpClient();
	     if (proxyHost != null) {
	         client.getHostConfiguration().setProxy(proxyHost, proxyPort);
	     }
	     client.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
	     client.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
	     GetMethod method = new GetMethod(url);
	     method.addRequestHeader("Content-Type", "text/html; charset=utf-8");
	     try {
	         int statusCode = client.executeMethod(method);
	         if (statusCode != HttpStatus.SC_OK) {
	             throw new HttpException("HttpStatusCode : " + statusCode);
	         }
	         InputStream is = method.getResponseBodyAsStream();
	         
	         return (is);
	     } catch (HttpException he) {
	         he.printStackTrace();
	     } catch (IOException ie) {
	         ie.printStackTrace();
	     }
	     return null;
	 }
}
