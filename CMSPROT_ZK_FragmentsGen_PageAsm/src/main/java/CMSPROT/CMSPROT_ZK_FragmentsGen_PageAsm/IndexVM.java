package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zul.Iframe;

public class IndexVM {
	
	private Iframe ifr;	//our mainpage on the right side
	private PageManipulator pageManip;
	private FragmentGenerator fragmentGen;
	
	//STARTUP
	
	@AfterCompose
	public void startup() throws Exception {
		
		//get the iframe component by id
	    ifr = (Iframe)Path.getComponent("/indexWindow/ifrInsideZk");		//NOTE: https://www.zkoss.org/wiki/ZK_Developer%27s_Guide/ZK_in_Depth/Component_Path_and_Accesibility/Access_UI_Component
																		//also, remember that if you move the iframe in another position, you'll have to change the path here as well! 
	    
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
		
//	//PAGE GENERATION
	
	@GlobalCommand("saveToTreeGlobal")
	public void generatePage(@BindingParam("pipeMap") Map<String, String> pipeHashMap) throws Exception {

		//generate fragment code with Velocity
		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(pipeHashMap.get("fragmentType")), pipeHashMap);
		
		//rebuild the mainpage with the new fragment
		pageManip.addFragment(newFragmentHtml, "generated-page-root", 0);	//TODO bogus values here
																			//THIS IS THE ONLY OBSTACLE TO COMPLETE THE (INITIAL) BACKEND-FRONTEND MERGE!
		
		//invalidate the iframe to force its refreshing
		ifr.invalidate();
		
	}	
}
