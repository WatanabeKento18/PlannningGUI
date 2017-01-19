import java.awt.*;
import java.util.*;

/**
 *		テーブル上のオブジェクトを管理するためのクラス
 **/

class TableManager {
	StateManager stateManager;				//現在の状態
	HashMap<Integer,BlockBase> tableObject;		//テーブル上のオブジェクト
	HashMap<String,BlockManager> objectData;	//オブジェクトのデータを管理するためのHashMap
	HandManager handManager;					//手を管理するクラス
	ImagePanel imagePanel;					//描画用クラス
	boolean animationEnable;				//アニメーションをおこなうかのフラグ
	
	//コンストラクタ
	TableManager(){
		stateManager = new StateManager();
		tableObject = new HashMap<Integer,BlockBase>();
		objectData = new HashMap<String,BlockManager>();
		handManager = new HandManager(this);
		imagePanel = new ImagePanel();
		animationEnable = false;
	}
	
	//更新
	public void update(){
		//手に持っているオブジェクトを更新
		handManager.update();
		
		//すべてのオブジェクトを更新
		for(Iterator<String> it = objectData.keySet().iterator() ; it.hasNext() ; ){	//土台を順番に調べる
			String key = it.next();	//キー取得
			BlockManager obj = objectData.get(key);	//次のオブジェクトを取得
			obj.update();	//更新
		}
	}
	
	//初期化
	public void initObject(){
		addImage("img/back4.png","Back");
		addImage("img/table2.png","Table");
		addImage("img/hand1.png","Hand");
		addImage("img/selector1.png","Selector");
		addImage("img/mode1.png","Mode");
		setImageDisable("Mode");
		addObject("A");
		addObject("B");
		addObject("C");
		addObject("D");
		addObject("E");
		addObject("F");
		addObject("G");
		addObject("H");
		setImagePosition("Back" , 0 , 0);
		setImagePosition("Table" , 50 , 500);
		initObjectImage();
	}
	
	//テーブル上の初期化
	public void initTableObject(){
		tableObject = new HashMap<Integer,BlockBase>();
		handManager.releaseHolding();	//手に持っている物を離す
		//すべてのオブジェクトを初期化
		for(Iterator<String> it = objectData.keySet().iterator() ; it.hasNext() ; ){	//土台を順番に調べる
			String key = it.next();	//キー取得
			BlockManager obj = objectData.get(key);	//次のオブジェクトを取得
			obj.initSettings();	//初期化
		}
		animationEnable = false;	//アニメーションOFF
	}
	
	//テーブル上のオブジェクトを全消去
	public void removeAllObjects(){
		tableObject.clear();
	}
	
	//アニメーションの設定
	public void setAnimationEnable(boolean flag){
		animationEnable = flag;
	}
	
	//アニメーションの速度設定
	public void setAnimationSpeed(int spd){
		handManager.setHandSpeed(spd);
	}
	
	//指定の状態に更新
	public void setState(Vector<String> state){
		stateManager.setState(state);
		setTableObjectPosition();
	}
	
	//現在の状態を原始名状態に変換
	public void convertState(){
		stateManager.setState(StateManager.convertState(stateManager.getState(),this));
	}
	
	//指定のオペレータを実行した状態に更新
	public void applyOperator(Operator op){
		stateManager.applyOperator(op,this);
	}
	
	//行動するための条件をすべて満たしているかどうか（返り値 : 実行可能な場合true）
	//ifList : 条件をすべて保存したVector
	public boolean isActOperator(Vector<String> ifList){
		return stateManager.isActOperator(StateManager.convertState(ifList,this));	//原始名に変換した状態で比較
	}
	
	//isActOperatorの要素が一つの場合
	public boolean isActOperator(String ifElement){
		Vector<String> ifList = new Vector<String>();	//Vector生成
		ifList.addElement(ifElement);	//要素一つのVectorを作成
		return isActOperator(ifList);
	}
	
	//状態番号を取得
	public int getStateNumber(){
		return stateManager.getStateNumber();
	}
	
