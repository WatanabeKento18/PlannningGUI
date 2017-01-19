import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *		状態エディタ用フレーム 
 **/

class StateEditor extends JFrame implements ActionListener , ChangeListener , ListSelectionListener{
	private static final long serialVersionUID = 1L;
	PlanningFrame pf;	//呼び出したPlanningFrame
	Vector<String> editState;	//編集用状態Vector
	int editStateNumber;		//編集用状態番号
	JButton button[];			//各種ボタン
	DefaultListModel<String> stateModel;		//状態編集用モデル 
	JList<String> stateList;			//状態一覧表示用リスト
	JRadioButton radiobutton[];	//状態選択ラジオボタン
	JTextField textfield;		//状態入力用フィールド
	
	public StateEditor(PlanningFrame frame){
		pf = frame;
		initMenu();
		setResizable(false);	//サイズ変更不可に設定
		editStateNumber = pf.getTableManager().getStateNumber();
		getTableState();
	}
	
	//メニュー作成
	void initMenu(){
		
		JPanel p = new JPanel();
		p.setLayout(null);
		
		stateModel = new DefaultListModel<String>();
		stateList = new JList<String>(stateModel);
		stateList.addListSelectionListener(this);
		JScrollPane scroll = new JScrollPane(stateList);
		getContentPane().add(scroll);
		scroll.setBounds(170, 10, 200, 300);
		p.add(scroll);
		
		textfield = new JTextField();
		textfield.setBounds(10, 320, 190, 30);
		p.add(textfield);
		
		JLabel stateLabel = new JLabel("編集する状態");
		stateLabel.setBounds(50,10,80,30);
		p.add(stateLabel);
		
		radiobutton = new JRadioButton[3];
		radiobutton[0] = new JRadioButton("状態1");
		radiobutton[0].setBounds(50, 40, 80, 30);
		radiobutton[1] = new JRadioButton("状態2");
		radiobutton[1].setBounds(50, 65, 80, 30);
		radiobutton[2] = new JRadioButton("状態3");
		radiobutton[2].setBounds(50, 90, 80, 30);
		ButtonGroup group = new ButtonGroup();
		for(int i = 0 ; i < 3 ; i++){
			group.add(radiobutton[i]);
			radiobutton[i].addChangeListener(this);
			radiobutton[i].setBackground(Color.WHITE);
			p.add(radiobutton[i]);
			if(editStateNumber == i) radiobutton[i].setSelected(true);	//最初の状態番号に設定
		}
		
		button = new JButton[10];
		button[0] = new JButton("状態のロード");
		button[0].setBounds(10, 360, 150, 30);
		button[1] = new JButton("状態のセーブ");
		button[1].setBounds(170, 360, 150, 30);
		button[2] = new JButton("追加");
		button[2].setBounds(200, 320, 60, 30);
		button[3] = new JButton("置換");
		button[3].setBounds(260, 320, 60, 30);
		button[4] = new JButton("削除");
		button[4].setBounds(320, 320, 60, 30);
		button[5] = new JButton("テーブル状態を取得");
		button[5].setBounds(5, 135, 160, 40);
		button[6] = new JButton("テーブル状態に設定");
		button[6].setBounds(5, 185, 160, 40);
		button[7] = new JButton("オブジェクト名統一");
		button[7].setBounds(5, 240, 160, 30);
		button[8] = new JButton("エラーチェック");
		button[8].setBounds(5, 280, 160, 30);
		button[9] = new JButton("？");
		button[9].setBounds(340, 360, 50, 30);
		
		for(int i = 0 ; i < 10 ; i++){
			p.add(button[i]);	//パネルに追加
			button[i].addActionListener(this);	//行動受付
		}
		
		getContentPane().add(p , BorderLayout.CENTER);
	}
	
	//状態リストの作成
	void initStateList(){
		stateModel.clear();		//全消去
		for(int i = 0 ; i < editState.size() ; i++ ){
			stateModel.addElement(editState.elementAt(i));
		}
	}
	
	//テーブルの状態を受け取る
	void getTableState(){
		editState = pf.getTableManager().getState();	//状態を受け取る
		initStateList();	//リストに反映
		//System.out.println(editState + ":" + editStateNumber);
	}
	
