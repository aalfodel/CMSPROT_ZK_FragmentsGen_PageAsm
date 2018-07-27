package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;

public class ParagraphVM {
	
	private Map<String, String> pipeHashMap = new HashMap<String, String>();	//WARNING the same pipeHashMap is reused at every passage! 
																				//I solved this problem in the draggableTree element creation: the pipeHashMap gets cloned
	
	//GETTERS SETTERS
	
	public Map<String, String> getPipeHashMap() {
		return pipeHashMap;
	}

	public void setPipeHashMap(Map<String, String> pipeHashMap) {
		this.pipeHashMap = pipeHashMap;
	}
	
	//INITIALIZATION
	
	@Init
	public void init(@ExecutionArgParam("popupType") String popupType, @ExecutionArgParam("dataToLoad") HashMap<String, String> dataToLoad) {
		if (popupType.equals("modify"))
			pipeHashMap = new HashMap<String, String>(dataToLoad);	//load the Element old attributes into the form;   NOTE we need a *copy* here!
		else	// "add"
			pipeHashMap.put("fragmentType", "PARAGRAPH");	//set the type of the new Element
	}
	
	//TREE OPERATIONS
	
	@Command
	public void saveElement(@BindingParam("opType") String operationType) {
		Map<String, Object> wrapperMap = new HashMap<String, Object>();
		wrapperMap.put("pipeHashMap", pipeHashMap);
		BindUtils.postGlobalCommand(null, null, (operationType + "ElementGlobal"), wrapperMap);		//NOTE the possible commands are "addElementGlobal" or "modifyElementGlobal"
	}
	
}
