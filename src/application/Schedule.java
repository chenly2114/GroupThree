package application;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.read.biff.BiffException;


public class Schedule {
	
	public ArrayList<String> className;                  //记录班级的名称
    public String excelPath;                             //课表文件的路径
	
	public Schedule (String excelPath) {
		this.excelPath = excelPath;
	}
		
//	public ArrayList<String> getClassName() {
//		return className;
//	}

	public void setClassName(ArrayList<String> className) {
		this.className = className;
	}

	public String getExcelPath() {
		return excelPath;
	}
	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	
	//读取课程总表.xls文件并创建timtable.xls文件  
    public  void readExcel( ) throws Exception {  	
    	
        //创建文件读取课程总表
        File file1 = new File(Util.EXCEL_PATH);
        Workbook wb1 = Workbook.getWorkbook(file1);
        Sheet sheet1 = wb1.getSheet(0);
        
        //创建文件分周存储课表
        File file2 = new File(Util.TIMETABLE_PATH);
        WritableWorkbook wb2 = Workbook.createWorkbook(file2);

        for (int week = 1; week <= 20; week++) {
            //创建sheet
            @SuppressWarnings("unused")
			WritableSheet sheet = wb2.createSheet("第" + week + "周", week - 1);
        }
        
        WritableSheet[] sheets = wb2.getSheets();//创建sheet数组

        for (int row = 4;row <= sheet1.getRows()-2; row++) {        	
        	String cell1 = sheet1.getCell(0, row).getContents();
            for (int i = 1; i <= 20; i++) {
                sheets[i - 1].addCell(new Label(0, row-4, cell1));
            }
        	
            for (int column = 1; column <= sheet1.getColumns()-1; column++) {
                for (int i = 1; i <= 20; i++) {
                    sheets[i - 1].addCell(new Label(column, row-4, "F"));
                }
                
                String cell = sheet1.getCell(column, row).getContents();//获取总表单元格内容                        
                int firstPosition = -1;//获取周数的firstposition            
                int sdPositon = -1;//单双周sdPosition                                       
                int lastPosition;//获取周数的lastposition
                
                //每周是否有该课程的数组
                boolean weeks[] = new boolean[20];
                for (boolean week : weeks) {
                    week = false;
                }
                
                while (cell.indexOf("[", firstPosition + 1) != -1) {                
                	//带有[的[人]的情况
                    if (cell.indexOf("人]", firstPosition + 1) != -1) {
                        firstPosition = cell.indexOf("人]", firstPosition + 1);
                    }
                                               
                    firstPosition = cell.indexOf("[", firstPosition + 1);
                    lastPosition = cell.indexOf("周", firstPosition + 1);                  
                    sdPositon = cell.indexOf("]", firstPosition + 1);
                    
                    //获取该课程所对应的周数
                    String lesson = cell.substring(firstPosition + 1, lastPosition);
                    String lLesson = cell.substring(lastPosition, sdPositon); 
                    
                    //将课程周数分开存储到数组中
                    String[] wks = lesson.split(",");
                    
                    //将数组拆成双周并且在存在weeks[] 
                    for (String wk : wks) {
                        int centerPosition = wk.indexOf("-");
                        if (centerPosition != -1) {
                            int first = Integer.parseInt(wk.substring(0, centerPosition));
                            int last = Integer.parseInt(wk.substring(centerPosition + 1,wk.length()));
                            if(lLesson.length()<3) {
                                for (int i = first; i <= last; i++) {
                                    weeks[i] = true;   
                                }
                            }else if (lLesson.indexOf("双", 0) != -1) {
                                for (int i = first; i <= last; i++) {
                                    if (i % 2 == 0) {
                                        weeks[i] = true;  
                                    }
                                }
                            } else if (lLesson.indexOf("单", 0) != -1) {
                                for (int i = first; i <= last; i++) {
                                    if (i % 2 != 0) {
                                        weeks[i] = true;
                                    }
                                }
                            }else {
                                for (int i = first; i <= last; i++) {
                                    weeks[i] = true;
                                }
                            }
                        } else {
                            weeks[Integer.parseInt(wk)] = true;
                        }
                    }
                    
                    //修改对应周数的sheet
                    for (int i = 1; i <= 19; i++) {
                        if (weeks[i] == true) {
                            sheets[i - 1].addCell(new Label(column, row-4, "T"));
                        }
                        else {
                        	sheets[i - 1].addCell(new Label(column, row-4, "F"));
                        }
                    }
                    
                    //带有[的[节]的情况
                    if (cell.indexOf("节", firstPosition + 1) != -1) {
                        firstPosition = cell.indexOf("节", firstPosition + 1);
                    } 
                }
            }
        }
        //写入excel
        wb2.write();
        wb1.close();
        wb2.close();
    }
    
    
  //调用课表;weeks表示当前周数 1-20，classId表示当前班级，输入具体的班号 ，week表示星期1-5；lessonTime表示第 1-5节   
    public boolean readExcel(long weeks, String classId,int week, int lessonTime,File file) {  

        try {  
            //读取excel文件
            InputStream is = new FileInputStream(file.getAbsolutePath());  
            
            Workbook wb = Workbook.getWorkbook(is);  
            int time=(week-1)*5+lessonTime;
            //课时不在星期数*课程节数的范围内的情况
            if(time>25||time<1) {
            	boolean hasLesson=false;
            	return hasLesson;
            }
            //周数不在1-20以内的情况
            if(weeks>20||weeks<1) {
            	boolean i=false;
                return i;
            }
           
            boolean flag=false;
            Sheet sheet = wb.getSheet((int) (weeks-1));  
            for (int row = 0;row <= sheet.getRows(); row++) {
            	String cell = sheet.getCell(0, row).getContents();
//            	System.out.print(cell);
            	while (cell.indexOf(classId, 0) != -1) {    
                    String cell1 = sheet.getCell(time, row).getContents();  
//                    System.out.println(cell1);
                    char[] cell2=cell1.toCharArray();
//                    System.out.println(cell2);
                    flag=true; 
                    if(cell2[0]=='T') {
                          return true; 	
                    }
                    return false;//新增
//                    return flag; 
//                    System.out.println(cellout1[0]);
                    
            	}   	
            }
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (BiffException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
		return false;       
    }
    
    //获取所有的班级名称并存入className中
    public ArrayList<String> getClassName(){
    	File file = new File(Util.TIMETABLE_PATH);
    	ArrayList<String> className = new ArrayList<>();
    	try {  
            //读取excel文件
            InputStream is = new FileInputStream(new URI(Schedule.class.getClass().getResource(Util.TIMETABLE_PATH).toString()).getPath());  

            Workbook wb = Workbook.getWorkbook(is);           
            Sheet sheet = wb.getSheet(0);  
            for (int row = 0;row < sheet.getRows(); row++) {
            	String cell = sheet.getCell(0, row).getContents();
            	className.add(cell);
//             	System.out.println(cell); 	
            }
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (BiffException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return className;		
    }
}
