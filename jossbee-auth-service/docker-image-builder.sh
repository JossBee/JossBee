docker build -t jossbee-auth-server .
docker tag jossbee-auth-server 124225/jossbee-auth-server:jossbee-auth-server
docker login
docker push 124225/jossbee-auth-server:jossbee-auth-server