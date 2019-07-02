package com.zkhc.mysqlDif.controller;

import com.alibaba.fastjson.JSON;
import com.zkhc.mysqlDif.service.DifService;
import com.zkhc.mysqlDif.util.DateTimeUtil;
import com.zkhc.mysqlDif.util.ExportList;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/mysqlDif")
public class MysqlDifController {
    public final Logger log = Logger.getLogger(this.getClass());
    private static Properties prop = new Properties();
    private String CLASSFORNAME = "com.mysql.jdbc.Driver"; // 驱动类
    private Connection conn;
    private ArrayList<Hashtable<String, String>> diffTabList;
    private ArrayList<Hashtable<String, String>> diffColList;
    private ArrayList<Hashtable<String, String>> diffIndexList;
    private ArrayList<Hashtable<String, String>> diffRoutineList;

    @RequestMapping("/info")
    public Object getDifInfo(Model model){
        try {
            // 开始获取数据库对象查询信息
            System.out.println("1. 开始获取数据库对象查询信息" + (new Date()).toLocaleString());
            DifService.prepareAllDiffInfo();

            System.out.println("2. 表差异信息 " + (new Date()).toLocaleString());
            // 表差异信息 {TABLE_NAME1,TABLE_NAME2,TABLE_COMMENT}
            diffTabList =  DifService.getDiffTab();

            System.out.println("3. 列差异信息  " + (new Date()).toLocaleString());
            // 列差异信息 {TABLE_NAME,COLUMN_NAME1,COLUMN_NAME2,COLUMN_TYPE,IS_NULLABLE,COLUMN_KEY,EXTRA,COLUMN_COMMENT,COLUMN_DEFAULT}
            diffColList =  DifService.getDiffCol();

            System.out.println("4. 索引差异信息  " + (new Date()).toLocaleString());
            // 索引差异信息 {TABLE_NAME,INDEX_NAME1,INDEX_NAME2,COLUMNS}
            diffIndexList =  DifService.getDiffIndex();

            System.out.println("5. 函数、存储过程差异信息  " + (new Date()).toLocaleString());
            // 函数、存储过程差异信息 {ROUTINE_NAME1,ROUTINE_NAME2,ROUTINE_TYPE}
            diffRoutineList =  DifService.getDiffRoutine();

            model.addAttribute("diffTabList",JSON.toJSON(diffTabList));
            model.addAttribute("diffColList",JSON.toJSON(diffColList));
            model.addAttribute("diffIndexList",JSON.toJSON(diffIndexList));
            model.addAttribute("diffRoutineList",JSON.toJSON(diffRoutineList));
        }
        catch (Exception e) {
            e.printStackTrace();
        }finally{
            // 关闭连接
            DifService.getDb1().closeConnection();
            DifService.getDb2().closeConnection();
            DifService.getDb3().closeConnection();
        }
        return "mysqlDif.html";
    }

    @RequestMapping("/testConnect")
    @ResponseBody
    public Object testConnect(@RequestParam String url, @RequestParam String name, @RequestParam String pwd){
        String result="";
        try {
            Class.forName(CLASSFORNAME);
            conn = DriverManager.getConnection(url, name, pwd);
            if(conn!=null) {
                result="连接成功";
                log.debug("连接成功！"+url);
            }
            conn.close();
        } catch (ClassNotFoundException ex) {
            result = "驱动类没有找到!";
            log.error(url + "驱动类没有找到:" + ex);
        } catch (SQLException ex) {
            result = "连接异常："+ex.getMessage();
            log.error(url + "连接异常" + ex);
        }catch(Exception ex){
            result = "连接异常："+ex.getMessage();
            log.error(url + "连接异常" + ex);
        }
        return result;
    }

    @RequestMapping("/loadJDBC")
    @ResponseBody
    public Object loadJDBC(@RequestParam String param) {
        Map<String,Object> result=new HashMap<>();
        try {
            JSONArray array = JSONArray.fromObject(param);
            JSONObject obj = array.getJSONObject(0);
            Map<String, String> map = jsonToMap(obj);
            prop.load(MysqlDifController.class.getClassLoader().getResourceAsStream("jdbc.properties"));

            prop.setProperty("jdbc1.URL", map.get("url1"));
            prop.setProperty("jdbc1.SCHEMA", map.get("url1").substring(map.get("url1").lastIndexOf("/")+1));
            prop.setProperty("jdbc1.UID", map.get("name1"));
            prop.setProperty("jdbc1.PWD", map.get("pwd1"));

            prop.setProperty("jdbc2.URL", map.get("url2"));
            prop.setProperty("jdbc2.SCHEMA", map.get("url2").substring(map.get("url2").lastIndexOf("/")+1));
            prop.setProperty("jdbc2.UID", map.get("name2"));
            prop.setProperty("jdbc2.PWD", map.get("pwd2"));

            String path=MysqlDifController.class.getClassLoader().getResource("jdbc.properties").getPath();
            FileOutputStream fos = new FileOutputStream(path);
            prop.store(fos,"");
            result.put("code","200");
            result.put("message","操作成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 导出Excel
     */
    @RequestMapping("/export")
    public void exportDifList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String fileName = "数据库差异比对结果"+DateTimeUtil.dateToStr(new Date(),"yyyy-mm-dd")+".xls";
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        Workbook wb=new ExportList().exportDo(diffTabList, diffColList, diffIndexList, diffRoutineList);
        FileOutputStream fileOut = new FileOutputStream(file);
        wb.write(fileOut);
        fileOut.close();
        try {
        File xlsFile = new File(new File(".").getCanonicalPath()+"\\"+fileName);
        fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");//把文件名按UTF-8取出并按ISO8859-1编码，保证弹出窗口中的文件名中文不乱码，中文不要太多，最多支持17个中文，因为header有150个字节限制。
        response.setContentType("application/octet-stream");//告诉浏览器输出内容为流
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);//Content-Disposition中指定的类型是文件的扩展名，并且弹出的下载对话框中的文件类型图片是按照文件的扩展名显示的，点保存后，文件以filename的值命名，保存类型以Content中设置的为准。注意：在设置Content-Disposition头字段之前，一定要设置Content-Type头字段。
        String len = String.valueOf(xlsFile.length());
        response.setHeader("Content-Length", len);//设置内容长度
        OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(xlsFile);
        byte[] b = new byte[1024];
        int n;
        while ((n = in.read(b)) != -1) {
            out.write(b, 0, n);
        }
        in.close();
        out.close();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("6. 导出比对结果文件  " + (new Date()).toLocaleString());
    }

    /**
     * JSONObject转Map
     * @param jsonObject
     * @return
     */
    public static Map<String, String> jsonToMap(JSONObject jsonObject) {
        Map<String, String> result = new HashMap<String, String>();
        Iterator<String> iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
        }
        return result;
    }
}