	//状態番号を設定
	public void setStateNumber(int number){
		stateManager.setStateNumber(number);
	}
	
	//テーブルの状態からVectorを生成
	public Vector<String> stateToVector(){
		Vector<String> state = new Vector<String>();
		
		//配置されているオブジェクトの状態を変換
		for(Iterator<Integer> it = tableObject.keySet().iterator() ; it.hasNext() ; ){	//土台を順番に調べる
			Integer key = it.next();	//キー取得
			BlockBase obj = tableObject.get(key);	//次のオブジェクトを取得
			BlockManager topBlock = obj.getAboveBlock();	//テーブル上のオブジェクトを取得
			state.addElement("ontable " + topBlock.getName());	//テーブルにあることを示す
			while(topBlock.getAboveBlock() != null){
				state.addElement(topBlock.getAboveBlock().getName() + " on " + topBlock.getName());	//オブジェクトの上下関係を記述
				topBlock = topBlock.getAboveBlock();	//一つ上のオブジェクトへ
			}
			state.addElement("clear " + topBlock.getName());	//上に何もないことを示す
			if(!topBlock.getShapeName().equals("Triangle")) state.addElement("placeable " + topBlock.getName());	//三角形ではない場合
		}
		if(handManager.isHolding()){
			state.addElement("holding "+handManager.getHoldingName());	//手に持っている状態
		}else{
			state.addElement("handEmpty");	//何も持っていない状態
		}
		
		//オブジェクトの状態を変換
		for(Iterator<String> it = objectData.keySet().iterator() ; it.hasNext() ; ){	//オブジェクトを順番に調べる
			String key = it.next();	//キー取得
			BlockManager obj = objectData.get(key);	//次のオブジェクトを取得
			state.addElement(obj.getName() + " is-a " + obj.getShapeName() + " shape");
			state.addElement(obj.getName() + " is-a " + obj.getColorName() + " color");
			if(!obj.getTag().equals(""))	state.addElement(obj.getName() + " is-a " + obj.getTag());
		}
		//System.out.println(state);
		return state;
	}
	
	//テーブル上のオブジェクトをすべて既定の座標へ置く
	public void setTableObjectPosition(){
		//オブジェクトの座標を設定
		for(Iterator<Integer> it = tableObject.keySet().iterator() ; it.hasNext() ; ){	//土台を順番に調べる
			int baseCode = it.next();	//コード取得
			BlockBase obj = tableObject.get(baseCode);	//次の土台を取得
			BlockManager topBlock = obj.getAboveBlock();	//上のブロックを取得
			int posY = 500;
			while(topBlock != null){
				posY -= topBlock.getHeight();
				topBlock.setPosition(baseCode*80 + 70, posY);
				topBlock = topBlock.getAboveBlock();
			}
		}
	}
	
	//オブジェクトをテーブル上へ再配置
	public Vector<String> getAllObjectToTableState(){
		return stateManager.getInitialState();
	}
	
	//現在の状態のエラーチェックをおこないエラーメッセージを取得する
	public ArrayList<String> errorCheck(){
		return stateManager.errorCheck();
	}
	
	//指定の名前を持つオブジェクトを返す
	public BlockManager getObject(String fileKey){
		for(Iterator<String> it = objectData.keySet().iterator() ; it.hasNext() ; ){	//オブジェクトを順番に調べる
			String key = it.next();	//キー取得
			BlockManager obj = objectData.get(key);	//次のオブジェクトを取得
			if(obj.isObjectName(fileKey)){	//名前と合致するか
				return obj;
			} 
		}
		return null;
	}
	
	//オブジェクト生成　objectKey : オブジェクト用のキー
	public void addObject(String objectKey){
		BlockManager newObj = new BlockManager(objectKey);	//オブジェクト生成
		objectData.put(objectKey , newObj);			//オブジェクトを登録
	}
	
	//画像生成
	//imageKey : 画像管理用のキー , imageName : 画像ファイルの名前
	public void addImage(String imageName , String imageKey){
		imagePanel.addObjectImage(imageName, imageKey);	//画像生成
	}
	
