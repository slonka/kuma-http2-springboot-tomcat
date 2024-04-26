## Versions

- JDK - 17
- Spring Boot - 3.2.3 has embedded tomcat - 10.1.19 - https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-tomcat/3.2.3/dependencies
- Tomcat - 10.1.19
- kong-mesh 2.4.2

## Deployment

```bash
# create a k3d cluster
k3d cluster create spring

# build the application
./gradlew war assemble
docker build -t spring-http2-test:1.0.1 .
k3d image import --cluster=spring --verbose spring-http2-test:1.0.1

# install kuma and kic
kumactl install control-plane | kubectl apply -f - # kong-mesh 2.4.2
kubectl -n kuma-system rollout status deploy/kuma-control-plane

kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.0.0/standard-install.yaml
helm repo add kong https://charts.konghq.com
helm repo update
helm install kong kong/ingress -n kong --create-namespace

kubectl label namespace kong kuma.io/sidecar-injection=enabled
kubectl rollout restart -n kong deployment kong-gateway kong-controller
kubectl -n kong rollout status deploy/kong-gateway

# install the app and gateway/mesh routing

kubectl apply -f k8s/spring-app.yaml
kubectl apply -f k8s/routing.yaml
kubectl -n kuma-demo rollout status deploy/my-app-deployment
```

## Test

```bash
kubectl run --namespace kuma-demo mycurlpod --image=nicolaka/netshoot -i --tty -- sh
# When the request is handled normally, it will only print a dot(.)
# Otherwise, it will print the random id
while true; do
    RANDOM_ID="trial-$RANDOM"
    HTTP_STATUS=$(curl -s -o /dev/null --http2-prior-knowledge --connect-timeout 10 -w "%{http_code}" -H "X-RANDOM-ID: $RANDOM_ID" http://kong-gateway-proxy.kong.svc)
    if [[ "$HTTP_STATUS" -ge 200 ]] && [[ "$HTTP_STATUS" -lt 400 ]]; then
        echo -n "."
    else
        echo ""
        echo "$RANDOM_ID: $HTTP_STATUS"
    fi

    RANDOM_MS=$((100 + RANDOM % 100))
    sleep "0.${RANDOM_MS}"
done
```