import java.util.*;
 
public class Planner {
 Vector operators;
 Random rand;
 Vector plan;
 
 public static void main(String argv[]){
  (new Planner()).start();
 }
 
 //Planner(){
 // rand = new Random();
 //}
 
 public void start(){
  //初期状態から中間状態へ
  initOperators();
  Vector goalList     = initGoalList();
  //System.out.println(goalList);
  goallistArrange(goalList);
  //System.out.println(goalList);
  Vector midgoalList = initmidGoalList();
  Vector initialState = initInitialState();
 
  Hashtable theBinding = new Hashtable();
  plan = new Vector();
   

	System.out.println(initialState);
	System.out.println(midgoalList);
	System.out.println(goalList);
  
  setOperatorFirsttry();
  planning(midgoalList,initialState,theBinding);
   
  System.out.println("***** This is a plan! *****");
   for(int i = 0 ; i < plan.size() ; i++){
    Operator op = (Operator)plan.elementAt(i);     
    System.out.println((op.instantiate(theBinding)).name);
   }
    
   //中間状態から最終目標へ
   startagain();
  }
 
 public boolean start(Vector goalList , Vector initialState){
		// 初期状態から中間状態へ
		initOperators();
		System.out.println(goalList);
		// System.out.println(goalList);
		goallistArrange(goalList);
		// System.out.println(goalList);
		Vector midgoalList = initmidGoalList();

		System.out.println(initialState);
		System.out.println(midgoalList);
		System.out.println(goalList);
		
		
		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		setOperatorFirsttry();
		planning(midgoalList, initialState, theBinding);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}

