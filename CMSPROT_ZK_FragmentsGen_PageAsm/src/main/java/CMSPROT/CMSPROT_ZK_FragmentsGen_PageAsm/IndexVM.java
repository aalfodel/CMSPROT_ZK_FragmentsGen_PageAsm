package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.File;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Clients;

public class IndexVM {
	
	private PageManipulator pageManip;
	private FragmentGenerator fragmentGen;
	
	//STARTUP
	
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
		
	//PAGE GENERATION
	
	@GlobalCommand("generatePageGlobal")
	public void generatePage(@BindingParam("pipeHashMap") Map<String, String> pipeHashMap) throws Exception {
		System.out.println("**DEBUG** generatePageGlobal RECEIVED MAP: " + pipeHashMap);

		//generate fragment code with Velocity
		String newFragmentHtml = fragmentGen.generateFragmentHtml(FragmentType.valueOf(pipeHashMap.get("fragmentType")), pipeHashMap);
		
		//rebuild the mainpage with the new fragment
		pageManip.addFragment(newFragmentHtml, pipeHashMap.get("parentId"), Integer.parseInt(pipeHashMap.get("siblingsPosition")));																
		
		//force iframe refresh (using client-side js)
		Clients.evalJavaScript("document.getElementsByTagName(\"iframe\")[0].contentWindow.location.reload(true);");	//see: https://stackoverflow.com/questions/13477451/can-i-force-a-hard-refresh-on-an-iframe-with-javascript?lq=1
		
	}	
}
