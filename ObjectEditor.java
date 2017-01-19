import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *		状態エディタ用フレーム 
 **/

class ObjectEditor extends JFrame implements ActionListener , ChangeListener {
	private static final long serialVersionUID = 1L;
	private int editnum;	//編集するオブジェクト番号
	private final String objectTag[] = {"A","B","C","D","E","F","G","H"};	//オブジェクトのタグ一覧
	PlanningFrame pf;	//呼び出したPlanningFrame
	JLabel objectimage;		//オブジェクトの画像用ラベル
	JButton button[];			//各種ボタン
	JRadioButton radiobutton[];	//状態選択ラジオボタン
	JTextField textfield;		//状態入力用フィールド
	
	public ObjectEditor(PlanningFrame frame){
		pf = frame;
		editnum = 0;
		initMenu();
		setResizable(false);	//サイズ変更不可に設定
		setObjectStatus(pf.getTableManager().getObject("A"));	//初期設定のオブジェクトにあわせる
	}
	
	//メニュー作成
	void initMenu(){
		
		JPanel p = new JPanel();
		p.setLayout(null);
		
		JLabel objectLabel = new JLabel("編集するオブジェクト");
		objectLabel.setBounds(210,10,150,30);
		p.add(objectLabel);
		
		JLabel imageLabel = new JLabel("画像データ");
		imageLabel.setBounds(60,10,150,30);
		p.add(imageLabel);
		
		JLabel shapeLabel = new JLabel("オブジェクトの形");
		shapeLabel.setBounds(40,180,150,30);
		p.add(shapeLabel);
		
		JLabel colorLabel = new JLabel("オブジェクトの色");
		colorLabel.setBounds(40,290,150,30);
		p.add(colorLabel);
		
		JLabel enableLabel = new JLabel("表示");
		enableLabel.setBounds(260,240,150,30);
		p.add(enableLabel);

		JLabel tagLabel = new JLabel("タグ");
		tagLabel.setBounds(260,300,150,30);
		p.add(tagLabel);
		
		textfield = new JTextField();
		textfield.setBounds(210, 330, 150, 24);
		p.add(textfield);
		
		radiobutton = new JRadioButton[21];
		ButtonGroup group1 = new ButtonGroup();
		for(int i = 0 ; i < 8 ; i++){
			radiobutton[i] = new JRadioButton(objectTag[i]);
			radiobutton[i].setBounds(250, 40 + i*24, 50, 24);
			group1.add(radiobutton[i]);
		}
		ButtonGroup group2 = new ButtonGroup();
		radiobutton[8] = new JRadioButton("正方形");
		radiobutton[8].setBounds(40, 210, 100, 24);
		radiobutton[9] = new JRadioButton("半分サイズ");
		radiobutton[9].setBounds(40, 210 + 24, 100, 24);
		radiobutton[10] = new JRadioButton("三角形");
		radiobutton[10].setBounds(40, 210 + 24*2, 100, 24);
		for(int i = 8 ; i < 11 ; i++){
			group2.add(radiobutton[i]);
		}
		ButtonGroup group3 = new ButtonGroup();
		radiobutton[11] = new JRadioButton("赤");
		radiobutton[12] = new JRadioButton("青");
		radiobutton[13] = new JRadioButton("緑");
		radiobutton[14] = new JRadioButton("黄");
		radiobutton[15] = new JRadioButton("紫");
		radiobutton[16] = new JRadioButton("桃");
		radiobutton[17] = new JRadioButton("橙");
		radiobutton[18] = new JRadioButton("白");
		for(int i = 11 ; i < 19 ; i++){
			radiobutton[i].setBounds(10 + ((i-11)%4)*42, 320 + ((i-11)/4)*24, 42, 24);
			group3.add(radiobutton[i]);
		}
		ButtonGroup group4 = new ButtonGroup();
		radiobutton[19] = new JRadioButton("ON");
		radiobutton[19].setBounds(230, 270, 50, 24);
		radiobutton[20] = new JRadioButton("OFF");
		radiobutton[20].setBounds(280, 270, 50, 24);
		group4.add(radiobutton[19]);
		group4.add(radiobutton[20]);
		for(int i = 0 ; i < 21 ; i++){
			radiobutton[i].addChangeListener(this);
			radiobutton[i].setBackground(Color.WHITE);
			p.add(radiobutton[i]);
		}
		radiobutton[0].setSelected(true);
		
		objectimage = new JLabel();
		objectimage.setBounds(60,45,64,64);
		p.add(objectimage);
		
		button = new JButton[5];
		button[0] = new JButton("デフォルト画像を使用");
		button[0].setBounds(10, 120, 170, 24);
		button[1] = new JButton("画像を選択");
		button[1].setBounds(10, 144, 170, 24);
		button[2] = new JButton("設定");
		button[2].setBounds(250, 360, 60, 24);
		button[3] = new JButton("表示個数の設定");
		button[3].setBounds(170, 390, 150, 30);
		button[4] = new JButton("？");
		button[4].setBounds(330, 390, 50, 30);
		
		for(int i = 0 ; i < 5 ; i++){
			p.add(button[i]);	//パネルに追加
			button[i].addActionListener(this);	//行動受付
		}
		
		getContentPane().add(p , BorderLayout.CENTER);
	}
	
