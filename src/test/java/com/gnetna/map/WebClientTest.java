package com.gnetna.map;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Create by A.T on 2020/5/16
 */
public class WebClientTest {

    @Test
    public void getImageTest() throws Exception {
        WebClient.create().get()
                .uri("http://img01.lianzhong.com/upload/newbbs/2013/03/06/562/128521096509702.png")
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToMono(Resource.class)
                .subscribe((resource)-> {
                    try {
                        System.out.println("1 ------------------------ "+System.currentTimeMillis());
                        BufferedImage bufferedImage = ImageIO.read(resource.getInputStream());
                        ImageIO.write(bufferedImage, "png", new File("captcha.png"));
                        System.out.println("3 ------------------------ "+System.currentTimeMillis());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("2 ------------------------ "+System.currentTimeMillis());
        Thread.sleep(10000);
    }

    @Test
    public void mapTest() {
        double lon = 105.5108;
        double lat = 30.2358;
        for (int z = 10; z <= 18; z++) {
            Pair<Integer, Integer> tiles = Map.getTileNumber(lon, lat, z);
            System.out.println(String.format("zoom = %d, x = %d, y = %d", z, tiles.getKey(), tiles.getValue()));
        }
    }

    @Test
    public void loadTest() {
        Map.load(105.5108, 105.9285, 30.2358, 30.40, 12, 16);
        Map.sleep();
    }
}
