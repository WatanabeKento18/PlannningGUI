import java.awt.*;
import java.util.*;

/**
 *		手がおこなうべき処理をまとめたクラス
**/

//処理の種類
//MOVEX_OBJECT : 指定オブジェクトにX座標をあわせる , MOVEX_TABLE : 指定座標へX座標をあわせる 
//MOVEY_EMPTY : 指定オブジェクトにY座標をあわせる , MOVEY_HOLD : 指定オブジェクトの一つ上にY座標をあわせる
//MOVEY_TABLE : テーブルの上へY座標をあわせる , RETURN : 一番上まで手を戻す
//CATCH : 指定オブジェクトをつかむ , RELEASE : 指定オブジェクトの一つで持っているオブジェクトを離す , OPEN : 手を開き数フレーム待機
enum TaskType{MOVEX_OBJECT , MOVEX_TABLE , MOVEY_EMPTY , MOVEY_HOLD , MOVEY_TABLE , RETURN , CATCH  , RELEASE , OPEN}

class HandTask{
	TaskType taskType;	//処理内容
	BlockManager targetObject;	//目標となるオブジェクト
	int tablePosition;			//置くテーブルの位置
	int waitframe;				//待機フレーム数
	boolean taskCompleted;		//処理が終了したかどうか
	TableManager tableManager;	//置く,持つ操作をおこなうためのテーブル管理クラス
	static Random random = new Random();	//乱数発生用クラス
	
	HandTask(TaskType type , BlockManager target , TableManager tm){
		taskType = type;
		targetObject = target;
		taskCompleted = false;
		tableManager = tm;
		if(taskType == TaskType.MOVEX_OBJECT){	//オブジェクト上へ移動の場合
			tablePosition = random.nextInt(10)-6;	//左右のズレを指定
		}
		if(taskType == TaskType.OPEN){	//手を開く場合
			waitframe = 20;	//20フレーム待機
		}
	}
	
	HandTask(TaskType type , int position , TableManager tm){
		taskType = type;
		tablePosition = position;
		taskCompleted = false;
		tableManager = tm;
	}
	
	//現在座標から処理にあわせた動きをおこない,実行後の座標を返す
	public Point process(int posX , int posY){
		if(!taskCompleted){	//処理が終了している場合は何もしない
			BlockManager holdingObj = tableManager.getHoldingObject();	//持っているオブジェクトを取得
			int triangleFixedY = 0;	//三角形用調整Y座標
			
			//左右移動の場合
			if(taskType == TaskType.MOVEX_OBJECT){
				//指定オブジェクトの方へ向かって移動
				if(targetObject.getPosition().x + tablePosition > posX){
					posX++;
				}else if(targetObject.getPosition().x + tablePosition< posX){
					posX--;
				}
				//左右の座標がそろった場合
				if(targetObject.getPosition().x + tablePosition == posX){
					taskCompleted = true;
				}
			}
			
			//左右移動の場合
			if(taskType == TaskType.MOVEX_TABLE){
				//指定オブジェクトの方へ向かって移動
				if(tablePosition > posX){
					posX++;
				}else if(tablePosition < posX){
					posX--;
				}
				//左右の座標がそろった場合
				if(tablePosition == posX){
					taskCompleted = true;
				}
			}
			
			//オブジェクト上へ移動の場合
			if(taskType == TaskType.MOVEY_EMPTY){
				if(targetObject.getShape() == BlockShape.TRIANGLE){	//三角形の場合やや下にずらす
					triangleFixedY = 30;
				}
				//指定オブジェクトの方へ向かって移動
				if(targetObject.getPosition().y + triangleFixedY > posY){
					posY++;
				}else if(targetObject.getPosition().y + triangleFixedY < posY){
					posY--;
				}
				//上下の座標がそろった場合
				if(targetObject.getPosition().y + triangleFixedY == posY){
					taskCompleted = true;
				}
			}
			
			//オブジェクトの1つ上へ移動の場合
			if(taskType == TaskType.MOVEY_HOLD){
				if(holdingObj.getShape() == BlockShape.TRIANGLE){	//三角形の場合やや下にずらす
					triangleFixedY = 30;
				}
				//指定オブジェクトの方へ向かって移動
				if(targetObject.getPosition().y + triangleFixedY - holdingObj.getHeight() > posY){
					posY++;
				}else if(targetObject.getPosition().y + triangleFixedY - holdingObj.getHeight() < posY){
					posY--;
				}
				//上下の座標がそろった場合
				if(targetObject.getPosition().y + triangleFixedY - holdingObj.getHeight() == posY){
					taskCompleted = true;
				}
			}
			
			//テーブル上へ移動の場合
			if(taskType == TaskType.MOVEY_TABLE){
				if(holdingObj.getShape() == BlockShape.TRIANGLE){	//三角形の場合やや下にずらす
					triangleFixedY = 30;
				}
				//指定オブジェクトの方へ向かって移動
				posY++;
				//上下の座標がそろった場合
				if(posY == 500 + triangleFixedY - holdingObj.getHeight()){
					taskCompleted = true;
				}
			}
			
			//元の位置へ帰る場合
			if(taskType == TaskType.RETURN){
				//指定オブジェクトの方へ向かって移動
				if(10 > posY){
					posY++;
				}else if(10 < posY){
					posY--;
				}
				//上下の座標がそろった場合
				if(posY == 100){
					taskCompleted = true;
				}
			}
			
			//オブジェクトを持つ場合
			if(taskType == TaskType.CATCH){
				tableManager.setHoldingObject(targetObject);	//目標とするオブジェクトを手に持つ
				tableManager.addImage("img/hand1.png","Hand");	//手の画像を変更
				taskCompleted = true;
			}
			
			//オブジェクトを置く場合
			if(taskType == TaskType.RELEASE){
				tableManager.releaseHoldingObject();		//手に持っているオブジェクトを指定の座標で離す
				tableManager.addImage("img/hand2.png","Hand");	//手の画像を変更
				taskCompleted = true;
			}
			
			//手を開き数フレーム待機する
			if(taskType == TaskType.OPEN){
				if(waitframe == 20) tableManager.addImage("img/hand2.png","Hand");	//手の画像を変更
				if(waitframe == 0) taskCompleted = true;	//待機終了
				waitframe--;	//待機フレーム減少
			}
					
		}
		return new Point(posX , posY);	//最終的な座標を返す
	}
	
