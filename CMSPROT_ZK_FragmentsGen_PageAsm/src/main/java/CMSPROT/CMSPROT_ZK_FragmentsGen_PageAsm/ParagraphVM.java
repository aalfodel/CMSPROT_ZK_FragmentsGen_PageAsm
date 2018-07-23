package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zul.Window;

public class ParagraphVM {
	private Map<String, String> pipeHashMap = new HashMap<String, String>();
	
	public Map<String, String> getPipeHashMap() {
		return pipeHashMap;
	}

	public void setPipeHashMap(Map<String, String> pipeHashMap) {
		this.pipeHashMap = pipeHashMap;
	}
	
	@AfterCompose
	public void initFragmentType() {
		pipeHashMap.put("fragmentType", "PARAGRAPH");
	}
	
	@Command
	public void saveElement() {
		Map<String, Object> wrapperMap = new HashMap<String, Object>();
		wrapperMap.put("pipeHashMap", pipeHashMap);
		BindUtils.postGlobalCommand(null, null, "saveToDraggableTreeGlobal", wrapperMap);
		//TODO auto close popup after save
	}
	
//	//NOTE: doesn't hide, but *detaches* the window from the DOM
//	@Command
//	public void detachPopup(@ContextParam(ContextType.VIEW) Window component) {
//		component.detach();
//	}
}