	//画像非表示
	public void disableImage(String imageKey){
		imagePanel.disableObjectImage(imageKey);
	}
	
	//オブジェクト用画像を一括生成
	public void initObjectImage(){
		for(Iterator<String> it = objectData.keySet().iterator() ; it.hasNext() ; ){	//オブジェクトを順番に調べる
			String key = it.next();	//キー取得
			BlockManager obj = objectData.get(key);	//次のオブジェクトを取得
			obj.initObjectImage(this);
		}
	}
	
	//オブジェクトを取る
	public void pickUpObject(String objectKey){
		BlockManager obj = getObject(objectKey);	//オブジェクト取得
		if(obj.getUnderBlock() == null){	//一番下のオブジェクトの場合
			tableObject.remove(obj.getBaseCode());	//土台を削除
		}
		obj.holding();	//持っている状態に変更
		if(animationEnable) handManager.pickUpObject(obj);	//手に取りに行かせる命令
	}
	
	//オブジェクトをテーブルへ配置
	public void putObjectOnTable(String objectKey){
		BlockManager obj = getObject(objectKey);	//オブジェクト取得
		if(obj != null){
			Integer code = 0;
			while(true){	//あいてるコードで土台を作成するためのループ
				if(!tableObject.containsKey(code)){	//codeがあいている場合
					BlockBase baseObj = new BlockBase(code);	//土台を生成
					tableObject.put(code,baseObj);			//土台をリストに追加
					baseObj.placeAboveObject(obj);			//土台の上にオブジェクト追加
					if(animationEnable) handManager.putDownObjectTable(code);	//手に置きに行かせる命令
					break;
				}
				code++;	//加算
			}
		}
	}
	
	//オブジェクトをオブジェクトの上に配置
	//objectKey : 配置するオブジェクトのキー , placeKey : 上に置かれるオブジェクトのキー 
	public void putObjectOnObject(String objectKey , String placeKey){
		BlockManager obj = getObject(objectKey);	//オブジェクト取得
		BlockManager place = getObject(placeKey);	//オブジェクト取得
		if(obj != null && place != null){		//必要なオブジェクトがあるかどうか
			place.placeAboveObject(obj);		//上に配置
			if(animationEnable) handManager.putDownObject(place);		//手に置きに行かせる命令
		}
	}
	
	//指定の名前を持つオブジェクトを手に持つ
	public void setHoldingObject(String objectKey){
		BlockManager obj = getObject(objectKey);
		handManager.setHolding(obj);
		//System.out.println(obj);
	}
	
	//指定のオブジェクトを手に持つ
	public void setHoldingObject(BlockManager obj){
		handManager.setHolding(obj);
	}
	
	//手に持っているオブジェクトを得る
	public BlockManager getHoldingObject(){
		return handManager.getHoldingObject();
	}
	
	//手に持っているオブジェクトの名前を得る
	public String getHoldingObjectName(){
		return handManager.getHoldingName();
	}
	
	//手に持っているオブジェクトを解放
	public void releaseHoldingObject(){
		handManager.releaseHolding();		
	}
	
	//オブジェクトの形状を設定する
	public void setObjectShape(String objectKey , String s){
		BlockManager obj = getObject(objectKey);	//オブジェクト取得
		if(s.equals("Square")) obj.setShape(BlockShape.SQUARE);
		else if(s.equals("halfBlock")) obj.setShape(BlockShape.HALF);
		else if(s.equals("Triangle")) obj.setShape(BlockShape.TRIANGLE);
	}
	
