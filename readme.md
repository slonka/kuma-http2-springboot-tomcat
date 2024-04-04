## Versions

JDK - 17
Spring Boot - 3.2.3 has embedded tomcat - 10.1.19 - https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-tomcat/3.2.3/dependencies
Tomcat - 10.1.19
kong-mesh 2.4.2

## Deployment

```bash
./gradlew war assemble
docker build -t spring-http2-test:1.0.0 .
k3d cluster create spring
kumactl install control-plane | kubectl apply -f - # kong-mesh 2.4.2
k3d image import --cluster=spring --verbose spring-http2-test
```

## Test

```bash
while true; do
  curl -v http://my-app-service-spring.kuma-demo.svc.8080.mesh:80 && curl -v http://my-app-service-spring.kuma-demo.svc.8080.mesh:80
  if [[ "$?" -ne 0 ]]; then
    break
  fi
  sleep 1
done
```