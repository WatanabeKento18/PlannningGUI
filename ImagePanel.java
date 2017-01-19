import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
 * 		画像を一括表示するクラス
 **/

class ImagePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final String objectTag[] = {"A","B","C","D","E","F","G","H"};	//オブジェクトのタグ一覧
	
	HashMap<String,ObjectImage> objectImage;

	//コンストラクタ
	public ImagePanel() {
		objectImage = new HashMap<String,ObjectImage>(); //個々の画像データを保存するHashMap
		setSize(800, 600); // パネルのサイズ
		setBackground(Color.black); // パネルの背景色
	}
	
	//画像データを追加
	public void addObjectImage(String fileName , String fileKey){
		if(objectImage.containsKey(fileKey)){	//すでに同名のファイルが存在する場合
			objectImage.remove(fileKey);	//同名の画像データを削除
		}
		objectImage.put(fileKey, new ObjectImage(fileName));
	}
	
	//画像データを非表示に設定
	public void disableObjectImage(String fileKey){
		ObjectImage image = getObjectImage(fileKey);
		if(image != null) image.disableImage(); 
	}
	
	//画像データを取得
	public ObjectImage getObjectImage(String fileKey){
		return objectImage.get(fileKey);
	}
	
	//描画をおこなうメソッド
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.white);
		g.fillRect(0, 0, getSize().width, getSize().height);
		objectImage.get("Back").externalPaint(this, g);
		objectImage.get("Table").externalPaint(this, g);
		objectImage.get("Mode").externalPaint(this, g);
		
		//各オブジェクト
		for(int i = 0 ; i < 8 ; i++){
			ObjectImage obj = objectImage.get(objectTag[i]);
			if(obj != null) obj.externalPaint(this, g);
		}
		
		objectImage.get("Selector").externalPaint(this, g);
		objectImage.get("Hand").externalPaint(this, g);
		objectImage.get("Mode").externalPaint(this, g);
	}
}
