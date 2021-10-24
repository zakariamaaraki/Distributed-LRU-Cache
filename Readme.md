[![Build and Test](https://github.com/zakariamaaraki/Distributed-LRU-Cache/actions/workflows/build.yml/badge.svg)](https://github.com/zakariamaaraki/Distributed-LRU-Cache/actions/workflows/build.yml) [![Docker](https://badgen.net/badge/icon/docker?icon=docker&label)](https://https://docker.com/) [![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/)

# Distributed Cache

Implementation of a distributed caching solution (**LRU** : Least recently used) using **ZooKeeper**. 

![Alt text](./zcache-archi.png?raw=true "ZCache Architecture")

## Getting Started
Build the docker image by running the following command :
```
docker build image . -t zcache
```

Then run the container by running the following command :

```
docker container run -p 9999:9999 -e ZOOKEEPER_HOST=zookeeper_url -e NODE_ID=1 -e CACHE_CAPACITY=1000000 zcache
```

#### Note that :
* Each instance must have a unique **NODE_ID** !! 
* If the environment variable **CACHE_CAPACITY** is not mentioned the default value would be 1000000 
* If you are running Zookeeper locally using docker, do not forget to use the same network

```
--network your_zookeeper_network
```

## Quorum
To avoid split-brain problem it's essential to avoid multiple leaders getting elected. So an odd number of servers allows ZooKeeper to perform majority for leadership. If 2n + 1 is the number of servers, at any time there can be up to n failed servers and ZooKeeper cluster will keep quorum.  

## ZooKeeper

### Leader Election
Doing leader election with ZooKeeper is very simple. A simple way of doing that is to use the **sequence & ephemeral** flags when creating znodes that represent **proposals** of clients. The idea is to have a znode, say **/cache-election**, such that each znode creates a child znode **cache-election/p-0000X** With both flags **sequence** and **ephemeral**. \
With the sequence flag, ZooKeeper automatically appends a sequence number that is greater than any one previously appended to a child of **/cache-election**. The instance that created the znode with the smallest appended sequence number is the leader.

### Service Discovery
At the same time, we're using ZooKeeper as Service Discovery using Spring Cloud Zookeeper that leverages this extension for service registration and discovery.

### Consistency vs Availability
Note that according to the **[CAP theorem](https://en.wikipedia.org/wiki/CAP_theorem)**, ZooKeeper is a **CP** system. This implies that it sacrifices availability in order to achieve consistency and partition tolerance. In other words, if it cannot guarantee correct behaviour it will not respond to queries. \
Using a Service Discovery with a **CP** system is not a good idea, so it might be better if we use **Eureka** (which is an **AP** system) as a service discovery/registry instead of **ZooKeeper** (to guarantee availability) but in this case we'll need to set up another cluster.

## Endpoints

### CRUD operations
![Alt text](./zcache-api.png?raw=true "ZCache API")

### Health check API
![Alt text](./zcache-health-check.png?raw=true "ZCache API")

## Guarantees
* **Scalable** scale out easily together with increasing number of requests and data.
* **Highly Available** Survives hardware/network failures (Relication + Leader Election)
* **Highly Performant** fast puts, gets and fast delete.

## Author

- **Zakaria Maaraki** - _Initial work_ - [zakariamaaraki](https://github.com/zakariamaaraki)
