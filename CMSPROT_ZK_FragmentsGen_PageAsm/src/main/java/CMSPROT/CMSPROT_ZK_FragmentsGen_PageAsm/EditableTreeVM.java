//imported from Eros

package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

import biz.opengate.zkComponents.draggableTree.DraggableTreeModel;

public class EditableTreeVM {
	
	private DraggableTreeCmsElement root;
	private DraggableTreeModel model;
	private DraggableTreeCmsElement selectedElement;
	
	private List<String> fragmentTypeList;
	private String selectedFragmentType;
	
	private boolean popupVisibility;
	
	//GETTERS SETTERS
	
	public DraggableTreeCmsElement getRoot() {
		if (root == null) {
			Map<String, String> rootDataMap = new HashMap<String, String>();
			rootDataMap.put("id", "generated-page-root");
			root = new DraggableTreeCmsElement(null, rootDataMap);
		}
		return root;
	}
	
	public DraggableTreeModel getModel() {
		
		if (model == null) {
			model = new DraggableTreeModel(getRoot());
		}
		return model;
	}
	
	public void setSelectedElement(DraggableTreeCmsElement selectedElement) {
		this.selectedElement = selectedElement;
	}
	
	public DraggableTreeCmsElement getSelectedElement() {
		return selectedElement;
	}
	

	public List<String>  getFragmentTypeList() {
		if (fragmentTypeList == null) {
			fragmentTypeList = new ArrayList<String>();
			for (FragmentType i : FragmentType.values()) {
				fragmentTypeList.add(i.toString());
			}
		}
		return fragmentTypeList;
	}
	
	public String getSelectedFragmentType() {
		return selectedFragmentType;
	}
	
	@NotifyChange("fragmTypeZul")
	public void setSelectedFragmentType(String selectedFragmentType) {
		this.selectedFragmentType = selectedFragmentType;
	}
	
	
	public boolean isPopupVisibility() {
		return popupVisibility;
	}

	public void setPopupVisibility(boolean popupVisibility) {
		this.popupVisibility = popupVisibility;
	}
	
	//NOTE: zul templates must be in /WEB-INF/zul_templates dir and their name must be *all* lowercase
	//TODO: manage to remove the "all lowercase" restriction for zul template files
	public String getFragmTypeZul() { 
		if (selectedFragmentType == null) {
			return "";
		}
	
		return "/WEB-INF/zul_templates/" + selectedFragmentType.toLowerCase() + ".zul";
	}
	
//	private FragmentType fragmType;
//	
//	//NOTE: Enum<->String conversions
//	public String getFragmType() {
//		String s;
//		if(fragmType == null)	//to manage startup state, when fragmtType hasn't yet got a @save
//			s = "";		
//		else
//			s = fragmType.toString().toLowerCase();		
//		//System.out.println("**DEBUG** fragmType to lowercase string: " + s);
//		return s;
//	}
//	@NotifyChange("fragmTypeZul")
//	public void setFragmType(String fragmType) {
//		this.fragmType = FragmentType.valueOf(fragmType.toUpperCase());
//		//System.out.println("**DEBUG** fragmType: " + this.fragmType);
//	}
	
	//POPUP
	
	@Command
	@NotifyChange("popupVisibility")
	public void closePopup() {
		popupVisibility = false;
	}
	
	@Command
	@NotifyChange("popupVisibility")
	public void openPopup() {
		popupVisibility = true;
		
		//create new component and attach it to DOM
		//Executions.createComponents("/WEB-INF/zul_templates/title.zul", null,null);	
	}

	//TODO
	@GlobalCommand
	@NotifyChange("model")
	public void saveToDraggableTreeGlobal(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) {
		System.out.println("**DEBUG** SELECTED ELEMENT (saveToDraggableTreeGlobal): " + selectedElement + " = " + selectedElement.getElementDataMap());
		DraggableTreeCmsElement newElement =  new DraggableTreeCmsElement(selectedElement, pipeHashMap);
		pipeHashMap.put("parentId", selectedElement.getElementDataMap().get("id"));
		pipeHashMap.put("siblingsPosition", ( (Integer)selectedElement.getChilds().indexOf(newElement) ).toString());	//can't call .toString on primitive type int, so I use Integer
		
		Map<String, Object> wrapperMap = new HashMap<String, Object>();
		wrapperMap.put("pipeHashMap", pipeHashMap);
		BindUtils.postGlobalCommand(null, null, "generatePageGlobal", wrapperMap);
	}
	
}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//////COMMANDS		
//@Command
//@NotifyChange("*")
//public void deleteNode(){
//	DraggableTreeComponent.removeFromParent(selectedElement);
//	root.recomputeSpacersRecursive();
//	idList.remove(selectedElement.getAttributeDataMap().get("id"));
//}
	
//	@NotifyChange("*")
//	public void addComponent() throws Exception{
//		///////////////////////////////////////////////////////////////////////////////////////////////////////////
//		////// CHECK DATA
//		String errString=null;
		
//		System.out.println( fragmentMap.get(selectedFragment));
//		System.out.println( fragmentMap.get(selectedFragment).toString());
		
		//errString=checkFields(idList, selectedFragment, attributeDataMap, fragmentMap.get(selectedFragment));
//		if ((errString.equals(""))==true) {
//			
//			Clients.showNotification("Tutto OK");	
//			
////			attributeDataMap=generateFragment();
////			///////////////////////////////////////////////////////////////////////////////////////////////////////////
////			////// INITIALIZING COMPONENT ELEMENTS WITH MAIN PAGE ELEMENTS
////			componentIdList=mainPageIdList;
////			componentSelectedElement = mainPageselectedElement;
////			///////////////////////////////////////////////////////////////////////////////////////////////////////////
////			////// NODE ADDING
////			new DraggableTreeCmsElement(componentSelectedElement, fragmentId, selectedFragment, attributeDataMap);
////			componentIdList.add(fragmentId);
////			Map<String, Object> args = new HashMap<String, Object>();
////			args.put("selectedElement", componentSelectedElement);
////			args.put("idList", componentIdList);
////			BindUtils.postGlobalCommand(null, null, "reloadMainPageTree", args);
////			// RESET WINDOW SELECTIONS OR CONTENT
////			resetPopUpSelectionAndBack();
//		}else {
//			addPopupVisibility=true;
//			Clients.showNotification(errString);	
//		}
//	}
	
//    @Command
//    @NotifyChange("attributeDataMap")
//    public void resetHashMap() {
//    	for(String currentKey:attributeDataMap.values()) {
//    		attributeDataMap.put(currentKey, "");
//    	}
    	
    	
//    }
    
//    @Command
//    @NotifyChange("*")
//    public void resetPopUpSelectionAndBack() {
//    	resetHashMap();
//    	addPopupVisibility=false;
//    }
