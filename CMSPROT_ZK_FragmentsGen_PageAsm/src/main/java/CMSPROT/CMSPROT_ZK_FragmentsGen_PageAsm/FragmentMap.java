package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FragmentMap {
	static Map<String, HashMap<String, Boolean>> fragmentMap = new HashMap<String, HashMap<String,Boolean>>();
	static List<String> fragmentList = new ArrayList<String>();
	
	static{
		HashMap<String, Boolean> titleMap=new HashMap<String, Boolean>();
		titleMap.put("id", true);
		titleMap.put("text", true);
		titleMap.put("color", true);
		
		HashMap<String, Boolean> containerMap=new HashMap<String, Boolean>();
		containerMap.put("id", true);
		containerMap.put("border-style", false);
		containerMap.put("border-color", true);
		
		HashMap<String, Boolean> paragraphMap=new HashMap<String, Boolean>();
		paragraphMap.put("id", true);
		paragraphMap.put("text", true);
		paragraphMap.put("color", false);
		
		for(FragmentType fragmentName : FragmentType.values()) {
			
			fragmentList.add(fragmentName.toString());
			
			if(fragmentName.equals(FragmentType.TITLE)) {
				fragmentMap.put(fragmentName.toString(), titleMap);
			}
		
			if(fragmentName.equals(FragmentType.PARAGRAPH)) {
				fragmentMap.put(fragmentName.toString(), paragraphMap);
			}
				
			if(fragmentName.equals(FragmentType.CONTAINER)) {
				fragmentMap.put(fragmentName.toString(), containerMap);	
			}
		}
	}
	
	public static Map<String, HashMap<String, Boolean>> getFragmentMap() {
		return fragmentMap;
	} 
	
	public static List<String> getFragmentList() {
		return fragmentList;
	} 

}
