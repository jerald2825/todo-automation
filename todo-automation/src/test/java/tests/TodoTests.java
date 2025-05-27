package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.TodoPage;

import java.time.Duration;
import java.util.List;

public class TodoTests {
	WebDriver driver;
	TodoPage todoPage;
	String baseUrl = "https://todomvc.com/examples/react/dist/";

	@BeforeClass
	public void setUp() {
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		driver.manage().window().maximize();
		driver.get(baseUrl);
		todoPage = new TodoPage(driver);
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}

	@Test(priority = 1)
	public void TS01_addTodosAndVerify() {
		todoPage.addTodo("Buy groceries");
		todoPage.addTodo("Clean the house");
		todoPage.addTodo("Pay bills");

		List<String> todos = todoPage.getVisibleTodoTexts();
		Assert.assertTrue(todos.contains("Buy groceries"));
		Assert.assertTrue(todos.contains("Clean the house"));
		Assert.assertTrue(todos.contains("Pay bills"));
		Assert.assertEquals(todoPage.getActiveCount(), 3);
	}

	@Test(priority = 2)
	public void TS02_markCompletedAndVerify() {
		todoPage.markTodoCompleted("Buy groceries");
		todoPage.markTodoCompleted("Pay bills");

		Assert.assertTrue(todoPage.isTodoCompleted("Buy groceries"));
		Assert.assertTrue(todoPage.isTodoCompleted("Pay bills"));
		Assert.assertEquals(todoPage.getActiveCount(), 1); // "Clean the house"
	}

	@Test(priority = 3)
	public void TS03_deleteItemAndVerify() {
		todoPage.deleteTodo("Clean the house");
		Assert.assertFalse(todoPage.isTodoPresent("Clean the house"));
	}

	@Test(priority = 4)
	public void TS04_filteringTodos() {
		todoPage.filterBy("Active");
		List<String> activeTodos = todoPage.getVisibleTodoTexts();
		Assert.assertFalse(activeTodos.contains("Buy groceries"));
		Assert.assertFalse(activeTodos.contains("Pay bills"));

		todoPage.filterBy("Completed");
		List<String> completedTodos = todoPage.getVisibleTodoTexts();
		Assert.assertTrue(completedTodos.contains("Buy groceries"));
		Assert.assertTrue(completedTodos.contains("Pay bills"));

		todoPage.filterBy("All");
	}
}
