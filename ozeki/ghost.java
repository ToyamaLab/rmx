//package jp.co.bbreak.sokusen._3._4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class dbtest {
	public static void main(String[] args) {
		//connectDoc();
		inputQuery();
		return;
	}

	public static void connectDoc() {
		//String[] com = new String[] {"/usr/local/bin/docker", "hello-world"};
		String[] com = new String[] {"/usr/local/bin/psql", "-U", "perm", "-d", "testdb", "-p", "5432", "-h", "localhost", "-c", "select provenance a from x;"};
		//String[] com = new String[]{"/usr/local/bin/docker", "exec", "-ti", "myperm2", "/home/perm/install/bin/psql", "-U", "perm", "-d", "testdb", "-c", "\"select a from x;\""};
//		try {
//			// -c "select~"
//			//String[] com = new String[] {"docker", "hello-world"};
//			//String[] com = new String[]{"docker", "exec", "-ti", "myperm2", "/home/perm/install/bin/psql", "-U", "perm", "-d", "testdb", "-c", "\"select a from x;\""};
//			//Process p = Runtime.getRuntime().exec(com);
//			//Process p = Runtime.getRuntime().exec(new String[] {"psql", "-U", "perm", "-d", "testdb", "-p", "5432", "-h", "localhost", "-c", "\"select", "a", "from", "x;\""});
//			Process p = Runtime.getRuntime().exec(new String[] {"pwd"});
//			int ret = p.waitFor();
//			//System.out.println(ret);
////			String[] outs = new String[3];
////			InputStream in = null;
////			BufferedReader br = null;
//
//
//
//			InputStream is = p.getInputStream(); // プロセスの結果を変数に格納する
//	        BufferedReader br = new BufferedReader(new InputStreamReader(is)); // テキスト読み込みを行えるようにする
//
//	        while (true) {
//	            String line = br.readLine();
//	            if (line == null) {
//	                break; // 全ての行を読み切ったら抜ける
//	            } else {
//	                System.out.println("line : " + line); // 実行結果を表示
//	            }
//	        }
//


//			in = p.getInputStream();
//			StringBuffer out = new StringBuffer();
//			br = new BufferedReader(new InputStreamReader(in));
//			outs[0] = out.toString();
//			br.close();
//			in.close();
			//System.out.println("KK");
//			System.out.println(outs[0].length());
//			System.out.println(outs[0]);

			//Process p = Runtime.getRuntime().exec("docker exec -ti myperm /home/perm/install/bin/psql ts -U ozeki");
//		} catch (IOException | InterruptedException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
	      ProcessBuilder pb = new ProcessBuilder(com);
		try {
            //コマンド実行
            Process process = pb.start();

            //コマンド実行の結果を待機
            int ret = process.waitFor();

            //標準出力
            InputStream is = process.getInputStream();
            printInputStream(is);

            //標準エラー
            InputStream es = process.getErrorStream();
            printInputStream(es);

        }
        catch (Exception e) {
            System.out.print(e);

        }

        //return true;
    }
	public static void printInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            for (;;) {
                String line = br.readLine();
                if (line == null) break;
                System.out.println(line);
            }
        } finally {
            br.close();
        }
    }


    public static void inputQuery() {
    	   boolean flag = false;
    	   String query = new String();
    	   System.out.println("invoid");
    	   while(!flag) {
    		   Scanner scanner = new Scanner(System.in);
    		   System.out.print("input : ");

    		   while(scanner.hasNext()) {
    			   String tmp = scanner.next();
    			   if( tmp.matches("exit")) return;
    			   query += " ";
    			   query += tmp;
    			   //if(tmp.contains("select")) query += " PROVENANCE";
    			   if(tmp.contains(";")) {
    				   flag = true;
    				   //scanner.close();
    				   break;
    			   }
    			   //System.out.println(query);
    		   }
    		   //System.out.println(query);
    	   }
    	   //System.out.println("End");
    	   flag = false;
    	   connectpsql(query);

    }

    public static String getTableName(String s) {
    		System.out.println("column name " + s);
		String name = s.substring(12, s.indexOf("_", 13));
		System.out.println("table name " + name);
    		return name;
    }

    public static String getPColumn(String s) {

    		return s;
    }

    public static boolean isExist(ArrayList<TableInfo> ar, String s) {
    		for(int i = 0; i < ar.size(); i++) {
    			if(s.matches(ar.get(i).getTableName())) return true;
    		}
    		return false;
    }

    public static void connectpsql(String q) {
    		System.out.println(q);
        // データベースへの接続情報を格納する変数
        Connection conn = null;
        // JDBCドライバの読み込み
        try {
            // postgreSQLのJDBCドライバを読み込み
            Class.forName("org.postgresql.Driver");
        } catch(ClassNotFoundException e) {
            // JDBCドライバが見つからない場合
            e.printStackTrace();
        }
        try {
            // 1. データベースへの接続
            conn = DriverManager.getConnection("jdbc:postgresql:testdb", "perm", "pass");
            System.out.println("Connect");

            // 2. SELECT文の発光と結果の取得
            // Statementオブジェクトを生成
            Statement stmt = conn.createStatement();
            // SELECT文の発行と検索結果を格納する
            ResultSet rset = stmt.executeQuery(q);
            ArrayList<TableInfo> tables = new ArrayList<TableInfo>();
            ArrayList<String> columnName = new ArrayList<String>();
            ArrayList<String> checkColumnName = new ArrayList<String>();

            for(int i = 0; i < rset.getMetaData().getColumnCount(); i++) {
            	System.out.println("i = " + i + " " + rset.getMetaData().getColumnCount());
            		columnName.add(rset.getMetaData().getColumnName(i + 1));
             	System.out.println("AAA " + columnName.get(i));
             	//if(columnName.get(i).contains("prov_public_") && columnName.get(i).contains("_1_")) {
             	if(columnName.get(i).contains("prov_public_") && columnName.get(i).matches(".*[_]+[0-9]+[_]+.*")) {
             		System.out.println("HIT");
             		String tableName = getTableName(columnName.get(i));
             		if(isExist(tables, tableName)) {
             			System.out.println("Exist");
             		}else {
             			System.out.println("Not Exist");
             			TableInfo table = new TableInfo(tableName);
             			DatabaseMetaData dbmd = conn.getMetaData();
             			ResultSet rs2 = dbmd.getBestRowIdentifier(null, conn.getSchema(), tableName, 0, true);
             			//ResultSet rs2 = dbmd.getPrimaryKeys(null, conn.getSchema(), tableName);
             			try {
             				while (rs2.next()) {
             					/*String kname = rs2.getString("PK_NAME");
             					String cname = rs2.getString("COLUMN_NAME");
             					short seq = rs2.getShort("KEY_SEQ");

             					System.out.println("Check : " + cname + "\t" + seq + "\t" + kname);*/
             					String cname = rs2.getString("COLUMN_NAME");
             					short  pseud = rs2.getShort("PSEUDO_COLUMN");
             					short  scope = rs2.getShort("SCOPE");
             					System.out.println(Arrays.toString(
             						new Object[] { cname, pseud, scope }
             					));
             					System.out.println("BBB");
             					table.addCheckColumn(cname);
             					System.out.println("ABC");
             				}
             				checkColumnName.add(table.getCheckColumn());
             			} finally {
             				rs2.close();
             			}
             			/*System.out.println("RS2 : " + rs2);
             			while(rs2.next()) {
             				System.out.println("ADD " + table.getTableName());
             				System.out.println(rs2.getString("COLUMN_NAME"));
             				tables.add(table);
             			}*/
             		}

             	}
            }
            // 3. 結果の表示
            while (rset.next()) {
            		String res = new String();
            		for(int i = 0; i < columnName.size(); i++) {
            			if(i != 0) res += ", ";
            			res += rset.getString(columnName.get(i));
            		}
            		System.out.println("RES" + res);
            		String res2 = new String();
            		for(int i = 0; i < checkColumnName.size(); i++) {
            			if(i != 0) res2 += ", ";
            			res2 += rset.getString(checkColumnName.get(i));
            		}
            		System.out.println("RES" + res2);
            }
        } catch(SQLException e) {
            // 接続、SELECT文の発行でエラーが発生した場合
            e.printStackTrace();
        } finally {
            // 4.データベース接続の切断
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (SQLException e) {
                    // データベース接続の切断でエラーが発生した場合
                    e.printStackTrace();
                }finally {
                	 System.out.println("Fin");
                	 inputQuery();
                }
            }
        }
    }
}

class TableInfo{
	private String name;
	private ArrayList<String> checkColumns;
	private String checkColumn;

	public TableInfo(String s) {
		name = s;
		checkColumns = new ArrayList<String>();
		checkColumn = new String();
	}

	public void addCheckColumn(String s) {
		//System.out.println("ADD Be");
		//this.checkColumns.add(s);
		checkColumn = s;
		//System.out.println("ADD Af");
	}

	public String getTableName() {
		return name;
	}

	public String getCheckColumn() {
		return checkColumn;
	}
}
//select * from artist;
//select provenance a from x;
/*
select provenance p.name
from person p, sigmod s, y2000 y
where s.name = y.name and p.name = s.name and p.sex = 'F';
*/
