# Spring Beans

## Bean scopes
A Spring bean is a singleton by default: the container creates one instance and shares it everywhere. The prototype scope creates a new instance each time the bean is requested. Web scopes such as request and session tie the bean to an HTTP request or session.

## Dependency injection
Dependency injection means a class receives its collaborators instead of creating them itself. Constructor injection is preferred because the dependencies are final and the class is easy to test. Field injection with @Autowired hides dependencies and is harder to test.

## Component stereotypes
@Component is the generic stereotype for a Spring-managed class. @Service marks business logic, @Repository marks data access and translates persistence exceptions, and @Controller or @RestController marks web endpoints.