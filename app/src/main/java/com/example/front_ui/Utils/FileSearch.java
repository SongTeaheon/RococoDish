package com.example.front_ui.Utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileSearch {


    //dirctory 내부의 디렉토리 리스트를 리턴
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFiles = file.listFiles();
        if(listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    pathArray.add(listFiles[i].getAbsolutePath());
                }
            }
        }
        return pathArray;
    }

    //dirctory 내부의 파일 리스트를 리턴

    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);

        //원하는 파일만 가져오기 위해 FileFilter정의 : 이미지 파일만 포함시킨다!
        File[] listFiles = file.listFiles(new FileFilter() {

            String strImgExt = "jpg|jpeg|png|gif|bmp"; //허용할 이미지타입

            @Override
            public boolean accept(File pathname) {

                //System.out.println(pathname);
                boolean chkResult = false;
                if(pathname.isFile()) {
                    String ext = pathname.getName().substring(pathname.getName().lastIndexOf(".")+1);
                    chkResult = strImgExt.contains(ext.toLowerCase());
                }
                return chkResult;
            }
        });


        //시간별로 파일정렬
        listFiles = sortFileList(listFiles);

        //pathArray에 파일 경로를 저장한다.
        if(listFiles != null) {
            for (int i = 0; i < listFiles.length; i++) {
                pathArray.add(listFiles[i].getAbsolutePath());
            }
        }
        return pathArray;

    }

    //파일을 시간별로 정렬
    static public File[] sortFileList(File[] files)
    {

        Arrays.sort(files,
                new Comparator<Object>()
                {
                    @Override
                    public int compare(Object object1, Object object2) {

                        String s1 = "";
                        String s2 = "";


                        s1 = ((File)object1).lastModified()+"";
                        s2 = ((File)object2).lastModified()+"";



                        return s2.compareTo(s1);

                    }
                });

        return files;
    }



}
