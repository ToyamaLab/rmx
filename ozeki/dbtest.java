import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class dbtest {
	public static long startTime;
	public static long finishTime;
	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Input Database pass and user name (and password)");
			return;
		}else if(args.length >= 2) {
			String dbn = args[0];
			String un = args[1];
			if(args.length == 2) {
				inputQuery(dbn, un, "");
			}else if(args.length == 3) {
				String ps = args[2];
				inputQuery(dbn, un, ps);
			}
		}
		return;
	}

    public static void inputQuery(String dbn, String un, String ps) {
    	   boolean flag = false;
    	   String query = new String();
    	   while(!flag) {
    		   Scanner scanner = new Scanner(System.in);
    		   System.out.print("Input Query: ");

    		   while(scanner.hasNext()) {
    			   String tmp = scanner.next();
						 tmp = tmp.toLowerCase();
    			   if(tmp.equals("exit")) return;
    			   else if(tmp.contains("provenance")) continue;
    			   query += " ";
    			   query += tmp;
    			   if(tmp.contains("select")) query += " PROVENANCE";
    			   if(tmp.contains(";")) {
    				   flag = true;
    				   break;
    			   }
    		   }
    	   }
    	   flag = false;
    	   connectpsql(query, dbn, un, ps);
    }

    public static String getTableName(String s) {
    		//System.out.print("-- getTableName -- column name " + s);
		String name = s.substring(12, s.indexOf("_", 13));
		//System.out.println("　-->  table name " + name + "--");
    		return name;
    }

    public static String getColumnName(String s) {
    		//System.out.print("-- getColumnName -- column name " + s);
    		int last = s.lastIndexOf("_");
    		int next = s.lastIndexOf("__");
    		if(last - next <= 1) {
    			last = s.lastIndexOf("_", next - 1);
    		}
    		String name = s.substring(last + 1, s.length());
    		//System.out.println("　-->  column name " + name + "--");
    		return name;
    }

    public static ArrayList<String> getPColumn(Connection conn, String tableName) {
    		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs2 = dbmd.getBestRowIdentifier(null, conn.getSchema(), tableName, 0, true);
			ArrayList<String> checkColumn = new ArrayList<String>();
			try {
				while (rs2.next()) {
					String cname = rs2.getString("COLUMN_NAME");
					//System.out.print("Indentifier column(s) --->");
					if(cname.contains("_")) {
						//System.out.println("Contain _");
						cname = cname.replace("_", "__");
					}
					//System.out.println(Arrays.toString(
					//	new Object[] {cname}
				//	));

					checkColumn.add(cname);
				}
			} finally {
				rs2.close();
			}
			if(checkColumn.isEmpty()) {
				rs2 = dbmd.getColumns(null, conn.getSchema(), tableName, "%");
				while(rs2.next()) {
					String cname = rs2.getString("COLUMN_NAME");
					//System.out.print("Indentifier column(s) --->");
					if(cname.contains("_")) {
						//System.out.println("Contain _");
						cname = cname.replace("_", "__");
					}
				//	System.out.println(Arrays.toString(
				//		new Object[] {cname}
				//	));
					checkColumn.add(cname);
				}
				//System.out.println("Identifier column names --- " + checkColumn);
			}
			return checkColumn;
    		}catch(SQLException e) {
    		}
    		return null;
    }

    public static boolean isExist(ArrayList<TableInfo> ar, String s) {
    		for(int i = 0; i < ar.size(); i++) {
    			if(s.equals(ar.get(i).getTableName())) return true;
    		}
    		return false;
    }

    public static int getTableIndex(ArrayList<TableInfo> ar, String s) {
    		for(int i = 0; i < ar.size(); i++) {
			if(s.equals(ar.get(i).getTableName())) return i;
		}
		return -1;
    }

	public static ArrayList<ArrayList<Integer>> generateCheckIndexs(ArrayList<TableInfo> ts){
		ArrayList<ArrayList<Integer>> cis = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < ts.size(); i++){
			if(ts.get(i).checkable()) {
				for(int j = 0; j < ts.get(i).getIntChecks().size(); j++) {
					cis.add(ts.get(i).getIntChecks().get(j));
				}
			}
		}
		return cis;
	}

	public static void showAllColumn(ArrayList<String> columnName){
		String res = new String();
		for(int i = 0; i < columnName.size(); i++) {
			if(i != 0) res += ", ";
			res += columnName.get(i);
		}
		System.out.println(res);
	}

	public static void showAll(ResultSet rset, int columnNum) throws SQLException{
		String res = new String();
		for(int i = 1; i <= columnNum; i++) {
			if(i != 1) res += ", ";
			res += rset.getString(i);
		}
		System.out.println(res);
	}

	public static void showAllRow(ResultSet rset, int columnNum) throws SQLException{
		rset.absolute(0);
		while(rset.next()) {
			showAll(rset, columnNum);
		}
	}

	public static void showEachCheckColumn(ResultSet rset, ArrayList<ArrayList<Integer>> checkIndexs, ArrayList<String> columnName) throws SQLException{
		for(int i = 0; i < checkIndexs.size(); i++) {
			String res2 = new String();
			String res3 = new String();
			//ArrayList<String> checks = tables.get(i).getCheckColumns();
			for(int j = 0; j < checkIndexs.get(i).size(); j++) {
				if(j != 0) res2 += ", ";
				if(j != 0) res3 += ", ";
				res2 += rset.getString(checkIndexs.get(i).get(j) + 1);
				res3 += columnName.get(checkIndexs.get(i).get(j));
			}
			//System.out.println("RES2 (" + res3 + ")  (" + res2 + ")");
		}
	}

	public static DifferentRow checkEachCheckColumn(int index, ResultSet rset, ArrayList<ArrayList<Integer>> checkIndexs, ArrayList<String> columnName) throws SQLException{
		ArrayList<Integer> difColumns = new ArrayList<Integer>();
		for(int i = 0; i < checkIndexs.size(); i++) {
			boolean dif = false;
			String res2 = new String();
			String res3 = new String();
			String tmp = new String();
			//ArrayList<String> checks = tables.get(i).getCheckColumns();
			for(int j = 0; j < checkIndexs.get(i).size(); j++) {
				String tmp2 = rset.getString(checkIndexs.get(i).get(j) + 1);
				if(j == 0) tmp = tmp2;
				else{
					res2 += ", ";
					res3 += ", ";
					if(!tmp2.equals(tmp))	dif = true;
				}
				res2 += tmp2;
				res3 += columnName.get(checkIndexs.get(i).get(j));
			}
			//System.out.println("RES (" + res3 + ")  (" + res2 + ")");
			if(dif){
				difColumns.add(i);
				//System.out.println("Having Different Provenance");
			}
		}
		if(!difColumns.isEmpty()){
			DifferentRow dr = new DifferentRow(index, difColumns);
			return dr;
		}else{
			return null;
		}
	}

	public static void showNormalColumnName(ArrayList<String> columnName, int normal){
		String s = new String();
		for(int i = 0; i < normal; i++){
			if(i != 0) s += ", ";
			s += columnName.get(i);
		}
		System.out.println(s);
	}

	public static void showAllNormalColumn(ResultSet rset, int normal) throws SQLException{
		rset.absolute(0);
		while(rset.next()){
			String s = new String();
			for(int i = 0; i < normal; i++){
				if(i != 0) s += ", ";
				s += rset.getString(i + 1);
			}
			System.out.println(s);
		}
	}

	public static void showDifferent(ArrayList<DifferentRow> drs, ResultSet rset, int x) throws SQLException{
		for(int i = 0; i < drs.size(); i++){
			rset.absolute(drs.get(i).getIndex() + 1);
			//System.out.println(drs.get(i).getIndex());
			String s = new String();
			for(int j = 0; j < x; j++){
				if(j != 0) s += ", ";
				s += rset.getString(j + 1);
			}
			System.out.println(s);
		}
	}

	public static void showCorrect(ArrayList<Integer> ci, ResultSet rset, int x) throws SQLException{
		for(int i = 0; i < ci.size(); i++){
			rset.absolute(ci.get(i) + 1);
			String s = new String();
			for(int j = 0; j < x; j++){
				if(j != 0) s += ", ";
				s += rset.getString(j + 1);
			}
			System.out.println(s);
		}
	}

	public static void showOnlyDifferentProvenance(ArrayList<DifferentRow> drs, ResultSet rset, ArrayList<ArrayList<Integer>> checkIndexs, ArrayList<String> columnName, int normal) throws SQLException{
		for(int i = 0; i < drs.size(); i++){
			rset.absolute(drs.get(i).getIndex() + 1);
			showNormalColumnName(columnName, normal);
			showAll(rset, normal);
			for(int j = 0; j < drs.get(i).getCheckIndex().size(); j++){
				String c = new String();
				String s = new String();
				ArrayList<Integer> tmp = checkIndexs.get(drs.get(i).getCheckIndex().get(j));
				for(int k = 0; k < tmp.size(); k++){
					if(k != 0){
						c += ", ";
						s += ", ";
					}
					c += columnName.get(tmp.get(k));
					s += rset.getString(tmp.get(k) + 1);
				}
				System.out.println("(" + c + ") = (" + s + ")");
			}
		}
	}

	public static void askNextOption(){
		System.out.println();
		System.out.println("Look all rows' all columns : Input 'a'");//
		System.out.println("Look all rows' normal columns : Input 'an'");//
		System.out.println("Look Same provenance rows' normal columns : Input 'sn'");//
		System.out.println("Look Same provenance rows' all columns : Input 's'");//
		System.out.println("Look Different provenance rows' normal columns : Input 'dn'");//
		System.out.println("Look Different provenance rows' all provenances : Input 'd'");//
		System.out.println("Look Different provenance rows' only different column provenances : Input 'dp'");//
		System.out.println("Look the number of the Same and the Different provenance row : Input 'n'");//
		System.out.println("Look all column names : Input 'c'");//
		System.out.println("Finish this query : Input 'q'");
	}

	public static String waitUserInput(){
		Scanner s = new Scanner(System.in);
		while(s.hasNext()) {
			String tmp = s.next();
			//System.out.println(tmp);
//			if(s.hasNext()) {
//				System.out.println("Unexpected input　");
//				break;
//			}
			if(tmp.equals("a") || tmp.equals("an") || tmp.equals("sn") || tmp.equals("s") || tmp.equals("dn") || tmp.equals("d") || tmp.equals("dp") || tmp.equals("n") || tmp.equals("c") || tmp.equals("q")) {
				//System.out.println("ok");
				return tmp;
			}
			else {
				System.out.println("Unexpected input");
				break;
			}
		}
		return null;
	}

    public static void connectpsql(String q, String dbn, String un, String ps) {
    		System.out.println(q);
    		startTime = System.currentTimeMillis();
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
        		String dbName = "jdbc:postgresql:" + dbn;
        		String userName = un;
        		String pass = ps;
            conn = DriverManager.getConnection(dbName, userName, pass);
            //System.out.println("Connect");
            //conn = DriverManager.getConnection("jdbc:postgresql://172.17.0.2:5432/testdb", "perm", "");

            // 2. SELECT文の発光と結果の取得
            // Statementオブジェクトを生成
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // SELECT文の発行と検索結果を格納する
            ResultSet rset = stmt.executeQuery(q);
            ArrayList<TableInfo> tables = new ArrayList<TableInfo>();
            ArrayList<String> columnName = new ArrayList<String>();
            ArrayList<String> checkColumnName = new ArrayList<String>();
//            ArrayList<String> normalColumn = new ArrayList<String>();
//       		ArrayList<ArrayList<String>> corrects = new ArrayList<ArrayList<String>>();
//       		ArrayList<ArrayList<String>> differents = new ArrayList<ArrayList<String>>();
       		ArrayList<Integer> ci = new ArrayList<Integer>();
       		ArrayList<DifferentRow> di = new ArrayList<DifferentRow>();
			ArrayList<ArrayList<Integer>> checkIndexs;

       		int normal = - 1;

			//各結果カラムに対して
            for(int i = 0; i < rset.getMetaData().getColumnCount(); i++) {
            		//System.out.println("Column No : " + i + " / " + rset.getMetaData().getColumnCount());
            		columnName.add(rset.getMetaData().getColumnName(i + 1));
             	//System.out.println("ColumnName --- " + columnName.get(i));
             	if(columnName.get(i).contains("prov_public_")) {
             		if(normal == -1)normal = i;
             		String tableName = getTableName(columnName.get(i));
             		if(isExist(tables, tableName)) {
             			//System.out.println("TABLE " + tableName + " is Exist");
             			int tableIndex = getTableIndex(tables, tableName);
             			if(tableIndex < 0) //System.out.println("Not correct tableIndex");
             				;
						String cname = getColumnName(columnName.get(i));
             			for(int j = 0; j < tables.get(tableIndex).getCheckColumn().size(); j++) {
             				if(cname.equals(tables.get(tableIndex).getCheckColumn().get(j))) {
                 				//System.out.println("MATCH with Check Column");
								tables.get(tableIndex).addintChecks(j, i);
                 				if(columnName.get(i).matches(".*[_]+[0-9]+[_]+.*"))tables.get(tableIndex).setTrue();
                 			}
             			}
             		} else {
             			//System.out.print("TABLE " + tableName + " is NOT Exist. -- ");
             			TableInfo table = new TableInfo(tableName);
             			tables.add(table);
             			table.registCheckColumn2(getPColumn(conn, tableName));
             			String cname = getColumnName(columnName.get(i));
             			for(int j = 0; j < table.getCheckColumn().size(); j++) {
             				if(cname.equals(table.getCheckColumn().get(j))) {
                 				//System.out.println("MATCH with Check Column");
								table.addintChecks(j, i);
                 			}
             			}
             		}
             	}
            }
            //----ここまでカラムからテーブル情報（TableInfo）を作成・記入

        		//System.out.println("Tables Size : " + tables.size());
            for(int i = 0; i < tables.size(); i++) {
            		//tables.get(i).seeAll();
            		if(tables.get(i).checkable()) {
            			//System.out.println(tables.get(i).getCheckColumns());
            			ArrayList<String> columns = tables.get(i).getCheckColumns();
            			//System.out.println("columns : " + columns);
            			for(int j = 0; j < columns.size(); j++) {
                			checkColumnName.add(columns.get(j));
                		}
            		}else continue;
            }
            //----ここまでどこを検査すればいいかを一列に整列　→　indexで管理してしまいたい
            //System.out.println("----checks----");
            //for(int i = 0; i < tables.size(); i++) {
            //		if(tables.get(i).checkable()) {
            			//System.out.println(new Object[]{tables.get(i).getChecks()});
            	//	}else continue;
            //}
			checkIndexs = generateCheckIndexs(tables);
			//System.out.println("----check Indexs----");
			//for(int i = 0; i < checkIndexs.size(); i++) {
				//System.out.println(checkIndexs.get(i));
			//}
            //----ここまで確認用

            //-----For Debug---------------------------------------
      /*      System.out.println("---- Check Column Name ----");
            for(int i = 0; i < checkColumnName.size(); i++) {
            		System.out.print(checkColumnName.get(i) + " , ");
            }
						*/
            //-----------------------------------------------------
            System.out.println("");
            // 3. 結果の表示
//            int correctCount = 0;
//            int differentCount = 0;
            int index = 0;
			showAllColumn(columnName);
            while (rset.next()) {
//            		boolean dif = false;

				//showAll(rset, columnName.size());
				//showEachCheckColumn(rset, checkIndexs, columnName);
				DifferentRow dr = checkEachCheckColumn(index, rset, checkIndexs, columnName);
				if(dr == null){
					ci.add(index);
				}else{
					di.add(dr);
				}
	        		index++;
            }
			if(di.size() < 1){
				//System.out.println("si");
				showNormalColumnName(columnName, normal);
				//showAllNormalColumn(rset, normal);
				showCorrect(ci, rset, normal);
			}else{
				System.out.println("With Same Provenance Rows");
				showNormalColumnName(columnName, normal);
				showCorrect(ci, rset, normal);
				System.out.println("With Different Provenance Rows");
				showNormalColumnName(columnName, normal);
				showDifferent(di, rset, normal);
				finishTime = System.currentTimeMillis();
				System.out.println("Time : " + (finishTime - startTime) + " ms");
				while(true) {
					askNextOption();
					String in = waitUserInput();
					if(in.equals("s")) {
						startTime = System.currentTimeMillis();
						showNormalColumnName(columnName, columnName.size());
						showCorrect(ci, rset, columnName.size());
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("d")) {
						startTime = System.currentTimeMillis();
						showNormalColumnName(columnName, columnName.size());
						showDifferent(di, rset, columnName.size());
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("dp")) {
						startTime = System.currentTimeMillis();
						showOnlyDifferentProvenance(di, rset, checkIndexs, columnName, normal);
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("a")) {
						startTime = System.currentTimeMillis();
						showNormalColumnName(columnName, columnName.size());
						showAllRow(rset, columnName.size());
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("an")) {
						startTime = System.currentTimeMillis();
						showNormalColumnName(columnName, normal);
						showAllRow(rset, normal);
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("sn")) {
						startTime = System.currentTimeMillis();
						showNormalColumnName(columnName, normal);
						showCorrect(ci, rset, normal);
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("dn")) {
						startTime = System.currentTimeMillis();
						showNormalColumnName(columnName, normal);
						showDifferent(di, rset, normal);
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("n")) {
						startTime = System.currentTimeMillis();
						System.out.println("Same provenances : " + ci.size());
						System.out.println("Different provenances : " + di.size());
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("c")) {
						startTime = System.currentTimeMillis();
						showNormalColumnName(columnName, columnName.size());
						finishTime = System.currentTimeMillis();
						System.out.println("Time : " + (finishTime - startTime) + " ms");
					}else if(in.equals("q")) break;
				}
			}
        } catch(SQLException e) {
            // 接続、SELECT文の発行でエラーが発生した場合
        		System.out.println("Err");
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
                	 inputQuery(dbn, un, ps);
                }
            }
        }
    }
}
class DifferentRow{
	private int index;
	private ArrayList<ArrayList<Integer>> dColumns;
	private ArrayList<Integer> checkIndex;

