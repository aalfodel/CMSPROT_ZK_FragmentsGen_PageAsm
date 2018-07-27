package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Clients;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import biz.opengate.zkComponents.draggableTree.DraggableTreeComponent;
import biz.opengate.zkComponents.draggableTree.DraggableTreeElement;
import biz.opengate.zkComponents.draggableTree.DraggableTreeModel;
import biz.opengate.zkComponents.draggableTree.DraggableTreeElement.DraggableTreeElementType;

public class IndexVM {
	
	private String projectPath;
	private DraggableTreeCmsElement root;
	private DraggableTreeModel model;
	private DraggableTreeCmsElement selectedElement;
	
	private List<String> fragmentTypeList;
	private ArrayList<String> idList;
	private String selectedFragmentType;
	
	private Map<String, HashMap<String, Boolean>> fragmentMap;
	
	private String selectedPopup;
	private String popupType;

	private PageManipulator pageManip;
	private FragmentGenerator fragmentGen;
	
	
	/////GETTERS SETTERS/////
	
	public DraggableTreeCmsElement getRoot() {
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
	
	@NotifyChange("*")
	public void setSelectedFragmentType(String selectedFragmentType) {
		this.selectedFragmentType = selectedFragmentType;
	}
	
	//NOTE: zul templates must be in /WEB-INF/zul_templates dir and their name must be *all* lowercase
	//TODO: manage to remove the "all lowercase" restriction for zul template files
	public String getSelectedFragmentTypeZul() { 	//NOTE chosen type -> get the zul file (different from below)
		if (selectedFragmentType == null) {
			return null;
		}
	
		return "/WEB-INF/zul_templates/" + selectedFragmentType.toLowerCase() + ".zul";
	}
	
	public String getSelectedElementTypeZul() {		//NOTE chosen element -> get the type -> get the zul file (different from above)
		return "/WEB-INF/zul_templates/" + selectedElement.getElementDataMap().get("fragmentType").toLowerCase() + ".zul";
	}
	
	
	public String getSelectedPopup() {
		return selectedPopup;
	}

	public void setSelectedPopup(String selectedPopup) {
		this.selectedPopup = selectedPopup;
	}
	
	public String getPopupType() {
		return popupType;
	}

	public void setPopupType(String popupType) {
		this.popupType = popupType;
	}
	
	
	/////STARTUP/////
	@Init
	@NotifyChange("*")
	public void init() throws Exception {
	    
		projectPath = WebApps.getCurrent().getServletContext().getRealPath("/");
		Map<String, String> attributeDataMap = new HashMap<String, String>();
		attributeDataMap.put("id","generated-page-root");
		attributeDataMap.put("descriptor", "Main Page");
		root = new DraggableTreeCmsElement(null, attributeDataMap);
		
		idList = new ArrayList<String>();
		fragmentMap = FragmentMap.getFragmentMap();
		
		try {
			// LOAD MAIN JSON OBJECT
			FileReader reader = new FileReader(projectPath+"savedRoot.json");
			JsonParser parser = new JsonParser();
			JsonObject draggableTreeCmsElement = (JsonObject) parser.parse(reader);
			reloadTree(root, draggableTreeCmsElement, true);
			
		} catch (Exception e) {
			System.out.println("Generated default tree root");
		}

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
	
	
	/////POPUP WINDOWS/////
	
	@Command
	@NotifyChange({"selectedPopup", "popupType"})
	public void openPopup(@BindingParam("popupType") String popupType) {
		selectedPopup = "/WEB-INF/" + "popup_" + popupType + ".zul";	//ex.  add -> /WEB-INF/popup_add.zul
		this.popupType = popupType;
	}
	
	@GlobalCommand
	@NotifyChange("*")
	public void closePopup() {
		selectedPopup = null;
		popupType = null;
		selectedFragmentType = null;
	}
	
	//TODO This one works but it's ugly. Find an alternative solution
	//send draggableTree selected element data to fill the form in the "modify" popup
	@GlobalCommand
	public void passDataToFillPopupInner() {
		Map<String, Object> wrapperMap = new HashMap<String, Object>();
		wrapperMap.put("selectedElement", selectedElement);
		//System.out.println("**DEBUG** -passDataToFillPopupInner DATA TO SEND-:");
		//System.out.println("**DEBUG** \tselectedElement: " + selectedElement + " HAS " + selectedElement.getElementDataMap());
		//System.out.println("**DEBUG** \twrapper: " + wrapperMap);
		//System.out.println("**DEBUG** -/passDataToFillPopupInner-");
		BindUtils.postGlobalCommand(null, null, "getDataToFillPopupInner", wrapperMap);
		
	}
		
//	create new component and attach it to DOM
//	Executions.createComponents("/WEB-INF/zul_templates/title.zul", null,null);		

	/////TREE OPERATIONS/////
	
	@GlobalCommand
	@NotifyChange({"model","selectedFragmentType","selectedPopup", "popupType"})
	public void addElementGlobal(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) throws Exception {
		System.out.println("**DEBUG** addElementGlobal received pipeHashMap: " + pipeHashMap);
		
		////// CHECK DATA
		String errString = null;
		errString = checkFields(pipeHashMap, fragmentMap.get(selectedFragmentType));
		if (idList.contains(pipeHashMap.get("id"))) {
			errString += "Node Id already exists. Please change it";
		}
		if ((errString.equals(""))) {
			
			/*create new node and save it into draggableTree*/
			DraggableTreeCmsElement newDraggableTreeCmsElement =  new DraggableTreeCmsElement(selectedElement, pipeHashMap);	//NOTE the pipeHashMap is COPIED into the new element
			root.recomputeSpacersRecursive();
			idList.add(newDraggableTreeCmsElement.getElementDataMap().get("id"));
			Map<String, String> newElDataMap = newDraggableTreeCmsElement.getElementDataMap();	//just saved for later brevity
			newElDataMap.put("parentId", selectedElement.getElementDataMap().get("id"));
			newElDataMap.put("siblingsPosition", ( (Integer)selectedElement.getChildren().indexOf(newDraggableTreeCmsElement) ).toString());	//can't call .toString on primitive type int, so I use Integer
			/*create new DOM node and write it to output page*/
			//generate fragment code with Velocity
			String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(newElDataMap.get("fragmentType")), newElDataMap);
			//rebuild the output page with the new fragment
			pageManip.addFragment(newFragmentHtml, newElDataMap.get("parentId"), Integer.parseInt(newElDataMap.get("siblingsPosition")));																
			//force iframe refresh (using client-side js)
			saveTreeToDisc();
			forceIframeRefresh();
			closePopup();
			selectedFragmentType = null;	//reset to show empty values to the next add
		} else {
			Clients.showNotification(errString);
		}
	}
	
	@Command
	@NotifyChange({"model","selectedPopup","popupType"})
	public void removeElement() throws Exception {
		//remove from output page
		pageManip.removeFragment(selectedElement.getElementDataMap().get("id"));
		forceIframeRefresh();
		idList.remove(selectedElement.getElementDataMap().get("id"));
		//remove from draggableTree
		DraggableTreeComponent.removeFromParent(selectedElement);
		root.recomputeSpacersRecursive();
		saveTreeToDisc(); 
		closePopup();
	}
	
	@GlobalCommand
	@NotifyChange({"model","selectedPopup","popupType"})
	public void modifyElementGlobal(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) throws Exception {
		System.out.println("**DEBUG** modifyElementGlobal received pipeHashMap: " + pipeHashMap);
		
		////// CHECK DATA
		String errString = null;
		errString = checkFields(pipeHashMap, fragmentMap.get(selectedElement.getElementDataMap().get("fragmentType")));
		
		if (!selectedElement.getElementDataMap().get("id").equals(pipeHashMap.get("id")) && idList.contains(pipeHashMap.get("id"))){
				errString += "Node Id already exists. Please change it";
		}
		if ((errString.equals(""))) {
		/*create new node and save it into draggableTree*/
		DraggableTreeCmsElement newDraggableTreeCmsElement =  new DraggableTreeCmsElement(selectedElement.getParent(), pipeHashMap);	//NOTE the pipeHashMap is COPIED into the new element
		for (DraggableTreeElement currentEl : selectedElement.getChildren()) {
			if (currentEl instanceof DraggableTreeCmsElement) {
				new DraggableTreeCmsElement(newDraggableTreeCmsElement, ((DraggableTreeCmsElement) currentEl).getElementDataMap());
			}
		}
		DraggableTreeComponent.removeFromParent(selectedElement);
		root.recomputeSpacersRecursive();
		Map<String, String> newElDataMap = newDraggableTreeCmsElement.getElementDataMap();	//just saved for later brevity
		/*create new DOM node and write it to output page*/
		//generate fragment code with Velocity
		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(newElDataMap.get("fragmentType")), newElDataMap);
		//rebuild the output page with the new fragment
		pageManip.addFragment(newFragmentHtml, newElDataMap.get("parentId"), Integer.parseInt(newElDataMap.get("siblingsPosition")));																
		//force iframe refresh (using client-side js)
		forceIframeRefresh();
		saveTreeToDisc(); 
		
	
		
//	
//		/*create new DOM node and write it to output page*/
//		//generate fragment code with Velocity
//		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(newElDataMap.get("fragmentType")), newElDataMap);
//		//rebuild the output page with the new fragment
//		pageManip.addFragment(newFragmentHtml, newElDataMap.get("parentId"), Integer.parseInt(newElDataMap.get("siblingsPosition")));																
//		//force iframe refresh (using client-side js)
//		forceIframeRefresh();
		
		closePopup();
		}
		else {
			Clients.showNotification(errString);
		}
	}

