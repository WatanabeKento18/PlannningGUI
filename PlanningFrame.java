import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 		ウィンドウを表示するためのクラス
 **/

//フレームのクラス
class PlanningFrame extends JFrame implements Runnable , ActionListener , ChangeListener , MouseListener{
	private static final long serialVersionUID = 1L;	//シリアルバージョン
	private final String objectTag[] = {"A","B","C","D","E","F","G","H"};	//オブジェクトのタグ一覧
	
	Thread frameThread;	//並列処理のためのスレッド
	int threadCounter;	//カウンタ
	TableManager tableManager;	//テーブルの上がどうなっているか
	Vector<Operator> operators;					//動作一覧
	Translation translator;	//自然言語処理用クラス
	
	//コンストラクタ
	PlanningFrame(){
		tableManager = new TableManager();
		initMenu();		//メニュー初期化
		tableManager.initObject();	//画像とオブジェクトの初期化
		getContentPane().add(tableManager.getImagePanel());	//画像描画クラスを登録
		addMouseListener(this);	//マウス入力受付
		setResizable(false);	//サイズ変更不可に設定
		tableManager.setAnimationSpeed(3);	//デフォルトアニメーション速度
		translator = new Translation();	//自然言語処理用クラス初期化
	}
	
	//スレッド処理開始
	public void startThread(){
		frameThread = new Thread(this);      //このコンポーネントを管理するスレッドを生成
		frameThread.start();			      //スレッドを開始する
		tableManager.setAnimationEnable(true);	//アニメーションを有効にする
	}
	
	//状態を取得しテーブルに反映
	public void setTableState(Vector<String> state){
		if(state == null) return;	//エラー
		tableManager.initTableObject();	//初期化
		//テーブルの上にあるものを確認
		for(Iterator<String> it = state.iterator() ; it.hasNext() ;){
			String stateData = it.next();		//状態Vectorから次のデータを取得
			Unifier uni = new Unifier();		//マッチング用Unifierクラス
			if(uni.unify(stateData , "ontable ?object")){		//テーブル上にあるという情報を得る
				String objectName = (String)uni.getVarBindings().get("?object");	//テーブルの上にあるオブジェクトの名前を取得
				tableManager.putObjectOnTable(objectName);
			}else if(uni.unify(stateData , "?object1 on ?object2")){		//オブジェクト上にあるという情報を得る
				String objectName1 = (String)uni.getVarBindings().get("?object1");	//上にあるオブジェクトの名前を取得
				String objectName2 = (String)uni.getVarBindings().get("?object2");	//下にあるオブジェクトの名前を取得
				tableManager.putObjectOnObject(objectName1, objectName2);
			}else if(uni.unify(stateData , "holding ?object")){		//手に持っている情報を得る
				tableManager.setHoldingObject((String)uni.getVarBindings().get("?object"));	//手に持っているオブジェクトの名前を取得し代入
			}else if(uni.unify(stateData , "?object is-a ?shape shape")){		//形情報を得る
				tableManager.setObjectShape((String)uni.getVarBindings().get("?object"), (String)uni.getVarBindings().get("?shape"));
			}else if(uni.unify(stateData , "?object is-a ?color color")){		//色情報を得る
				tableManager.setObjectColor((String)uni.getVarBindings().get("?object"), (String)uni.getVarBindings().get("?color"));
			}else if(uni.unify(stateData , "?object is-a ?tag")){		//タグ情報を得る
				tableManager.getObject((String)uni.getVarBindings().get("?object")).setTag((String)uni.getVarBindings().get("?tag"));
			}
		}
		tableManager.setState(state);	//状態を反映
		tableManager.initObjectImage();	//画像を読み込みなおす
		tableManager.setAnimationEnable(true);	//アニメーション開始
	}
		
