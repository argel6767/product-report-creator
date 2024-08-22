package tactical.blue.excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelRow {
    private String itemName; //name of item
    private String manufacturer; //maker of item
    private String sku; //item sku
    private Integer quantityRequested; //how much of item customer wants
    private Integer quantityNeeded; //how much is actually needed to be bought, depened on packaging
    private String packaging; //how item is sold -- each, box, etc
    private Double msrp; //Manufacturers Suggested Retail Price 
    private Double wholeSalePrice; //cost for a single item
    private Double costOfGoods; //  Cost of Goods Sold – qty x wholesale (usually just qty * wholesale)
    private final Double MARKUP = 1.30; //markup of customers 
    private Double unitPrice; //price per item customer will pay
    private Double extendedPrice; //entiry quantity of item customer will pay
    private String source; //website item information was acquired from
    private String productURL; //url of product page

    /*
     * Can be used by Boundtree
     */
    public ExcelRow(String itemName, String manufacturer, String sku, int quantityRequested, String packaging, double msrp, double wholeSalePrice, String productURL) {
        this.itemName = itemName;
        this.manufacturer = manufacturer;
        this.sku = sku;
        this.packaging = packaging;
        this.quantityRequested = quantityRequested;
        this.msrp = msrp;
        this.wholeSalePrice = wholeSalePrice;
        this.productURL = productURL;
        calculatePricingAndQuantities();
        determineSourceUsingProductURL();
    }

    //If Data is not preformatted before object is constructed
    public ExcelRow(String itemName, String manufacturer, String sku, String quantityRequested, String packaging, String msrp, String wholeSalePrice, String productURL) {
        this.itemName = itemName;
        this.manufacturer = manufacturer;
        this.sku = sku;
        this.packaging = packaging;
        this.quantityRequested = Integer.valueOf(quantityRequested);
        this.msrp = Double.valueOf(msrp);
        this.wholeSalePrice = Double.valueOf(wholeSalePrice);
        this.productURL = productURL;
        calculatePricingAndQuantities(); 
        determineSourceUsingProductURL();
    }


    public ExcelRow(String itemName, int quantityRequested, String packaging, double msrp, double wholeSalePrice, String productURL) {
        this.itemName = itemName;
        this.quantityRequested = quantityRequested;
        this.packaging = packaging;
        this.msrp = msrp;
        this.wholeSalePrice = wholeSalePrice;
        this.productURL = productURL;
        calculatePricingAndQuantities();
    }
    

    /*
     * Must be used for HenrySchein data as manufacturer and SKU are tied to the same element on the web page
     */

    //
    private void calculatePricingAndQuantities() {
        calculateQuantityNeeded();
        calculateCostOfGoods();
        calculateUnitPrice();
        calculateExtendedPrice();
    }

    /*
     * grab the number value of the packaging variable to be able to determine how much
     * of much of an item needs to be bought then save it to
     * quantity needed
     */
    private void calculateQuantityNeeded() {
        //create Pattern and matcher objects to find the digits in the in String packaging 
        Pattern pattern = Pattern.compile("(\\d+)?\\s*(.*)"); //look for the number and its packing method
        Matcher matcher = pattern.matcher(this.packaging.toLowerCase());

        
        if (matcher.matches()) { //check if there is any matches to regex in packaging string
        String numberPartOfPackaging = matcher.group(1);
        String textPartOfPackaging = matcher.group(2);
        double extractedPackagingVal = 0;

        
        if (numberPartOfPackaging != null && !numberPartOfPackaging.isEmpty()) { 
            extractedPackagingVal = Double.parseDouble(numberPartOfPackaging);
        } 
        else if (textPartOfPackaging.contains("each") || textPartOfPackaging.isEmpty() || numberPartOfPackaging == null) { //checking if its just each, ie packaged singular
            extractedPackagingVal = 1;
        }

        double quantityNeededToBuy = Math.ceil(this.quantityRequested/extractedPackagingVal); //round up as cannot will need to buy additional box/case if it theres remaineder
        setQuantityNeeded((int)quantityNeededToBuy);
        }
        else System.out.print("No Matches Found");
    }

    //find the source of the item bought using the url of the product page
    private void determineSourceUsingProductURL() {
        String urlLowerCase = this.productURL.toLowerCase(); // Convert to lowercase for easier matching
        
        if (urlLowerCase.contains("boundtree") || urlLowerCase.contains("boundtree.com")) {
            this.source = "Boundtree";
        } else if (urlLowerCase.contains("henryschein") || urlLowerCase.contains("henryschein.com")) {
            this.source = "Henry Schein";
        } else if (urlLowerCase.contains("narescue") || urlLowerCase.contains("north american rescue")) {
            this.source = "North American Rescue";
        } else if (urlLowerCase.contains("dynarex") || urlLowerCase.contains("dynarex.com")) {
            this.source = "Dynarex";
        } else if (urlLowerCase.contains("medco-athletics") || urlLowerCase.contains("medco sports medicine")) {
            this.source = "Medco Sports Medicine";
        } else if (urlLowerCase.contains("login required")) {
            this.source = "Login Required";
        } else {
            this.source = "Unknown";
        }
    }

    /*
     * calculating values costOfGoods, unitPrice, and extendedPrice
     */
    private void calculateCostOfGoods() {
        Double price = this.quantityNeeded * this.wholeSalePrice;
        setCostOfGoods(price);
    }

    private void calculateUnitPrice() {
        Double price = this.wholeSalePrice * this.MARKUP;
        setUnitPrice(price);
    }

    private void calculateExtendedPrice() {
        Double price = this.unitPrice * quantityNeeded;
        setExtendedPrice(price);
    }


    //getters and setters
    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSku() {
        return this.sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantityRequested() {
        return this.quantityRequested;
    }

    public void setQuantityRequested(Integer quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    public Integer getQuantityNeeded() {
        return this.quantityNeeded;
    }

    public void setQuantityNeeded(Integer quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }

    public String getPackaging() {
        return this.packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public Double getMsrp() {
        return this.msrp;
    }


    public Double getWholeSalePrice() {
        return this.wholeSalePrice;
    }


    public Double getCostOfGoods() {
        return this.costOfGoods;
    }

    private void setCostOfGoods(Double costOfGoods) {
        this.costOfGoods = costOfGoods;
    }

    public Double getMARKUP() {
        return this.MARKUP;
    }


    public Double getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getExtendedPrice() {
        return this.extendedPrice;
    }

    public void setExtendedPrice(Double extendedPrice) {
        this.extendedPrice = extendedPrice;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProductURL() {
        return this.productURL;
    }

    public void setProductURL(String productURL) {
        this.productURL = productURL;
    }

    

    public ExcelRow(){}

    public void setitemName(String itemName) {
        this.itemName = itemName;
    }
    
    public void setSKU(String sku) {
        this.sku = sku;
    }
    
    public void setQuantity(int quantity) {
        this.quantityNeeded = quantity;
    }
    
    public void setMsrp(double msrp) {
        this.msrp = msrp;
    }
    
    public void setWholeSalePrice(double wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }

    @Override
    public String toString() {
        return this.itemName + ", " + this.sku + ", " + this.quantityNeeded + ", " + this.msrp + ", " + this.wholeSalePrice + ", " + this.productURL;
    }

    public Object[] toArray() {
        return new Object[]{itemName, sku, quantityNeeded, msrp, wholeSalePrice, productURL};
    }
}

