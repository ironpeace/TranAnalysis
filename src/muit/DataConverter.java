package muit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataConverter {

	public static void main(String[] args) {
		
		String in_file_name = args[0];
		String out_file_name = args[1];
		
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
            //�t�@�C����ǂݍ���
            FileReader fr = new FileReader(in_file_name);
            BufferedReader br = new BufferedReader(fr);

            List<Trandata> shimukeList = new ArrayList<Trandata>();
            List<Trandata> hishimukeList = new ArrayList<Trandata>();
            
            
            //�ǂݍ��񂾃t�@�C�����P�s����������
            String line;
            while ((line = br.readLine()) != null) {
            	
            	//�P�s�ڂ̓X�L�b�v
            	if(cnt == 0){
            		cnt++;
            		continue;
            	}
            	
                //��؂蕶��","�ŕ�������
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

            //�I������
            br.close();
            
            System.out.println("s : " + shimukeList.size() + ", h : " + hishimukeList.size());
            
            FileWriter fw = new FileWriter(out_file_name, false);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
        	pw.println("source,target,value");
            
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
            	
            	System.out.println(gd.getFrom() + "," + gd.getTo() + "," + gd.getValue());
            	pw.println(gd.getFrom() + "," + gd.getTo() + "," + gd.getValue());
            }
            
            //�t�@�C���ɏ����o��
            pw.close();

        } catch (Exception ex) {
            //��O����������
            ex.printStackTrace();
        }		
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