	//入力受付
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		
		//ボタンの種類によって分岐
		if(actionCommand.equals("デフォルト画像を使用")){
			//選択中オブジェクト検索
			for(int i = 0 ; i < 8 ; i++ ){
				if(radiobutton[i].isSelected()){
					BlockManager editObj = pf.getTableManager().getObject(objectTag[i]);	//オブジェクトを設定
					if(editObj != null) editObj.setDefaultImage();	//画像設定
					if(objectimage != null) objectimage.setIcon(new ImageIcon(editObj.getFilePath()));	//アイコン設定
					break;
				}
			}
		}else if(actionCommand.equals("画像を選択")){
			JFileChooser fileChooser = new JFileChooser();	//ファイル選択ダイアログ
			fileChooser.setFileFilter(new FileNameExtensionFilter("画像ファイル", "png", "jpg","jpeg", "gif", "bmp"));
			int selected = fileChooser.showOpenDialog(this);	//ダイアログ表示
			if (selected == JFileChooser.APPROVE_OPTION) {		//選択された場合
				File file = fileChooser.getSelectedFile();		//選択ファイルを開く
				//選択中オブジェクト検索
				for(int i = 0 ; i < 8 ; i++ ){
					if(radiobutton[i].isSelected()){
						BlockManager editObj = pf.getTableManager().getObject(objectTag[i]);	//オブジェクトを設定
						if(editObj != null) editObj.setImage(file);	//画像設定
						if(objectimage != null) objectimage.setIcon(new ImageIcon(editObj.getFilePath()));	//アイコン設定
						break;
					}
				}
			}
		}else if(actionCommand.equals("設定")){
			//オブジェクト選択
			for(int i = 0 ; i < 8 ; i++ ){
				if(radiobutton[i].isSelected()){
					pf.getTableManager().getObject(objectTag[i]).setTag(textfield.getText());	//タグを設定
					JOptionPane.showMessageDialog(this, "タグを " + textfield.getText() + " に設定しました");
					break;
				}
			}
		}else if(actionCommand.equals("表示個数の設定")){
			String setNumStr = JOptionPane.showInputDialog(this, "表示する個数を入力(0-8)");	//表示する個数入力
			if(setNumStr == null) return;	//入力されなかった場合	
			int setNum =Integer.parseInt(setNumStr);	//整数値に変換
			if(setNum < 0 || setNum > 8){	//不正な値
				JOptionPane.showMessageDialog(this, "不正な入力です");	//失敗したことを表示
				return;
			}
			//オブジェクト選択
			pf.getTableManager().setAnimationEnable(false);
			pf.getTableManager().removeAllObjects();
			for(int i = 0 ; i < 8 ; i++ ){
				BlockManager editObj = pf.getTableManager().getObject(objectTag[i]);	//オブジェクトを設定
				if(i < setNum){	//表示するオブジェクト
					pf.getTableManager().putObjectOnTable(editObj.getName());	//配置
					editObj.setEnable(true);
				}else{	//表示しないオブジェクト
					pf.getTableManager().removeTableObject(editObj);	//除去
					editObj.setEnable(false);
				}
			}
			pf.setTableState(pf.getTableManager().stateToVector());	//状態更新
		}else if(actionCommand.equals("？")){
			JOptionPane.showMessageDialog(this, "テーブル上に配置される各オブジェクトについてそれぞれ設定をします。\n"
					+"最初にプランニングに使用する個数を[表示個数の設定]ボタンで決めます。\n"
					+"[編集するオブジェクト]欄で選択したオブジェクトの画像や形/色、タグを設定することができます。\n\n"
					+"画像データは色と形に合わせたデフォルトのものが用意されていますが、用意した独自素材を用いることができます。\n"
					+"サイズは64×64が基本であり、それを超える場合は縮小されます。\n"
					+"縦の大きさが64未満のオブジェクトは上に積む場合に自動的に座標が調整されます。\n\n"
					+"タグは各オブジェクトが持てる固有の名前です。タグは使わなくても問題ありません。\n"
					+"命令はA～Hの名前、redなどの色、そしてこのタグのいずれかでオブジェクトを指定できます。\n\n"
					+"色/形を設定した場合、状態には反映されないので状態エディタでテーブル状態を取得する必要があります。");
		}
	}

	//状態変更
	public void stateChanged(ChangeEvent e) {
		BlockManager editObj = null;	//編集オブジェクト
		
		//オブジェクト選択
		for(int i = 0 ; i < 8 ; i++ ){
			if(radiobutton[i].isSelected()){
				editObj = pf.getTableManager().getObject(objectTag[i]);	//オブジェクトを設定
				if(editnum != i){	//オブジェクトの変更がある場合
					setObjectStatus(editObj);
					editnum = i;
					return;
				}
				break;
			}
		}
		if(editObj == null) return;	//オブジェクトがない場合は終了
		
		//形
		if(radiobutton[8].isSelected()){
			editObj.setShape(BlockShape.SQUARE);
		}else if(radiobutton[9].isSelected()){
			editObj.setShape(BlockShape.HALF);
		}else if(radiobutton[10].isSelected()){
			editObj.setShape(BlockShape.TRIANGLE);
		}
		
		//色
		if(radiobutton[11].isSelected()){
			editObj.setColor(BlockColor.RED);
		}else if(radiobutton[12].isSelected()){
			editObj.setColor(BlockColor.BLUE);
		}else if(radiobutton[13].isSelected()){
			editObj.setColor(BlockColor.GREEN);
		}else if(radiobutton[14].isSelected()){
			editObj.setColor(BlockColor.YELLOW);
		}else if(radiobutton[15].isSelected()){
			editObj.setColor(BlockColor.PURPLE);
		}else if(radiobutton[16].isSelected()){
			editObj.setColor(BlockColor.PINK);
		}else if(radiobutton[17].isSelected()){
			editObj.setColor(BlockColor.ORANGE);
		}else if(radiobutton[18].isSelected()){
			editObj.setColor(BlockColor.WHITE);
		}
		
		//表示切替
		if(radiobutton[19].isSelected()){
			if(!editObj.isEnableImage()){	//非表示の場合
				pf.getTableManager().setAnimationEnable(false);
				pf.getTableManager().putObjectOnTable(editObj.getName());	//配置
				pf.setTableState(pf.getTableManager().stateToVector());	//状態更新
				editObj.setEnable(true);
			}
		}else if(radiobutton[20].isSelected()){
			if(editObj.isEnableImage()){	//表示の場合
				pf.getTableManager().removeTableObject(editObj);	//除去
				pf.setTableState(pf.getTableManager().stateToVector());	//状態更新
				editObj.setEnable(false);
			}
		}
		
		//更新
		editObj.initObjectImage(pf.getTableManager());
		pf.getTableManager().setTableObjectPosition();
		if(objectimage != null) objectimage.setIcon(new ImageIcon(editObj.getFilePath()));	//アイコン設定
	}
	
	//指定のオブジェクトの選択を反映する
	private void setObjectStatus(BlockManager editObj){
		for(int j = 8 ; j < 21 ; j++) radiobutton[j].setSelected(false);	//すべて選択解除
		radiobutton[getShapeNumber(editObj.getShape()) + 8].setSelected(true);	//形の設定
		radiobutton[getColorNumber(editObj.getColor()) + 11].setSelected(true);	//色の設定
		if(editObj.isEnableImage()) radiobutton[19].setSelected(true);	//表示設定
		else radiobutton[20].setSelected(true);
		textfield.setText(editObj.tag);	//タグの設定
		if(objectimage != null) objectimage.setIcon(new ImageIcon(editObj.getFilePath()));	//アイコン設定
	}
	
	//形番号を得る
	private static int getShapeNumber(BlockShape s){
		if(s == BlockShape.SQUARE) return 0;
		if(s == BlockShape.HALF) return 1;
		if(s == BlockShape.TRIANGLE) return 2;
		return -1;
	}
	
	//色番号を得る
	private static int getColorNumber(BlockColor c){
		if(c == BlockColor.RED) return 0;
		if(c == BlockColor.BLUE) return 1;
		if(c == BlockColor.GREEN) return 2;
		if(c == BlockColor.YELLOW) return 3;
		if(c == BlockColor.PURPLE) return 4;
		if(c == BlockColor.PINK) return 5;
		if(c == BlockColor.ORANGE) return 6;
		if(c == BlockColor.WHITE) return 7;
		return -1;
	}
}