	/////UTILITIES/////
	private void forceIframeRefresh() {
		Clients.evalJavaScript("document.getElementsByTagName(\"iframe\")[0].contentWindow.location.reload(true);");	//see: https://stackoverflow.com/questions/13477451/can-i-force-a-hard-refresh-on-an-iframe-with-javascript?lq=1
		System.out.println("**DEBUG** Forced Iframe refresh.");
	}
	
	//////LOAD SAVED TREE	
	private void reloadTree(DraggableTreeCmsElement currentElement, JsonObject currentJsonObject, Boolean iAmRoot) throws Exception{
		// ELEMENTS TO LOAD
		Map<String, String> loadedMap = new HashMap<String, String>();
		DraggableTreeElementType treeElementType=null;
		String loadedDescription = null;
		JsonArray currentChilds = new JsonArray();
		// LOADING ELEMENTS ONE BY ONE
		Gson gson = new Gson();
		// CURRENT ATTRIBUTE MAP 
		loadedMap = gson.fromJson(currentJsonObject.getAsJsonObject("elementDataMap"), Map.class);
		// ELEMENT TYPE 
		treeElementType = gson.fromJson(currentJsonObject.getAsJsonObject().getAsJsonPrimitive("type"), DraggableTreeElementType.class);
		// CHILDREN LIST 
		currentChilds = currentJsonObject.getAsJsonArray("children");
		// DESCRIPTION 
		loadedDescription=gson.fromJson(currentJsonObject.get("description"), String.class);
		
		if (iAmRoot) {
			currentElement.setDescription(loadedDescription); 
			currentElement.setElementDataMap(loadedMap);
			
			if (currentChilds.size()>0) {
				Iterator<JsonElement> localChildren = currentChilds.iterator();
				
				while (localChildren.hasNext()) {
					reloadTree(currentElement, localChildren.next().getAsJsonObject(), false);
				}
			}	
		}
		else {
			if(treeElementType.equals(DraggableTreeElementType.NORMAL)) {
				DraggableTreeCmsElement localNode = new DraggableTreeCmsElement(currentElement, loadedMap);		
				idList.add(loadedMap.get("id"));
				root.recomputeSpacersRecursive();	
	
				if (currentChilds.size()>0) {
					Iterator<JsonElement> localChildren = currentChilds.iterator();
					while (localChildren.hasNext()) {
						reloadTree(localNode,localChildren.next().getAsJsonObject() ,false);
					}				
				}	
			}
		}	
	}
	
