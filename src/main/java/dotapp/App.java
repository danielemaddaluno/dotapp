package dotapp;
import java.io.File;

class App extends Folder{
	public Contents contents;

	App(File output_folder, String app_name){
		super(output_folder, app_name + ".app");
		this.contents = new Contents(this.folder);
	}


	class Contents extends Folder{
		public MacOS macos;
		public Resources resources;
		public File info_plist;

		Contents(File app){
			super(app, "Contents");
			this.macos = new MacOS(folder);
			this.resources = new Resources(folder);
			this.info_plist = new File(folder, "Info.plist");
		}

		class MacOS extends Folder{
			public File launcher;

			MacOS(File contents){
				super(contents, "MacOS");
				this.launcher = new File(folder, "launcher");
			}
		}

		class Resources extends Folder{
			public File application_icns;

			Resources(File contents){
				super(contents, "Resources");
				this.application_icns = new File(folder, "application.icns");
			}
		}
	}
}