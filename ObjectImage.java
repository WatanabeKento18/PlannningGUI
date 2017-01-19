import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

/**
 *			オブジェクトごとの描画に必要な情報 
 **/

class ObjectImage{
	BufferedImage img = null;	//画像
	int posX , posY;	//描画座標
	int sizeX , sizeY;	//描画サイズ
	boolean enable;	//画像を表示するかしないか
	
	//コンストラクタでファイルのロード
	public ObjectImage(String fileName) {
		// 画像の取得
		loadImage(new File(fileName));
		
		// 表示位置の初期値
		posX = posY = 0;
		
		//最初は非表示
		enable = false;
	}
	
	// ファイルのロード
	public void loadImage(File imagefile) {
		// 画像の取得
		try {
			img = ImageIO.read(imagefile);
		} catch (IOException ioe) {
			System.out.println("ファイル名[ " + imagefile.getName() + " ]は存在しません"); // エラー
		}

		// サイズの初期値
		sizeX = img.getWidth();
		sizeY = img.getHeight();
	}

	// サイズを限定してファイルのロード
	public void loadObjectImage(File imagefile) {
		// 画像の取得
		try {
			img = ImageIO.read(imagefile);
		} catch (IOException ioe) {
			System.out.println("ファイル名[ " + imagefile.getName() + " ]は存在しません"); // エラー
		}

		// サイズの初期値
		sizeX = img.getWidth();
		sizeY = img.getHeight();
		if(sizeX > 64) sizeX = 64;
		if(sizeY > 64) sizeY = 64;
	}
	
	//座標をPointクラスで返す
	public Point getPosition(){
		return new Point(posX,posY);
	}
	
	//座標を設定する
	public void setPosition(int x , int y){
		posX = x;
		posY = y;
		enable = true;
		//System.out.println("YO!!");
	}
	
	//画像を非表示にする
	public void disableImage(){
		enable = false;
	}
	
	//外部の描画メソッドよりGraphicsを取得し描画
	public void externalPaint(ImageObserver observer , Graphics g){
		if(enable) g.drawImage(img, posX, posY, sizeX, sizeY, observer);
	}
}