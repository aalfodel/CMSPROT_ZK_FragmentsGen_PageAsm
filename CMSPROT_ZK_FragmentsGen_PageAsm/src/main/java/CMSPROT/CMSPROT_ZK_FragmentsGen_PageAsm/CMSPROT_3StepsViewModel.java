package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zul.Iframe;

public class CMSPROT_3StepsViewModel {
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
		
	    //initialize PageManipulator
		String mainPagePath = WebApps.getCurrent().getServletContext().getRealPath("mainpage.html");	//uses ZK to resolve the path to the mainpage
		/* DEBUG */
	    System.out.println("**DEBUG** ZK mainPagePath: " + mainPagePath); 
		PageManipulator.setupPageManipulator(new File(mainPagePath), true);
	
	    //initialize FragmentGenerator
		String templatesFolderName = "templates";
	    FragmentGenerator.setupFragmentGenerator(templatesFolderName);
			   
	}
		
	//PAGE GENERATION

	@Command("genPage")
	public void generatePage() throws Exception {

		//generate fragment code with Velocity
		String newFragmentHtml = FragmentGenerator.generateFragmentHtml(FragmentType.PARAGRAPH, fillPassingMap());
		
		//rebuild the mainpage with the new fragment
		String parentId = "0";
		int fragmentPosition = 1;
		PageManipulator.addFragment(newFragmentHtml, parentId, fragmentPosition);
		
		//get the iframe component by id and invalidate it to force its refreshing
		Iframe ifr = (Iframe)Path.getComponent("/mainWindow/ifr");		//NOTE: https://www.zkoss.org/wiki/ZK_Developer%27s_Guide/ZK_in_Depth/Component_Path_and_Accesibility/Access_UI_Component
																		//also, remember that if you move the iframe in another position, you'll have to change the path here as well!
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
