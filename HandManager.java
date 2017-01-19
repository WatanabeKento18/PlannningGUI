import java.awt.*;
import java.util.*;

/**
 *		オブジェクトを持つ手を管理するクラス
 **/

class HandManager{
	int posX , posY;	//手の座標
	BlockManager holdingObject;	//手に持っているオブジェクト
	TableManager tableManager;	//親となるテーブル管理クラス
	ArrayList<HandTask> taskList;	//処理をまとめたリスト
	boolean movingFlag;				//動いているかどうか
	int processCycle = 1;	//1ループでおこなう処理の回数	
	
	//コンストラクタ
	HandManager(TableManager tm){
		tableManager = tm;
		holdingObject = null;
		posX = 50;
		posY = 100;
		taskList = new ArrayList<HandTask>();
	}

	// 更新
	public void update() {
		movingFlag = true;	//アニメーション中にする
		for (int i = 0; i < processCycle || processCycle == 0; i++) {	//指定回数だけ処理をおこなう
			tableManager.setImagePosition("Hand", posX - 45, posY - 124);	//手の座標を設定
			if(isHolding()){
				if(holdingObject.getShape() == BlockShape.TRIANGLE) holdingObject.setPosition(posX, posY-30);	//三角形オブジェクトの座標を設定
				else holdingObject.setPosition(posX, posY);	//持っているオブジェクトの座標を設定
			}
			if (taskList.size() > 0) {	// 処理がある場合
				HandTask task = taskList.get(0); // 次の処理を取得
				Point newPos = task.process(posX, posY); // 処理をおこなう
				posX = newPos.x; // 座標の更新
				posY = newPos.y; // 座標の更新
				if (task.isTaskCompleted()) { // 処理が終了した場合
					taskList.remove(0); // 処理リストから削除
				}
			}else{	//処理がない場合は即終了
				movingFlag = false;	//アニメーション終了
				break;
			}
		}
	}
	
	//手の処理速度を設定する
	public void setHandSpeed(int spd){
		processCycle = spd;
	}
	
	//指定オブジェクトを取りに行かせる
	public void pickUpObject(BlockManager obj){
		taskList = HandTask.addPickUpTask(obj, tableManager, taskList);	//処理の追加
	}
	
	//指定オブジェクトの上に置きに行かせる
	public void putDownObject(BlockManager obj){
		taskList = HandTask.addPutDownTask(obj, tableManager, taskList);	//処理の追加
	}
	
	//テーブルの指定位置の上へ置きに行かせる
	public void putDownObjectTable(int setCode){
		taskList = HandTask.addPutDownTableTask(setCode, tableManager, taskList);	//処理の追加
	}
	
	//手に持っているオブジェクトの名前を得る
	public BlockManager getHoldingObject(){
		if(!isHolding()) return null;
		return holdingObject;
	}
	
	//手に持っているオブジェクトの名前を得る
	public String getHoldingName(){
		if(!isHolding()) return null;
		return holdingObject.getName();
	}
	
	//手に持っているかを返す
	public boolean isHolding(){
		return (holdingObject != null);
	}
	
	//手に持つオブジェクトを設定する
	public void setHolding(BlockManager object){
		holdingObject = object;
	}
	
	//手に持っているオブジェクトを置く
	public void releaseHolding(){
		holdingObject = null;	//持っているオブジェクトを消去
	}
	
	//アニメーション中かどうかを返す
	public boolean isMoving(){
		return movingFlag;
	}
}
