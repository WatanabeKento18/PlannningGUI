import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *				マッチングをおこなうクラス 
**/


class Unifier {
	StringTokenizer st1;
	String buffer1[];
	StringTokenizer st2;
	String buffer2[];
	Hashtable<String,String> vars;

	Unifier() {
		vars = new Hashtable();
	}

	public boolean unify(String string1, String string2, Hashtable<String,String> theBindings) {
		Hashtable<String,String> orgBindings = new Hashtable<String,String>();
		for (Enumeration<String> e = theBindings.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = (String) theBindings.get(key);
			orgBindings.put(key, value);
		}
		this.vars = theBindings;
		if (unify(string1, string2)) {
			return true;
		} else {
			// 螟ｱ謨励＠縺溘ｉ蜈�縺ｫ謌ｻ縺呻ｼ�
			theBindings.clear();
			for (Enumeration<String> e = orgBindings.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) orgBindings.get(key);
				theBindings.put(key, value);
			}
			return false;
		}
	}

	public boolean unify(String string1, String string2) {
		//this.vars = new Hashtable<String,String>();
		
		// 蜷後§縺ｪ繧画�仙粥
		if (string1.equals(string2))
			return true;

		// 蜷�縲�繝医�ｼ繧ｯ繝ｳ縺ｫ蛻�縺代ｋ
		st1 = new StringTokenizer(string1);
		st2 = new StringTokenizer(string2);

		// 謨ｰ縺檎焚縺ｪ縺｣縺溘ｉ螟ｱ謨�
		if (st1.countTokens() != st2.countTokens())
			return false;

		// 螳壽焚蜷悟｣ｫ
		int length = st1.countTokens();
		buffer1 = new String[length];
		buffer2 = new String[length];
		for (int i = 0; i < length; i++) {
			buffer1[i] = st1.nextToken();
			buffer2[i] = st2.nextToken();
		}

		// 蛻晄悄蛟､縺ｨ縺励※繝舌う繝ｳ繝�繧｣繝ｳ繧ｰ縺御ｸ弱∴繧峨ｌ縺ｦ縺�縺溘ｉ
		if (this.vars.size() != 0) {
			for (Enumeration<String> keys = vars.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				String value = (String) vars.get(key);
				replaceBuffer(key, value);
			}
		}

		for (int i = 0; i < length; i++) {
			if (!tokenMatching(buffer1[i], buffer2[i])) {
				return false;
			}
		}

		return true;
	}

	boolean tokenMatching(String token1, String token2) {
		if (token1.equals(token2))
			return true;
		if (var(token1) && !var(token2))
			return varMatching(token1, token2);
		if (!var(token1) && var(token2))
			return varMatching(token2, token1);
		if (var(token1) && var(token2))
			return varMatching(token1, token2);
		return false;
	}

	boolean varMatching(String vartoken, String token) {
		if (vars.containsKey(vartoken)) {
			if (token.equals(vars.get(vartoken))) {
				return true;
			} else {
				return false;
			}
		} else {
			replaceBuffer(vartoken, token);
			if (vars.contains(vartoken)) {
				replaceBindings(vartoken, token);
			}
			vars.put(vartoken, token);
		}
		return true;
	}

	void replaceBuffer(String preString, String postString) {
		for (int i = 0; i < buffer1.length; i++) {
			if (preString.equals(buffer1[i])) {
				buffer1[i] = postString;
			}
			if (preString.equals(buffer2[i])) {
				buffer2[i] = postString;
			}
		}
	}

	void replaceBindings(String preString, String postString) {
		Enumeration<String> keys;
		for (keys = vars.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			if (preString.equals(vars.get(key))) {
				vars.put(key, postString);
			}
		}
	}

	boolean var(String str1) {
		// 蜈磯�ｭ縺� ? 縺ｪ繧牙､画焚
		return str1.startsWith("?");
	}

	public Hashtable<String,String> getVarBindings(){
		return vars;
	}
}