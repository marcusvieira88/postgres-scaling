# Scaling Postgres

This project has a goal to horizontal scale a Postgres database used in a Spring REST API with Citus extension.

Postgres can be scale vertically and horizontally.

In the vertical scale you increase the machine power (CPU, RAM, Network, etc..), it can be achieved easily in an AWS platform only changing the type of the RDS database instance.

The horizontal scaling is more complex because it involves more than one machine. When you scale horizontally you need to load balance the operations (queries, inserts, updates, etc..) between the machines, this type of scaling is used calling sharding.

At AWS you can reach only the read horizontally scaling increasing the number of read replicas (RDS Postgres has a limitation of max 5 replica instances). To scale the writes you need to use some service like Azure Hiperscale (Citus), it allows you to add many workers (shards) giving you the possibility to distribute your table between the workers, it has a coordinator responsible for load balance the writes between workers.

Also, these tools/services can help to scale Postgres: Pgpool, Postgres-xl, Pgbouncer and Greenplum.

## Project REST API

Create product - POST http://localhost:8080/products/:
```
{
	"description":"Product 1",
	"units":10
}
```

Get product - GET http://localhost:8080/products/1

Update product - PUT http://localhost:8080/products/:
```
{
	"description":"Product 1 Update",
	"units":10
}
```

Delete product - DELETE http://localhost:8080/products/1.

Get products - http://localhost:8080/products:
```
[
    {
        "id": 1,
        "description": "Product 1 Update",
        "units": 10
    }
]
```

## Run Scaling Project

Start Postgres Citus cluster:
```
docker-compose -p citus up
```

Scale citus workers:
```
docker-compose -p citus scale worker=5
```

Connect to citus coordinator:
```
docker exec -it citus_master psql -U postgres
```

List all databases:
```
\l
```

List all tables:
```
\dt
```

Connect to the database:
```
\c postgres 
```

Set the replication factor (number of workers that will keep a copy of table data):
```
SET citus.shard_replication_factor=2;
```

In another terminal start the spring application to create the Product table(spring.jpa.hibernate.ddl-auto=create) into Citus cluster:
```
mvn spring-boot:run
```

Distribute the Product table between workers:
```
SELECT create_distributed_table('public.products', 'id');
```

Now we need to add some data to execute our queries, for it you can use our Project REST API.

After insert some products you can check:
```
-- get distribution column name for products table
SELECT column_to_column_name(logicalrelid, partkey) AS dist_col_name
 FROM pg_dist_partition
 WHERE logicalrelid='products'::regclass;

-- get products table replication factor
SELECT logicalrelid AS tablename,
       count(*)/count(DISTINCT ps.shardid) AS replication_factor
  FROM pg_dist_shard_placement ps
  JOIN pg_dist_shard p ON ps.shardid=p.shardid
  GROUP BY logicalrelid;

-- get size of product table shards
SELECT *
  FROM run_command_on_shards('public.products', $cmd$
    SELECT json_build_object(
      'shard_name', '%1$s',
      'size',       pg_size_pretty(pg_table_size('%1$s'))
    );
  $cmd$);

-- find where some specific item is stored, you can see that we have 5 workers 
-- but the item is only in 2 of them, because our replication factor is 2
SELECT shardid, shardstate, shardlength, nodename, nodeport, placementid
  FROM pg_dist_placement AS placement,
       pg_dist_node AS node
 WHERE placement.groupid = node.groupid
   AND node.noderole = 'primary'
   AND shardid = (
     SELECT get_shard_id_for_distribution_column('products', 1)
   );
```

Shut down Postgres Citus cluster:
```
docker-compose -p citus down
```

### Analyze Query Cost

To analize the cost of distributed query execute the explain command:
```
explain analyze select * from products;
```

## Reference Documentation
Below the technologies used in this project:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/maven-plugin/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/htmlsingle/#using-boot-devtools)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/htmlsingle/#production-ready)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Scalable Postgresql Solution](https://hub.packtpub.com/building-a-scalable-postgresql-solution/)
* [Citus](https://github.com/citusdata/)
* [Scaling your amazon rds instance vertically and horizontally](https://aws.amazon.com/pt/blogs/database/scaling-your-amazon-rds-instance-vertically-and-horizontally/)
* [Pgpool](https://www.pgpool.net/mediawiki/index.php/Main_Page)
* [Postgres-xl](https://www.postgres-xl.org/)
* [Pgbouncer](http://www.pgbouncer.org/usage.html)
* [Greenplum](https://greenplum.org/)
* [Postgres Indexes](https://www.youtube.com/watch?v=clrtT_4WBAw)
