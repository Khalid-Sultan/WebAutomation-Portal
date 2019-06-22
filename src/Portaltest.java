/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author KhalidSultan
 */
public class Portaltest {

    static String username = "ATR/8444/09";
    static String password = "PASSWORD_GOES_HERE";
    static String url = "https://portal.aait.edu.et/";
    static String loggedInUrl = "https://portal.aait.edu.et/Home";
    static String gradeUrl = "https://portal.aait.edu.et/Grade/GradeReport";

    static String[] possibleValues = {
        "Course Number",
        "Course Title",
        "Course Code",
        "Course CreditHour",
        "Course ECTS",
        "Course Grade"
    };
    
    static String breakpoint = "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
    static String endBreakpoint = "===================================================================================================================================================================================================================================";

    public static WebDriver initializer() {
        System.setProperty("webdriver.gecko.driver", "C:\\Gecko\\geckodriver.exe");
        // Create a new instance of the FireFox driver
        WebDriver driver = new FirefoxDriver();
        return driver;
    }

    public static void inputEmailAndPassword(WebDriver driver) {
        //Input Email
        driver.findElement(By.id("UserName")).click();
        driver.findElement(By.id("UserName")).sendKeys(username);
        //Input Password
        driver.findElement(By.id("Password")).click();
        driver.findElement(By.id("Password")).sendKeys(password);
        //Submit Form
        driver.findElement(By.className("btn-success")).click();
    } 
    public static void main(String[] args) throws InterruptedException {

        System.out.println("URL is opened in window.");
        WebDriver driver = initializer();
        driver.get(url);
        inputEmailAndPassword(driver);
        Thread.sleep(20);
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.equalsIgnoreCase(loggedInUrl)) {
            System.out.println("Logged In SuccessFully");
            driver.navigate().to(gradeUrl);
            if (driver.getCurrentUrl().equalsIgnoreCase(gradeUrl)) {
                PrintWriter fw = null;
                try {
                    fw = new PrintWriter("GradeReports.txt", "UTF-8");
                    fw.println("YOUR GRADE REPORT IS AS FOLLOWS.\n");
                    WebElement gradeReports = driver.findElement(By.xpath("//*[@class='table table-bordered table-striped table-hover']"));
                    List<WebElement> grades = gradeReports.findElements(By.xpath("..//tbody/tr"));                    
                    StringBuilder builder;
                    int index;
                    for (index = 0; index<grades.size();index++) {
                        if(index==0) continue; 
                        WebElement grade = grades.get(index);
                        builder = new StringBuilder();
                        if("yrsm".equalsIgnoreCase(grade.getAttribute("class"))) {
                            String semesterInformation = grade.getText();
                            if(semesterInformation.contains("Academic Year")){
                                builder.append("Semester Information : \t").append(grade.getText()).append("\n");                                
                                builder.append(breakpoint);
                            }
                            else{
                                builder.append("\tOverall Semester Information : \n");
                                builder.append(grade.getText()).append("\n");
                                builder.append(endBreakpoint).append("\n").append("\n");
                            } 
                        }
                        else{ 
                            builder.append("\tCourse Information : \n");         
                            List<WebElement> gradeInfos = grade.findElements(By.xpath(".//td"));   
                            int i = 0;
                            for(int j = 0; j< gradeInfos.size();j++){
                                WebElement gradeInfo = gradeInfos.get(j);
                                if(gradeInfos.indexOf(gradeInfo)<=possibleValues.length-1){
                                    builder.append("\t\t").append(possibleValues[i]).append(":\t").append(gradeInfo.getText()).append("\n");
                                    i++;
                                    continue;
                                }
                                break;
                            } 
                            builder.append(breakpoint);
                        }
                        fw.println(builder.toString()); 
                    }

                    driver.close();
                    System.out.println("Browser is closed.");
                    return;
                } 
                catch (IOException ex) {
                    Logger.getLogger(Portaltest.class.getName()).log(Level.SEVERE, null, ex);
                } 
                finally {
                    fw.close();
                    System.out.println("File is closed.");
                }
            }
            System.out.println("Failed to Get Grades Page.");
            driver.close();
            System.out.println("Browser is closed.");
        }
        System.out.println("Failed to Log In.");
        driver.close();
        System.out.println("Browser is closed.");

    }

}
