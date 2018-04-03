package quoters;

import annotation.DeprecatedClass;

@DeprecatedClass(newImpl = T2000.class)
public class T1000 implements Quoter {

    public void saySomething() {
        System.out.println("Я ЖИДКИЙ");
    }
}
