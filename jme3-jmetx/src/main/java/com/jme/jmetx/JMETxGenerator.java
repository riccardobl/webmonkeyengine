package com.jme.jmetx;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.TextureKey;
import com.jme3.export.OutputCapsule;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.system.JmeSystem;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.plugins.DDSLoader;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class JMETxGenerator extends SimpleApplication {
    private static String imageExts[] = new String[] { "png", "jpg", "jpeg", "bmp" };
    private static String compressonatorPath;
    private static String assetRoot;
    private static String tempFolder;
    private DDSLoader ddsLoader;

    private static final CompressonatorFormatMap[] cpFormatMap = new CompressonatorFormatMap[] { new CompressonatorFormatMap(Format.DXT1, "DXT1", false, true),
            new CompressonatorFormatMap(Format.DXT1, "DXT1", false, false), new CompressonatorFormatMap(Format.DXT5, "DXT5", true, true),
            new CompressonatorFormatMap(Format.DXT5, "DXT5", true, false), new CompressonatorFormatMap(Format.ETC1, "ETC2_RGB", false, true),
            new CompressonatorFormatMap(Format.ETC1, "ETC2_RGB", false, false), new CompressonatorFormatMap(Format.ETC2, "ETC2_RGBA", true, true),
            new CompressonatorFormatMap(Format.ETC2, "ETC2_RGBA", true, false)
    };

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("jmetx").build().description("Compress textures for jmetx");
        parser.addArgument("--compressonator-path").dest("compressonator-path").metavar("PATH").type(String.class)
                .help("Path to CompressonatorCLI. If not specified, will try to find it automatically.");

        parser.addArgument("asset root").dest("asset-root").metavar("ASSET_ROOT").type(String.class)
                .help("Path to the root of the asset directory. All textures in this directory and subdirectories will be processed.");

        parser.addArgument("--imageExts").dest("image-exts").metavar("EXTS_CSV").dest("image-exts").type(String.class)
                .help("CSV list of image extensions to process. Default is " + String.join(",", imageExts));

        parser.addArgument("--temp-folder").dest("temp-folder").metavar("PATH").type(String.class)
                .help("Path to a temporary folder. If not specified, will use the system temp folder.");

        try {
            args = new String[] { "/PROJ/Assets/CANTINETTE/rendering/3d/taste-box1/" };
            Namespace res = parser.parseArgs(args);

            String cpath = res.get("compressonator-path");
            if (cpath != null && !cpath.trim().isEmpty()) {
                System.out.println("Use compressonator path " + cpath);
                compressonatorPath = cpath;
            }

            cpath = res.get("asset-root");
            if (cpath != null && !cpath.trim().isEmpty()) {
                System.out.println("Use asset root " + cpath);
                assetRoot = cpath;
            }

            cpath = res.get("image-exts");
            if (cpath != null && !cpath.trim().isEmpty()) {
                System.out.println("Use image extensions " + cpath);
                imageExts = cpath.split(",");
            }

            cpath = res.get("temp-folder");
            if (cpath != null && !cpath.trim().isEmpty()) {
                System.out.println("Use temp folder " + cpath);
                tempFolder = cpath;
            }

        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }

        if (assetRoot == null) {
            parser.printHelp();
            System.exit(1);
        }

        JMETxGenerator tx = new JMETxGenerator();
        tx.start(JmeContext.Type.Headless);

    }

    private String findCompressonator() {

        if (compressonatorPath != null) {
            if (compressonatorPath.trim().isEmpty()) {
                compressonatorPath = null;
            } else {
                return compressonatorPath;
            }
        }

        String envVar = System.getenv("COMPRESSONATOR_PATH");
        if (envVar != null && !envVar.trim().isEmpty()) {
            compressonatorPath = envVar;
            return compressonatorPath;
        }

        String os = JmeSystem.getPlatform().name().toLowerCase();
        if (os.contains("windows")) {
            // amd compressonator CompressonatorCLI
            String possibleWindowsPaths[] = new String[] { "C:\\Program Files\\AMD\\Compressonator\\bin\\CompressonatorCLI.exe",
                    "C:\\Program Files (x86)\\AMD\\Compressonator\\bin\\CompressonatorCLI.exe", "C:\\Program Files\\Compressonator\\bin\\CompressonatorCLI.exe",
                    "C:\\Program Files (x86)\\Compressonator\\bin\\CompressonatorCLI.exe", };
            for (String path : possibleWindowsPaths) {
                File f = new File(path);
                if (f.exists()) {
                    compressonatorPath = path;
                    return compressonatorPath;
                }
            }
            if (compressonatorPath == null) {
                // try to find it in the path
                String path = System.getenv("PATH");
                if (!path.trim().isEmpty()) {
                    String paths[] = path.split(File.pathSeparator);
                    for (String p : paths) {
                        File f = new File(p, "CompressonatorCLI.exe");
                        if (f.exists()) {
                            compressonatorPath = f.getAbsolutePath();
                            return compressonatorPath;
                        }
                    }
                }
            }

        } else if (os.contains("linux")) {
            // amd compressonator CompressonatorCLI
            String possibleLinuxPaths[] = new String[] { "/opt/AMD/Compressonator/bin/compressonatorcli", "/opt/Compressonator/bin/compressonatorcli",
                    "/usr/local/bin/compressonatorcli", "/usr/bin/compressonatorcli", "/usr/compressonatorcli", "/usr/AMD/bin/compressonatorcli", "/usr/AMD/compressonatorcli",
                    "/usr/AMD/Compressonator/bin/compressonatorcli", "/usr/Compressonator/bin/compressonatorcli",

            };
            for (String path : possibleLinuxPaths) {
                File f = new File(path);
                if (f.exists()) {
                    compressonatorPath = path;
                    return compressonatorPath;
                }
            }
            if (compressonatorPath == null) {
                // try to find it in the path
                String path = System.getenv("PATH");
                if (!path.trim().isEmpty()) {
                    String paths[] = path.split(File.pathSeparator);
                    for (String p : paths) {
                        File f = new File(p, "compressonatorcli");
                        if (f.exists()) {
                            compressonatorPath = f.getAbsolutePath();
                            return compressonatorPath;
                        }
                    }
                }
            }
        } else if (os.contains("mac")) {
            // amd compressonator CompressonatorCLI
            String possibleMacPaths[] = new String[] { "/Applications/Compressonator.app/Contents/MacOS/CompressonatorCLI", };
            for (String path : possibleMacPaths) {
                File f = new File(path);
                if (f.exists()) {
                    compressonatorPath = path;
                    return compressonatorPath;
                }
            }
            if (compressonatorPath == null) {
                // try to find it in the path
                String path = System.getenv("PATH");
                if (!path.trim().isEmpty()) {
                    String paths[] = path.split(File.pathSeparator);
                    for (String p : paths) {
                        File f = new File(p, "CompressonatorCLI");
                        if (f.exists()) {
                            compressonatorPath = f.getAbsolutePath();
                            return compressonatorPath;
                        }
                    }
                }
            }
        }

        if (compressonatorPath == null) {
            System.err.println("Could not find CompressonatorCLI. Please install it and try again.");
            System.err.println(
                    "If CompressonatorCLI is installed and you are still getting this error, please set the path to CompressonatorCLI manually by exporting the environment variable COMPRESSONATOR_PATH or by passing --compressonator-path arg to the cli.");
            System.exit(1);
        }
        return compressonatorPath;
    }

    private String makePathRelative(String path) {
        if (path.startsWith(assetRoot)) {
            path = path.substring(assetRoot.length());
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
    
    private String makePathAbsolute(String path) {
        return  new File(assetRoot, path).getAbsolutePath();
        

    }

    private List<String> getAllImages(String root, List<String> out) {
        if (out == null) out = new ArrayList<String>();
        File f = new File(root);
        if (f.exists()) {
            if (f.isDirectory()) {
                File files[] = f.listFiles();
                for (File file : files) {
                    getAllImages(file.getAbsolutePath(), out);
                }
            } else {
                String ext = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                if (ext != null) {
                    for (String e : imageExts) {
                        if (e.equalsIgnoreCase(ext)) {
                            out.add(f.getAbsolutePath());
                            break;
                        }
                    }
                }
            }
        }
        return out;

    }



 

 

    private List<CompressonatorFormatMap> findFormats(ImageInfo info) {
        ArrayList<CompressonatorFormatMap> out = new ArrayList<CompressonatorFormatMap>();
        for (CompressonatorFormatMap map : cpFormatMap) {
            if (map.hasAlpha == info.hasAlpha && map.isSrgb == info.isSrgb) {
                out.add(map);
            }
        }
        return out;
    }


    private Image readAndCompress(String path, CompressonatorFormatMap f) {
        Image out = null;
        try {
            String uid = f.cpFormat+"_"+System.currentTimeMillis() + "_" + Math.random() + "_" + f.cpFormat;

            File outPath = null;// tmp file
            if (tempFolder != null) {
                outPath = new File(tempFolder, "compressonator_" + uid + ".dds");
            }

            if (outPath == null) {
                try {
                    outPath = File.createTempFile("compressonator_"+uid, ".dds");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (outPath == null) {
                outPath = new File(new File(path).getAbsolutePath() + "tmp_compressonator_" + uid + ".dds");
            }

            // String cmd = String.format("\"%s\" -EncodeWith CPU " + "-fd %s \"%s\" \"%s\"", findCompressonator(), f.cpFormat, path, outPath.getAbsolutePath());
            // run cmd and wait for it to finish pass out to system out
            // System.out.println(cmd);
            ArrayList<String> cmd = new ArrayList<String>();
            cmd.add(findCompressonator());
            cmd.add("-miplevels");
            cmd.add("12");
            if (f.cpFormat.contains("ETC")) {
                cmd.add("-doswizzle");
            }
            cmd.add("-EncodeWith");
            cmd.add("CPU");
            cmd.add("-fd");
            cmd.add(f.cpFormat);
            cmd.add(path);
            cmd.add(outPath.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder(cmd);
            System.out.println("Compressing " + path + " to " + outPath);
            System.out.println("Command: " + pb.command());
            
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            try {
                Process p = pb.start();
                p.waitFor();
                p.destroy();
                p = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (outPath.exists()) {
                System.out.println("Compressed " + path + " to " + outPath);
                byte data[] = Files.readAllBytes(outPath.toPath());
                try {
                    // outPath.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AssetInfo tmpInfo = new AssetInfo(assetManager, new TextureKey(path,false)) {
                    public InputStream openStream() {
                        return new ByteArrayInputStream(data);
                    }
                };
                try {
                    if (ddsLoader == null) {
                        ddsLoader = new DDSLoader();
                    }
                    out = (Image) ddsLoader.load(tmpInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (out == null) {
            System.err.println("Failed to compress " + path);
        }

        

        return out;
    }

 
    JmeTxRoot root;

    private void startImageProcess(String path) {
        System.out.println("Processing " + path);
        root = new JmeTxRoot();
    }

    private void addImageVariation(String imagePath, Image img, Format f) throws IOException {
        File outputFile=new File(Paths.get(assetRoot, "jmetx_"+f.name(), makePathRelative(imagePath)).toString()+".jmetx_part");
        outputFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outputFile);
        BinaryExporter.getInstance().save(img, fos);
        root.add(f, makePathRelative(outputFile.getAbsolutePath()));
        fos.close();
           
    }
    
    private void commitImage(String imagePath) throws IOException {
        BinaryExporter exporter = BinaryExporter.getInstance();
        File outputFile = new File(Paths.get(assetRoot, "jmetx", makePathRelative(imagePath)).toString() + ".jmetx");
        outputFile.getParentFile().mkdirs();
        exporter.save(root, outputFile);
    }


    @Override
    public void simpleInitApp() {
        List<String> images = getAllImages(assetRoot, null);
        for (String image : images) {
            System.out.println("Processing " + image);
            try{
                startImageProcess(image);
                ImageInfo info = new ImageInfo(image);
                List<CompressonatorFormatMap> formats = findFormats(info);
                for (CompressonatorFormatMap f : formats) {
                    Image img = readAndCompress(image, f);
                    addImageVariation(image,img, f.jmeFormat);
                }
                commitImage(image);
            }catch(Exception e){
                e.printStackTrace();
                System.err.println("Failed to process "+image);
            }
        }

    }

}
