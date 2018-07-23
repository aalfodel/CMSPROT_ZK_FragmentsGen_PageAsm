//imported from Eros

package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import biz.opengate.zkComponents.draggableTree.*;

import java.util.HashMap;
import java.util.Map;

public class DraggableTreeCmsElement extends DraggableTreeElement {
	
	private Map<String,String> elementDataMap;
		
	//GETTERS AND SETTERS

	public Map<String, String> getElementDataMap() {
		return elementDataMap;
	}

	public void setElementDataMap(Map<String, String> elementDataMap) {
		this.elementDataMap = elementDataMap;
	}

	//CONSTRUCTOR
	
	public DraggableTreeCmsElement(DraggableTreeElement parent, Map<String,String> elementDataMap) {
		super(parent, elementDataMap.get("id"));
		this.elementDataMap=new HashMap<String, String>(elementDataMap);	//WARNING we need a copy here, not just the reference! (because we'll reuse the same pipeHashMap instance for every passage of the specific fragment type viewmodel)
	}																		//NOTE: it's a shallow copy, but they're all strings so we're ok

}
