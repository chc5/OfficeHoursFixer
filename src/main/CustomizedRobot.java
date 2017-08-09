package main;
import java.awt.Robot;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.awt.event.InputEvent;

public class CustomizedRobot
{
	//ALL OF THESE VARIABLES ARE IMPROPERLY NAMED DUE TO COMPANY'S RESTRICTIONS.
	private static final int tabsToA = 17;
	private static final int tabsfromAtoB = 4;
	private static final int fromCToNewRow = 5;
	private static final int numberOfDays = 7;
	
	//THIS IS ALL IN PIXELS
	private static final int positionWidthofName = 275;
	private static final int positionHeightofName = 90;
	private static final int positionWidthofUpdate = 195;
	private static final int positionHeightofUpdate = 730;
	private static final int positionWidthofUndo = 320;
	private static final int positionHeightofUndo = 730;
	private static final int positionWidthofTable = 170;
	private static final int positionHeightofTable = 625;
	private static final int positionHeightScrollBar = 700;
	private static final int positionWidthMiddleofScollBar = 845;
	private static final int positionWidthLeftofScollBar = 140;
	
	private static String previousProvider = "Hello World!";
	private static Robot robot;
	private static Clipboard clipboard;
	private static int BADCODEcount = 0;
	
	public CustomizedRobot(){
		try {
			//PRECONDITION MUST OPEN THE APP AND TABLE HAVE TO BE STARTING ON INACTIVE DATE ON THE MOST LEFT
			robot = new Robot();
			robot.delay(2000);//Wait 2 seconds to get window opened
			b:while(true){
				moveTheScollBarBack();
				selectName();
				copyDatatoClipboard();
				if(!verifyingName())
					break;
				clickToTables();
				while(true){
					tabMultipleTimes(tabsToA);
					copyDatatoClipboard();
					if(!verifyingA())
						break;
					tabMultipleTimes(tabsfromAtoB);
					checkingTimes();
					tabMultipleTimes(fromCToNewRow);
					if(BADCODEcount > 3)
						break b;
				}
//				clickUndo();
//				clickUndo();
				clickUpdate();
			}																																																																																																																																																																																																																		
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		robot = null;
	}
	private static void changingTheTimeCell(String timecell) {
		for(int i=0;i<timecell.length();i++) {
			if(timecell.charAt(i) == ' ') {
				timecell = timecell.substring(0, i) + timecell.substring(i+1);
				i--;
			}
		}
		TimeNode node = new TimeNode(timecell);
		try {
			node.reformatData();
			String nodeString = node.toString();
			System.out.println("Time Node toString() : "+nodeString);
			if((nodeString.toLowerCase().indexOf("close")>= 0 && nodeString.length() < 7) || 
				((nodeString.toLowerCase().indexOf("n/a") >= 0 || nodeString.toLowerCase().indexOf("na") >=0) && nodeString.length() <= 3) || 
				(nodeString.toLowerCase().indexOf("off") >=0 && nodeString.length() == 3)) 
			{
				StringSelection selection = new StringSelection("");
				clipboard.setContents(selection, selection);
				System.out.println("TIME NODE STRING HAS BEEN CHANGED TO BLANK");
				pasteDatatoClipboard();
			}
			if(nodeString.indexOf("0")>=0) {
				if(nodeString.length() == 15 || nodeString.length() == 31) {
					StringSelection selection = new StringSelection(nodeString);
					clipboard.setContents(selection, selection);
					pasteDatatoClipboard();
				}
				else {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("BAD CODE: "+timecell);
			BADCODEcount++;
			//e.printStackTrace();
		}
		node = null;

	}
	private static void checkingTimes() throws AWTException{
		//Check for discrepancies and change
		for(int i = 0; i<numberOfDays;++i) {
			copyDatatoClipboard();
			//robot.delay(1000);
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable contents = clipboard.getContents(null);
			String timecell = null;
			if(contents != null  && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try{
					timecell = (String)contents.getTransferData(DataFlavor.stringFlavor);
					if(timecell.length() != 0)
						changingTheTimeCell(timecell);
					else {
						System.out.println("EMPTY STRING");
					}
				} catch (UnsupportedFlavorException | IOException ex){
					System.out.println(ex);
					ex.printStackTrace();
				}
			}
			tabMultipleTimes(1);
		}
	}
	private static void copyDatatoClipboard(){
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
	}
	private static void pasteDatatoClipboard() {
		robot.keyPress(KeyEvent.VK_Z);
		robot.keyRelease(KeyEvent.VK_Z);
		robot.keyPress(KeyEvent.VK_BACK_SPACE);
		robot.keyRelease(KeyEvent.VK_BACK_SPACE);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(1000);
	}
	private static void tabMultipleTimes(int numberOfTimes) throws AWTException{
		for(int i=0;i<numberOfTimes;i++) {
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
		}
	}
	private static void clickUpdate(){
		//Click on Update
		robot.mouseMove(positionWidthofUpdate,positionHeightofUpdate);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(2000);//The server is slow to respond.
	}
	private static void clickUndo() {
		robot.mouseMove(positionWidthofUndo, positionHeightofUndo);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}	
	private static boolean verifyingA(){
		// After tabbingtoTIN(), verify the TIN by checking if it's empty or not using the clipboard;
		// If true, continue to change the schedule by tabbingFromTINtoMon(). If false, go to clickUpdate();
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		String tinname = null;
		if(contents == null || !contents.isDataFlavorSupported(DataFlavor.stringFlavor))
			return false;
		try{
			tinname = (String)contents.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException | IOException ex){
			System.out.println(ex);
			ex.printStackTrace();
		}

		if(tinname.equals("")){
			return false;
		}
		return true;
	}
	private static void clickToTables() throws AWTException{
		//After selecting a name from selectName(), the robot will click its way down to the tables
		robot.mouseMove(positionWidthofTable,positionHeightofTable);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	private static void selectName(){
		//The robot will select the person's name and press down to go to another provider
		robot.mouseMove(positionWidthofName,positionHeightofName);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyRelease(KeyEvent.VK_DOWN);
	}
	private static boolean verifyingName(){
		//Check for infinite loop at the end by checking the currentProvider and the previous provider
		// Return false breaks the loop and print the last name at the end to prove we have finished
		// Return true to continue the loop and copy the currentProviders name on to the previousProvider string;
		String currentName = null;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if(contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)){
			try{
				currentName = (String)contents.getTransferData(DataFlavor.stringFlavor);
				System.out.println(currentName+ ":");
			} catch (UnsupportedFlavorException | IOException ex){
				System.out.println(ex);
				ex.printStackTrace();
			}
			if(currentName.equals(previousProvider)){
				System.out.println(currentName);
				return false;
			}
		}
		else{
			if(previousProvider.equals(""))
				System.out.println("NO NAME");
			else
				System.out.println(previousProvider);
			return false;
		}
		previousProvider = currentName;
		return true;
	}
	private static void moveTheScollBarBack() {
		robot.mouseMove(positionWidthMiddleofScollBar, positionHeightScrollBar);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseMove(positionWidthLeftofScollBar, positionHeightScrollBar);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
}