	////// CHECK IF ALL THE MANDATORY FIELDS ARE NOT EMPTY
	private String checkFields(Map<String, String> attributeMap,
			HashMap<String, Boolean> controlComponentMap) {
		String errMsgFun = "";
		System.out.println("Recived map");
		System.out.println(attributeMap);
		System.out.println("Control map");
		System.out.println(controlComponentMap);
		// CYCLE ON CONTROL MAP ID'S
		for (String currentCheckName : controlComponentMap.keySet()) {
			System.out.println(currentCheckName);
			if (!currentCheckName.equals("fragmentType") || !currentCheckName.equals("siblingsPosition") || !currentCheckName.equals("parentId") ) {
				Boolean currentCheckBool = controlComponentMap.get(currentCheckName);
				System.out.println(currentCheckBool);
				if (currentCheckBool) {
					System.out.println(currentCheckName + " con controllo obbligatorio " + currentCheckBool);
					String localString = attributeMap.get(currentCheckName);
					System.out.println(localString);
					if (localString==null || localString.equals("")) {
						errMsgFun += currentCheckName;
						errMsgFun += " \n";
					}
				}		
			}
		}

		if ((errMsgFun.equals("")) == false) {
			errMsgFun += " empty. Please insert all the mandatory data. \n";
		}
		System.out.println("Stringa Errore");
		System.out.println(errMsgFun);
		return errMsgFun;
	}
	
	//////SAVE TREE	
	private void saveTreeToDisc() throws Exception {
		Gson gson = new Gson();
		System.out.println(projectPath+"savedRoot.json");
		Writer treeWriterUpdate = new FileWriter(projectPath+"savedRoot.json"); 
		System.out.println("writer ok");
		try {
			gson.toJson(root, treeWriterUpdate);
			treeWriterUpdate.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}










