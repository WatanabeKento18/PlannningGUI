import java.awt.*;

import javax.swing.*;

import java.util.*;

/**
 *		プランニングをおこなうGUI 
 **/

//プランニングの様子をGUIで表示するためのクラス
public class PlanningGUI{
	Vector<Operator> operators;
	
	//単独で動作させるためのmainメソッド
	public static void main(String args[]){
		(new PlanningGUI()).display();	//表示
	}
	
	//フレームを生成し表示する
	public void display(){
		PlanningFrame pf = new PlanningFrame();
		pf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// ウィンドウを閉じる動作の登録
		pf.setBackground(Color.blue);	//背景色を設定
		pf.setTitle("Planning GUI");		//タイトルを設定
		pf.setBounds(100, 30, 800, 600); // ウインドウの大きさを指定する
		pf.setVisible(true); // ウインドウを表示
		pf.setTableState(initInitialState());	//初期状態設定
		initOperators();
		pf.setOperators(operators);
		pf.startThread();	//処理開始
	}
	
	//外部から命令を与える
	public void sendCommand(Operator op){
		System.out.println("TEST");
	}

	private Vector<String> initInitialState() {
		Vector<String> initialState = new Vector<String>();
		
		Random rnd = new Random();
		// 図形の個数の設定。以下の数字を変えることで図形の個数変更可能
		int Triangle = 2;
		int halfBlock = 2;
		//int Square = 4;

		ArrayList<Integer> check = new ArrayList<Integer>();
		ArrayList<Integer> check1 = new ArrayList<Integer>();
		String Name[];
		Name = new String[9]; // あらかじめ操作しやすいように名前や色を配列にしておく
		Name[1] = "A";
		Name[2] = "B";
		Name[3] = "C";
		Name[4] = "D";
		Name[5] = "E";
		Name[6] = "F";
		Name[7] = "G";
		Name[8] = "H";
		String Color[];
		Color = new String[9];
		Color[1] = "red";
		Color[2] = "green";
		Color[3] = "blue";
		Color[4] = "white";
		Color[5] = "pink";
		Color[6] = "purple";
		Color[7] = "orange";
		Color[8] = "yellow";

		initialState.addElement("clear A");
		initialState.addElement("clear B");
		initialState.addElement("clear C");
		initialState.addElement("clear D");
		initialState.addElement("clear E");
		initialState.addElement("clear F");
		initialState.addElement("clear G");
		initialState.addElement("clear H");

		// 全てのオブジェクトのステータスを決めるまでループ
		// 八色 red yellow green blue orange white pink purple
		int temp = 0;
		int temp1 = 0;
		for (int i = 1; i < 9; i++) {

			while (true) {
				int ran = rnd.nextInt(8) + 1; // 1~8乱数生成
				if (!check.contains(ran)) { // 今までに生成したことのない乱数だった場合
					temp = ran; // 決定
					check.add(ran);
					break;
				}
			}
			while (true) {
				int ran2 = rnd.nextInt(8) + 1; // 1~8乱数生成
				if (!check1.contains(ran2)) { // 今までに生成したことのない乱数だった場合
					temp1 = ran2; // 決定
					check1.add(ran2);
					break;
				}
			}
			//System.out.println("temp1 =" + temp1);
			if (temp1 <= Triangle) {
				initialState.addElement(Name[i] + " is-a " + "Triangle"
						+ " shape");
			} else if (temp1 <= (Triangle + halfBlock)) {
				initialState.addElement(Name[i] + " is-a " + "halfBlock"
						+ " shape");
				initialState.addElement("placeable " + Name[i]);
			} else {
				initialState.addElement(Name[i] + " is-a " + "Square"
						+ " shape");
				initialState.addElement("placeable " + Name[i]);
			}
			initialState.addElement(Name[i] + " is-a " + Color[temp] + " color");
		}

		initialState.addElement("ontable A");
		initialState.addElement("ontable B");
		initialState.addElement("ontable C");
		initialState.addElement("ontable D");
		initialState.addElement("ontable E");
		initialState.addElement("ontable F");
		initialState.addElement("ontable G");
		initialState.addElement("ontable H");

		initialState.addElement("handEmpty");
		
		return initialState;
	}
	
	public void initOperators() {
		operators = new Vector<Operator>();

		// OPERATOR 1
		// / NAME
		String name1 = new String("place ?x on ?y");
		// / IF
		Vector<String> ifList1 = new Vector<String>();
		ifList1.addElement(new String("clear ?y"));
		ifList1.addElement(new String("holding ?x"));
		ifList1.addElement(new String("placeable ?y"));
		// / ADD-LIST
		Vector<String> addList1 = new Vector<String>();
		addList1.addElement(new String("?x on ?y"));
		addList1.addElement(new String("clear ?x"));
		addList1.addElement(new String("handEmpty"));
		// / DELETE-LIST
		Vector<String> deleteList1 = new Vector<String>();
		deleteList1.addElement(new String("clear ?y"));
		deleteList1.addElement(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1);
		operators.addElement(operator1);

		// OPERATOR 2
		// / NAME
		String name2 = new String("remove ?x from on top ?y");
		// / IF
		Vector<String> ifList2 = new Vector<String>();
		ifList2.addElement(new String("?x on ?y"));
		ifList2.addElement(new String("clear ?x"));
		ifList2.addElement(new String("handEmpty"));
		// / ADD-LIST
		Vector<String> addList2 = new Vector<String>();
		addList2.addElement(new String("clear ?y"));
		addList2.addElement(new String("holding ?x"));
		// / DELETE-LIST
		Vector<String> deleteList2 = new Vector<String>();
		deleteList2.addElement(new String("?x on ?y"));
		deleteList2.addElement(new String("clear ?x"));
		deleteList2.addElement(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2);
		operators.addElement(operator2);

		// OPERATOR 3
		// / NAME
		String name3 = new String("pick up ?x from the table");
		// / IF
		Vector<String> ifList3 = new Vector<String>();
		ifList3.addElement(new String("ontable ?x"));
		ifList3.addElement(new String("clear ?x"));
		ifList3.addElement(new String("handEmpty"));
		// / ADD-LIST
		Vector<String> addList3 = new Vector<String>();
		addList3.addElement(new String("holding ?x"));
		// / DELETE-LIST
		Vector<String> deleteList3 = new Vector<String>();
		deleteList3.addElement(new String("ontable ?x"));
		deleteList3.addElement(new String("clear ?x"));
		deleteList3.addElement(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3);
		operators.addElement(operator3);

		// OPERATOR 4
		// / NAME
		String name4 = new String("put ?x down on the table");
		// / IF
		Vector<String> ifList4 = new Vector<String>();
		ifList4.addElement(new String("holding ?x"));
		// / ADD-LIST
		Vector<String> addList4 = new Vector<String>();
		addList4.addElement(new String("ontable ?x"));
		addList4.addElement(new String("clear ?x"));
		addList4.addElement(new String("handEmpty"));
		// / DELETE-LIST
		Vector<String> deleteList4 = new Vector<String>();
		deleteList4.addElement(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4);
		operators.addElement(operator4);
	}

}


/*参考
http://msyk.net/keio/JavaBook/eclipse-indigo/ch13.html
http://www.clas.kitasato-u.ac.jp/~yoshida/kouki/jyoho_b67.html
*/
