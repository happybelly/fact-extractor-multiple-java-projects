package utility;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import utility.Debug.DEBUG_CONFIG;

import java.io.BufferedReader;

public class utility {
	public void writeFile(String path, String s, boolean append) {
		File log_f;
		log_f = new File(path);
		Writer out; 
		try {

			if(!log_f.exists()) {
				Path pathToFile = Paths.get(path);
				if(Files.createDirectories(pathToFile.getParent()) == null || Files.createFile(pathToFile) == null) {
					Debug.print("Failed to create file " + path, DEBUG_CONFIG.debug_error);
					return;
				}
			}
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(path), append), "UTF-8"));
			//				out = new BufferedWriter(new FileWriter(new File(path), append));
			out.append(s);
			out.flush();
			out.close();

		}
		catch(Exception e) {
			Debug.print("Failed to create file " + path, DEBUG_CONFIG.debug_error);
			e.printStackTrace();

		}
	}

	String sortMap(Map<String, Integer> freqs) {
		List<String> words  = null;
		List<Integer> freq = null;
		words = new ArrayList<String>(freqs.keySet());
		freq = new ArrayList<Integer>();
		utility util = new utility();
		util.sort(freqs, words, freq);
		String m = "";
		for(int i = 0; i < words.size(); i++) {
			m = m + words.get(i) + "\t" + freq.get(i) + "\r\n";

		}
		return m;
	}

	public void sort(Map<String, Integer> top_word, List<String> words, List<Integer> freq ) {
		{

			for(int j = 0; j < words.size(); j++) freq.add(top_word.get(words.get(j)));
			{//sort words and freq
				for(int t = 0; t < freq.size(); t++) {
					for(int j = t + 1; j < freq.size(); j++) {
						if(freq.get(t) < freq.get(j)) {
							int temp = freq.get(t); freq.set(t, freq.get(j)); freq.set(j, temp);
							String temps = words.get(t); words.set(t, words.get(j)); words.set(j, temps);
						}
					}
				}

			}
		}
	}


	public String readFromFile(String path) {
		BufferedReader br;
		try {
			br= new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			String everything = IOUtils.toString(br);
			return everything;
		}
		catch(Exception e) {e.printStackTrace(); return null;}
		finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public String readFromFile(File file) {
		BufferedReader br;
		try {
			br= new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		try {
			String everything = IOUtils.toString(br);
			return everything;
		}
		catch(Exception e) {e.printStackTrace(); return null;}
		finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	/**
	 * This is for special characters that PDFBox is not able to recognize. e.g. μ
	 * http://www.fileformat.info/info/charset/UTF-8/list.htm
	 * @param s
	 * @return
	 */
	public String fixEncoding(String s) {
		byte[] bytes;
		try {
			bytes = s.getBytes("UTF-8");
			//				 String result = new String(bytes, "UTF-8");
			//				 System.out.println("+++" + result);
			List<Byte> newb = new ArrayList<Byte>();
			for(int i = 0; i < bytes.length; i++) newb.add(bytes[i]);
			for(int i = 0; i < newb.size(); i++) {
				//common: 17, 65, 67, 1 // 13 is cr; 10 is line feed
				if(newb.get(i) == 1) {
					if(newb.get(i + 1) == 108 || newb.get(i+1) == 103 ) {// "l" or "g"; i.e. (char)bytes[i] + (char)bytes[i+1] is "μl" or "μg"
						//							 bytes[i] = (byte) 117; // 52925 is "0x cebc" i.e. μ; 117 for u
						//add two bytes which combined represents u 
						byte t1 = (byte) -50;
						byte t2 = (byte) -68;
						newb.set(i, t1);
						int size = newb.size();
						newb.add(newb.get(size - 1));
						for(int j = size -1; j > i + 1; j--) newb.set(j, newb.get(j - 1));
						newb.set(i + 1, t2);


						//							 System.out.println((char) bytes[i]);
					}
				}
				//				 String t = "" ;
				//				 for(int j = 5; j > 0; j--) {
				//					 t = t + (char) bytes[i - j]; 
				//				 }
				//				 for(int j = 0; j < 5; j++) {
				//					 t = t + (char) bytes[i + j]; 
				//				 }
				//				 System.out.println(i + ";" + bytes[i] + "++" + t + "++");
				//					 System.out.println(String.format("%02X ", test.getBytes("UTF-8")[1]));
			}
			//				 Bytes.toArray()
			byte[] newbytes = new byte[newb.size()];
			for(int i = 0; i < newb.size(); i++) newbytes[i]=newb.get(i);
			String result = new String(newbytes, "UTF-8");
			return result;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Document stringToDom(String xmlSource) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlSource)));
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Document stringToDomWithoutDtd(String xmlSource) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			//		        factory.
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xmlSource)));
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String readingFromURL(String url) {
		URL oracle = null;
		try {
			oracle = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//					e.printStackTrace();
			return null;
		}
		String web = null;
		try {
			web = IOUtils.toString(oracle.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//					e.printStackTrace();
		}
		return web;
	}


	public static double round(double x, int decimal) {
		String s = Double.toString(x);
		int indexDot = -1;
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == '.') {
				indexDot = i; break;
			}
		}
		s = s.substring(0, Math.min(indexDot + 3, s.length()));
		return Double.parseDouble(s);
	}
	public void sortByStart(List<Span> input) {
		//			 List<Span> result = new ArrayList<Span>();
		for(int i = 0; i < input.size(); i++) {
			for(int j = i + 1; j < input.size(); j++) {
				if(input.get(i).getStart() > input.get(j).getStart()) {
					Span temp = input.get(i);
					input.set(i, input.get(j));
					input.set(j, temp);
				}
			}
		}
	}

	public String xmlNodeToString(Document doc){
		try {
			//			 Document doc = builder.parse(st);
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			//			 System.out.println("XML IN String format is: \n" + writer.toString());
			return writer.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String charToUnicode(char a) {
		return "\\u" + Integer.toHexString(a | 0x10000).substring(1);
	}

	public String toString(BufferedReader br) {
		try {
			return IOUtils.toString(br);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}



