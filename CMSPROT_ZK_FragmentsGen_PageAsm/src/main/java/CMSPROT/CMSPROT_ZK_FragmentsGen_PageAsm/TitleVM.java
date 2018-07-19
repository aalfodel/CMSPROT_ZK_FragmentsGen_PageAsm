package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;

public class TitleVM {
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Command
	public void saveToTree() {
		Map<String, Object> pipeHashMap = new HashMap<String, Object>();
		pipeHashMap.put("text", text);
		BindUtils.postGlobalCommand(null, null, "saveToTreeGlobal", pipeHashMap);
		System.out.println("pipeHM SENT: " + pipeHashMap);
		exitPopup();
	}

	
	@Command
	public void exitPopup() {
		
	}
}
