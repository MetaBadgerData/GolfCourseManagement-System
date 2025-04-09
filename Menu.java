public class Menu {
    private int menu_item_id;
	private String item_name;
	private String category;
	private double price;
	
	public Menu(int menu_item_id, String item_name, String category, double price) {
		super();
		this.menu_item_id = menu_item_id;
		this.item_name = item_name;
		this.category = category;
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	public int getMenu_item_id() {
		return menu_item_id;
	}

	public void setMenu_item_id(int menu_item_id) {
		this.menu_item_id = menu_item_id;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Menu [menu_item_id=" + menu_item_id + ", item_name=" + item_name + ", category=" + category + ", price=" + price
				 + "]";
	}
}
