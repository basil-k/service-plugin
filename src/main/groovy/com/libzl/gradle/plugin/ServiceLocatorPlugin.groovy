package com.libzl.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * Created by basil on 24-02-2017.
 */
class ServiceLocatorPlugin implements Plugin<Project>{

    public static final String LOCATION = "resources" +
                                            File.separator +
                                            "main" +
                                            File.separator +
                                            "META-INF" +
                                            File.separator +
                                            "services";

    private static void writeToProperty(File dir, String type, String main){
            File fi = new File(dir, type);
            if (!fi.exists()){
                fi.createNewFile();
                PrintWriter writer = null;
                try{
                    writer = new PrintWriter(new FileOutputStream(fi))
                    writer.write(main);
                    writer.flush();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (writer != null){
                        writer.close();
                    }
                }
            }
    }

    private static File checkAndCreateOutputDir(File buildDir){
        File f =  new File(buildDir, LOCATION);
        if (!f.exists()){
            f.mkdirs();
        }
        return f;
    }

    @Override
    void apply(Project project) {
        project.extensions.create("service", ServiceConf.class)
        if (!project.plugins.hasPlugin("java") && !project.plugins.hasPlugin("groovy")){
           project.plugins.apply("java")
        }
        project.tasks.getByName('jar')?.doFirst {
            String main = project.service.main;
            String type = project.service.type;

            if (!((main && main.isEmpty()) ||
                    (type && type.isEmpty()))){
                File f = checkAndCreateOutputDir(project.getBuildDir());
                writeToProperty(f, type, main)
            }
        }

    }
}
