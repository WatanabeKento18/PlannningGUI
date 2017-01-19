import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translation {
	
	//英語変換結果
	String result;
	
	//ユーザーが指定した名前を8つ記憶する
	String[] user_name = new String[8];
	 //色および名前認識用配列
	String[] color_name1 = new String[24];
	//色およびなめ英語変換用配列
	String[] color_name2 = new String[24];
	
	//pat1は　(色)+(色)
	//pat2は　(色)+(名前)
	//pat3は　(名前)+(色)
	//pat4は　(名前)+(名前)	
	Pattern pat;
	Matcher mat;
	
	public Translation(){
		for(int i = 0 ; i < 8 ; i++ )  user_name[i] = "dammy";
	}
	
	public void setUsername(int code , String name){
		user_name[code] = name;
	}
	
	public String translation(String text){
		 
		result = " ";

		color_name1[0]="赤";
		color_name1[1]="緑";
		color_name1[2]="青";
		color_name1[3]="白";
		color_name1[4]="ピンク";
		color_name1[5]="紫";
		color_name1[6]="オレンジ";
		color_name1[7]="黄";
		color_name1[8]="A";
		color_name1[9]="B";
		color_name1[10]="C";
		color_name1[11]="D";
		color_name1[12]="E";
		color_name1[13]="F";
		color_name1[14]="G";
		color_name1[15]="H";
		

		color_name2[0]="red ";
		color_name2[1]="green ";
		color_name2[2]="blue ";
		color_name2[3]="white ";
		color_name2[4]="pink ";
		color_name2[5]="purple ";
		color_name2[6]="orange ";
		color_name2[7]="yellow ";
		color_name2[8]="A ";
		color_name2[9]="B ";
		color_name2[10]="C ";
		color_name2[11]="D ";
		color_name2[12]="E ";
		color_name2[13]="F ";
		color_name2[14]="G ";
		color_name2[15]="H ";
		
		for(int i = 0 ; i < 8 ; i++){
			if(user_name[i].equals("")||user_name[i].equals(" ")){
				System.out.println("ユーザー指定ブロック名が正しくありません");
				return null;
			}
			color_name1[16 + i]=user_name[i];
			color_name2[16 + i]=user_name[i];
		}
		
		

				
		for(int i = 0; i < color_name1.length; i++){
			for(int j = 0; j < color_name1.length; j++){
				//以下は操作に対応
				
		        //　(色or名前)の上に(色or名前)を乗せる　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"上"+".*"+color_name1[j]+".*"+"乗"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "Place "+color_name2[j]+"on "+color_name2[i];
		        	System.out.println(result);
		        	return result;
		        }
						      
		        //　(色or名前)を(色or名前)の上に乗せる　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+color_name1[j]+".*"+"上"+".*"+"乗"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "Place "+color_name2[i]+"on "+color_name2[j];
		        	System.out.println(result);
		        	return result;
		        }
		          
		        //　(色or名前)の上に(色or名前)を置く　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"上"+".*"+color_name1[j]+".*"+"置"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "Place "+color_name2[j]+"on "+color_name2[i];
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　(色or名前)を(色or名前)の上に置く　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+color_name1[j]+".*"+"上"+".*"+"置"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "Place "+color_name2[i]+"on "+color_name2[j];
		        	System.out.println(result);
		        	return result;
		        }
				
		        //　(色or名前)の上から(色or名前)を消す　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"上"+".*"+color_name1[j]+".*"+"消"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "remove "+color_name2[j]+"from on "+color_name2[i];
		        	System.out.println(result);
		        	return result;
		        }
		        //　(色or名前)を(色or名前)の上から消す　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+color_name1[j]+".*"+"上"+".*"+"消"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "remove "+color_name2[i]+"from on "+color_name2[j];
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　(色or名前)をテーブルの上から取る　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"テーブル"+"取"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "pick up "+color_name2[i]+"from the table";
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　テーブルの上の(色or名前)を取る　に対応
		        pat = Pattern.compile("テーブル"+".*"+color_name1[i]+".*"+"取"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "pick up "+color_name2[i]+"from the table";
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　(色or名前)をテーブルの上に乗せる　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"テーブル"+"乗"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "put "+color_name2[i]+"down on the table";
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　テーブルの上に(色or名前)を乗せる　に対応
		        pat = Pattern.compile("テーブル"+".*"+color_name1[i]+".*"+"乗"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "put "+color_name2[i]+"down on the table";
		        	System.out.println(result);
		        	return result;
		        }

		        //　(色or名前)をテーブルの上に置く　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"テーブル"+".*"+"置"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "put "+color_name2[i]+"down on the table";
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　テーブルの上に(色or名前)を置く　に対応
		        pat = Pattern.compile("テーブル"+".*"+color_name1[i]+".*"+"置"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "put "+color_name2[i]+"down on the table";
		        	System.out.println(result);
		        	return result;
		        }
		        
		        
		        //以下は初期状態、目的状態に対応
				
				//　(色or名前)の上に(色or名前)　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"上"+".*"+color_name1[j]+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = color_name2[j]+"on "+color_name2[i];
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　(色or名前)は(色or名前)の上　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"は"+color_name1[j]+".*"+"上"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = color_name2[i]+"on "+color_name2[j];
		        	System.out.println(result);
		        	return result;
		        }
		        
		        //　(色or名前)はテーブルの上　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"テーブル"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "ontable "+color_name2[i];
		        	System.out.println(result);
		        	return result;
		        }
	
		        //　(色or名前)は机の上　に対応
		        pat = Pattern.compile(color_name1[i]+".*"+"机"+".*");
		        mat = pat.matcher(text);
		        if (mat.find()){
		        	result = "ontable "+color_name2[i];
		        	System.out.println(result);
		        	return result;
				}        		        
			}
		}
	 
		System.out.println("英語変換出来ませんでした");
		return null;
	}



}
