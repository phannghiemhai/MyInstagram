/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author haipn
 */
public class RedisKey {
    public static class Image {
        public static final String listImagesKey = "image.list";
        public static final String imageEntityKey = "image.id.%s";
        public static final String listImagesByUserIdKey = "image.list.by.user.id.%s";
        public static final String imageSeq = "image.seq";
        public static final int DEFAUT_EXPIRE_TIME = 60 * 60 * 24 * 7;
    }
    
    public static class LikeImage {
        
    }
}
