# HttpServer
Simple HTTP server for static content return. The server is based on [Netty](https://netty.io/).

### Usage
```
make
./run.sh
```
### Performance
Test machine: Intel Core i3 2350M, DDR3 1333 MHz 8 GB, HDD  

RPS (Requests per page) on [Wikipedia Russia](https://en.wikipedia.org/wiki/Russia) page
* [Nginx](https://nginx.org) - 4241 rpc
* LarionovServer - 3658 rpc

RPS on "Hello world" page:
* Nginx - 20111 rpc
* LarionovServer - 16475 rpc
