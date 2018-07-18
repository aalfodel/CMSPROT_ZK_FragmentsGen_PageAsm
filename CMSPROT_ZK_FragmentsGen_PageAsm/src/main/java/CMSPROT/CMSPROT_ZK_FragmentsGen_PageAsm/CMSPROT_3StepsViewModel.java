package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zul.Iframe;

public class CMSPROT_3StepsViewModel {
	
	private Iframe ifr;	//our mainpage on the right side
	private PageManipulator pageManip;
	private FragmentGenerator fragmentGen;
	
	private String id;
	private String text;
	private String color;
	
	//GETTERS SETTERS
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	//STARTUP
	
	@AfterCompose
	public void startup() throws Exception {
		
		//get the iframe component by id
	    ifr = (Iframe)Path.getComponent("/mainWindow/ifrInsideZk");		//NOTE: https://www.zkoss.org/wiki/ZK_Developer%27s_Guide/ZK_in_Depth/Component_Path_and_Accesibility/Access_UI_Component
																		//also, remember that if you move the iframe in another position, you'll have to change the path here as well! 
	    
	    //initialize PageManipulator
		String mainPagePath = WebApps.getCurrent().getServletContext().getRealPath("mainpage.html");	//uses ZK to resolve the path to the mainpage
																										//NOTE the main page file must be inside the root dir of the webapp
		
		System.out.println("**DEBUG** ZK mainPagePath: " + mainPagePath); 	/* DEBUG */ 
		pageManip = new PageManipulator(new File(mainPagePath), true);
	
	    //initialize FragmentGenerator
		String templatesFolderName = "templates";	//NOTE the folder for the templates must be inside WEB-INF
		fragmentGen = new FragmentGenerator(templatesFolderName);
	    
	}
		
	//PAGE GENERATION

	@Command("genPage")
	public void generatePage() throws Exception {

		//generate fragment code with Velocity
		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.PARAGRAPH, fillPassingMap());
		
		//rebuild the mainpage with the new fragment
		String parentId = "0";
		int fragmentPosition = 0;
		pageManip.addFragment(newFragmentHtml, parentId, fragmentPosition);
		
		//invalidate the iframe to force its refreshing
		ifr.invalidate();
	}
	
	//UTILITIES
	
	//TODO this is just a stub
	private Map<String, String> fillPassingMap() {
		Map<String, String> dataPassing = new HashMap<String, String>();
		
		dataPassing.put("id", id);
		dataPassing.put("text", text);
		dataPassing.put("color", color);
		
		return dataPassing;
	}
	
}