	public DifferentRow(int i, ArrayList<Integer> d) {
		index = i;
		checkIndex = d;
	}

	public int getIndex() {
		return index;
	}

	public ArrayList<ArrayList<Integer>> getDColumns(){
		return dColumns;
	}

	public ArrayList<Integer> getCheckIndex(){
		return checkIndex;
	}
}

class TableInfo{
	private String name;
	private ArrayList<String> checkColumns;
	private ArrayList<String> checkColumn;
	private ArrayList<ArrayList<String>> checks;
	private boolean check;

	private ArrayList<ArrayList<Integer>> intChecks;

	public TableInfo(String s) {
		name = s;
		checkColumns = new ArrayList<String>();
		checkColumn = new ArrayList<String>();
		check = false;
		checks= new ArrayList<ArrayList<String>>();
	}

	public void seeAll() {
		System.out.println("Name : " + name);
		System.out.println("checkcolumn : " + checkColumn);
		System.out.println("checkcolumns : " + checkColumns);
		System.out.println("check : " + check);
		System.out.println("checks : " + checks);
		System.out.println("intChecks : " + intChecks);
	}

	public boolean checkable() {
		return check;
	}

	public void addintChecks(int no, int addIndex){
//		System.out.println("addintChecks");
//		System.out.println(no + " " + addIndex + " " + intChecks);
//		System.out.println(intChecks.get(no));
		intChecks.get(no).add(addIndex);
	}

