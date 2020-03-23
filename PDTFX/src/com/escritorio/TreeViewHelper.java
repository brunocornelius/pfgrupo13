package com.escritorio;

import java.util.ArrayList;
import javafx.scene.control.TreeItem;
 
public class TreeViewHelper 
{
    public TreeViewHelper()
    {
    }
     
    // This method creates an ArrayList of TreeItems (Products)
    public ArrayList<TreeItem<String>> getProducts()
    {
        ArrayList<TreeItem<String>> products = new ArrayList<TreeItem<String>>();
         
        TreeItem cars = new TreeItem("Cars");
        cars.getChildren().addAll(getCars());
         
        TreeItem buses = new TreeItem("Buses");
        buses.getChildren().addAll(getBuses());
 
        TreeItem trucks = new TreeItem("Trucks");
        trucks.getChildren().addAll(getTrucks());
         
        TreeItem motorbikes = new TreeItem("Motorcycles");
        motorbikes.getChildren().addAll(getMotorcycles());
         
        products.add(cars);
        products.add(buses);
        products.add(trucks);
        products.add(motorbikes);
         
        return products;
    }
 
    // This method creates an ArrayList of TreeItems (Cars)
    private ArrayList<TreeItem<String>> getCars()
    {
        ArrayList<TreeItem<String>> cars = new ArrayList<TreeItem<String>>();
         
        TreeItem ferrari = new TreeItem("Ferrari");
        TreeItem porsche = new TreeItem("Porsche");
        TreeItem ford = new TreeItem("Ford");
        TreeItem mercedes = new TreeItem("Mercedes");
         
        cars.add(ferrari);
        cars.add(porsche);
        cars.add(ford);
        cars.add(mercedes);
         
        return cars;        
    }
 
    // This method creates an ArrayList of TreeItems (Buses)
    private ArrayList<TreeItem<String>> getBuses()
    {
        ArrayList<TreeItem<String>> buses = new ArrayList<TreeItem<String>>();
         
        TreeItem gm = new TreeItem("GM");
        TreeItem vw = new TreeItem("VW");
        TreeItem man = new TreeItem("MAN");
        TreeItem volvo = new TreeItem("Volvo");
         
        buses.add(gm);
        buses.add(man);
        buses.add(volvo);
        buses.add(vw);
         
        return buses;       
    }
     
    // This method creates an ArrayList of TreeItems (Trucks)
    private ArrayList<TreeItem<String>> getTrucks()
    {
        ArrayList<TreeItem<String>> trucks = new ArrayList<TreeItem<String>>();
         
        TreeItem scania = new TreeItem("Scania");
        TreeItem mercedes = new TreeItem("Mercedes");
        TreeItem gm = new TreeItem("GM");
        TreeItem ford = new TreeItem("Ford");
         
        trucks.add(mercedes);
        trucks.add(scania);
        trucks.add(gm);
        trucks.add(ford);
         
        return trucks;
    }
 
    // This method creates an ArrayList of TreeItems (Motorbikes)
    private ArrayList<TreeItem<String>> getMotorcycles()
    {
        ArrayList<TreeItem<String>> motorcycles = new ArrayList<TreeItem<String>>();
         
        TreeItem harley = new TreeItem("Harley");
        TreeItem suzuki = new TreeItem("Suzuki");
        TreeItem ktm = new TreeItem("KTM");
        TreeItem honda = new TreeItem("Honda");
         
        motorcycles.add(harley);
        motorcycles.add(honda);
        motorcycles.add(ktm);
        motorcycles.add(suzuki);
         
        return motorcycles;
    }
}
