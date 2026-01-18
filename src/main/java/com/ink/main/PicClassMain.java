package com.ink.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 当前类用于给指定文件夹下的文件按照后缀名划分到各自后缀名下的文件夹
 */
public class PicClassMain {

    private static final String folder = "D:\\doc\\2026\\1\\红博苑营地";


    public static void main(String[] args) {
        classifyRecursively(new File(folder));
    }

    /**
     * 递归处理目录
     */
    public static void classifyRecursively(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        boolean hasSubDir = false;
        for (File file : files) {
            if (file.isDirectory()) {
                hasSubDir = true;
                classifyRecursively(file);
            }
        }

        // 如果当前目录下没有子目录，只包含文件，则进行分类
        if (!hasSubDir) {
            classifyFilesInDir(dir);
        }
    }

    /**
     * 对当前目录下的文件按后缀分类
     */
    private static void classifyFilesInDir(File dir) {
        List<File> files = FileUtil.loopFiles(dir, File::isFile);

        if (files.isEmpty()) {
            return;
        }

        // 提取所有文件后缀（统一转大写）
        Set<String> suffixSet = files.stream()
                .map(FileUtil::extName)
                .map(ext -> StrUtil.isBlank(ext) ? "" : ext.toUpperCase())
                .collect(Collectors.toSet());

        // 如果所有文件后缀一致且不为空，则不进行任何操作
        if (suffixSet.size() == 1 && !suffixSet.contains("")) {
            return;
        }

        // 否则进行分类
        for (File file : files) {
            String ext = FileUtil.extName(file);
            String folderName;

            if (StrUtil.isBlank(ext)) {
                folderName = "NO_SUFFIX";
            } else {
                folderName = ext.toUpperCase();
            }

            File targetDir = new File(dir, folderName);
            FileUtil.mkdir(targetDir);

            File targetFile = new File(targetDir, file.getName());
            try {
                FileUtil.move(file, targetFile, true);
            } catch (IORuntimeException e) {
                System.out.println("文件移动出现异常，将跳过处理，" + file.getAbsolutePath());
            }
        }
    }
}
