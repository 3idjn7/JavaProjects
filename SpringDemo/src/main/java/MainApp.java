import com.example.AnotherBean;
import com.example.MyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
        MyBean myBean = (MyBean) context.getBean("myBean");
        AnotherBean anotherBean = (AnotherBean) context.getBean("anotherBean");
        System.out.println(myBean.getMessage());
        System.out.println(anotherBean.getMessage());
    }
}
