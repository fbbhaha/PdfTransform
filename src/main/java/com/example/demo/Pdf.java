package com.example.demo;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class Pdf {

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public Map<String,Object> PdfTransform(@RequestParam("file") MultipartFile file){
        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()){
            result.put("code",500);
            result.put("data",null);
            result.put("msg","该文件是空文件");
            return result;
        }
        String filename = file.getOriginalFilename();
        String filePath = "E://UploadFile/";
        deleteFile(filePath+filename);
        File newFile = new File(filePath+filename);
        try {
            file.transferTo(newFile);
            result.put("code",200);
            result.put("data",filePath+filename);
            result.put("msg","上传成功");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code",500);
            result.put("data",null);
            result.put("msg","IO异常");
            return result;
        }
    }

    /**
     * 文件下载
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/download")
    public String downloadFile(HttpServletRequest request,
                               HttpServletResponse response) throws UnsupportedEncodingException {

        String filePath = request.getParameter("path");
        int index = filePath.lastIndexOf(".pdf");
        String newPath = filePath.substring(0,index)+".docx";
        PdfDocument pdf = new PdfDocument(filePath);
        pdf.saveToFile(newPath, FileFormat.DOCX);

        File file = new File(newPath);
        // 如果文件名存在，则进行下载
        if (file.exists()) {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));

            // 实现文件下载
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("Download the song successfully!");
            }
            catch (Exception e) {
                System.out.println("Download the song failed!");
            }
            finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }



    /**
     *删除路径下的所有文件
     * @param path 需要遍历的路径
     * @return 路径下文件的名称集合
     */
    public ArrayList<String> getFile(String path){
        // 获得指定文件对象
        File file = new File(path);
        // 获得该文件夹内的所有文件
        File[] array = file.listFiles();
        ArrayList<String> list = new ArrayList<String>();
        int n = 0;
        for(int i = 0; i< Objects.requireNonNull(array).length; i++)
        {
            if(array[i].isFile())//如果是文件
            {
                // 只输出文件名字
                list.add(array[i].getName());
            }
        }
        return list;
    }

    /**
     * 删除单个文档
     * @param fileName 文件名
     * @return Boolean
     */
    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }



}
