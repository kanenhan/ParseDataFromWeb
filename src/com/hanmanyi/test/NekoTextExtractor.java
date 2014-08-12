package com.hanmanyi.test;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class NekoTextExtractor {
	
	private static int connectionTimeout = 5000;
    private static int soTimeout = 12000;
    private static String proxyHost = null;
    private static int proxyPort;
    
 public DocumentFragment getDocument(InputStream is){
  DocumentFragment node=null;
  try{
   DOMFragmentParser parser=new DOMFragmentParser();
   node=new HTMLDocumentImpl().createDocumentFragment();
   parser.parse(new InputSource(is),node);
  }catch(Exception ex){
   ex.printStackTrace();
  }
  return node;
 }
 
 public DocumentFragment getDocument(Reader reader){
  DocumentFragment node=null;
  try{
   DOMFragmentParser parser=new DOMFragmentParser();
   node=new HTMLDocumentImpl().createDocumentFragment();
   parser.parse(new InputSource(reader),node);
  }catch(Exception ex){
   ex.printStackTrace();
  }
  return node;
 }
 
 public void getText(StringBuffer sb,Node node)throws Exception{
  if(enabledNode(node)){
   if(node.getNodeType()==Node.TEXT_NODE){
    String value=node.getNodeValue();
    sb.append(value.trim());
   }
   else{
    NodeList nodes=node.getChildNodes();
    for(int i=0;i<nodes.getLength();i++){
     Node n=nodes.item(i);
     getText(sb,n);
    }
   }
  }
 }
 
 public void getTable(StringBuffer sb,Node node)throws Exception{
	  if(enabledNode(node)){
	   if("TABLE".equals(node.getNodeName())){
	    String value=node.getNodeValue();
	    System.out.println(node.getTextContent());
	    
	    System.out.println(node.getAttributes());
	    System.out.println("-----------------");
//	    sb.append(value.trim());
	   }
	   else{
		   
	    NodeList nodes=node.getChildNodes();
	    for(int i=0;i<nodes.getLength();i++){
	     Node n=nodes.item(i);
	     getTable(sb,n);
	    }
	   }
	  }
 	}
 
 public String extractText(String source)throws Exception{
  StringReader reader=new StringReader(source);
  return extractText(reader);
 }
 
 public String extractText(InputStream is)throws Exception{
  Node node=getDocument(is);
  StringBuffer sb=new StringBuffer();
  getText(sb,node);
  return sb.toString();
 }
 
 public String extractText(Reader reader)throws Exception{
  Node node=getDocument(reader);
  StringBuffer sb=new StringBuffer();
  getText(sb,node);
  return sb.toString();
 }
 
 
 public boolean enabledNode(Node node){
  if(node.getNodeName().equalsIgnoreCase("script"))
   return false;
  else if(node.getNodeName().equalsIgnoreCase("link"))
   return false;
  else if(node.getNodeName().equalsIgnoreCase("style"))
   return false;
  else if(node.getNodeType()==Node.COMMENT_NODE)
   return false;
  else
   return true;
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
 
 public static void main(String[] args)throws Exception {
	 String url="http://mysaf2.safmarine.com/wps/portal/Safmarine/etrackUnregistered?linktype=unreg&queryorigin=Header&queryoriginauto=HeaderNonSecure&searchType=Document&searchNumberString=754315522";
	 StringBuffer sb=new StringBuffer();
	 NekoTextExtractor nte=new NekoTextExtractor();
	 InputStream is = getInputStream(url);
	 
	 System.out.println("read done");
	 
	 Node node=nte.getDocument(is);
	 nte.getTable(sb,node);
//	 nte.getText(sb,node);
//	 System.out.println(sb.toString());
 }
} 