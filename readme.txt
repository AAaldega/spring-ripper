конспект лекции "спринг-потрошитель" Борисова.

как всё работает:
Мы пишем "наши классы".
в xml прописываем бины.
При запуске апп приходит XmlBeanDefinitionReader считывает с xml все декларации бинов
и кладёт их в мапу BeanDefinitions<id бина, декларация бина(все ньюансы, прописанные в xml)>
После того, как BeanDefinitions созданы, BeanFactory начинает по ним работать,
делает из "наших классов" объекты, настраивает их согласно beanDefinitions,
и кладёт в IoC контейнер(хэш мапа)

"сноска"
по умолчанию создаются и попадают в Ioc контейнер только синглетоны
prototype создаются только по требованию. Это важно, поскольку когда контекст закрывается,
у бинов находящихся в контейнере (только синглетоны) вызывается destroy метод, у прототайп
не вызываются

BeanPostProcessor:
- позволяет настраивать бины до того как они попали в контейнер (chain of responsobility)
(пример @Autowired), поднимается как раз с помощью AutowiredAnnotationBeanPostProcessor
в коде есть кастомный BeanPostProcessor для аннотации @InjectRandomInt.
он при запуске приложения инжектит случайное число в поле repeat
- у интерфейса 2 метода :

    //выполняется до init метода
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

		return bean;
	}

	//выполняется после init метода
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

- между методами вызывается init() метод:
1)@PostConstruct (аннотации)
2)init-method  (в xml)

ВОПРОС: зачем init method?
ответ: двухфазовый конструктор

как работает: BeanDefinitionReader парсит BeanDefinitions
BeanFactory сначала обрабатывает спринговские BeanDefinitions
 создавая BPP и другие кишки спринга (чтобы с их помощью потом создавать наши объекты)
Далее BeanFactory создаёт и подготавливает наши бины с помощью BPP и прочих приблуд
объект (будущий бин) передаётся по порядку каждому BPP вызываются postProcessBeforeInitialization,
 далее вызывается init метод далее опять проходится по всем BPP и вызывается postProcessAfterInitialization

На выходе готовый бин.

Вопрос: зачем 2 прохода по BPP?
ответ: объект засовывается в проксю, которая добавляет доп функциональность (транзакции и тд)
1)если бы был один проход по BPP до init метода и мы бы в этом проходе делали проксю, тогда init
вызывался бы у прокси, а не у исходного класса. postProcessBeforeInitialization расчитывает,
что у класса будет метадата (аннотации), но если к нему прилетит уже прокси(у прокси уже нет метадаты)
то логика не отработает, поэтому BPP разбит пополам. поэтому те BPP которые меняют класс крупно
должны это делать на этапе  postProcessAfterInitialization
2) init метод всегда работает на оригинальный класс а не на проксю

Когда создаётся прокси, никто не должен заметить, что объект подменен.
тк контракт не должен быть изменен. Есть 2 варианта генерации класса (прокси на лету):
- наследоваться от оригинального класса и переопределять его методы (cglib) //хуже
- имплементивароть те же самые интерфейсы (dynamic proxies)
spring aop смотрит, есть ли у класса интерфейсы то идёт через dynamic proxies
иначе через cglib

--------------------------------------------------------------------------------
Компонент ApplicationListener
-ContextStartedEvent
-ContextStopedEvent
-ContextRefreshEvent
-ContextClosedEvent

из любого ивента можно вытащить контекст
и прикреплять при этом какую-нибудь логику пример PostProxyInvokerContextListener
------------------------------------------------------------------------------
BeanFactoryPostProcessor

- позволяет настраивать бин дифинишны, до того, как создаются бины
(пример: замена пропертей в бинах xml на конкретные значения)
- этот интерфейт имеет один метод
    postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)

- этот метод запустится на этапе, когда другие бины ещё не созданы и есть только BeanDefinitions
и сам BeanFactory
------------------------------------------------------------------------------
сканирование пакетов
вариант 1) <context: component-scan base-package="com"/>
вариант 2) new AnnotationConfigApplicationContext("com")

как сканируются пакеты:
ClassPathBeanDefinitionScanner берёт пакет и ищет все бины, аннотированые @Component
далее он создаёт дополнительные BeanDefinitions из всех классов, над которыми стоит
@Component, или другую аннотацию, аннотированную @Component (@Repository, @Service, @Controller)

ClassPathBeanDefinitionScanner не является BeanPostProcessor-ом , ни BeanFactoryPostProcessor-ом
- он ResourceLoaderAware.


преимущества xml перед javaConfig:
можно держать как внешний ресурс и не нужно перекомпилировать, если поправили параметр в xml
преимущество javaConfig:
при настройке бинов можно запускать методы
------------------------------------------------------------------------------

Java Config

- new AnnotationConfigApplicationContext(JavaConfig.class)
- Казалось бы, его должен парсировать, какой-нибудь BeanDefinitionReader как у XML (XmlBeanDefinitionReader)
- И даже его класс называется схоже: AnnotatedBeanDefinitionReader
- но нет, annotatedBeanDefinitionReader вообще ничего не имплементирует
- он просто является частью ApplicationContext-a (находится внутри него)
- он только регистрирует все JavaConfig-и

пример:
@Configuration
@ComponentScan("root")
public class JavaConfig {
    @Bean
    public CoolDao dao() {
        return new CoolDaoImpl();
    }

    @Bean(initMethod = "init")
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CoolService coolService() {
        CoolServiceImlp service = new CoolServiceImpl();
        servicel.setDao(dao());
        return service;
    }
}


Кто обрабатывает JavaConfig

- есть ConfigurationClassPostProcessor (особый BeanFactoryPostProcessor) - он не меняет
готовые BeanDefinitions а добавляет к ним новые бины из JavaConfig
- его регистрирует AnnotationConfigApplicationContext
- он создаёт бин-дифинишны по @Bean
- а также относится к:
  - @Import
  - @ImportResource
  - @ComponentScan (опять ClassPathBeanDefinitionScanner)