	//命令を送信(返り値 : [COMMAND_OK :命令を実行] [COMMAND_FAULT :命令を実行できる状態ではない] [COMMAND_MISSING :命令が不正])
	//command : 命令文
	enum Command_Result{ COMMAND_OK , COMMAND_FAULT , COMMAND_MISSING};	//返り値用定数宣言
	public Command_Result sendCommand(String command){
		for( int i = 0 ; i < operators.size() ; i++ ){
			Unifier uni = new Unifier();	//マッチング用Unifierクラス
			Operator op = operators.get(i);	//オペレータを取得
			if(uni.unify(op.getName(), command)){	//マッチングして確認
				Operator newOp = op.instantiate(uni.getVarBindings());	//マッチング後のオペレータ
				if(tableManager.isActOperator(newOp.getIfList())){	//行動できる状態の場合
					tableManager.applyOperator(newOp);				//状態を更新
					if(op.getAddList().contains("holding ?x")){	//オブジェクトを取る場合
						pickUpObject(uni.getVarBindings().get("?x"));
					}else if(op.getName().equals("Place ?x on ?y")){	//オブジェクトをオブジェクトに置く場合
						putDownObject(uni.getVarBindings().get("?x") , uni.getVarBindings().get("?y"));
					}else if(op.getName().equals("put ?x down on the table")){	//オブジェクトをテーブルに置く場合
						putDownObject(uni.getVarBindings().get("?x") , "Table");
					}
					System.out.println(newOp.toString());
					return Command_Result.COMMAND_OK;	//成功
				}
				return Command_Result.COMMAND_FAULT;	//失敗
			}
		}
		return Command_Result.COMMAND_MISSING;	//命令に合うオペレータが無い場合
	}

	//指定のオブジェクトを取る
	void pickUpObject(String pickUpObjName){
		tableManager.pickUpObject(pickUpObjName);
	}
	
	//指定のオブジェクトを置く
	void putDownObject(String putDownObjName , String place){
		if(place.equals("Table")){	//テーブルの上の場合
			tableManager.putObjectOnTable(putDownObjName);
		}else{	//オブジェクトの上の場合
			tableManager.putObjectOnObject(putDownObjName, place);
		}
	}
	
	int i = 0;
	
	//スレッドで動作をおこなう
	boolean movingFlag = false;	//アニメーション中の確認
	public void run(){
		//スレッド用メインループ
		while(frameThread!=null){
			//動作制御のための構文
			try{
				Thread.sleep(16);	//60フレームで動作させるため1000ms/60だけ待つ
			}catch(InterruptedException e){	//sleepメソッド用try-catch構文
				System.out.println(e);	//エラー
			}
			
			/**
			 * メニュー状態の設定をおこなう
			**/
			//アニメーションの切り替え
			if(movingFlag != tableManager.isMoving()){
				//切替
				movingFlag = tableManager.isMoving();
				//メニュー
				menu[1].setEnabled(!movingFlag);
				menuitem[0][0].setEnabled(!movingFlag);
				menuitem[0][1].setEnabled(!movingFlag);
				//モード表示切替
				if(movingFlag){	//動作中
					tableManager.addImage("img/mode2.png","Mode");	//位置編集モード画像設定
					tableManager.setImagePosition("Mode", 560, 10);	//モード表示位置設定
				}else{	//動作終了
					if(objectEditMode){
						tableManager.addImage("img/mode1.png","Mode");	//位置編集モード画像設定
						tableManager.setImagePosition("Mode", 560, 10);	//モード表示位置設定
					}else{
						tableManager.setImageDisable("Mode");	//非表示
					}
				}
			}
			
			/**
			 * 描画位置の設定をおこなう
			**/
			//オブジェクト位置編集時のカーソル座標
			if(selectedObject != null){
				int x = selectedObject.getPosition().x;
				int y = selectedObject.getPosition().y;
				tableManager.setImagePosition("Selector", x , y);
				if(threadCounter/10 %2 == 0) tableManager.setImageDisable("Selector");
			}else{
				tableManager.setImageDisable("Selector");
			}
			tableManager.update();
			
			repaint();	//再描画
			threadCounter++;	//カウンタ加算
		}
	}

