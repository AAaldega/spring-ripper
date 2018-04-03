import org.springframework.context.support.ClassPathXmlApplicationContext;
import quoters.Quoter;
import quoters.T1000;

public class MainBFPP {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring_bean.xml").getBean(Quoter.class).saySomething();

    }
}