	//オブジェクトの形状を設定する
	public void setObjectColor(String objectKey , String c_org){
		BlockManager obj = getObject(objectKey);	//オブジェクト取得
		String c = c_org.toLowerCase();	//小文字で比較
		if(c.equals("red")) obj.setColor(BlockColor.RED);
		else if(c.equals("blue")) obj.setColor(BlockColor.BLUE);
		else if(c.equals("green")) obj.setColor(BlockColor.GREEN);
		else if(c.equals("yellow")) obj.setColor(BlockColor.YELLOW);
		else if(c.equals("purple")) obj.setColor(BlockColor.PURPLE);
		else if(c.equals("pink")) obj.setColor(BlockColor.PINK);
		else if(c.equals("orange")) obj.setColor(BlockColor.ORANGE);
		else if(c.equals("white")) obj.setColor(BlockColor.WHITE);
	}
	
	//アニメーション中かどうかを取得
	public boolean isMoving(){
		return handManager.isMoving();
	}
	
	//現在の状態を取得
	public Vector<String> getState(){
		return stateManager.getState();
	}

	//指定の状態を取得
	public Vector<String> getState(int num){
		return stateManager.getState(num);
	}
	
	//ImagePanelを取得
	public ImagePanel getImagePanel(){
		return imagePanel;
	}
	
	//指定の画像キーを持つ画像の位置を設定
	public void setImagePosition(String imageKey , int x , int y){
		ObjectImage image = imagePanel.getObjectImage(imageKey);
		if(image != null) image.setPosition(x, y);
	}
	
	//指定の画像キーを持つ画像の非表示設定
	public void setImageDisable(String imageKey){
		ObjectImage image = imagePanel.getObjectImage(imageKey);
		if(image != null) image.disableImage();
	}
	
	//指定座標にあるオブジェクトを得る
	public BlockManager getObjectByPosition(int x , int y){
		x -= 8;		//座標調整
		y -= 57;	//座標調整
		for(Iterator<String> it = objectData.keySet().iterator() ; it.hasNext() ; ){	//土台を順番に調べる
			String key = it.next();	//キー取得
			BlockManager obj = objectData.get(key);	//次のオブジェクトを取得
			Point pos = obj.getPosition();	//座標を取得
			if(pos.x <= x){
				if(pos.y <= y){
					if(pos.x+64 >= x){
						if(pos.y+64 >= y){
							return obj;	//指定のオブジェクトを返す
						}
					}
				}
			}
		}
		return null;
	}
	