	//ボタンを押したときの動作を定義
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		//ボタンの種類によって分岐
		if(actionCommand.equals("命令を入力")){
			commandButton();
		}else if(actionCommand.equals("オブジェクトを取る")){
			pickUpObjectButton();
		}else if(actionCommand.equals("オブジェクトをオブジェクトに置く")){
			String putObjName = JOptionPane.showInputDialog(this, "何の上に置けば良いですか？");
			BlockManager putObj = tableManager.getObject(putObjName);
			if(putObj != null){
				if(sendCommand("Place " + tableManager.getHoldingObjectName() +" on " + putObj.getName()) == Command_Result.COMMAND_FAULT){
					JOptionPane.showMessageDialog(this, "その命令は今はできません…(´･ω･｀)");	//失敗したことを表示
				}
			}else{
				JOptionPane.showMessageDialog(this, "そのオブジェクトは存在しません…(´･ω･｀)");	//失敗したことを表示
			}
		}else if(actionCommand.equals("オブジェクトをテーブルに置く")){
			sendCommand("put " + tableManager.getHoldingObjectName() +" down on the table");
		}else if(actionCommand.equals("状態を開く")){
			stateSetting(false);
		}else if(actionCommand.equals("状態を保存")){
			stateSetting(true);
		}else if(actionCommand.equals("閉じる")){
			this.dispose();	//ウィンドウの破棄
			System.exit(0);	//プログラムの終了
		}else if(actionCommand.equals("全オブジェクトをテーブルへ")){
			tableManager.releaseHoldingObject();
			setTableState(tableManager.getAllObjectToTableState());
		}else if(actionCommand.equals("オブジェクト位置編集モード")){
			menuitem[1][1].setText("オブジェクト閲覧モード");
			selectedObject = null;	//選択中オブジェクトを消去
			objectEditMode = true;	//オブジェクト編集モードへ
			tableManager.addImage("img/mode1.png","Mode");	//位置編集モード画像設定
			tableManager.setImagePosition("Mode", 560, 10);	//モード表示位置設定
			JOptionPane.showMessageDialog(this, "移動するオブジェクトをクリックして選択します。\n次にオブジェクトを配置したい場所の下をクリックします。");	
		}else if(actionCommand.equals("オブジェクト閲覧モード")){
			menuitem[1][1].setText("オブジェクト位置編集モード");
			selectedObject = null;	//選択中オブジェクトを消去
			objectEditMode = false;	//オブジェクト編集モード終了
			tableManager.setImageDisable("Mode");	//モード表示消去
		}else if(actionCommand.equals("オブジェクトエディタ")){
			ObjectEditor oe = new ObjectEditor(this);	//フレーム作成
			oe.setTitle("オブジェクトエディタ");		//タイトルを設定
			oe.setBounds(600, 30, 400, 460); // ウインドウの大きさを指定する
			oe.setVisible(true); // ウインドウを表示
		}else if(actionCommand.equals("状態エディタ")){
			StateEditor se = new StateEditor(this);	//フレーム作成
			se.setTitle("状態エディタ");		//タイトルを設定
			se.setBounds(600, 30, 400, 440); // ウインドウの大きさを指定する
			se.setVisible(true); // ウインドウを表示
		}else if(actionCommand.equals("プランニングの設定")){
			PlanningEditor pe = new PlanningEditor(this);	//フレーム作成
			pe.setTitle("プランニングの設定");		//タイトルを設定
			pe.setBounds(600, 30, 580, 440); // ウインドウの大きさを指定する
			pe.setVisible(true); // ウインドウを表示
		}else if(actionCommand.equals("プランニングの使い方")){
			JOptionPane.showMessageDialog(this, "[編集]→[オブジェクトエディタ]で使用するオブジェクトの個数や見た目,形状を設定します.\n次に[編集]→[オブジェクト位置編集モード]または[編集]→[状態エディタ]を使ってプランニングの初期状態を作成します.\n状態エディタで目標状態を作成することもできます.");
			JOptionPane.showMessageDialog(this,"最後に[プランニング]→[プランニングの設定]からプランニングの設定画面を出します.\nここで先ほど作成した初期状態を選択し(現在のテーブルの状態が自動で選択されています),目標状態を入力します.\n目標状態を状態エディタで作成した場合は,ラジオボタンで選ぶだけで生成されます.");
			JOptionPane.showMessageDialog(this,"作成していない場合はテキストフィールドに目標状態を記述し,目標状態追加ボタンから1つずつ追加していきます.\n初期状態と目標状態が完成したらプランニングボタンを押します.プランの欄に初期状態から目標状態への必要な操作が表示されます.\nここでアニメーションを実行ボタンを押すことで最初の画面で手が動き,目標状態への操作が視覚的に示されます.");
		}else if(actionCommand.equals("バージョン情報")){
			JOptionPane.showMessageDialog(this,"Planning GUI ver.0119\n\nGUI制作 : 渡邉 賢人\n自然言語処理 : 石田 雄登\n音声認識処理 : 稲本 琢磨\n画像素材制作 : 渡邉 賢人");
		}
	}
	
	//状態セーブorロードをおこなう(isSave : セーブする場合はtrue,ロードの場合はfalse)
	public void stateSetting(boolean isSave){
		int selected;	//選択状態を記憶する変数
		JFileChooser fileChooser = new JFileChooser();	//ファイル選択ダイアログ
		FileNameExtensionFilter filter = new FileNameExtensionFilter("テキストファイル", "txt", "dat");	//テキストファイル用フィルタ
		fileChooser.addChoosableFileFilter(filter);
		if(isSave) selected = fileChooser.showSaveDialog(this);	//ダイアログ表示
		else selected = fileChooser.showOpenDialog(this);	//ダイアログ表示
		if (selected == JFileChooser.APPROVE_OPTION) {		//選択された場合
			File file = fileChooser.getSelectedFile();		//選択ファイルを開く
			if(isSave) FileManager.saveState(file, tableManager.stateToVector());	//保存
			else setTableState(FileManager.loadState(file));		//開く
		}
	}
	
	//命令ボタンの動作
	void commandButton(){
		String command = JOptionPane.showInputDialog(this, "何をすれば良いですか？");	//命令受け取り
		if(command != null){	//命令取り消しではない場合
			for(int i = 0 ; i < 8 ; i++){	//自然言語処理クラスにオブジェクトタグを設定
				String tag = tableManager.getObject(objectTag[i]).getTag();	//タグ取得
				if(tag == null || tag.equals("")) translator.setUsername(i, "dammy");	//タグが無い場合
				else translator.setUsername(i, tag);	//設定
			}
			String translatedCommand = translator.translation(command);	//変換後の命令
			Command_Result result;
			if(translatedCommand != null) result = sendCommand(translatedCommand);	//変換に成功した場合は変換コマンドを送信
			else result = sendCommand(command);	//変換に失敗した場合はそのまま送信
			if(result.equals(Command_Result.COMMAND_FAULT)){	//命令を送信し成功したかを確認
				JOptionPane.showMessageDialog(this, "その命令は今はできません…(´･ω･｀)");	//失敗したことを表示
			}else if(result.equals(Command_Result.COMMAND_MISSING)){
				
				
				if(result.equals(Command_Result.COMMAND_FAULT)){	//命令を送信し成功したかを確認
					JOptionPane.showMessageDialog(this, "その命令は今はできません…(´･ω･｀)");	//失敗したことを表示
				}else if(result.equals(Command_Result.COMMAND_MISSING)){
					JOptionPane.showMessageDialog(this, "その命令は私にはわかりません…(´･ω･｀)");	//失敗したことを表示
				}
			}
		}
	}
	
	//オブジェクト取るボタンの動作
	void pickUpObjectButton(){
		String pickUpObjName = JOptionPane.showInputDialog(this, "何を取れば良いですか。");
		if(pickUpObjName != null){	//入力がある場合
			BlockManager pickUpObj = tableManager.getObject(pickUpObjName);	//名前からオブジェクトを取得 
			if(pickUpObj != null){	//存在するオブジェクト名の場合
				if(tableManager.isActOperator("ontable " + pickUpObj.getName())){	//テーブル上にある場合
					sendCommand("pick up " + pickUpObj.getName() +" from the table");	//命令を送信
				}else{	//オブジェクト上にある場合
					BlockManager underObj = pickUpObj.getUnderBlock();	//上から2番目のオブジェクトを取得
					if(underObj != null) sendCommand("remove " + pickUpObj.getName() +"  from on top " + underObj.getName());	//命令を送信
				}
			}
		}
	}

	/**
	 *	マウス操作用 
	**/
	boolean objectEditMode;	//オブジェクト位置編集モード
	BlockManager selectedObject; // 選択中オブジェクト

	// マウスクリック時の動作
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e){
		// 閲覧モード
		if (!objectEditMode || tableManager.isMoving()) {
			BlockManager obj = tableManager.getObjectByPosition(e.getX(),e.getY()); // 座標からオブジェクトを取得
			if (obj != null) { // 存在する場合
				JOptionPane.showMessageDialog(this, obj.getGuide());
			}
			// System.out.println(e.getX() + " : " + e.getY());
		} else { // 編集モード
			if (selectedObject == null) { // オブジェクトが選択されていない場合
				selectedObject = tableManager.getObjectByPosition(e.getX(),e.getY()); // 座標からオブジェクトを取得
				if(selectedObject != null){
					if(selectedObject.getShapeName().equals("halfBlock")){	//ブロックの形に合わせて選択カーソル画像変更
						tableManager.addImage("img/selector2.png", "Selector");
					}else{
						tableManager.addImage("img/selector1.png", "Selector");
					}
				}
			} else {
				tableManager.setObjectByPosition(selectedObject, e.getX(),e.getY()); // 座標とオブジェクトを指定して配置
				selectedObject = null; // 選択解除
			}
		}
	}
	public void mouseReleased(MouseEvent e)  {      }
	public void mouseEntered(MouseEvent e)  {	}
	public void mouseExited(MouseEvent e)  {    }
	
	//使用するオペレータを登録
	public void setOperators(Vector<Operator> opSet){
		operators = opSet;
	}
	
	//使用するメニューバー生成
	JMenu menu[];
	JMenuItem menuitem[][];
	JRadioButtonMenuItem radiomenuitem[];
	void initMenu(){
		//メニューバー
		JMenuBar menubar = new JMenuBar();
		
		//メインメニュー
		menu = new JMenu[6];
		menu[0] = new JMenu("ファイル(F)");
		menu[0].setMnemonic(KeyEvent.VK_F);
		menu[1] = new JMenu("編集(E)");
		menu[1].setMnemonic(KeyEvent.VK_E);
		menu[2] = new JMenu("表示(D)");
		menu[2].setMnemonic(KeyEvent.VK_D);
		menu[3] = new JMenu("命令(C)");
		menu[3].setMnemonic(KeyEvent.VK_C);
		menu[4] = new JMenu("プランニング(P)");
		menu[4].setMnemonic(KeyEvent.VK_P);
		menu[5] = new JMenu("ヘルプ(H)");
		menu[5].setMnemonic(KeyEvent.VK_H);

		//メインメニューをメニューバーに登録
		for(int i = 0 ; i < 6 ; i++){
			menubar.add(menu[i]);
		}
		
		//サブメニュー
		menuitem = new JMenuItem[6][4];

		menuitem[0][0] = new JMenuItem("状態を開く");
		menuitem[0][1] = new JMenuItem("状態を保存");
		menuitem[0][2] = new JMenuItem("閉じる");

		menu[0].add(menuitem[0][0]);
		menu[0].add(menuitem[0][1]);
		menu[0].addSeparator();
		menu[0].add(menuitem[0][2]);
		
		menuitem[1][0] = new JMenuItem("全オブジェクトをテーブルへ");
		menuitem[1][1] = new JMenuItem("オブジェクト位置編集モード");
		menuitem[1][2] = new JMenuItem("オブジェクトエディタ");
		menuitem[1][3] = new JMenuItem("状態エディタ");

		menu[1].add(menuitem[1][0]);
		menu[1].add(menuitem[1][1]);
		menu[1].addSeparator();
		menu[1].add(menuitem[1][2]);
		menu[1].add(menuitem[1][3]);

		JMenu menulist1 = new JMenu("アニメーション");
		JMenu menulist2 = new JMenu("背景画像");
		JMenu menulist3 = new JMenu("テーブル画像");

		menu[2].add(menulist1);
		menu[2].add(menulist2);
		menu[2].add(menulist3);
		
		menuitem[3][0] = new JMenuItem("命令を入力");
		menuitem[3][1] = new JMenuItem("オブジェクトを取る");
		menuitem[3][2] = new JMenuItem("オブジェクトをオブジェクトに置く");
		menuitem[3][3] = new JMenuItem("オブジェクトをテーブルに置く");

		menu[3].add(menuitem[3][0]);
		menu[3].addSeparator();
		menu[3].add(menuitem[3][1]);
		menu[3].add(menuitem[3][2]);
		menu[3].add(menuitem[3][3]);
		
		menuitem[4][0] = new JMenuItem("プランニングの設定");
		menu[4].add(menuitem[4][0]);
		
		menuitem[5][0] = new JMenuItem("プランニングの使い方");
		menu[5].add(menuitem[5][0]);
		menu[5].addSeparator();
		menuitem[5][1] = new JMenuItem("バージョン情報");
		menu[5].add(menuitem[5][1]);
		
		//ラジオボタン
		radiomenuitem = new JRadioButtonMenuItem[17]; 
		radiomenuitem[0] = new JRadioButtonMenuItem("遅い");
		radiomenuitem[1] = new JRadioButtonMenuItem("普通");
		radiomenuitem[2] = new JRadioButtonMenuItem("速い");
		radiomenuitem[3] = new JRadioButtonMenuItem("とても速い");
		radiomenuitem[4] = new JRadioButtonMenuItem("アニメーションOFF");
		radiomenuitem[5] = new JRadioButtonMenuItem("画像無し");
		radiomenuitem[6] = new JRadioButtonMenuItem("深海");
		radiomenuitem[7] = new JRadioButtonMenuItem("石版");
		radiomenuitem[8] = new JRadioButtonMenuItem("ポップ");
		radiomenuitem[9] = new JRadioButtonMenuItem("メルヘン");
		radiomenuitem[10] = new JRadioButtonMenuItem("幻想");
		radiomenuitem[11] = new JRadioButtonMenuItem("木");
		radiomenuitem[12] = new JRadioButtonMenuItem("レンガ");
		radiomenuitem[13] = new JRadioButtonMenuItem("鉄");
		radiomenuitem[14] = new JRadioButtonMenuItem("ガラス");
		radiomenuitem[15] = new JRadioButtonMenuItem("土");
		radiomenuitem[16] = new JRadioButtonMenuItem("クリスタル");
		radiomenuitem[1].setSelected(true);
		radiomenuitem[9].setSelected(true);
		radiomenuitem[12].setSelected(true);

		//ラジオボタン用グループ
		ButtonGroup group1 = new ButtonGroup();
		for(int i = 0 ; i < 5 ; i++){
			group1.add(radiomenuitem[i]);
			menulist1.add(radiomenuitem[i]);
		}
		ButtonGroup group2 = new ButtonGroup();
		for(int i = 5 ; i < 11 ; i++){
			group2.add(radiomenuitem[i]);
			menulist2.add(radiomenuitem[i]);
			if(i == 5) menulist2.addSeparator();
		}
		ButtonGroup group3 = new ButtonGroup();
		for(int i = 11 ; i < 17 ; i++){
			group3.add(radiomenuitem[i]);
			menulist3.add(radiomenuitem[i]);
		}
		for(int i = 0 ; i < 17 ; i++){	//ChangeListener用
			radiomenuitem[i].addChangeListener(this);
		}
		
		//操作を受け付けるようにする
		for(int i = 0 ; i < 6 ; i++){
			for(int j = 0 ; j < 4 ; j++){
				if(menuitem[i][j] != null) menuitem[i][j].addActionListener(this);
			}
		}
		
		//メニューバーをフレームに登録
		setJMenuBar(menubar);
	}
	
	//状態変更
	int backImageNumber = 4;
	int tableImageNumber = 2;
	public void stateChanged(ChangeEvent e) {
		//アニメーション速度
		for(int i = 0 ; i < 5 ; i++){
			if(radiomenuitem[i].isSelected()){
				int speed = i*3+1;
				if(i == 3) speed+=3;
				if(i == 4) speed = 0;
				tableManager.setAnimationSpeed(speed);
				break;
			}
		}
		//背景画像
		if(radiomenuitem[5].isSelected()){
			tableManager.disableImage("Back");
			backImageNumber = 0;
		}else{
			tableManager.setImagePosition("Back" , 0 , 0);	//座標をセットし画像を表示
			for(int i = 1 ; i <= 5 ; i++){
				if(radiomenuitem[5 + i].isSelected()){
					if(backImageNumber != i) tableManager.addImage("img/back" + i +".png","Back");
					backImageNumber = i;
					break;
				}
			}
		}
		//テーブル画像
		tableManager.setImagePosition("Table", 50, 500); // 座標をセットし画像を表示
		for (int i = 1; i <= 6; i++) {
			if (radiomenuitem[10 + i].isSelected()) {
				if (tableImageNumber != i)
					tableManager.addImage("img/table" + i + ".png", "Table");
				tableImageNumber = i;
				break;
			}
		}
	}
	
	//テーブル管理クラスを取得
	public TableManager getTableManager(){
		return tableManager;
	}
}
