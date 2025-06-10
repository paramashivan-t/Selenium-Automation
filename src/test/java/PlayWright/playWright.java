package PlayWright;

import com.microsoft.playwright.*;

public class playWright {
    public static void main(String[] args) {

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium()
                    .launch(new BrowserType.LaunchOptions().setChannel("msedge").setHeadless(false));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));
            //Page page = context.newPage();

            page.navigate("http://demoqa.com/automation-practice-form/");
            System.out.println("Page navigated");
            System.out.println("Page title: " + page.title());

            browser.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}