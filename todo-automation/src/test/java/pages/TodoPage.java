package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class TodoPage {
	WebDriver driver;
	WebDriverWait wait;

	public TodoPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	By inputBox = By.className("new-todo");
	By todoListItems = By.cssSelector(".todo-list li");
	By todoCount = By.cssSelector(".todo-count strong");

	public void addTodo(String text) {
		int currentSize = getAllTodos().size();
		driver.findElement(inputBox).sendKeys(text + "\n");
		wait.until(driver -> getAllTodos().size() > currentSize);
	}

	public List<WebElement> getAllTodos() {
		return driver.findElements(todoListItems);
	}

	public int getActiveCount() {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(todoCount));
			return Integer.parseInt(driver.findElement(todoCount).getText());
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Fallback to manual count as .todo-count not found.");
			return (int) getAllTodos().stream().filter(el -> !el.getAttribute("class").contains("completed")).count();
		}
	}

	public void markTodoCompleted(String todoText) {
		WebElement item = findTodoItem(todoText);
		if (item != null) {
			item.findElement(By.className("toggle")).click();
		}
	}

	public void deleteTodo(String todoText) {
		WebElement item = findTodoItem(todoText);
		if (item != null) {
			Actions actions = new Actions(driver);
			actions.moveToElement(item).perform();
			item.findElement(By.className("destroy")).click();
			wait.until(ExpectedConditions.stalenessOf(item));
		}
	}

	public void filterBy(String filter) {
		driver.findElement(By.linkText(filter)).click(); // "All", "Active", "Completed"
	}

	public boolean isTodoPresent(String todoText) {
		return getAllTodos().stream().anyMatch(el -> el.getText().contains(todoText));
	}

	public boolean isTodoCompleted(String todoText) {
		WebElement item = findTodoItem(todoText);
		return item != null && item.getAttribute("class").contains("completed");
	}

	private WebElement findTodoItem(String todoText) {
		return getAllTodos().stream().filter(el -> el.getText().contains(todoText)).findFirst().orElse(null);
	}

	public List<String> getVisibleTodoTexts() {
		return getAllTodos().stream().map(WebElement::getText).collect(Collectors.toList());
	}
}
