import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;

/**
 *		プランニング設定用フレーム 
 **/

class PlanningEditor extends JFrame implements ActionListener , ChangeListener {
	private static final long serialVersionUID = 1L;
	private int editInitState;	//参照している初期状態番号
	private int editGoalState;	//参照している目標状態番号
	PlanningFrame pf;	//呼び出したPlanningFrame
	JLabel objectimage;		//オブジェクトの画像用ラベル
	JButton button[];			//各種ボタン
	JRadioButton radiobutton[];	//状態選択ラジオボタン
	DefaultListModel<String> stateModel;		//状態編集用モデル 
	JList<String> stateList;			//状態一覧表示用リスト
	DefaultListModel<String> planModel;		//プラン編集用モデル 
	JList<String> planList;			//プラン一覧表示用リスト
	JTextField textfield;		//状態入力用フィールド
	
	public PlanningEditor(PlanningFrame frame){
		pf = frame;
		editInitState = pf.getTableManager().getStateNumber();	//初期状態番号取得
		setResizable(false);	//サイズ変更不可に設定
		initMenu();
	}
	
	//メニュー作成
	void initMenu(){
		
		JPanel p = new JPanel();
		p.setLayout(null);
		
		JLabel startLabel = new JLabel("初期状態");
		startLabel.setBounds(50,10,150,30);
		p.add(startLabel);
		
		JLabel goalLabel = new JLabel("目標状態");
		goalLabel.setBounds(50,120,150,30);
		p.add(goalLabel);
		
		radiobutton = new JRadioButton[7];
		ButtonGroup group1 = new ButtonGroup();
		for(int i = 0 ; i < 3 ; i++){
			radiobutton[i] = new JRadioButton("状態" + (i+1));
			radiobutton[i].setBounds(30, 40 + i*24, 120, 24);
			group1.add(radiobutton[i]);
		}
		
		ButtonGroup group2 = new ButtonGroup();
		radiobutton[3] = new JRadioButton("設定した状態");
		radiobutton[3].setBounds(30, 150, 120, 24);
		group2.add(radiobutton[3]);
		for(int i = 4 ; i < 7 ; i++){
			radiobutton[i] = new JRadioButton("状態" + (i-3));
			radiobutton[i].setBounds(30, 150 + (i-3)*24, 120, 24);
			group2.add(radiobutton[i]);
		}
		radiobutton[editInitState].setSelected(true);
		radiobutton[3].setSelected(true);
		
		for(int i = 0 ; i < 7 ; i++){
			radiobutton[i].addChangeListener(this);
			radiobutton[i].setBackground(Color.WHITE);
			p.add(radiobutton[i]);
		}

		JLabel goallistLabel = new JLabel("目標状態");
		goallistLabel.setBounds(180,10,150,30);
		p.add(goallistLabel);
		
		stateModel = new DefaultListModel<String>();
		stateList = new JList<String>(stateModel);
		//stateList.addListSelectionListener(this);
		JScrollPane scroll1 = new JScrollPane(stateList);
		getContentPane().add(scroll1);
		scroll1.setBounds(170, 40, 180, 220);
		p.add(scroll1);

		JLabel planningLabel = new JLabel("プラン");
		planningLabel.setBounds(370,10,150,30);
		p.add(planningLabel);
		
		planModel = new DefaultListModel<String>();
		planList = new JList<String>(planModel);
		JScrollPane scroll2 = new JScrollPane(planList);
		getContentPane().add(scroll2);
		scroll2.setBounds(360, 40, 190, 220);
		p.add(scroll2);
		
		textfield = new JTextField();
		textfield.setBounds(260, 330, 190, 30);
		p.add(textfield);
		
		button = new JButton[10];
		button[0] = new JButton("プランニング");
		button[0].setBounds(10, 280, 160, 40);
		button[1] = new JButton("アニメーション実行");
		button[1].setBounds(10, 330, 160, 40);

		button[2] = new JButton("目標状態追加");
		button[2].setBounds(200, 270, 150, 24);
		button[3] = new JButton("目標状態削除");
		button[3].setBounds(200, 300, 150, 24);
		button[4] = new JButton("プラン追加");
		button[4].setBounds(360, 270, 140, 24);
		button[5] = new JButton("プラン削除");
		button[5].setBounds(360, 300, 140, 24);
		button[6] = new JButton("初期化");
		button[6].setBounds(400, 370, 90, 24);
		button[7] = new JButton("↑");
		button[7].setBounds(508, 270, 48, 24);
		button[8] = new JButton("↓");
		button[8].setBounds(508, 300, 48, 24);
		button[9] = new JButton("？");
		button[9].setBounds(500, 370, 50, 24);
		
		for(int i = 0 ; i < 10 ; i++){
			p.add(button[i]);	//パネルに追加
			button[i].addActionListener(this);	//行動受付
		}
		
		getContentPane().add(p , BorderLayout.CENTER);
	}
	
	//リスト設定(isPlan : 目標状態かプランどちらに設定するか　newList : 設定内容)
	private void setPlanningList(boolean isPlan , Vector<String> newList){
		if(!isPlan){	//目標状態
			stateModel.clear();
			for(int i = 0 ; i < newList.size() ; i++){
				stateModel.addElement(newList.elementAt(i));
			}
		}else{	//プラン
			planModel.clear();
			for(int i = 0 ; i < newList.size() ; i++){
				planModel.addElement(newList.elementAt(i));
			}
		}
	}
	
