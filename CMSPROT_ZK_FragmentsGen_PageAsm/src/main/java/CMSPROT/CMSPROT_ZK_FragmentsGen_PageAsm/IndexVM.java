package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Clients;

import biz.opengate.zkComponents.draggableTree.DraggableTreeModel;

public class IndexVM {
	
	private DraggableTreeCmsElement root;
	private DraggableTreeModel model;
	private DraggableTreeCmsElement selectedElement;
	
	private List<String> fragmentTypeList;
	private String selectedFragmentType;
	
	private boolean popupVisibility;
	
	private PageManipulator pageManip;
	private FragmentGenerator fragmentGen;
	
	
	/////GETTERS SETTERS/////
	
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
	
	//NOTE: zul templates must be in /WEB-INF/zul_templates dir and their name must be *all* lowercase
	//TODO: manage to remove the "all lowercase" restriction for zul template files
	public String getFragmTypeZul() { 
		if (selectedFragmentType == null) {
			return null;
		}
	
		return "/WEB-INF/zul_templates/" + selectedFragmentType.toLowerCase() + ".zul";
	}
	
	
	
	public boolean isPopupVisibility() {
		return popupVisibility;
	}

	public void setPopupVisibility(boolean popupVisibility) {
		this.popupVisibility = popupVisibility;
	}
	
	
	/////STARTUP/////
	
	@AfterCompose
	public void startup() throws Exception {
	    
	    //initialize PageManipulator
		String mainPagePath = WebApps.getCurrent().getServletContext().getRealPath("generated-page.html");	//uses ZK to resolve the path to the mainpage
																											//NOTE the main page file must be inside the root dir of the webapp
		/* DEBUG 
		 * System.out.println("**DEBUG** ZK returned mainPagePath: " + mainPagePath); 
		 */
		pageManip = new PageManipulator(new File(mainPagePath), true, true);	//the last arg is to activate debug output
	
	    //initialize FragmentGenerator
		String templatesFolderName = "velocity_templates";	//NOTE the folder for the templates must be inside WEB-INF
		fragmentGen = new FragmentGenerator(templatesFolderName);
	    
	}
	
	
	/////POPUP WINDOW/////
	
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
	
	
	/////TREE OPERATIONS/////
	
	@GlobalCommand
	@NotifyChange("model")
	public void addElementGlobal(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) throws Exception {
		System.out.println("**DEBUG** addElementGlobal received pipeHashMap: " + pipeHashMap);
		
		////create new node and save it into draggableTree
		DraggableTreeCmsElement newDraggableTreeCmsElement =  new DraggableTreeCmsElement(selectedElement, pipeHashMap);	//NOTE the pipeHashMap is COPIED into the new element
		Map<String, String> newElDataMap = newDraggableTreeCmsElement.getElementDataMap();	//just saved for later brevity
		newElDataMap.put("parentId", selectedElement.getElementDataMap().get("id"));
		newElDataMap.put("siblingsPosition", ( (Integer)selectedElement.getChilds().indexOf(newDraggableTreeCmsElement) ).toString());	//can't call .toString on primitive type int, so I use Integer
	
		////create new DOM node and write it to output page
		//generate fragment code with Velocity
		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(newElDataMap.get("fragmentType")), newElDataMap);
		//rebuild the output page with the new fragment
		pageManip.addFragment(newFragmentHtml, newElDataMap.get("parentId"), Integer.parseInt(newElDataMap.get("siblingsPosition")));																
		//force iframe refresh (using client-side js)
		forceIframeRefresh();
	}
	
//	@Command
//	@NotifyChange("model")
//	public void removeElement() {
//		Map<String, Object> pipeHashMap = new HashMap<String, Object>();
//		pipeHashMap.put("fragmentId", selectedElement.getElementDataMap().get("id"));
//		BindUtils.postGlobalCommand(null, null, "removeFragmentGlobal", pipeHashMap);
//		
//		DraggableTreeComponent.removeFromParent(selectedElement);
//		root.recomputeSpacersRecursive();
//	}
//	
//	//FRAGMENT REMOVAL
//	
//	@GlobalCommand("removeFragmentGlobal")
//	public void removeFragment(@BindingParam("fragmentId") String fragmentId) throws Exception {
//		System.out.println("**DEBUG** removeFragmentGlobal RECEIVED ID: " + fragmentId);
//		
//		pageManip.removeFragment(fragmentId);
//		forceIframeRefresh();
//	}
	
	
	//UTILITIES
	private void forceIframeRefresh() {
		Clients.evalJavaScript("document.getElementsByTagName(\"iframe\")[0].contentWindow.location.reload(true);");	//see: https://stackoverflow.com/questions/13477451/can-i-force-a-hard-refresh-on-an-iframe-with-javascript?lq=1
		System.out.println("**DEBUG** Forced Iframe refresh.");
	}
	
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////
//////COMMANDS		
//@Command
//@NotifyChange("*")
//public void deleteNode(){
//DraggableTreeComponent.removeFromParent(selectedElement);
//root.recomputeSpacersRecursive();
//idList.remove(selectedElement.getAttributeDataMap().get("id"));
//}

//@NotifyChange("*")
//public void addComponent() throws Exception{
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////// CHECK DATA
//String errString=null;

//System.out.println( fragmentMap.get(selectedFragment));
//System.out.println( fragmentMap.get(selectedFragment).toString());

//errString=checkFields(idList, selectedFragment, attributeDataMap, fragmentMap.get(selectedFragment));
//if ((errString.equals(""))==true) {
//
//Clients.showNotification("Tutto OK");	
//
////attributeDataMap=generateFragment();
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// INITIALIZING COMPONENT ELEMENTS WITH MAIN PAGE ELEMENTS
////componentIdList=mainPageIdList;
////componentSelectedElement = mainPageselectedElement;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////// NODE ADDING
////new DraggableTreeCmsElement(componentSelectedElement, fragmentId, selectedFragment, attributeDataMap);
////componentIdList.add(fragmentId);
////Map<String, Object> args = new HashMap<String, Object>();
////args.put("selectedElement", componentSelectedElement);
////args.put("idList", componentIdList);
////BindUtils.postGlobalCommand(null, null, "reloadMainPageTree", args);
////// RESET WINDOW SELECTIONS OR CONTENT
////resetPopUpSelectionAndBack();
//}else {
//addPopupVisibility=true;
//Clients.showNotification(errString);	
//}
//}

//@Command
//@NotifyChange("attributeDataMap")
//public void resetHashMap() {
//for(String currentKey:attributeDataMap.values()) {
//attributeDataMap.put(currentKey, "");
//}


//}

//@Command
//@NotifyChange("*")
//public void resetPopUpSelectionAndBack() {
//resetHashMap();
//addPopupVisibility=false;
//}

