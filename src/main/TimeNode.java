package main;

public class TimeNode{
	public static final String[] dividers = {",","-",":"};
	private String key;
	private TimeNode left = null;
	private TimeNode right = null;
	private boolean isPeriod = false;
	private boolean isAM = true;
	public TimeNode(String timecell) {
		// Analyzing discrepancies and separating the numbers by the dividers into a binary tree.
		for(int i=0;i<dividers.length;i++) {
			int index = timecell.indexOf(dividers[i]);
			if(index>=0) {
				String[] timeparts = timecell.split(dividers[i]);
				key = dividers[i];
				left = new TimeNode(timeparts[0]);
				if(timeparts.length > 2)
					System.out.println("BAD CODE");
				if(timeparts.length == 2)
					right = new TimeNode(timeparts[1]);
				break;
			}
		}
		if(left == null && right == null)
			key = timecell;
	}
	public void reformatData() throws Exception {
		//Recursively reformatting the TimeCell and fixing each levels of the binary tree.
		//The end result should be for example: 09:00AM-05:00PM
		if(this.key.equals(",")) {
			this.left.reformatData();
			this.right.reformatData();
		}
		if((this.key.equals("-"))) {
			if(!this.left.getKey().equals(":")) {
				String s = this.left.getKey();
				boolean isAM = s.toLowerCase().indexOf("am") >=0;
				boolean isPM = s.toLowerCase().indexOf("pm") >=0;
				this.left.setIsAM(isAM);
				if(isAM || isPM) {
					this.left.setIsPeriod(true);
					s = s.substring(0,s.length()-2);
				}
				this.left.setKey(":");
				this.left.setLeft(new TimeNode(s));
				this.left.setRight(new TimeNode("00"));
			}
			if(!this.right.getKey().equals(":")) {
				String s = this.right.getKey();
				boolean isAM = s.toLowerCase().indexOf("am") >=0;
				boolean isPM = s.toLowerCase().indexOf("pm") >=0;
				this.right.setIsAM(isAM);
				if(isAM || isPM) {
					this.right.setIsPeriod(true);
					s = s.substring(0,s.length()-2);
				}
				this.right.setKey(":");
				this.right.setLeft(new TimeNode(s));
				this.right.setRight(new TimeNode("00"));
			}
			this.left.reformatData();
			this.right.reformatData();
		}
		if(this.key.equals(":")) {
		//This function also accounts for whether there is AM or PM on the string specifically
			if(this.right.getKey().toLowerCase().indexOf("am") >=0 ||this.right.getKey().toLowerCase().indexOf("pm") >=0) {
				if(this.right.getKey().toLowerCase().indexOf("pm") >=0)
					isAM = false;
				this.right.setKey(this.right.getKey().substring(0,this.right.getKey().length()-2));
				isPeriod = true;
			}
		}
		if(this.key.equals("-")) {
			String timeBeginMin = this.getLeft().getRight().getKey();
			String timeEndMin = this.getRight().getRight().getKey();
			int timeBeginHour = Integer.valueOf(this.getLeft().getLeft().getKey());
			int timeEndHour = Integer.valueOf(this.getRight().getLeft().getKey());
			boolean isLeftPeriod = this.left.isPeriod();
			boolean isRightPeriod = this.right.isPeriod();
			boolean isLeftAM = this.left.isAM();
			boolean isRightAM = this.right.isAM();
			if(isRightPeriod && isRightAM && timeEndHour == 12) {
				isRightAM = false;
			}
			if(isLeftPeriod && isLeftAM && timeBeginHour == 12) {
				isLeftAM = false;
			}
			if(isLeftPeriod || isRightPeriod) {
				if(isLeftPeriod) {
					if(isLeftAM)
						timeBeginMin+="AM";
					else
						timeBeginMin+="PM";
				}

				if(isRightPeriod) {
					if(isRightAM)
						timeEndMin+="AM";
					else
						timeEndMin+="PM";
				}
				if(isLeftPeriod && !isRightPeriod) {
					if(isLeftAM && timeBeginHour < timeEndHour && timeBeginHour != 12)
						timeEndMin+="AM";
					else
						timeEndMin+="PM";
				}
				if(isRightPeriod && !isLeftPeriod) {
					if(!isRightAM && timeBeginHour < timeEndHour && timeEndHour != 12) 
						timeBeginMin+="PM";
					else
						timeBeginMin+="AM";

				}
			}
			else {
				// If the string does not specifically says the period of time, 
				// The code below checks the times adds the AM/PM in accordingly.
				// This is all under the assumption that everyone is working under
				// normal working hours.
				if(timeBeginHour>=7 && timeBeginHour<12) {
					timeBeginMin += "AM";
					if(timeBeginHour<timeEndHour && timeEndHour != 12)
						timeEndMin+="AM";
					else
						timeEndMin+="PM";
				}
				else {
					if(timeBeginHour>=timeEndHour && timeBeginHour != 12) {
						timeBeginMin+="AM";
						timeEndMin+="PM";
					}
					else {
						timeBeginMin+="PM";
						timeEndMin+="PM";
					}
				}
			}

			this.getLeft().getRight().setKey(timeBeginMin);
			this.getRight().getRight().setKey(timeEndMin);
			if(timeBeginHour<10)
				this.getLeft().getLeft().setKey("0"+timeBeginHour);
			if(timeEndHour<10)
				this.getRight().getLeft().setKey("0"+timeEndHour);
		}
	}
	public void setIsPeriod(boolean isPeriod) {
		this.isPeriod = isPeriod;
	}
	public void setIsAM(boolean isAM) {
		this.isAM = isAM;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public TimeNode getRight() {
		return right;
	}
	public TimeNode getLeft() {
		return left;
	}
	public void setRight(TimeNode right) {
		this.right = right;
	}
	public void setLeft(TimeNode left) {
		this.left = left;
	}
	public String toString() {
		//Recursively get the strings in this binary tree.
		String s = "";
		if(getLeft() != null)
			s += getLeft().toString();
		if(getKey()!=null);
		s+=getKey();
		if(getRight()!= null)
			s+=getRight().toString();
		return s;
	}
	public boolean isAM() {
		return isAM;
	}
	public boolean isPeriod() {
		return isPeriod;
	}
}
