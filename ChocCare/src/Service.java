/**
 * Created by Sam on 11/5/2016.
 */
public class Service {
    private String name; //Name of service
    private int code; //6 digit code
    private int fee; //fee of service, in cents. Displayed as proper dollar amount with display function.
    private boolean isActive; //status is active or inactive.

    public Service(String name, int code, int fee, boolean isActive){ //Argument constructor
        this.name=name;
        this.code=code;
        this.fee=fee;
        this.isActive=isActive;
    }

    public String getName(){ //returns name of service
        return this.name;
    }

    public int getCode(){ //returns service code
        return this.code;
    }

    public int getFee(){ //returns fee
        return this.fee;
    }

    public void setFee(int newFee){ //sets fee
        this.fee=newFee;
    }

    public boolean getIsActive(){ //gets active status
        return this.isActive;
    }

    public void toggleActive(){ //toggles active status
        this.isActive=!this.isActive;
    }

    public void display(){ //displays service name, code, fee in dollar amount, and active status.
        System.out.println("Service: " + this.name);
        System.out.println("Code: " + this.code);
        System.out.println("Fee: $" + this.fee/100 + "." + this.fee%100);
        if(this.isActive){
            System.out.println("Status: Active");
        }
        else{
            System.out.println("Status: Inactive");
        }
    }
}
