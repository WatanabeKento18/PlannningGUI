import java.io.*;
import java.util.*;

/**
 * 		ファイルの入出力操作
 **/

class FileManager {

	// 指定のファイルに状態を書き出す
	public static void saveState(File file, Vector<String> state) {
		try {
			FileWriter fw = new FileWriter(file); // 書き込む準備
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0 ; i < state.size() ; i++ ) { // 状態すべてに対して順番に処理をおこなう
				bw.write(state.elementAt(i)); // 状態出力
				if( i < state.size()-1 ) bw.newLine();	//改行
			}
			bw.close(); // 使ったらしまう
		} catch (IOException e) { // 例外処理
			System.out.println(e); // エラーメッセージ表示
		}
	}

	// 指定のファイルから状態を読み込む
	public static Vector<String> loadState(File file) {
		Vector<String> state = new Vector<String>();
		try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文
			// 文字コードを指定してBufferedReaderオブジェクトを作る
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.getCanonicalPath()), "UTF-8"));

			// 変数lineに1行ずつ読み込むfor文
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				state.addElement(line);	//1行ずつ追加
			}
		} catch (IOException e) {
			e.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
		}
		return state;
	}
}