	//指定座標にオブジェクトを配置
	public void setObjectByPosition(BlockManager putObj , int x , int y){
		x -= 8;		//座標調整
		y -= 57;	//座標調整
		
		int code = (x-70)/80;	//列番号を計算
		if(code < 0) code = 0;	//範囲調整
		if(code > 7) code = 7;	//範囲調整
		
		int height = (500-y)/64+1;	//オブジェクトの高さを計算
		if(y > 500) height = 0;	//範囲調整
		
		BlockManager holdingObj = getHoldingObject();	//手に持っているオブジェクト
		if(holdingObj != null){	//手に持っている場合
			if(holdingObj.equals(putObj)){	//手に持っているオブジェクトを指定した場合
				releaseHoldingObject();	//持っているオブジェクトを解放
			}
		}
		
		BlockBase baseObject = tableObject.get(code);	//列の土台を計算
		System.out.println("------------------------------------");
		System.out.println("操作オブジェクト : " + putObj.getName());
		System.out.println("上 : " + putObj.getAboveBlock() + " 下 : " + putObj.getUnderBlock());
		System.out.println("選択コード : " + code);
		if(baseObject != null){	//土台がある場合
			if (height == 0) {	//一番下を指定した場合
				System.out.println("操作 : 土台指定");
				BlockManager topBlock = baseObject.getAboveBlock();	//土台の上のオブジェクトを取得
				if(topBlock.equals(putObj)){
					System.out.println("同一オブジェクト消去判定");
					return;	//同じオブジェクト同士の削除は行わない
				}
				removeTableObject(putObj); // 元にあった位置を修正
				putObj.setAboveBlock(topBlock);	//土台の上にあったものをさらに上へ
				putObj.setUnderBlock(null);		//下は何もない
				topBlock.setUnderBlock(putObj);	//上にあったオブジェクトの下に設定
				baseObject.setAboveBlock(putObj);	//土台の上に設定
			} else {
				BlockManager topBlock = baseObject.getAboveBlock(); // 土台の上からスタート
				for (int i = 1; i < height; i++) { // 指定のオブジェクトの位置へ
					if (topBlock != null)
						topBlock = topBlock.getAboveBlock(); // 順番に上へ
					else
						break; // もうオブジェクトがない場合
				}
				if (topBlock != null) { // オブジェクトが存在する場合
					System.out.println("操作 : オブジェクト指定");
					if(topBlock.equals(putObj)){
						System.out.println("同一オブジェクト消去判定");
						return;	//同じオブジェクト同士の削除は行わない
					}
					removeTableObject(putObj); // 元にあった位置を修正
					BlockManager superTopObj = topBlock.getAboveBlock(); // 上の上のオブジェクトを取得
					putObj.setAboveBlock(superTopObj); // 上のブロックを設定
					putObj.setUnderBlock(topBlock); // 下のブロックを設定
					if (superTopObj != null)
						superTopObj.setUnderBlock(putObj); // 指定オブジェクトの上の設定
					topBlock.setAboveBlock(putObj); // 指定オブジェクトの下の設定
				} else {
					System.out.println("操作 : オブジェクト上空間指定");
					topBlock = baseObject.getTopBlock();
					if(topBlock.equals(putObj)){
						System.out.println("同一オブジェクト消去判定");
						return;	//同じオブジェクト同士の削除は行わない
					}
					removeTableObject(putObj); // 元にあった位置を修正
					topBlock.setAboveBlock(putObj);	//上に設定
					putObj.setUnderBlock(topBlock);	//下に設定
					putObj.setAboveBlock(null);
					if(!tableObject.containsKey(baseObject.getBaseCode())){	//土台が削除されてしまっている場合は再度追加
						tableObject.put(baseObject.getBaseCode(), baseObject);
					}
				}
			}
		}else{	//土台が無い位置だった場合は追加
			System.out.println("操作 : 土台空間指定");
			baseObject = new BlockBase(code);	//土台を生成
			tableObject.put(code,baseObject);			//土台をリストに追加
			removeTableObject(putObj); // 元にあった位置を修正
			baseObject.setAboveBlock(putObj);			//土台の上にオブジェクト追加
			putObj.setAboveBlock(null);
			putObj.setUnderBlock(null);
		}
		putObj.setBaseCode(code);	//コードを設定しなおす
		setTableObjectPosition();
		stateManager.setState(stateToVector());
		printObject();
	}

	//指定オブジェクトを消去し上下にあったオブジェクトを連結
	//指定オブジェクトを抜き去る
	public void removeTableObject(BlockManager obj){
		if(obj == null) return;
		BlockManager above = obj.getAboveBlock();
		BlockManager under = obj.getUnderBlock();
		obj.setAboveBlock(null);
		obj.setUnderBlock(null);
		if(above != null){
			System.out.println("オブジェクト除去 : " + above.getName() + "を下と連結");
			above.setUnderBlock(under);	//上のブロックを下のブロックにつなぐ
		}
		if(under != null){	//下にオブジェクトがある場合
			System.out.println("オブジェクト除去 : " + under.getName() + "を上と連結");
			under.setAboveBlock(above);	//下のブロックを上のブロックにつなぐ
		}else{	//下が土台の場合
			if(above != null){
				System.out.println("オブジェクト除去 : BASECODE " + obj.getBaseCode() + "の上に連結");
				tableObject.get(obj.getBaseCode()).setAboveBlock(above);	//土台と上のブロックをつなぐ
			}else{
				System.out.println("オブジェクト除去 : BASECODE " + obj.getBaseCode() + "を除去");
				tableObject.remove(obj.getBaseCode());
			}
		}
	}
	
	// テーブル上のオブジェクトをコンソール上に表示
	public void printObject(){
		for(Iterator<Integer> it = tableObject.keySet().iterator() ; it.hasNext() ; ){
			BlockBase obj = tableObject.get(it.next());
			obj.printUpperObject();
		}
	}
}
