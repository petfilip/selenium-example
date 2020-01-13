package cz.filip.rukovoditel.selenium;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.sql.Driver;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TS_Userlogin {
    private ChromeDriver driver;

//help
//založit soubor, kde mohou být proměnné
    //Vytvořit if statement - if (element is visible) then....
    //Pády aplikace na jednom assertu, na clicknutí na link xfilp12 (projekt)
    //iFrame v test2
    //počítání v tablce v test3
    //názvy TC
    //



//

    @Before
    public void init() {
        ChromeOptions cho = new ChromeOptions();
       // driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        boolean runOnTravis = true;
        if (runOnTravis) {
            cho.addArguments("headless");
        } else {
            System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        }
//        ChromeDriverService service = new ChromeDriverService()
        driver = new ChromeDriver(cho);
//        driver.manage().window().maximize();

    }

    @After
    public void tearDown() {
       driver.close();
    }

    @Test
    public void openBrovser(){
        WebDriverWait wait = new WebDriverWait(driver, 15);
        //otevře stránku na loginpage
        driver.get("https://digitalnizena.cz/rukovoditel/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        driver.manage().window().maximize();

    }

    @Test
    public void should_login(){
        // Given
        openBrovser();

        //vyplnění name+password
        WebElement nameColumn = driver.findElement(By.name("username"));
        nameColumn.sendKeys("rukovoditel");
        WebElement passColumn = driver.findElement(By.name("password"));
        passColumn.sendKeys("vse456ru");

        //potvrzení formuláře
        //WebElement submit = driver.findElement(By.className("btn  btn-primary"));
        passColumn.submit();

        //then
        //WebElement logedUser = driver.findElement(By.xpath("/html/body/div[1]/div/ul/li[2]/a/span"));
        WebElement logedUser = driver.findElement(By.xpath("//span[@class='username']"));
        Assert.assertEquals("System Administrator", logedUser.getText());
    }

    @Test
    public void shouldNotLogIn(){
        // Given
        openBrovser();

        //vyplnění správného loginu
        WebElement nameColumn = driver.findElement(By.name("username"));
        nameColumn.sendKeys("rukovoditel");
        //vyplnění zlého hesla
        WebElement passColumn = driver.findElement(By.name("password"));
        passColumn.sendKeys("vse456ruxxx");
        //potvrzení formuláře
        passColumn.submit();

        //then

        Assert.assertTrue(driver.getPageSource().contains("No match for Username and/or Password."));
    }


    @Test
    public void  should_logOff(){
        // Given
        //uživatel je přihlášen
        should_login();

        //When
        WebElement dropDown = driver.findElement(By.xpath("/html/body/div[1]/div/ul/li[2]/a/i"));
        new Actions(driver).moveToElement(dropDown).perform();
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@class='dropdown user open']//a[@class='dropdown-toggle']")));

        WebElement logOffBtn = driver.findElement(By.xpath("//a[contains(text(),'Logoff')]"));
        logOffBtn.click();
        //Then
        Assert.assertTrue(driver.getPageSource().contains("Password forgotten?"));

    }

    @Test
    public void prechodNaProjekty(){
        // Given
        should_login();

        //Přejít na kartu projekty
        WebElement project = driver.findElement(By.linkText("Projects"));
        project.click();
    }

    @Test
    public void projectWithoutName(){
        // Given
        WebDriverWait wait = new WebDriverWait(driver, 15);
        //uživatel je přhlášen na kartě projekty
        prechodNaProjekty();


        //vyčistit filter
        WebElement filter = driver.findElement(By.id("entity_items_listing66_21_search_keywords"));
        filter.clear();
        //seřadit dle jména
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"slimScroll\"]/table/thead/tr/th[5]")));
        WebElement name = driver.findElementByXPath("//*[@id=\"slimScroll\"]/table/thead/tr/th[5]");

        //Vybere text ve sloupci name prvního řádku
        WebElement nameSloupec = driver.findElement(By.xpath("//*[@id=\"slimScroll\"]/table/tbody/tr[1]/td[5]"));

        //then
        //
        Assert.assertNotNull(nameSloupec);

        //seřazení z druhé strany abecedy a nová kontrola
        name.click();
        Assert.assertNotNull(nameSloupec);
    }

    @Test
    public void createProject_findProject_deleteProject(){
        // Given
        // Uživatlel je přihlášen a na kartě projekty
        WebDriverWait wait = new WebDriverWait(driver, 3);
        //Before
        prechodNaProjekty();

        //when
        //založení nového projektu
        WebElement addProject = driver.findElementByXPath("//button[contains(.,'Add Project')]");
        addProject.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-title")));
        //priority high

        Select dropdown = new Select(driver.findElement(By.id("fields_156")));
        dropdown.selectByVisibleText("High");



        //name random
        WebElement projectName = driver.findElementById("fields_158");
        String uuid = UUID.randomUUID().toString();
        projectName.sendKeys("Petr " + uuid);
        //potvrzení
        projectName.submit();
        //kontrola založeného
        Assert.assertTrue(driver.getPageSource().contains("Petr " + uuid));
        Assert.assertTrue(driver.getPageSource().contains("Tasks"));
        //přechod na projekty a filtrování nového
        WebElement project = driver.findElement(By.linkText("Projects"));
        project.click();
        WebElement filtr = driver.findElement(By.id("entity_items_listing66_21_search_keywords"));
        filtr.clear();
        filtr.sendKeys(uuid);
        //filtr.sendKeys("\"Petr " + uuid + "\"");
        filtr.submit();
        //kontrola nově založeného
        String lokator = "//a[contains(text()," + uuid ;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), '" + uuid + "')]")));
        WebElement tableName = driver.findElement(By.xpath("//a[contains(text(), '" + uuid + "')]"));

        // nevím proč 2x za sebou vrátí jiné výsledky

         Assert.assertEquals("Petr " + uuid, tableName.getText());

        //zrušení
        //kliknutí na delete
        //CSS lokator funguje 1/3 případů...
        //najitPockatClicknoutCSS(driver, ".fieldtype_action .fa-trash");
        najitPockatClicknoutXpath(driver, "//*[@id=\"slimScroll\"]/table/tbody/tr/td[2]/a[1]/i");
        //najitPockatClicknoutCSS(driver, ".table .fa-trash-o");

        //potvrzení checkboxu

        WebElement deleteCheckBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("uniform-delete_confirm")));
        deleteCheckBox.click();

        deleteCheckBox.submit();
        //Assert.assertEquals(tableName.getText(), "Petr " + uuid);
        WebElement filtr2 = driver.findElement(By.id("entity_items_listing66_21_search_keywords"));
        filtr2.clear();
        filtr2.sendKeys("\"Petr " + uuid + "\"");
        filtr2.submit();

        driver.getPageSource().contains("No Records Found");

    }

    @Test
    public void createTsk_validateTask_deleteTask(){
        // Given
        WebDriverWait wait = new WebDriverWait(driver, 3);
        //Mám projekt s názvem "xfilp nemazat"
        prechodNaProjekty();
        //existuje projekt na který přejdu

        WebElement filtr = driver.findElement(By.id("entity_items_listing66_21_search_keywords"));
        filtr.clear();
        filtr.sendKeys("\"xfilp nemazat\"");
        filtr.submit();

        //obcas to nalezne projekt, selenium provede kliknuti ale na strance se fakticky neotevře projekt.
        // Následně pokračuje test dále a snaží se místo nového tasku přidat nový projekt
        najitPockatClicknoutXpath(driver, "//a[contains(text(),'xfilp nemazat')]");

        //New Task will be created with type Task, name, status New, prio Medium and some description.

         //addtask.click();
        najitPockatClicknoutXpath(driver, "//button[contains(@class,'btn btn-primary')]");
        //počká na zobrazení tabulky
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tab-content")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='fields_168']")));
        WebElement nameImput = driver.findElement(By.xpath("//input[@id='fields_168']"));
        int cisloTasku = ((int)(Math.random()*11)+20);

        //String uuid = UUID.randomUUID().toString();
        String name = "Jmeno tasku" + cisloTasku;
        nameImput.sendKeys(name);
        //WebElement descriptionImput = driver.findElement(By.className("cke_wysiwyg_frame cke_reset"));
        //descriptionImput.sendKeys("Popisek");

        //práce s iframe description
        driver.switchTo().frame(0);
        WebElement descriptionImput = driver.findElement(By.xpath("/html/body"));
        descriptionImput.sendKeys("Popisek");
        driver.switchTo().parentFrame();
        najitPockatClicknoutCSS(driver, ".modal-footer .btn-primary");

        // Verify task attributes (Type Task, description, name, priority, status) on task info page (icon i).
        najitPockatClicknoutXpath(driver, "//a[contains(@title, 'Info')]");

        WebElement TypeValue = driver.findElement(By.xpath("//*[@class=\"form-group-167\"]/td"));
        Assert.assertEquals("Task", TypeValue.getText());

        WebElement Description = driver.findElement(By.xpath("//div[@class='content_box_content fieldtype_textarea_wysiwyg']"));
        Assert.assertEquals("Popisek", Description.getText());

        WebElement Caption = driver.findElement(By.xpath("//div[@class='caption']"));
        //Assert.assertEquals(name, Caption.getText());

        WebElement Priority = driver.findElement(By.xpath("//*[@class=\"form-group-170\"]/td"));
        Assert.assertEquals("Medium", Priority.getText());

        WebElement Status = driver.findElement(By.xpath("//*[@class=\"form-group-169\"]/td"));
        Assert.assertEquals("New", Status.getText());


        // Delete that task.    //button[@class='btn btn-default btn-sm dropdown-toggle']
        WebElement moreActions = driver.findElement(By.xpath("//button[@class='btn btn-default btn-sm dropdown-toggle']"));
        new Actions(driver).moveToElement(moreActions).perform();

        najitPockatClicknoutXpath(driver, "//a[contains(text(),'Delete')]");

        najitPockatClicknoutXpath(driver, "//button[@class='btn btn-primary btn-primary-modal-action']");
        //Then
        Assert.assertTrue(driver.getPageSource().contains("No Records Found"));
    }

    public static void najitPockatClicknoutXpath (WebDriver driver, String locateValue){
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locateValue)));
        WebElement element = driver.findElement(By.xpath(locateValue));
        element.click();
    }

    public static void najitPockatClicknoutID (WebDriver driver, String locateValue){
        WebDriverWait wait = new WebDriverWait(driver, 3);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(locateValue)));
        WebElement element = driver.findElement(By.id(locateValue));
        element.click();
    }

    public static void najitPockatClicknoutClass (WebDriver driver, String locateValue){
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(ExpectedConditions.elementToBeClickable(By.className(locateValue)));
        WebElement element = driver.findElement(By.className(locateValue));
        element.click();
    }

    public static void najitPockatClicknoutCSS (WebDriver driver, String locateValue){

        WebDriverWait wait = new WebDriverWait(driver, 3);
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(locateValue)));
        WebElement element = driver.findElement(By.cssSelector(locateValue));
        element.click();
    }

    public static void zalozeniTasku(WebDriver driver, String nazev, String status){
        // Given
        // jsem na určitém projeku na základní stránce
        WebDriverWait wait = new WebDriverWait(driver, 3);
        WebElement addtask = driver.findElement(By.cssSelector("body > div.page-container > div.page-content-wrapper > div > div > div.row > div > div:nth-child(7) > div:nth-child(1) > div > button"));
        addtask.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("tab-content")));
        WebElement nameImput = driver.findElement(By.id("fields_168"));
        int cisloTasku = ((int)(Math.random()*11)+20);
        nameImput.sendKeys(nazev + cisloTasku);
        Select dropdown = new Select(driver.findElement(By.id("fields_169")));
        dropdown.selectByVisibleText(status);
        nameImput.submit();
        //cekani na tabulku
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("slimScroll")));


    }


    @Test
    public void createTasks_countsTasks_deleteTasks() {
        // Given
        //TC: (Precondition - there exists project yourname already in the system.)
        //existuje projekt xfilp12 na kterém není žádný task

        //Přejdu na vytvořený projekt do jeho detailu - projekt "xfilp12"
        WebDriverWait wait = new WebDriverWait(driver, 3);
        prechodNaProjekty();
        WebElement filtr = driver.findElement(By.id("entity_items_listing66_21_search_keywords"));
        filtr.clear();
        filtr.sendKeys("\"xfilp12\"");
        filtr.submit();


       //tady to z nějakého důvodu projde jen někdy....
        najitPockatClicknoutXpath(driver, "//a[contains(text(),'xfilp12')]");
        // Create new 7 tasks with different statuses New, Open, Waiting, Done, Closed, Paid, Canceled.
        // Založení tasku
        zalozeniTasku(driver, "New task", "New");
        zalozeniTasku(driver, "Open task", "Open");
        zalozeniTasku(driver, "Waiting task", "Waiting");
        zalozeniTasku(driver, "Done task", "Done");
        zalozeniTasku(driver, "Closed task", "Closed");
        zalozeniTasku(driver, "Paid task", "Paid");
        zalozeniTasku(driver, "Canceled task", "Canceled");

        // Verify that using default filter (New, Open, Waiting) only 3 tasks will be shown.
        ////button[@class='btn dropdown-toggle btn-users-filters']//i[@class='fa fa-angle-down']
        WebElement moreFilters = driver.findElement(By.xpath("//button[@class='btn dropdown-toggle btn-users-filters']//i[@class='fa fa-angle-down']"));
        new Actions(driver).moveToElement(moreFilters).perform();
        najitPockatClicknoutXpath(driver, "//a[contains(text(),'Default Filters')]");

        //List<WebElement> rows = driver.findElements(By.cssSelector("#slimScroll > table > tbody > tr"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#slimScroll > table")));
        int count = driver.findElements(By.cssSelector("#slimScroll > table > tbody > tr")).size();
        Assert.assertEquals(3, count);
        //do defaltního flteru spadají 3 založené tasky

        // Change applied filter in Filter info dialog to only contain (New, Waiting) ...
        //
        //odstranění filtru
        najitPockatClicknoutXpath(driver, "//i[@title='Remove Filter']");



        //vybrání přednastaveného filteru NewAndWaiting
        WebElement moreFilters2 = driver.findElement(By.xpath("//button[@class='btn dropdown-toggle btn-users-filters']//i[@class='fa fa-angle-down']"));
        new Actions(driver).moveToElement(moreFilters2).perform();
        najitPockatClicknoutXpath(driver, "//a[contains(text(),'NewAndWaiting')]");

        //kontrola počtu
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#slimScroll > table")));
        int count2 = driver.findElements(By.cssSelector("#slimScroll > table > tbody > tr")).size();
        Assert.assertEquals(2, count2);

        //Najetí kurzoru na elemment
         // there are more ways how to do it (you can click small x on Open "label" to delete it, or you can deal with

        // writing into "suggestion box").
        //
        // Verify only New and Waiting tasks are displayed.
        //
        // Now remove all filters and verify all created tasks are displayed.
        //

        // Delete all tasks using Select all and batch delete.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"slimScroll\"]/table/tbody/tr")));

        WebElement chekboxAll = driver.findElement(By.id("select_all_items"));
        chekboxAll.click();

        //Najetí kurzoru na tlačítko with selected + smazání
        WebElement moreActions = driver.findElement(By.xpath("//button[contains(text(),'With Selected')]"));
        new Actions(driver).moveToElement(moreActions).perform();



        //najitPockatClicknoutClass(driver, " link-to-modalbox");
        najitPockatClicknoutXpath(driver, "//div[contains(@class,'btn-group open')]//a[contains(@class,'link-to-modalbox')][contains(text(),'Delete')]");


        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class,'btn btn-primary btn-primary-modal-action')]")));
        WebElement deleteSelected = driver.findElement(By.xpath("//button[contains(@class,'btn btn-primary btn-primary-modal-action')]"));
        deleteSelected.click();

        //Then
        //na stráne je text s No Records Found - všechny tasky byly smazány

        Assert.assertTrue(driver.getPageSource().contains("No Records Found"));
    }

}
