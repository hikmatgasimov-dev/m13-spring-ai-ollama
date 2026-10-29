# JPA and Hibernate

## Entity basics
An entity is a class annotated with @Entity that maps to a database table. It needs an @Id field for the primary key. @GeneratedValue lets the database generate the id.

## Lazy loading
Relationships such as @OneToMany are loaded lazily by default: the related data is fetched only when you access it. Accessing it outside a transaction throws LazyInitializationException. Use a fetch join or an entity graph to load the data in one query.

## The N+1 problem
The N+1 problem happens when one query loads N parents and then N extra queries load their children. It is fixed with JOIN FETCH, @EntityGraph or batch fetching.