		// 中間状態から最終目標へ
		theBinding = new Hashtable();
		midgoalList = initmidGoalList();
		setOperatorSecondtry();
		planning(goalList, midgoalList, theBinding);
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}
	 return true;
 }
 
 public Vector<Operator> getPlan(){
	 return plan;
 }
 
 public void startagain(){
      
      initOperators();
      Vector goalList     = initGoalList();
      goallistArrange(goalList);
      Vector midgoalList = initmidGoalList();
      Vector initialState = initInitialState();
 
      Hashtable theBinding = new Hashtable();
      plan = new Vector();
      setOperatorSecondtry();
       planning(goalList,midgoalList,theBinding);
        
       for(int i = 0 ; i < plan.size() ; i++){
            Operator op = (Operator)plan.elementAt(i);     
            System.out.println((op.instantiate(theBinding)).name);
       }
 }
  
  
 private boolean planning(Vector theGoalList,
                          Vector theCurrentState,
                          Hashtable theBinding){
  //XSystem.out.println("*** GOALS ***" + theGoalList);
  if(theGoalList.size() == 1){
   String aGoal = (String)theGoalList.elementAt(0);
   if(planningAGoal(aGoal,theCurrentState,theBinding,0) != -1){
    return true;
   } else {
    return false;
   }
  } else {
   String aGoal = (String)theGoalList.elementAt(0);
   int cPoint = 0;
   while(cPoint < operators.size()){
    //System.out.println("cPoint:"+cPoint);
    // Store original binding
    Hashtable orgBinding = new Hashtable();
    for(Enumeration e = theBinding.keys() ; e.hasMoreElements();){
     String key = (String)e.nextElement();
     String value = (String)theBinding.get(key);
     orgBinding.put(key,value);
    }
    Vector orgState = new Vector();
    for(int i = 0; i < theCurrentState.size() ; i++){
     orgState.addElement(theCurrentState.elementAt(i));
    }
 
    int tmpPoint = planningAGoal(aGoal,theCurrentState,theBinding,cPoint);
    //System.out.println("tmpPoint: "+tmpPoint);
    if(tmpPoint != -1){
     theGoalList.removeElementAt(0);
     //XSystem.out.println(theCurrentState);
     if(planning(theGoalList,theCurrentState,theBinding)){
      //System.out.println("Success !");
      return true;
     } else {
      cPoint = tmpPoint;
      //System.out.println("Fail::"+cPoint);
      theGoalList.insertElementAt(aGoal,0);
       
      theBinding.clear();
      for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
       String key = (String)e.nextElement();
       String value = (String)orgBinding.get(key);
       theBinding.put(key,value);
      }
      theCurrentState.removeAllElements();
      for(int i = 0 ; i < orgState.size() ; i++){
       theCurrentState.addElement(orgState.elementAt(i));
      }
     }
    } else {
     theBinding.clear();
     for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
      String key = (String)e.nextElement();
      String value = (String)orgBinding.get(key);
      theBinding.put(key,value);
     }
     theCurrentState.removeAllElements();
     for(int i = 0 ; i < orgState.size() ; i++){
      theCurrentState.addElement(orgState.elementAt(i));
     }
     return false;
    }
   }
   return false;
  }
 }
 
  
 /**
  * 一回目探索用にオペレータの位置を変える
  */
  private void setOperatorFirsttry(){  
     for(int i=0;i<operators.size();i++){
         String name=((Operator)operators.get(i)).name;
         if(name.equals("remove ?x from on top ?y")||name.equals("put ?x down on the table")){
             Operator op=(Operator) operators.get(i);
             operators.remove(op);
             operators.add(0,op);
         }
     }
  }
 
  /**
  * 二回目探索用にオペレータの位置を変える
  */
  private void setOperatorSecondtry(){
     for(int i=0;i<operators.size();i++){
         String name=((Operator)operators.get(i)).name;
         if(name.equals("pick up ?x from the table")||name.equals("place ?x on ?y")){
             Operator op=(Operator) operators.get(i);
             operators.remove(op);
             operators.add(0,op);
         }
     }
  }
   
 private int planningAGoal(String theGoal,Vector theCurrentState,
                           Hashtable theBinding,int cPoint){
  //System.out.println("**"+theGoal);
  int size = theCurrentState.size();
  for(int i =  0; i < size ; i++){
   String aState = (String)theCurrentState.elementAt(i);
   if((new Unifier()).unify(theGoal,aState,theBinding)){
    return 0;
   }
  }
 
  /*int randInt = Math.abs(rand.nextInt()) % operators.size();
  Operator op = (Operator)operators.elementAt(randInt);
  operators.removeElementAt(randInt);
  operators.addElement(op);
*/
   
  for(int i = cPoint ; i < operators.size() ; i++){
   Operator anOperator = rename((Operator)operators.elementAt(i));
   // 迴ｾ蝨ｨ縺ｮCurrent state, Binding, plan繧鍛ackup
   Hashtable orgBinding = new Hashtable();
   for(Enumeration e = theBinding.keys() ; e.hasMoreElements();){
    String key = (String)e.nextElement();
    String value = (String)theBinding.get(key);
    orgBinding.put(key,value);
   }
   Vector orgState = new Vector();
   for(int j = 0; j < theCurrentState.size() ; j++){
    orgState.addElement(theCurrentState.elementAt(j));
   }
   Vector orgPlan = new Vector();
   for(int j = 0; j < plan.size() ; j++){
    orgPlan.addElement(plan.elementAt(j));
   }
 
   Vector addList = (Vector)anOperator.getAddList();
   for(int j = 0 ; j < addList.size() ; j++){
    if((new Unifier()).unify(theGoal,
                             (String)addList.elementAt(j),
                             theBinding)){
     Operator newOperator = anOperator.instantiate(theBinding);
     Vector newGoals = (Vector)newOperator.getIfList();
     //XSystem.out.println(newOperator.name);
     if(planning(newGoals,theCurrentState,theBinding)){
      //XSystem.out.println(newOperator.name);
      plan.addElement(newOperator);
      theCurrentState =
       newOperator.applyState(theCurrentState);
      return i+1;
     } else {
      // 螟ｱ謨励＠縺溘ｉ蜈�縺ｫ謌ｻ縺呻ｼ�
      theBinding.clear();
      for(Enumeration e=orgBinding.keys();e.hasMoreElements();){
       String key = (String)e.nextElement();
       String value = (String)orgBinding.get(key);
       theBinding.put(key,value);
      }
      theCurrentState.removeAllElements();
      for(int k = 0 ; k < orgState.size() ; k++){
       theCurrentState.addElement(orgState.elementAt(k));
      }
      plan.removeAllElements();
      for(int k = 0 ; k < orgPlan.size() ; k++){
       plan.addElement(orgPlan.elementAt(k));
      }
     }
    }      
   }
  }
  return -1;
 }
     
 int uniqueNum = 0;
 private Operator rename(Operator theOperator){
  Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
  uniqueNum = uniqueNum + 1;
  return newOperator;
 }
 
 private Vector initGoalList(){
  Vector goalList = new Vector();
  //goalList.addElement("B on C");
  //goalList.addElement("A on B");
   
  //goalList.addElement("clear A");
   
  goalList.addElement("A on B");
  goalList.addElement("B on C");
  goalList.addElement("C on D");
  goalList.addElement("D on E");
  //goalList.addElement("ontable D");
  //goalList.addElement("handEmpty");
 
  return goalList;
   
 }
   
 private Vector initmidGoalList(){
      Vector midgoalList = new Vector();
      midgoalList.addElement("clear A");
      midgoalList.addElement("ontable A");
      midgoalList.addElement("clear C");
      midgoalList.addElement("ontable C");
      midgoalList.addElement("clear D");
      midgoalList.addElement("ontable D");
      midgoalList.addElement("clear B");
      midgoalList.addElement("ontable B");
      midgoalList.addElement("clear E");
      midgoalList.addElement("ontable E");
      midgoalList.addElement("clear F");
      midgoalList.addElement("ontable F");
      midgoalList.addElement("clear G");
      midgoalList.addElement("ontable G");
      midgoalList.addElement("clear H");
      midgoalList.addElement("ontable H");
      midgoalList.addElement("handEmpty");
       
      return midgoalList;
     }
  
 private Vector initInitialState(){
  Vector initialState = new Vector();
  //initialState.addElement("clear A");
  //initialState.addElement("clear B");
  //initialState.addElement("clear C");
 
  //initialState.addElement("ontable A");
  //initialState.addElement("ontable B");
  //initialState.addElement("ontable C");
  //initialState.addElement("handEmpty");
  initialState.addElement("clear A");
  //initialState.addElement("ontable A");
  initialState.addElement("A on C");
  initialState.addElement("C on D");
  initialState.addElement("ontable D");
  initialState.addElement("clear E");
  initialState.addElement("ontable E");
  initialState.addElement("clear B");
  initialState.addElement("ontable B");
  //initialState.addElement("clear C");
  //initialState.addElement("ontable C");
  initialState.addElement("ontable F");
  initialState.addElement("ontable G");
  initialState.addElement("ontable H");
  initialState.addElement("clear F");
  initialState.addElement("clear G");
  initialState.addElement("clear H");
  initialState.addElement("handEmpty");
  return initialState;
 }
 
  
 private void goallistArrange(Vector theGoalList){
     String str0 = new String();
     String str1 = new String();
     String str2 = new String();
     String str3 = new String ();
     String str4 = new String ();
     String strA = new String();
     String strB = new String();
     String strC = new String();
     String strD = new String();
      
     for(int k = 0; k<theGoalList.size(); k++){
     for(int i = k; i<theGoalList.size(); i++){
         for(int j = 1; j<theGoalList.size(); j++){
         str1 = (String) theGoalList.elementAt(k);
         str2 = (String) theGoalList.elementAt(j);
          
         StringTokenizer st1 = new StringTokenizer(str1);
           StringTokenizer st2 = new StringTokenizer(str2);
           strA = st1.nextToken();
           strB = st1.nextToken();
           strC = st1.nextToken();
           strD = st2.nextToken();
            if (strC.equals(strD)){
                str0 = str1;
                theGoalList.remove(k);
                theGoalList.addElement(str0);
            }
         } 
        }  
    }  
 }
  
