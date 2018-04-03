import org.springframework.context.support.ClassPathXmlApplicationContext;
import quoters.Quoter;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        //имплементация этого контекста инициализируется и сканируется
        //XmlBeanDefinitionReader`ом
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("spring_bean.xml");
        //можно вытаскивать по интерфейсу Quoter, а можно по классу TerminatorQuoter
        //но лучше по интерфейсу т.к. хз узнать!!!

//        while (true) {
//            Thread.sleep(1000L);
//            context.getBean(Quoter.class).saySomething();
//        }
        //при ContextListener method saySomething вызывается сам



    }
}
