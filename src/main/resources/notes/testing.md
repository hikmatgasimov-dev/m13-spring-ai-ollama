# Testing

## Mockito basics
@Mock creates a fake dependency and @InjectMocks creates the class under test with those fakes injected. when(...).thenReturn(...) defines the fake behavior and verify(...) checks that a method was called. Use this for fast unit tests without a Spring context.

## MockitoBean
@MockitoBean replaces a bean in the Spring context with a mock. Use it in Spring tests such as @WebMvcTest or @SpringBootTest, when the real Spring wiring is needed but one dependency must be faked.

## MockMvc
MockMvc tests controllers without starting a real server. It sends fake HTTP requests and lets you assert on status, headers and JSON body.