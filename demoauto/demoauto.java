package demoauto;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class demoauto {

    public static void main(String[] args) {
        // Set up WebDriver
        System.setProperty("webdriver.chrome.driver", "C:\\\\chromedriver\\\\chromedriver-win64 (1)\\\\chromedriver-win64\\\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.manage().window().maximize();

            // Log in to OpenEMR
            loginToOpenEMR(driver);

            // Workflow 1: Patient Record Creation
            testPatientRecordCreation(driver);

            // Workflow 2: Search Functionality
            testSearchFunctionality(driver);

            // Workflow 3: Pagination and Sorting
            testPaginationAndSorting(driver);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            // Close the browser
            driver.quit();
        }
    }

    private static void loginToOpenEMR(WebDriver driver) {
        driver.get("https://demo.openemr.io/openemr/interface/login/login.php");

        // Enter login credentials
        driver.findElement(By.id("authUser")).sendKeys("admin");
        driver.findElement(By.id("clearPass")).sendKeys("pass");
        driver.findElement(By.name("languageChoice")).sendKeys("English (Standard)");
        driver.findElement(By.className("btn-login")).click();

        // Validate login success
        if (driver.getTitle().contains("OpenEMR")) {
            System.out.println("Login successful.");
        }
    }

    // Workflow 1: Patient Record Creation
    private static void testPatientRecordCreation(WebDriver driver) {
        // Navigate to Patient/Client -> New/Search
        driver.findElement(By.xpath("//div[text()='Patient/Client']")).click();
        driver.findElement(By.xpath("//div[text()='New/Search']")).click();

        // Test valid data entry
        driver.switchTo().frame("pat");
        driver.findElement(By.id("form_fname")).sendKeys("John");
        driver.findElement(By.id("form_lname")).sendKeys("Doe");
        driver.findElement(By.id("form_DOB")).sendKeys("2000-01-01");
        driver.findElement(By.id("form_sex")).sendKeys("Male");
        driver.findElement(By.id("create")).click();

        // Validate successful creation
        driver.switchTo().defaultContent();
        WebElement successMessage = driver.findElement(By.className("alert-success"));
        if (successMessage.isDisplayed()) {
            System.out.println("Patient record creation with valid data passed.");
        }

        // Test invalid data (e.g., missing last name)
        driver.switchTo().frame("pat");
        driver.findElement(By.id("form_fname")).sendKeys("Jane");
        driver.findElement(By.id("form_lname")).clear();
        driver.findElement(By.id("create")).click();

        // Validate error message
        WebElement errorMessage = driver.findElement(By.xpath("//*[contains(text(),'required')]"));
        if (errorMessage.isDisplayed()) {
            System.out.println("Patient record creation with invalid data passed.");
        }
        driver.switchTo().defaultContent();
    }

    // Workflow 2: Search Functionality
    private static void testSearchFunctionality(WebDriver driver) {
        // Navigate to Patient/Client -> Patients
        driver.findElement(By.xpath("//div[text()='Patient/Client']")).click();
        driver.findElement(By.xpath("//div[text()='Patients']")).click();

        // Test search by valid name
        driver.switchTo().frame("fin");
        driver.findElement(By.id("patient_search")).sendKeys("John");
        driver.findElement(By.id("search")).click();

        // Validate search results
        List<WebElement> searchResults = driver.findElements(By.xpath("//table[@id='pt_table']//tr"));
        if (searchResults.size() > 1) {
            System.out.println("Search with valid name passed.");
        }

        // Test search by invalid name
        driver.findElement(By.id("patient_search")).clear();
        driver.findElement(By.id("patient_search")).sendKeys("Nonexistent");
        driver.findElement(By.id("search")).click();

        // Validate no results
        WebElement noResultsMessage = driver.findElement(By.xpath("//div[contains(text(),'No matching records')]"));
        if (noResultsMessage.isDisplayed()) {
            System.out.println("Search with invalid name passed.");
        }
        driver.switchTo().defaultContent();
    }

    // Workflow 3: Pagination and Sorting
    private static void testPaginationAndSorting(WebDriver driver) {
        // Navigate to Patient/Client -> Patients
        driver.findElement(By.xpath("//div[text()='Patient/Client']")).click();
        driver.findElement(By.xpath("//div[text()='Patients']")).click();

        // Test pagination
        driver.switchTo().frame("fin");
        WebElement nextPageButton = driver.findElement(By.xpath("//a[contains(text(),'Next')]"));
        if (nextPageButton.isDisplayed() && nextPageButton.isEnabled()) {
            nextPageButton.click();
            System.out.println("Pagination test passed.");
        }

        // Test sorting by patient name
        WebElement sortByName = driver.findElement(By.xpath("//th[contains(text(),'Name')]"));
        sortByName.click();

        // Validate sorting
        List<WebElement> sortedNames = driver.findElements(By.xpath("//table[@id='pt_table']//td[2]"));
        boolean isSorted = true;
        for (int i = 0; i < sortedNames.size() - 1; i++) {
            if (sortedNames.get(i).getText().compareTo(sortedNames.get(i + 1).getText()) > 0) {
                isSorted = false;
                break;
            }
        }
        if (isSorted) {
            System.out.println("Sorting test passed.");
        }
        driver.switchTo().defaultContent();
    }
}