	//処理が終了したかどうか
	public boolean isTaskCompleted(){
		return taskCompleted;
	}
	
	//オブジェクトを取る処理の追加
	public static ArrayList<HandTask> addPickUpTask(BlockManager pickUpObj , TableManager tm , ArrayList<HandTask> taskList){
		taskList.add(new HandTask(TaskType.MOVEX_OBJECT , pickUpObj , tm));	//左右移動
		taskList.add(new HandTask(TaskType.OPEN , pickUpObj , tm));	//手を開く
		taskList.add(new HandTask(TaskType.MOVEY_EMPTY , pickUpObj , tm));	//下移動
		taskList.add(new HandTask(TaskType.CATCH , pickUpObj , tm));	//つかむ
		taskList.add(new HandTask(TaskType.RETURN , pickUpObj , tm));	//上移動
		return taskList;
	}
	
	//オブジェクトをオブジェクト上へ置く処理の追加
	public static ArrayList<HandTask> addPutDownTask(BlockManager pickUpObj , TableManager tm , ArrayList<HandTask> taskList){
		taskList.add(new HandTask(TaskType.MOVEX_OBJECT , pickUpObj , tm));	//左右移動
		taskList.add(new HandTask(TaskType.MOVEY_HOLD , pickUpObj , tm));	//下移動
		taskList.add(new HandTask(TaskType.RELEASE , pickUpObj , tm));	//置く
		taskList.add(new HandTask(TaskType.RETURN , pickUpObj , tm));	//上移動
		return taskList;
	}
	
	//オブジェクトをテーブル上へ置く処理の追加
	public static ArrayList<HandTask> addPutDownTableTask(int code , TableManager tm , ArrayList<HandTask> taskList){
		taskList.add(new HandTask(TaskType.MOVEX_TABLE , 70 + code*80 , tm));	//左右移動
		taskList.add(new HandTask(TaskType.MOVEY_TABLE , null , tm));	//下移動
		taskList.add(new HandTask(TaskType.RELEASE , null , tm));	//置く
		taskList.add(new HandTask(TaskType.RETURN , null , tm));	//上移動
		return taskList;
	}
}