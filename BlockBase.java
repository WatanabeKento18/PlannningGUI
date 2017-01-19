/**
 *		個々のオブジェクトを管理するためのクラス
 **/
		
	
//状態定数(Base : 土台用 , NotExist : 画面に存在しない , Placed : 配置されている , Holding : 手に持っている)
enum BlockState{ Base , NotExist , Placed , Holding };

//BlockBaseクラス（オブジェクトの基本的な要素）
class BlockBase{
	int baseCode;			//オブジェクトの位置	
	String name;			//オブジェクトの名前
	BlockState state;		//オブジェクトの状態
	BlockManager aboveObject;	//この上にあるオブジェクト
	BlockManager underObject;	//この下にあるオブジェクト
	
	//BlockBaseのコンストラクタ
	BlockBase(){}	//BlockManager用
	BlockBase(int code){
		baseCode = code;
		name = "BaseObject"+code;
		state = BlockState.Base;	//土台ブロックに設定
	}
	
	//上にブロックを配置
	public void placeAboveObject(BlockManager obj){
		aboveObject = obj;		//上のオブジェクトに追加
		if(this instanceof BlockManager){			//BlockManagerクラスの場合
			obj.underObject = (BlockManager)this;	//下のオブジェクトとして追加
		}else{
			obj.underObject = null;
		}
		obj.state = BlockState.Placed;	//配置状態へ
		setUpperBaseCode();		//baseCodeを更新
	}
	
	//このオブジェクトを持っている状態へ移行
	public void holding(){
		if(underObject != null)	//下がある場合
			underObject.aboveObject = null;	//下のオブジェクトの上を空に変更
		state = BlockState.Holding;		//持っている状態へ
	}
	
	//名前を取得
	public String getName(){
		return name;
	}
	
	//土台のコードを取得
	public int getBaseCode(){
		return baseCode;
	}
	
	//上にあるオブジェクトを取得
	public BlockManager getAboveBlock(){
		return aboveObject;
	}
	
	//下にあるオブジェクトを取得
	public BlockManager getUnderBlock(){
		return underObject;
	}
	
	//上にあるオブジェクトを設定
	public void setAboveBlock(BlockManager above){
		aboveObject = above;
	}
	
	//下にあるオブジェクトを設定
	public void setUnderBlock(BlockManager under){
		underObject = under;
	}
	
	//一番上にあるオブジェクトを取得
	public BlockManager getTopBlock(){
		BlockManager topBlock = aboveObject;	//このオブジェクトの上を起点
		if(aboveObject == null) return null;	//何も返さない
		while(topBlock.getAboveBlock() != null){	//上がある場合
			topBlock = topBlock.getAboveBlock();	//上のオブジェクトを調べる
		}
		return topBlock;
	}
	
	//baseCodeを設定
	public void setBaseCode(int code){
		baseCode = code;
	}
	
	//baseCodeを上のオブジェクト全てに設定
	void setUpperBaseCode(){
		BlockBase topBlock = this;	//このオブジェクトを起点
		while(topBlock.getAboveBlock() != null){	//上がある場合
			topBlock = topBlock.getAboveBlock();	//上のオブジェクトを調べる
			topBlock.baseCode = baseCode;	//baseCodeを設定
		}
	}
	
	//上にあるオブジェクトをすべて表示
	public void printUpperObject(){
		System.out.print("BASECODE : " + baseCode +" [ ");
		BlockBase topBlock = this;
		while(topBlock.getAboveBlock() != null){
			topBlock = topBlock.getAboveBlock();	//上のオブジェクトを調べる
			System.out.print(topBlock.name + "(" +topBlock.getBaseCode() + ")");
			if(topBlock.getAboveBlock() == null) break;
			System.out.print(" , ");
		}
		System.out.println(" ]");
	}
	
	//名前を設定
	protected void setName(String theName){
		name = theName;
	}
}
