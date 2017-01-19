import java.util.*;

/**
 *		状態の不正を発見するための情報を保持するクラス
 **/

//状態の異常を検知する
public class ErrorChecker {
	String name; // 表示名
	boolean up, down; // 上下にオブジェクトはあるかどうか
	boolean clear, placeable; // 上に何もない/配置可能
	boolean color, shape, triangle; // 色/形を決定しているか,三角形であるかどうか
	boolean error; // 1つでもエラーがある場合
	ArrayList<String> errorList; // エラー内容

	// コンストラクタ
	public ErrorChecker(String initname) {
		name = initname;
		up = down = clear = placeable = color = shape = triangle = error = false;
		errorList = new ArrayList<String>();
	}

	// 上にオブジェクトがある場合
	public void setAbove() {
		if (!up)
			up = true;
		else {
			error = true;
			errorList.add(name + "の上に配置されているオブジェクトが複数あります。");
		}
	}

	// 下にオブジェクトがある場合
	public void setUnder() {
		if (!down)
			down = true;
		else {
			error = true;
			errorList.add(name + "が複数あります。");
		}
	}

	// 上に何もない場合
	public void setClear() {
		if (!clear)
			clear = true;
		else {
			error = true;
			errorList.add("clear " + name + "が複数記述されています。");
		}
	}

	// 上にオブジェクトを置ける場合
	public void setPlaceable() {
		if (!placeable)
			placeable = true;
		else {
			error = true;
			errorList.add("placeable " + name + "が複数記述されています。");
		}
	}

	// 色情報の登録
	public void setColor() {
		if (!color)
			color = true;
		else {
			error = true;
			errorList.add(name + "の色情報が複数記述されています。");
		}
	}

	// 形情報の登録
	public void setShape() {
		if (!shape)
			shape = true;
		else {
			error = true;
			errorList.add(name + "の形情報が複数記述されています。");
		}
	}
	
	// 三角形情報の登録
	public void setTriangle(){
		triangle = true;
	}

	// エラーチェックをおこなう
	public void scan() {
		if (!down && up) {
			error = true;
			errorList.add(name + "は配置されていませんが、上にオブジェクトが存在します。");
		}
		if (up && clear) {
			error = true;
			errorList.add(name + "の上にオブジェクトが存在するにも関わらずclear " + name
					+ "が記述されています。");
		}
		if (up && placeable) {
			error = true;
			errorList.add(name + "の上にオブジェクトが存在するにも関わらずplaceable " + name
					+ "が記述されています。");
		}
		if (down && !up && !clear) {
			error = true;
			errorList.add(name + "の上にオブジェクトが存在しないにも関わらずclear " + name
					+ "が記述されていません。");
		}
		if (down && !up && !placeable && !triangle) {
			error = true;
			errorList.add(name + "の上にオブジェクトを置けるにも関わらずplaceable " + name
					+ "が記述されていません。");
		}
		if (placeable && triangle) {
			error = true;
			errorList.add(name + "の上にオブジェクトを置けないにも関わらずplaceable " + name
					+ "が記述されています。");
		}
		if (down && !color) {
			error = true;
			errorList.add(name + "の色情報が記述されていません。");
		}
		if (down && !shape) {
			error = true;
			errorList.add(name + "の形情報が記述されていません。");
		}
	}
	
	//エラーメッセージを取得する、無い場合はnullを返す
	public ArrayList<String> getErrorMessage(){
		return errorList;
	}
}