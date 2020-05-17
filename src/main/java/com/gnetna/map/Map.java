package com.gnetna.map;

import javafx.util.Pair;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create by A.T on 2020/5/17
 */
public class Map {
    private static String URL = "http://mt2.google.cn/vt/lyrs=y&hl=zh-CN&gl=cn&x=%d&y=%d&z=%d";
    private static AtomicInteger request = new AtomicInteger(0);
    private static AtomicInteger response = new AtomicInteger(0);

    static public void sleep() {
        while (request.get() == 0 || request.get() != response.get()) {
            try {
                Thread.sleep(500);
                System.out.print(String.format("(%d, %d), ", request.get(), response.get()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static public void load(int x, int y, int z, String image) {
        request.incrementAndGet();
        while (request.get() > (response.get() + 50)) {
            try {
                Thread.sleep(100);
                System.out.print(response.get() + " , ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        WebClient.create().get()
                .uri(String.format(URL, x,y,z))
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(Resource.class)
                .subscribe((resource)-> {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(resource.getInputStream());
                        ImageIO.write(bufferedImage, "png", new File(image));
                        response.incrementAndGet();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    static private void load(int xMin,int xMax, int yMin, int yMax, int z) {
        for (int i = xMin; i <= xMax; i++) {
            File dir = new File(String.format("C:\\Users\\GNETNA\\Desktop\\map\\tiles\\%d\\%d", z, i));
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            for (int j = yMin; j < yMax; j++) {
                load(i, j, z, String.format("%s/%d,png", dir.getPath(), j));
            }
        }
    }

    static public void load(double lon1, double lon2, double lat1, double lat2, int zMin, int zMax) {
        for (int i = zMin; i <= zMax; i++) {
            Pair<Integer, Integer> tiles1 = getTileNumber(lon1, lat1, i);
            Pair<Integer, Integer> tiles2 = getTileNumber(lon2, lat2, i);

            int x1 = tiles1.getKey();
            int x2 = tiles2.getKey();
            int y1 = tiles1.getValue();
            int y2 = tiles2.getValue();

            load(Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2), i);

        }
    }

    static public Pair<Integer, Integer> getTileNumber(final double lon, final double lat, final int zoom) {
        if (zoom < 0 || zoom > 20) {
            throw new RuntimeException("invalid zoom level :" + zoom);
        }

        int n = 1<<zoom;
        int xTile = (int)Math.floor( (lon + 180) / 360 * n ) ;
        int yTile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * n ) ;
        return new Pair<>(xTile, yTile);
    }
}