	//テーブルの状態に反映させる
	void setTableState(){
		pf.setTableState(editState);	//状態反映
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
			if(isSave) FileManager.saveState(file, editState);	//保存
			else{
				editState = FileManager.loadState(file);	//開く
				initStateList();	//リスト更新
			}
		}
	}
	
	//入力受付
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		//ボタンの種類によって分岐
		if(actionCommand.equals("状態のロード")){
			stateSetting(false);
		}else if(actionCommand.equals("状態のセーブ")){
			stateSetting(true);
		}else if(actionCommand.equals("追加")){
			String text = textfield.getText();
			if(!text.equals("")){
				editState.addElement(text);	//要素追加
				stateModel.addElement(text);	//リストにも追加
				textfield.setText("");	//入力欄初期化
			}
		}else if(actionCommand.equals("削除")){
			int index = stateList.getSelectedIndex();	//選択中の要素番号
			if(index != -1){
				editState.remove(index);	//要素削除
				stateModel.remove(index);	//リストも削除
			}
		}else if(actionCommand.equals("置換")){
			int index = stateList.getSelectedIndex();	//選択中の要素番号
			if(index != -1){
				String text = textfield.getText();
				if(!text.equals("")){
					editState.setElementAt(text,index);	//要素変更
					stateModel.setElementAt(text, index);	//リストも変更
				}
			}
		}else if(actionCommand.equals("テーブル状態を取得")){
			int option = JOptionPane.showConfirmDialog(this, "現在選択中の状態に上書きします。よろしいですか。","状態上書きの確認", JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.YES_OPTION) getTableState();
		}else if(actionCommand.equals("テーブル状態に設定")){
			int option = JOptionPane.showConfirmDialog(this, "現在のテーブルの状態に上書きします。よろしいですか。","状態上書きの確認", JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.YES_OPTION){
				pf.getTableManager().convertState();//状態を変換しておく
				editState = pf.getTableManager().getState();	//状態を受け取る
				initStateList();	//リストに反映
				setTableState();
			}
		}else if(actionCommand.equals("オブジェクト名統一")){
			int option = JOptionPane.showConfirmDialog(this, "オブジェクト名に色が指定されているものをA～Hに置き換えます、よろしいですか。","状態上書きの確認", JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.YES_OPTION){
				pf.getTableManager().convertState();//状態を変換しておく
				editState = pf.getTableManager().getState();	//状態を受け取る
				initStateList();	//リストに反映
			}
		}else if(actionCommand.equals("エラーチェック")){
			pf.getTableManager().convertState();//状態を変換しておく
			editState = pf.getTableManager().getState();	//状態を受け取る
			initStateList();	//リストに反映
			String errorLog = new String();	//表示するエラー
			ArrayList<String> errorList = pf.getTableManager().errorCheck();	//エラー取得
			for(int i = 0 ; i < errorList.size() ; i++){
				errorLog += errorList.get(i) + "\n";
			}
			if(errorList.size() == 0) JOptionPane.showMessageDialog(this,"エラーは検出されませんでした。");
			else JOptionPane.showMessageDialog(this,errorLog);
		}else if(actionCommand.equals("？")){
			JOptionPane.showMessageDialog(this, "プランニングに必要な初期状態や目標状態を設定することができます。\n"
					+"状態は3種類まで記憶しておくことができ、[編集する状態]欄より選択できます。\n"
					+"状態1に初期状態、状態2に目標状態を設定するといった使い方をします。\n"
					+"他にも状態のセーブ/ロードボタンより外部ファイルとして状態を保存できます。\n\n"
					+"画面に表示されているテーブルの状態は[テーブル状態に設定]ボタンを押すまで更新されません。\n"
					+"ただし[命令]メニューはここで選択されている状態をもとにおこなうため、テーブルに適用するまではうまく動きません。\n"
					+"テーブルに適用するとオブジェクト名はすべてA～Hの名前で置き換えられます。\n\n"
					+"状態が正しく動かない場合はエラーチェックボタンを押すことで原因を発見できる可能性があります。");
		}
	}

	//状態変更
	public void stateChanged(ChangeEvent e) {
		for(int i = 0 ; i < 3 ; i++ ){
			if( i == editStateNumber ) continue;	//同じ番号は調べない
			if(radiobutton[i].isSelected()){
				pf.getTableManager().setStateNumber(i);
				editStateNumber = i;
				getTableState();
				setTableState();
				break;
			}
		}
	}

	//リスト変更
	public void valueChanged(ListSelectionEvent arg0) {
		textfield.setText(stateList.getSelectedValue());
	}
}