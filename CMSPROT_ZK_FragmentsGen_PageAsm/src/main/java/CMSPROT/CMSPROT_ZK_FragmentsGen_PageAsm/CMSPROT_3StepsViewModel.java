package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import org.zkoss.bind.annotation.Command;

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
	
	@Command("genPage")
	public void generatePage() {
		System.out.println("OK");
	}
	
}
