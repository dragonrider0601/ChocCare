import java.io.*;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Provider directory
 * Created by Sam on 11/5/2016.
 */
public class ProviderDirectory {
    //Replaced classes with their interface (HashMap -> Map, TreeMap -> NavigableMap) to maximize code flexibility --Griffin
    private Map<Integer, Service> directory; //HashMap is used when looking up a service by 6 digit code
    private NavigableMap<String, Service> alphabetical; //TreeMap is used to alphabetically store the services.

    //Default constructor
    public ProviderDirectory(){
        //Added <> to eliminate unchecked warning --Griffin
        directory=new HashMap<>();
        alphabetical=new TreeMap<>();
    }

    //Reads provider directory from services.xml.
    public ProviderDirectory(File f) throws IOException {
        //Added <> to eliminate unchecked warning --Griffin
        directory=new HashMap<>();
        alphabetical=new TreeMap<>();
        if(f.exists()) {

            //Reads data from the file if the file isn't empty
            BufferedReader br = new BufferedReader(new FileReader(f));
            if (br.readLine() != null) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document xml = builder.parse(f);
                    NodeList serviceNodes = xml.getElementsByTagName("Service");
                    NodeList nameNodes = xml.getElementsByTagName("name");
                    NodeList codeNodes = xml.getElementsByTagName("code");
                    NodeList feeNodes = xml.getElementsByTagName("fee");
                    NodeList statusNodes = xml.getElementsByTagName("status");
                    for (int i = 0; i < serviceNodes.getLength(); i++) {
                        Service s = new Service(nameNodes.item(i).getTextContent(),
                                Integer.parseInt(codeNodes.item(i).getTextContent()),
                                Integer.parseInt(feeNodes.item(i).getTextContent()),
                                Boolean.parseBoolean(statusNodes.item(i).getTextContent()));
                        addService(s);
                    }
                } catch (ParserConfigurationException | IOException | SAXException e) {
                    throw new RuntimeException("Could not read provider directory data");
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid data in file");
                }
            }
        }

        //creates services.xml if it doesn't already exist
        else{
            f.createNewFile();
        }
    }

    //looks up a service by code and displays it.
    public void display(int code){
        if(directory.containsKey(code)){
            directory.get(code).display();
        }
        else{
            System.out.println("Error: Service not found with code " + code);
        }
    }

    //looks up service by name and displays it.
    public void displayByName(String name){
        if(alphabetical.containsKey(name)){
            alphabetical.get(name).display();
        }
        else{
            System.out.println("Error: Service not found with code " + name);
        }
    }

    //displays services in alphabetical if target is System.out; exports alphabetical list to file if target is a file.
    public void displayAll(PrintStream target){
        for(Map.Entry<String, Service> item : alphabetical.entrySet()){
            target.println("Service name: " + item.getValue().getName());
            target.println("Service code: " + item.getValue().getCode());
            target.println("Fee: $" + item.getValue().getFee()/100 + "." + item.getValue().getFee()%100);
            if(item.getValue().getIsActive()){
                target.println("Status: Active");
            }
            else{
                target.println("Status: Inactive");
            }
            target.println();
        }
    }

    //In class Service generator that adds the generated service.
    public void addService(){
        Scanner reader=new Scanner(System.in);
        Service s;

        //Name entry
        String name;
        do {
            System.out.println("Enter service name.");
            name = reader.nextLine();
        }while(name.equals(""));

        //Code entry
        String code;
        boolean codeIsValid;
        int finalCode=0;
        do{
            //get appropriate code length
            do{
                System.out.println("Enter service code.");
                code = reader.nextLine();
                if(code.length()>6){
                    System.out.print("Input is too large. ");
                }
            }while(code.equals("")||code.length()>6);
            codeIsValid=true;

            //check that all characters are digits
            for(int i=0; i<code.length(); i++) {
                if (!isDigit(code.charAt(i))) {
                    codeIsValid = false;
                }
            }

            //check that code is between 100000 and 999999, and that it doesn't match an existing code
            if(codeIsValid){
                finalCode = Integer.parseInt(code);
                if(finalCode<100000||finalCode>999999){
                    codeIsValid=false;
                    System.out.print("Not a valid 6-digit code, must be between 100000 and 999999. ");
                }
                if(directory.containsKey(finalCode)){
                    codeIsValid=false;
                    System.out.print("There is already a service with code " + finalCode + ". ");
                }
            }
        }while(codeIsValid==false);

        //Fee entry
        String fee;
        boolean feeIsValid;
        int finalFee=0;
        do {

            //checks for appropriate fee length
            do {
                System.out.println("Enter service fee.");
                fee = reader.nextLine();
                if(fee.length()>7){
                    System.out.print("Input is too large. ");
                }
            } while (fee.equals("")||fee.length()>7);
            if(fee.charAt(0)=='$'){
                fee=fee.substring(1);
            }
            feeIsValid=true;

            //checks for a single decimal point, any more and it's not a valid fee. Checks that all other characters are digits
            boolean hasDecimal = false;
            for(int i=0; i<fee.length(); i++){
                if(fee.charAt(i)=='.'&&hasDecimal==true){
                    feeIsValid=false;
                }
                if(fee.charAt(i)=='.'&&hasDecimal==false){
                    hasDecimal=true;
                }

                if(!isDigit(fee.charAt(i))&&fee.charAt(i)!='.'){
                    feeIsValid=false;
                }
            }
            //converts to int if it's a double, checks that it's between 0 and 99999
            if(feeIsValid){
                if(hasDecimal){
                    finalFee=(int)(Double.parseDouble(fee)*100);
                }
                else{
                    finalFee=Integer.parseInt(fee);
                }
                if(finalFee<0){
                    feeIsValid=false;
                    System.out.print("Fee cannot be negative. ");
                }
                if(finalFee>99999){
                    feeIsValid=false;
                    System.out.println("Fee cannot be greater than $999.99.");
                }
            }
        }while(feeIsValid==false);

        //creates the service and adds it to the data structures
        s=new Service(name, finalCode, finalFee, true);
        directory.put(s.getCode(), s);
        alphabetical.put(s.getName(), s);
    }

    //Adds newService to the directory and alphabetical.
    public void addService(Service newService){
        if(directory.containsKey(newService.getCode())){
            System.out.println("Error: There is already a service with code " +newService.getCode() +".");
        }
        else if(newService.getName().equals("")){
            System.out.println("Error: Service has no name.");
        }
        else if(newService.getCode()<100000){
            System.out.println("Error: Code too low. Must be between 100000 and 999999.");
        }
        else if(newService.getCode()>999999){
            System.out.println("Error: Code too high. Must be between 100000 and 999999.");
        }
        else if(newService.getFee()<0){
            System.out.println("Error: Fee cannot be negative. Must be between $0.00 and $999.99");
        }
        else if(newService.getFee()>99999){
            System.out.println("Error: Fee too high. Must be between $0.00 and $999.99");
        }
        else{
            directory.put(newService.getCode(), newService);
            alphabetical.put(newService.getName(), newService);
        }
    }

    //toggles status of service based on code.
    public void toggleActive(int code){
        if (directory.containsKey(code)) {
            directory.get(code).toggleActive();
            if(directory.get(code).getIsActive()){
                System.out.println(directory.get(code).getName()+" is now active");
            }
            else{
                System.out.println(directory.get(code).getName()+" is now inactive");
            }
        }
        else{
            System.out.println("No service found with code " + code);
        }
    }

    //Added by Griffin
    //gets fee of service based on code.
    public int getFee(int code){
        if (isActive(code)) {
            return directory.get(code).getFee();
        } else {
            throw new NoSuchElementException("No service found with code " + code);
        }
    }

    //Added by Griffin
    //Returns true if there is a service with that code and it is marked active
    public boolean isActive(int code) {
        return isValid(code) && directory.get(code).getIsActive();
    }

    //Added by Griffin
    //Returns true if there is a service with that code
    public boolean isValid(int code) {
        return directory.containsKey(code);
    }

    //updates fee of service based on code.
    public void updateFee(int code, int newFee){
        if(directory.containsKey(code)){
            directory.get(code).setFee(newFee);
            System.out.println(directory.get(code).getName()+ " now costs $" + newFee/100 + "." + newFee%100);
        }
        else{
            System.out.println("Error: No service found with code " + code);
        }
    }

    //save function wrapper
    public void save(File out){
        try{
            if(!out.exists()){
                out.createNewFile();
            }
            PrintStream output=new PrintStream(out);
            save(output);
        } catch(IOException e){
            throw new RuntimeException("Error: couldn't save Provider Directory");
        }
    }

    //saves directory to services.xml
    public void save(PrintStream out) throws IOException{
        out.println("<ProviderDirectory>");
        for(Map.Entry<String, Service> item : alphabetical.entrySet()) {
            out.println("  <Service>");
            out.println("    <name>" + item.getValue().getName() + "</name>");
            out.println("    <code>" + item.getValue().getCode() + "</code>");
            out.println("    <fee>" + item.getValue().getFee() + "</fee>");
            if (item.getValue().getIsActive()) {
                out.println("    <status>true</status>");
            } else {
                out.println("    <status>false</status>");
            }
            out.println("  </Service>");
        }
        out.println("</ProviderDirectory>");
        out.close();
    }

    private boolean isDigit(char c){
        if(c=='0'||c=='1'||c=='2'||c=='3'||c=='4'||c=='5'||c=='6'||c=='7'||c=='8'||c=='9'){
            return true;
        }
        else return false;
    }
}
