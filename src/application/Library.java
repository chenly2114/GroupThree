package com.LibrarySeat;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Library {
	public Library()  throws Exception {
		//初始化每节课的时间
	  	classTime[0][0]=LocalDateTime.of(2019, 1, 1, 8, 0);
    	classTime[0][1]=LocalDateTime.of(2019, 1, 1, 9, 40);
    	classTime[1][0]=LocalDateTime.of(2019, 1, 1, 10, 0);
    	classTime[1][1]=LocalDateTime.of(2019, 1, 1, 11, 40);
    	classTime[2][0]=LocalDateTime.of(2019, 1, 1, 13, 30);
    	classTime[2][1]=LocalDateTime.of(2019, 1, 1, 15, 10);
    	classTime[3][0]=LocalDateTime.of(2019, 1, 1, 15, 30);
    	classTime[3][1]=LocalDateTime.of(2019, 1, 1, 17, 10);
    	classTime[4][0]=LocalDateTime.of(2019, 1, 1, 18, 30);
    	classTime[4][1]=LocalDateTime.of(2019, 1, 1, 20, 10);
    	
    	weeksStartDate=LocalDateTime.of(2019, 3, 4, 0, 0);//学期开始的日期
    	this.maxWeeks=20;
    	
    //	this.schedule.readExcel();
    	
	}
	
	public ArrayList<String> getClassName(){
		return this.schedule.getClassName();
	}
	
	//返回值如果为0 表示为占用状态，如果为正，表示空闲的时间
	public int query(String className,LocalDateTime time) {
		//if(time.getDayOfWeek())
		
		//如果是周六周日则没课
		if(time.getDayOfWeek().getValue()==6||time.getDayOfWeek().getValue()==7){
			return 0;
		}
		
		if(time.isAfter(this.weeksStartDate)==false) {
			return 0;
		}
		
		LocalDateTime onlyTime=LocalDateTime.of(2019,1,1,time.getHour(),time.getMinute());
		
		int week=0;
		
		Duration duration=Duration.between(time,this.weeksStartDate);
		int days=(int)duration.toDays();
		
		week=(days/7)+1;
		
		//大约20周
		if(week>maxWeeks) {
			return 0;
		}
		
		int i=0;
		boolean isFind=false;
		for(i=0;i<5;i++) {
    		if(onlyTime.isAfter(classTime[i][0])&&onlyTime.isBefore(classTime[i][1])) {
    			isFind=true;
    			break;
    		}
		}
		
		//不是上课时间
		if(isFind==false) {
			return 0;
		}
		
		File xlsFile=new File("./timetable/timetable.xls");
		
		boolean isHaveClass=this.schedule.readExcel(week, className, time.getDayOfWeek().getValue(), i+1,xlsFile );
		if(isHaveClass==false) {
			return 0;
		}
		
		Duration d=Duration.between(onlyTime, this.classTime[i][1]);//计算时间
		return (int)d.toMinutes();
	}
	
	private LocalDateTime classTime[][]=new LocalDateTime[5][2];//每节课的上课以及下课时间
	private LocalDateTime weeksStartDate;//学期开始日期
	private int maxWeeks;//学期最大周数
	private Schedule schedule=new Schedule("./timetable/课程总表.xls");
	
	public static void main(String[] args) {	
		
		LocalDateTime time=LocalDateTime.of(2019, 3, 5, 8, 50);
		try {
			Library L=new Library();
			//System.out.println(L.getClassName());
			
			System.out.println(L.query("旅游管理1710106", time));
			
		}catch(Exception e) {
			e.printStackTrace();  
		}
		

		
	    }
	
}