private void initOperators(){
  operators = new Vector();
 
  // OPERATOR 1
  /// NAMEifList1.addElement(new String("clear ?y"));
  String name1 = new String("place ?x on ?y");
  /// IF
  Vector ifList1 = new Vector();
  ifList1.addElement(new String("clear ?y"));
  ifList1.addElement(new String("holding ?x"));
  //ifList1.addElement(new String("placeable ?y"));
  /// ADD-LIST
  Vector addList1 = new Vector();
  addList1.addElement(new String("?x on ?y"));
  addList1.addElement(new String("clear ?x"));
  addList1.addElement(new String("handEmpty"));
  /// DELETE-LIST
  Vector deleteList1 = new Vector();
  deleteList1.addElement(new String("clear ?y"));
  deleteList1.addElement(new String("holding ?x"));
  Operator operator1 =
        new Operator(name1,ifList1,addList1,deleteList1);
    operators.addElement(operator1);
 
    // OPERATOR 2
    /// NAME
    String name2 = new String("remove ?x from on top ?y");
    /// IF
    Vector ifList2 = new Vector();
    ifList2.addElement(new String("?x on ?y"));
    ifList2.addElement(new String("clear ?x"));
    ifList2.addElement(new String("handEmpty"));
    /// ADD-LIST
    Vector addList2 = new Vector();
    addList2.addElement(new String("clear ?y"));
    addList2.addElement(new String("holding ?x"));
    /// DELETE-LIST
    Vector deleteList2 = new Vector();
    deleteList2.addElement(new String("?x on ?y"));
    deleteList2.addElement(new String("clear ?x"));
    deleteList2.addElement(new String("handEmpty"));
    Operator operator2 =
        new Operator(name2,ifList2,addList2,deleteList2);
    operators.addElement(operator2);
 
    // OPERATOR 3
    /// NAME
    String name3 = new String("pick up ?x from the table");
    /// IF
    Vector ifList3 = new Vector();
    ifList3.addElement(new String("ontable ?x"));
    ifList3.addElement(new String("clear ?x"));
    ifList3.addElement(new String("handEmpty"));
    /// ADD-LIST
    Vector addList3 = new Vector();
    addList3.addElement(new String("holding ?x"));
    /// DELETE-LIST
    Vector deleteList3 = new Vector();
    deleteList3.addElement(new String("ontable ?x"));
    deleteList3.addElement(new String("clear ?x"));
    deleteList3.addElement(new String("handEmpty"));
    Operator operator3 =
        new Operator(name3,ifList3,addList3,deleteList3);
    operators.addElement(operator3);
 
    // OPERATOR 4
    /// NAME
    String name4 = new String("put ?x down on the table");
    /// IF
    Vector ifList4 = new Vector();
    ifList4.addElement(new String("holding ?x"));
    /// ADD-LIST
    Vector addList4 = new Vector();
    addList4.addElement(new String("ontable ?x"));
    addList4.addElement(new String("clear ?x"));
    addList4.addElement(new String("handEmpty"));
    /// DELETE-LIST
    Vector deleteList4 = new Vector();
    deleteList4.addElement(new String("holding ?x"));
    Operator operator4 =
        new Operator(name4,ifList4,addList4,deleteList4);
    operators.addElement(operator4);
    }
}
 
