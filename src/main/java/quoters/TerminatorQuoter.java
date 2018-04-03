package quoters;

import annotation.InjectRandomInt;
import annotation.PostProxy;
import annotation.Profiling;

import javax.annotation.PostConstruct;

@Profiling
public class TerminatorQuoter implements Quoter {

    @InjectRandomInt(min = 2, max = 10)
    private int repeat;

    private String message;

    @PostConstruct
    public void init() {
        System.out.println("Phase 2, repeat = " + repeat);
    }

    public TerminatorQuoter() {
        System.out.println("Phase 1, repeat = " + repeat);
    }

    //если XML config, требуются сеттеры ко всем бинам находящимся в классе (если без аннотаций)
    //также без сеттеров в идея не будет подсвечивать проперти при написания бина в xml
    public void setMessage(String message) {
        this.message = message;
    }

    @PostProxy //все метод будет запускаться сам после того, как всё уже настроено и все proxy сгенерированы
    //это может делать ApplicationListener
    public void saySomething() {
        System.out.println("Phase 3");
        for (int i = 0; i < repeat; i++) {
            System.out.println("Message = " + message);
        }
    }
}
