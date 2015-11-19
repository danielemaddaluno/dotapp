package dotapp;

import java.io.File;

class Folder {
	protected File folder;
	
	public Folder(File parent_folder, String name){
		this.folder = new File(parent_folder, name);
	}
	
	public File toFile(){
		return folder;
	}
}