	public ArrayList<ArrayList<Integer>> getIntChecks(){
		return intChecks;
	}

	public void registCheckColumn2(ArrayList<String> as){
		checkColumn = as;
		intChecks = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < as.size(); i++) {
			intChecks.add(new ArrayList<Integer>());
		}
	}
	public void setTrue() {
		check = true;
	}

	public void addCheckColumn(String s) {
		//System.out.println("ADD Be");

		//if(this.name.contains("_")) this.name.replace("_", "__");
		if(s.contains("_")) {
			System.out.println("contain _");
			s = s.replace("_", "__");
			System.out.println(s);
		}
		checkColumn.add(s);
		String column = "prov_public_" + this.name + "_" + s;
		//checkColumns.add(column);
		//System.out.println("ADD Af");
	}

	public void addCheckColumns(String s) {
		checkColumns.add(s);
	}


	public String getTableName() {
		return name;
	}

	public ArrayList<String> getCheckColumn() {
		return checkColumn;
	}

	public ArrayList<String> getCheckColumns(){
		return checkColumns;
	}

	public ArrayList<ArrayList<String>> getchecks(){
		return checks;
	}

	public void addNewCheck(String s, String c) {
		ArrayList<String> n = new ArrayList<String>();
		if(s.contains("_")) {
			System.out.println("contain _");
			s = s.replace("_", "__");
			System.out.println(s);
		}
		n.add(s);
		n.add("prov_public_" + name + "_" + s);
		checks.add(n);
	}

	public int checksIndex(String s) {
		for(int i = 0; i < checks.size(); i++) {
			if(s.equals(checks.get(i).get(0))) {
				return i;
			}
		}
		return -1;
	}

	public int checksIndex2(String s) {
		for(int i = 0; i < checkColumn.size(); i++) {
			if(s.equals(checkColumn.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public void addCheck(int x, String s) {
		checks.get(x).add(s);
	}

	public ArrayList<ArrayList<String>> getChecks(){
		return checks;
	}
}