class Operator{
    String name;
    Vector ifList;
    Vector addList;
    Vector deleteList;
 
    Operator(String theName,
         Vector theIfList,Vector theAddList,Vector theDeleteList){
    name       = theName;
    ifList     = theIfList;
    addList    = theAddList;
    deleteList = theDeleteList;
    }
 
    public String getName(){
    	return name;
    }
    
    public Vector getAddList(){
    return addList;
    }
 
    public Vector getDeleteList(){
    return deleteList;
    }
 
    public Vector getIfList(){
    return ifList;
    }
 
    public String toString(){
    String result =
        "NAME: "+name + "\n" +
        "IF :"+ifList + "\n" +
        "ADD:"+addList + "\n" +
        "DELETE:"+deleteList;
    return result;
    }
 
    public Vector applyState(Vector theState){
    for(int i = 0 ; i < addList.size() ; i++){
        theState.addElement(addList.elementAt(i));
    }
    for(int i = 0 ; i < deleteList.size() ; i++){
        theState.removeElement(deleteList.elementAt(i));
    }
    return theState;
    }
     
 
    public Operator getRenamedOperator(int uniqueNum){
    Vector vars = new Vector();
    // IfList縺ｮ螟画焚繧帝寔繧√ｋ
    for(int i = 0 ; i < ifList.size() ; i++){
        String anIf = (String)ifList.elementAt(i);
        vars = getVars(anIf,vars);
    }
    // addList縺ｮ螟画焚繧帝寔繧√ｋ
    for(int i = 0 ; i < addList.size() ; i++){
        String anAdd = (String)addList.elementAt(i);
        vars = getVars(anAdd,vars);
    }
    // deleteList縺ｮ螟画焚繧帝寔繧√ｋ
    for(int i = 0 ; i < deleteList.size() ; i++){
        String aDelete = (String)deleteList.elementAt(i);
        vars = getVars(aDelete,vars);
    }
    Hashtable renamedVarsTable = makeRenamedVarsTable(vars,uniqueNum);
     
    // 譁ｰ縺励＞IfList繧剃ｽ懊ｋ
    Vector newIfList = new Vector();
    for(int i = 0 ; i < ifList.size() ; i++){
        String newAnIf =
        renameVars((String)ifList.elementAt(i),
               renamedVarsTable);
        newIfList.addElement(newAnIf);
    }
    // 譁ｰ縺励＞addList繧剃ｽ懊ｋ
    Vector newAddList = new Vector();
    for(int i = 0 ; i < addList.size() ; i++){
        String newAnAdd =
        renameVars((String)addList.elementAt(i),
               renamedVarsTable);
        newAddList.addElement(newAnAdd);
    }
    // 譁ｰ縺励＞deleteList繧剃ｽ懊ｋ
    Vector newDeleteList = new Vector();
    for(int i = 0 ; i < deleteList.size() ; i++){
        String newADelete =
        renameVars((String)deleteList.elementAt(i),
               renamedVarsTable);
        newDeleteList.addElement(newADelete);
    }
    // 譁ｰ縺励＞name繧剃ｽ懊ｋ
    String newName = renameVars(name,renamedVarsTable);
     
    return new Operator(newName,newIfList,newAddList,newDeleteList);
    }
 
