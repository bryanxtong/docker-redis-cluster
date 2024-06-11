redis-cluster with bitnami can support the same port and different ports config in containers and they support both ip and hostname

1. docker-compose.yml use all the default port 6379 for all containers 
2. docker-compose-ip.yml  uses different ports in containers and 192.168.71.5 is windows ip/linux ip
3. docker-compose-hostname.yml uses different ports in containers and supports the config with hostname for our local testing and with this setting, the following config needs to be added to C:\Windows\System32\drivers\etc\hosts

127.0.0.1	    redis-node-0
127.0.0.1	    redis-node-1
127.0.0.1	    redis-node-2
127.0.0.1	    redis-node-3
127.0.0.1	    redis-node-4
127.0.0.1	    redis-node-5