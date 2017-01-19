import java.awt.*;
import java.io.*;

/**
 *		個々のオブジェクトを管理するためのクラス
 **/

//色を表す定数
enum BlockColor{RED , BLUE , GREEN , YELLOW , PURPLE , PINK , ORANGE , WHITE}

//形を表す定数
enum BlockShape{SQUARE , HALF , TRIANGLE}

//BlockManagerクラス
class BlockManager extends BlockBase{
	ObjectImage image;	//このオブジェクトの画像情報
	boolean usedefault;	//画像ファイルをデフォルトにするかどうか
	String filePath;	//画像ファイルのパス
	BlockColor color;	//色
	BlockShape shape;	//形
	String tag;			//タグ
	int worldPosX , worldPosY;	//ワールド位置
	
	//BlockManagerのコンストラクタ
	BlockManager(String objectName){
		setName(objectName);				//名前の設定
		state = BlockState.NotExist;	//非表示ブロックに設定
		color = BlockColor.RED;			//赤に設定
		shape = BlockShape.SQUARE;		//正方形ブロックに設定
		tag = "";
		usedefault = true;	//デフォルトを使用
		//System.out.println(image);
	}
	
	//更新
	public void update(){
		if(isEnableImage()) image.setPosition(worldPosX, worldPosY);
	}
	
	//初期化
	public void initSettings(){
		state = BlockState.NotExist;
		aboveObject = null;
		underObject = null;
	}
	
	//画像情報を登録
	public void initObjectImage(TableManager tm){
		if(usedefault){
			String fileName = getFileName();
			tm.getImagePanel().addObjectImage(fileName, name);	//画像データを登録
			image = tm.getImagePanel().getObjectImage(name);	//登録した画像データを保存
			filePath = fileName;	//パス設定
		}
	}
	
	//表示切替
	public void setEnable(boolean flag){
		if(flag && state == BlockState.NotExist) state = BlockState.Placed;
		if(!flag) state = BlockState.NotExist;
		if(!isEnableImage()) image.disableImage();
	}
	
	//対応する画像ファイル名を取得
	private String getFileName(){
		return getFileName(shape , color);
	}
	
	public static String getFileName(BlockShape shape , BlockColor color){
		String fileName = "img/";
		//色
		if(color == BlockColor.RED) fileName+="r";
		else if(color == BlockColor.BLUE) fileName+="b";
		else if(color == BlockColor.GREEN) fileName+="g";
		else if(color == BlockColor.YELLOW) fileName+="y";
		else if(color == BlockColor.PURPLE) fileName+="p";
		else if(color == BlockColor.PINK) fileName+="m";
		else if(color == BlockColor.ORANGE) fileName+="o";
		else if(color == BlockColor.WHITE) fileName+="w";
		//間
		fileName += "_";
		//形
		if(shape == BlockShape.SQUARE) fileName+="s";
		else if(shape == BlockShape.HALF) fileName+="h";
		else if(shape == BlockShape.TRIANGLE) fileName+="t";
		//拡張子
		fileName += ".png";
		return fileName;
	}
	
	//座標の設定
	public void setPosition(int x , int y){
		worldPosX = x;
		worldPosY = y;
	}
	
	//座標を取得
	public Point getPosition(){
		return new Point(worldPosX , worldPosY);
	}
	
	//形を設定
	public void setShape(BlockShape s){
		shape = s;
	}
	
	//形を取得
	public BlockShape getShape(){
		return shape;
	}
	
	//形の名前を取得
	public String getShapeName(){
		return getShapeName(shape);
	}
	
	//色を設定
	public void setColor(BlockColor c){
		color = c;
	}
		
	//色を取得
	public BlockColor getColor(){
		return color;
	}
	
	//色の名前を取得
	public String getColorName(){
		return getColorName(color);
	}
	
	//縦サイズを取得
	public int getHeight(){
		if(shape == BlockShape.HALF) return 32;	//半分サイズの場合
		return 64;
	}
	
	//オブジェクトの説明用文字列を取得
	public String getGuide(){
		String result = new String("【名前 : ");
		result += getName();
		result += " 】 【色 : ";
		result += getColorName();
		result += " 】 【形 : ";
		result += getShapeName();
		result += " 】【タグ : ";
		if(!tag.equals("")) result += tag;
		else result += "未登録";
		result += " 】";
		return result;
	}
	
	//タグを設定
	public void setTag(String tagName){
		tag = tagName;
	}
	
	//タグを取得
	public String getTag(){
		return tag;
	}
	
	//画像ファイルをオリジナルのものに設定
	public void setDefaultImage(){
		usedefault = true;
		image.loadObjectImage(new File(getFileName()));
		filePath = getFileName();	//パス設定
	}
	
	//画像ファイルを設定
	public void setImage(File file){
		usedefault = false;
		image.loadObjectImage(file);
		filePath = file.getPath();	//パス設定
	}
	
	//画像ファイルのパスを取得
	public String getFilePath(){
		return filePath;
	}
	
	//このブロックを手に取る
	public void catchObject(){
		underObject.aboveObject = null;	//下のオブジェクトは上に何もないことにする
		state = BlockState.Holding;		//持っている状態へ
	}

	//現在表示されているかどうか
	public boolean isEnableImage(){
		return state != BlockState.NotExist;
	}
	
	//指定の文字列がこのオブジェクトを指しているかどうか
	public boolean isObjectName(String str_org){
		String str = str_org.toLowerCase();	//小文字同士で比較
		if(str.equals(name.toLowerCase())) return true;	//名前と一致した場合
		if(str.equals(BlockManager.getColorName(color))) return true;	//色の名前と一致した場合
		if(tag != null) if(str.equals(tag.toLowerCase())) return true;	//タグと一致した場合
		return false;
	}
	
	//形を表す単語を返す
	public static String getShapeName(BlockShape s){
		if(s == BlockShape.SQUARE) return "Square";
		else if(s == BlockShape.HALF) return "halfBlock";
		else if(s == BlockShape.TRIANGLE) return "Triangle";
		return null;
	}
	
	//色を表す単語を返す
	public static String getColorName(BlockColor c){
		if(c == BlockColor.RED) return "red";
		else if(c == BlockColor.BLUE) return "blue";
		else if(c == BlockColor.GREEN) return "green";
		else if(c == BlockColor.YELLOW) return "yellow";
		else if(c == BlockColor.PURPLE) return "purple";
		else if(c == BlockColor.PINK) return "pink";
		else if(c == BlockColor.ORANGE) return "orange";
		else if(c == BlockColor.WHITE) return "white";
		return null;
	}
}