    private Vector getVars(String thePattern,Vector vars){
    StringTokenizer st = new StringTokenizer(thePattern);
    for(int i = 0 ; i < st.countTokens();){
        String tmp = st.nextToken();
        if(var(tmp)){
        vars.addElement(tmp);
        }
    }
    return vars;
    }
 
    private Hashtable makeRenamedVarsTable(Vector vars,int uniqueNum){
    Hashtable result = new Hashtable();
    for(int i = 0 ; i < vars.size() ; i++){
        String newVar =
        (String)vars.elementAt(i) + uniqueNum;
        result.put((String)vars.elementAt(i),newVar);
    }
    return result;
    }
     
    private String renameVars(String thePattern,
                  Hashtable renamedVarsTable){
    String result = new String();
    StringTokenizer st = new StringTokenizer(thePattern);
    for(int i = 0 ; i < st.countTokens();){
        String tmp = st.nextToken();
        if(var(tmp)){
        result = result + " " +
            (String)renamedVarsTable.get(tmp);
        } else {
        result = result + " " + tmp;
        }
    }
    return result.trim();
    }
 
     
    public Operator instantiate(Hashtable theBinding){
    // name 繧貞�ｷ菴灘喧
    String newName =
        instantiateString(name,theBinding);
    // ifList    繧貞�ｷ菴灘喧
    Vector newIfList = new Vector();
    for(int i = 0 ; i < ifList.size() ; i++){
        String newIf =
        instantiateString((String)ifList.elementAt(i),theBinding);
        newIfList.addElement(newIf);
    }
    // addList   繧貞�ｷ菴灘喧
    Vector newAddList = new Vector();
    for(int i = 0 ; i < addList.size() ; i++){
        String newAdd =
        instantiateString((String)addList.elementAt(i),theBinding);
        newAddList.addElement(newAdd);
    }
    // deleteList繧貞�ｷ菴灘喧
    Vector newDeleteList = new Vector();
    for(int i = 0 ; i < deleteList.size() ; i++){
        String newDelete =
        instantiateString((String)deleteList.elementAt(i),theBinding);
        newDeleteList.addElement(newDelete);
    }
    return new Operator(newName,newIfList,newAddList,newDeleteList);
    }
 
