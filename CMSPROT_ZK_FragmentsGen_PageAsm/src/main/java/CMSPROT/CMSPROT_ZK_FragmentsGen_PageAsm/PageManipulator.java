package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class PageManipulator {

	private static boolean _debug;
	private static File _mainPage;
	private static Document _docTree;	
	
	//SETUP
	
	public static void setupPageManipulator(File mainPage, boolean indentation) throws IOException {
		_debug = false;
		
		_mainPage = mainPage;
		_docTree = Jsoup.parse(mainPage, null);
		
		if(indentation) {
			_docTree.outputSettings().prettyPrint(true).indentAmount(4);	//sets output indentation
		}
	}
	
	public static void setupPageManipulator(File mainPage, boolean indentation, boolean debug) throws IOException {
		setupPageManipulator(mainPage, indentation);
		_debug = debug;
	}
	
	//DOM MANIPULATION PUBLIC METHODS
	
	public static void addFragment(String fragmentHtml, String fragmentParentId, int fragmentPosition) throws IOException {
		
		Element fragmentTree = getFragmentTreeFromHtml(fragmentHtml);
		addFragment(fragmentTree, fragmentParentId, fragmentPosition);
		saveTreeToFile();
	}
	
	public static void removeFragment(String fragmentId) throws IOException {
		
		removeFragment(_docTree.getElementById(fragmentId));
		saveTreeToFile();
	}
	
	public static void moveFragment(String fragmentId, String newParentId, int newFragmentPosition) throws IOException {
		
		moveFragment(_docTree.getElementById(fragmentId), newParentId, newFragmentPosition);
		saveTreeToFile();
	}
	
	public static void updateFragment(String newFragmentHtml, String oldFragmentId) throws IOException {
		
		Element newFragmentTree = getFragmentTreeFromHtml(newFragmentHtml);
		updateFragment(newFragmentTree, oldFragmentId);
		saveTreeToFile();
	}
	
	//UTILITIES IN/OUT
	
	private static Element getFragmentTreeFromFile(File fragmentInputFile) throws IOException {
		
		String fragmentHtml = FileUtils.readFileToString(fragmentInputFile, "UTF-8");
		Element fragmentTree = getFragmentTreeFromHtml(fragmentHtml);
		return fragmentTree;
	}
	
	//Jsoup automatically wraps a complete html tree around a fragment when parseBodyFragment(), so I had to strip it with this workaround
	private static Element getFragmentTreeFromHtml(String fragmentHtml) {
			return Jsoup.parseBodyFragment(fragmentHtml).body().child(0);	//TODO this is just a stub (?)
	}
	
	private static void saveTreeToFile() throws IOException {
		FileUtils.writeStringToFile(_mainPage, _docTree.html(), "UTF-8"); 
	}
	
	//DOM MANIPULATION
	
	private static void addFragment(Element fragmentTree, String fragmentParentId, int fragmentPosition) throws IOException {
		
		if (_debug) {
			System.out.println("\n+IN TREE (add)+\n");
			printNodeRecursive(_docTree);
			
			System.out.println("\n+FRAGMENT TREE (add)+\n");
			printNodeRecursive(fragmentTree);
		}
		
		Element fragmentParent = _docTree.getElementById(fragmentParentId);
		int siblingsSize = fragmentParent.children().size();
		if( siblingsSize == 0 || fragmentPosition > siblingsSize-1) {
			fragmentTree.appendTo(fragmentParent);
		} else {
			//NOTE: do NOT use "fragmentParent.insertChildren(fragmentPosition, fragmentTree)" because it considers all child Nodes and not only child Elements
			//NOTE: do NOT use "fragmentParent.children().add(fragmentPosition, fragmentTree)" because children() gives a COPY
			Element fragmentPreviousSibling = fragmentParent.child(fragmentPosition);	
			fragmentPreviousSibling.before(fragmentTree);
		}
		if (_debug) {
			System.out.println("\n+OUT TREE (add)+\n");
			printNodeRecursive(_docTree);
		}
	
	}
	
	private static void removeFragment(Element fragmentTreeToRemove) throws IOException {
		if (_debug) {
			System.out.println("\n+IN TREE (remove)+\n");
			printNodeRecursive(_docTree);
		}
		
		fragmentTreeToRemove.remove();
		if (_debug) {
			System.out.println("\n+OUT TREE (remove)+\n");
			printNodeRecursive(_docTree);
		}
	}
	
	private static void moveFragment(Element fragmentTreeToMove, String newParentId, int newFragmentPosition) throws IOException {
		if (_debug) {
			System.out.println("\n+IN TREE (move)+\n");
			printNodeRecursive(_docTree);
		}
		
		removeFragment(fragmentTreeToMove);
		addFragment(fragmentTreeToMove, newParentId, newFragmentPosition);
		
		if (_debug) {
			System.out.println("\n+OUT TREE (move)+\n");
			printNodeRecursive(_docTree);
		}
		
	}
	
	private static void updateFragment(Element newFragmentTree, String oldFragmentId) throws IOException {
		if (_debug) {
			System.out.println("\n+IN TREE (update)+\n");
			printNodeRecursive(_docTree);
			
			System.out.println("\n+FRAGMENT TREE (update)+\n");
			printNodeRecursive(newFragmentTree);
		}
		
		Element oldFragmentTree = _docTree.getElementById(oldFragmentId);
		oldFragmentTree.replaceWith(newFragmentTree);
		if (_debug) {
			System.out.println("\n+OUT TREE (update)+\n");
			printNodeRecursive(_docTree);
		}
	}
	
	//DEBUGGING UTILITIES
		
	private static void printNodeRecursive(Node n) {
		printNodeRecursive(n,"");
	}
	
	private static void printNodeRecursive(Node n, String space) {
			
			if (n == null) return;
			
			else if (n instanceof Element) {
				System.out.println( space + n.nodeName() + " (" + n.attributes() + " )");
			}
	
			else if (n instanceof TextNode) {
				if (! ((TextNode)n).isBlank())	//is needed  otherwise spaces and newlines (by the indentation) would be considered
					System.out.println( space + n.nodeName() + " : " + n );
			}
			
			else {
				System.out.println( space + n.nodeName());
			}
			
			List<Node> children = n.childNodes();
			for(int i=0; i<children.size(); i++)
					printNodeRecursive(children.get(i), space + " - ");		
	}

}

