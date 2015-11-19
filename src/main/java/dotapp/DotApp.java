package dotapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Chmod;
import org.apache.tools.ant.taskdefs.ExecuteOn.FileDirBoth;
import org.apache.tools.ant.types.FileSet;

public class DotApp {
	private final static String RESOURCE_FOLDER = "src/main/resources/";

	private static final String EXECUTABLE_JAR_PLACEHOLDER = "EXECUTABLE_JAR_NAME";
	private static final String SAMPLE_COPY_NAME = "SampleCopy";

	private String app_name = "IBM";
	private String executable_jar = "src/main/resources/input/Finance-1.0.0.BUILD-SNAPSHOT.jar";
	private String resources = "src/main/resources/input/resources";
	private String icons = "src/main/resources/input/application.icns";
	private String output_folder = "out";

	public DotApp setResourcesPath(String resources){
		this.resources = resources;
		return this;
	}

	public DotApp setExecutableJarPath(String executable_jar_path){
		this.executable_jar = executable_jar_path;
		return this;
	}

	public DotApp setAppName(String app_name){
		this.app_name = app_name;
		return this;
	}

	public DotApp setIcns(String icns){
		this.icons = icns;
		return this;
	}

	public DotApp setOutputFolder(String output_folder){
		this.output_folder = output_folder;
		return this;
	}


	public void execute() {
		App resource_app =  new App(new File(RESOURCE_FOLDER), SAMPLE_COPY_NAME);
		App new_app = new App(new File(output_folder), app_name);
		try{
			// Copy Info.plist
			File source_info_plist = resource_app.contents.info_plist;
			File dest_info_plist = new_app.contents.info_plist;
			FileUtils.copyFile(source_info_plist, dest_info_plist);

			// Copy the icons
			File source_icons = new File(icons);
			File dest_icons = new_app.contents.resources.application_icns;
			FileUtils.copyFile(source_icons, dest_icons);

			// Copy resources folder
			File source_resources = new File(resources);
			File dest_resources = new File(new_app.contents.macos.toFile(), source_resources.getName());
			FileUtils.copyDirectory(source_resources, dest_resources);
			FileUtils.deleteDirectory(new File(dest_resources, "META-INF"));

			// Copy the executable jar
			File source_jar = new File(executable_jar);
			File dest_jar = new File(new_app.contents.macos.toFile(), source_jar.getName());
			FileUtils.copyFile(source_jar, dest_jar);

			// Copy the launcher
			InputStreamReader launcher_stream = new InputStreamReader(new FileInputStream(resource_app.contents.macos.launcher));
			String launcher_string = IOUtils.toString(launcher_stream);
			launcher_string = launcher_string.replace(EXECUTABLE_JAR_PLACEHOLDER, source_jar.getName());

			FileUtils.writeStringToFile(new_app.contents.macos.launcher, launcher_string);
			System.out.println(launcher_string);

			// Set launcher permissions, convert it in a unix executable file
			// http://stackoverflow.com/questions/664432/how-do-i-programmatically-change-file-permissions/20592571#20592571
			Chmod chmod = new Chmod();
			chmod.setProject(new Project());
			FileSet mySet = new FileSet();
			mySet.setDir(new_app.contents.macos.toFile());
			mySet.setIncludes("launcher");
			chmod.addFileset(mySet);
			chmod.setPerm("+x");
			chmod.setType(new FileDirBoth());
			chmod.execute();
		} catch(Exception e){
			try {
				FileUtils.deleteDirectory(new_app.toFile());
			} catch (IOException e1) {}
		}
	}
}