    private String instantiateString(String thePattern, Hashtable theBinding){
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for(int i = 0 ; i < st.countTokens();){
            String tmp = st.nextToken();
            if(var(tmp)){
        String newString = (String)theBinding.get(tmp);
        if(newString == null){
            result = result + " " + tmp;
        } else {
            result = result + " " + newString;
        }
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }
 
    private boolean var(String str1){
        // 蜈磯�ｭ縺� ? 縺ｪ繧牙､画焚
        return str1.startsWith("?");
    }
}
 
/*class Unifier {
    StringTokenizer st1;
    String buffer1[];   
    StringTokenizer st2;
    String buffer2[];
    Hashtable vars;
     
    Unifier(){
    //vars = new Hashtable();
    }
 
    public boolean unify(String string1,String string2,Hashtable theBindings){
    Hashtable orgBindings = new Hashtable();
    for(Enumeration e = theBindings.keys() ; e.hasMoreElements();){
        String key = (String)e.nextElement();
        String value = (String)theBindings.get(key);
        orgBindings.put(key,value);
    }
    this.vars = theBindings;
    if(unify(string1,string2)){
        return true;
    } else {
        // 螟ｱ謨励＠縺溘ｉ蜈�縺ｫ謌ｻ縺呻ｼ�
        theBindings.clear();
        for(Enumeration e = orgBindings.keys() ; e.hasMoreElements();){
        String key = (String)e.nextElement();
        String value = (String)orgBindings.get(key);
        theBindings.put(key,value);
        }
        return false;
    }
    }
 
    public boolean unify(String string1,String string2){
    // 蜷後§縺ｪ繧画�仙粥
    if(string1.equals(string2)) return true;
     
    // 蜷�縲�繝医�ｼ繧ｯ繝ｳ縺ｫ蛻�縺代ｋ
    st1 = new StringTokenizer(string1);
    st2 = new StringTokenizer(string2);
     
    // 謨ｰ縺檎焚縺ｪ縺｣縺溘ｉ螟ｱ謨�
    if(st1.countTokens() != st2.countTokens()) return false;
     
    // 螳壽焚蜷悟｣ｫ
    int length = st1.countTokens();
    buffer1 = new String[length];
    buffer2 = new String[length];
    for(int i = 0 ; i < length; i++){
        buffer1[i] = st1.nextToken();
        buffer2[i] = st2.nextToken();
    }
 
    // 蛻晄悄蛟､縺ｨ縺励※繝舌う繝ｳ繝�繧｣繝ｳ繧ｰ縺御ｸ弱∴繧峨ｌ縺ｦ縺�縺溘ｉ
    if(this.vars.size() != 0){
        for(Enumeration keys = vars.keys(); keys.hasMoreElements();){
        String key = (String)keys.nextElement();
        String value = (String)vars.get(key);
        replaceBuffer(key,value);
        }
    }
     
    for(int i = 0 ; i < length ; i++){
        if(!tokenMatching(buffer1[i],buffer2[i])){
        return false;
        }
    }
     
    return true;
    }
 
    boolean tokenMatching(String token1,String token2){
    if(token1.equals(token2)) return true;
    if( var(token1) && !var(token2)) return varMatching(token1,token2);
    if(!var(token1) &&  var(token2)) return varMatching(token2,token1);
    if( var(token1) &&  var(token2)) return varMatching(token1,token2);
    return false;
    }
 
    boolean varMatching(String vartoken,String token){
    if(vars.containsKey(vartoken)){
        if(token.equals(vars.get(vartoken))){
        return true;
        } else {
        return false;
        }
    } else {
        replaceBuffer(vartoken,token);
        if(vars.contains(vartoken)){
        replaceBindings(vartoken,token);
        }
        vars.put(vartoken,token);
    }
    return true;
    }
 
    void replaceBuffer(String preString,String postString){
    for(int i = 0 ; i < buffer1.length ; i++){
        if(preString.equals(buffer1[i])){
        buffer1[i] = postString;
        }
        if(preString.equals(buffer2[i])){
        buffer2[i] = postString;
        }
    }
    }
     
    void replaceBindings(String preString,String postString){
    Enumeration keys;
    for(keys = vars.keys(); keys.hasMoreElements();){
        String key = (String)keys.nextElement();
        if(preString.equals(vars.get(key))){
        vars.put(key,postString);
        }
    }
    }
     
    boolean var(String str1){
    // 蜈磯�ｭ縺� ? 縺ｪ繧牙､画焚
    return str1.startsWith("?");
    }
 
}
*/