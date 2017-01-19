import java.util.*;

/**
 *		状態を管理するクラス
 **/

class StateManager{
	private final String objectTag[] = {"A","B","C","D","E","F","G","H"};	//オブジェクトのタグ一覧
	int stateNumber;	//現在の状態番号
	Vector<String> currentState1;	//状態
	Vector<String> currentState2;	//状態
	Vector<String> currentState3;	//状態
	
	//コンストラクタ
	public StateManager(){
		stateNumber = 0;
		currentState1 = getInitialState();
		currentState2 = getInitialState();
		currentState3 = getInitialState();
	}
	
	//現在の状態を得る
	private Vector<String> getCurrentState(){
		if(stateNumber == 0) return currentState1;
		if(stateNumber == 1) return currentState2;
		return currentState3;
	}
	
	//現在の状態を設定
	public void setState(Vector<String> initialState){
		setState(initialState , stateNumber);
	}
	
	//指定の番号の状態を設定
	public void setState(Vector<String> initialState , int sn){
		if(sn == 0) currentState1 = initialState;
		if(sn == 1) currentState2 = initialState;
		if(sn == 2) currentState3 = initialState;
	}
	
	//現在の状態番号を取得
	public int getStateNumber(){
		return stateNumber;
	}
	
	//現在の状態番号を設定
	public void setStateNumber(int number){
		if(number >= 0 && number <= 2){	//適切な値かを確認
			stateNumber = number;
		}
	}
	
	//オペレータを実行
	public void applyOperator(Operator op , TableManager tm){
		Vector<String> addList = convertState(op.getAddList(),tm);
		Vector<String> deleteList = convertState(op.getDeleteList(),tm);
		for (int i = 0; i < addList.size(); i++) {
			addState((String)addList.elementAt(i));
		}
		for (int i = 0; i < deleteList.size(); i++) {
			removeState(deleteList.elementAt(i));
		}
		//System.out.println(currentState);
	}
	
	//状態を追加
	void addState(String state){
		getCurrentState().addElement(state);
	}
	
	//状態を削除
	void removeState(String state){
		getCurrentState().removeElement(state);
	}
	
	//指定の状態をすべて持っているか調べる
	public boolean isActOperator(Vector<String> ifList){
		for(int i = 0 ; i < ifList.size() ; i++ ){
			if(!getCurrentState().contains(ifList.get(i))) return false;	//1つでも条件が欠けている場合
		}
		return true;	//成功
	}
	
	//現在の状態を取得
	public Vector<String> getState(){
		return getCurrentState();
	}
	
	//指定の状態を取得
	public Vector<String> getState(int num){
		if(num == 0) return currentState1;
		if(num == 1) return currentState2;
		return currentState3;
	}
	
	//すべてをテーブル上に置いた状態を生成
	public Vector<String> getInitialState(){
		Vector<String> initialState = getCurrentState();
		if(initialState != null){
			for(int i = 0 ; i < initialState.size() ; i++){
				String stateData = initialState.elementAt(i);		//状態Vectorからデータを取得
				Unifier uni = new Unifier();		//マッチング用Unifierクラス
				if( uni.unify(stateData , "?object on ?object1") || uni.unify(stateData , "holding ?object")){
					String objectName = uni.getVarBindings().get("?object");	//オブジェクト名を取得
					initialState.removeElementAt(i);	//状態削除
					initialState.add(i,"ontable " + objectName);	//テーブル上にある状態を追加
					if(!initialState.contains("clear " + objectName)){
						initialState.add(i,"clear " + objectName);	//上に何もない状態を追加
						i++;	//追加分をずらす
					}if(!initialState.contains(objectName + " is-a Triangle shape")){
						initialState.add(i,"placeable " + objectName);	//上に置くことができる状態を追加
						i++;	//追加分をずらす
					}
				}else if(uni.unify(stateData , "ontable ?object")){
					String objectName = uni.getVarBindings().get("?object");	//オブジェクト名を取得
					if(!initialState.contains("clear " + objectName)){
						initialState.add(i,"clear " + objectName);	//上に何もない状態を追加
						i++;	//追加分をずらす
					}
				}
			}
		}else{
			initialState = new Vector<String>();
			for(int i = 0 ; i < 8 ; i++){	//色と形情報を付与
				initialState.addElement(objectTag[i] + " is-a Red color");
				initialState.addElement(objectTag[i] + " is-a Square shape");
				initialState.addElement("ontable " + objectTag[i]);
				initialState.addElement("clear " + objectTag[i]);
				initialState.addElement("placeable " + objectTag[i]);
			}
		}
		initialState.addElement("handEmpty");
		return initialState;
	}
	