	//入力受付
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		//ボタンの種類によって分岐
		if(actionCommand.equals("デフォルト画像を使用")){
			
		}else if(actionCommand.equals("目標状態追加")){
			String text = textfield.getText();
			if(!text.equals("")){
				stateModel.addElement(text);	//リストに追加
				textfield.setText("");	//入力欄初期化
				radiobutton[3].setSelected(true);	//設定をオリジナルに移動
				editGoalState = -1;	//設定をオリジナルに移動
			}
		}else if(actionCommand.equals("目標状態削除")){
			int index = stateList.getSelectedIndex();	//選択中の要素番号
			if(index != -1){
				stateModel.remove(index);	//リストも削除
				radiobutton[3].setSelected(true);	//設定をオリジナルに移動
				editGoalState = -1;	//設定をオリジナルに移動
			}
		}else if(actionCommand.equals("プラン追加")){
			String text = textfield.getText();
			if(!text.equals("")){
				planModel.addElement(text);	//リストに追加
				textfield.setText("");	//入力欄初期化
			}
		}else if(actionCommand.equals("プラン削除")){
			int index = planList.getSelectedIndex();	//選択中の要素番号
			if(index != -1){
				planModel.remove(index);	//リストも削除
			}
		}else if(actionCommand.equals("初期化")){
			int option = JOptionPane.showConfirmDialog(this, "目標状態とプランをすべて消去しますがよろしいですか。","状態上書きの確認", JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.YES_OPTION){
				stateModel.clear();
				planModel.clear();
			}
		}else if(actionCommand.equals("プランニング")){
			Vector<String> goalList = new Vector<String>();
			for(int i = 0 ; i < stateModel.size() ; i++){	//目標状態リストをVector型で作成
				goalList.addElement(stateModel.get(i));	//目標状態リストに追加
			}
			Planner planner = new Planner();
			Vector<String> initState = new Vector<String>();
			Vector<String> currentState = pf.getTableManager().getState();
			for(int i = 0 ; i < currentState.size() ; i++){initState.add(currentState.get(i));}
			if(planner.start(goalList,initState)){
				Vector<Operator> plan = planner.getPlan();	//作成したプランを取得
				planModel.clear();	//初期化
				for(int i = 0 ; i < plan.size() ; i++){	//プランをプラン一覧に追加
					planModel.addElement(plan.get(i).getName());	//プラン追加
				}
			}
		}else if(actionCommand.equals("アニメーション実行")){
			for(int i = 0 ; i < planModel.size() ; i++){	//プランを命令として実行
				pf.sendCommand(planModel.get(i));
			}
		}else if(actionCommand.equals("↑")){
			int sel = planList.getSelectedIndex();	//選択番号
			if(sel != -1 && 0 < sel){
				String selPlan = planList.getSelectedValue();	//選択中文字列
				planModel.remove(sel);	//削除
				planModel.add(sel-1, selPlan);	//追加
				planList.setSelectedIndex(sel-1);	//選択しておく
				//System.out.println(sel);
			}
		}else if(actionCommand.equals("↓")){
			int sel = planList.getSelectedIndex();	//選択番号
			if(sel != -1 && planModel.size()-1 > sel){
				String selPlan = planList.getSelectedValue();	//選択中文字列
				planModel.remove(sel);	//削除
				planModel.add(sel+1, selPlan);	//追加
				planList.setSelectedIndex(sel+1);	//選択しておく
			}
		}else if(actionCommand.equals("？")){
			JOptionPane.showMessageDialog(this, "プランニングをおこなうための画面です。初期状態と目標状態からプランニングを実行します。\n"
					+"初期状態は[編集]→[状態エディタ]より作成してください。デフォルトで現在表示されている状態が選択されています。\n"
					+"目標状態はテキストフィールドに1つずつ入力し目標状態追加ボタンから入力できます。\n"
					+"またすでに作成されている状態を選択することでその状態をもとに目標状態のリストが生成されます。\n\n"
					+"状態をセットしたらプランニングボタンを押します。プランリストに目標状態への必要な動作が生成されます。\n"
					+"アニメーションの実行ボタンを押すとプランに従って表示されているオブジェクトが動作します。\n"
					+"アニメーションの動作速度はメニューの[表示]→[アニメーション]より設定します。");
		}
	}

	//状態変更
	public void stateChanged(ChangeEvent e) {
		//初期状態確認
		for( int i = 0 ; i < 3 ; i++){
			if(radiobutton[i].isSelected()){
				if(i != editInitState){
					editInitState = i;	//初期状態変更
					pf.getTableManager().setStateNumber(editInitState);	//状態番号変更
					pf.setTableState(pf.getTableManager().getState());	//状態を更新
					return;
				}
				break;
			}
		}
		
		// 目標状態確認
		for (int i = 3; i < 7; i++) {
			if (radiobutton[i].isSelected()) {
				if (i-4 != editGoalState) {
					editGoalState = i-4; // 目標状態変更
					if(editGoalState != -1){	//状態設定された場合
						Vector<String> newGoal = StateManager.makeGoalState(pf.getTableManager().getState(i-4));	//目標状態を作成
						setPlanningList(false , newGoal);	//目標状態リストを更新
					}
					return;
				}
				break;
			}
		}
	}
}