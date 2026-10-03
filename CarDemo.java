class Car {
    String brand;
    String model;
    double price;

    Car(String brand, String model, double price) {
        this.brand = brand;
        this.model = model;
        this.price = price;
    }

    void displayDetails() {
        System.out.println("Brand: " + brand);
        System.out.println("Model: " + model);
        System.out.println("Price: Rs. " + price);
        System.out.println("----------------------");
    }
}

public class CarDemo {
    public static void main(String[] args) {
        Car car1 = new Car("Toyota", "Fortuner", 3500000);
        Car car2 = new Car("Honda", "City", 1500000);
        Car car3 = new Car("Hyundai", "Creta", 1800000);

        car1.displayDetails();
        car2.displayDetails();
        car3.displayDetails();
    }
}