	//現在の状態に対しエラーチェックをおこないエラーメッセージを取得する
	public ArrayList<String> errorCheck(){
		boolean holding = false;	//手に関する記述があるかどうか
		ErrorChecker ec[] = new ErrorChecker[8];
		for(int i = 0 ; i < 8 ; i++){
			ec[i] = new ErrorChecker(objectTag[i]);	//エラー発見用クラス生成
		}
		Vector<String> state = getCurrentState();
		ArrayList<String> errorList = new ArrayList<String>();
		if(state != null){
			for(int i = 0 ; i < state.size() ; i++){	//状態調査
				String stateData = state.elementAt(i);		//状態Vectorからデータを取得
				Unifier uni = new Unifier();		//マッチング用Unifierクラス
				if( uni.unify(stateData , "?object1 on ?object2")){
					int objnum1 = getObjectNameNumber(uni.getVarBindings().get("?object1"));	//オブジェクト番号取得
					int objnum2 = getObjectNameNumber(uni.getVarBindings().get("?object2"));	//オブジェクト番号取得
					if(objnum1 != -1 && objnum2 != -1){	//存在する場合
						ec[objnum1].setUnder();	//下オブジェクトが存在
						ec[objnum2].setAbove();	//上オブジェクトが存在
					}else{	//エラー
						if(objnum1 ==-1) errorList.add(i + "番目:オブジェクト名" + uni.getVarBindings().get("?object1") + "は存在しません。");
						if(objnum2 ==-1) errorList.add(i + "番目:オブジェクト名" + uni.getVarBindings().get("?object2") + "は存在しません。");
					}
				}else if(uni.unify(stateData , "holding ?object")){
					if(holding){	//エラー
						errorList.add("holding XおよびhandEmpty状態はあわせて1つである必要があります。");
					}
					holding = true;
				}else if(uni.unify(stateData , "ontable ?object")){
					int objnum = getObjectNameNumber(uni.getVarBindings().get("?object"));	//オブジェクト番号取得
					if(objnum != -1){	//存在する場合
						ec[objnum].setUnder();	//下オブジェクトが存在
					}else{	//エラー
						errorList.add(i + "番目:オブジェクト名" + uni.getVarBindings().get("?object") + "は存在しません。");
					}
				}else if(uni.unify(stateData , "clear ?object")){
					int objnum = getObjectNameNumber(uni.getVarBindings().get("?object"));	//オブジェクト番号取得
					if(objnum != -1){	//存在する場合
						ec[objnum].setClear();	//下オブジェクトが存在
					}else{	//エラー
						errorList.add(i + "番目:オブジェクト名" + uni.getVarBindings().get("?object") + "は存在しません。");
					}
				}else if(uni.unify(stateData , "placeable ?object")){
					int objnum = getObjectNameNumber(uni.getVarBindings().get("?object"));	//オブジェクト番号取得
					if(objnum != -1){	//存在する場合
						ec[objnum].setPlaceable();	//下オブジェクトが存在
					}else{	//エラー
						errorList.add(i + "番目:オブジェクト名" + uni.getVarBindings().get("?object") + "は存在しません。");
					}
				}else if(uni.unify(stateData , "?object is-a ?shape shape")){
					int objnum = getObjectNameNumber(uni.getVarBindings().get("?object"));	//オブジェクト番号取得
					if(objnum != -1){	//存在する場合
						ec[objnum].setShape();	//下オブジェクトが存在
						if(uni.getVarBindings().get("?shape").equals("Triangle")){	//三角形の場合
							ec[objnum].setTriangle();	//三角形である
						}
					}else{	//エラー
						errorList.add(i + "番目:オブジェクト名" + uni.getVarBindings().get("?object") + "は存在しません。");
					}
				}else if(uni.unify(stateData , "?object is-a ?color color")){
					int objnum = getObjectNameNumber(uni.getVarBindings().get("?object"));	//オブジェクト番号取得
					if(objnum != -1){	//存在する場合
						ec[objnum].setColor();	//下オブジェクトが存在
					}else{	//エラー
						errorList.add(i + "番目:オブジェクト名" + uni.getVarBindings().get("?object") + "は存在しません。");
					}
				}else if(uni.unify(stateData , "handEmpty")){
					if(holding){	//エラー
						errorList.add("holding XおよびhandEmpty状態はあわせて1つである必要があります。");
					}
					holding = true;
				}
			}
			for(int i = 0 ; i < 8 ; i++){	//各オブジェクトに対して調査
				ec[i].scan();	//エラー分析
				ArrayList<String> objectError = ec[i].getErrorMessage();	//個々のエラーメッセージ
				for(int j = 0 ; j < objectError.size() ; j++){
					errorList.add(objectError.get(j));	//追加
				}
			}
		}
		return errorList;
	}
	
