package rpc.core.common.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {

    private static List<Class<?>> classes;
    private static ClassLoader classLoader = ClassUtil.class.getClassLoader();//默认使用的类加载器

    public static String getMainClassName() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        return stack[stack.length - 1].getClassName();
    }


    /**
     * 查找包内的class
     * @param classPath  要查找的类路径
     * @param needSearchInJar  是否需要在jar里查找  false:只在本地查找  true:即在本地查找，也在jar里查找
     * @return
     */
    public static List<Class<?>> searchClasses(String classPath, boolean needSearchInJar){
        classes = new ArrayList<>();
        try {
            if(needSearchInJar) {
                Enumeration<URL> urls = classLoader.getResources(classPath.replace(".", "/"));
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    String protocol = url.getProtocol();
                    if ("file".equals(protocol)) {
                        // 本地自己可见的代码
                        findClassLocal(url.toURI(), classPath);
                    } else if ("jar".equals(protocol)) {
                        // 引用jar包的代码
                        findClassJar(url, classPath.replace(".", "/"));
                    }
                }
            }else{
                URI uri = classLoader.getResource(classPath.replace(".", "/")).toURI();
                findClassLocal(uri, classPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 本地查找
     */
    private static void findClassLocal(URI uri, String packName)throws Exception{
        File file = new File(uri);
        file.listFiles(chiFile -> {
            if(chiFile.isDirectory()){
                String subPackName =  packName+"."+chiFile.getName();
                try {
                    URI subUrl = classLoader.getResource(subPackName.replace(".", "/")).toURI();
                    findClassLocal(subUrl,subPackName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(chiFile.getName().endsWith(".class")){
                Class<?> clazz = null;
                try {
                    clazz = classLoader.loadClass(packName + "." + chiFile.getName().replace(".class", ""));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                classes.add(clazz);
                return true;
            }
            return false;
        });

    }

    /**
     * jar包查找
     * @param url
     */
    private static void findClassJar(URL url,String pathName) throws IOException {
        JarFile jarFile  = null;
        try {
            JarURLConnection jarURLConnection  = (JarURLConnection )url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();

            if(jarEntryName.contains(pathName) && !jarEntryName.equals(pathName+"/")){
                //递归遍历子目录
                if(jarEntry.isDirectory()){
                    String clazzName = jarEntry.getName().replace("/", ".");
                    int endIndex = clazzName.lastIndexOf(".");
                    String prefix = null;
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex);
                    }
                    prefix = prefix.replace(".", "/");
                    Enumeration<URL> subUrls = classLoader.getResources(prefix);
                    while (subUrls.hasMoreElements()) {
                        URL subUrl = subUrls.nextElement();
                        if (subUrl.getPath().startsWith(url.getPath())) {//子目录以父目录作为开始，保证是同一个jar包内
                            findClassJar(subUrl,prefix);
                        }
                    }

                }
                if(jarEntry.getName().endsWith(".class")){
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    classes.add(clazz);
                }
            }

        }

    }

}
