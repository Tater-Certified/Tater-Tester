package com.github.tatercertified.mc;

import com.google.gson.Gson;
import net.lingala.zip4j.ZipFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

import static com.github.tatercertified.gui.MainGUI.*;

public class DownloadMC {

    public static String[] versions;

    static {
        try {
            versions = Arrays.stream(getVersions()).toList().toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static String lateststableversion = getLatestStableVersion(List.of(versions));
    static String currentloader;



    public static void download() throws IOException {

        System.out.println("You are downloading "+ loader + " Launcher "+ loaderversion.getSelectedItem().toString() + " for Minecraft "+ minecraftversion.getSelectedItem().toString());

        String url;
        if (Objects.equals(loader, "Fabric")) {
            currentloader = "fabric-loader";
            url = "https://meta.fabricmc.net/v2/versions/loader/" + minecraftversion.getSelectedItem().toString() + "/" + loaderversion.getSelectedItem().toString() + "/profile/zip";
            try {
                download(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (Objects.equals(loader, "Quilt")) {
            currentloader = "quilt-loader";
            url = "https://meta.quiltmc.org/v3/versions/loader/" + minecraftversion.getSelectedItem().toString() + "/" + loaderversion.getSelectedItem().toString() + "/profile/zip";
            try {
                download(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            currentloader = "minecraft";
        }
        extractFile();
    }

    private static void extractFile() {
        try(ZipFile zipFile = new ZipFile("testcraft/" + currentloader + "-" + loaderversion.getSelectedItem().toString() + "-" + minecraftversion.getSelectedItem().toString() + ".zip")) {
            zipFile.extractAll("testcraft");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object[] getVersions() throws IOException {
        Gson gson = new Gson();
        ArrayList<String> out = new ArrayList<>();
        VersionFromJsonButInAClass versionMetaFromJson = gson.fromJson(getVersionsFromMojank(), VersionFromJsonButInAClass.class);
        for (VersionFromJsonButInAClass.Version v: versionMetaFromJson.versions) {
            out.add(v.id);
        }
        return out.toArray();
    }

    public static String getVersionsFromMojank() throws IOException {
        InputStream in = new URL("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").openConnection().getInputStream();
        Scanner jsonScanner = new Scanner(in).useDelimiter("\\A");
        return jsonScanner.hasNext() ? jsonScanner.next() : "";
    }

    static class VersionFromJsonButInAClass {
        Latest latest;
        Version[] versions;

        public VersionFromJsonButInAClass(Latest a, Version[] b) {
            latest = a;
            versions = b;
        }

        public static class Latest {
            static String release;
            String snapshot;

            public static String getRelease() {
                return release;
            }

            public String getSnapshot() {
                return snapshot;
            }
        }

        static class Version {
            String id;
            String type;
            URL url;
            String time;
            String releaseTime;

            public String getId() {
                return id;
            }

            public String getReleaseTime() {
                return releaseTime;
            }

            public String getTime() {
                return time;
            }

            public String getType() {
                return type;
            }

            public URL getUrl() {
                return url;
            }
        }
    }
    public static String getLatestStableVersion(List<String> versions) {
        List<String> snapshots = Arrays.asList("w", "b", "a", "inf", "c", "r");
        List<String> filtered = versions.stream().filter(word -> snapshots.stream().noneMatch(word::contains)).toList();
        return filtered.get(0);
    }

    public static String[] getStableVersions(List<String> versions) {
        List<String> snapshots = Arrays.asList("w", "b", "a", "inf", "c", "r");
        List<String> filtered = versions.stream().filter(word -> snapshots.stream().noneMatch(word::contains)).toList();
        return filtered.toArray(new String[0]);
    }

    public static String getLatestDevVersion(List<String> versions) {
        List<String> stable = List.of("1.");
        List<String> filtered = versions.stream().filter(word -> stable.stream().noneMatch(word::contains)).toList();
        return filtered.get(0);
    }

    private static void download(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream("testcraft/" + currentloader + "-" + loaderversion.getSelectedItem().toString() + "-" + minecraftversion.getSelectedItem().toString() + ".zip");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