	//エラーチェック用　オブジェクト名から番号を得る
	private int getObjectNameNumber(String name){
		for(int i = 0 ; i < 8 ; i++){
			if(objectTag[i].equals(name)) return i;
		}
		return -1;
	}
	
	//与えられた状態から目標状態を生成
	public static Vector<String> makeGoalState(Vector<String> state){
		Vector<String> goal = new Vector<String>();
		for( int i = 0 ; i < state.size() ; i++ ){
			if((new Unifier()).unify(state.elementAt(i), "ontable ?x")
					|| (new Unifier()).unify(state.elementAt(i), "?x on ?y")){
				goal.add(state.elementAt(i));	//目標状態に追加
			}
		}
		return goal;
	}
	
	//与えられた状態をすべて原始名に変換(state_org : 変換する状態 , tm : オブジェクト名対応用TableManager)
	public static Vector<String> convertState(Vector<String> state_org , TableManager tm){
		Vector<String> state = new Vector<String>();
		if(state_org != null){
			for(int i = 0 ; i < state_org.size() ; i++){
				String stateData = state_org.elementAt(i);		//状態Vectorからデータを取得
				Unifier uni = new Unifier();		//マッチング用Unifierクラス
				if( uni.unify(stateData , "?object1 on ?object2")){
					BlockManager obj1 = tm.getObject(uni.getVarBindings().get("?object1"));	//オブジェクト名を取得
					BlockManager obj2 = tm.getObject(uni.getVarBindings().get("?object2"));	//オブジェクト名を取得
					if(obj1 != null && obj2 != null) state.addElement(obj1.getName() + " on " + obj2.getName());	//変換してから追加
				}else if(uni.unify(stateData , "holding ?object")){
					BlockManager obj = tm.getObject(uni.getVarBindings().get("?object"));	//オブジェクト名を取得
					if(obj != null) state.addElement("holding " + obj.getName());	//変換してから追加
				}else if(uni.unify(stateData , "ontable ?object")){
					BlockManager obj = tm.getObject(uni.getVarBindings().get("?object"));	//オブジェクト名を取得
					if(obj != null) state.addElement("ontable " + obj.getName());	//変換してから追加
				}else if(uni.unify(stateData , "clear ?object")){
					BlockManager obj = tm.getObject(uni.getVarBindings().get("?object"));	//オブジェクト名を取得
					if(obj != null) state.addElement("clear " + obj.getName());	//変換してから追加
				}else if(uni.unify(stateData , "placeable ?object")){
					BlockManager obj = tm.getObject(uni.getVarBindings().get("?object"));	//オブジェクト名を取得
					if(obj != null) state.addElement("placeable " + obj.getName());	//変換してから追加
				}else if(uni.unify(stateData , "?object is-a ?shape shape")){
					BlockManager obj = tm.getObject(uni.getVarBindings().get("?object"));	//オブジェクト名を取得
					if(obj != null) state.addElement(obj.getName() + " is-a " + uni.getVarBindings().get("?shape") + " shape");	//変換してから追加
				}else if(uni.unify(stateData , "?object is-a ?color color")){
					BlockManager obj = tm.getObject(uni.getVarBindings().get("?object"));	//オブジェクト名を取得
					if(obj != null) state.addElement(obj.getName() + " is-a " + uni.getVarBindings().get("?color") + " color");	//変換してから追加
				}
			}
		}
		return state;
	}
}