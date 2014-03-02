package muit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.security.*;
import org.apache.commons.codec.binary.Base64;


public class DataConverter {

	public static void main(String[] args) {
		
		String in_file_name = args[0];
		String out_path = args[1];
		
		int COL_NO_CUSTOMER_ID = 0;
		int COL_NO_FROM_BRANCH_NO = 1;
		int COL_NO_FROM_ACCOUNT_NO = 2;
		int COL_NO_TO_BANK_CODE = 4;
		int COL_NO_TO_BRANCH_NO = 5;
		int COL_NO_TO_ACCOUNT_NO = 6;
		int COL_NO_DIRECTION = 3;
		
		String SHIMUKE = "0";
		String HISHIMUKE = "1";
		
		String BTMU_CODE = "005";
		
		int cnt = 0;
		
		try {
			//ファイルを読み込む
            FileReader fr = new FileReader(in_file_name);
            BufferedReader br = new BufferedReader(fr);

            List<Trandata> shimukeList = new ArrayList<Trandata>();
            List<Trandata> hishimukeList = new ArrayList<Trandata>();
            
            //読み込んだファイルを１行ずつ処理する
            String line;
            while ((line = br.readLine()) != null) {
            	cnt++;
            	
            	//１行目はスキップ
            	if(cnt == 1){
            		continue;
            	}
            	
                //区切り文字","で分割する
            	String[] tokens = line.split(",");
            	
            	if(tokens[COL_NO_DIRECTION].equals(SHIMUKE)){
            		Trandata shimuke = new Trandata();
            		shimuke.setCustomer_id(tokens[COL_NO_CUSTOMER_ID]);
            		shimuke.setFrom_branch_no(tokens[COL_NO_FROM_BRANCH_NO]);
            		shimuke.setFrom_account_no(tokens[COL_NO_FROM_ACCOUNT_NO]);
            		shimuke.setTo_bank_code(tokens[COL_NO_TO_BANK_CODE]);
            		shimuke.setTo_branch_no(tokens[COL_NO_TO_BRANCH_NO]);
            		shimuke.setTo_account_no(tokens[COL_NO_TO_ACCOUNT_NO]);
            		shimukeList.add(shimuke);
            		
            	}else if(tokens[COL_NO_DIRECTION].equals(HISHIMUKE)){
            		Trandata hishimuke = new Trandata();
            		hishimuke.setCustomer_id(tokens[COL_NO_CUSTOMER_ID]);
            		hishimuke.setFrom_branch_no(tokens[COL_NO_FROM_BRANCH_NO]);
            		hishimuke.setFrom_account_no(tokens[COL_NO_FROM_ACCOUNT_NO]);
            		hishimuke.setTo_bank_code(tokens[COL_NO_TO_BANK_CODE]);
            		hishimuke.setTo_branch_no(tokens[COL_NO_TO_BRANCH_NO]);
            		hishimuke.setTo_account_no(tokens[COL_NO_TO_ACCOUNT_NO]);
            		hishimukeList.add(hishimuke);
            		
            	}else{
            		throw new Exception("Unexpected Flg : " + tokens[COL_NO_DIRECTION]);
            	}
                
            }

            //終了処理
            br.close();
            
//            System.out.println("s : " + shimukeList.size() + ", h : " + hishimukeList.size());
            System.out.println(cnt + "件読み込み完了");
            
            FileWriter fw = new FileWriter(out_path + "graphdata.csv", false);
            FileWriter enfw = new FileWriter(out_path + "encrypted_graphdata.csv", false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            PrintWriter enpw = new PrintWriter(new BufferedWriter(enfw));
        	pw.println("source,target,value");
        	enpw.println("source,target,value");
            
        	int total = shimukeList.size();
        	int done = 0;
        	int percent = 0;
        	int outCnt = 0;
        	
        	List<String> patterns = new ArrayList<String>();
        	
            for(Trandata shimuke : shimukeList){
            	GraphData gd = new GraphData();
            	gd.setFrom(BTMU_CODE +"-" 
            			+ shimuke.getFrom_branch_no() + "-" 
	            			+ shimuke.getCustomer_id() + "-" 
	            				+ "xxxxxxx");
				gd.setTo(shimuke.getTo_bank_code() + "-" 		//bankcode
    					+ shimuke.getTo_branch_no() + "-"		//branchno
							+ "???????-"						//customerid
    							+ shimuke.getTo_account_no());	//accountno
				gd.setValue("1.0");
            	
            	if(shimuke.getTo_bank_code().equals(BTMU_CODE)){
            		for(Trandata hishimuke : hishimukeList){

//            			System.out.println(
//                    	"hishimuke.getFrom_branch_no : " + hishimuke.getFrom_branch_no() + 
//                    	", shimuke.getTo_branch_no : " + shimuke.getTo_branch_no() +
//            			", hishimuke.getFrom_account_no : " + hishimuke.getFrom_account_no() +
//            			", shimuke.getTo_account_no : " + shimuke.getTo_account_no()
//            			);
            			
            			if(hishimuke.getFrom_branch_no().equals(shimuke.getTo_branch_no())
            					&& hishimuke.getFrom_account_no().equals(shimuke.getTo_account_no())){
            				gd.setTo(shimuke.getTo_bank_code() + "-" 	//bankcode
            					+ hishimuke.getFrom_branch_no() + "-" 	//branchno
            						+ hishimuke.getCustomer_id() + "-"	//customerid
            							+ "xxxxxxx");					//accountno
            				break;
            			}
            		}
            	}
            	
//            	System.out.println(gd.getFrom() + "," + gd.getTo() + "," + gd.getValue());
            	
            	String pattern = gd.getFrom() + "," + gd.getTo();
            	if(!patterns.contains(pattern)){
            		patterns.add(pattern);
            		
            		pw.println(gd.getFrom() + "," + gd.getTo() + "," + gd.getValue());
            		enpw.println(encrypt(gd.getFrom()) + "," + encrypt(gd.getTo()) + "," + gd.getValue());
            		outCnt++;
            	}
            	
            	done++;
            	int p = Math.round(done / total * 100);
            	for(int i=0; i < (p - percent); i++){
            		System.out.print("#");
            	}
            	percent = p;
            }
            
            //ファイルに書き出す
            pw.close();
            enpw.close();
            
            //debug
            if(outCnt != patterns.size()){
            	throw new Exception("出力件数とパターン数が相違（出力件数：" + outCnt + ", パターン数:" + patterns.size());
            }
            
            System.out.println("\n" + outCnt + "件書き出し完了");
//            System.out.println(patterns.size());

        } catch (Exception ex) {
            //例外発生時処理
            ex.printStackTrace();
        }		
	}

	public static String encrypt (String target) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(target.getBytes());
		byte[] digest = md.digest();
		byte[] encodedBytes = Base64.encodeBase64(digest);
		return new String(encodedBytes);
	}	
	
}

final class GraphData{
	private String from = "";
	private String to = "";
	private String value = "";

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

final class Trandata{
	private String customer_id = "";
	private String from_branch_no = "";
	private String from_account_no = "";
	private String to_bank_code = "";
	private String to_branch_no = "";
	private String to_account_no = "";

	public String getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	public String getFrom_branch_no() {
		return from_branch_no;
	}
	public void setFrom_branch_no(String from_branch_no) {
		this.from_branch_no = from_branch_no;
	}
	public String getFrom_account_no() {
		return from_account_no;
	}
	public void setFrom_account_no(String from_account_no) {
		this.from_account_no = from_account_no;
	}
	public String getTo_bank_code() {
		return to_bank_code;
	}
	public void setTo_bank_code(String to_bank_code) {
		this.to_bank_code = to_bank_code;
	}
	public String getTo_branch_no() {
		return to_branch_no;
	}
	public void setTo_branch_no(String to_branch_no) {
		this.to_branch_no = to_branch_no;
	}
	public String getTo_account_no() {
		return to_account_no;
	}
	public void setTo_account_no(String to_account_no) {
		this.to_account_no = to_account_no;
	}
}
