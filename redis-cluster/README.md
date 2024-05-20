Redis cluster which supports external access by java apps.

In bridge mode, The following setting needs to be done and REDIS_CLUSTER_ANNOUNCE_IP is the docker host ip(window ip/linux ip/wsl ip)

      - 'REDIS_CLUSTER_ANNOUNCE_IP=192.168.71.4'
      - 'REDIS_CLUSTER_ANNOUNCE_PORT=6384'
      - 'REDIS_CLUSTER_ANNOUNCE_BUS_PORT=16384'