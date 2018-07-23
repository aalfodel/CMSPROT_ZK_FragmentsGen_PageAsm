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
	
	
	private DraggableTreeCmsElement root = new DraggableTreeCmsElement(null, "generated-page-root", null, null);	//TODO move init
	private DraggableTreeModel model;
	private DraggableTreeCmsElement selectedElement;
	
	private List<String> fragmentTypeList;
	private String selectedFragmentType;
	
	private boolean popupVisibility;
	
	//GETTERS SETTERS
	
	//@NotifyChange("*")
	public void setSelectedElement(DraggableTreeCmsElement selectedElement) {
		this.selectedElement = selectedElement;
	}
	

	public DraggableTreeCmsElement getSelectedElement() {
		return selectedElement;
	}
	
	public DraggableTreeModel getModel() {
		
		if (model == null) {
			model = new DraggableTreeModel(root);
		}
		return model;
	}
	
	public DraggableTreeCmsElement getRoot() {
		return root;
	}

	public void setRoot(DraggableTreeCmsElement root) {
		this.root = root;
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
	
	//variable for Enum -> .zul conversation (ex. FragmentType.PARAGRAPH -> /WEB-INF/zul_templates/paragraph.zul)
	//NOTE: zul templates must be in /WEB-INF/zul_templates dir and their name must be *all* lowercase
	//TODO: manage to remove the "all lowercase" restriction for zul template files
	public String getFragmTypeZul() { 
//		String s;
//		if(fragmType == null)	//to manage startup state, when fragmtType hasn't yet got a @load
//			s = "";
//		else
//			s = "/WEB-INF/zul_templates/" + getFragmType() + ".zul";
//		System.out.println("ZULPATH: "+s);
//		return s;
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
		//Executions.createComponents("/WEB-INF/zul_templates/title.zul", null,null);		//TODO remove hardcoded path
	}

	//TODO
	@GlobalCommand
	@NotifyChange("model")
	public void saveToTreeGlobal(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) {
		//System.out.println("**DEBUG** pipeHashMap RECEIVED: " + pipeHashMap);
		DraggableTreeCmsElement newElement =  new DraggableTreeCmsElement(selectedElement, pipeHashMap.get("id"), FragmentType.valueOf(pipeHashMap.get("fragmentType")), pipeHashMap);
		pipeHashMap.put("parentId", selectedElement.getDescription());
		pipeHashMap.put("siblingsPosition", ((Integer)selectedElement.getChilds().indexOf(newElement)).toString());
		Map<String, Object> wrapperMap = new HashMap<String, Object>();
		wrapperMap.put("pipeHashMap", pipeHashMap);
		BindUtils.postGlobalCommand(null, null, "genPageGlobal", wrapperMap);
	}
	
}
	
//	private FileWriter out;
//	private FragmentType selectedFragment;
//	private Map<String,String> attributeDataMap;
//	
//	private ArrayList<String> idList = new ArrayList<String>();
//
//	private Map<FragmentType, HashMap<String, Boolean>> fragmentMap;
//	
//	private List<FragmentType> fragmentList;
//	
//	private boolean addPopupVisibility= false;
//	private boolean modifyPopupVisibility= false;
	
//	//TODO review this
//	@AfterCompose
//	public void doAfterCompose() {
//		//init the root element
//		//attributeDataMap = new HashMap<String,String>();
//		Map<String, String> rootMap = new HashMap<String, String>();
//		rootMap.put("id", "root");
//		root = new DraggableTreeCmsElement(null, "root", FragmentType.TITLE, rootMap);
//	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////GETTERS AND SETTERS
//	public List<FragmentType> getFragmentList() {
//		return FragmentMap.getFragmentList();
//	}
//	
//	public void setFragmentMap(Map<FragmentType, HashMap<String, Boolean>> fragmentMap) {
//		this.fragmentMap = fragmentMap;
//	}
//
//	public Map<FragmentType, HashMap<String, Boolean>> getFragmentMap() {
//		return FragmentMap.getFragmentMap();
//	}
	
//	public ArrayList<String> getIdList() {
//	return idList;
//}
//
//public void setIdList(ArrayList<String> idList) {
//	this.idList = idList;
//}
//
//public FragmentType getSelectedFragment() {
//	return selectedFragment;
//}
//
//public void setSelectedFragment(FragmentType selectedFragment) {
//	this.selectedFragment = selectedFragment;
//}
//
//public Map<String, String> getAttributeDataMap() {
//	return attributeDataMap;
//}
//
//public void setAttributeDataMap(Map<String, String> attributeDataMap) {
//	this.attributeDataMap = attributeDataMap;
//}

//public boolean isModifyPopupVisibility() {
//	return modifyPopupVisibility;
//}
//
//public void setModifyPopupVisibility(boolean modifyPopupVisibility) {
//	this.modifyPopupVisibility = modifyPopupVisibility;
//}
//
//public boolean isAddPopupVisibility() {
//	return addPopupVisibility;
//}
//
//public void setAddPopupVisibility(boolean addPopupVisibility) {
//	this.addPopupVisibility = addPopupVisibility;
//}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//////COMMANDS		
//@Command
//@NotifyChange("*")
//public void deleteNode(){
//	DraggableTreeComponent.removeFromParent(selectedElement);
//	root.recomputeSpacersRecursive();
//	idList.remove(selectedElement.getAttributeDataMap().get("id"));
//}
//
//@Command
//@NotifyChange ("colorAttribute")
//public void saveColor(@BindingParam ("colorAttribute") String color) {
//	attributeDataMap.put("colorAttribute", color);
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
    
    
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////UTILITIES
//	private String checkFields(ArrayList<String> idList, FragmentType selectedType, Map<String, String> attributeMap, HashMap<String, Boolean> controlComponentMap) {
//		String errMsgFun = "";
//
//		for (Boolean currentCheck : controlComponentMap.values()) {
//			for (String currentCheckName : controlComponentMap.keySet()) {
//				
//				for (String currentAttributeValue : attributeMap.values()) {
//					for (String currentAttributeName : attributeMap.keySet()) {
//						if (currentCheck && currentCheckName.equals(currentAttributeName)) {
//	
//							if (currentAttributeValue == "" || currentAttributeValue == null) {
//								errMsgFun += currentAttributeName;
//								errMsgFun += " \n";
//							}
//						}
//					}		
//				}
//			}
//		}
//		
//		if((errMsgFun.equals(""))==false) {
//			errMsgFun += " empty. Please insert all the data. \n";
//		}
//		
//		if(idList.contains(attributeMap.get("id"))){
//			errMsgFun += "Node Id already exists. Please change it";
//		}
//		
//		return errMsgFun;
//	}

